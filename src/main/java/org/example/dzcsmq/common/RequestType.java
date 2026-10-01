package org.example.dzcsmq.common;

public class RequestType {
    public static final String CREATE_CHANNEL = "createChannel";
    public static final String CLOSE_CHANNEL = "closeChannel";
    public static final String EXCHANGE_DECLARE = "exchangeDeclare";
    public static final String EXCHANGE_DELETE = "exchangeDelete";
    public static final String QUEUE_DECLARE = "queueDeclare";
    public static final String QUEUE_DELETE = "queueDelete";
    public static final String QUEUE_BIND = "queueBind";
    public static final String QUEUE_UNBIND = "queueUnbind";
    public static final String BASIC_PUBLISH = "basicPublish";
    public static final String BASIC_CONSUME = "basicConsume";
    public static final String BASIC_ACK = "basicAck";

    private RequestType() {
    }
}
