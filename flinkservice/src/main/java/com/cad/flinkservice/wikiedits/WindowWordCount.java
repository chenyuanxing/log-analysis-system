package com.cad.flinkservice.wikiedits;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.util.Properties;

// 在windows  下 执行 nc -l -p 9999  后，再执行此方法
public class WindowWordCount {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

//        Properties properties = new Properties();
//        properties.setProperty("bootstrap.servers", "10.108.210.194:9092");
// only required for Kafka 0.8
//        properties.setProperty("zookeeper.connect", "10.108.210.194:2181");
//        properties.setProperty("group.id", "test2");


        DataStream<Tuple2<String, Integer>> dataStream = env
                .socketTextStream("localhost", 9999)
//                .addSource(new FlinkKafkaConsumer<>("metric_topic_no_hosts", new SimpleStringSchema(), properties))
//                .readTextFile("C:/Users/chen/IdeaProjects/log-analysis-system/flinkservice/src/main/java/com/cad/flinkservice/wikiedits/test.txt")
                .flatMap(new Splitter())
                // 0表示Tuple2<String, Integer> 中的第一个元素，即分割后的单词
                .keyBy(0)
                .timeWindow(Time.seconds(5))
                // 同理，1表示Tuple2<String, Integer> 中的第二个元素，即出现次数
                .sum(1)
                ;


        dataStream.print().setParallelism(1);

        env.execute("My Socket Window WordCount");
    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {

            for (String word: sentence.split("\n")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }

}