package com.hureru.common;

import lombok.Data;

/**
 * @author zheng
 */
@Data
public class R<T> {
    int code;
    String msg;
    T data;

    public static <T> R<T> ok(T data){
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg("ok");
        r.setData(data);
        return r;
    }

    public static <T> R<T> ok(String msg, T data){
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static <T> R<T> ok (int code, String msg, T data){
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
}
