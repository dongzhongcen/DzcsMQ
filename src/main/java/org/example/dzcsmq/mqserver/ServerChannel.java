package org.example.dzcsmq.mqserver;

import org.example.dzcsmq.common.Request;
import org.example.dzcsmq.common.RequestType;
import org.example.dzcsmq.common.Response;
import org.example.dzcsmq.mqserver.core.BasicProperties;
import org.example.dzcsmq.mqserver.core.ExchangeType;
import org.example.dzcsmq.mqserver.core.VirtualHost;

import java.io.IOException;
import java.util.Map;

public class ServerChannel {
    private final String channelId;
    private final VirtualHost virtualHost;
    private volatile boolean open = true;

    public ServerChannel(String channelId, VirtualHost virtualHost) {
        this.channelId = channelId;
        this.virtualHost = virtualHost;
    }

    public Response process(Request request) {
        if (!open) {
            return Response.error("channel is closed");
        }
        try {
            return switch (request.getType()) {
                case RequestType.CLOSE_CHANNEL -> closeChannel();
                case RequestType.EXCHANGE_DECLARE -> exchangeDeclare(request.getArguments());
                case RequestType.EXCHANGE_DELETE -> exchangeDelete(request.getArguments());
                case RequestType.QUEUE_DECLARE -> queueDeclare(request.getArguments());
                case RequestType.QUEUE_DELETE -> queueDelete(request.getArguments());
                case RequestType.QUEUE_BIND -> queueBind(request.getArguments());
                case RequestType.QUEUE_UNBIND -> queueUnbind(request.getArguments());
                case RequestType.BASIC_PUBLISH -> basicPublish(request.getArguments());
                case RequestType.BASIC_ACK -> basicAck(request.getArguments());
                default -> Response.error("unknown request type: " + request.getType());
            };
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    private Response closeChannel() {
        open = false;
        return Response.ok(true);
    }

    private Response exchangeDeclare(Map<String, Object> args) {
        String exchangeName = stringArg(args, "exchangeName");
        ExchangeType exchangeType = ExchangeType.valueOf(stringArg(args, "exchangeType"));
        boolean durable = booleanArg(args, "durable");
        boolean autoDelete = booleanArg(args, "autoDelete");
        return Response.ok(virtualHost.exchangeDeclare(exchangeName, exchangeType, durable, autoDelete));
    }

    private Response exchangeDelete(Map<String, Object> args) {
        return Response.ok(virtualHost.exchangeDelete(stringArg(args, "exchangeName")));
    }

    private Response queueDeclare(Map<String, Object> args) throws IOException {
        String queueName = stringArg(args, "queueName");
        boolean durable = booleanArg(args, "durable");
        boolean exclusive = booleanArg(args, "exclusive");
        boolean autoDelete = booleanArg(args, "autoDelete");
        return Response.ok(virtualHost.queueDeclare(queueName, durable, exclusive, autoDelete));
    }

    private Response queueDelete(Map<String, Object> args) {
        return Response.ok(virtualHost.queueDelete(stringArg(args, "queueName")));
    }

    private Response queueBind(Map<String, Object> args) {
        return Response.ok(virtualHost.queueBind(stringArg(args, "queueName"), stringArg(args, "exchangeName"), stringArg(args, "bindingKey")));
    }

    private Response queueUnbind(Map<String, Object> args) {
        return Response.ok(virtualHost.queueUnbind(stringArg(args, "queueName"), stringArg(args, "exchangeName"), stringArg(args, "bindingKey")));
    }

    private Response basicPublish(Map<String, Object> args) throws IOException {
        String exchangeName = stringArg(args, "exchangeName");
        String routingKey = stringArg(args, "routingKey");
        BasicProperties basicProperties = (BasicProperties) args.get("basicProperties");
        byte[] body = (byte[]) args.get("body");
        return Response.ok(virtualHost.basicPublish(exchangeName, routingKey, basicProperties, body));
    }

    private Response basicAck(Map<String, Object> args) {
        return Response.ok(virtualHost.basicAck(stringArg(args, "messageId")));
    }

    private String stringArg(Map<String, Object> args, String key) {
        Object value = args.get(key);
        return value == null ? null : value.toString();
    }

    private boolean booleanArg(Map<String, Object> args, String key) {
        Object value = args.get(key);
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean isOpen() {
        return open;
    }
}
