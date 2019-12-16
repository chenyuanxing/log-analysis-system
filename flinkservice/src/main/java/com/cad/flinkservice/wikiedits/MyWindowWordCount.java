package com.cad.flinkservice.wikiedits;

import io.thekraken.grok.api.Grok;
import io.thekraken.grok.api.Match;
import io.thekraken.grok.api.exception.GrokException;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.apache.kafka.common.protocol.types.Field;

// 在windows  下 执行 nc -l -p 9999  后，再执行此方法
public class MyWindowWordCount {

    public static void main(String[] args) throws Exception {
        String pattern = "%{GREEDYDATA:loglevel} %{GREEDYDATA:year}-%{GREEDYDATA:month}-%{GREEDYDATA:day} %{GREEDYDATA:hour}:%{GREEDYDATA:minute}:%{GREEDYDATA:second} (%{GREEDYDATA:data}) - %{GREEDYDATA:message} %{GREEDYDATA:Erreur}";

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> dataStream = env
                .socketTextStream("localhost", 9999)
                .map(new Splitter(pattern))
                ;

        dataStream.print().setParallelism(1);


        env.execute("My Socket Window WordCount");
    }

    public static class Splitter implements MapFunction<String,String> {
        Grok grok = new Grok();
        public Splitter(){
            throw new IllegalArgumentException();
        }

        public Splitter(String pattern) throws GrokException {
            grok.addPattern("GREEDYDATA", ".*");
            grok.compile(pattern);
        }
        @Override
        public String map(String sentence) throws Exception {

            Match gm = grok.match(sentence);
            gm.captures();
            String res = gm.toJson();
            return res;

        }
    }

}