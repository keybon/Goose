package com.emao.application.ui.bean;

/**
 *
 * @author keybon
 */

public class ClassifyBean {


    private String utime;
    private String id;
    private String ctime;
    private String name;
    private String sort;

    public ClassifyBean(String name,String id) {
        this.name = name;
        this.id = id;
    }

    public String getUtime() {
        return utime;
    }

    public void setUtime(String utime) {
        this.utime = utime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
