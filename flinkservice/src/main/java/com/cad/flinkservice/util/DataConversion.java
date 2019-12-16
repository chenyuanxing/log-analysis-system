package com.cad.flinkservice.util;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.kafka.common.protocol.types.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataConversion {

    /**
     * 数据转换
     * @param linkedMap
     * @param conversionEntities
     * @return
     */
    public Map<String,Object> conversion(Map<String,Object> linkedMap,ConversionEntities  conversionEntities){
        Map<String,Object> map = (Map<String, Object>) linkedMap.get("message");
        if (map==null){
            System.out.println("map null ");
            return linkedMap;
        }
        for (ConversionEntities.ConversionEntity conversionEntity:conversionEntities.getConversionEntityList()){
            if (conversionEntity.getType().equals(TypeEnum.IP)){
                String val = (String) map.get(conversionEntity.getKey());
                if (val==null || val.equals("")){
                    continue;
                }
                if (IPCheck(val)){
                    try {
                        val = IpToAddress.getProvince(val);
                    } catch (Exception e) {
                        val = "";
                        e.printStackTrace();
                    }
                    map.put(conversionEntity.getTarget(),val);
                }else{
                    map.put(conversionEntity.getTarget(),"notIP");
                }
            }
            else if (conversionEntity.getType().equals(TypeEnum.Date)){

            }
        }
        linkedMap.remove("message");
        linkedMap.put("message",map);
        return linkedMap;
    }
    /**
     * 判断IP地址的合法性，这里采用了正则表达式的方法来判断
     * return true，合法,false 不合法
     * */
    public static boolean IPCheck(String str) {
        if (str != null && !str.isEmpty()) {
            // 定义正则表达式
            String regex="^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$";
            // 判断ip地址是否与正则表达式匹配
            if (str.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }
}
