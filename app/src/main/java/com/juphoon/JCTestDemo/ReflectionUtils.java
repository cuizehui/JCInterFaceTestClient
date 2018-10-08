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
     *
     * @param T
     * @param className
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

    public static <T> T refMethod(Class<T> T, String className, Object obj, String methodName, List pramsList) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> cls = Class.forName(className);
        Class<?>[] pramsTypes = new Class[pramsList.size()];
        Object[] prams = new Object[pramsList.size()];
        for (int i = 0; i < pramsList.size(); i++) {
            pramsTypes[i] = pramsList.get(i).getClass();
            prams[i] = pramsList.get(i);
        }
        Method method = cls.getMethod(methodName, pramsTypes);

        T resultValue;//初始化返回值
        resultValue = (T) method.invoke(obj, prams);//第一个参数表示类的实例化对象，第二个及其以后参数为可变参数
        return resultValue;
    }


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

    public static <T> T refMethod2(Class<T> T, Class<?> cls, Object obj, String methodName, List<HashMap<Class<?>, Object>> pramsList) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
     * 调用无参静态方法
     *
     * @param className
     * @param methodName
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static Object refStaticMethod(String className, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        Class<?> object = Class.forName(className);
        Class<?>[] pramsTypes = null;
        Object[] prams = null;
        Method method = object.getMethod(methodName, pramsTypes);
        Object returnValue = "";//初始化返回值
        returnValue = method.invoke(object, prams);//第一个参数表示类的实例化对象，第二个及其以后参数为可变参数
        System.out.println(returnValue);
        return returnValue;
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

    /**
     * @param obj       宿主对象
     * @param FieldName
     * @return
     */
    public static Object refFieldInstance(Object obj, String FieldName) {

        Object instance = null;
        try {
            Field field = obj.getClass().getDeclaredField(FieldName);
            field.setAccessible(true);
            instance = field.get(obj);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return instance;

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return instance;
        }
        return instance;
    }

    public static Object refField(String className) {
        try {
            Class<?> cls = Class.forName(className);
            Field[] fields = cls.getDeclaredFields();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param T          返回值类型
     * @param className  类名
     * @param methodName 方法名
     * @param pramsList  参数列表
     * @param <T>        返回值类型
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static <T> T refStaticMethod(Class<T> T, String className, String methodName, ArrayList pramsList) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        Class<?> cls = Class.forName(className);
        Class<?>[] pramsTypes = null;
        Object[] prams = null;
        if (pramsList != null) {
            pramsTypes = new Class[pramsList.size()];
            prams = new Object[pramsList.size()];
            for (int i = 0; i < pramsList.size(); i++) {
                pramsTypes[i] = pramsList.get(i).getClass();
                prams[i] = pramsList.get(i);
            }
        }

        Method method = cls.getMethod(methodName, pramsTypes);
        T returnValue;
        returnValue = (T) method.invoke(cls, prams);//第一个参数表示类的实例化对象，第二个及其以后参数为可变参数

        return returnValue;
    }


    /**
     * 反射需要参数的对象
     *
     * @param className
     * @param pramsList
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> refObject(String className, ArrayList pramsList) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> cls = Class.forName(className);

        cls.newInstance();
        Class<?>[] pramsTypes = null;
        Object[] prams = null;
        if (pramsList != null) {
            pramsTypes = new Class[pramsList.size()];
            prams = new Object[pramsList.size()];
            for (int i = 0; i < pramsList.size(); i++) {
                pramsTypes[i] = pramsList.get(i).getClass();
                prams[i] = pramsList.get(i);
            }
        }
        return null;
    }
}
