package com.cad.web.util;

import com.cad.web.domain.Chart;
import com.cad.web.domain.Dashboard;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PanelLocation {
    public int[] getpanelLocation(Dashboard dashboard){
        int x,y,w,h;
        x = 0;
        y = 0;
        w = 10;
        h = 6;

        int i = 0;
        //是否为空余位置
        boolean NotEmpty = true;
        do {
            NotEmpty = false;
            x = (i%2)*10;
            y = (i/2)*6;
            //防止小数引起计算错误，全部乘以二
            double x_mid = 2*x+10;
            double y_mid = 2*y+6;
            for (Chart chart :dashboard.getCharts()){
                double mid_x = 2*chart.getX_axis()+chart.getWidth();
                double mid_y = 2*chart.getY_axis()+chart.getHeight();
                if(Math.abs(mid_x-x_mid)>=10+chart.getWidth() || Math.abs(mid_y-y_mid)>=6+chart.getHeight()){
                }else {
                    NotEmpty = true;
                }
            }
            i++;
        }while (NotEmpty);

        int[] location = {x,y,w,h};
        return location;
    }
}
