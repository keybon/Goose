package com.emao.application.ui.bean;

/**
 *
 * @author keybon
 */

public class ReportBean {

    private String itemname;
    private Boolean select;

    public ReportBean(String itemname, Boolean select) {
        this.itemname = itemname;
        this.select = select;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }
}
