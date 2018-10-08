package com.juphoon.JCTestDemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCTestEvent;
import com.juphoon.JCTestDemo.JCWrapper.JCManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int SERVICE_CONNECT_OK = 0;
    private static final int SERVICE_SEND_COMMAND = 1;
    private static final int SERVICE_RECEIVE_COMMAND = 2;
    private EditText mETAddress;
    private EditText mETPort;
    private TextView mTestInfo;

    private JCManager mJcManager;
    private TcpUtils mTcpUtils;
    private Handler mWorkHandler;
    private Handler mWriteHandler;

    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SERVICE_CONNECT_OK:
                    mTestInfo.setText("服务连接成功");
                    break;
                case SERVICE_SEND_COMMAND:
                    mTestInfo.setText("发送请求" + msg.getData());
            }
        }
    };
    private String mHost;
    private int mPort;
    private ThreadPoolExecutor mExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        mJcManager = JCManager.getInstance();
        initView();
    }

    private void initView() {
        mETAddress = (EditText) findViewById(R.id.ev_address);
        mETPort = (EditText) findViewById(R.id.ev_port);
        mTestInfo = (TextView) findViewById(R.id.tv_info);
    }

    @Subscribe
    public void JCEvent(JCTestEvent jcEvent) {
        Message message = buildMessage(SERVICE_SEND_COMMAND, "sendData", jcEvent.testResult);
        mWriteHandler.sendMessage(message);
    }

    Runnable mWorkTask = new Runnable() {
        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            Looper.prepare();
            mWorkHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    CommandUtils commandUtils = new CommandUtils();
                    switch (msg.what) {
                        case SERVICE_RECEIVE_COMMAND:
                            Bundle messageBundle = msg.getData();
                            String serviceData = messageBundle.getString("ReceiveData");
                            //解析命令包
                            TestBean testBean = commandUtils.dealJson(serviceData);
                            try {
                                //命令脚本
                                if (TextUtils.equals(testBean.getType(), TestBean.TYPE_COMMEND)) {
                                    //获取App key初始化JCMannager
                                    Object value;
                                    if (TextUtils.isEmpty(testBean.getModule())) {
                                        value = ReflectionUtils.refMethod2(testBean.getReturnX(), JCManager.class, mJcManager, testBean.getMethod(), testBean.getParams());
                                    }
                                    //执行反射方法
                                    else {
                                        Field field = null;
                                        field = ReflectionUtils.refField(JCManager.class.getName(), testBean.getModule());
                                        value = ReflectionUtils.refMethod2(testBean.getReturnX(), field.getType(), field.get(mJcManager), testBean.getMethod(), testBean.getParams());
                                    }
                                    //组建回执包
                                    ResultBean resultBean = ResultBean.createResultBean(testBean.getMethod(), value);
                                    String resultMessage = ResultBean.transToJson(resultBean);
                                    Message message = buildMessage(SERVICE_SEND_COMMAND, "sendData", resultMessage);
                                    mWriteHandler.sendMessage(message);
                                } else if (TextUtils.equals(testBean.getType(), TestBean.TYPE_INFO)) {
                                    Log.d("WorkTask", "info脚本");
                                } else {
                                    Log.d("WorkTask", "解析命令失败");
                                }
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            };
            Looper.loop();
        }
    };

    Runnable mReadTask = new Runnable() {
        @Override
        public void run() {

            mTcpUtils = new TcpUtils();
            Boolean connectResult = mTcpUtils.connectService(mHost, mPort);
            if (connectResult) {
                boolean flag = true;
                while (flag) {
                    String content = mTcpUtils.getDataFromService();
                    if (content != null) {
                        Log.d("ReadTask: ", content.toString());
                        Message message = buildMessage(SERVICE_RECEIVE_COMMAND, "ReceiveData", content.toString());
                        mWorkHandler.sendMessage(message);
                        // mWorkHandler.sendMessage();
                        // mWriteTask.sendMessage();
                    }
                }
            } else {
                Log.d(TAG, "连接失败");
            }
        }
    };

    Runnable mWriteTask = new Runnable() {
        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            Looper.prepare();
            mWriteHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case SERVICE_SEND_COMMAND:
                            Bundle bundle = msg.getData();
                            String data = bundle.getString("sendData");
                            Log.d("WriteTask", data.toString());
                            mTcpUtils.sentData2Server(data);
                            break;
                    }
                }
            };
            Looper.loop();
        }
    };

    public Message buildMessage(int what, String key, String data) {
        Message message = new Message();
        message.what = what;
        Bundle bundle = new Bundle();
        bundle.putString(key, data.toString());
        message.setData(bundle);
        return message;
    }


    public void startTask(Runnable myTask) {
        mExecutor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(5));
        mExecutor.execute(myTask);
    }

    public void onConnect(View view) {
        mHost = mETAddress.getText().toString();
        mPort = Integer.parseInt(mETPort.getText().toString());
        if (!TextUtils.isEmpty(mHost) && !TextUtils.isEmpty(mHost)) {
            startTask(mReadTask);
            startTask(mWriteTask);
            startTask(mWorkTask);
        }
    }

    public void onSentCommand(View view) {
        Message message = new Message();
        message.what = SERVICE_SEND_COMMAND;
        mWriteHandler.sendMessage(message);
    }

    @Override
    protected void onDestroy() {
        mExecutor.shutdown();
        mWriteHandler = null;
        mWorkHandler = null;
        super.onDestroy();
    }
}
