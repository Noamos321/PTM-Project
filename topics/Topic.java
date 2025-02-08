package topics;

import agents.Agent;
import topics.Message;
import java.util.ArrayList;
import java.util.List;

public class Topic {
    public final String name;
    private final List<Agent> subs;
    private final List<Agent> pubs;
    private Message lastMessage;

    Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
        this.lastMessage = null;
    }

    public String getName() {
        return name;
    }

    public void subscribe(Agent a) {
        if (!subs.contains(a)) {
            subs.add(a);
        }
    }

    public void unsubscribe(Agent a) {
        subs.remove(a);
    }

    public void publish(Message m) {
        lastMessage = m;
        for (Agent a : subs) {
            a.callback(name, m);
        }
    }

    public void addPublisher(Agent a) {
        if (!pubs.contains(a)) {
            pubs.add(a);
        }
    }

    public void removePublisher(Agent a) {
        pubs.remove(a);
    }

    public Message getMessage() {
        return lastMessage;
    }

    public List<Agent> getSubscribers() {
        return subs;
    }

    public Agent[] getPublishers() {
        return pubs.toArray(new Agent[0]);
    }
}
