package com.hureru.common;

import lombok.Data;

/**
 * @author zheng
 */
@Data
public class R {
    int code;
    String msg;
    Object data;

    public static R ok (){
        R r = new R();
        r.setCode(200);
        return r;
    }

    public static R ok (String msg, Object data){
        R r = new R();
        r.setCode(200);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static R error (String msg){
        R r = new R();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }

    public static R error (int code, String msg){
        R r = new R();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

}
