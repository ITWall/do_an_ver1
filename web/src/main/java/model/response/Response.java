package model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
    private int code;
    private String message;

    @JsonProperty
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
