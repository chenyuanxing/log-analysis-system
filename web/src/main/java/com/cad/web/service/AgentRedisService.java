package com.cad.web.service;

import com.cad.entity.domain.AgentInfo;
import com.cad.entity.domain.CollectionStatus;
import com.cad.web.dao.RedisDBHelperImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class AgentRedisService {
    @Resource(name = "RedisDBHelper")
    private RedisDBHelperImpl redisDBHelper;

    @Value("${manage.agent.forwardkey}")
    private String forwardkey;

    private String forward = "manage2:agent";

    private String collectStatusforward=forward+"_collect";

    public void addAgent(AgentInfo agentInfo){
        redisDBHelper.hashPut(forwardkey+agentInfo.getUserName(),agentInfo.getUuid(),agentInfo);
        redisDBHelper.hashPut(forward,agentInfo.getUuid(),"on");
    }
    public void delAgent(AgentInfo agentInfo)throws Exception{
        redisDBHelper.hashRemove(forwardkey+agentInfo.getUserName(),agentInfo.getUuid());

        redisDBHelper.hashRemove(forward,agentInfo.getUuid());
    }


    /**
     * 根据user查询该user下的所有machine
     * @param user user名称
     * @return
     */
    public ArrayList<AgentInfo> getAgents(String user){
        Map map = redisDBHelper.hashFindAll(forwardkey+user);
        Map statusMap = redisDBHelper.hashFindAll(forward);
        ArrayList<AgentInfo> agents = new ArrayList();
        for (Object object:map.values()){
            AgentInfo agentInfo = (AgentInfo) object;
            if(statusMap.containsKey(agentInfo.getUuid())){
                agentInfo.setStatus((String) statusMap.get(agentInfo.getUuid()));

            }else {
                agentInfo.setStatus("off");
            }
            agents.add(agentInfo);
        }
        return agents;
    }

    /**
     * 根据user和uuid查询machine
     * 实际上执行了两次查询，包括基本信息和状态信息
     * @param user user名称
     *  @param uuid
     * @return
     */
    public AgentInfo getAgent(String user,String uuid) throws Exception{
        AgentInfo agentInfo = (AgentInfo) redisDBHelper.hashGet(forwardkey+user,uuid);
        String status = (String) redisDBHelper.hashGet(forward,uuid);
        agentInfo.setStatus(status);
        return agentInfo;
    }

    /**
     * 修改tags
     * @return
     */
    public void updateTags(String user,String uuid,ArrayList<String> tags){
        AgentInfo agentinfo = (AgentInfo) redisDBHelper.hashGet(forwardkey+user,uuid);
        agentinfo.setTags(tags);
        addAgent(agentinfo);

    }
    /**
     * 修改agent状态为status
     * @return
     */
    public void updateStatus(String uuid,String status){
        redisDBHelper.hashPut(forward,uuid,status);
    }
    /**
     * 获取所有状态
     * @return
     */
    public Map<String,String> getAllStatus(){
        Map<String,String> map  = redisDBHelper.hashFindAll(forward);
        return map;
    }

    public ArrayList<CollectionStatus> getCollectionStatus(String uuid){
        ArrayList<CollectionStatus> arrayList = (ArrayList<CollectionStatus>)redisDBHelper.hashGet(collectStatusforward,uuid);
        return arrayList;
    }
    public void putCollectionStatus(String uuid,ArrayList<CollectionStatus> arrayList){
        redisDBHelper.hashPut(collectStatusforward,uuid,arrayList);
    }



}


