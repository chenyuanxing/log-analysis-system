package com.cad.web.util;




import com.cad.web.service.PanelService;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * 造点数据
 */

public class CreatNumber {
    public static void main(String[] args) {
        System.out.println(new PanelService().timeDiagramData("index","111111111",20,"serviceRequestNum","mean"));
        System.out.println(new PanelService().timeDiagramData("index","111111111",20,"serviceRequestNum","mean"));
        System.out.println(new PanelService().timeDiagramData("index","111111111",20,"serviceRequestNum","mean"));
        System.out.println(new PanelService().timeDiagramData("index","111111111",20,"serviceRequestNum","mean"));
        System.out.println(new PanelService().timeDiagramData("index","111111111",20,"serviceRequestNum","mean"));


//        new CreatNumber().creatRandomList();
    }
    public ArrayList<Integer> creatRandomList(){
        DecimalFormat df = new DecimalFormat("0.000");
        Random rd = new Random();
        for(int i = 0;i<20;i++){
            System.out.println(df.format(rd.nextFloat()/4+0.2));
        }
        for (int i = 0;i<20;i++){
            System.out.println(rd.nextInt(5));
        }
        return null;
    }
}
