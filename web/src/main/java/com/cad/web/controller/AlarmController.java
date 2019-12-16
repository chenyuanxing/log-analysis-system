package com.cad.web.controller;

import com.cad.entity.domain.AlarmConfig;
import com.cad.web.GeneralResult;
import com.cad.web.service.AlarmService;
import com.cad.web.service.SendMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/alarm" )
public class AlarmController {
    @Autowired
    private SendMail sendMail;

    @Autowired
    private AlarmService alarmService;

    @RequestMapping(value = "/emailTest",method = RequestMethod.GET)
    public GeneralResult emailTest(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                       @RequestParam(value = "mailto",required = false,defaultValue = "sun_magine@163.com") String mailto){

        GeneralResult result = new GeneralResult();
        try {
            sendMail.sendSimpleMail(mailto);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping(value = "/addAlarm",method = RequestMethod.POST)
    public GeneralResult addAlarm(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                  @RequestParam(value = "dashboardName") String dashboardName,
                                  @RequestParam(value = "panelId") String panelId,
                                  @RequestParam(value = "type" ,required = false,defaultValue = "gte") String type,
                                  @RequestParam(value = "lineName" ) String lineName,
                                  @RequestParam(value = "alarmNum" ) double alarmNum,
                                  @RequestParam(value = "email")String email,

                                  @RequestParam(value = "index") String index,
                                  @RequestParam(value = "datefield",required = false,defaultValue = "@timestamp") String datefield,
                                  @RequestParam(value = "from",required = false,defaultValue = "") String From,
                                  @RequestParam(value = "to",required = false,defaultValue = "") String To,
                                  @RequestParam(value = "interval",required = false,defaultValue = "6h") String interval,
                                  @RequestParam(value = "searchName",required = false,defaultValue = "test") String searchName,
                                  @RequestParam(value = "mutilines",required = false,defaultValue = "") String mutilines,
                                  @RequestParam(value = "filter",required = false,defaultValue = "") String FilterJsonString,
                                  @RequestParam(value = "maxNum",required = false,defaultValue = "4") int maxNum){

        GeneralResult result = new GeneralResult();
        HashMap<String,Double> alarmMap = new HashMap<>();
        alarmMap.put(lineName,alarmNum);
        AlarmConfig alarmConfig =  new AlarmConfig(dashboardName,panelId,type,alarmMap,email,index,datefield,searchName,interval,From,To,mutilines,FilterJsonString);
        try {
            alarmService.addAlarm(alarmConfig);
            result.setResultStatus(true);
        }catch (Exception e){
            result.setResultStatus(false);
            e.printStackTrace();
        }
        return result;
    }
    @RequestMapping(value = "/getAlarm",method = RequestMethod.GET)
    public GeneralResult getAlarm(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                  @RequestParam(value = "dashboardName") String dashboardName,
                                  @RequestParam(value = "panelId") String panelId){

        GeneralResult result = new GeneralResult();
        try {
            AlarmConfig alarmConfig  = alarmService.getAlarm(dashboardName,panelId);
            result.setResultData(alarmConfig);
            result.setResultStatus(true);
        }catch (Exception e){
            result.setResultStatus(false);
            e.printStackTrace();
        }
        return result;
    }
    @RequestMapping(value = "/delAlarm",method = RequestMethod.POST)
    public GeneralResult delAlarm(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                  @RequestParam(value = "dashboardName") String dashboardName,
                                  @RequestParam(value = "panelId") String panelId,
                                  @RequestParam(value = "lineName") String lineName){

        GeneralResult result = new GeneralResult();
        try {
            alarmService.delAlarm(dashboardName,panelId,lineName);
            result.setResultStatus(true);
        }catch (Exception e){
            result.setResultStatus(false);
            e.printStackTrace();
        }
        return result;
    }
    @RequestMapping(value = "/delAlarmWhole",method = RequestMethod.POST)
    public GeneralResult delAlarmWhole(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                  @RequestParam(value = "dashboardName") String dashboardName,
                                  @RequestParam(value = "panelId") String panelId){

        GeneralResult result = new GeneralResult();
        try {
            alarmService.delAlarmWhole(dashboardName,panelId);
            result.setResultStatus(true);
        }catch (Exception e){
            result.setResultStatus(false);
            e.printStackTrace();
        }
        return result;
    }



}
