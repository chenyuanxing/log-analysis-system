package com.cad.entity.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class AlarmConfig implements Serializable {
    private String dashboardName;
    private String panelId;
    private String type;
    // 线条名称与告警数值的对应.
    private HashMap<String ,Double> alarmMap;
    private String email;

    private String index;
    private String datefield;
    private String searchName;
    private String interval;
    private String Fromdate;
    private String Todate;
    private String mutilines;
    private String FilterJsonString;
    private int maxNum = 4;

    public AlarmConfig(){

    }
    public AlarmConfig(String dashboardName, String panelId, String type, HashMap<String, Double> alarmMap, String email, String index, String datefield, String searchName, String interval, String fromdate, String todate, String mutilines, String filterJsonString) {
        this.dashboardName = dashboardName;
        this.panelId = panelId;
        this.type = type;
        this.alarmMap = alarmMap;
        this.email = email;
        this.index = index;
        this.datefield = datefield;
        this.searchName = searchName;
        this.interval = interval;
        Fromdate = fromdate;
        Todate = todate;
        this.mutilines = mutilines;
        FilterJsonString = filterJsonString;
    }

    public String getDashboardName() {
        return dashboardName;
    }

    public void setDashboardName(String dashboardName) {
        this.dashboardName = dashboardName;
    }

    public String getPanelId() {
        return panelId;
    }

    public void setPanelId(String panelId) {
        this.panelId = panelId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, Double> getAlarmMap() {
        return alarmMap;
    }

    public void setAlarmMap(HashMap<String, Double> alarmMap) {
        this.alarmMap = alarmMap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDatefield() {
        return datefield;
    }

    public void setDatefield(String datefield) {
        this.datefield = datefield;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getFromdate() {
        return Fromdate;
    }

    public void setFromdate(String fromdate) {
        Fromdate = fromdate;
    }

    public String getTodate() {
        return Todate;
    }

    public void setTodate(String todate) {
        Todate = todate;
    }

    public String getMutilines() {
        return mutilines;
    }

    public void setMutilines(String mutilines) {
        this.mutilines = mutilines;
    }

    public String getFilterJsonString() {
        return FilterJsonString;
    }

    public void setFilterJsonString(String filterJsonString) {
        FilterJsonString = filterJsonString;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    @Override
    public String toString() {
        return "AlarmConfig{" +
                "dashboardName='" + dashboardName + '\'' +
                ", panelId='" + panelId + '\'' +
                ", type='" + type + '\'' +
                ", index='" + index + '\'' +
                ", datefield='" + datefield + '\'' +
                ", searchName='" + searchName + '\'' +
                ", interval='" + interval + '\'' +
                ", maxNum=" + maxNum +
                '}';
    }
}
