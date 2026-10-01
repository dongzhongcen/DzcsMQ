package org.example.dzcsmq;

import org.example.dzcsmq.mqserver.core.Binding;
import org.example.dzcsmq.mqserver.core.Exchange;
import org.example.dzcsmq.mqserver.core.MSGQueue;
import org.example.dzcsmq.mqserver.mapper.MetaMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DzcsmqApplicationTests {
    @Autowired
    private MetaMapper metaMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void metaMapperCanInsertAndDeleteMetadata() {
        Exchange exchange = new Exchange();
        exchange.setName("test.exchange");
        String exchangeName = exchange.getName();
        exchange.setDurable(true);

        MSGQueue queue = new MSGQueue();
        queue.setName("test.queue");
        queue.setDurable(true);

        Binding binding = new Binding();
        binding.setExchangeName(exchange.getName());
        binding.setQueueName(queue.getName());
        binding.setBindingKey("test.key");

        metaMapper.deleteBinding(binding);
        metaMapper.deleteExchange(exchangeName);
        metaMapper.deleteQueue(queue.getName());

        metaMapper.insertExchange(exchange);
        metaMapper.insertQueue(queue);
        metaMapper.insertBinding(binding);

        metaMapper.deleteBinding(binding);
        metaMapper.deleteExchange(exchangeName);
        metaMapper.deleteQueue(queue.getName());
    }

}
