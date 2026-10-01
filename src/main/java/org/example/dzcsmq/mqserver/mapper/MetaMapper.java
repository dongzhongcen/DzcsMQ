package org.example.dzcsmq.mqserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.dzcsmq.mqserver.core.Binding;
import org.example.dzcsmq.mqserver.core.Exchange;
import org.example.dzcsmq.mqserver.core.MSGQueue;

import java.util.List;

@Mapper
public interface MetaMapper {
    void createExchangeableTable();
    void createQueueTable();
    void createBindingTable();

    void insertExchange(Exchange exchange);
    void deleteExchange(String exchangeName);

    void insertQueue(MSGQueue queue);
    void deleteQueue(@Param("queueName") String queueName);

    void insertBinding(Binding binding);
    void deleteBinding(Binding binding);

    List<Exchange> selectAllExchanges();
    List<MSGQueue> selectAllQueues();
    List<Binding> selectAllBindings();
}
