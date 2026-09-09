package org.example.dzcsmq.mqserver.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MetaMapper {
    void createExchangeableTable();
    void createQueueTable();
    void createBindingTable();
}
