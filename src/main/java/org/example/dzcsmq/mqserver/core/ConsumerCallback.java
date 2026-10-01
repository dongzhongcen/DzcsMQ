package org.example.dzcsmq.mqserver.core;

@FunctionalInterface
public interface ConsumerCallback {
    void handleDelivery(String consumerTag, BasicProperties basicProperties, byte[] body) throws Exception;
}
