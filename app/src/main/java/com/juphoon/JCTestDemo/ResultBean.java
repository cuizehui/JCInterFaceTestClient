package com.juphoon.JCTestDemo;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ResultBean {

    public static final String TYPE_COMMAND = "command";
    public static final String TYPE_CALLBACK = "callback";

    private String type;
    private String method;

    @SerializedName("return")
    private Object returnX;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getReturnX() {
        return returnX;
    }

    public void setReturnX(Object returnX) {
        this.returnX = returnX;
    }

    public static String transToJson(ResultBean resultBean) {
        Gson gson = new Gson();
        String resultString = gson.toJson(resultBean);
        return resultString;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static ResultBean createResultBean(String methodName, Object returnX) {
        ResultBean resultBean = new ResultBean();
        resultBean.setType(ResultBean.TYPE_COMMAND);
        resultBean.setMethod(methodName);
        resultBean.setReturnX(returnX);
        return resultBean;
    }
}
