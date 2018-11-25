package com.juphoon.JCTestDemo;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class ReflectionUtils {


    /**
     * 调用成员有参函数
     * 问题：返回值为NULL 如何处理
     *
     * @param T
     * @param cls        成员对应的类名
     * @param obj        实例对象
     * @param methodName
     * @param pramsList
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    public static <T> T refMethod(Class<T> T, Class<?> cls, Object obj, String methodName, List<HashMap<Class<?>, Object>> pramsList) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] pramsTypes = null;
        Object[] prams = null;
        if (pramsList != null) {
            pramsTypes = new Class[pramsList.size()];
            prams = new Object[pramsList.size()];
            for (int i = 0; i < pramsList.size(); i++) {
                HashMap<Class<?>, Object> pramMap = pramsList.get(i);
                Iterator<Class<?>> iter = pramMap.keySet().iterator();
                while (iter.hasNext()) {
                    Class<?> key = iter.next();
                    Object value = pramMap.get(key);
                    pramsTypes[i] = key;
                    prams[i] = value;
                }
            }
        }
        Method method = cls.getMethod(methodName, pramsTypes);
        T resultValue;//初始化返回值
        resultValue = (T) method.invoke(obj, prams);//第一个参数表示类的实例化对象，第二个及其以后参数为可变参数
        return resultValue;
    }

    /**
     * 获取成员
     *
     * @param className
     * @param FieldName
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Field refField(String className, String FieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> cls = Class.forName(className);
        Field field = cls.getDeclaredField(FieldName);
        field.setAccessible(true);
        return field;
    }

}
