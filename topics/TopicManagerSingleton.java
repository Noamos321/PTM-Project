package topics;

import java.util.Collection;
import java.util.HashMap;
import topics.Topic;

public class TopicManagerSingleton {

    public static class TopicManager {
        private final HashMap<String, Topic> topics = new HashMap<>();

        public synchronized Topic getTopic(String name) {
            if (!topics.containsKey(name)) {
                topics.put(name, new Topic(name));
            }
            return topics.get(name);
        }

        public Collection<Topic> getTopics() {
            return topics.values();
        }

        public synchronized void clear() {
            topics.clear();
        }
    }

    private static final TopicManager instance = new TopicManager();

    private TopicManagerSingleton() {}

    public static TopicManager get() {
        return instance;
    }
}
