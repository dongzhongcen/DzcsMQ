package org.example.dzcsmq.mqserver.core;

import java.io.Serializable;

public class BasicProperties implements Serializable {
    private String messageId; //changeable type for unique
    private String routingKey;
    private int deliveryMode = 1; //1 stands for durable, 0 for else

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }


    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public int getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(int deliveryMode) {
        this.deliveryMode = deliveryMode;
    }
}
