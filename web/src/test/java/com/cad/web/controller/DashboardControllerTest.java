//package com.cad.web.controller;
//
//import com.cad.web.domain.Chart;
////import net.sf.json.JSONObject;
//import com.google.gson.Gson;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class DashboardControllerTest {
//    @Test
//    public void updateDashboard() {
//        String chartString = "{\n" +
//                "\"panel\":{\n" +
//                "\"panelid\": \"panelid1\",\n" +
//                "\"title\": \"API响应时间\",\n" +
//                "\"currentchart\": \"aaa\",\n" +
//                "\"currentpara\": \"{\\\"title\\\":\\\"API响应时间\\\",\\\"chartdata\\\":[171,104,52,151,204,115,153,60,87,170,171,21,94,60,27,148,61,206],\\\"legend\\\":[\\\"he\\\"],\\\"url\\\":\\\"http://10.108.210.194:8999/panel/realTimePanel?userId=1&isRealTime=true&Index=Index&LastTime=2018-12-06 11:10:51&interval=20&Yaxis=responseTime&Indicator=count\\\",\\\"chartType\\\":\\\"line\\\"}\"\n" +
//                "},\n" +
//                "\"x_axis\": 0,\n" +
//                "\"y_axis\": 0,\n" +
//                "\"width\": 0,\n" +
//                "\"height\": 0\n" +
//                "}";
//        System.out.println(chartString);
//
//        Gson g = new Gson();
//        Chart chart = g.fromJson(chartString, Chart.class);
//
////
////        JSONObject jsonObject= JSONObject.fromObject(chartString);
////
////        Chart chart = (Chart) JSONObject.toBean(jsonObject,Chart.class);
//        System.out.println(chart.getPanel().getPanelid());
//    }
//
//}