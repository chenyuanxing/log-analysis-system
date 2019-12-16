package com.cad.elasticsearchservice.Dao;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service("ElasticsearchHelper")
public class ElasticsearchHelper {
    public RestHighLevelClient client ;
    public ElasticsearchHelper(){
//        Settings settings = Settings.builder()
//                .put("cluster.name", "myES_Cluster").build();
//        TransportClient client = new PreBuiltTransportClient(settings);
        //Add transport addresses and do something with the client...

        // on startup

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
//                        new HttpHost("112.74.173.198", 9200, "http"),
                        new HttpHost("112.74.173.198", 9200, "http")));

//        TransportClient client = null;
//        try {
//
//            client = new PreBuiltTransportClient(settings)
//    //                .addTransportAddress(new TransportAddress(InetAddress.getByName("host1"), 9300))
//                    .addTransportAddress(new TransportAddress(InetAddress.getByName("112.74.173.198"), 9300));
//        } catch (Exception e) {
//            System.out.println("cyx ---------------- Exception");
//
//            e.printStackTrace();
//        }
        this.client = client;
    }

    public void closeClient(){
        // on shutdown
        try {
            this.client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test(){

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "kimchy");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest indexRequest = new IndexRequest("posts", "doc", "1")
                .source(jsonMap);

        try {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

            String index = indexResponse.getIndex();
            String type = indexResponse.getType();
            String id = indexResponse.getId();
            long version = indexResponse.getVersion();
            System.out.println(index+"  "+type+"  "+id+"  "+version+"  ");


//            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
//
//            } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
//
//            }
//            ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
//            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
//
//            }
//            if (shardInfo.getFailed() > 0) {
//                for (ReplicationResponse.ShardInfo.Failure failure :
//                        shardInfo.getFailures()) {
//                    String reason = failure.reason();
//                }
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}
