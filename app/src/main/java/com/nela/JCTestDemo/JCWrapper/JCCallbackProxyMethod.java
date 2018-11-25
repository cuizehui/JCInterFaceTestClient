package com.nela.JCTestDemo.JCWrapper;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public interface JCCallbackProxyMethod extends InvocationHandler {
    @Override
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;

    public String makeResultJson(String methodName, Object[] args);

    public boolean sendResult(String resultJson);
}
