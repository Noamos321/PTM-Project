package agents;
import topics.Message;


public interface Agent {
    String getName();
    void reset();
    void callback(String topic, Message msg);
    void close();
}
