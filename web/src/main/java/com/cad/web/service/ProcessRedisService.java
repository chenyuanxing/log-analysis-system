package com.cad.web.service;

import com.cad.entity.domain.ProcessOperation;
import com.cad.web.dao.RedisDBHelperImpl;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProcessRedisService {
    @Resource(name = "RedisDBHelper")
    private RedisDBHelperImpl redisDBHelper;

    @Value("${manage.process.config.key}")
    private String processKey;
    @Value("${manage.process.config.key.list}")
    private String processListKey;

    private Gson gson = new Gson();

    public void addProcess(ProcessOperation processOperation){

        String processOpString = gson.toJson(processOperation);
        redisDBHelper.hashPut(processKey,processOperation.getOperateId(),processOpString);
        redisDBHelper.listPush(processListKey,processOperation.getOperateId());
    }
    public List<ProcessOperation> getAllProcessOperation(){
        Map map = redisDBHelper.hashFindAll(processKey);
        List<ProcessOperation> list = new ArrayList<>();
        for (Object object:map.values()){
            ProcessOperation processOperation  = (ProcessOperation) object;
            list.add(processOperation);
        }

        return list;
    }

    public List<ProcessOperation> getProcessOperationS(Long start,Long end){

        List<String> list= redisDBHelper.listRange(processListKey,start,end);
        List<String> processOpList= redisDBHelper.hashFindMutiValue(processKey,list);
        ArrayList<ProcessOperation> processOperationList = new ArrayList<>();
        for (String op:processOpList){
            ProcessOperation processOperation  = gson.fromJson(op,ProcessOperation.class);
            processOperationList.add(processOperation);
        }
        return processOperationList;
    }


}
