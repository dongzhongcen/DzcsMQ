package org.example.dzcsmq.mqserver.datacenter;

import org.example.dzcsmq.mqserver.core.Message;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

/*
    管理消息持久化文件
 */
public class MessageFileManager {
    private static final String BASE_DIR = "./data/message/";
    private static final String MESSAGE_FILE_NAME = "queue_data.txt";
    private static final String STAT_FILE_NAME = "queue_stat.txt";
    private static final byte VALID = 0x1;
    private static final byte INVALID = 0x0;

    private String getQueueDir(String queueName) {
        return BASE_DIR + queueName;
    }

    private String getMessageFilePath(String queueName) {
        return getQueueDir(queueName) + "/" + MESSAGE_FILE_NAME;
    }

    private String getStatFilePath(String queueName) {
        return getQueueDir(queueName) + "/" + STAT_FILE_NAME;
    }

    public void createQueueFiles(String queueName) throws IOException {
        File queueDir = new File(getQueueDir(queueName));
        if (!queueDir.exists()) {
            queueDir.mkdirs();
        }

        File messageFile = new File(getMessageFilePath(queueName));
        if (!messageFile.exists()) {
            messageFile.createNewFile();
        }

        File statFile = new File(getStatFilePath(queueName));
        if (!statFile.exists()) {
            statFile.createNewFile();
        }
    }

    public void destroyQueueFiles(String queueName) {
        File messageFile = new File(getMessageFilePath(queueName));
        if (messageFile.exists()) {
            messageFile.delete();
        }

        File statFile = new File(getStatFilePath(queueName));
        if (statFile.exists()) {
            statFile.delete();
        }

        File queueDir = new File(getQueueDir(queueName));
        if (queueDir.exists()) {
            queueDir.delete();
        }
    }

    public void sendMessage(MSGQueueMessage message) throws IOException {
        sendMessage(message.getQueueName(), message.getMessage());
    }

    public void sendMessage(String queueName, Message message) throws IOException {
        createQueueFiles(queueName);

        byte[] body = message.getBody();
        if (body == null) {
            body = new byte[0];
        }

        try (RandomAccessFile messageFile = new RandomAccessFile(getMessageFilePath(queueName), "rw");
             RandomAccessFile statFile = new RandomAccessFile(getStatFilePath(queueName), "rw")) {
            long offsetBeg = messageFile.length();
            messageFile.seek(offsetBeg);
            messageFile.writeByte(VALID);
            messageFile.writeUTF(message.getMessageId());
            messageFile.writeUTF(message.getRoutingKey());
            messageFile.writeInt(message.getDeliveryMode());
            messageFile.writeInt(body.length);
            messageFile.write(body);
            long offsetEnd = messageFile.getFilePointer();

            message.setOffsetBeg(offsetBeg);
            message.setOffsetEnd(offsetEnd);
            message.setIsValid(VALID);

            statFile.seek(statFile.length());
            statFile.writeLong(offsetBeg);
            statFile.writeLong(offsetEnd);
        }
    }

    public void deleteMessage(String queueName, Message message) throws IOException {
        deleteMessage(queueName, message.getOffsetBeg());
    }

    public void deleteMessage(String queueName, long offsetBeg) throws IOException {
        try (RandomAccessFile messageFile = new RandomAccessFile(getMessageFilePath(queueName), "rw")) {
            if (offsetBeg >= 0 && offsetBeg < messageFile.length()) {
                messageFile.seek(offsetBeg);
                messageFile.writeByte(INVALID);
            }
        }
    }

    public List<Message> loadAllMessages(String queueName) throws IOException {
        List<Message> messages = new LinkedList<>();
        File messageFileObj = new File(getMessageFilePath(queueName));
        if (!messageFileObj.exists()) {
            return messages;
        }

        try (RandomAccessFile messageFile = new RandomAccessFile(messageFileObj, "r")) {
            while (messageFile.getFilePointer() < messageFile.length()) {
                long offsetBeg = messageFile.getFilePointer();
                byte isValid = messageFile.readByte();
                String messageId = messageFile.readUTF();
                String routingKey = messageFile.readUTF();
                int deliveryMode = messageFile.readInt();
                int bodyLength = messageFile.readInt();
                byte[] body = new byte[bodyLength];
                messageFile.readFully(body);
                long offsetEnd = messageFile.getFilePointer();

                if (isValid == VALID) {
                    Message message = Message.createMessageWithId(routingKey, null, body);
                    message.setMessageId(messageId);
                    message.getBasicProperties().setDeliveryMode(deliveryMode);
                    message.setOffsetBeg(offsetBeg);
                    message.setOffsetEnd(offsetEnd);
                    message.setIsValid(isValid);
                    messages.add(message);
                }
            }
        }
        return messages;
    }

    public void gc(String queueName) throws IOException {
        List<Message> messages = loadAllMessages(queueName);
        File messageFile = new File(getMessageFilePath(queueName));
        File statFile = new File(getStatFilePath(queueName));

        if (messageFile.exists()) {
            messageFile.delete();
        }
        if (statFile.exists()) {
            statFile.delete();
        }

        createQueueFiles(queueName);
        for (Message message : messages) {
            sendMessage(queueName, message);
        }
    }

    public static class MSGQueueMessage {
        private String queueName;
        private Message message;

        public MSGQueueMessage(String queueName, Message message) {
            this.queueName = queueName;
            this.message = message;
        }

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }
}
