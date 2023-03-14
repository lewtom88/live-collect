package com.wy.model;

public class Result<T> {

    private boolean success;

    private Integer errorCode;

    private String errorMsg;

    private T data;

    public Result(T data) {
        success = true;
        this.data = data;
    }

    public Result(String errMsg) {
        this.success = false;
        this.errorMsg = errMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
