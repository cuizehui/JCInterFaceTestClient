package com.juphoon.JCTestDemo;


import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class TcpUtils {

    private final int CONNECT_TIME_OUT = 30000;

    public final  String TAG=TcpUtils.class.getSimpleName();

    private Socket client;

    private InputStream mInputStream;
    private InputStreamReader mInputStreamReader;
    private BufferedReader mBufferedReader;

    public Boolean connectService(final String adress, final int port) {
        try {
            client = new Socket(adress, port);
            client.setSoTimeout(CONNECT_TIME_OUT);
            if(isConnected()){
                initReader();
                return true;
            }else {
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void initReader() throws IOException {
        mInputStream = client.getInputStream();
        mInputStreamReader = new InputStreamReader(mInputStream);
        mBufferedReader = new BufferedReader(mInputStreamReader);
    }

    //判断是否连接
    public boolean isConnected(){
        try{
            client.sendUrgentData(0xFF);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    //阻塞方法
    public String getDataFromService() {
        String content = null;
        try {
            if (mInputStream != null) {
                content = mBufferedReader.readLine();
            } else {
                Log.d(TAG, "InputStream  获取失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return content;
        }
        return content;
    }

    public void sentData2Server(String data) {
        if(TextUtils.isEmpty(data)){
            return;
        }
         data=data.replaceAll("\r\n","");
        try {
            OutputStream outputStream = client.getOutputStream();
            if (outputStream != null) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                data = data + "\r\n";
                writer.write(data);
                writer.flush();
                outputStream.flush();
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
