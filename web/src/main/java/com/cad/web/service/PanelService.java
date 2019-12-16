package com.cad.web.service;

import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PanelService {
    //冒充数据库
    static Map<String,ArrayList> db = new HashMap();
    static int countNow = 220;

    /**
     * @param Index  数据库索引
     * @param LastTime 最后时间
     * @param interval 时间间隔
     * @param type 需要绘制的指标 (各个字段的选择，如cpu.pct,memory.pct)
     * @param Indicator  聚合指标（计数，平均值，最大值等）
     * @return
     */
    public ArrayList<Float> timeDiagramData(String Index,String LastTime, Number interval,String type,String Indicator){
        DecimalFormat df = new DecimalFormat("0.000");
        Random rd = new Random();
        if(type.equals("cpu")){
            if(Indicator.equals("max")){
                if(!db.containsKey("max")){
                    ArrayList<Float> al =  new ArrayList<Float>();
                    for(int i = 0;i<20;i++){
                        al.add(Float.parseFloat(df.format(rd.nextFloat()/4+0.25)));
                    }
                    db.put("max",al);
                }else{
                    db.get("max").remove(0);
                    db.get("max").add(Float.parseFloat(df.format(rd.nextFloat()/4+0.25)));
                }
                return db.get("max");
            }else if(Indicator.equals("mean")){
                if(!db.containsKey("mean")){
                    ArrayList<Float> al =  new ArrayList<Float>();
                    for(int i = 0;i<20;i++){
                        al.add(Float.parseFloat(df.format(rd.nextFloat()/4+0.2)));
                    }
                    db.put("mean",al);
                }else{
                    db.get("mean").remove(0);
                    db.get("mean").add(Float.parseFloat(df.format(rd.nextFloat()/4+0.2)));
                }
                return db.get("mean");
            }else if(Indicator.equals("count")){
                if(!db.containsKey("count")){
                    ArrayList<Integer> al =  new ArrayList<Integer>();

                    for(int i = 0;i<20;i++){
                        countNow +=rd.nextInt(20);
                        al.add(countNow);
                    }
                    db.put("count",al);
                }else{
                    countNow += rd.nextInt(20);
                    db.get("count").remove(0);
                    db.get("count").add(countNow);
                }
                return db.get("count");
            }else{

                if(!db.containsKey("other")){
                    ArrayList<Float> al =  new ArrayList<Float>();
                    for(int i = 0;i<20;i++){
                        al.add(Float.parseFloat(df.format(rd.nextFloat()/4+0.25)));
                    }
                    db.put("other",al);
                }else{
                    db.get("other").remove(0);
                    db.get("other").add(Float.parseFloat(df.format(rd.nextFloat()/4+0.25)));
                }
                return db.get("other");
            }

        }else if(type.equals("responseTime")){
                if(!db.containsKey("responseTime_mean")){
                    ArrayList<Integer> al =  new ArrayList<Integer>();
                    for(int i = 0;i<20;i++){
                        al.add(rd.nextInt(200)+20);
                    }
                    db.put("responseTime_mean",al);
                }else{
                    db.get("responseTime_mean").remove(0);
                    db.get("responseTime_mean").add(rd.nextInt(200)+20);
                }
                return db.get("responseTime_mean");
        }else if (type.equals("diskUseage")){
            if(!db.containsKey("diskUseage_mean")){
                ArrayList<Float> al =  new ArrayList<Float>();
                for(int i = 0;i<20;i++){
                    al.add(Float.parseFloat(df.format(rd.nextFloat()/5+0.2)));
                }
                db.put("diskUseage_mean",al);
            }else{
                db.get("diskUseage_mean").remove(0);
                db.get("diskUseage_mean").add(Float.parseFloat(df.format(rd.nextFloat()/5+0.2)));
            }
            return db.get("diskUseage_mean");
        }else if (type.equals("serviceRequestNum")){
            if(!db.containsKey("serviceRequestNum_mean")){
                ArrayList<Integer> al =  new ArrayList<Integer>();
                for(int i = 0;i<20;i++){
                    al.add(rd.nextInt(100));
                }
                db.put("serviceRequestNum_mean",al);
            }else{
                db.get("serviceRequestNum_mean").remove(0);
                db.get("serviceRequestNum_mean").add(rd.nextInt(100));
            }
            return db.get("serviceRequestNum_mean");
        }else if(type.equals("count")){
            if(!db.containsKey("count")){
                ArrayList<Integer> al =  new ArrayList<Integer>();

                for(int i = 0;i<20;i++){
                    countNow +=rd.nextInt(20);
                    al.add(countNow);
                }
                db.put("count",al);
            }else{
                countNow += rd.nextInt(20);
                db.get("count").remove(0);
                db.get("count").add(countNow);
            }
            return db.get("count");
        } else{
            if(!db.containsKey("serviceRequestNum_mean")){
                ArrayList<Integer> al =  new ArrayList<Integer>();
                for(int i = 0;i<20;i++){
                    al.add(rd.nextInt(100));
                }
                db.put("serviceRequestNum_mean",al);
            }else{
                db.get("serviceRequestNum_mean").remove(0);
                db.get("serviceRequestNum_mean").add(rd.nextInt(100));
            }
            return db.get("serviceRequestNum_mean");
        }

    }
}
