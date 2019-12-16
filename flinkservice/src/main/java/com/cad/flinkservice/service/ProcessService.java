package com.cad.flinkservice.service;

import com.cad.flinkservice.ReceiveData;
import com.cad.flinkservice.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.thekraken.grok.api.exception.GrokException;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch.util.RetryRejectedExecutionFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.kafka.common.protocol.types.Field;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProcessService {
    @Value("${flink.address}")
    private String flinkAddress;
    @Value("${flink.port}")
    private int flinkPort;
    @Value("${Linux.jars.paths}")
    private String LjarsPaths;
    @Value("${Window.jars.paths}")
    private String WjarsPaths;
    @Value("${flink.local}")
    private boolean useLocalFlink;
//    Properties sourceKafkaProps = new Properties();
//    List<HttpHost> httpHosts = new ArrayList<>();

//    public ProcessService(Properties props,List<HttpHost> httpHosts){
//        this.sourceKafkaProps = props;
//        this.httpHosts = httpHosts;
//    }
//    public static void main(String [] args){
//        Properties props = new Properties();
//        props.setProperty("bootstrap.servers", "10.108.210.194:9092");
//        props.setProperty("group.id", "flink-test");
//
//        List<HttpHost> httpHosts = new ArrayList<>();
//
//        new ProcessService(props,httpHosts).test();
//    }
//    public void test(){
//        String[] jars = {"flinkservice/target/flinkservice-0.0.1-SNAPSHOT.jar",
//                "flinkservice/target/flinkservice-0.0.1-SNAPSHOT-kafka.jar"};
//        StreamExecutionEnvironment env =
//                StreamExecutionEnvironment.createRemoteEnvironment("10.108.211.22",8081,1,jars);
//
//        DataStream<String> dataStream = env.addSource(new FlinkKafkaConsumer("sys_metric_data1120",
//                new SimpleStringSchema(), sourceKafkaProps));
//        dataStream.map(new MapFunction<String, String>() {
//            private static final long serialVersionUID = -6867736771747690202L;
//            @Override
//            public String map(String value) throws Exception
//            {
//                System.out.println(value);
//                return value;
//            }
//        }).print();
//        try {
//            env.execute();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



    public boolean grokProcessToES(String bootstrapServers,String pattern,ConversionEntities conversionEntities,String topicName,String hostName,int port,String indexName,String type,String userName) throws GrokException {

        Properties sourceKafkaProps = new Properties();
        List<HttpHost> httpHosts = new ArrayList<>();
        sourceKafkaProps.setProperty("bootstrap.servers", bootstrapServers);
        sourceKafkaProps.setProperty("group.id", "flink-group"+userName);
        httpHosts.add(new HttpHost(hostName,port,"http"));
        StreamExecutionEnvironment env = getENV();

        FlinkKafkaConsumer<String> flinkKafkaConsumer = new FlinkKafkaConsumer<String>(topicName,new SimpleStringSchema(),sourceKafkaProps);
        SingleOutputStreamOperator<Map<String, Object>> dataStream = env
                .addSource(flinkKafkaConsumer)
                .map(new GrokSplitter(pattern))
                .map(new ConversionSplitter(conversionEntities))
                ;

        // use a ElasticsearchSink.Builder to create an ElasticsearchSink
        // 使用自己写的 ElasticsearchSinkFunctionWithConf 可以避免序列化的问题.
        ElasticsearchSink.Builder<Map<String, Object>> esSinkBuilder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunctionWithConf<>(indexName,type)
        );
        // configuration for the bulk requests; this instructs the sink to emit after every element, otherwise they would be buffered
        esSinkBuilder.setBulkFlushMaxActions(1);

        // provide a RestClientFactory for custom configuration on the internally created REST client
        esSinkBuilder.setRestClientFactory(
                restClientBuilder -> {
                    restClientBuilder.setDefaultHeaders(new BasicHeader[]{new BasicHeader("Content-Type","application/json")});
                    restClientBuilder.setMaxRetryTimeoutMillis(90000);
                }
        );

        esSinkBuilder.setFailureHandler(new RetryRejectedExecutionFailureHandler());

        // finally, build and add the sink to the job's pipeline
        dataStream.addSink(esSinkBuilder.build());


        try {

            ExecuteThread executeThread =  new ExecuteThread(env,"ProcessToES "+userName+" "+new Date().getTime());
            executeThread.start();
            //todo 睡眠半分钟 然后停止启动的client 等待结果线程?
//            Thread.sleep(30000);
//            executeThread.stop();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
    /**todo 测试grok的接口. 如何停止任务运行?
     * @param pattern
     * @return
     */
    public String grokProcessTest(String bootstrapServers,String pattern,String topicName,String hostName,int port,String indexName,String type,String userName) throws GrokException {
        Properties sourceKafkaProps = new Properties();
        List<HttpHost> httpHosts = new ArrayList<>();
        sourceKafkaProps.setProperty("bootstrap.servers", bootstrapServers);
        httpHosts.add(new HttpHost(hostName,port,"http"));

        sourceKafkaProps.setProperty("group.id", "flink-group-test"+new Random().nextInt(100));
        StreamExecutionEnvironment env = getENV();
;
        try {
            env.execute("flink-grok-test");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public StreamExecutionEnvironment getENV(){

        if (!useLocalFlink){
            String osName = System.getProperties().getProperty("os.name");
            String jarsPaths;
            if(osName.equals("Linux")) {
                jarsPaths = LjarsPaths;
            }else{
                jarsPaths = WjarsPaths;
            }
            String[] jars = jarsPaths.split(",");
            return StreamExecutionEnvironment.createRemoteEnvironment(flinkAddress,flinkPort,1,jars);
        }else {
            return StreamExecutionEnvironment.createLocalEnvironment(1);
        }
    }

}

