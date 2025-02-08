package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import topics.TopicManagerSingleton;
import topics.Topic;
import agents.Agent;


public class Graph extends ArrayList<Node> {

    // בדיקה אם הגרף מכיל מחזור
    public boolean hasCycles() {
        for (Node node : this) {
            if (node.hasCycles()) {
                return true; // מחזור זוהה
            }
        }
        return false; // לא זוהה מחזור
    }

    public void createFromTopics() {
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        this.clear();

        Map<String, Node> nodeMap = new HashMap<>();

        for (Topic topic : tm.getTopics()) {
            // יצירת צומת לנושא
            Node topicNode = nodeMap.computeIfAbsent("T" + topic.getName(), Node::new);
            if (!this.contains(topicNode)) this.add(topicNode);

            // הוספת קשרים לסוכנים כמנויים
            for (Agent agent : topic.getSubscribers()) {
                Node agentNode = nodeMap.computeIfAbsent("A" + agent.getName(), Node::new);
                if (!this.contains(agentNode)) this.add(agentNode);
                topicNode.addEdge(agentNode); // קשר מנושא לסוכן
            }

            // הוספת קשרים בין מפרסמים לנושאים
            for (Agent publisher : topic.getPublishers()) { // שינוי ל-getPublishers()
                Node publisherNode = nodeMap.computeIfAbsent("A" + publisher.getName(), Node::new);
                if (!this.contains(publisherNode)) this.add(publisherNode);
                publisherNode.addEdge(topicNode); // קשר ממפרסם לנושא
            }
        }
    }

}
