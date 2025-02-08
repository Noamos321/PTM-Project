package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


public class RequestParser {

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        String line;
        String httpCommand = null;
        String uri = null;
        String uriNew = null;
        String[] uriSegments = null;
        Map<String, String> parameters = new HashMap<>();
        byte[] content = null;
        int contentLength = 0;

        // Read and parse the request line (e.g., "GET /path?query=value HTTP/1.1")
        line = reader.readLine();
        if (line != null) {
            String[] requestLineParts = line.split(" ");
            if (requestLineParts.length >= 2) {
                httpCommand = requestLineParts[0]; // HTTP method (e.g., GET, POST)
                uri = requestLineParts[1]; // Request URI

                // Parse URI and query parameters if present
                int queryIndex = uri.indexOf("?");
                if (queryIndex != -1) {
                    String queryString = uri.substring(queryIndex + 1); // Extract query string
                    uriNew = uri.substring(0, queryIndex); // Extract URI without query string
                    String[] paramPairs = queryString.split("&"); // Split query string into key-value pairs
                    for (String pair : paramPairs) {
                        String[] keyValue = pair.split("=");
                        if (keyValue.length == 2) {
                            parameters.put(keyValue[0], keyValue[1]); // Add parameters to map
                        }
                    }
                } else {
                    uriNew = uri; // No query parameters, use full URI
                }

                // Split URI into segments and filter out empty segments
                String[] rawSegments = uriNew.split("/");
                List<String> filteredSegments = new ArrayList<>();
                for (String segment : rawSegments) {
                    if (!segment.isEmpty()) {
                        filteredSegments.add(segment); // Add non-empty segments to the list
                    }
                }
                uriSegments = filteredSegments.toArray(new String[0]); // Convert list to array
            }
        }

        // Parse headers and content
        boolean inContent = false; // Flag to determine when to start reading content
        StringBuilder contentBuilder = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                inContent = true; // Empty line indicates start of content section
                continue;
            }

            if (inContent) {
                contentBuilder.append(line).append("\n"); // Collect content lines
            } else {
                // Parse headers
                String[] headerParts = line.split(": ");
                if (headerParts.length == 2) {
                    if (headerParts[0].equalsIgnoreCase("Content-Length")) {
                        contentLength = Integer.parseInt(headerParts[1]); // Get content length
                    } else if (headerParts[0].equalsIgnoreCase("filename")) {
                        parameters.put("filename", headerParts[1]); // Get filename from headers
                    }
                }
            }
        }

        BufferedReader copyReader = new BufferedReader(reader);

        // Process content and filename if available
        if (contentLength > 0 && contentBuilder.length() > 0) {
            String contentString = contentBuilder.toString();
            int filenameIndex = contentString.indexOf("filename=\"");
            if (filenameIndex != -1) {
                int start = filenameIndex + "filename=\"".length();
                int end = contentString.indexOf("\"", start);
                if (end != -1) {
                    String filename = contentString.substring(start, end);
                    filename = "\"" + filename + "\"";
                    parameters.put("filename", filename);

                    // Find content-type line to handle content properly
                    int contentTypeIndex = contentString.indexOf("Content-Type:");
                    if (contentTypeIndex != -1) {
                        // Move start to the end of the content-type line
                        start = contentString.indexOf("\n", contentTypeIndex);
                        // Skip an additional newline character after the header
                        start = contentString.indexOf("\n", start);
                        // Extract the content up to the boundary
                        String tmp = contentString.substring(start);
                        end = tmp.indexOf("----");
                        if (end != -1) {
                            tmp = tmp.substring(0, end).trim(); // Get content before boundary
                            parameters.put("fileContent", tmp);
                            content = tmp.getBytes(); // Convert content to bytes
                        } else {
                            content = contentString.getBytes(); // No boundary, get entire content
                        }
                    }
                    else{ //if there is no 'Content-Type' on request
                        // Extract the actual content
                        int contentStartIndex = contentString.indexOf("\n", end) + 1;
                        contentStartIndex = contentString.indexOf("\n") + 1;
                        content = contentString.substring(contentStartIndex).getBytes();
                    }
                }
            } else {
                content = contentString.getBytes(); // No filename, use entire content
            }
        } else {
            content = contentBuilder.toString().getBytes(); // Default case: convert content to bytes
        }

        // Return the parsed request information
        return new RequestInfo(httpCommand, uri, uriSegments, parameters, content);
    }



    public static class RequestInfo {
        private final String httpCommand; // HTTP command (e.g., GET, POST)
        private final String uri; // Request URI
        private final String[] uriSegments; // Segments of the URI
        private final Map<String, String> parameters; // Query parameters and headers
        private final byte[] content; // Content of the request


        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }


        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }


        public byte[] getContent() {
            return content;
        }


        public void print() {
            System.out.println("HTTP Command: " + httpCommand);
            System.out.println("URI: " + uri);
            System.out.println("URI Segments: ");
            for (String segment : uriSegments) {
                System.out.println("  - " + segment);
            }
            System.out.println("Parameters: ");
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                System.out.println("  " + entry.getKey() + " = " + entry.getValue());
            }
            if (content != null) {
                System.out.println("Content: " + new String(content));
            }
        }
    }
}