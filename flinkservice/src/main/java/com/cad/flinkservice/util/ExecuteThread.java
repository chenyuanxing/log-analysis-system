package com.cad.flinkservice.util;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * todo env.execute 此方法不会停止 后期修改
 */
public class ExecuteThread extends Thread {

    private StreamExecutionEnvironment env;
    private String name;
    public ExecuteThread(StreamExecutionEnvironment env,String name){
        this.env = env;
        this.name = name;
    }
    @Override
    public void run() {
        try {
            JobExecutionResult jobExecutionResult =  env.execute(name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
