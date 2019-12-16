package com.cad.web.controller;


import com.cad.web.GeneralResult;

import com.cad.web.domain.Chart;
import com.cad.web.domain.Dashboard;
import com.cad.web.domain.Panel;
import com.cad.web.service.DashboardService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping(value = "/dashboard" )
public class DashboardController {

//    @Value("${manage.folder}")
    private final String defaultFolder = "default";


    @Autowired
    private DashboardService dashboardService;

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public GeneralResult test(){

        GeneralResult result = new GeneralResult();

        result.setResultStatus(true);


        return result;
    }
    /**
     * 新建dashboard
     */
    @RequestMapping(value = "/addDashboard",method = RequestMethod.POST)
    public GeneralResult addDashboard(@RequestParam("userId") String id, @RequestParam("dashboardname") String dashboardname,
                                      @RequestParam(value = "foldername",required = false,defaultValue = defaultFolder) String folder){
//        Dashboard dashboard = new Dashboard();
//        dashboard.setDashboardname(dashboardname);
//        DashboardService.dashboardMap.put(dashboardname,dashboard);
        GeneralResult result = new GeneralResult();

        try {
            dashboardService.addNewDashboard(dashboardname,id,folder);
            result.setResultStatus(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            result.setDescribe(e);
            result.setErrorMessage("dashboard name "+dashboardname+" is exist !!");
        }


        return result;
    }
    /**
     * 获取某用户，某文件夹下 所有dashboard名称
     */
    @RequestMapping(value = "/getDashboardNames",method = RequestMethod.GET)
    public GeneralResult getDashboardNames(@RequestParam("userId") String id,@RequestParam(value = "foldername",required = false,defaultValue = defaultFolder) String folder){

        GeneralResult result = new GeneralResult();
        result.setResultStatus(true);
        result.setResultData(dashboardService.getAllDashboardNameByUsername(id,folder));
        return result;
    }
    /**
     * 将panel添加至仪表盘
     */
    @RequestMapping(value = "/addPanelToDashboard",method = RequestMethod.POST)
    public GeneralResult addPanelToDashboard(@RequestParam("userId") String id ,@RequestParam(value = "foldername",required = false,defaultValue = defaultFolder) String folder,
                                             @RequestParam("dashboardname") String dashboardname, @RequestParam("panelname") String panelname,
                                             @RequestParam("currentchart") String currentchart, @RequestParam("currentpara") String currentpara){
        GeneralResult result = new GeneralResult();

        Panel panel = new Panel();
        UUID uuid = UUID.randomUUID();
        panel.setPanelid(uuid.toString());
        panel.setTitle(panelname);
        panel.setCurrentchart(currentchart);
        panel.setCurrentpara(currentpara);

        try {
            dashboardService.addPanelToDashboard(panel,dashboardname,id,folder);
            result.setResultStatus(true);
            result.setResultData("success");
        }catch (Exception e){
            result.setResultStatus(false);
            result.setErrorMessage(e.toString());
        }
        return result;
    }

    /**
     * 删除某个Chart
     */
    @RequestMapping(value = "/delChartFromDashboard",method = RequestMethod.POST)
    public GeneralResult delChartFromDashboard(@RequestParam("userId") String id ,@RequestParam(value = "foldername",required = false,defaultValue = defaultFolder) String folder,
                                             @RequestParam("dashboardname") String dashboardname, @RequestParam("panelid") String panelid){

        GeneralResult result =  new GeneralResult();
        try {
            dashboardService.delChartFromDashboard(dashboardname,id,folder,panelid);
            result.setResultStatus(true);
        } catch (Exception e) {
            result.setResultStatus(false);
            result.setDescribe(e);
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 更新某个chart
     */
    @RequestMapping(value = "/updateChartFromDashboard",method = RequestMethod.POST)
    public GeneralResult updateChartFromDashboard(@RequestParam("userId") String id ,@RequestParam(value = "foldername",required = false,defaultValue = defaultFolder) String folder,
                                               @RequestParam("dashboardname") String dashboardname,@RequestParam("Chart") String chartString){

        GeneralResult result = new GeneralResult();
//        System.out.println(chartString);

        Gson g = new Gson();
        Chart chart = g.fromJson(chartString, Chart.class);
        if(chart==null){
            result.setResultStatus(false);
            result.setDescribe("chart could not be translate to Chart.class or chart is null ");
            return result;
        }

        try {
            dashboardService.updateChartFromDashboard(dashboardname,id,folder,chart);
            result.setResultStatus(true);
        } catch (Exception e) {
            result.setResultStatus(false);
            result.setDescribe(e);
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 查看某个仪表盘
     */
    @RequestMapping(value = "/getDashboardData",method = RequestMethod.GET)
    public GeneralResult getDashboardData(@RequestParam("userId") String id, @RequestParam("dashboardname") String dashboardname,@RequestParam(value = "foldername",required = false,defaultValue = defaultFolder) String folder){
        GeneralResult result = new GeneralResult();

        Dashboard dashboard= dashboardService.getDashboardByName(dashboardname,id,folder);
        if(dashboard !=null){
            result.setResultData(dashboard);
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("不存在该仪表盘");
        }
        return result;
    }

    /**
     * 删除某个仪表盘
     */
    @RequestMapping(value = "/delDashboard",method = RequestMethod.POST)
    public GeneralResult delDashboard(@RequestParam("userId") String id, @RequestParam(value = "foldername",required = false,defaultValue = defaultFolder) String folder,
                                      @RequestParam("dashboardname") String dashboardname){
        GeneralResult result = new GeneralResult();

//        Dashboard dashboard= dashboardService.getDashboardByName(dashboardname,id,folder);
        try {
            dashboardService.delDahboard(dashboardname,id,folder);
            result.setResultData("success");
            result.setResultStatus(true);
        }catch (Exception e){
            result.setResultStatus(false);
            result.setErrorMessage(e.toString());
        }
        return result;
    }

    /**
     * 更新某个仪表盘
     */
    @RequestMapping(value = "/updateDashboard",method = RequestMethod.POST)
    public GeneralResult updateDashboard(@RequestParam("userId") String id,@RequestParam(value = "foldername",required = false,defaultValue = defaultFolder) String folder,
                                         @RequestParam("dashboardname") String dashboardname, @RequestParam("dashboard") String dashboarddata,
                                         @RequestParam(value = "dashboardID",required = false,defaultValue = "0000") String dashboardid){
        GeneralResult result = new GeneralResult();

//        Dashboard dashboard= dashboardService.getDashboardByName(dashboardname,id,folder);
//        Dashboard dashboard = new Dashboard();

        Gson g = new Gson();
        Dashboard dashboard = g.fromJson(dashboarddata, Dashboard.class);
//        dashboard.setCharts(charts);
        try {
            dashboardService.updateDashboard(dashboardname,id,dashboard,folder);
            result.setResultData("success");
            result.setResultStatus(true);
        }catch (Exception e){
            result.setResultStatus(false);
            result.setErrorMessage(e.toString());
        }
        return result;
    }
    /**
     * changeDashboardName 更改dashboard 名称
     */
    @RequestMapping(value = "/changeDashboardName",method = RequestMethod.POST)
    public GeneralResult changeDashboardName(@RequestParam("userId") String id,
                                             @RequestParam(value = "foldername",required = false,defaultValue = defaultFolder) String folder,
                                             @RequestParam("dashboardname") String dashboardname,
                                             @RequestParam("newDashboardname") String newDashboardname,
                                             @RequestParam(value = "dashboardID",required = false,defaultValue = "0000") String dashboardid){
        GeneralResult result = new GeneralResult();

//        Dashboard dashboard= dashboardService.getDashboardByName(dashboardname,id,folder);
//        Dashboard dashboard = new Dashboard();
        Gson g = new Gson();
        try {
            Dashboard dashboard = dashboardService.getDashboardByName(dashboardname,id,folder);
            dashboard.setDashboardname(newDashboardname);
            Boolean addSuccess = dashboardService.addDashboard(dashboard,id,folder);
            if (addSuccess){
                dashboardService.delDahboard(dashboardname,id,folder);
                result.setResultData("success");
                result.setResultStatus(true);
            }else {
                result.setErrorMessage("Could not addDashboard .So failed");
                result.setResultStatus(false);
            }
        }catch (Exception e ){
            result.setErrorMessage(e.toString());
            result.setResultStatus(false);
        }
        return result;
    }

}
