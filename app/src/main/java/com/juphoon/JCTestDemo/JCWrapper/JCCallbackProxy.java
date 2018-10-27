package com.juphoon.JCTestDemo.JCWrapper;


import android.util.Log;

import com.google.gson.Gson;
import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCTestEvent;
import com.juphoon.cloud.JCCallItem;
import com.juphoon.cloud.JCMediaChannel;
import com.juphoon.cloud.JCMediaChannelParticipant;

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
            //序列化基本类型
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    Log.d("class type:", args[i].getClass().toString() + ".");
                    if (args[i].getClass() == com.juphoon.cloud.JCCallItem.class) {
                        //强制类型转换后内部类对象为OBJ
                        JCCallItem jcCallItem = (JCCallItem) args[i];
                        String s = gson.toJson(jcCallItem, JCCallItem.class);
                        resultJson.put("arg" + i, s);
                    } else if (args[i].getClass() == JCCallItem.ChangeParam.class) {
                        JCCallItem.ChangeParam callChangeParam = (JCCallItem.ChangeParam) args[i];
                        String s = gson.toJson(callChangeParam, JCCallItem.ChangeParam.class);
                        resultJson.put("arg" + i, s);
                    } else if (args[i].getClass() == JCMediaChannelParticipant.class) {
                        JCMediaChannelParticipant jcMediaChannelParticipant = (JCMediaChannelParticipant) args[i];
                        String partp = gson.toJson(jcMediaChannelParticipant);
                        resultJson.put("arg" + i, partp);
                    } else if (args[i].getClass() == JCMediaChannelParticipant.ChangeParam.class) {
                        JCMediaChannelParticipant.ChangeParam jcMCPartPchangeParam = (JCMediaChannelParticipant.ChangeParam) args[i];
                        resultJson.put("arg" + i, gson.toJson(jcMCPartPchangeParam));
                    } else if (args[i].getClass() == JCMediaChannel.PropChangeParam.class) {
                        JCMediaChannel.PropChangeParam propChangeParam = (JCMediaChannel.PropChangeParam) args[i];
                        resultJson.put("arg" + i, gson.toJson(propChangeParam));
                    } else {
                        resultJson.put("arg" + i, args[i]);
                    }
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
