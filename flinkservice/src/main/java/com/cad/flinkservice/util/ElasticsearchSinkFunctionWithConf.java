package com.cad.flinkservice.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于避免ElasticsearchSinkFunction 的序列化问题.
 * @param <T>
 */
public class ElasticsearchSinkFunctionWithConf<T> implements ElasticsearchSinkFunction<T>, Serializable {
    private String indexName;
    private String typeName;

    public ElasticsearchSinkFunctionWithConf(String indexName, String typeName) {
        this.indexName = indexName;
        this.typeName = typeName;
    }

    @Override
    public void process(T element, RuntimeContext ctx, RequestIndexer indexer) {
        indexer.add(createIndexRequest(element));
    }

    /**
     * @param element 传入为 Map<String,Object> 类型
     * @return
     */
    public IndexRequest createIndexRequest(T element) {
//        Map<String,Object> map = new HashMap<>();
//        String e = String.valueOf(element);
//        map = new com.google.gson.Gson().fromJson(String.valueOf(element),map.getClass());
        Map<String,Object> map = (Map<String,Object>)element;
        return Requests.indexRequest()
                .index(indexName)
                .type(typeName)
                .source(map);

    }
}