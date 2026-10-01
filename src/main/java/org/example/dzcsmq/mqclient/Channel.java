package org.example.dzcsmq.mqclient;

import org.example.dzcsmq.common.Request;
import org.example.dzcsmq.common.RequestType;
import org.example.dzcsmq.common.Response;
import org.example.dzcsmq.mqserver.core.BasicProperties;
import org.example.dzcsmq.mqserver.core.ExchangeType;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Channel implements Closeable {
    private final Connection connection;
    private final String channelId;
    private boolean open = true;

    Channel(Connection connection) throws IOException, ClassNotFoundException {
        this.connection = connection;
        Response response = connection.sendRequest(buildRequest(RequestType.CREATE_CHANNEL, null));
        if (!response.isOk()) {
            throw new IOException(response.getReason());
        }
        this.channelId = (String) response.getData();
    }

    public boolean exchangeDeclare(String exchangeName, ExchangeType exchangeType, boolean durable, boolean autoDelete) throws IOException, ClassNotFoundException {
        Map<String, Object> args = new HashMap<>();
        args.put("exchangeName", exchangeName);
        args.put("exchangeType", exchangeType.name());
        args.put("durable", durable);
        args.put("autoDelete", autoDelete);
        return requestBoolean(RequestType.EXCHANGE_DECLARE, args);
    }

    public boolean exchangeDelete(String exchangeName) throws IOException, ClassNotFoundException {
        Map<String, Object> args = new HashMap<>();
        args.put("exchangeName", exchangeName);
        return requestBoolean(RequestType.EXCHANGE_DELETE, args);
    }

    public boolean queueDeclare(String queueName, boolean durable, boolean exclusive, boolean autoDelete) throws IOException, ClassNotFoundException {
        Map<String, Object> args = new HashMap<>();
        args.put("queueName", queueName);
        args.put("durable", durable);
        args.put("exclusive", exclusive);
        args.put("autoDelete", autoDelete);
        return requestBoolean(RequestType.QUEUE_DECLARE, args);
    }

    public boolean queueDelete(String queueName) throws IOException, ClassNotFoundException {
        Map<String, Object> args = new HashMap<>();
        args.put("queueName", queueName);
        return requestBoolean(RequestType.QUEUE_DELETE, args);
    }

    public boolean queueBind(String queueName, String exchangeName, String bindingKey) throws IOException, ClassNotFoundException {
        Map<String, Object> args = new HashMap<>();
        args.put("queueName", queueName);
        args.put("exchangeName", exchangeName);
        args.put("bindingKey", bindingKey);
        return requestBoolean(RequestType.QUEUE_BIND, args);
    }

    public boolean queueUnbind(String queueName, String exchangeName, String bindingKey) throws IOException, ClassNotFoundException {
        Map<String, Object> args = new HashMap<>();
        args.put("queueName", queueName);
        args.put("exchangeName", exchangeName);
        args.put("bindingKey", bindingKey);
        return requestBoolean(RequestType.QUEUE_UNBIND, args);
    }

    public boolean basicPublish(String exchangeName, String routingKey, BasicProperties basicProperties, byte[] body) throws IOException, ClassNotFoundException {
        Map<String, Object> args = new HashMap<>();
        args.put("exchangeName", exchangeName);
        args.put("routingKey", routingKey);
        args.put("basicProperties", basicProperties);
        args.put("body", body);
        return requestBoolean(RequestType.BASIC_PUBLISH, args);
    }

    public boolean basicAck(String messageId) throws IOException, ClassNotFoundException {
        Map<String, Object> args = new HashMap<>();
        args.put("messageId", messageId);
        return requestBoolean(RequestType.BASIC_ACK, args);
    }

    private boolean requestBoolean(String type, Map<String, Object> args) throws IOException, ClassNotFoundException {
        Response response = connection.sendRequest(buildRequest(type, args));
        if (!response.isOk()) {
            throw new IOException(response.getReason());
        }
        Object data = response.getData();
        return data instanceof Boolean && (Boolean) data;
    }

    private Request buildRequest(String type, Map<String, Object> args) {
        Request request = new Request();
        request.setType(type);
        request.setChannelId(channelId);
        if (args != null) {
            request.setArguments(args);
        }
        return request;
    }

    @Override
    public void close() throws IOException {
        if (!open) {
            return;
        }
        try {
            connection.sendRequest(buildRequest(RequestType.CLOSE_CHANNEL, null));
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        } finally {
            open = false;
        }
    }

    public String getChannelId() {
        return channelId;
    }
}
