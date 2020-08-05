package com.fanshanhong.nettydemo.netty.groupchat;

import java.io.Serializable;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-08-05 10:46
 * @Modify:
 */
public class Message implements Serializable {

    String msgType;
    String from;
    String to;
    String content;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
