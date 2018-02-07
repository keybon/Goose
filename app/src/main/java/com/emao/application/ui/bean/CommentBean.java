package com.emao.application.ui.bean;

/**
 *
 * @author keybon
 */

public class CommentBean {

    private String userid;
    private String nickName;
    private String Content;
    private String time;
    private String to;


    public CommentBean(String nickName, String content, String time) {
        this.nickName = nickName;
        Content = content;
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
