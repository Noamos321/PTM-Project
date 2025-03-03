package http;

import java.io.IOException;
import java.io.OutputStream;
import http.RequestParser.RequestInfo;


public interface Servlet {
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;
    void close() throws IOException;
}
