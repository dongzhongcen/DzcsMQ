package org.example.dzcsmq.mqserver.datacenter;

import org.example.dzcsmq.mqserver.core.Message;

import java.io.IOException;
import java.util.List;

/*
    统一硬盘操作
 */
public class DiskDataCenter {
    private final DataBaseManager dataBaseManager = new DataBaseManager();
    private final MessageFileManager messageFileManager = new MessageFileManager();

    public void init() {
        dataBaseManager.init();
    }

    public DataBaseManager getDataBaseManager() {
        return dataBaseManager;
    }

    public MessageFileManager getMessageFileManager() {
        return messageFileManager;
    }

    public void createQueueFiles(String queueName) throws IOException {
        messageFileManager.createQueueFiles(queueName);
    }

    public void destroyQueueFiles(String queueName) {
        messageFileManager.destroyQueueFiles(queueName);
    }

    public void sendMessage(String queueName, Message message) throws IOException {
        messageFileManager.sendMessage(queueName, message);
    }

    public void deleteMessage(String queueName, Message message) throws IOException {
        messageFileManager.deleteMessage(queueName, message);
    }

    public List<Message> loadAllMessages(String queueName) throws IOException {
        return messageFileManager.loadAllMessages(queueName);
    }

    public void gc(String queueName) throws IOException {
        messageFileManager.gc(queueName);
    }
}
