package org.example.dzcsmq.mqserver.core;

public class Consumer {
    private String consumerTag;
    private String queueName;
    private boolean autoAck;
    private ConsumerCallback consumerCallback;

    public Consumer() {
    }

    public Consumer(String consumerTag, String queueName, boolean autoAck, ConsumerCallback consumerCallback) {
        this.consumerTag = consumerTag;
        this.queueName = queueName;
        this.autoAck = autoAck;
        this.consumerCallback = consumerCallback;
    }

    public String getConsumerTag() {
        return consumerTag;
    }

    public void setConsumerTag(String consumerTag) {
        this.consumerTag = consumerTag;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public boolean isAutoAck() {
        return autoAck;
    }

    public void setAutoAck(boolean autoAck) {
        this.autoAck = autoAck;
    }

    public ConsumerCallback getConsumerCallback() {
        return consumerCallback;
    }

    public void setConsumerCallback(ConsumerCallback consumerCallback) {
        this.consumerCallback = consumerCallback;
    }
}
