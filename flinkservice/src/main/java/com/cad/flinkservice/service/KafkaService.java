package com.cad.flinkservice.service;

import com.cad.flinkservice.util.RandomIp;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

@Service
public class KafkaService {

    public Set<String> getAllTopics(String bootstrapServers){
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "test-getTopics-consumer-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(props);
        Map<String, List<PartitionInfo>> map  =  kafkaConsumer.listTopics();
        System.out.println(map);
        kafkaConsumer.close();
        return map.keySet();
    }

    public List<String> getTestData(String bootstrapServers,ArrayList<String> topics,String lines){
        List<String> arrayList = new ArrayList<>();
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "test-pull-consumer-group");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("max.poll.records", lines);
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        KafkaConsumer kafkaConsumer = new KafkaConsumer(props);
        kafkaConsumer.subscribe(topics);
        ConsumerRecords<String, String> msgList = kafkaConsumer.poll(Duration.ofSeconds(3));
        for (ConsumerRecord consumerRecord:msgList){
            arrayList.add((String) consumerRecord.value());
        }
        kafkaConsumer.close();
        return arrayList;
    }

    /**
     * 生产一批测试数据
     * @param bootstrapServers
     * @param topic
     */
    public void produceTestData(String bootstrapServers,String topic){
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        KafkaProducer<String,String> kafkaProducer = new KafkaProducer<String, String>(props);
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        for (int i = 0;i<1000;i++){
            Date date = new Date();// 获取当前时间
            String messageStr="info: log "+ RandomIp.getRandomIp()+" name chen"+" logp "+i+" time "+sdf.format(date)+" other";
            kafkaProducer.send(new ProducerRecord<String, String>(topic, "Message", messageStr));
        }
        kafkaProducer.close();
    }

}
