package com.xsh.entity;

public class CommonResponse<T> {
    private String code;
    private String msg;
    private T data;

    public CommonResponse() {
    }

    public CommonResponse(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>("200", "操作成功", data);
    }

    public static <T> CommonResponse<T> success(String msg, T data) {
        return new CommonResponse<>("200", msg, data);
    }

    public static CommonResponse<?> error(String msg) {
        return new CommonResponse<>("500", msg, null);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
