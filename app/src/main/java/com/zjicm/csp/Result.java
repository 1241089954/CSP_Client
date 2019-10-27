package com.zjicm.csp;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private TYPE resultType;
    private String message;
    private String onNumber ;
    private T value;
    private int csp;

    enum TYPE {
        TABLE_INFO,
        TABLE_UPDATE,
        Game_START,
        CSP_INFO,
    }

    public Result() {

    }

    public Result(TYPE type,int csp){
        this.resultType = type;
        this.csp = csp;
    }

    public int getCsp() {
        return csp;
    }

    public void setCsp(int csp) {
        this.csp = csp;
    }

    public Result(TYPE resultType) {
        this.resultType = resultType;
    }

    public Result(TYPE resultType,String message,String onNumber, T value) {
        this.resultType = resultType;
        this.message = message;
        this.onNumber = onNumber;
        this.value = value;
    }

    public TYPE getResultType() {
        return resultType;
    }

    public void setResultType(TYPE resultType) {
        this.resultType = resultType;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOnNumber() {
        return onNumber;
    }

    public void setOnNumber(String onNumber) {
        this.onNumber = onNumber;
    }
}
