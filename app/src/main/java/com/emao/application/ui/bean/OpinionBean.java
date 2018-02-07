package com.emao.application.ui.bean;

import android.support.annotation.NonNull;

/**
 *
 * @author keybon
 */

public class OpinionBean implements Comparable<OpinionBean>{

    private User user;
    private String uid;
    private String ctime;
    private String id;
    private String username;
    private String headimgurl;
    private String type;
    private String comment;
    private String cid;


    public OpinionBean(String uid, String ctime, String id, String username, String headimgurl, String type, String comment, String cid) {
        this.uid = uid;
        this.ctime = ctime;
        this.id = id;
        this.username = username;
        this.headimgurl = headimgurl;
        this.type = type;
        this.comment = comment;
        this.cid = cid;
    }

    public OpinionBean() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public int compareTo(@NonNull OpinionBean o) {
        int compareCtime = this.ctime.compareTo(o.getCtime());
        return Math.abs(compareCtime);
    }
}
