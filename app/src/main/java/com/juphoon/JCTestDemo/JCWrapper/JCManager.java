package com.juphoon.JCTestDemo.JCWrapper;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.juphoon.JCTestDemo.JCApplication;
import com.juphoon.JCTestDemo.JCWrapper.JCData.JCGroupData;
import com.juphoon.JCTestDemo.JCWrapper.JCData.JCMessageData;
import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCAccountQueryStatusEvent;
import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCCallMessageEvent;
import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCConfMessageEvent;
import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCConfQueryEvent;
import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCEvent;
import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCJoinEvent;
import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCLoginEvent;
import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCMessageEvent;
import com.juphoon.JCTestDemo.JCWrapper.JCEvent.JCStorageEvent;
import com.juphoon.cloud.JCAccount;
import com.juphoon.cloud.JCAccountCallback;
import com.juphoon.cloud.JCAccountItem;
import com.juphoon.cloud.JCCall;
import com.juphoon.cloud.JCCallCallback;
import com.juphoon.cloud.JCCallItem;
import com.juphoon.cloud.JCClient;
import com.juphoon.cloud.JCClientCallback;
import com.juphoon.cloud.JCConfig;
import com.juphoon.cloud.JCGroup;
import com.juphoon.cloud.JCGroupCallback;
import com.juphoon.cloud.JCGroupItem;
import com.juphoon.cloud.JCGroupMember;
import com.juphoon.cloud.JCMediaChannel;
import com.juphoon.cloud.JCMediaChannelCallback;
import com.juphoon.cloud.JCMediaChannelParticipant;
import com.juphoon.cloud.JCMediaChannelQueryInfo;
import com.juphoon.cloud.JCMediaDevice;
import com.juphoon.cloud.JCMediaDeviceCallback;
import com.juphoon.cloud.JCMessageChannel;
import com.juphoon.cloud.JCMessageChannelCallback;
import com.juphoon.cloud.JCMessageChannelItem;
import com.juphoon.cloud.JCPush;
import com.juphoon.cloud.JCStorage;
import com.juphoon.cloud.JCStorageCallback;
import com.juphoon.cloud.JCStorageItem;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 本类主要是对Juphoon Cloud SDK 的简单封装
 */
public class JCManager implements JCClientCallback, JCCallCallback, JCMediaChannelCallback, JCMediaDeviceCallback,
        JCStorageCallback, JCGroupCallback, JCMessageChannelCallback, JCAccountCallback {

    public static JCManager getInstance() {
        return JCManagerHolder.INSTANCE;
    }

    public Boolean pstnMode = false; // 会议的Pstn落地模式

    private Context mContext;
    public JCClient client;
    public JCCall call;
    public JCMediaDevice mediaDevice;
    public JCMediaChannel mediaChannel;
    public JCMessageChannel messageChannel;
    public JCStorage storage;
    public JCGroup group;
    public JCPush push;
    public JCAccount account;
    public JCConfig config;

    private static final String MIPUSH_APP_ID = "2882303761517831413";
    private static final String MIPUSH_APP_KEY = "5271783186413";
    public static final String TAG = "your packagename";

    private static final int PUSH_TYPE_MI = 1;
    private static final int PUSH_TYPE_HMS = 2;
    private int mPushType = PUSH_TYPE_MI;

    public boolean initialize(String appkey) {
        mContext = JCApplication.sContext;
        JCCallbackProxy jcCallbackProxy = new JCCallbackProxy();
        Object jcCallbackImplProxy = jcCallbackProxy.bind(new JCCallbackImpl());
        client = JCClient.create(JCApplication.sContext, appkey, (JCClientCallback) jcCallbackImplProxy, null);
        mediaDevice = JCMediaDevice.create(client, (JCMediaDeviceCallback) jcCallbackImplProxy);
        mediaChannel = JCMediaChannel.create(client, mediaDevice, (JCMediaChannelCallback) jcCallbackImplProxy);
        call = JCCall.create(client, mediaDevice, (JCCallCallback) jcCallbackImplProxy);
        messageChannel = JCMessageChannel.create(client, (JCMessageChannelCallback) jcCallbackImplProxy);
        storage = JCStorage.create(client, (JCStorageCallback) jcCallbackImplProxy);
        push = JCPush.create(client);
        group = JCGroup.create(client, (JCGroupCallback) jcCallbackImplProxy);
        account = JCAccount.create(this);
        config = JCConfig.create();
        // 本程序设置为固定方向
        mediaDevice.autoRotate = false;
        return true;
    }

    public void uninitialize() {
        if (client != null) {
            JCPush.destroy();
            JCStorage.destroy();
            JCMessageChannel.destroy();
            JCCall.destroy();
            JCMediaChannel.destroy();
            JCMediaDevice.destroy();
            JCClient.destroy();
            JCAccount.destroy();
            JCConfig.destory();
            push = null;
            storage = null;
            messageChannel = null;
            call = null;
            mediaChannel = null;
            mediaDevice = null;
            client = null;
            account = null;
            config = null;
        }
    }

    @Override
    public void onLogin(boolean result, @JCClient.ClientReason int reason) {

//        StackTraceElement[] stacks = new Throwable().getStackTrace();
//        String methodName = stacks[0].getMethodName();
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("type", "callback");
//            jsonObject.put("method", methodName);
//            jsonObject.put("result", result);
//            jsonObject.put("reason", reason);
//            String testResult = jsonObject.toString();
//            EventBus.getDefault().post(new JCTestEvent(testResult));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        EventBus.getDefault().post(new JCLoginEvent(result, reason));
        if (result) {
            switch (mPushType) {
                case PUSH_TYPE_MI:
                    MiPushClient.registerPush(mContext, MIPUSH_APP_ID, MIPUSH_APP_KEY);
                    break;
            }
        }
    }

    @Override
    public void onLogout(@JCClient.ClientReason int reason) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.LOGOUT));
        //      saveLastLogined("", "");
        JCMessageData.clear();
        JCGroupData.clear();
        switch (mPushType) {
            case PUSH_TYPE_MI:
                //      HMSPush.stop(mContext);
                break;
            case PUSH_TYPE_HMS:
                MiPushClient.unregisterPush(mContext);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClientStateChange(@JCClient.ClientState int state, @JCClient.ClientState int oldState) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CLIENT_STATE_CHANGE));
    }

    @Override
    public void onCallItemAdd(JCCallItem item) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CALL_ADD));
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CALL_UI));
    }

    @Override
    public void onCallItemRemove(JCCallItem item, @JCCall.CallReason int reason, String description) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CALL_REMOVE));
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CALL_UI));
    }

    @Override
    public void onCallItemUpdate(JCCallItem item) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CALL_UPDATE));
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CALL_UI));


    }

    @Override
    public void onMessageReceive(String type, String content, JCCallItem callItem) {
        EventBus.getDefault().post(new JCCallMessageEvent(type, content, callItem));
    }

    @Override
    public void onMediaChannelStateChange(@JCMediaChannel.MediaChannelState int state, @JCMediaChannel.MediaChannelState int oldState) {

    }

    @Override
    public void onMediaChannelPropertyChange() {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CONFERENCE_PROP_CHANGE));
    }

    @Override
    public void onJoin(boolean result, @JCMediaChannel.MediaChannelReason int reason, String channelId) {
//        if (result) {
//            Log.d(TAG,"加入成功");
//        } else {
//            // 加入频道失败
//            Log.d(TAG,"加入失败");
//        }
        EventBus.getDefault().post(new JCJoinEvent(result, reason, channelId));
        if (result && pstnMode) {
            if (mediaChannel.inviteSipUser(channelId) == -1) {
                mediaChannel.leave();
            }
        }
    }

    @Override
    public void onLeave(@JCMediaChannel.MediaChannelReason int reason, String channelId) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CONFERENCE_LEAVE));
    }


    @Override
    public void onQuery(int operationId, boolean result, @JCMediaChannel.MediaChannelReason int reason, JCMediaChannelQueryInfo queryInfo) {
        EventBus.getDefault().post(new JCConfQueryEvent(operationId, result, reason, queryInfo));
    }

    @Override
    public void onParticipantJoin(JCMediaChannelParticipant participant) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CONFERENCE_PARTP_JOIN));
        if (pstnMode) {
            mediaChannel.enableAudioOutput(true);
        }
    }

    @Override
    public void onParticipantLeft(JCMediaChannelParticipant participant) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CONFERENCE_PARTP_LEAVE));
        if (pstnMode) {
            mediaChannel.leave();
        }
    }

    @Override
    public void onParticipantUpdate(JCMediaChannelParticipant participant) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CONFERENCE_PARTP_UPDATE));
    }

    @Override
    public void onMessageReceive(String type, String content, String fromUserId) {
        EventBus.getDefault().post(new JCConfMessageEvent(type, content, fromUserId));
    }

    @Override
    public void onInviteSipUserResult(int operationId, boolean result, int reason) {
        if (pstnMode && !result) {
            mediaChannel.leave();
        }
    }

    @Override
    public void onCameraUpdate() {

    }

    @Override
    public void onAudioOutputTypeChange(boolean speaker) {

    }
//
//    // 用于自动登录上次登录着的账号
//    public boolean loginIfLastLogined() {
//        String userId = PreferenceManager.getDefaultSharedPreferences(mContext)
//                .getString(mContext.getString(R.string.cloud_setting_last_login_user_id), null);
//        if (TextUtils.isEmpty(userId)) {
//            return false;
//        }
//        String password = PreferenceManager.getDefaultSharedPreferences(mContext)
//                .getString(mContext.getString(R.string.cloud_setting_last_login_password), null);
//        return client.login(userId, password);
//    }
//
//    // 保存最后一次登录账号信息
//    public void saveLastLogined(String userId, String password) {
//        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
//                .putString(mContext.getString(R.string.cloud_setting_last_login_user_id), userId)
//                .putString(mContext.getString(R.string.cloud_setting_last_login_password), password)
//                .apply();
//    }
//
//    // 生成默认配置
//    private void generateDefaultConfig(Context context) {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = sp.edit();
//        String value = sp.getString(context.getString(R.string.cloud_setting_key_display_name), "");
//        if (TextUtils.isEmpty(value)) {
//            editor.putString(context.getString(R.string.cloud_setting_key_display_name), "");
//        }
//        value = sp.getString(context.getString(R.string.cloud_setting_key_server), "");
//        if (TextUtils.isEmpty(value)) {
//            editor.putString(context.getString(R.string.cloud_setting_key_server), client.getConfig(JCClient.CONFIG_KEY_SERVER_ADDRESS));
//        }
//        value = sp.getString(context.getString(R.string.cloud_setting_key_call_max_num), "");
//        if (TextUtils.isEmpty(value)) {
//            editor.putString(context.getString(R.string.cloud_setting_key_call_max_num), String.valueOf(call.maxCallNum));
//        }
//        value = sp.getString(context.getString(R.string.cloud_setting_key_conference_max_num), "");
//        if (TextUtils.isEmpty(value)) {
//            editor.putString(context.getString(R.string.cloud_setting_key_conference_max_num), mediaChannel.getConfig(JCMediaChannel.CONFIG_CAPACITY));
//        }
//        editor.apply();
//    }

    @Override
    public void onMessageSendUpdate(JCMessageChannelItem jcMessageChannelItem) {
        EventBus.getDefault().post(new JCMessageEvent(true, jcMessageChannelItem));
    }

    @Override
    public void onMessageRecv(JCMessageChannelItem jcMessageChannelItem) {
        EventBus.getDefault().post(new JCMessageEvent(false, jcMessageChannelItem));
    }

    @Override
    public void onFileUpdate(JCStorageItem jcStorageItem) {
        EventBus.getDefault().post(new JCStorageEvent(jcStorageItem));
    }

    @Override
    public void onFetchGroups(int operationId, boolean result, @JCGroup.Reason int reason, List<JCGroupItem> groups, long updateTime, boolean fullUpdated) {
        if (result) {
            JCGroupData.gourpListUpdateTime = updateTime;
            // 演示群列表更新操作，demo是存入内存，实际应同步到数据库
            for (JCGroupItem item : groups) {
                if (item.changeState == JCGroup.GROUP_CHANGE_STATE_ADD) {
                    boolean find = false;
                    for (JCGroupItem temp : JCGroupData.listGroups) {
                        if (TextUtils.equals(temp.groupId, item.groupId)) {
                            find = true;
                            break;
                        }
                    }
                    if (!find) {
                        JCGroupData.listGroups.add(0, item);
                    }
                    // 添加群组则去拉下详情
                    group.fetchGroupInfo(item.groupId, JCGroupData.getFetchGroupInfoLastTime(item.groupId));
                } else if (item.changeState == JCGroup.GROUP_CHANGE_STATE_UPDATE) {
                    for (JCGroupItem temp : JCGroupData.listGroups) {
                        if (TextUtils.equals(temp.groupId, item.groupId)) {
                            JCGroupData.listGroups.remove(temp);
                            break;
                        }
                    }
                    JCGroupData.listGroups.add(0, item);
                } else if (item.changeState == JCGroup.GROUP_CHANGE_STATE_REMOVE) {
                    for (JCGroupItem temp : JCGroupData.listGroups) {
                        if (TextUtils.equals(temp.groupId, item.groupId)) {
                            // 删除该群组缓存
                            JCGroupData.mapGroupMembers.remove(item.groupId);
                            JCGroupData.listGroups.remove(temp);
                            JCGroupData.mapGroupUpdateTime.remove(item.groupId);
                            JCMessageData.removeMessages(item.groupId);
                            break;
                        }
                    }
                }
            }
            EventBus.getDefault().post(new JCEvent(JCEvent.EventType.GROUP_LIST));
        }
    }

    @Override
    public void onFetchGroupInfo(int operationId, boolean result, @JCGroup.Reason int reason, JCGroupItem groupItem, List<JCGroupMember> members, long updateTime, boolean fullUpdated) {
        if (result) {
            // 演示群列表更新操作，demo是存入内存，实际应同步到数据库
            JCGroupData.setFetchGroupInfoLastTime(groupItem.groupId, updateTime);
            List<JCGroupMember> saveMembers = null;
            if (JCGroupData.mapGroupMembers.containsKey(groupItem.groupId)) {
                saveMembers = JCGroupData.mapGroupMembers.get(groupItem.groupId);
            } else {
                saveMembers = new ArrayList<>();
                JCGroupData.mapGroupMembers.put(groupItem.groupId, saveMembers);
            }
            for (JCGroupItem item : JCGroupData.listGroups) {
                if (TextUtils.equals(item.groupId, groupItem.groupId)) {
                    JCGroupData.listGroups.remove(item);
                    JCGroupData.listGroups.add(groupItem);
                    break;
                }
            }
            for (JCGroupMember member : members) {
                if (member.changeState == JCGroup.GROUP_CHANGE_STATE_ADD) {
                    boolean find = false;
                    for (JCGroupMember temp : saveMembers) {
                        if (TextUtils.equals(temp.userId, member.userId)) {
                            find = true;
                            break;
                        }
                    }
                    if (!find) {
                        saveMembers.add(member);
                    }
                } else if (member.changeState == JCGroup.GROUP_CHANGE_STATE_UPDATE) {
                    for (JCGroupMember temp : saveMembers) {
                        if (TextUtils.equals(temp.userId, member.userId)) {
                            saveMembers.remove(temp);
                            break;
                        }
                    }
                    saveMembers.add(member);
                } else if (member.changeState == JCGroup.GROUP_CHANGE_STATE_REMOVE) {
                    for (JCGroupMember temp : saveMembers) {
                        // 删除只能根据uid来进行判断
                        if (TextUtils.equals(temp.uid, member.uid)) {
                            saveMembers.remove(temp);
                            break;
                        }
                    }
                }
            }
            EventBus.getDefault().post(new JCEvent(JCEvent.EventType.GROUP_INFO));
        }
    }

    @Override
    public void onGroupListChange() {
        group.fetchGroups(JCGroupData.gourpListUpdateTime);
    }

    @Override
    public void onGroupInfoChange(String groupId) {
        group.fetchGroupInfo(groupId, JCGroupData.getFetchGroupInfoLastTime(groupId));
    }

    @Override
    public void onCreateGroup(int operationId, boolean result, @JCGroup.Reason int reason, JCGroupItem groupItem) {
        if (!result) {
            Toast.makeText(mContext, "创建群失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdateGroup(int operationId, boolean result, @JCGroup.Reason int reason, String groupId) {
        group.fetchGroupInfo(groupId, JCGroupData.getFetchGroupInfoLastTime(groupId));
    }

    @Override
    public void onDissolve(int operationId, boolean result, @JCGroup.Reason int reason, String groupId) {

    }

    @Override
    public void onLeave(int operationId, boolean result, @JCGroup.Reason int reason, String groupId) {

    }

    @Override
    public void onDealMembers(int operationId, boolean result, @JCGroup.Reason int reason) {
    }

    @Override
    public void onQueryUserStatusResult(int i, boolean result, List<JCAccountItem> list) {
        JCAccountQueryStatusEvent event = new JCAccountQueryStatusEvent(result, list);
        EventBus.getDefault().post(event);
    }

    private static final class JCManagerHolder {
        private static final JCManager INSTANCE = new JCManager();
    }
}
