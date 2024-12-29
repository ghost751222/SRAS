package com.consiliuminc.sras.vo;

import org.apache.logging.log4j.util.Strings;

public class ScfCustomHeaderVo {

    private String headerName;
    private Object data = Strings.EMPTY;

    public ScfCustomHeaderVo() {

    }


    public ScfCustomHeaderVo(String headerName, String data) {
        this.headerName = headerName;
        this.data = data;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

