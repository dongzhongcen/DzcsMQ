package org.example.dzcsmq.mqserver.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class Exchange {
    private String name;
    private ExchangeType type = ExchangeType.DIRECT;
    private boolean durable = false;
    private boolean autoDelete = false;

//    public Map<String, Object> getArguments() {
//        return arguments;
//    }
//
//    public void setArguments(Map<String, Object> arguments) {
//        this.arguments = arguments;
//    }

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
    }
    private Map<String, Object> arguments = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExchangeType getType() {
        return type;
    }

    public void setType(ExchangeType type) {
        this.type = type;
    }


    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }
}
