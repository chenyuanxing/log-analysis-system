package com.cad.flinkservice.util;

import com.maxmind.geoip2.DatabaseReader;

import com.maxmind.geoip2.model.CityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
@Component
public class IpToAddress {
    private static String Wpath;
    private static String Fpath;
    @Value("${Window.GeoLite2.path}")
    public void setWpath(String wpath) {
        Wpath = wpath;
    }
    @Value("${FlinkCluster.GeoLite2.path}")
    public void setFpath(String fpath) {
        Fpath = fpath;
    }

    volatile static DatabaseReader reader;

    private static Logger logger = LoggerFactory.getLogger(IpToAddress.class);

    /**
     *
     * @description: 获得国家
     * @param ip
     * @return
     * @throws Exception
     */
    public static String getCountry(String ip) throws Exception {
        if (reader==null){
            reader = getSingleReader();
        }
        return reader.city(InetAddress.getByName(ip)).getCountry().getNames().get("zh-CN");
    }

    /**
     *
     * @description: 获得省份 包括北京市等.
     * @param ip
     * @return
     * @throws Exception
     */
    public static String getProvince(String ip) throws Exception {
        if (reader==null){
            reader = getSingleReader();
        }
        return reader.city(InetAddress.getByName(ip)).getMostSpecificSubdivision().getNames().get("zh-CN");
    }

    /**
     *
     * @description: 获得城市
     * @param ip
     * @return
     * @throws Exception
     */
    public static String getCity(String ip) throws Exception {
        if (reader==null){
            reader = getSingleReader();
        }
        CityResponse city= reader.city(InetAddress.getByName(ip));
        return reader.city(InetAddress.getByName(ip))
                .getCity()
                .getNames()
                .get("zh-CN");
    }

    /**
     *
     * @description: 获得经度
     * @param ip
     * @return
     * @throws Exception
     */
    public static Double getLongitude(String ip) throws Exception {
        if (reader==null){
            reader = getSingleReader();
        }
        return reader.city(InetAddress.getByName(ip)).getLocation().getLongitude();
    }

    /**
     *
     * @description: 获得纬度
     * @param ip
     * @return
     * @throws Exception
     */
    public static Double getLatitude(String ip) throws Exception {
        if (reader==null){
            reader = getSingleReader();
        }
        return reader.city(InetAddress.getByName(ip)).getLocation().getLatitude();
    }
    private static DatabaseReader getSingleReader(){
        if (reader==null){
            synchronized (DatabaseReader.class){
                if (reader==null){
                    try {
                        String osName = System.getProperties().getProperty("os.name");
                        String path;
                        if(osName.equals("Linux")) {
                            path = Fpath;
                        }else{
                            path = Wpath;
                        }
                        // 创建 GeoLite2 数据库
                        File database = new File(path);
                        reader = new DatabaseReader.Builder(database).build();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return reader;
    }
}
