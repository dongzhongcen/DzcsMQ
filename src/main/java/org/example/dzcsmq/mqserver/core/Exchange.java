package org.example.dzcsmq.mqserver.core;

import java.util.HashMap;
import java.util.Map;

public class Exchange {
    private String name;
    private ExchangeType type = ExchangeType.DIRECT;
    private boolean durable = false;
    private boolean autoDelete = false;
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
