package com.consiliuminc.sras.vo;

import org.springframework.http.HttpStatus;

public class ResVo {
    private HttpStatus status;

    private Integer statusCode;

    private String message;


    public ResVo(){

    }
    public ResVo(HttpStatus status, String message) {
        this.setStatus(status);
        this.message = message;


    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
        this.statusCode = this.status.value();
        this.message = this.status.getReasonPhrase();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public Integer getStatusCode() {
        return statusCode;
    }
}
