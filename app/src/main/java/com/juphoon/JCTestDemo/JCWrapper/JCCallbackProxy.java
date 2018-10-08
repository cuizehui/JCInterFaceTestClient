package com.juphoon.JCTestDemo.JCWrapper;


import com.google.gson.Gson;
import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCTestEvent;
import com.juphoon.cloud.JCCallItem;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JCCallbackProxy implements JCCallbackProxyMethod {

    private Object tar;

    //绑定委托对象，并返回代理类
    public Object bind(Object tar) {
        this.tar = tar;
        //绑定该类实现的所有接口，取得代理类
        return Proxy.newProxyInstance(tar.getClass().getClassLoader(),
                tar.getClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        String resultJson = makeResultJson(method.getName(), args);

        method.getParameterTypes();

        result = method.invoke(tar, args);
        sendResult(resultJson);
        return result;
    }

    @Override
    public String makeResultJson(String method, Object[] args) {
        JSONObject resultJson = new JSONObject();
        //此处做处理类型判断处理
        try {
            resultJson.put("type", "callback");
            resultJson.put("method", method);
            Gson gson = new Gson();
            for (int i = 0; i < args.length; i++) {
                if (args[i].getClass() == com.juphoon.cloud.JCCallItem.class) {
                    JCCallItem jcCallItem = (JCCallItem) args[i];
                    resultJson.put("arg" + i, gson.toJson(jcCallItem));
                } else {
                    resultJson.put("arg" + i, args[i]);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return resultJson.toString();
    }

    @Override
    public boolean sendResult(String resultJson) {
        if (resultJson == null) {
            return false;
        }
        EventBus.getDefault().post(new JCTestEvent(resultJson));
        return true;
    }
}
