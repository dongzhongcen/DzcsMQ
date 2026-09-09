package org.example.dzcsmq.mqserver.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class MSGQueue {
    private String name;
    private boolean durable = false;
    private boolean exclusive = false;
    private boolean autoDelete = false;
    private Map<String, Object> arguments = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    public Queue<Message> getMessages() {
        return messages;
    }

    public void setMessages(Queue<Message> messages) {
        this.messages = messages;
    }

    private Queue<Message> messages;

    public String getArguments(){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(arguments);
        } catch (JsonProcessingException e) {
            return "{}";
        }


    }

    public void setArguments(String argumentsJson){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            this.arguments = objectMapper.readValue(argumentsJson, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }}
