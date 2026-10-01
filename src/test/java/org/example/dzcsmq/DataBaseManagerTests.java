package org.example.dzcsmq;

import org.example.dzcsmq.mqserver.core.Binding;
import org.example.dzcsmq.mqserver.core.Exchange;
import org.example.dzcsmq.mqserver.core.MSGQueue;
import org.example.dzcsmq.mqserver.datacenter.DataBaseManager;
import org.example.dzcsmq.mqserver.mapper.MetaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DataBaseManagerTests {
    @Autowired
    private MetaMapper metaMapper;

    private DataBaseManager dataBaseManager;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        dataBaseManager = new DataBaseManager();

        Field metaMapperField = DataBaseManager.class.getDeclaredField("metaMapper");
        metaMapperField.setAccessible(true);
        metaMapperField.set(dataBaseManager, metaMapper);
    }

    @Test
    void testInsertAndDeleteExchange() {
        Exchange exchange = new Exchange();
        exchange.setName("test.exchange");
        exchange.setDurable(true);

        dataBaseManager.deleteExchange(exchange.getName());
        dataBaseManager.insertExchange(exchange);

        List<Exchange> exchanges = dataBaseManager.selectAllExchanges();
        assertTrue(exchanges.stream().anyMatch(item -> item.getName().equals(exchange.getName())));

        dataBaseManager.deleteExchange(exchange.getName());
        exchanges = dataBaseManager.selectAllExchanges();
        assertFalse(exchanges.stream().anyMatch(item -> item.getName().equals(exchange.getName())));
    }

    @Test
    void testInsertAndDeleteQueue() {
        MSGQueue queue = new MSGQueue();
        queue.setName("test.queue");
        queue.setDurable(true);

        dataBaseManager.deleteQueue(queue.getName());
        dataBaseManager.insertQueue(queue);

        List<MSGQueue> queues = dataBaseManager.selectAllQueues();
        assertTrue(queues.stream().anyMatch(item -> item.getName().equals(queue.getName())));

        dataBaseManager.deleteQueue(queue.getName());
        queues = dataBaseManager.selectAllQueues();
        assertFalse(queues.stream().anyMatch(item -> item.getName().equals(queue.getName())));
    }

    @Test
    void testInsertAndDeleteBinding() {
        Exchange exchange = new Exchange();
        exchange.setName("test.binding.exchange");

        MSGQueue queue = new MSGQueue();
        queue.setName("test.binding.queue");

        Binding binding = new Binding();
        binding.setExchangeName(exchange.getName());
        binding.setQueueName(queue.getName());
        binding.setBindingKey("test.binding.key");

        dataBaseManager.deleteBinding(binding);
        dataBaseManager.deleteExchange(exchange.getName());
        dataBaseManager.deleteQueue(queue.getName());

        dataBaseManager.insertExchange(exchange);
        dataBaseManager.insertQueue(queue);
        dataBaseManager.insertBinding(binding);

        List<Binding> bindings = dataBaseManager.selectAllBindings();
        assertTrue(bindings.stream().anyMatch(item ->
                item.getExchangeName().equals(binding.getExchangeName())
                        && item.getQueueName().equals(binding.getQueueName())
                        && item.getBindingKey().equals(binding.getBindingKey())));

        dataBaseManager.deleteBinding(binding);
        bindings = dataBaseManager.selectAllBindings();
        assertFalse(bindings.stream().anyMatch(item ->
                item.getExchangeName().equals(binding.getExchangeName())
                        && item.getQueueName().equals(binding.getQueueName())
                        && item.getBindingKey().equals(binding.getBindingKey())));

        dataBaseManager.deleteExchange(exchange.getName());
        dataBaseManager.deleteQueue(queue.getName());
    }
}
