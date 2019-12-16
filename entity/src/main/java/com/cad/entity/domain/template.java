package com.cad.entity.domain;
public class template {
    public template(String id, Long time){
        this.id = id;
        this.time = time;
    }
    private String id;
    private Long time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}