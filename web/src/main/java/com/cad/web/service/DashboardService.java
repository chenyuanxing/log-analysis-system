package com.cad.web.service;




import com.cad.web.GeneralResult;
import com.cad.web.dao.RedisDBHelperImpl;
import com.cad.web.domain.Chart;
import com.cad.web.domain.Dashboard;
import com.cad.web.domain.Panel;
import com.cad.web.util.PanelLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DashboardService {
//    public static Map<String,Dashboard> dashboardMap = new HashMap();

    @Resource(name = "RedisDBHelper")
    private RedisDBHelperImpl redisDBHelper;

    @Value("${manage.dashboard.forwardkey}")
    private String forwardkey;

    @Autowired
    private FolderService folderService;

    /**
     * @param username 用户名
     * @param folder 文件夹名
     * @return 返回该用户某文件夹所有 dashboard 的数据
     *
     */
    public Map<String,Dashboard> getAllDashboardByUsername(String username,String folder){
        Map<String,Dashboard> map = redisDBHelper.hashFindAll(forwardkey+username+":"+folder);
        return map;
    }
    /**
     * @param username 用户名
     * @return 返回该用户所有 dashboard 名称
     */
    public Set<String> getAllDashboardNameByUsername(String username,String folder){
        Map<String,Dashboard> map = this.getAllDashboardByUsername(username,folder);
        return map.keySet();
    }

    /** 根据名称添加一个空的dashboard
     * @param dashboardname
     * @param username
     * @return
     */
    public boolean addNewDashboard(String dashboardname,String username,String folder)throws Exception{
        //判断在文件夹列表中，是否存在该文件夹,若不存在则添加
        if(!folderService.hasFolder(folder,username)){
            folderService.addFolder(folder,username);
        }

        Set<String> dashboardnames = this.getAllDashboardNameByUsername(username,folder);

        if(dashboardnames.contains(dashboardname)){
            System.out.println("dashboard name "+dashboardname+" is exist !!");
            return false;
        }else{
            Dashboard dashboard = new Dashboard();
            dashboard.setDashboardname(dashboardname);
            redisDBHelper.hashPut(forwardkey+username+":"+folder,dashboardname,dashboard);
            return true;
        }
    }

    /** 添加一个新的 dashboard
     * @param dashboard
     * @param username
     * @return
     */
    public boolean addDashboard(Dashboard dashboard,String username, String folder){
        Set<String> dashboardnames = this.getAllDashboardNameByUsername(username,folder);
        if(dashboard ==null){
            System.out.println("dashboard can not be null ");
            return false;
        }
        if(dashboard.getDashboardname()==null || dashboard.getDashboardname().length()<=0){
            System.out.println("dashboard name "+dashboard.getDashboardname()+" is not permit !!");
            return false;
        }
        if(dashboardnames.contains(dashboard.getDashboardname())){
            System.out.println("dashboard name "+dashboard.getDashboardname()+" is exist !!");
            return false;
        }else{

            redisDBHelper.hashPut(forwardkey+username+":"+folder,dashboard.getDashboardname(),dashboard);
            return true;
        }
    }

    /** 根据用户名 & folder 和 dashboard 名称 获取 dashboard 数据
     * @param dashboardname
     * @param username
     * @return
     */
    public  Dashboard getDashboardByName(String dashboardname,String username,String folder){
        Dashboard dashboard;
        try {
            dashboard  =  (Dashboard) redisDBHelper.hashGet(forwardkey+username+":"+folder,dashboardname);
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
        return dashboard;
    }

    /**
     * 根据用户名 & folder 和 dashboard 名称 获取某 dashboard 内的charts 数据
     * @param dashboardname
     * @param username
     * @return
     */
    public ArrayList<Chart> getChartsByDashboardName(String dashboardname, String username,String folder){
        Dashboard dashboard  = this.getDashboardByName(dashboardname,username,folder);

        if(dashboard==null){
            return null;
        }else {
            return dashboard.getCharts();
        }
    }

    /**删除某dashboard
     * @param dashboardname
     * @param username
     */
    public void delDahboard(String dashboardname,String username,String folder) throws Exception{
        redisDBHelper.hashRemove(forwardkey+username+":"+folder,dashboardname);
    }
    /**
     * 将 dashboard 数据更新为 newdashboard,若dashboardname等于newdashboard中dashboardname，则替换，否则为删除后添加
     * @param dashboardname
     * @param username
     * @param dashboard
     */
    public void updateDashboard(String dashboardname, String username,Dashboard dashboard,String folder)throws Exception{
        if(dashboard.getDashboardname().equals(dashboardname)){
            redisDBHelper.hashPut(forwardkey+username+":"+folder,dashboard.getDashboardname(),dashboard);
        }else {
            if(this.addDashboard(dashboard,username,folder)){
                this.delDahboard(dashboardname,username,folder);
            }else{
                System.out.println("addDashboard wrong ! maybe it has exit the dashboard`s dashboardname");
            }
        }
    }

    public void addPanelToDashboard(Panel panel, String dashboardname, String username,String folder)throws Exception{

        Dashboard dashboard =  this.getDashboardByName(dashboardname,username,folder);
        if(dashboard==null){
            this.addNewDashboard(dashboardname,username,folder);
            dashboard = this.getDashboardByName(dashboardname,username,folder);
        }
        int[] location = new PanelLocation().getpanelLocation(dashboard);
        dashboard.addChart_param(panel,location);
        this.updateDashboard(dashboardname,username,dashboard,folder);
    }


    /**
     * 将 删除dashboard中某Chart
     * @param dashboardname
     * @param username
     * @param folder
     * @param panelid
     */
    public void delChartFromDashboard(String dashboardname, String username, String folder, String panelid)throws Exception{
        Dashboard dashboard = this.getDashboardByName(dashboardname,username,folder);
        if(dashboard==null){
            throw new Exception("cannot found this dashboard");
        }
        boolean removepanel = false;

        Chart delchart1 = new Chart();
        for(Chart chart:dashboard.getCharts()){
            if(chart.getPanel().getPanelid().equals(panelid)){
                delchart1 = chart;
                removepanel = true;
                break;
            }
        }
        if(removepanel){
            dashboard.getCharts().remove(delchart1);
        }else {
            throw new Exception("cannot found this panelid");
        }

        this.updateDashboard(dashboardname,username,dashboard,folder);
    }

    /**
     * 更新dashboard中某chart
     * @param dashboardname
     * @param username
     * @param folder
     * @param newchart
     */
    public void updateChartFromDashboard(String dashboardname, String username, String folder, Chart newchart)throws Exception{
        Dashboard dashboard = this.getDashboardByName(dashboardname,username,folder);
        if(dashboard==null){
            throw new Exception("cannot found this dashboard");
        }
        boolean removepanel = false;
        Chart delchart1 = new Chart();
        for(Chart chart:dashboard.getCharts()){
            if(chart.getPanel().getPanelid().equals(newchart.getPanel().getPanelid())){
                delchart1 = chart;
                removepanel = true;
                break;
            }
        }
        if(removepanel){
            dashboard.getCharts().remove(delchart1);

        }else {
            throw new Exception("cannot found this panelid");
        }

        dashboard.addChart(newchart);

        this.updateDashboard(dashboardname,username,dashboard,folder);
    }
}
