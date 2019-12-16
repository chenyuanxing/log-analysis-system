package com.cad.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

@Service
public class YamlTranslate {

    /**
     * path 对应的文件内容一定得是JSONObject类型的数据。<不能是JSONArray类型>
     * @param path
     * @return
     */
    public Map getYamlToMap(String path) {
        Map map = new HashMap();
        FileInputStream fileInputStream = null;
        try {
            Yaml yaml = new Yaml();//实例化解析器
            File file = new File(path);//配置文件地址

            fileInputStream = new FileInputStream(file);
            map = yaml.loadAs(fileInputStream, Map.class);//装载的对象，这里使用Map, 当然也可使用自己写的对象
            //printMap(map, 0);
        }catch(FileNotFoundException e) {
            System.out.println("文件地址错误");
            e.printStackTrace();
        }finally {
            try {
                if(fileInputStream!=null)  fileInputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return map;
    }

    public JSONObject getYamlToJson(String path) {
        JSONObject jsonObj = new JSONObject();
        FileInputStream fileInputStream = null;
        try {
            Yaml yaml = new Yaml();//实例化解析器
            File file = new File(path);//配置文件地址

            fileInputStream = new FileInputStream(file);
            jsonObj = yaml.loadAs(fileInputStream, JSONObject.class);//装载的对象，这里使用Map, 当然也可使用自己写的对象
            //printMap(map, 0);
        }catch(FileNotFoundException e) {
            System.out.println("文件地址错误");
            e.printStackTrace();
        }finally {
            try {
                if(fileInputStream!=null)  fileInputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return jsonObj;
    }

    public com.alibaba.fastjson.JSONObject getYamlToAliJson(String path) {
        com.alibaba.fastjson.JSONObject jsonObj = new com.alibaba.fastjson.JSONObject();
        FileInputStream fileInputStream = null;
        try {
            Yaml yaml = new Yaml();//实例化解析器
            File file = new File(path);//配置文件地址

            fileInputStream = new FileInputStream(file);
            jsonObj = yaml.loadAs(fileInputStream, com.alibaba.fastjson.JSONObject.class);//装载的对象，这里使用Map, 当然也可使用自己写的对象
            //printMap(map, 0);
        }catch(FileNotFoundException e) {
            System.out.println("文件地址错误");
            e.printStackTrace();
        }finally {
            try {
                if(fileInputStream!=null)  fileInputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return jsonObj;
    }

    public com.alibaba.fastjson.JSONArray getYamlToAliJSONArray(String path) {
        com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
        FileInputStream fileInputStream = null;
        try {
            Yaml yaml = new Yaml();//实例化解析器
            File file = new File(path);//配置文件地址

            fileInputStream = new FileInputStream(file);
            jsonArray = yaml.loadAs(fileInputStream, com.alibaba.fastjson.JSONArray.class);//装载的对象，这里使用Map, 当然也可使用自己写的对象
            //printMap(map, 0);
        }catch(FileNotFoundException e) {
            System.out.println("文件地址错误");
            e.printStackTrace();
        }finally {
            try {
                if(fileInputStream!=null)  fileInputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return jsonArray;
    }


    public String getYaml(String path){
        BufferedReader reader = null;
        String shresult = "";
        try {
            reader = new BufferedReader(new FileReader(path));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                shresult = shresult+ tempString+"\n";
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return shresult;
    }

    /**
     * 通过 filebeat 各项配置信息，返回 json 格式配置,output为elasticsearch
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    public com.alibaba.fastjson.JSONObject getfilebeatJsonBydetails(String filebeatymlpath, com.alibaba.fastjson.JSONArray inputs, List<String> hosts, String index) {
        com.alibaba.fastjson.JSONObject jsonObj = new YamlTranslate().getYamlToAliJson(filebeatymlpath);
        jsonObj.remove("filebeat.inputs");
        jsonObj.put("filebeat.inputs",inputs);
        com.alibaba.fastjson.JSONObject output = new com.alibaba.fastjson.JSONObject();
        com.alibaba.fastjson.JSONArray hostsArray = new com.alibaba.fastjson.JSONArray();
        for (String host:hosts){
            hostsArray.add(host);
        }
        output.put("hosts",hostsArray);
        output.put("index",index+"-%{+yyyy.MM.dd}");
        jsonObj.remove("output.elasticsearch");
        jsonObj.put("output.elasticsearch",output);


        com.alibaba.fastjson.JSONObject templeteObject = new com.alibaba.fastjson.JSONObject();
        templeteObject.put("name",index);
        templeteObject.put("pattern",index+"-*");
        templeteObject.put("enabled",false);

        jsonObj.put("setup.template",templeteObject);

        return jsonObj;
    }
    /**
     * 通过 filebeat 各项配置信息，返回 json 格式配置，output为kafka
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    public com.alibaba.fastjson.JSONObject getfilebeatJsonBydetailsOutKafka(String filebeatymlpath, com.alibaba.fastjson.JSONArray inputs, List<String> hosts, String topic) {
        com.alibaba.fastjson.JSONObject jsonObj = new YamlTranslate().getYamlToAliJson(filebeatymlpath);
        jsonObj.remove("filebeat.inputs");
        jsonObj.put("filebeat.inputs",inputs);
        com.alibaba.fastjson.JSONObject output = new com.alibaba.fastjson.JSONObject();
        com.alibaba.fastjson.JSONArray hostsArray = new com.alibaba.fastjson.JSONArray();
        for (String host:hosts){
            hostsArray.add(host);
        }
        output.put("hosts",hostsArray);
        output.put("topic",topic);
        output.put("required_acks",1);
        output.put("compression","gzip");
        output.put("max_message_bytes","1000000");

        jsonObj.remove("output.elasticsearch");
        jsonObj.remove("output.kafka");
        jsonObj.put("output.kafka",output);

        return jsonObj;
    }

    /**
     * 通过 metricbeat的模块名称，获取其模块 json 的整合string
     * @param path
     * @param names
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    public JSONArray getMetricModulesJsonByNames(String path,List names) throws JsonProcessingException, IOException{
        com.alibaba.fastjson.JSONArray jsonArray = new YamlTranslate().getYamlToAliJSONArray(path);
        JSONArray jsonArray1 = new JSONArray();
//        System.out.println(jsonObj.get("metricbeat.modules"));
//        JSONArray jsonArray = ((JSONArray) jsonObj.get("metricbeat.modules"));
        for (int i = 0;i<jsonArray.size();i++){
            if(names.contains(jsonArray.getJSONObject(i).get("module"))){
                jsonArray1.add(jsonArray.getJSONObject(i));
            }
        }
//        JSONObject resultJsonObject = new JSONObject();
//        resultJsonObject.put("metricbeat.modules",arrayList.toArray());
        return jsonArray1;
    }
    /**
     * 通过 文件路径，hosts，modulesPath 获取正确的yaml string
     * @param path
     * @param hosts
     * @param modulesPath  最终该modules的文件路径及其名称  "${path.config}/modules.d/metricbeatmodules.yml"
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    public String getMetricYamlByHosts(String path,List<String> hosts,String modulesPath) throws JsonProcessingException, IOException{
        JSONObject jsonObj = new YamlTranslate().getYamlToJson(path);
        JSONObject jsonObject = (JSONObject) jsonObj.get("output.elasticsearch");
        JSONArray jsonArray = ((JSONArray) jsonObject.get("hosts"));
        jsonArray.clear();
        for (String host:hosts){
            jsonArray.add(host);
        }
        jsonObject.remove("hosts");
        jsonObject.put("hosts",jsonArray);
        jsonObj.remove("output.elasticsearch");
        jsonObj.put("output.elasticsearch",jsonObject);

        JSONObject jsonObject1 = (JSONObject) jsonObj.get("metricbeat.config.modules");
        jsonObject1.remove("path");
        jsonObject1.put("path",modulesPath);
        jsonObj.remove("metricbeat.config.modules");
        jsonObj.put("metricbeat.config.modules",jsonObject1);

//        System.out.println(resultJsonObject);
        // 将 json 转为 yml
        JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonObj.toString());
        String jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNodeTree);

        return jsonAsYaml;
    }

    /**
     * 通过 文件路径，hosts，modulesPath 获取正确的json string,弃用
     * @param path
     * @param hosts
     * @param modulesPath  最终该modules的文件路径及其名称  "${path.config}/modules.d/metricbeatmodules.yml"
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    public JSONObject getMetricJsonByHosts(String path, List<String> hosts, String modulesPath) throws JsonProcessingException, IOException{
        JSONObject jsonObj = new YamlTranslate().getYamlToJson(path);
        ArrayList<JSONObject> arrayList = new ArrayList<>();
        JSONObject jsonObject = (JSONObject) jsonObj.get("output.elasticsearch");
        JSONArray jsonArray = ((JSONArray) jsonObject.get("hosts"));
        jsonArray.clear();
        for (String host:hosts){
            jsonArray.add(host);
        }
        jsonObject.remove("hosts");
        jsonObject.put("hosts",jsonArray);
        jsonObj.remove("output.elasticsearch");
        jsonObj.put("output.elasticsearch",jsonObject);

        JSONObject jsonObject1 = (JSONObject) jsonObj.get("metricbeat.config.modules");
        jsonObject1.remove("path");
        jsonObject1.put("path",modulesPath);
        jsonObj.remove("metricbeat.config.modules");
        jsonObj.put("metricbeat.config.modules",jsonObject1);

        return jsonObj;
    }
    /**
     * 通过 文件路径，hosts,index，modulesPath 获取正确的json string  output为ES
     * @param path
     * @param hosts
     * @param index
     * @param modulesPath  最终该modules的文件路径及其名称  "${path.config}/modules.d/metricbeatmodules.yml"
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    public JSONObject getMetricJsonByHostsAndIndex(String path, List<String> hosts, String modulesPath,String index) throws JsonProcessingException, IOException{
        JSONObject jsonObj = new YamlTranslate().getYamlToJson(path);
        JSONObject templateObject = new JSONObject();
        templateObject.put("name",index);
        templateObject.put("pattern",index+"-*");
        templateObject.put("enabled",false);
        jsonObj.put("setup.template",templateObject);
        JSONObject jsonObject = (JSONObject) jsonObj.get("output.elasticsearch");
        jsonObject.put("index",index+"-"+"%{+yyyy.MM.dd}");
        JSONArray jsonArray = ((JSONArray) jsonObject.get("hosts"));
        jsonArray.clear();
        for (String host:hosts){
            jsonArray.add(host);
        }
        jsonObject.remove("hosts");
        jsonObject.put("hosts",jsonArray);
        jsonObj.remove("output.elasticsearch");
        jsonObj.put("output.elasticsearch",jsonObject);

        JSONObject jsonObject1 = (JSONObject) jsonObj.get("metricbeat.config.modules");
        jsonObject1.remove("path");
        jsonObject1.put("path",modulesPath);
        jsonObj.remove("metricbeat.config.modules");
        jsonObj.put("metricbeat.config.modules",jsonObject1);

        return jsonObj;
    }
    /**
     * 通过 文件路径，hosts,index，modulesPath 获取正确的json string  output为kafka
     * @param path
     * @param hosts
     * @param topic
     * @param modulesPath  最终该modules的文件路径及其名称  "${path.config}/modules.d/metricbeatmodules.yml"
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    public JSONObject getMetricJsonByHostsAndIndexOutKafka(String path, List<String> hosts, String modulesPath,String topic) throws JsonProcessingException, IOException{
        JSONObject jsonObj = new YamlTranslate().getYamlToJson(path);

        com.alibaba.fastjson.JSONObject output = new com.alibaba.fastjson.JSONObject();
        com.alibaba.fastjson.JSONArray hostsArray = new com.alibaba.fastjson.JSONArray();
        for (String host:hosts){
            hostsArray.add(host);
        }
        output.put("hosts",hostsArray);
        output.put("topic",topic);
        output.put("required_acks",1);
        output.put("compression","gzip");
        output.put("max_message_bytes","1000000");

        jsonObj.remove("output.elasticsearch");
        jsonObj.remove("output.kafka");
        jsonObj.put("output.kafka",output);

        JSONObject jsonObject1 = (JSONObject) jsonObj.get("metricbeat.config.modules");
        jsonObject1.remove("path");
        jsonObject1.put("path",modulesPath);
        jsonObj.remove("metricbeat.config.modules");
        jsonObj.put("metricbeat.config.modules",jsonObject1);

        return jsonObj;
    }
    /**
     * 测试 YamlTranslate
     * @param args
     */

    /**
    public static void main(String[] args) {
        System.out.println("String is : --------------------------------------");

        String s = new YamlTranslate().getYaml("C:/Users/chen/IdeaProjects/log-analysis-system/web/src/main/java/com/cad/web/metricmodules.yml");
        System.out.println(s);

        System.out.println("getMetricYamlByHosts is : ------------------------------------------- over");

        String sss = null;
        try {
            sss= new YamlTranslate().getMetricYamlByHosts("C:/Users/chen/IdeaProjects/log-analysis-system/web/src/main/java/com/cad/web/metricbeat.yml", Arrays.asList("localhost:9200","10.108.210.194:8999"),"${path.config}/modules.d/metricbeatmodule.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sss);


        System.out.println("getMetricModulesJsonByNames is : ------------------------------------------- over");

        JSONArray   jsonArray = new JSONArray();
        List list =  new ArrayList();
        list.add("system");
        list.add("http");
        try {
            jsonArray= new YamlTranslate().getMetricModulesJsonByNames("C:/Users/chen/IdeaProjects/log-analysis-system/web/src/main/java/com/cad/web/metricmodules.yml",list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(jsonArray);
    }
     */
}
