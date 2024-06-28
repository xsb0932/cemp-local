package com.landleaf.gw.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;

public class MethodUtils {


    public static void invoke(Object subitem, Class cla , String methodName) {
        try{
            Method method  =cla.getMethod(methodName);
            method.invoke(subitem);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
