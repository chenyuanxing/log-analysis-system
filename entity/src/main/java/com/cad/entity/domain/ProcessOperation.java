package com.cad.entity.domain;

public class ProcessOperation {
    private String  operateId;
    private String  userId;
    private String  bootstrapServers;
    private String  pattern;
    private String  conversionEntities;
    private String  topicName;
    private String  esHostName;
    private int  esPort;
    private String  indexName;
    private String  type;
    private Long  time;

    public ProcessOperation(String operateId, String userId, String bootstrapServers, String pattern, String conversionEntities, String topicName, String esHostName, int esPort, String indexName, String type, Long time) {
        this.operateId = operateId;
        this.userId = userId;
        this.bootstrapServers = bootstrapServers;
        this.pattern = pattern;
        this.conversionEntities = conversionEntities;
        this.topicName = topicName;
        this.esHostName = esHostName;
        this.esPort = esPort;
        this.indexName = indexName;
        this.type = type;
        this.time = time;
    }

    public String getOperateId() {
        return operateId;
    }

    public void setOperateId(String operateId) {
        this.operateId = operateId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getConversionEntities() {
        return conversionEntities;
    }

    public void setConversionEntities(String conversionEntities) {
        this.conversionEntities = conversionEntities;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getEsHostName() {
        return esHostName;
    }

    public void setEsHostName(String esHostName) {
        this.esHostName = esHostName;
    }

    public int getEsPort() {
        return esPort;
    }

    public void setEsPort(int esPort) {
        this.esPort = esPort;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
