package agents;
import topics.Message;
import topics.TopicManagerSingleton;

import java.util.function.BinaryOperator;


public class BinOpAgent implements Agent {
    private final String name;
    private final String input1;
    private final String input2;
    private final String output;
    private final BinaryOperator<Double> operation;

    public BinOpAgent(String name, String input1, String input2, String output, BinaryOperator<Double> operation) {
        this.name = name;
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.operation = operation;

        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
        topicManager.getTopic(input1).subscribe(this);
        topicManager.getTopic(input2).subscribe(this);
        topicManager.getTopic(output).addPublisher(this);

        Message initialMessage = new Message("0");
        topicManager.getTopic(input1).publish(initialMessage);
        topicManager.getTopic(input2).publish(initialMessage);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {

    }

    @Override
    public void callback(String topic, Message msg) {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
        Message msg1 = topicManager.getTopic(input1).getMessage();
        Message msg2 = topicManager.getTopic(input2).getMessage();

        if (msg1 != null && msg2 != null && !Double.isNaN(msg1.asDouble) && !Double.isNaN(msg2.asDouble)) {
            Double result = operation.apply(msg1.asDouble, msg2.asDouble);
            topicManager.getTopic(output).publish(new Message(result));
        }
    }


    @Override
    public void close() {

        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
        topicManager.getTopic(input1).unsubscribe(this);
        topicManager.getTopic(input2).unsubscribe(this);
        topicManager.getTopic(output).removePublisher(this);
    }
}