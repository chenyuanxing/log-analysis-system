package com.cad.entity.domain;

public class Operate {
    private String operate;
    private int param;
    private String id;
    private Object file;
    private String other;
    private Long timestamp ;

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public int getParam() {
        return param;
    }

    public void setParam(int param) {
        this.param = param;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getFile() {
        return file;
    }

    public void setFile(Object file) {
        this.file = file;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return "Operate{" +
                "operate='" + operate + '\'' +
                ", param=" + param +
                ", id='" + id + '\'' +
                ", file=" + file +
                ", other='" + other + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
