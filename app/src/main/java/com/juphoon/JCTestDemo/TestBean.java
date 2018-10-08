package com.juphoon.JCTestDemo;


import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class TestBean {

    /**
     * module : client
     * method : login
     * params : [{"string":"userid"},{"string":"password"}]
     * return : bool
     */

    public static final String TYPE_COMMEND = "command";
    public static final String TYPE_INFO = "info";

    private String module;
    private String method;
    private String type;

    @SerializedName("return")
    private Class<?> returnX;
    private List<HashMap<Class<?>, Object>> params;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class<?> getReturnX() {
        return returnX;
    }

    public void setReturnX(Class<?> returnX) {
        this.returnX = returnX;
    }

    public List<HashMap<Class<?>, Object>> getParams() {
        return params;
    }

    public void setParams(List<HashMap<Class<?>, Object>> params) {
        this.params = params;
    }

}
