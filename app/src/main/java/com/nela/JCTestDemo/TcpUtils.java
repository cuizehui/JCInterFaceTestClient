package com.nela.JCTestDemo;


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

    public final static String CONNECT_READER_FAILED = "CONNECT_READER_FAILED/r/n/r/n";

    public final String TAG = TcpUtils.class.getSimpleName();

    private Socket client;

    private InputStream mInputStream;
    private InputStreamReader mInputStreamReader;
    private BufferedReader mBufferedReader;

    public Boolean connectService(final String address, final int port) {
        try {
            client = new Socket(address, port);
            client.setSoTimeout(CONNECT_TIME_OUT);
            if (isConnected()) {

                return initReader();
            } else {
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean initReader() throws IOException {
        mInputStream = client.getInputStream();
        mInputStreamReader = new InputStreamReader(mInputStream);
        mBufferedReader = new BufferedReader(mInputStreamReader);
        if (mInputStream != null && mInputStreamReader != null && mBufferedReader != null) {
            return true;
        } else {
            return false;
        }
    }

    //判断是否连接
    public boolean isConnected() {
        try {
            client.sendUrgentData(0xFF);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //阻塞方法
    public String getDataFromService() {
        String content = null;
        try {
            if (mInputStream != null && mBufferedReader != null) {
                content = mBufferedReader.readLine();
            } else {
                Log.d(TAG, "InputStream  获取失败");
                content = CONNECT_READER_FAILED;
                return content;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return content;
        }
        return content;
    }

    public void sentData2Server(String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }
        data = data.replaceAll("\r\n", "");
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
