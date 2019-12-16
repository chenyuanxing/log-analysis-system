package com.cad.entity.Study;

import javax.annotation.processing.RoundEnvironment;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Function;

@TestAnnotation(id= 3,meg = "hello annotation")
public class test {
    public static void main(String [] args){
        Runnable runnable = new Thread();
        System.out.println(hha());
        boolean hasAnnotation = test.class.isAnnotationPresent(TestAnnotation.class);
        if(hasAnnotation){
            TestAnnotation testAnnotation= test.class.getAnnotation(TestAnnotation.class);

            System.out.println("id:"+testAnnotation.id());
            System.out.println("msg:"+testAnnotation.meg());
        }

        try {
            Method method = test.class.getDeclaredMethod("hha");


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


    }
    @TestAnnotation(id = 1,meg = "oo")
    @Deprecated
    public static String hha(){
        return "deprecated  ";
    }
}
