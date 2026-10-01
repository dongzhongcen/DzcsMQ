package org.example.dzcsmq.common;

import java.io.Serializable;

public class Response implements Serializable {
    private boolean ok;
    private int code;
    private String reason;
    private Object data;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public static Response ok(Object data) {
        Response response = new Response();
        response.setOk(true);
        response.setCode(ResponseCode.OK);
        response.setData(data);
        return response;
    }

    public static Response error(String reason) {
        Response response = new Response();
        response.setOk(false);
        response.setCode(ResponseCode.ERROR);
        response.setReason(reason);
        return response;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
