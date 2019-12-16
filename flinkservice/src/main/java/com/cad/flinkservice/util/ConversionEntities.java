package com.cad.flinkservice.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConversionEntities implements Serializable {

    private List<ConversionEntity> conversionEntityList = new ArrayList<>();

    public List<ConversionEntity> getConversionEntityList() {
        return conversionEntityList;
    }

    public void setConversionEntityList(List<ConversionEntity> conversionEntityList) {
        this.conversionEntityList = conversionEntityList;
    }

    public class ConversionEntity implements Serializable{
        private String key;
        private TypeEnum type;
        private String target;
        private String tag;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public TypeEnum getType() {
            return type;
        }

        public void setType(TypeEnum type) {
            this.type = type;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }

}
