package com.cad.web.controller;

import com.alibaba.fastjson.JSON;
import com.cad.collectionservice.server.DataCollectionServer;
import com.cad.entity.domain.*;
import com.cad.web.GeneralResult;
import com.cad.web.service.AgentRedisService;
import com.cad.web.service.CollectionRedisService;
import com.cad.web.util.ScheduledService;
import com.cad.web.util.YamlTranslate;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/collection" )
public class CollectionController {
    @Value("${Window.metricbeat.ymlpath}")
    private String Wmetricbeatpath;
    @Value("${Linux.metricbeat.ymlpath}")
    private String Lmetricbeatpath;
    @Value("${Window.metricbeat.modules.ymlpath}")
    private String Wmetricbeatmodulespath;
    @Value("${Linux.metricbeat.modules.ymlpath}")
    private String Lmetricbeatmodulespath;
    @Value("${Window.filebeat.ymlpath}")
    private String Wfilebeatymlpath;
    @Value("${Linux.filebeat.ymlpath}")
    private String Lfilebeatymlpath;
    @Value("${Window.filebeat.modules.ymlpath}")
    private String WfilebeatymlModulespath;
    @Value("${Linux.filebeat.modules.ymlpath}")
    private String LfilebeatymlModulespath;


    @Autowired
    private DataCollectionServer dataCollectionServer;
    @Autowired
    private AgentRedisService agentRedisService;
    @Autowired
    private YamlTranslate yamlTranslate;

    @Autowired
    private CollectionRedisService collectionRedisService;
    /**
     * todo 上传配置文件 待续...
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/uploadConfig",method = RequestMethod.GET)
    public GeneralResult getInstallCMD(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                       @RequestParam(value = "tag",required = false,defaultValue = "") String tag){
        // 鉴权 判断该id可查询哪些index

        // next

        GeneralResult result = new GeneralResult();

        result.setResultStatus(true);

        return result;
    }

    /**
     * 获取所有可以选择的modules名称
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/system/getmoduleNames",method = RequestMethod.GET)
    public GeneralResult getmoduleNames(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        ArrayList<com.alibaba.fastjson.JSONObject> arrayList = new ArrayList<>();
        Set<String> set = new HashSet();

        String osName = System.getProperties().getProperty("os.name");
        String metricbeatmodulespath = Lmetricbeatmodulespath;
        if(osName.equals("Linux"))
        {
            System.out.println("running in Linux");
            metricbeatmodulespath = Lmetricbeatmodulespath;
        }
        else
        {
            System.out.println("don't running in Linux Maybe in windows ? ");
            metricbeatmodulespath = Wmetricbeatmodulespath;
        }

        com.alibaba.fastjson.JSONArray jsonArray =  yamlTranslate.getYamlToAliJSONArray(metricbeatmodulespath);

        for (int i = 0;i<jsonArray.size();i++){
            set.add((String) jsonArray.getJSONObject(i).get("module"));
        }
        // 将set中的所有名称 更改为json格式，并添加描述信息，然后放到数组中
        for (String name:set){
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("name",name);
            switch (name){
                case "kubernetes" :
                    jsonObject.put("descripe","Kubernetes 基础信息监控");
                    arrayList.add(jsonObject);
                    break;
                case "memcached" :
                    jsonObject.put("descripe","监控 Memcached 实例统计信息，包括运行时间、请求量、连接数等");
                    arrayList.add(jsonObject);
                    break;
                case "golang" :
                    jsonObject.put("descripe","获取 Golang 中由 expvar 暴露的信息 ");
                    arrayList.add(jsonObject);
                    break;
                case "traefik" :
                    jsonObject.put("descripe","定期从 Traefik 实例获取指标");
                    break;
                case "graphite" :
                    jsonObject.put("descripe","Graphite 用于指定要运行哪些模块");
                    break;
                case "dropwizard" :
                    jsonObject.put("descripe","");
                    break;
                case "redis" :
                    jsonObject.put("descripe","定期从 Redis 服务器获取指标");
                    break;
                case "docker" :
                    jsonObject.put("descripe","监控 Docker 资源占用信息，包括容器个数、占用文件描述符个数等信息");
                    arrayList.add(jsonObject);
                    break;
                case "logstash" :
                    jsonObject.put("descripe","监控 Logstash 的 各种 node 状态信息");
                    arrayList.add(jsonObject);
                    break;
                case "apache" :
                    jsonObject.put("descripe","从 Apache 服务器获取指标，如cpu信息，uptime信息，连接上述状况等");
                    arrayList.add(jsonObject);
                    break;
                case "elasticsearch" :
                    jsonObject.put("descripe","监控 Elasticsearch 文件索引，操作系统，Java虚拟机，文件系统等信息");
                    arrayList.add(jsonObject);
                    break;
                case "php_fpm" :
                    jsonObject.put("descripe","");
                    break;
                case "postgresql" :
                    jsonObject.put("descripe","监控 PostgreSQL 模块的各种信息，如用户查询语句，占用内存状态等信息");
                    arrayList.add(jsonObject);
                    break;
                case "couchbase" :
                    jsonObject.put("descripe","");
                    break;
                case "mysql" :
                    jsonObject.put("descripe","定期从MySQL服务器获取指标。如 连接数，线程状态等");
                    arrayList.add(jsonObject);
                    break;
                case "prometheus" :
                    jsonObject.put("descripe","监控部署 Prometheus 节点监控的机器信息");
                    arrayList.add(jsonObject);
                    break;
                case "uwsgi" :
                    jsonObject.put("descripe","");
                    break;
                case "kibana" :
                    jsonObject.put("descripe","监控 Kibana 系统状态信息");
                    arrayList.add(jsonObject);
                    break;
                case "mongodb" :
                    jsonObject.put("descripe","从 MongoDB 服务器获取指标。监控 MongoDB 的系统状态信息");
                    arrayList.add(jsonObject);
                    break;
                case "munin" :
                    jsonObject.put("descripe","");
                    break;
                case "jolokia" :
                    jsonObject.put("descripe","");
                    break;
                case "kvm" :
                    jsonObject.put("descripe","");
                    break;
                case "nginx" :
                    jsonObject.put("descripe","监控 Nginx 服务器的状态信息");
                    arrayList.add(jsonObject);
                    break;
                case "zookeeper" :
                    jsonObject.put("descripe","监控 Zookeeper 的系统状态信息");
                    arrayList.add(jsonObject);
                    break;
                case "windows" :
                    jsonObject.put("descripe","监控 Windows 的性能，服务等系统状态信息");
                    arrayList.add(jsonObject);
                    break;
                case "rabbitmq" :
                    jsonObject.put("descripe","监控 Rabbitmq 的各种指标，如连接，节点等信息");
                    arrayList.add(jsonObject);
                    break;
                case "envoyproxy" :
                    jsonObject.put("descripe","");
                    break;
                case "system" :
                    jsonObject.put("descripe","监控 内存、用户数、cpu核数以及系统启动时间等");
                    arrayList.add(jsonObject);
                    break;
                case "ceph" :
                    jsonObject.put("descripe","");
                    break;
                case "vsphere" :
                    jsonObject.put("descripe","");
                    break;
                case "kafka" :
                    jsonObject.put("descripe","监控 Kafka 的状态信息，如分片信息，消费组信息等");
                    arrayList.add(jsonObject);
                    break;
                case "haproxy" :
                    jsonObject.put("descripe","");
                    break;
                case "http" :
                    jsonObject.put("descripe","监控某个或某些 http 请求，用于调用没有专用监控模块的 HTTP 端点。");
                    arrayList.add(jsonObject);
                    break;
                case "aerospike" :
                    jsonObject.put("descripe","");
                    break;
                case "etcd" :
                    jsonObject.put("descripe","监控 Etcd 的各项信息，如 leader 状态，followers 状态等信息");
                    arrayList.add(jsonObject);
                    break;
                default:{
                    jsonObject.put("descripe","default descripe");
                    break;
                }
            }
//            arrayList.add(jsonObject);
        }
        result.setResultData(arrayList);
        result.setResultStatus(true);
        return result;
    }

    /**
     * 获取所有modules 的Json
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/system/getAllmodules",method = RequestMethod.GET)
    public GeneralResult getAllmodules(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id){
        // 鉴权 判断该id可查询哪些index

        // next

        GeneralResult result = new GeneralResult();
        Set set = new HashSet();
        String osName = System.getProperties().getProperty("os.name");
        String metricbeatmodulespath = Lmetricbeatmodulespath;
        if(osName.equals("Linux")) {
            metricbeatmodulespath = Lmetricbeatmodulespath;
        }
        else {
            metricbeatmodulespath = Wmetricbeatmodulespath;
        }
        com.alibaba.fastjson.JSONArray s =  yamlTranslate.getYamlToAliJSONArray(metricbeatmodulespath);

        System.out.println(s);
        result.setResultData(s);
        System.out.println("------");
        System.out.println(result.getResultData());
        result.setResultStatus(true);

        return  result;
    }

    /**
     * 根据提供的module名称 获取部分modules 的json
     * @param id 用户id
     * @param Names 逗号分隔，选择的所有module的名称
     * @return
     */
    @RequestMapping(value = "/system/getmodules",method = RequestMethod.GET)
    public GeneralResult getmodules(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                    @RequestParam(value = "names") String Names){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        List<String> names =Arrays.asList(Names.split(","));

        String osName = System.getProperties().getProperty("os.name");
        String metricbeatmodulespath = Lmetricbeatmodulespath;
        if(osName.equals("Linux")) {
            metricbeatmodulespath = Lmetricbeatmodulespath;
        } else {
            metricbeatmodulespath = Wmetricbeatmodulespath;
        }

        JSONArray s = null;
        try {
            s = yamlTranslate.getMetricModulesJsonByNames(metricbeatmodulespath,names);
        } catch (IOException e) {
            e.printStackTrace();
            result.setResultData(e);
            result.setResultStatus(false);
        }

        result.setResultData(s);
        result.setResultStatus(true);
        return result;
    }
    /**
     * 获取metricbeat的yaml string
     * @param id 用户id
     * @param Hosts 以 , 分隔
     * @return
     */
    @RequestMapping(value = "/system/getmetricYaml",method = RequestMethod.GET)
    public GeneralResult getmetricYaml(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                    @RequestParam(value = "hosts",required = false,defaultValue = "localhost:9200") String Hosts){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        List<String> hosts =Arrays.asList(Hosts.split(","));
        String osName = System.getProperties().getProperty("os.name");
        String metricbeatpath = Lmetricbeatpath;
        if(osName.equals("Linux")) {
            metricbeatpath = Lmetricbeatpath;
        } else {
            metricbeatpath = Wmetricbeatpath;
        }

        String s = null;
        try {
            s = yamlTranslate.getMetricYamlByHosts(metricbeatpath,hosts,"${path.config}/modules.d/"+id+new Date().getTime()+".yml");
        } catch (IOException e) {
            e.printStackTrace();
            result.setResultData(e);
            result.setResultStatus(false);
        }

        result.setResultData(s);
        result.setResultStatus(true);
        return result;
    }
    /**
     * 获取metricbeat的json string
     * @param id 用户id
     * @param Hosts 以 , 分隔
     * @return
     */
    @RequestMapping(value = "/system/getmetricJson",method = RequestMethod.GET)
    public GeneralResult getmetricJson(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                       @RequestParam(value = "hosts",required = false,defaultValue = "localhost:9200") String Hosts,
                                       @RequestParam(value = "index",required = false,defaultValue = "test-index") String index){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        List<String> hosts =Arrays.asList(Hosts.split(","));
        String osName = System.getProperties().getProperty("os.name");
        String metricbeatpath = Lmetricbeatpath;
        if(osName.equals("Linux")) {
            metricbeatpath = Lmetricbeatpath;
        } else {
            metricbeatpath = Wmetricbeatpath;
        }

        JSONObject s = null;
        try {
            s = yamlTranslate.getMetricJsonByHostsAndIndex(metricbeatpath,hosts,"${path.config}/modules.d/"+id+new Date().getTime()+".yml",index);
        } catch (IOException e) {
            e.printStackTrace();
            result.setResultData(e);
            result.setResultStatus(false);
        }

        result.setResultData(s);
        result.setResultStatus(true);
        return result;
    }
    /**
     * 获取metricbeat的json string
     * @param id 用户id
     * @param Hosts 以 , 分隔
     * @return
     */
    @RequestMapping(value = "/system/getmetricJsonToKafka",method = RequestMethod.GET)
    public GeneralResult getmetricJsonToKafka(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                       @RequestParam(value = "hosts",required = false,defaultValue = "localhost:9200") String Hosts,
                                       @RequestParam(value = "topic",required = false,defaultValue = "test-topic") String topic){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        List<String> hosts =Arrays.asList(Hosts.split(","));
        String osName = System.getProperties().getProperty("os.name");
        String metricbeatpath = Lmetricbeatpath;
        if(osName.equals("Linux")) {
            metricbeatpath = Lmetricbeatpath;
        } else {
            metricbeatpath = Wmetricbeatpath;
        }

        JSONObject s = null;
        try {
            s = yamlTranslate.getMetricJsonByHostsAndIndexOutKafka(metricbeatpath,hosts,"${path.config}/modules.d/"+id+new Date().getTime()+".yml",topic);
        } catch (IOException e) {
            e.printStackTrace();
            result.setResultData(e);
            result.setResultStatus(false);
        }

        result.setResultData(s);
        result.setResultStatus(true);
        return result;
    }

    /**
     * 获取 filebeat 的json string 的demo
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/log/getLogConfigDemo",method = RequestMethod.GET)
    public GeneralResult getLogConfigDemo(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        String osName = System.getProperties().getProperty("os.name");
        String filebeatymlpath = Lfilebeatymlpath;
        if(osName.equals("Linux")) {
            filebeatymlpath = Lfilebeatymlpath;
        } else {
            filebeatymlpath = Wfilebeatymlpath;
        }

        com.alibaba.fastjson.JSONObject s = null;
        try {
            s = yamlTranslate.getYamlToAliJson(filebeatymlpath);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultData(e);
            result.setResultStatus(false);
        }

        result.setResultData(s);
        result.setResultStatus(true);
        return result;
    }

    /**
     * 获取 filebeat 的 input json
     * @param id 用户id
     * @param InputPaths 逗号分隔
     * @param multiline
     * @
     * @return
     */
    @RequestMapping(value = "/log/getLogInputConfig",method = RequestMethod.POST)
    public GeneralResult getLogInputConfig(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                      @RequestParam(value = "type",required = false,defaultValue = "f-test-index") String type,
                                      @RequestParam(value = "enabled",required = false,defaultValue = "true") boolean enabled,
                                      @RequestParam(value = "InputPaths",required = false,defaultValue = "test-index") String InputPaths,
                                      @RequestParam(value = "fields",required = false,defaultValue = "{}") String fields,
                                      @RequestParam(value = "multiline",required = false,defaultValue = "{}") String multiline){
        // 鉴权 判断该id可查询哪些index
        // next
        GeneralResult result = new GeneralResult();
        com.alibaba.fastjson.JSONObject InputObject = new com.alibaba.fastjson.JSONObject();

        com.alibaba.fastjson.JSONArray pathsArray = new com.alibaba.fastjson.JSONArray();
        for (String path:InputPaths.split(",")){
            pathsArray.add(path);
        }
        com.alibaba.fastjson.JSONObject multilineObject = JSON.parseObject(multiline);
        com.alibaba.fastjson.JSONObject fieldsObject = JSON.parseObject(fields);

        InputObject.put("type",type);
        InputObject.put("enabled",enabled);

        InputObject.put("paths",pathsArray);

        if (multilineObject != null && multilineObject.size()>=1){
            InputObject.put("multiline",multilineObject);
        }
        if (fieldsObject != null && fieldsObject.size()>=1){
            InputObject.put("fields",fieldsObject);
        }
        result.setResultData(InputObject);
        result.setResultStatus(true);
        return result;
    }

    /**
     * 获取 filebeat output 为 kafka 的json string
     * @param id 用户id
     * @param Hosts
     * @param topic topic名称
     * @param Inputs  是
     * @
     * @return
     */
    @RequestMapping(value = "/log/getLogConfigToKafka",method = RequestMethod.POST)
    public GeneralResult getLogConfigToKafka(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                      @RequestParam(value = "hosts",required = false,defaultValue = "localhost:9200") String Hosts,
                                      @RequestParam(value = "topic",required = false,defaultValue = "f-test-topic") String topic,
                                      @RequestParam(value = "Inputs") String Inputs){
        // 鉴权 判断该id可查询哪些index
        // next
        GeneralResult result = new GeneralResult();

        com.alibaba.fastjson.JSONArray inputs = com.alibaba.fastjson.JSONArray.parseArray(Inputs);

        List<String> hosts =Arrays.asList(Hosts.split(","));

        String osName = System.getProperties().getProperty("os.name");
        String filebeatymlpath = Lfilebeatymlpath;
        if(osName.equals("Linux")) {
            filebeatymlpath = Lfilebeatymlpath;
        } else {
            filebeatymlpath = Wfilebeatymlpath;
        }
        com.alibaba.fastjson.JSONObject s = null;
        try {
            s = yamlTranslate.getfilebeatJsonBydetailsOutKafka(filebeatymlpath,inputs,hosts,topic);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultData(e);
            result.setResultStatus(false);
        }

        result.setResultData(s);
        result.setResultStatus(true);
        return result;
    }

    /**
     * 获取 filebeat 的json string
     * @param id 用户id
     * @param Hosts
     * @param index
     * @param Inputs  是
     * @
     * @return
     */
    @RequestMapping(value = "/log/getLogConfig",method = RequestMethod.POST)
    public GeneralResult getLogConfig(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                      @RequestParam(value = "hosts",required = false,defaultValue = "localhost:9200") String Hosts,
                                      @RequestParam(value = "index",required = false,defaultValue = "f-test-index") String index,
                                      @RequestParam(value = "Inputs") String Inputs){
        // 鉴权 判断该id可查询哪些index
        // next
        GeneralResult result = new GeneralResult();

        com.alibaba.fastjson.JSONArray inputs = com.alibaba.fastjson.JSONArray.parseArray(Inputs);

        List<String> hosts =Arrays.asList(Hosts.split(","));

        String osName = System.getProperties().getProperty("os.name");
        String filebeatymlpath = Lfilebeatymlpath;
        if(osName.equals("Linux")) {
            filebeatymlpath = Lfilebeatymlpath;
        } else {
            filebeatymlpath = Wfilebeatymlpath;
        }
        com.alibaba.fastjson.JSONObject s = null;
        try {
            s = yamlTranslate.getfilebeatJsonBydetails(filebeatymlpath,inputs,hosts,index);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultData(e);
            result.setResultStatus(false);
        }

        result.setResultData(s);
        result.setResultStatus(true);
        return result;
    }

    /**
     * 获取 filebeat 的 modules json 的 demo
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/log/getLogConfigModulesDemo",method = RequestMethod.GET)
    public GeneralResult getLogConfigModulesDemo(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        String osName = System.getProperties().getProperty("os.name");
        String filebeatymlModulespath = LfilebeatymlModulespath;
        if(osName.equals("Linux")) {
            filebeatymlModulespath = LfilebeatymlModulespath;
        } else {
            filebeatymlModulespath = WfilebeatymlModulespath;
        }
        com.alibaba.fastjson.JSONArray s = null;
        try {
            s = yamlTranslate.getYamlToAliJSONArray(filebeatymlModulespath);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultData(e);
            result.setResultStatus(false);
        }
        result.setResultData(s);
        result.setResultStatus(true);
        return result;
    }

    /**
     * 添加收集器
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/updateCollectionTags",method = RequestMethod.POST)
    public GeneralResult updateCollectionTags(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                       @RequestParam(value = "configName",required = false,defaultValue = "testName") String configName,
                                       @RequestParam(value = "tags",required = false,defaultValue = "") String Tags){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();

        List<String> tags = Arrays.asList(Tags.split(","));

        try {
            boolean success = collectionRedisService.updateCollectionTags(id,configName,tags);
            if (success){
                result.setDescribe("Success ");
                result.setResultStatus(true);
            }else {
                result.setErrorMessage(" exception occur");
                result.setResultStatus(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            return result;
        }
        return result;
    }


    /**
     * 添加收集器
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/addCollection",method = RequestMethod.POST)
    public GeneralResult addCollection(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                         @RequestParam(value = "configName",required = false,defaultValue = "demoName") String configName,
                                         @RequestParam(value = "tags",required = false,defaultValue = "tag") String Tags,
                                         @RequestParam(value = "descripe",required = false,defaultValue = "Descripe") String descripe,
                                       @RequestParam(value = "type",required = false,defaultValue = "metricbeat") String type,
                                       @RequestParam(value = "beatJson") String beatJson1,
                                         @RequestParam(value = "modulesJson") String modulesJson1){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        BeatConfig beatConfig = new BeatConfig();
        beatConfig.setName(configName);
        beatConfig.setTags(Arrays.asList(Tags.split(",")));
        beatConfig.setDescripe(descripe);
        beatConfig.setType(type);
        beatConfig.setUpdateTime(new Date().getTime());

        BeatJson beatJson = new BeatJson();
        beatJson.setName(configName);
        beatJson.setJsonFile(beatJson1);
        beatJson.setModulesJsonFile(modulesJson1);

        try {
            boolean success = collectionRedisService.addBeatConfigAndJson(id,beatConfig,beatJson);
            if (success){
                result.setDescribe("Success ");
                result.setResultStatus(true);
            }else {
                result.setErrorMessage("The config name has exits !");
                result.setResultStatus(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            return result;
        }
        return result;
    }

    /**
     * 添加收集器
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/delCollection",method = RequestMethod.POST)
    public GeneralResult delCollection(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                       @RequestParam(value = "configName",required = false,defaultValue = "testName") String configName){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();

        try {
            boolean success = collectionRedisService.delBeatConfigAndJson(id,configName);
            if (success){
                result.setDescribe("Success ");
                result.setResultStatus(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            return result;
        }
        return result;
    }
    /**
     * 获取所有收集器的config
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/getAllCollctionConfig",method = RequestMethod.GET)
    public GeneralResult getAllCollctionConfig(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();

        try {
            Map map = collectionRedisService.getAllBeatConfig(id);
            result.setResultData(map.values());
            result.setResultStatus(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            return result;
        }
        return result;
    }

    /**
     * 获取所有收集器的config
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/getAllTags",method = RequestMethod.GET)
    public GeneralResult getAllTags(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        HashSet<String> hashSet = new HashSet<>();
        try {
            Map map = collectionRedisService.getAllBeatConfig(id);
            for (Object object:map.values()){
                BeatConfig beatConfig = (BeatConfig) object;
                for (String tag:beatConfig.getTags()){
                    hashSet.add(tag);
                }
            }
            result.setResultData(hashSet);
            result.setResultStatus(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            return result;
        }
        return result;
    }
    /**
     * 获取某个收集器的Json
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/getCollctionJson",method = RequestMethod.GET)
    public GeneralResult getCollctionJson(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                         @RequestParam(value = "configName") String configName){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();

        try {
            BeatJson beatJson = collectionRedisService.getBeatJson(id,configName);
            result.setResultData(beatJson);
            result.setResultStatus(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            return result;
        }
        return result;
    }
    /**
     * 获取某个agent上 所有收集器的Status
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/getCollctionsStatus",method = RequestMethod.GET)
    public GeneralResult getCollctionsStatus(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                          @RequestParam(value = "agentuuid") String agentuuid){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        try {
            ArrayList<CollectionStatus> arrayList = agentRedisService.getCollectionStatus(agentuuid);
            result.setResultData(arrayList);
            result.setResultStatus(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            return result;
        }
        return result;
    }


    /**
     * 将配置和config 分发 到相应机器上 启动
     * @param id 用户id
     * @param configName 配置名称
     * @param AgentuuidKeys 以逗号分隔
     * @return
     */
    @RequestMapping(value = "/dispense",method = RequestMethod.GET)
    public GeneralResult dispense(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                  @RequestParam(value = "configName") String configName,
                                  @RequestParam(value = "AgentuuidKeys") String AgentuuidKeys){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        List<String> agentuuidKeys = Arrays.asList(AgentuuidKeys.split(","));
        Set<AgentInfo> dispenseAgents = new HashSet<>();
        result.setResultStatus(true);
        for (String agentuuidKey:agentuuidKeys){
            try {
                AgentInfo agentInfo = agentRedisService.getAgent(id,agentuuidKey);
                if (agentInfo.getStatus()!=null && agentInfo.getStatus().equals("on")){
                    dispenseAgents.add(agentInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.setErrorMessage("\n fail on "+agentuuidKey);
            }
        }
        ArrayList<String> list = new ArrayList();
        for (AgentInfo agentInfo:dispenseAgents){
            try {
                BeatJson beatJson = collectionRedisService.getBeatJson(id,configName);
                BeatConfig beatConfig =collectionRedisService.getBeatConfig(id,configName);
                dataCollectionServer.startCollection(agentInfo,beatConfig,beatJson);
                list.add(agentInfo.getUuid());
            }catch (Exception e){
                e.printStackTrace();
                result.setErrorMessage(result.getErrorMessage()+"\n then fail on "+agentInfo.getUuid());
                result.setResultStatus(false);
            }
        }
        result.setResultData(list);
        result.setDescribe("the success dispense agents' agentuuidKeys list is as resultdata");
        return result;
    }
    /**
     * 将配置和config 分发 到 拥有相应标签的 机器上 启动
     * @param id 用户id
     * @param configName 配置名称
     * @param Tags 以逗号分隔
     * @return
     */
    @RequestMapping(value = "/dispenseByTags",method = RequestMethod.GET)
    public GeneralResult dispenseByTag(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                       @RequestParam(value = "configName") String configName,
                                        @RequestParam(value = "tags") String Tags){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        ArrayList<AgentInfo> arrayList = agentRedisService.getAgents(id);
        Set<String> agentuuidKeys = new HashSet<>();
        String[] tags = Tags.split(",");
        for (String tag:tags){
            for (AgentInfo agent:arrayList){
                //状态为on 的 agent 才有可能分发
                // 判空为防止null 掉用异常
                if(agent.getStatus()!=null && agent.getStatus().equals("on")){
                    if (agent.getTags() !=null && agent.getTags().contains(tag)){
                        agentuuidKeys.add(agent.getUuid());
                    }
                }
            }
        }
        for (String agentuuidKey:agentuuidKeys){
            try {
                AgentInfo agentInfo = agentRedisService.getAgent(id,agentuuidKey);
                BeatJson beatJson = collectionRedisService.getBeatJson(id,configName);
                BeatConfig beatConfig =collectionRedisService.getBeatConfig(id,configName);
                dataCollectionServer.startCollection(agentInfo,beatConfig,beatJson);
                result.setResultStatus(true);
            } catch (Exception e) {
                e.printStackTrace();
                result.setErrorMessage(result.getErrorMessage()+"\n fail on "+agentuuidKey);
            }
        }
        result.setResultData(agentuuidKeys);
        result.setDescribe("the dispense agents' agentuuidKeys list is as resultdata");
        return result;
    }

    /**
     * 停止某 collection
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/stopCollection",method = RequestMethod.GET)
    public GeneralResult stopCollection(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                        @RequestParam(value = "uuidKey") String uuidKey,
                                        @RequestParam(value = "configName") String configName,
                                        @RequestParam(value = "pid") String pid){
        // 鉴权 判断该id可查询哪些index
        // next
        GeneralResult result = new GeneralResult();
        ArrayList<CollectionStatus> arrayList = agentRedisService.getCollectionStatus(uuidKey);
        for ( CollectionStatus collectionStatus: arrayList) {
            // 找出匹配的 collectionStatus
            if (configName.equals(collectionStatus.getConfigname()) && pid.equals(collectionStatus.getPid())){
                if(collectionStatus.getStatus().equals("on")){
                    // 执行 停止操作
                    try {
                        AgentInfo agentInfo = agentRedisService.getAgent(id,uuidKey);
                        BeatConfig beatConfig =collectionRedisService.getBeatConfig(id,configName);
                        dataCollectionServer.stopCollection(agentInfo,beatConfig,collectionStatus);
                        result.setDescribe("success");
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.setResultStatus(false);
                        result.setDescribe(e);
                        break;
                    }
                    result.setResultStatus(true);
                    break;
                }else {
                    result.setResultStatus(false);
                    result.setErrorMessage("the collection has bean stoped !! do not need to stop it again !!");
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 获取某测试数据
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/getTestData",method = RequestMethod.GET)
    public GeneralResult getTestData(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                        @RequestParam(value = "uuidKey") String uuidKey,
                                        @RequestParam(value = "logPath") String logPath){
        // 鉴权 判断该id可查询哪些index
        // next
        GeneralResult result = new GeneralResult();


        // 执行 停止操作
        try {
            AgentInfo agentInfo = agentRedisService.getAgent(id,uuidKey);
            result.setResultData(dataCollectionServer.getTestData(agentInfo,logPath));
            result.setDescribe("success");
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultStatus(false);
            result.setDescribe(e);
        }
        result.setResultStatus(true);

        return result;
    }
}
