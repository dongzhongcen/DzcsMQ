package org.example.dzcsmq.mqserver.datacenter;

import org.example.dzcsmq.mqserver.core.Binding;
import org.example.dzcsmq.mqserver.core.Exchange;
import org.example.dzcsmq.mqserver.core.MSGQueue;
import org.example.dzcsmq.mqserver.core.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/*
    管理内存中的元数据和消息数据
 */
public class MemoryDataCenter {
    private final Map<String, Exchange> exchangeMap = new ConcurrentHashMap<>();
    private final Map<String, MSGQueue> queueMap = new ConcurrentHashMap<>();
    private final Map<String, Binding> bindingMap = new ConcurrentHashMap<>();
    private final Map<String, Message> messageMap = new ConcurrentHashMap<>();
    private final Map<String, Queue<Message>> queueMessageMap = new ConcurrentHashMap<>();
    private final Map<String, Message> unAckMessageMap = new ConcurrentHashMap<>();

    public void insertExchange(Exchange exchange) {
        exchangeMap.put(exchange.getName(), exchange);
    }

    public void deleteExchange(String exchangeName) {
        exchangeMap.remove(exchangeName);
    }

    public Exchange getExchange(String exchangeName) {
        return exchangeMap.get(exchangeName);
    }

    public boolean containsExchange(String exchangeName) {
        return exchangeMap.containsKey(exchangeName);
    }

    public void insertQueue(MSGQueue queue) {
        queueMap.put(queue.getName(), queue);
        queueMessageMap.putIfAbsent(queue.getName(), new LinkedList<>());
    }

    public void deleteQueue(String queueName) {
        queueMap.remove(queueName);
        queueMessageMap.remove(queueName);
    }

    public MSGQueue getQueue(String queueName) {
        return queueMap.get(queueName);
    }

    public boolean containsQueue(String queueName) {
        return queueMap.containsKey(queueName);
    }

    public void insertBinding(Binding binding) {
        bindingMap.put(bindingKey(binding.getExchangeName(), binding.getQueueName(), binding.getBindingKey()), binding);
    }

    public void deleteBinding(Binding binding) {
        bindingMap.remove(bindingKey(binding.getExchangeName(), binding.getQueueName(), binding.getBindingKey()));
    }

    public List<Binding> getBindings(String exchangeName) {
        List<Binding> result = new LinkedList<>();
        for (Binding binding : bindingMap.values()) {
            if (binding.getExchangeName().equals(exchangeName)) {
                result.add(binding);
            }
        }
        return result;
    }

    public void sendMessage(String queueName, Message message) {
        messageMap.put(message.getMessageId(), message);
        queueMessageMap.putIfAbsent(queueName, new LinkedList<>());
        queueMessageMap.get(queueName).offer(message);
    }

    public Message pollMessage(String queueName) {
        Queue<Message> messages = queueMessageMap.get(queueName);
        if (messages == null) {
            return null;
        }
        return messages.poll();
    }

    public void deleteMessage(String messageId) {
        messageMap.remove(messageId);
        unAckMessageMap.remove(messageId);
    }

    public Message getMessage(String messageId) {
        return messageMap.get(messageId);
    }

    public void addUnAckMessage(Message message) {
        unAckMessageMap.put(message.getMessageId(), message);
    }

    public Message removeUnAckMessage(String messageId) {
        return unAckMessageMap.remove(messageId);
    }

    public Map<String, Exchange> getExchangeMap() {
        return exchangeMap;
    }

    public Map<String, MSGQueue> getQueueMap() {
        return queueMap;
    }

    public Map<String, Binding> getBindingMap() {
        return bindingMap;
    }

    public Map<String, Message> getMessageMap() {
        return messageMap;
    }

    public Map<String, Queue<Message>> getQueueMessageMap() {
        return queueMessageMap;
    }

    private String bindingKey(String exchangeName, String queueName, String bindingKey) {
        return exchangeName + "\0" + queueName + "\0" + bindingKey;
    }
}
