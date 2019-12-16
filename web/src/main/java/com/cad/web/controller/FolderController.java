package com.cad.web.controller;

import com.cad.web.GeneralResult;
import com.cad.web.domain.FolderContainsDashboard;
import com.cad.web.service.DashboardService;
import com.cad.web.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/folder" )
public class FolderController {

    @Autowired
    private FolderService folderService;
    @Autowired
    private DashboardService dashboardService ;
    /**
     * 添加文件夹
     *
     */
    @RequestMapping(value = "/addfolder",method = RequestMethod.POST)
    public GeneralResult addfolder(@RequestParam("userId") String id, @RequestParam("foldername") String foldername){
        GeneralResult result = new GeneralResult();
        try {
            folderService.addFolder(foldername,id);
            result.setResultStatus(true);
            result.setDescribe("success!");
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            result.setDescribe(e);
        }
        return result;
    }

    /**
     * 删除文件夹
     *
     */
    @RequestMapping(value = "/delfolder",method = RequestMethod.POST)
    public GeneralResult delfolder(@RequestParam("userId") String id, @RequestParam("foldername") String foldername){
        GeneralResult result = new GeneralResult();
        try {
            folderService.delFolder(foldername,id);
            result.setResultStatus(true);
            result.setDescribe("success!");
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            result.setDescribe(e);
        }
        return result;
    }
    /**
     * 获取所有文件夹名称
     *
     */
    @RequestMapping(value = "/getfolders",method = RequestMethod.GET)
    public GeneralResult getfolders(@RequestParam("userId") String id){
        GeneralResult result = new GeneralResult();
        try {
            result.setResultData(folderService.getFolders(id));
            result.setResultStatus(true);
            result.setDescribe("success!");
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            result.setDescribe(e);
        }
        return result;
    }

    /**
     * 获取所有文件夹,以及旗下dashboard名称
     *
     */
    @RequestMapping(value = "/getfoldersAndDashboard",method = RequestMethod.GET)
    public GeneralResult getfoldersAndDashboard(@RequestParam("userId") String id){
        GeneralResult result = new GeneralResult();

        ArrayList<FolderContainsDashboard> arrayList = new ArrayList<>();

        try {
            for(Object folder: folderService.getFolders(id)){
                FolderContainsDashboard folderContainsDashboard =  new FolderContainsDashboard();
                folderContainsDashboard.setFoldername((String)folder);
                folderContainsDashboard.setDashboards(dashboardService.getAllDashboardNameByUsername(id,folderContainsDashboard.getFoldername()));
                arrayList.add(folderContainsDashboard);
            }
            result.setResultData(arrayList);
            result.setResultStatus(true);
            result.setDescribe("success!");
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            result.setDescribe(e);
        }
        return result;
    }
}
