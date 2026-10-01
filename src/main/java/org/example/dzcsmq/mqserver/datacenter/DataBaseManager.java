package org.example.dzcsmq.mqserver.datacenter;

import org.example.dzcsmq.DzcsmqApplication;
import org.example.dzcsmq.mqserver.core.Binding;
import org.example.dzcsmq.mqserver.core.Exchange;
import org.example.dzcsmq.mqserver.core.ExchangeType;
import org.example.dzcsmq.mqserver.core.MSGQueue;
import org.example.dzcsmq.mqserver.mapper.MetaMapper;

import java.io.File;
import java.util.List;

/*
    整合数据库操作
 */
public class DataBaseManager {
    private MetaMapper metaMapper;

    //初始化数据库: 建库建表 + 插入默认数据
    /*
        构造方法一般适用于初始化类的属性但是不会涉及到复杂的业务逻辑
        此处带有业务逻辑的手动调用会更合适一些
     */
    public void init(){
        metaMapper = DzcsmqApplication.context.getBean(MetaMapper.class);


    //1. 如果数据库已经存在, 表和数据都有了, 不做任何操作
        //2. 如果数据库不存在, 则建库建表, 构造默认数
        /*
            broker server部署到新的服务器上面需要执行
            如果已经部署过了, broker sever重启则不需要执行
            判定方法就是判断.db这个文件是否存在
         */
        if(!checkDBExists()){
            createTable();
            createDefualtData();
            System.out.println("[DataBaseManager] 数据库初始化已完成!");

        } else {
            System.out.println("[DataBaseManager] 数据库已存在!");
        }

    }


    private boolean checkDBExists() {
        File file = new File("./data/meta.db");
        if (file.exists()) {
            return true;
        }
        return false;
    }

    //建库不需要手动经行, 首次自动执行数据库操作就最懂创建出 meta.db 文件
    private void createTable() {
        metaMapper.createBindingTable();
        metaMapper.createQueueTable();
        metaMapper.createExchangeableTable();

        System.out.println("[DataBaseManager] 创建表完成!");
    }

    //此处主要添加一个默认的交换机, rmq中有一个设定: 带有一个交换机, 默认是direct
    private void createDefualtData() {
        Exchange exchange = new Exchange();
        exchange.setName("");
        exchange.setType(ExchangeType.DIRECT);
        exchange.setDurable(true);
        exchange.setAutoDelete(false);
        metaMapper.insertExchange(exchange);

        System.out.println("[DataBaseManager] 创建初始数据完成!");
    }

    public void insertExchange(Exchange exchange){
        metaMapper.insertExchange(exchange);
    }

    public void deleteExchange(String exchangeName){
        metaMapper.deleteExchange(exchangeName);
    }

    public void insertQueue(MSGQueue queue){
        metaMapper.insertQueue(queue);
    }

    public void deleteQueue(String queueName){
        metaMapper.deleteQueue(queueName);
    }

    public void insertBinding(Binding binding){
        metaMapper.insertBinding(binding);
    }

    public void deleteBinding(Binding binding){
        metaMapper.deleteBinding(binding);
    }

    public List<Exchange> selectAllExchanges(){
        return metaMapper.selectAllExchanges();
    }

    public List<MSGQueue> selectAllQueues(){
        return metaMapper.selectAllQueues();
    }

    public List<Binding> selectAllBindings(){
        return metaMapper.selectAllBindings();
    }
}
