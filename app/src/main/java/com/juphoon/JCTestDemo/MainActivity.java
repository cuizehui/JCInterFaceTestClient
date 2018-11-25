package com.juphoon.JCTestDemo;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCTestEvent;
import com.juphoon.JCTestDemo.JCWrapper.JCManager;
import com.juphoon.JCTestDemo.Toos.Config;

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
    private static final int SHOW_TEST_INFO = 3;

    private static final String MAIN_HANDLER_MESSAGE_KEY_INFO = "info";
    private static final String WRITE_HANDLER_MESSAGE_KEY_SEND_DATA = "send_data";
    private static final String WORK_HANDLER_MESSAGE_KEY_RECEIVER_DATA = "ReceiveData";

    private EditText mETAddress;
    private EditText mETPort;
    private TextView mTestInfo;

    private JCManager mJcManager;
    private TcpUtils mTcpUtils;
    private Handler mWorkHandler;
    private Handler mWriteHandler;


    private StringBuilder testInfo = new StringBuilder();

    @SuppressLint("HandlerLeak")
    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SERVICE_CONNECT_OK:
                    mTestInfo.setText("服务连接成功");
                    break;
                case SERVICE_SEND_COMMAND:
                    mTestInfo.setText("发送请求" + msg.getData());
                case SHOW_TEST_INFO:
                    Bundle messageShowInfo = msg.getData();
                    testInfo.append(messageShowInfo.getString(MAIN_HANDLER_MESSAGE_KEY_INFO));
                    mTestInfo.setText(testInfo.toString());
            }
        }
    };
    private String mHost;
    private int mPort;
    private ThreadPoolExecutor mExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        mJcManager = JCManager.getInstance();
        initView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i : grantResults) {
            if (i != PackageManager.PERMISSION_GRANTED) {
                System.exit(1);
            }
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA}, 1000);
            }
        }
    }


    private void initView() {
        mETAddress = (EditText) findViewById(R.id.ev_address);
        mETAddress.setText(Config.getLastConnectIp(this));
        mETPort = (EditText) findViewById(R.id.ev_port);
        mTestInfo = (TextView) findViewById(R.id.tv_info);
        mTestInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    @Subscribe
    public void JCEvent(JCTestEvent jcEvent) {
        Message message = buildMessage(SERVICE_SEND_COMMAND, WRITE_HANDLER_MESSAGE_KEY_SEND_DATA, jcEvent.testResult);
        //延时100秒防止回调比调用来的快
        mWriteHandler.sendMessageDelayed(message, 100);
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
                            String serviceData = messageBundle.getString(WORK_HANDLER_MESSAGE_KEY_RECEIVER_DATA);
                            //解析命令包
                            TestBean testBean = commandUtils.dealJson(serviceData);
                            try {
                                //命令脚本
                                if (TextUtils.equals(testBean.getType(), TestBean.TYPE_COMMEND)) {
                                    //获取App key初始化JCMannager
                                    Object value;
                                    if (TextUtils.isEmpty(testBean.getModule())) {
                                        value = ReflectionUtils.refMethod(testBean.getReturnX(), JCManager.class, mJcManager, testBean.getMethod(), testBean.getParams());
                                    }
                                    //执行反射方法
                                    else {
                                        Field field = null;
                                        field = ReflectionUtils.refField(JCManager.class.getName(), testBean.getModule());
                                        value = ReflectionUtils.refMethod(testBean.getReturnX(), field.getType(), field.get(mJcManager), testBean.getMethod(), testBean.getParams());
                                    }
                                    //组建回执包
                                    ResultBean resultBean = ResultBean.createResultBean(testBean.getMethod(), value);
                                    String resultMessage = ResultBean.transToJson(resultBean);
                                    Message message = buildMessage(SERVICE_SEND_COMMAND, WRITE_HANDLER_MESSAGE_KEY_SEND_DATA, resultMessage);
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
                Config.setLastConnectIp(MainActivity.this, mHost);
                boolean flag = true;
                while (flag) {
                    String content = mTcpUtils.getDataFromService();
                    if (content != null) {
                        if (TextUtils.equals(content, TcpUtils.CONNECT_READER_FAILED)) {
                            break;
                        }

                        Message messageShowInfo = buildMessage(SHOW_TEST_INFO, MAIN_HANDLER_MESSAGE_KEY_INFO, "\r\n收到命令：" + content);
                        mMainHandler.sendMessage(messageShowInfo);
                        Log.d("ReadTask: ", content);
                        Message message = buildMessage(SERVICE_RECEIVE_COMMAND, WORK_HANDLER_MESSAGE_KEY_RECEIVER_DATA, content);
                        mWorkHandler.sendMessage(message);
                        // mWorkHandler.sendMessage();
                        // mWriteTask.sendMessage();
                    } else {
                        if (!JCManager.getInstance().call.getCallItems().isEmpty()) {
                            JCManager.getInstance().call.term(JCManager.getInstance().call.getCallItems().get(0), 0, "");
                        }
                        JCManager.getInstance().mediaChannel.leave();
                        JCManager.getInstance().client.logout();
                        JCManager.getInstance().uninitialize();
                        break;
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
                            String data = bundle.getString(WRITE_HANDLER_MESSAGE_KEY_SEND_DATA);
                            //显示信息
                            Message messageShowInfo = buildMessage(SHOW_TEST_INFO, MAIN_HANDLER_MESSAGE_KEY_INFO, "\r\n发送命令: " + data);
                            mMainHandler.sendMessage(messageShowInfo);
                            Log.d("WriteTask", data);
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
        testInfo.setLength(0);
        mTestInfo.setText(testInfo.toString());
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
