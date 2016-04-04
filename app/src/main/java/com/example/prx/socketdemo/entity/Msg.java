package com.example.prx.socketdemo.entity;

/**
 * Created by prx on 2016/4/4 17:45.
 * Email:pangrongxian@gmail.com
 */
public class Msg {

    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    private String content;
    private int type;

    public Msg(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "content='" + content + '\'' +
                ", type=" + type +
                '}';
    }
}
