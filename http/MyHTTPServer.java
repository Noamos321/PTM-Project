package http;

import http.Servlet;
import http.RequestParser;
import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;
import java.net.ServerSocket;


public class MyHTTPServer extends Thread implements HTTPServer {

    private ConcurrentHashMap<String, Servlet> getServlets = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Servlet> postServlets = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Servlet> deleteServlets = new ConcurrentHashMap<>();

    private ExecutorService requestHandlerPool;

    private ServerSocket serverSocket;

    private volatile boolean isServerStopped = false;

    private final int port;

    private final int threadCount;


    public MyHTTPServer(int port, int threadCount) {
        // Initialize the thread pool with a fixed number of threads
        requestHandlerPool = Executors.newFixedThreadPool(threadCount);
        this.port = port;
        this.threadCount = threadCount;
    }


    public void addServlet(String httpCommand, String uri, Servlet servlet) {
        if (uri == null || servlet == null) {
            return;
        }

        httpCommand = httpCommand.toUpperCase();

        switch (httpCommand) {
            case "GET":
                getServlets.put(uri, servlet);
                break;
            case "POST":
                postServlets.put(uri, servlet);
                break;
            case "DELETE":
                deleteServlets.put(uri, servlet);
                break;
        }
    }


    public void removeServlet(String httpCommand, String uri) {
        if (uri == null) {
            return;
        }

        httpCommand = httpCommand.toUpperCase();

        switch (httpCommand) {
            case "GET":
                getServlets.remove(uri);
                break;
            case "POST":
                postServlets.remove(uri);
                break;
            case "DELETE":
                deleteServlets.remove(uri);
                break;
        }
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            serverSocket.setSoTimeout(1000); // Set timeout for socket accept operations

            while (!isServerStopped) {
                try {
                    // Accept a new client connection
                    Socket clientSocket = serverSocket.accept();

                    // Handle the client request in a separate thread
                    requestHandlerPool.submit(() -> {
                        try {
                            Thread.sleep(125); // Delay to ensure proper request reception
                            BufferedReader requestReader = createBufferedReader(clientSocket);

                            // Parse the incoming request
                            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(requestReader);
                            ConcurrentHashMap<String, Servlet> servletMap;

                            if (requestInfo != null) {
                                switch (requestInfo.getHttpCommand()) {
                                    case "GET":
                                        servletMap = getServlets;
                                        break;
                                    case "POST":
                                        servletMap = postServlets;
                                        break;
                                    case "DELETE":
                                        servletMap = deleteServlets;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Unsupported HTTP command: " + requestInfo.getHttpCommand());
                                }

                                // Find the best matching servlet based on the longest URI match
                                String bestMatchUri = "";
                                Servlet matchingServlet = null;
                                for (Map.Entry<String, Servlet> entry : servletMap.entrySet()) {
                                    if (requestInfo.getUri().startsWith(entry.getKey()) && entry.getKey().length() > bestMatchUri.length()) {
                                        bestMatchUri = entry.getKey();
                                        matchingServlet = entry.getValue();
                                    }
                                }

                                // Handle the request using the matching servlet
                                if (matchingServlet != null) {
                                    matchingServlet.handle(requestInfo, clientSocket.getOutputStream());
                                }
                            }
                            requestReader.close();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            // Close the client connection
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    // Handle socket accept timeout exception
                    if (isServerStopped) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedReader createBufferedReader(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        int availableBytes = inputStream.available();
        byte[] buffer = new byte[availableBytes];
        int bytesRead = inputStream.read(buffer, 0, availableBytes);

        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(buffer, 0, bytesRead)
                )
        );
    }


    public void start() {
        isServerStopped = false;
        super.start();
    }


    public void close() {
        isServerStopped = true;
        requestHandlerPool.shutdownNow();
    }


    public Object getThreadPool() {
        return requestHandlerPool;
    }
}