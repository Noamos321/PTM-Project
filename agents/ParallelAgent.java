package agents;
import topics.Message;


public class ParallelAgent implements Agent {
    private final Agent wrappedAgent;

    public ParallelAgent(Agent agent) {
        this.wrappedAgent = agent;
    }

    @Override
    public String getName() {
        return wrappedAgent.getName();
    }

    @Override
    public void reset() {
        wrappedAgent.reset();
    }

    @Override
    public void callback(String topic, Message msg) {
        wrappedAgent.callback(topic, msg);
    }

    @Override
    public void close() {
        wrappedAgent.close();
    }
}
