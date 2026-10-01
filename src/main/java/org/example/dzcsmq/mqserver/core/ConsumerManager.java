package org.example.dzcsmq.mqserver.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerManager {
    private final Map<String, Consumer> consumerMap = new ConcurrentHashMap<>();

    public String addConsumer(String queueName, boolean autoAck, ConsumerCallback callback) {
        String consumerTag = "C-" + UUID.randomUUID();
        Consumer consumer = new Consumer(consumerTag, queueName, autoAck, callback);
        consumerMap.put(consumerTag, consumer);
        return consumerTag;
    }

    public void removeConsumer(String consumerTag) {
        consumerMap.remove(consumerTag);
    }

    public Consumer getConsumer(String consumerTag) {
        return consumerMap.get(consumerTag);
    }

    public Map<String, Consumer> getConsumerMap() {
        return consumerMap;
    }
}
