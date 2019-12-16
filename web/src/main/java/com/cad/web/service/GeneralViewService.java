package com.cad.web.service;

import com.alibaba.fastjson.JSONObject;
import com.cad.elasticsearchservice.Service.ESService;
import com.cad.entity.domain.BeatConfig;
import com.cad.entity.domain.CollectionStatus;
import com.cad.flinkservice.service.KafkaService;
import org.apache.flink.shaded.zookeeper.org.apache.zookeeper.KeeperException;
import org.apache.flink.shaded.zookeeper.org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 概览对应的能力封装.
 */
@Service
public class GeneralViewService {
    @Autowired
    private AgentRedisService agentRedisService;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private ESService esService;
    @Autowired
    private CollectionRedisService collectionRedisService;

    @Value("${kafka.address}")
    private String kafkaAddress;
    @Value("${kafka.port}")
    private String kafkaPort;
    @Value("${zookeeper.address}")
    private String zookeeperAddress;
    @Value("${zookeeper.port}")
    private String zookeeperPort;

    @Value("${elasticsearch.ip}")
    String[] ipAddress ;

    public WatcherView getAgentView(){
        WatcherView watcherView = new WatcherView();
        Map<String,String> map=  agentRedisService.getAllStatus();
        List<String> agentIds = new ArrayList<>();
        for (Map.Entry entry:map.entrySet()){
            if("on".equals(entry.getValue().toString())){
                agentIds.add((String) entry.getKey());
            }
        }
        watcherView.setOnAgentNum(agentIds.size());
        watcherView.setOffAgentNum(map.size()-agentIds.size());
        int hasTaskNum = 0;
        for (String agentId:agentIds){
            ArrayList<CollectionStatus> collectionStatuses =  agentRedisService.getCollectionStatus(agentId);
            for (CollectionStatus collectionStatus:collectionStatuses){
                if(!"off".equals(collectionStatus.getStatus())){
                    hasTaskNum++;
                    break;
                }
            }

        }
        watcherView.setHasTaskNum(hasTaskNum);
        watcherView.setNotHasTaskNum(agentIds.size()-hasTaskNum);
        return watcherView;
    }
    // userId smj
    public BeatView getBeatView(String userId){
        BeatView beatView = new BeatView();
        Map<String,String> map=  agentRedisService.getAllStatus();
        Set<String> agentIds = map.keySet();

        int beatNum = 0;
        int onBeatNum = 0;
        int beatClassNum = 0;
        try {
            beatClassNum = collectionRedisService.getAllBeatConfig(userId).size();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<String> beatClassSet = new HashSet<>();
        for (String agentId:agentIds){
            ArrayList<CollectionStatus> collectionStatuses =  agentRedisService.getCollectionStatus(agentId);
            if (collectionStatuses!=null){
                if (map.get(agentId).equals("off")){
                    int temp = collectionStatuses.size();
                    beatNum +=temp;
                }else{
                    for (CollectionStatus collectionStatus:collectionStatuses){
                        beatNum++;
                        if("off".equals(collectionStatus.getStatus())){

                        }else{
                            beatClassSet.add(collectionStatus.getConfigname());
                            onBeatNum++;
                        }
                    }
                }
            }

        }
        int beatClassRunNum = beatClassSet.size();
        beatView.setBeatClassNum(beatClassNum);
        beatView.setBeatClassRunNum(beatClassRunNum);
        beatView.setBeatNum(beatNum);
        beatView.setOnBeatNum(onBeatNum);
        return beatView;
    }

    public KafkaView getKafkaView() throws IOException, KeeperException, InterruptedException {
        KafkaView kafkaView = new KafkaView();
        kafkaView.setTopicNum(kafkaService.getAllTopics(kafkaAddress+":"+kafkaPort).size());
        ZooKeeper zk = new ZooKeeper(zookeeperAddress+":"+zookeeperPort, 10000, null);
        List<String> ids = zk.getChildren("/brokers/ids", false);
        kafkaView.setBrokerNum(ids.size());
        for (String id : ids) {
            String brokerInfo = new String(zk.getData("/brokers/ids/" + id, false, null));
            System.out.println(id + ": " + brokerInfo);
        }
        kafkaView.setZookeeperAddress(zookeeperAddress);
        kafkaView.setZookeeperVersion("0.9.0.1");
        return kafkaView;
    }


    public ESView getESView(){
        ESView esView = new ESView();
        esView.setIndexNum(esService.getIndex().size());
        JSONObject jsonObject = (JSONObject) esService.getNodeDetails(ipAddress[0]);
        if (jsonObject!=null){
            LinkedHashMap map = (LinkedHashMap) jsonObject.get("_shards");
            if (map !=null){
                esView.setSuccessful((Integer) map.get("successful"));
                esView.setTotal((Integer) map.get("total"));
                esView.setFailed((Integer) map.get("failed"));
            }else {
                System.out.println("map null2  "+jsonObject);
            }
        }else {
            System.out.println("jsonObject null  "+jsonObject);
        }
        return esView;
    }

    public class KafkaView{
        // topic数量
        private int topicNum;
        // broker数量
        private int brokerNum;
        // zookeeper 地址
        private String zookeeperAddress;
        // zookeeper 版本
        private String zookeeperVersion;

        public String getZookeeperAddress() {
            return zookeeperAddress;
        }

        public void setZookeeperAddress(String zookeeperAddress) {
            this.zookeeperAddress = zookeeperAddress;
        }

        public String getZookeeperVersion() {
            return zookeeperVersion;
        }

        public void setZookeeperVersion(String zookeeperVersion) {
            this.zookeeperVersion = zookeeperVersion;
        }

        public int getTopicNum() {
            return topicNum;
        }

        public void setTopicNum(int topicNum) {
            this.topicNum = topicNum;
        }

        public int getBrokerNum() {
            return brokerNum;
        }

        public void setBrokerNum(int brokerNum) {
            this.brokerNum = brokerNum;
        }
    }

    public class ESView{
        // 索引个数
        private int indexNum;
        // 分片个数
        private int total;
        // 成功分片个数
        private int successful;
        // 失败分片个数
        private int failed;

        public int getIndexNum() {
            return indexNum;
        }

        public void setIndexNum(int indexNum) {
            this.indexNum = indexNum;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSuccessful() {
            return successful;
        }

        public void setSuccessful(int successful) {
            this.successful = successful;
        }

        public int getFailed() {
            return failed;
        }

        public void setFailed(int failed) {
            this.failed = failed;
        }
    }

    public class BeatView{
        // 正在运行的beat数量
        private int onBeatNum;
        // 数据库中所有agent下的beat 数量
        private int beatNum;
        // 所有收集器类型数量
        private int beatClassNum;
        // 所有运行着的类型数量
        private int beatClassRunNum;

        public int getOnBeatNum() {
            return onBeatNum;
        }

        public void setOnBeatNum(int onBeatNum) {
            this.onBeatNum = onBeatNum;
        }

        public int getBeatNum() {
            return beatNum;
        }

        public void setBeatNum(int beatNum) {
            this.beatNum = beatNum;
        }

        public int getBeatClassNum() {
            return beatClassNum;
        }

        public void setBeatClassNum(int beatClassNum) {
            this.beatClassNum = beatClassNum;
        }

        public int getBeatClassRunNum() {
            return beatClassRunNum;
        }

        public void setBeatClassRunNum(int beatClassRunNum) {
            this.beatClassRunNum = beatClassRunNum;
        }
    }

    public class WatcherView{
        private int onAgentNum;
        private int offAgentNum;
        private int hasTaskNum;
        private int notHasTaskNum;

        public int getOnAgentNum() {
            return onAgentNum;
        }

        public void setOnAgentNum(int onAgentNum) {
            this.onAgentNum = onAgentNum;
        }

        public int getOffAgentNum() {
            return offAgentNum;
        }

        public void setOffAgentNum(int offAgentNum) {
            this.offAgentNum = offAgentNum;
        }

        public int getHasTaskNum() {
            return hasTaskNum;
        }

        public void setHasTaskNum(int hasTaskNum) {
            this.hasTaskNum = hasTaskNum;
        }

        public int getNotHasTaskNum() {
            return notHasTaskNum;
        }

        public void setNotHasTaskNum(int notHasTaskNum) {
            this.notHasTaskNum = notHasTaskNum;
        }
    }


}
