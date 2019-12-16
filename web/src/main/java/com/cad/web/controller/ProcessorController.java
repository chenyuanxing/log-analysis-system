package com.cad.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.cad.entity.domain.ProcessOperation;
import com.cad.entity.domain.template;
import com.cad.flinkservice.service.KafkaService;
import com.cad.flinkservice.service.ProcessService;
import com.cad.flinkservice.util.ConversionEntities;
import com.cad.flinkservice.util.DataConversion;
import com.cad.flinkservice.util.GrokSplitter;
import com.cad.flinkservice.util.TypeEnum;
import com.cad.web.GeneralResult;
import com.cad.web.service.ProcessRedisService;
import com.cad.web.util.ScheduledService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.internal.LinkedTreeMap;
import io.thekraken.grok.api.exception.GrokException;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(value = "/processor" )
public class ProcessorController {

    @Autowired
    private ProcessService processService;
    @Autowired
    private KafkaService kafkaService;
    @Autowired
    private ProcessRedisService processRedisService;

    /**
     * 设置 启动到ES的处理
     * 核心处理方法
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/startProcessToES",method = RequestMethod.POST)
    public GeneralResult getInstallCMD(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                       @RequestParam(value = "bootstrapServers",required = false,defaultValue = "10.108.210.194:9092") String bootstrapServers,
                                       @RequestParam(value = "pattern",required = false,defaultValue = "%{GREEDYDATA:cmessage}") String pattern,
                                       @RequestParam(value = "conversionEntities",required = false,defaultValue = "{\"conversionEntityList\":[{\"key\":\"mes\",\"type\":\"IP\",\"target\":\"haha\",\"tag\":\"yes\"},{\"key\":\"mes2\",\"type\":\"IP\",\"target\":\"haha2\",\"tag\":\"yes2\"}]}") String conversionEntitiesString,
                                       @RequestParam(value = "topicName",required = false,defaultValue = "sys_metric_data1120") String topicName,
                                       @RequestParam(value = "esHostName",required = false,defaultValue = "10.108.210.194") String esHostName,
                                       @RequestParam(value = "esPort",required = false,defaultValue = "9200") int esPort,
                                       @RequestParam(value = "indexName",required = false,defaultValue = "defaultindex") String indexName,
                                       @RequestParam(value = "type",required = false,defaultValue = "doc") String type){
        GeneralResult result = new GeneralResult();
        Gson gson = new Gson();
        Long time = new Date().getTime();
        String opID = id+"-"+time+"-"+new Random().nextInt(1000);
        ProcessOperation processOperation = new ProcessOperation(opID,id,bootstrapServers,pattern,conversionEntitiesString,topicName,esHostName,esPort,indexName,type,time);

        try {
            ConversionEntities conversionEntities = gson.fromJson(conversionEntitiesString,ConversionEntities.class);
            boolean status = processService.grokProcessToES(bootstrapServers,pattern,conversionEntities,topicName,esHostName,esPort,indexName,type,id);
            result.setResultStatus(status);
            processRedisService.addProcess(processOperation);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }
    @RequestMapping(value = "/getPageProcessRecords",method = RequestMethod.GET)
    public GeneralResult getPageProcessRecords(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                             @RequestParam(value = "startIndex",required = false,defaultValue = "0") Long start,
                                             @RequestParam(value = "endIndex",required = false,defaultValue = "20") Long end){
        GeneralResult result = new GeneralResult();
        try {
            List<ProcessOperation> processOperations= processRedisService.getProcessOperationS(start,end);
            result.setResultStatus(true);
            result.setResultData(processOperations);
        }catch (Exception e){
            e.printStackTrace();
            result.setResultStatus(false);
            result.setErrorMessage(e.toString());
        }
        return result;
    }

    /**
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/getAllTopics",method = RequestMethod.GET)
    public GeneralResult getAllTopics(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                       @RequestParam(value = "bootstrapServers",required = false,defaultValue = "10.108.210.194:9092") String bootstrapServers) {
        GeneralResult result = new GeneralResult();
        try {
            Set<String> allTopics = kafkaService.getAllTopics(bootstrapServers);
            result.setResultStatus(true);
            result.setResultData(allTopics);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 测试解析样例数据.
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/testParse",method = RequestMethod.POST)
    public GeneralResult testParse(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                    @RequestParam(value = "parseData") String parseData,
                                    @RequestParam(value = "pattern",required = false,defaultValue = "%{GREEDYDATA:cmessage}") String pattern) {
        GeneralResult result = new GeneralResult();
        result.setResultStatus(true);
        try {
            ArrayList<LinkedTreeMap<String,Object>> arrayList = new ArrayList<>();
            List<LinkedTreeMap<String,Object>> dataList = new com.google.gson.Gson().fromJson(String.valueOf(parseData),arrayList.getClass());

            GrokSplitter grokSplitter = new GrokSplitter(pattern);
            List<Map<String, Object>> resultMapList = new ArrayList<>();
            for (LinkedTreeMap<String,Object> data:dataList){
                String dataString =  new com.google.gson.Gson().toJson(data);
                Map<String, Object> map = grokSplitter.map(dataString);
                resultMapList.add(map);
            }

            result.setResultData(resultMapList);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }
    /**
     * 测试转换解析后的日志数据
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/testConversion",method = RequestMethod.POST)
    public GeneralResult testConversion(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                   @RequestParam(value = "parseData") String parseData,
                                   @RequestParam(value = "pattern",required = false,defaultValue = "%{GREEDYDATA:cmessage}") String pattern,
                                   @RequestParam(value = "conversionEntities",required = false,defaultValue = "{\"conversionEntityList\":[{\"key\":\"mes\",\"type\":\"IP\",\"target\":\"haha\",\"tag\":\"yes\"},{\"key\":\"mes2\",\"type\":\"IP\",\"target\":\"haha2\",\"tag\":\"yes2\"}]}") String conversionEntitiesString) {
        GeneralResult result = new GeneralResult();
        result.setResultStatus(true);
        Gson gson = new Gson();
        DataConversion dataConversion = new DataConversion();
        ConversionEntities conversionEntities =  gson.fromJson(conversionEntitiesString,ConversionEntities.class);

        try {
            ArrayList<LinkedTreeMap<String,Object>> arrayList = new ArrayList<>();
            List<LinkedTreeMap<String,Object>> dataList = new com.google.gson.Gson().fromJson(String.valueOf(parseData),arrayList.getClass());
//            List<String> dataList= JSONArray.parseArray(parseData,String.class);

            GrokSplitter grokSplitter = new GrokSplitter(pattern);
            List<Map<String, Object>> resultMapList = new ArrayList<>();
            for (LinkedTreeMap<String,Object> data:dataList){
                String dataString = new com.google.gson.Gson().toJson(data);

                    Map<String, Object> map = grokSplitter.map(dataString);
                    map = dataConversion.conversion(map,conversionEntities);


                resultMapList.add( map);
            }

            result.setResultData(resultMapList);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 根据源获取kafka的测试数据
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/testSource",method = RequestMethod.GET)
    public GeneralResult testSource(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                      @RequestParam(value = "bootstrapServers",required = false,defaultValue = "10.108.210.194:9092") String bootstrapServers,
                                      @RequestParam(value = "topicName",required = false,defaultValue = "sys_metric_data1120") String topicName,
                                      @RequestParam(value = "lines",required = false,defaultValue = "20") String lines) {
        GeneralResult result = new GeneralResult();
        ArrayList<String> topics = new ArrayList();
        topics.add(topicName);
        try {
            List<String> testData = kafkaService.getTestData(bootstrapServers,topics,lines);
            result.setResultStatus(true);
            result.setResultData(testData);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 制造一千条测试数据到kafka
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/produceTestData",method = RequestMethod.POST)
    public GeneralResult produceTestData(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                      @RequestParam(value = "bootstrapServers",required = false,defaultValue = "10.108.210.194:9092") String bootstrapServers,
                                      @RequestParam(value = "topicName",required = false,defaultValue = "normTopic") String topicName) {
        GeneralResult result = new GeneralResult();
        try {
            kafkaService.produceTestData(bootstrapServers,topicName);
            result.setResultStatus(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }
}
