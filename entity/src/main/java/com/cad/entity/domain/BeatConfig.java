package com.cad.entity.domain;


import java.util.List;

/**
 * name 为唯一属性 与beatyml中的相同
 * type 只有两种可能 "filebeat" 和 "metricbeat"
 */
public class BeatConfig {
    private String name;
    private String type;
    private List<String> tags;
    private String descripe;
    private Long updateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDescripe() {
        return descripe;
    }

    public void setDescripe(String descripe) {
        this.descripe = descripe;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
