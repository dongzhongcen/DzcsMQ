package org.example.dzcsmq.mqserver.core;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private BasicProperties basicProperties = new BasicProperties();
    private byte[] body;

    private transient long offsetBeg = 0;
    private transient long offsetEnd = 0;

    private byte isValid = 0x1; //validation of message 0x0 un
    //--------------------------------------------------------------------------------
    /*
        factory method for better name, the reason for discarding gouzaofangfa
     */
    public static Message createMessageWithId(String routingKey, BasicProperties basicProperties, byte[] body){
        Message message = new Message();
        if(basicProperties != null){
            message.setBasicProperties(basicProperties);
        }
        message.basicProperties.setRoutingKey(routingKey);
        message.basicProperties.setMessageId("M-" + UUID.randomUUID());
        message.body = body;
        return message;

    }

    //--------------------------------------------------------------------------------
    public String getMessageId(){
        return basicProperties.getMessageId();
    }
    public void setMessageId(String messageId){
        basicProperties.setMessageId(messageId);
    }

    public String getRoutingKey(){
        return basicProperties.getRoutingKey();
    }

    public void setRoutingKey(String routingKey){
        basicProperties.setRoutingKey(routingKey);
    }

    public int getDeliveryMode(){
        return basicProperties.getDeliveryMode();
    }

    public void setDeliveryMode(String deliveryMode){
        basicProperties.setRoutingKey(deliveryMode);
    }
    //--------------------------------------------------------------------------------
    /*
        getter and setter
     */

    public BasicProperties getBasicProperties() {
        return basicProperties;
    }

    public void setBasicProperties(BasicProperties basicProperties) {
        this.basicProperties = basicProperties;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public long getOffsetBeg() {
        return offsetBeg;
    }

    public void setOffsetBeg(long offsetBeg) {
        this.offsetBeg = offsetBeg;
    }

    public long getOffsetEnd() {
        return offsetEnd;
    }

    public void setOffsetEnd(long offsetEnd) {
        this.offsetEnd = offsetEnd;
    }

    public byte getIsValid() {
        return isValid;
    }

    public void setIsValid(byte isValid) {
        this.isValid = isValid;
    }

}


