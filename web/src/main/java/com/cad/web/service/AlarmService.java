package com.cad.web.service;

import com.cad.entity.domain.AlarmConfig;
import com.cad.web.GeneralResult;
import com.cad.web.controller.LogController;
import com.cad.web.dao.RedisDBHelperImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.kafka.common.protocol.types.Field;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlarmService {
    @Resource(name = "RedisDBHelper")
    private RedisDBHelperImpl redisDBHelper;

    @Value("${manage.alarm.key}")
    private String alarmKey;

    @Value("${manage.alarmRule.key}")
    private String alarmRuleKey;

    @Autowired
    private LogController logController;

    @Autowired
    private SendMail sendMail;
    /**
     * 报警全部是针对一个panel中一个线条做的.
     * @param alarmConfig
     */
    public void addAlarm(AlarmConfig alarmConfig){
        // 增加之前先查询是否存在，若存在，则添加到map中
        AlarmConfig preAlarmConfig = (AlarmConfig) redisDBHelper.hashGet(alarmKey,getAlarmHashKey(alarmConfig.getDashboardName(),alarmConfig.getPanelId()));
        if (preAlarmConfig!=null ){
            HashMap<String, Double>  map = preAlarmConfig.getAlarmMap();
            alarmConfig.getAlarmMap().putAll(map);
        }
        redisDBHelper.hashPut(alarmKey,getAlarmHashKey(alarmConfig.getDashboardName(),alarmConfig.getPanelId()),alarmConfig);

    }

    public AlarmConfig getAlarm(String dashboardName,String panelId){
        AlarmConfig alarmConfig = (AlarmConfig) redisDBHelper.hashGet(alarmKey,getAlarmHashKey(dashboardName,panelId));
        return alarmConfig;
    }
    public void delAlarm(String dashboardName,String panelId,String lineName){
        AlarmConfig preAlarmConfig = (AlarmConfig) redisDBHelper.hashGet(alarmKey,getAlarmHashKey(dashboardName,panelId));
        if (preAlarmConfig!=null ){
            preAlarmConfig.getAlarmMap().remove(lineName);
            if (preAlarmConfig.getAlarmMap().size()>0){
                redisDBHelper.hashPut(alarmKey,getAlarmHashKey(dashboardName,panelId),preAlarmConfig);
            }else {
                redisDBHelper.hashRemove(alarmKey,getAlarmHashKey(dashboardName,panelId));
            }
        }
    }

    /**
     * 删除一个panel中所有alarm
     * @param dashboardName
     * @param panelId
     */
    public void delAlarmWhole(String dashboardName,String panelId){
        redisDBHelper.hashRemove(alarmKey,getAlarmHashKey(dashboardName,panelId));
    }

    private String getAlarmHashKey(String dashboardName, String panelId){
        return dashboardName+"--"+panelId;
    }

    // 10 秒钟执行一次
    @Scheduled(fixedRate = 10000)
    public void scheduledAlarm() {

        HashMap<String,Object> map = (HashMap<String, Object>) redisDBHelper.hashFindAll(alarmKey);
        for (String key:map.keySet()){
            search((AlarmConfig) map.get(key));
        }
    }

    private void search(AlarmConfig alarmConfig){
        System.out.println(alarmConfig.getIndex());
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String now = sdf.format(new Date());
        GeneralResult generalResult = logController.superMutiDateMetricHistogram("smj",alarmConfig.getIndex(),alarmConfig.getDatefield(),"",now,alarmConfig.getInterval(),alarmConfig.getSearchName(),alarmConfig.getMutilines(),alarmConfig.getFilterJsonString(),alarmConfig.getMaxNum());
        ParsedDateHistogram parsedDateHistogram = (ParsedDateHistogram)generalResult.getResultData();
        HashMap<String, Double> alarmMap = alarmConfig.getAlarmMap();
        for (String lineName:alarmMap.keySet()){
            if (parsedDateHistogram==null){
                continue;
            }
            Aggregation aggregation =  parsedDateHistogram.getBuckets().get(parsedDateHistogram.getBuckets().size()-1).getAggregations().asMap().get(lineName);
            System.out.println(aggregation.toString());
            Map<String, Object> map = aggregation.getMetaData();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeSpecialFloatingPointValues();
            Gson gson = gsonBuilder.create();

            String aggregationString = gson.toJson(aggregation);
            System.out.println("aggregationString:\n" + aggregationString);

            JsonElement jsonElement= gson.toJsonTree(aggregation);
            JsonElement jsonElement2 = jsonElement.getAsJsonObject().get("value");
            String value = "" ;
            if (jsonElement2!=null){
                value = jsonElement2.toString();
            }
            // 判断是否超限
            if (notice(value,alarmMap.get(lineName),alarmConfig.getType())){
                String alarmMessage = "数据指标异常:\n";
                alarmMessage += "your metric is : "+jsonElement.toString()+"\n"
                        + "your alarmConfig is : "+alarmConfig.toString() +"\n"
                        +"your alarmLine is : "+lineName+"\n"+"\n"
                        +"-----------------------------------------------------------";
                System.out.println("alarm :+\n"+alarmMessage);
                // 判断邮件通知规则.
                if (noticeRule(alarmConfig)){
                    try {
                        sendMail.sendAlarmMail(alarmConfig.getEmail(),alarmMessage);
                    } catch (Exception e) {
                        System.out.println(alarmMessage);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private boolean notice(String value,double alarmNum,String type){
        Double val = 0.0;
        if (value.equals("Infinity")&& type.equals("gte")){
            // todo 通知 还是不通知 ?
            return false;
        }
        if(value.equals("-Infinity")&& type.equals("lte")){
            return true;
        }

        if (!value.equals("NaN") && !value.equals("")){
            val = Double.parseDouble(value);
        }
        if (val>alarmNum && type.equals("gte")){
            return true;
        }
        if (val < alarmNum && type.equals("lte")){
            return true;
        }
        return false;
    }

    /**
     *
     * @param alarmConfig
     * @return 返回true为应该通知，false为不用通知
     */
    private boolean noticeRule(AlarmConfig alarmConfig){
        // 1个小时最多通知一遍.
        Long time = (Long) redisDBHelper.hashGet(alarmRuleKey,alarmConfig.getDashboardName()+alarmConfig.getPanelId()+alarmConfig.getEmail());
        long now = new Date().getTime();
        if (time==null){
            redisDBHelper.hashPut(alarmRuleKey,alarmConfig.getDashboardName()+alarmConfig.getPanelId()+alarmConfig.getEmail(),now);
            return true;
        }else if(time+60*60*1000<now){
            redisDBHelper.hashPut(alarmRuleKey,alarmConfig.getDashboardName()+alarmConfig.getPanelId()+alarmConfig.getEmail(),now);
            return true;
        }else {
            return false;
        }
    }
}
