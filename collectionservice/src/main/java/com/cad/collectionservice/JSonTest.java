package com.cad.collectionservice;

import com.cad.entity.domain.CollectionStatus;
import com.cad.entity.domain.Operate;
import com.google.gson.Gson;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSonTest {

    /**
     * @param args
     */
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
       conn();
    }
    public static void test2(){
        Map map = new HashMap();
        map.put("222","22");
        map.put("222","11");
        map.put("111","22");
        map.put("222","new");

        System.out.println(map.toString());
    }
    public void test(){
        String collectionStatuses = "[{\"agentuuid\":\"1234567890default\",\"configname\":\"configname\",\"pid\":1111,\"status\":\"on\",\"other\":\"\"},{\"agentuuid\":\"1234567890default\",\"configname\":\"configname2\",\"pid\":2222,\"status\":\"off\",\"other\":\"\"}]";
        JSONArray jsonArray = JSONArray.fromObject(collectionStatuses);
        ArrayList<CollectionStatus> arrayList = new ArrayList();
        for(Object object : jsonArray){
            CollectionStatus collectionStatus =(CollectionStatus)JSONObject.toBean((JSONObject)object,CollectionStatus.class);
            arrayList.add(collectionStatus);
        }
        for (CollectionStatus collectionStatus:arrayList){
            System.out.println(collectionStatus.getAgentuuid());

        }
    }
    public static void conn(){


//        JSONObject jsonObj = new JSONObject();
//        jsonObj.put("operate", "test2");
//        jsonObj.put("param", 1);
//        jsonObj.put("id", "id_cyx");
//        jsonObj.put("timestamp", 22);
//
//        jsonObj.put("file", "liangyongs");
//        String[] likes = {"java", "golang", "clang"};
//        jsonObj.put("Lks", likes);
//        System.out.println("Object before sending to golang side:");

        Operate operate = new Operate();
        operate.setOperate("start");
        operate.setTimestamp(12345678901234L);
        operate.setId("id_cyx");
        operate.setFile("{\"file\":{\"jsonFile\":{\"setup.template.settings\":{\"index.number_of_shards\":1,\"index.codec\":\"best_compression\"},\"output.elasticsearch\":{\"hosts\":[\"localhost:9200\",\"10.108.210.194:8999\"]},\"metricbeat.config.modules\":{\"reload.enabled\":false,\"path\":\"${path.config}/modules.d/smj1547363524203.yml\"}},\"modulesJsonFile\":{\"metricbeat.modules\":[{\"module\":\"system\",\"metricsets\":[\"cpu\",\"filesystem\",\"fsstat\",\"load\",\"memory\",\"network\",\"process\",\"process_summary\",\"uptime\"],\"enabled\":true,\"period\":\"10s\",\"processes\":[\".*\"],\"cpu.metrics\":[\"percentages\"],\"core.metrics\":[\"percentages\"]},{\"module\":\"mysql\",\"period\":\"10s\",\"hosts\":[\"root:secret@tcp(127.0.0.1:3306)/\"]}]},\"name\":\"new_Collection\"},\"id\":\"\",\"operate\":\"\",\"param\":1,\"timestamp\":1547368715987}");
        operate.setParam(1);
        JSONObject jsonObj = JSONObject.fromObject(operate);
        System.out.println(jsonObj);

        try {
            socket = new Socket("10.108.211.22", 50001);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(jsonObj);
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                System.out.println(e);
//            }
            String line = in.readLine();
            System.out.println("Object read from golang side:");
            JSONObject backjsonObj = JSONObject.fromObject(line);
            Operate op = (Operate) JSONObject.toBean(backjsonObj,Operate.class);
            System.out.println(" operate is:" +op.toString());
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
