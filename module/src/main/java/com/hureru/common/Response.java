package com.hureru.common;

import lombok.Data;

/**
 * @author zheng
 */
@Data
public class Response {
    int code;
    String msg;
    Object data;

    public static Response ok (){
        Response r = new Response();
        r.setCode(200);
        r.setMsg("ok");
        return r;
    }

    public static Response ok (int code){
        Response r = new Response();
        r.setCode(code);
        r.setMsg("ok");
        return r;
    }

    public static Response error (String msg){
        Response r = new Response();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }

    public static Response error (int code, String msg){
        Response r = new Response();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

}
