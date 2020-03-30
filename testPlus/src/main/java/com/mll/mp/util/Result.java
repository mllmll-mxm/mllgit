package com.mll.mp.util;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private int status;
    private String code;
    private String message;
    private T data;

    public Result(){}

    public Result(String code,String message){
        this.code = code;
        this.message = message;
    }

    public Result(int status, String code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static<T> Result<T> success(T data){
        return new Result<>(1,"success","success",data);
    }

    public static <T> Result<T> success(T data,String message){
        return new Result<>(1,"success",message,data);
    }

    public static Result<String> success(){
        return success("");
    }

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static<T> Result<T> fail(T data){
        return new Result<>(0,"fail","fail",data);
    }
}
