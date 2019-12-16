package com.cad.collectionservice.server;



import com.cad.collectionservice.domain.Agent;
import com.cad.collectionservice.util.SocketToAgent;
import com.cad.entity.domain.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class DataCollectionServer {
    @Autowired
    private SocketToAgent socketToAgent;
    public static ArrayList<Agent> agents = new ArrayList<Agent>();

    public void stopAgent(AgentInfo agentInfo) throws  Exception{
        Operate operate = new Operate();
        operate.setOperate("stop");
        operate.setTimestamp(new Date().getTime());
        socketToAgent.executeOperate(agentInfo,operate);
    }

    /**
     * 将收集配置发送到指定机器上并启动
     * @param agentInfo
     * @param beatConfig
     * @param beatJson
     * @throws Exception
     */
    public void startCollection(AgentInfo agentInfo, BeatConfig beatConfig, BeatJson beatJson) throws  Exception{

        Operate operate = new Operate();
        operate.setOperate(beatConfig.getType());
        operate.setParam(1);
        operate.setFile(JSONObject.fromObject(beatJson));
        operate.setTimestamp(new Date().getTime());
        socketToAgent.executeOperate(agentInfo,operate);
    }

    /**
     *  停止指定的收集器
     * @param agentInfo
     * @param beatConfig
     * @param collectionStatus
     * @throws Exception
     */
    public void stopCollection(AgentInfo agentInfo, BeatConfig beatConfig,CollectionStatus collectionStatus) throws  Exception{

        Operate operate = new Operate();
        operate.setOperate(beatConfig.getType()+"_stop");
        operate.setParam(Integer.parseInt(collectionStatus.getPid()));
        operate.setTimestamp(new Date().getTime());
        socketToAgent.executeOperate(agentInfo,operate);
    }

    /**
     *  获取对应路径的测试数据
     * @param agentInfo
     * @param logPath
     * @throws Exception
     */
    public String getTestData(AgentInfo agentInfo, String logPath) throws  Exception{

        Operate operate = new Operate();
        operate.setOperate("readfile_test");
        // 默认读取行数
        operate.setParam(8);
        operate.setTimestamp(new Date().getTime());
        operate.setOther(logPath);
        Operate re_op = socketToAgent.executeOperate(agentInfo,operate);
        if ("success".equals(re_op.getOperate())){
            return re_op.getOther();
        }else {
            return " get fail ";
        }
    }
}
