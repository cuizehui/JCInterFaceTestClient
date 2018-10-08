package com.juphoon.JCTestDemo;


import android.text.TextUtils;

import com.juphoon.JCTestDemo.JCWrapper.JCManager;
import com.juphoon.cloud.JCCallItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CommandUtils {

    public TestBean dealJson(String json) {
        JSONObject jsonObject = null;
        TestBean testBean = new TestBean();
        try {
            jsonObject = new JSONObject(json);
            String type = jsonObject.optString("type");
            if (TextUtils.equals(type, TestBean.TYPE_COMMEND)) {
                String moduleName = jsonObject.optString("module");
                String methodName = jsonObject.optString("method");
                JSONArray prams = jsonObject.optJSONArray("params");
                if (prams != null) {
                    List<HashMap<Class<?>, Object>> pramsList = new ArrayList();
                    for (int i = 0; i < prams.length(); i++) {
                        JSONObject pram = new JSONObject(prams.get(i).toString());
                        Iterator<String> sIterator = pram.keys();
                        HashMap<Class<?>, Object> parmMap = null;
                        while (sIterator.hasNext()) {
                            parmMap = new HashMap<>();
                            String key = sIterator.next();
                            Class<?> typeClass = translateType(key);
                            parmMap.put(typeClass, translateParam(typeClass, pram.get(key)));

                        }
                        pramsList.add(parmMap);
                    }
                    testBean.setParams(pramsList);
                }
                String returnType = jsonObject.optString("return");
                testBean.setModule(moduleName);
                testBean.setMethod(methodName);
                testBean.setType(type);
                testBean.setReturnX(translateType(returnType));
            }
            testBean.setType(type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return testBean;
    }


    public Class<?> translateType(String key) {
        Class<?> pramsType = null;
        if (TextUtils.equals("bool", key)) {
            pramsType = boolean.class;
        } else if (TextUtils.equals("string", key)) {
            pramsType = String.class;
        } else if (TextUtils.equals("int", key)) {
            pramsType = int.class;
        } else if (TextUtils.equals("void", key)) {
            pramsType = void.class;
        } else if (TextUtils.equals("List", key)) {
            pramsType = List.class;
        } else if (TextUtils.equals("Bool", key)) {
            pramsType = Boolean.class;
        } else if (TextUtils.equals("callitem", key)) {
            pramsType = com.juphoon.cloud.JCCallItem.class;
        }
        return pramsType;
    }

    //处理特殊类型对象
    public Object translateParam(Class<?> type, Object value) {
        if (type == com.juphoon.cloud.JCCallItem.class) {
            List<JCCallItem> items = JCManager.getInstance().call.getCallItems();
            JCCallItem item = items.get(0);
            return item;
        }
        return value;
    }
}
