package com.beautyboss.slogen.simplejobserver.web.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Author : Slogen
 * Date   : 2017/12/24
 */
@Data
public class Response<T> implements Status {
    private String code;
    private String msg;
    private int status;
    private T data;

    public Response() {
    }


    public Response(Status status) {
        this(status.getStatus(), status.getCode(), status.getMsg());
    }

    public Response(int status, String code, String msg) {
        this.status = status;
        this.code = code;
        this.msg = msg;
    }

    public static Response failResponse(StatusCode statusCode,String message) {
        Response response = new Response();
        response.setCode(statusCode.getCode());
        response.setStatus(statusCode.getStatus());
        response.setMsg(String.format(statusCode.getMsg(),message));
        response.setData(null);
        return response;
    }


    public static <T> Response response(String code,int status,String msg,T data) {
        Response response = new Response(status,code,msg);
        response.setData(data);
        return response;
    }

    public static <R> Response success(StatusCode statusCode,R data) {
        Response response = new Response(statusCode.getStatus(),statusCode.getCode(),statusCode.getMsg());
        response.setData(data);
        return response;
    }


    @JsonProperty("success")
    public boolean isSuccess() {
        // 有些地方0也表示成功
        return status == 10000;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
