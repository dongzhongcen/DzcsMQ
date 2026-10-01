package org.example.dzcsmq.mqserver.core;

import org.example.dzcsmq.mqserver.datacenter.DiskDataCenter;
import org.example.dzcsmq.mqserver.datacenter.MemoryDataCenter;

import java.io.IOException;
import java.util.List;

public class VirtualHost {
    private final String virtualHostName;
    private final MemoryDataCenter memoryDataCenter = new MemoryDataCenter();
    private final DiskDataCenter diskDataCenter = new DiskDataCenter();
    private final ConsumerManager consumerManager = new ConsumerManager();

    public VirtualHost(String virtualHostName) {
        this.virtualHostName = virtualHostName;
    }

    public String getVirtualHostName() {
        return virtualHostName;
    }

    public boolean exchangeDeclare(String exchangeName, ExchangeType exchangeType, boolean durable, boolean autoDelete) {
        if (memoryDataCenter.containsExchange(exchangeName)) {
            return true;
        }
        Exchange exchange = new Exchange();
        exchange.setName(exchangeName);
        exchange.setType(exchangeType);
        exchange.setDurable(durable);
        exchange.setAutoDelete(autoDelete);
        memoryDataCenter.insertExchange(exchange);
        if (durable) {
            diskDataCenter.getDataBaseManager().insertExchange(exchange);
        }
        return true;
    }

    public boolean exchangeDelete(String exchangeName) {
        Exchange exchange = memoryDataCenter.getExchange(exchangeName);
        memoryDataCenter.deleteExchange(exchangeName);
        if (exchange != null && exchange.isDurable()) {
            diskDataCenter.getDataBaseManager().deleteExchange(exchangeName);
        }
        return true;
    }

    public boolean queueDeclare(String queueName, boolean durable, boolean exclusive, boolean autoDelete) throws IOException {
        if (memoryDataCenter.containsQueue(queueName)) {
            return true;
        }
        MSGQueue queue = new MSGQueue();
        queue.setName(queueName);
        queue.setDurable(durable);
        queue.setExclusive(exclusive);
        queue.setAutoDelete(autoDelete);
        memoryDataCenter.insertQueue(queue);
        if (durable) {
            diskDataCenter.getDataBaseManager().insertQueue(queue);
            diskDataCenter.createQueueFiles(queueName);
        }
        return true;
    }

    public boolean queueDelete(String queueName) {
        MSGQueue queue = memoryDataCenter.getQueue(queueName);
        memoryDataCenter.deleteQueue(queueName);
        if (queue != null && queue.isDurable()) {
            diskDataCenter.getDataBaseManager().deleteQueue(queueName);
            diskDataCenter.destroyQueueFiles(queueName);
        }
        return true;
    }

    public boolean queueBind(String queueName, String exchangeName, String bindingKey) {
        Binding binding = new Binding();
        binding.setQueueName(queueName);
        binding.setExchangeName(exchangeName);
        binding.setBindingKey(bindingKey);
        memoryDataCenter.insertBinding(binding);

        Exchange exchange = memoryDataCenter.getExchange(exchangeName);
        MSGQueue queue = memoryDataCenter.getQueue(queueName);
        if (exchange != null && queue != null && exchange.isDurable() && queue.isDurable()) {
            diskDataCenter.getDataBaseManager().insertBinding(binding);
        }
        return true;
    }

    public boolean queueUnbind(String queueName, String exchangeName, String bindingKey) {
        Binding binding = new Binding();
        binding.setQueueName(queueName);
        binding.setExchangeName(exchangeName);
        binding.setBindingKey(bindingKey);
        memoryDataCenter.deleteBinding(binding);

        Exchange exchange = memoryDataCenter.getExchange(exchangeName);
        MSGQueue queue = memoryDataCenter.getQueue(queueName);
        if (exchange != null && queue != null && exchange.isDurable() && queue.isDurable()) {
            diskDataCenter.getDataBaseManager().deleteBinding(binding);
        }
        return true;
    }

    public boolean basicPublish(String exchangeName, String routingKey, BasicProperties basicProperties, byte[] body) throws IOException {
        Exchange exchange = memoryDataCenter.getExchange(exchangeName);
        if (exchange == null) {
            return false;
        }
        Message message = Message.createMessageWithId(routingKey, basicProperties, body);
        List<Binding> bindings = memoryDataCenter.getBindings(exchangeName);
        for (Binding binding : bindings) {
            if (route(exchange.getType(), routingKey, binding.getBindingKey())) {
                memoryDataCenter.sendMessage(binding.getQueueName(), message);
                MSGQueue queue = memoryDataCenter.getQueue(binding.getQueueName());
                if (queue != null && queue.isDurable() && message.getDeliveryMode() == 1) {
                    diskDataCenter.sendMessage(binding.getQueueName(), message);
                }
            }
        }
        return true;
    }

    public String basicConsume(String queueName, boolean autoAck, ConsumerCallback callback) {
        return consumerManager.addConsumer(queueName, autoAck, callback);
    }

    public boolean basicAck(String messageId) {
        Message message = memoryDataCenter.removeUnAckMessage(messageId);
        if (message == null) {
            return false;
        }
        memoryDataCenter.deleteMessage(messageId);
        return true;
    }

    public Message consumeMessage(String queueName) {
        Message message = memoryDataCenter.pollMessage(queueName);
        if (message != null) {
            memoryDataCenter.addUnAckMessage(message);
        }
        return message;
    }

    public boolean route(ExchangeType exchangeType, String routingKey, String bindingKey) {
        if (exchangeType == ExchangeType.FANOUT) {
            return true;
        }
        if (exchangeType == ExchangeType.DIRECT) {
            return routingKey.equals(bindingKey);
        }
        return routeTopic(routingKey, bindingKey);
    }

    public boolean routeTopic(String routingKey, String bindingKey) {
        if (!checkRoutingKey(routingKey) || !checkBindingKey(bindingKey)) {
            return false;
        }
        String[] routingTokens = routingKey.split("\\.");
        String[] bindingTokens = bindingKey.split("\\.");
        return routeTopic(routingTokens, 0, bindingTokens, 0);
    }

    private boolean routeTopic(String[] routingTokens, int routingIndex, String[] bindingTokens, int bindingIndex) {
        if (routingIndex == routingTokens.length && bindingIndex == bindingTokens.length) {
            return true;
        }
        if (bindingIndex == bindingTokens.length) {
            return false;
        }
        if ("#".equals(bindingTokens[bindingIndex])) {
            return routeTopic(routingTokens, routingIndex, bindingTokens, bindingIndex + 1)
                    || (routingIndex < routingTokens.length && routeTopic(routingTokens, routingIndex + 1, bindingTokens, bindingIndex));
        }
        if (routingIndex == routingTokens.length) {
            return false;
        }
        if ("*".equals(bindingTokens[bindingIndex]) || bindingTokens[bindingIndex].equals(routingTokens[routingIndex])) {
            return routeTopic(routingTokens, routingIndex + 1, bindingTokens, bindingIndex + 1);
        }
        return false;
    }

    public boolean checkRoutingKey(String routingKey) {
        return routingKey != null && !routingKey.isEmpty() && !routingKey.contains("*") && !routingKey.contains("#");
    }

    public boolean checkBindingKey(String bindingKey) {
        if (bindingKey == null || bindingKey.isEmpty()) {
            return false;
        }
        String[] words = bindingKey.split("\\.");
        for (String word : words) {
            if (word.isEmpty()) {
                return false;
            }
            if (word.contains("#") && !word.equals("#")) {
                return false;
            }
            if (word.contains("*") && !word.equals("*")) {
                return false;
            }
        }
        return true;
    }

    public MemoryDataCenter getMemoryDataCenter() {
        return memoryDataCenter;
    }

    public DiskDataCenter getDiskDataCenter() {
        return diskDataCenter;
    }

    public ConsumerManager getConsumerManager() {
        return consumerManager;
    }
}
