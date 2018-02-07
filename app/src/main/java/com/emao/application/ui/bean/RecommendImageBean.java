package com.emao.application.ui.bean;

import java.io.Serializable;

/**
 *
 * @author keybon
 */

public class RecommendImageBean implements Serializable{

    private String id;
    private String picture;
    private String ctime;
    private String cid;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
