package com.cad.web.domain;

import java.util.ArrayList;

/**
 * Dashboard类
 * dashboardname dashboard名称，唯一
 * chart_params 数组，包括panel中内容，和panel的 位置以及大小
 */
public class Dashboard{
    private String dashboardname;
    private String id;
    private ArrayList<Chart> charts = new ArrayList();

    /**
     * @param panel panel实体
     * @param args 包括 int x,int y,int w,int h
     */
    public void addChart_param(Panel panel, int ...args) {
        Chart chartparam = new Chart();
        chartparam.setPanel(panel);
        if(args.length==4){
            chartparam.setX_axis(args[0]);
            chartparam.setY_axis(args[1]);
            chartparam.setWidth(args[2]);
            chartparam.setHeight(args[3]);
        }
        this.charts.add(chartparam) ;
    }

    /**
     * @param chart
     */
    public void addChart(Chart chart) {
        this.charts.add(chart);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDashboardname() {
        return dashboardname;
    }

    public void setDashboardname(String dashboardname) {
        this.dashboardname = dashboardname;
    }

    public ArrayList<Chart> getCharts() {
        return charts;
    }

    public void setCharts(ArrayList<Chart> charts) {
        this.charts = charts;
    }

    @Override
    public String toString() {
        return "Dashboard{" +
                "dashboardname='" + dashboardname + '\'' +
                ", id='" + id + '\'' +
                ", charts=" + charts +
                '}';
    }
}
