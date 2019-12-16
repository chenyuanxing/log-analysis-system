package com.cad.flinkservice.controller;

import com.cad.flinkservice.util.IpToAddress;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

/**
 * 测试使用
 */
@RestController
@RequestMapping("flink")

public class FlinkController   {
    @RequestMapping(value="/test",method= RequestMethod.GET)
    public void test() throws Exception {
        String[] jars = {"flinkservice/target/flinkservice-0.0.1-SNAPSHOT.jar",
                "flinkservice/target/flinkservice-0.0.1-SNAPSHOT-kafka.jar"};
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createRemoteEnvironment("10.108.211.22",8081,1,jars);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "10.108.210.194:9092");
        properties.setProperty("group.id", "flink-test");
        DataStream<String> stream = env.addSource(new FlinkKafkaConsumer("sys_metric_data1120",
                new SimpleStringSchema(), properties));
        stream.map(new MapFunction<String, String>() {
            private static final long serialVersionUID = -6867736771747690202L;
            @Override
            public String map(String value) throws Exception
            {
                System.out.println(value);
                return value;
            }
        }).print();
        env.execute();
    }
    @RequestMapping(value="/testIPToAddress",method= RequestMethod.GET)
    public String testIPToAddress(@RequestParam("ip") String ip){
        String res = "";
        try {
            res = IpToAddress.getCity(ip);
            res +="  :  "+ IpToAddress.getCountry(ip);
            res +="  :  "+ IpToAddress.getProvince(ip);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
