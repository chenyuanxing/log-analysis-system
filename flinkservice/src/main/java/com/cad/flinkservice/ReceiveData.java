package com.cad.flinkservice;

import com.cad.flinkservice.wikiedits.MyWindowWordCount;
import io.thekraken.grok.api.Grok;
import io.thekraken.grok.api.Match;
import io.thekraken.grok.api.exception.GrokException;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch.util.RetryRejectedExecutionFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

// Sucess
public class ReceiveData {

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "10.108.210.194:9092");
        props.setProperty("group.id", "flink-group");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        FlinkKafkaConsumer<String> flinkKafkaConsumer = new FlinkKafkaConsumer<String>("sys_metric_data1120",new SimpleStringSchema(),props);
        DataStream<String> dataStream = env
                .addSource(flinkKafkaConsumer)
                .map(new Splitter())
                ;

//        dataStream.print().setParallelism(1);

//      toKafka
//        FlinkKafkaProducer myProducer = new FlinkKafkaProducer<String>("10.108.210.194:9092","processed_sys_metric_data1120",new SimpleStringSchema());
//        myProducer.setWriteTimestampToKafka(true);
//        dataStream.addSink(myProducer);

        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("127.0.0.1", 9200, "http"));
        httpHosts.add(new HttpHost("10.2.3.1", 9200, "http"));

// use a ElasticsearchSink.Builder to create an ElasticsearchSink
        ElasticsearchSink.Builder<String> esSinkBuilder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<String>() {
                    public IndexRequest createIndexRequest(String element) {
                        Map<String, String> json = new HashMap<>();
                        json.put("data", element);

                        return Requests.indexRequest()
                                .index("my-index")
                                .type("my-type")
                                .source(json);
                    }

                    @Override
                    public void process(String element, RuntimeContext ctx, RequestIndexer indexer) {
                        indexer.add(createIndexRequest(element));
                    }
                }
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


        env.execute("Log Process");
    }


    public static class Splitter implements MapFunction<String,String> {

        @Override
        public String map(String sentence) throws Exception {

            System.out.println("--  "+sentence);
            return sentence;

        }
    }
}
