package com.cad.elasticsearchservice.Util;

public class getIntervalTime {

    /**
     * 月之后为近似值
     * @param interval
     * @return
     * @throws Exception
     */
    public static Long getAsMil(String interval)throws Exception{
        if (interval==null || "".equals(interval)){
            throw new Exception("interval is null or None");
        }
        String numS = interval.substring(0,interval.length()-1);
        Long base = 3600000L;
        Long num = 0L;
        if (isNumeric(numS)){
            num = Long.valueOf(Integer.parseInt(numS));
        }else {
            throw new Exception("interval wrong");
        }

        char last = interval.charAt(interval.length()-1);
        if (last=='s'){
            base = 1000L;
        }else if(last=='m'){
            base = 60000L;
        }else if(last=='h'){
            base = 3600000L;
        }else if(last=='d'){
            base = 864000000L;
        }else if(last=='w'){
            base = 6048000000L;
        }else if(last=='M'){
            base = 25920000000L;
        }else if(last=='q'){
            base = 77760000000L;
        }else if(last=='y'){
            base = 311040000000L;
        }else {
            throw new Exception("interval wrong");
        }

        return base*num;
    }
    public final static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim()))
            return s.matches("^[0-9]*$");
        else
            return false;
    }
}
