package com.cad.web.util;

import com.cad.entity.domain.CollectionStatus;
import com.cad.entity.domain.template;
import com.cad.web.service.AgentRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class ScheduledService {

    //分别为key和创建时间
    public static Map<String, template> keyMap = new HashMap();

    // 记录所有10秒钟内发送过心跳的节点的uuid
//    public static Set<String> set = new HashSet();

    // 记录所有10秒钟内发送过心跳的节点的状态
    public static Map<String, ArrayList<CollectionStatus>> alivemap = new HashMap<>();



    @Autowired
    private AgentRedisService agentRedisService;

    @Value("${node.type}")
    private String node;

    // 1分钟执行一次，让产生的每个uuid只能存活一小时
    @Scheduled(fixedRate = 60000)
    public void scheduled1() {
//        System.out.println("=====>>>>>"+System.currentTimeMillis());
        Set<String> removeKeySet = new HashSet<>();
        for (String key : keyMap.keySet()){
            if(keyMap.get(key).getTime()+3600000<new Date().getTime()){
                removeKeySet.add(key);
            }
        }
        for (String key:removeKeySet){
            keyMap.remove(key);
        }
    }
//
//    // 10 秒钟执行一次 ,同步状态
//    @Scheduled(fixedRate = 10000)
//    public void scheduled2() {
//        if(node.equals("master")){
//            Set<String> set1 = set;
//            set = new HashSet();
//            Map<String,String> map =agentRedisService.getAllStatus();
//            Map<String,String> mapOn = new HashMap<>();
//            for (String uuid : set1){
//                if(map.containsKey(uuid)){
//                    if(map.get(uuid).equals("on")){
//                        map.remove(uuid);
//                    }else {
//                        mapOn.put(uuid,"on");
//                    }
//                }else {
//                    System.out.println(uuid + " is not contained in the status map ");
//                }
//            }
//            for (String uuid: map.keySet()){
//                agentRedisService.updateStatus(uuid,"off");
//            }
//            for (String uuid: mapOn.keySet()){
//                agentRedisService.updateStatus(uuid,"on");
//            }
//        }else {
//            set = new HashSet();
//        }
//    }
//
    // 10 秒钟执行一次 ,同步状态
    @Scheduled(fixedRate = 10000)
    public void scheduled3() {
        if(node.equals("master")){
            Map<String, ArrayList<CollectionStatus>> map1 = alivemap;
            alivemap = new HashMap<>();
            Set<String> set1 = map1.keySet();
            Map<String,String> map =agentRedisService.getAllStatus();
            Map<String,String> mapOn = new HashMap<>();
            for (String uuid : set1){
                ArrayList<CollectionStatus> collectionStatusArray = map1.get(uuid);
                if(map.containsKey(uuid)){

                    // 若redis中数据显示 agent状态为on
                    if(map.get(uuid).equals("on")){
                        // 查看collection状态是否一样.若不一样则修改
                        ArrayList<CollectionStatus> arrayList = agentRedisService.getCollectionStatus(uuid);

                        if (arrayList==null ){
                            if (collectionStatusArray != null){
                                agentRedisService.putCollectionStatus(uuid,collectionStatusArray);
                            }
                        } else if (!arrayList.equals(collectionStatusArray)){
                            agentRedisService.putCollectionStatus(uuid,collectionStatusArray);
                        }
                        map.remove(uuid);
                    }else {
                        mapOn.put(uuid,"on");
                    }

                }else {
                    System.out.println(uuid + " is not contained in the status map ");
                }
            }
            for (String uuid: map.keySet()){
                // 在改变所有agent状态为off之前，先将collection状态都更改为off
                ArrayList<CollectionStatus> arrayList = agentRedisService.getCollectionStatus(uuid);
                if (arrayList ==null){}else {
                    for (int i = 0;i<arrayList.size();i++){
                        arrayList.get(i).setStatus("off");
                    }
                    agentRedisService.putCollectionStatus(uuid,arrayList);
                }
                agentRedisService.updateStatus(uuid,"off");
            }
            for (String uuid: mapOn.keySet()){
                agentRedisService.putCollectionStatus(uuid,map1.get(uuid));
                agentRedisService.updateStatus(uuid,"on");

            }
        }else {
            // 如果不是master节点，直接将所有接收到的数据删除
            alivemap = new HashMap<>();
            System.out.println("Is not master ");
        }
    }

}
