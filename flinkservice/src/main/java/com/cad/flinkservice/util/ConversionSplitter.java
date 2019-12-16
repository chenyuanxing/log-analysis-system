package com.cad.flinkservice.util;

import org.apache.flink.api.common.functions.MapFunction;

import java.util.Map;

public class ConversionSplitter implements MapFunction<Map<String, Object>,Map<String, Object>> {
    private ConversionEntities conversionEntities;
    public ConversionSplitter(ConversionEntities conversionEntities){
        this.conversionEntities = conversionEntities;
    }
    @Override
    public Map<String, Object> map(Map<String, Object> map) throws Exception {
        DataConversion dataConversion = new DataConversion();
        dataConversion.conversion(map,conversionEntities);
        return map;
    }
}
