package com.cad.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.cad.collectionservice.domain.Agent;
import com.cad.collectionservice.server.DataCollectionServer;
import com.cad.entity.domain.AgentInfo;
import com.cad.entity.domain.CollectionStatus;
import com.cad.entity.domain.template;
import com.cad.web.GeneralResult;

import com.cad.web.domain.Chart;
import com.cad.web.service.AgentRedisService;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

import com.cad.web.util.ScheduledService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/agents" )
public class AgentController {
    @Autowired
    private HttpServletRequest request;

    @Value("${node.type}")
    private String node;


    @Value("${Wsh.shellfile}")
    private String Wshfile;
    @Value("${Lsh.shellfile}")
    private String Lshfile;

    @Autowired
    private AgentRedisService agentRedisService;

    @Autowired
    private DataCollectionServer dataCollectionServer;
    /**
     * 获取安装cmd
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/getInstallCMD",method = RequestMethod.GET)
    public GeneralResult getInstallCMD(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id,
                                       @RequestParam(value = "tag",required = false,defaultValue = "") String tag){
        // 鉴权 判断该id可查询哪些index

        // next

        String uuidKey = getUUID();

        ScheduledService.keyMap.put(uuidKey,new template(id,new Date().getTime()));

        GeneralResult result = new GeneralResult();

        result.setResultStatus(true);
        String cmd = "curl -L 'http://10.108.210.194:8999/agents/getInstallSh?uuidKey="+uuidKey+"&userId="+id+"&tag="+tag+"' | bash";

        result.setResultData(cmd);
        return result;
    }

    /**
     * 返回需要执行的shell脚本
     * @return
     */
    @RequestMapping(value = "/getInstallSh",method = RequestMethod.GET)
    public String getInstallCMD2(@RequestParam(value = "uuidKey",required = false,defaultValue = "uuidKey_example") String uuidKey,
                                 @RequestParam(value = "userId",required = false,defaultValue = "smj") String user,
                                 @RequestParam(value = "tag",required = false,defaultValue = "") String tag){
        // 鉴权 判断该id可查询哪些index

        // next

        String shresult = "";
        File file  = null;
        try {
            file = new File(Lshfile);
        }catch (Exception e){
            file = new File(Wshfile);

        }
        BufferedReader reader = null;

        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                shresult = shresult+ tempString+"\n";
                if(line ==6){
                    shresult = shresult+ "logsystemUserName=\""+user+"\""+"\n";
                }
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
        // 源码运行
//        if(ScheduledService.keyMap.keySet().contains(uuidKey)) {
//            shresult = shresult + "\n" + "go run main.go -k \"" + uuidKey + "\" " + "-u \"" + ScheduledService.keyMap.get(uuidKey).getId() + "\" "+ "-tag \"" + tag + "\" ";
//        }else {
//            shresult = shresult + "\n" + "go run main.go -k \"" + "test_uuidKey" + "\" " + "-u \"" + "test" + "\" ";
//        }
        //通过可执行文件运行
        if(ScheduledService.keyMap.keySet().contains(uuidKey)) {
            shresult = shresult + "\n" + "./go_build_github_com_beatwatcher_linux -k \"" + uuidKey + "\" " + "-u \"" + ScheduledService.keyMap.get(uuidKey).getId() + "\" "+ "-tag \"" + tag + "\" ";
        }else {
            shresult = shresult + "\n" + "./go_build_github_com_beatwatcher_linux -k \"" + "test_uuidKey" + "\" " + "-u \"" + "test" + "\" ";
        }
        return shresult;
    }



    /**
     * 注册
     * @tag tag 以逗号分隔
     * @return
     */
    @RequestMapping(value = "/registAgent",method = RequestMethod.POST)
    public GeneralResult registAgent(@RequestParam(value = "uuidKey",required = false,defaultValue = "test111") String uuidKey,
                                     @RequestParam(value = "agentVersion",required = false,defaultValue = "0.0.1") String agentVersion,
                                     @RequestParam(value = "tag",required = false,defaultValue = "") String tag,
                                     @RequestParam(value = "system",required = false,defaultValue = "") String system,
                                     @RequestParam(value = "port",required = false,defaultValue = "") String port,
//                                     @RequestParam(value = "memory",required = false,defaultValue = "") String mem,
//                                     @RequestParam(value = "cpu",required = false,defaultValue = "") String cpu,
                                     @RequestParam(value = "kernelVersion",required = false,defaultValue = "") String kernelVersion){
        // 鉴权 判断该id可查询哪些index

        // next
        AgentInfo agentInfo =  new AgentInfo();

        ArrayList<String> tags = new ArrayList<>();
        for (String s: tag.split(",")){
            tags.add(s);
        }
        int portInt = Integer.parseInt(port);



        Long registTime = new Date().getTime();
        if(ScheduledService.keyMap.keySet().contains(uuidKey)){
            agentInfo.setUuid(uuidKey);
            agentInfo.setUserName(ScheduledService.keyMap.get(uuidKey).getId());
            agentInfo.setAddress(getIpAddr(request));
            agentInfo.setAgentVersion(agentVersion);
            agentInfo.setRegistTime(registTime);
            agentInfo.setStatus("on");
            agentInfo.setTags(tags);
            agentInfo.setKernelVersion(kernelVersion);
            agentInfo.setSystem(system);
            agentInfo.setPort(portInt);


            ScheduledService.keyMap.remove(uuidKey);
        }else {
            GeneralResult result = new GeneralResult();
            result.setResultStatus(false);
            result.setDescribe("the uuid key is not exit !!");
            return result;
        }

        agentRedisService.addAgent(agentInfo);

        GeneralResult result = new GeneralResult();

        result.setResultStatus(true);
        return result;
    }
    /**
     * 注册
     * @tag tag 以逗号分隔
     * @return
     */
    @RequestMapping(value = "/updateAgentTag",method = RequestMethod.POST)
    public GeneralResult updateAgentTag(@RequestParam(value = "uuidKey",required = false,defaultValue = "test111") String uuidKey,
                                        @RequestParam(value = "userId",required = false,defaultValue = "smj") String user,
                                     @RequestParam(value = "tag",required = false,defaultValue = "") String tag){
        // 鉴权 判断该id可查询哪些index

        // next

        ArrayList<String> tags = new ArrayList<>();
        for (String s: tag.split(",")){
            tags.add(s);
        }

        agentRedisService.updateTags(user,uuidKey,tags);

        GeneralResult result = new GeneralResult();

        result.setResultStatus(true);
        return result;
    }

    /**
     * 停止
     * @return
     */
    @RequestMapping(value = "/stopAgent",method = RequestMethod.POST)
    public GeneralResult stopAgent(@RequestParam(value = "uuidKey",required = false,defaultValue = "test111") String uuidKey,
                             @RequestParam(value = "userId",required = false,defaultValue = "smj") String user){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        AgentInfo agentInfo = null;
        try {
            agentInfo = agentRedisService.getAgent(user,uuidKey);
            dataCollectionServer.stopAgent(agentInfo);
        } catch (Exception e) {
            result.setResultStatus(false);
            result.setResultData(e);
            e.printStackTrace();
            return result;
        }

        result.setResultStatus(true);
        return result;
    }

    /**
     * 删除该Agent
     * @return
     */
    @RequestMapping(value = "/delAgent",method = RequestMethod.POST)
    public GeneralResult delAgent(@RequestParam(value = "uuidKey",required = false,defaultValue = "test111") String uuidKey,
                                   @RequestParam(value = "userId",required = false,defaultValue = "smj") String user){
        // 鉴权 判断该id可查询哪些index

        // next
        GeneralResult result = new GeneralResult();
        AgentInfo agentInfo = null;
        try {
            agentInfo = agentRedisService.getAgent(user,uuidKey);
            if (agentInfo.getStatus().equals("on")){
                dataCollectionServer.stopAgent(agentInfo);
                agentRedisService.delAgent(agentInfo);
            }else {
                agentRedisService.delAgent(agentInfo);
            }
        } catch (Exception e) {
            result.setResultStatus(false);
            result.setResultData(e);
            e.printStackTrace();
            return result;
        }

        result.setResultStatus(true);
        return result;
    }

    /**
     * 心跳
     * @return
     */
    @RequestMapping(value = "/aliveAgent",method = RequestMethod.POST)
    public void collectAgent(@RequestParam(value = "uuidKey",required = false,defaultValue = "test111") String uuidKey,
                             @RequestParam(value = "userId",required = false,defaultValue = "smj") String user,
                             @RequestParam(value = "collectionStatuses",required = false,defaultValue = "[{\"agentuuid\":\"1234567890default\",\"configname\":\"configname\",\"pid\":1111,\"status\":\"on\",\"other\":\"\"},{\"agentuuid\":\"1234567890default\",\"configname\":\"configname2\",\"pid\":2222,\"status\":\"off\",\"other\":\"\"}]") String collectionStatuses){
        // 鉴权 判断该id可查询哪些index

        // next
        com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(collectionStatuses);
//        JSONArray jsonArray = JSONArray.fromObject(collectionStatuses);
//        System.out.println(jsonArray);
        ArrayList<CollectionStatus> arrayList = new ArrayList();
        if(jsonArray !=null){
            for(Object object : jsonArray){
                CollectionStatus collectionStatus = JSONArray.toJavaObject((com.alibaba.fastjson.JSONObject)object,CollectionStatus.class);
//            CollectionStatus collectionStatus =(CollectionStatus)JSONObject.toBean((JSONObject)object,CollectionStatus.class);
                arrayList.add(collectionStatus);
            }
        }
        ScheduledService.alivemap.put(uuidKey,arrayList);

//        ScheduledService.set.add(uuidKey);


//        GeneralResult result = new GeneralResult();
//        result.setResultStatus(true);
//        return result;
    }



    /**
     * getMachine
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/getMachine",method = RequestMethod.GET)
    public GeneralResult getMachine(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id){

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id可查询哪些index

        // next
        result.setResultData(agentRedisService.getAgents(id));
        if(result.getResultData()!=null){
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }
    /**
     * 获取该用户机器的所有标签
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/getAllMachineTags",method = RequestMethod.GET)
    public GeneralResult getAllMachineTags(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id){
        GeneralResult result = new GeneralResult();
        // 鉴权 判断该id可查询哪些index

        // next
        ArrayList<AgentInfo> agentInfos= agentRedisService.getAgents(id);
        HashSet<String> hashSet = new HashSet<>();
        for (AgentInfo agentInfo:agentInfos){
            for (String tag:agentInfo.getTags()){
                hashSet.add(tag);
            }
        }
        result.setResultData(hashSet);
        if(result.getResultData()!=null){
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }

    /**
     * 根据索引获取日志,按照时间排序
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/configs",method = RequestMethod.GET)
    public GeneralResult getConfigs(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id){

        GeneralResult result = new GeneralResult();

        // 鉴权 判断该id可查询哪些index

        // next
        String stringObject = "{\n" +
                "\"panel\":{\n" +
                "\"panelid\": \"panelid1\",\n" +
                "\"title\": \"API响应时间\",\n" +
                "\"currentchart\": \"aaa\",\n" +
                "\"currentpara\": \"{\\\"title\\\":\\\"API响应时间\\\",\\\"chartdata\\\":[171,104,52,151,204,115,153,60,87,170,171,21,94,60,27,148,61,206],\\\"legend\\\":[\\\"he\\\"],\\\"url\\\":\\\"http://10.108.210.194:8999/panel/realTimePanel?userId=1&isRealTime=true&Index=Index&LastTime=2018-12-06 11:10:51&interval=20&Yaxis=responseTime&Indicator=count\\\",\\\"chartType\\\":\\\"line\\\"}\"\n" +
                "},\n" +
                "\"x_axis\": 0,\n" +
                "\"y_axis\": 0,\n" +
                "\"width\": 0,\n" +
                "\"height\": 0\n" +
                "}";

        Gson g = new Gson();
        Chart chart = g.fromJson(stringObject, Chart.class);

        result.setResultData(chart);

        if(result.getResultData()!=null){
            result.setResultStatus(true);
        }else {
            result.setResultStatus(false);
            result.setErrorMessage("get error !!");
        }
        return result;
    }


    /**
     * 添加机器
     * @param id 用户id
     * @return
     */
    @RequestMapping(value = "/addMachineByP",method = RequestMethod.POST)
    public GeneralResult addMachine(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id, @RequestParam("IP") String IP, @RequestParam("sshPORT") int sshPORT,
                                    @RequestParam("username") String userName, @RequestParam("password") String password, @RequestParam("tag") String tag){


        GeneralResult result = new GeneralResult();

        Agent agent = new Agent();
        agent.setId("id-"+IP.length()+getUUID()+userName+"jdienxd25t");
        agent.setHostname(getUUID());
        agent.setIp(IP);
        agent.setVersion("v0.03");
        String[] tags = {tag};
        agent.setTags(tags);
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        agent.setCreat_time(sdf.format(new Date()));


        DataCollectionServer.agents.add(agent);
        // 鉴权 判断该id可查询哪些index

        // next
        result.setResultStatus(true);
        return result;
    }
    public static String getUUID(){ UUID uuid=UUID.randomUUID(); String str = uuid.toString(); String uuidStr=str.replace("-", ""); return uuidStr; }

    /**
     * @Description: 获取客户端IP地址
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if(ip.equals("127.0.0.1")){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ip= inet.getHostAddress();
            }
        }
        // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ip != null && ip.length() > 15){
            if(ip.indexOf(",")>0){
                ip = ip.substring(0,ip.indexOf(","));
            }
        }
        return ip;
    }
}
