package com.cad.web.controller;


import com.alibaba.fastjson.JSONObject;
import com.cad.elasticsearchservice.Service.ESService;
import com.cad.entity.domain.Line;
import com.cad.entity.domain.SuperLine;
import com.cad.web.GeneralResult;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


@RestController
@RequestMapping(value = "/Loganalysis" )
public class LogController {

    @Autowired
    private ESService esService;
    @Value("${elasticsearch.ip}")
    String[] ipAddress ;
    /**
     * 创建索引
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/creatIndex",method = RequestMethod.POST)
    public GeneralResult creatIndex(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                    @RequestParam(value = "indexName") String INDEX_NAME,
                                    @RequestParam(value = "shards",required = false,defaultValue = "3") int number_of_shards,
                                    @RequestParam(value = "replicas",required = false,defaultValue = "2") int number_of_replicas){

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id可查询哪些index

        // next
        try {
            esService.createIndex(INDEX_NAME,number_of_shards,number_of_replicas);
            result.setResultStatus(true);
            result.setResultData("success");
        }catch (Exception e){
            e.printStackTrace();
            result.setResultData("err");
            result.setResultStatus(false);
            result.setErrorMessage(e.toString());
        }

        return result;
    }
    /**
     * 删除索引
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/delIndex",method = RequestMethod.POST)
    public GeneralResult delIndex(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                    @RequestParam(value = "indexName") String INDEX_NAME){

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id可查询哪些index

        // next
        try {
            if(esService.deleteIndex(INDEX_NAME)){
                result.setResultStatus(true);
                result.setResultData("success");
            }else{
                result.setResultData("false");
                result.setResultStatus(false);
                result.setErrorMessage(INDEX_NAME+" is not exits !");
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setResultData("err");
            result.setResultStatus(false);
            result.setErrorMessage(e.toString());
        }
        return result;
    }
    /**
     * 删除某条数据
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/delData",method = RequestMethod.POST)
    public GeneralResult delIndex(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                  @RequestParam(value = "indexName") String INDEX_NAME,
                                  @RequestParam(value = "type",required = false,defaultValue = "doc") String TYPE,
                                  @RequestParam(value = "docId") String docId){

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id可查询哪些index

        // next
        try {
            result.setResultData(esService.deleteData(INDEX_NAME,TYPE,docId));
            result.setResultStatus(true);
        }catch (Exception e){
            e.printStackTrace();
            result.setResultData("err");
            result.setResultStatus(false);
            result.setErrorMessage(e.toString());
        }
        return result;
    }
    /**
     * 获取es中索引基础信息
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/getIndexBaseInfo",method = RequestMethod.GET)
    public GeneralResult getIndexBaseInfo(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id){

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id可查询哪些index

        // next

        result.setResultData(esService.getIndexBaseInfo());


        if(result.getResultData()!=null){
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }
    /**
     * 获取es中节点的详细信息
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/getNodeDetails",method = RequestMethod.GET)
    public GeneralResult getNodeDetails(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id){

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id可查询哪些index

        // next

        result.setResultData(esService.getNodeDetails(ipAddress[0]));


        if(result.getResultData()!=null){
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }
    /**
     * 获取索引
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/getIndex",method = RequestMethod.GET)
    public GeneralResult getIndex(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id){

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id可查询哪些index

        // next

        result.setResultData(esService.getIndex());


        if(result.getResultData()!=null){
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }
    /**
     * 根据索引获取日志,按照时间排序
     * @param id 用户id
     * @param index 索引
     * @param lines 日志条数
     * @return
     */
    @RequestMapping(value = "/getLog",method = RequestMethod.POST)
    public GeneralResult getLog(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                @RequestParam(value = "index",required = false,defaultValue = "filebeat-6.3.1-2018.11.28") String index,
                                @RequestParam(value = "type",required = false,defaultValue = "doc") String type,
                                @RequestParam(value = "from",required = false,defaultValue = "") String From,
                                @RequestParam(value = "to",required = false,defaultValue = "") String To,
                                @RequestParam(value = "lines",required = false,defaultValue = "200") int lines,
                                @RequestParam(value = "filter",required = false,defaultValue = "") String FilterJsonString){

        GeneralResult result = new GeneralResult();
        // 鉴权 判断该id是否有权限操作该index

        // next
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        if(type==""){
            type = "doc";
        }
        Date Fromdate;
        Date Todate;
//        System.out.println(From);
        try {
            Fromdate = sdf.parse( From);
        } catch (ParseException e) {
            Fromdate = new Date(0);
        }

        try {
            Todate = sdf.parse( To);
        } catch (ParseException e) {
            Todate = new Date();
        }

        result.setResultData(esService.getDataByIndex(index,lines,type,Fromdate,Todate,FilterJsonString));


        if(result.getResultData()!=null){
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }

    /**
     * 根据索引获取所有字段
     * @param id 用户id
     * @param index 索引
     * @return
     */
    @RequestMapping(value = "/getAllFields",method = RequestMethod.GET)
    public GeneralResult getAllFields(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                @RequestParam(value = "index",required = false,defaultValue = "filebeat-6.3.1-2018.11.28") String index){

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id是否有权限操作该index

        // next

//        String[] fields2 = {"userIP", "@timestamp","_type","statusCode","consulReturnTime","realurl"};
        result.setResultData(esService.getFieldsByIndex(index));


        if(result.getResultData()!=null){
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }

    /**
     * 根据索引获取所有字段和类型
     * @param id 用户id
     * @param index 索引
     * @return
     */
    @RequestMapping(value = "/getAllFieldsDetail",method = RequestMethod.GET)
    public GeneralResult getAllFieldsDetail(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                      @RequestParam(value = "index",required = false,defaultValue = "filebeat-6.3.1-2018.11.28") String index){

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id是否有权限操作该index

        // next

//        String[] fields2 = {"userIP", "@timestamp","_type","statusCode","consulReturnTime","realurl"};
        result.setResultData(esService.getAllFieldsDetail(index));


        if(result.getResultData()!=null){
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }

    /**
     *  获取指标型数据单线条（x轴为时间）
     * @param index  数据索引（类似数据库）
     * @param datefield 横坐标（类型为时间）
     * @param From 起始时间 （若缺失或格式错误将以2018-11-13 19:40:19 为默认值）
     * @param To 截止时间 （若缺失或格式错误将以当前时间 为默认值）
     * @param interval 间隔 （为 "1s"  "5s"  "1m"  "1d"  "1w"  等）
     * @param searchName 该次查询名称
     *
     * @param field 查询的属性
     * @param Indicator 指标（max，avg，min）
     * @param LineName 线条名称
     * @return
     */
    @RequestMapping(value = "/dateMetricHistogram",method = RequestMethod.POST)
    public GeneralResult dateMetricHistogram(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                             @RequestParam(value = "index",required = false,defaultValue = "metricbeat-6.2.1-2018.11.*") String index,
                             @RequestParam(value = "datefield",required = false,defaultValue = "@timestamp") String datefield,
                             @RequestParam(value = "from",required = false,defaultValue = "") String From,
                             @RequestParam(value = "to",required = false,defaultValue = "") String To,
                             @RequestParam(value = "interval",required = false,defaultValue = "5m") String interval,
                             @RequestParam(value = "field",required = false,defaultValue = "@timestamp") String field,
                             @RequestParam(value = "Indicator",required = false,defaultValue = "count") String Indicator,
                             @RequestParam(value = "LineName",required = false,defaultValue = "default_LineName") String LineName,
                             @RequestParam(value = "panelName",required = false,defaultValue = "test") String searchName,
                             @RequestParam(value = "filter",required = false,defaultValue = "") String FilterJsonString,
                             @RequestParam(value = "maxNum",required = false,defaultValue = "100") int maxNum){

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id是否有权限操作该index

        // next
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date Fromdate;
        Date Todate;
        try {
            Fromdate = sdf.parse( From);
        } catch (ParseException e) {
            Fromdate = new Date(1542109219000L);
        }

        try {
            Todate = sdf.parse( To);
        } catch (ParseException e) {
            Todate = new Date();
        }
        Fromdate = getRationalFromDate(Fromdate,Todate,interval,maxNum);
        result.setResultData(esService.dateMetricHistogram(index,datefield,Fromdate,Todate,interval,field,Indicator,LineName,searchName,FilterJsonString));


        if(result.getResultData()!=null){
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }


    /**
     *  多线条的数值查询
     * @param index  数据索引（类似数据库）
     * @param datefield 横坐标（类型为时间）
     * @param From 起始时间 （若缺失或格式错误将以2018-11-13 19:40:19 为默认值）
     * @param To 截止时间 （若缺失或格式错误将以当前时间 为默认值）
     * @param interval 间隔 （为 "1s"  "5s"  "1m"  "1d"  "1w"  等）
     * @param searchName 该次查询名称
     * @param mutilines String[] 单个中包含  LineName field Indicator
     * @return
     */
    @RequestMapping(value = "/mutiDateMetricHistogram",method = RequestMethod.POST)
    public GeneralResult mutiDateMetricHistogram(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                                 @RequestParam(value = "index",required = false,defaultValue = "metricbeat-6.2.1-2018.11.*") String index,
                                                 @RequestParam(value = "datefield",required = false,defaultValue = "@timestamp") String datefield,
                                                 @RequestParam(value = "from",required = false,defaultValue = "") String From,
                                                 @RequestParam(value = "to",required = false,defaultValue = "") String To,
                                                 @RequestParam(value = "interval",required = false,defaultValue = "6h") String interval,
                                                 @RequestParam(value = "panelName",required = false,defaultValue = "test") String searchName,
                                                 @RequestParam(value = "mutilines",required = false,defaultValue = "") String mutilines,
                                                 @RequestParam(value = "filter",required = false,defaultValue = "") String FilterJsonString,
                                                 @RequestParam(value = "maxNum",required = false,defaultValue = "100") int maxNum){

        Gson gson = new Gson();

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id是否有权限操作该index

        // next
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date Fromdate;
        Date Todate;
        try {
            Fromdate = sdf.parse( From);
        } catch (ParseException e) {
            Fromdate = new Date(1542109219000L);
        }

        try {
            Todate = sdf.parse( To);
        } catch (ParseException e) {
            Todate = new Date();
        }
        Fromdate = getRationalFromDate(Fromdate,Todate,interval,maxNum);
        ArrayList<Line> arrayList = new ArrayList<>();
        for(Object object:gson.fromJson(mutilines,ArrayList.class)){
            System.out.println(object.toString());
            arrayList.add(gson.fromJson(object.toString(),Line.class));
        }


        result.setResultData(esService.mutiDateMetricHistogram(index,datefield,Fromdate,Todate,interval,searchName,arrayList,FilterJsonString));


        if(result.getResultData()!=null){
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }
    /**
     *  多线条的数值查询<每条线都额外存在一个过滤器>
     * @param index  数据索引（类似数据库）
     * @param datefield 横坐标（类型为时间）
     * @param From 起始时间 （若缺失或格式错误将以2018-11-13 19:40:19 为默认值）
     * @param To 截止时间 （若缺失或格式错误将以当前时间 为默认值）
     * @param interval 间隔 （为 "1s"  "5s"  "1m"  "1d"  "1w"  等）
     * @param searchName 该次查询名称
     * @param mutilines String[] 单个中包含  LineName field Indicator filter <here add a filter in mutilines[0]>
     * @return
     */
    @RequestMapping(value = "/superMutiDateMetricHistogram",method = RequestMethod.POST)
    public GeneralResult superMutiDateMetricHistogram(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                                     @RequestParam(value = "index",required = false,defaultValue = "metricbeat-6.2.1-2018.11.*") String index,
                                                     @RequestParam(value = "datefield",required = false,defaultValue = "@timestamp") String datefield,
                                                     @RequestParam(value = "from",required = false,defaultValue = "") String From,
                                                     @RequestParam(value = "to",required = false,defaultValue = "") String To,
                                                     @RequestParam(value = "interval",required = false,defaultValue = "6h") String interval,
                                                     @RequestParam(value = "panelName",required = false,defaultValue = "test") String searchName,
                                                     @RequestParam(value = "mutilines",required = false,defaultValue = "") String mutilines,
                                                     @RequestParam(value = "filter",required = false,defaultValue = "") String FilterJsonString,
                                                     @RequestParam(value = "maxNum",required = false,defaultValue = "100") int maxNum){

        Gson gson = new Gson();

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id是否有权限操作该index

        // next
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date Fromdate;
        Date Todate;
        try {
            Fromdate = sdf.parse( From);
        } catch (ParseException e) {
            Fromdate = new Date(1542109219000L);
        }

        try {
            Todate = sdf.parse( To);
        } catch (ParseException e) {
            Todate = new Date();
        }

        Fromdate = getRationalFromDate(Fromdate,Todate,interval,maxNum);



        ArrayList<SuperLine> arrayList = new ArrayList<>();
        for(Object object:gson.fromJson(mutilines,ArrayList.class)){
            System.out.println(object.toString());
            arrayList.add(gson.fromJson(object.toString(),SuperLine.class));
        }


        try {
            result.setResultData( esService.superMutiDateMetricHistogram(index,datefield,Fromdate,Todate,interval,searchName,arrayList,FilterJsonString));
            result.setResultStatus(true);
        }catch (Exception e){
            e.printStackTrace();
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }

    /**
     * 测试
     * @param id 用户id
     * @param index 索引
     * @param lines 日志条数
     * @return
     */
    @RequestMapping(value = "/avg",method = RequestMethod.GET)
    public GeneralResult avg(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                      @RequestParam(value = "index",required = false,defaultValue = "filebeat-6.3.1-2018.11.28") String index,
                                      @RequestParam(value = "lines",required = false,defaultValue = "20") int lines){

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id是否有权限操作该index

        // next
        System.out.println("get in");
//        String[] fields2 = {"userIP", "@timestamp","_type","statusCode","consulReturnTime","realurl"};
        result.setResultData(esService.avg(index,lines));


        if(result.getResultData()!=null){
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }

    public Date getRationalFromDate(Date from,Date to,String interval,int baseNum){
//        public static final DateHistogramInterval SECOND = new DateHistogramInterval("1s");
//        public static final DateHistogramInterval MINUTE = new DateHistogramInterval("1m");
//        public static final DateHistogramInterval HOUR = new DateHistogramInterval("1h");
//        public static final DateHistogramInterval DAY = new DateHistogramInterval("1d");
//        public static final DateHistogramInterval WEEK = new DateHistogramInterval("1w");
//        public static final DateHistogramInterval MONTH = new DateHistogramInterval("1M");
//        public static final DateHistogramInterval QUARTER = new DateHistogramInterval("1q");
//        public static final DateHistogramInterval YEAR = new DateHistogramInterval("1y");
        char c = interval.charAt(interval.length()-1);
        long num = Integer.parseInt(interval.substring(0,interval.length()-1))*1000;
        switch (c){
            case 's':
                num = num;
                break;
            case 'm':
                num = num*60;
                break;
            case 'h':
                num = num*3600;
                break;
            case 'd':
                num = num*3600*24;
                break;
            case 'w':
                num = num*504000;
                break;
            case 'M':
                num = num*2160000;
                break;
            case 'q':
                num = num*6480000;
                break;
            case 'y':
                num = num*25920000;
                break;
        }
        long temp = to.getTime()-num*baseNum;
        if (from.getTime()>temp){
            return from;
        }

        from = new Date(temp);
        System.out.println(" change fromdate to : "+from.toString());
        return from;
    }

}
