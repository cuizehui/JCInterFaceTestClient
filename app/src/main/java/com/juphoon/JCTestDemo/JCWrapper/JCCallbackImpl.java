package com.juphoon.JCTestDemo.JCWrapper;


import com.juphoon.cloud.JCAccountCallback;
import com.juphoon.cloud.JCAccountItem;
import com.juphoon.cloud.JCCallCallback;
import com.juphoon.cloud.JCCallItem;
import com.juphoon.cloud.JCClientCallback;
import com.juphoon.cloud.JCGroupCallback;
import com.juphoon.cloud.JCGroupItem;
import com.juphoon.cloud.JCGroupMember;
import com.juphoon.cloud.JCMediaChannel;
import com.juphoon.cloud.JCMediaChannelCallback;
import com.juphoon.cloud.JCMediaChannelParticipant;
import com.juphoon.cloud.JCMediaChannelQueryInfo;
import com.juphoon.cloud.JCMediaDeviceCallback;
import com.juphoon.cloud.JCMessageChannelCallback;
import com.juphoon.cloud.JCMessageChannelItem;
import com.juphoon.cloud.JCStorageCallback;
import com.juphoon.cloud.JCStorageItem;

import java.util.List;

public class JCCallbackImpl implements JCClientCallback, JCCallCallback, JCMediaChannelCallback, JCMediaDeviceCallback,
        JCStorageCallback, JCGroupCallback, JCMessageChannelCallback, JCAccountCallback {

    @Override
    public void onQueryUserStatusResult(int i, boolean b, List<JCAccountItem> list) {

    }

    @Override
    public void onCallItemAdd(JCCallItem jcCallItem) {

    }

    @Override
    public void onCallItemRemove(JCCallItem jcCallItem, int i, String s) {

    }

    @Override
    public void onCallItemUpdate(JCCallItem jcCallItem, JCCallItem.ChangeParam changeParam) {

    }

    @Override
    public void onMessageReceive(String s, String s1, JCCallItem jcCallItem) {

    }

    @Override
    public void onLogin(boolean b, int i) {

    }

    @Override
    public void onLogout(int i) {

    }

    @Override
    public void onClientStateChange(int i, int i1) {

    }

    @Override
    public void onFetchGroups(int i, boolean b, int i1, List<JCGroupItem> list, long l, boolean b1) {

    }

    @Override
    public void onFetchGroupInfo(int i, boolean b, int i1, JCGroupItem jcGroupItem, List<JCGroupMember> list, long l, boolean b1) {

    }

    @Override
    public void onGroupListChange() {

    }

    @Override
    public void onGroupInfoChange(String s) {

    }

    @Override
    public void onCreateGroup(int i, boolean b, int i1, JCGroupItem jcGroupItem) {

    }

    @Override
    public void onUpdateGroup(int i, boolean b, int i1, String s) {

    }

    @Override
    public void onDissolve(int i, boolean b, int i1, String s) {

    }

    @Override
    public void onLeave(int i, boolean b, int i1, String s) {

    }

    @Override
    public void onDealMembers(int i, boolean b, int i1) {

    }

    @Override
    public void onMediaChannelStateChange(int i, int i1) {

    }

    @Override
    public void onMediaChannelPropertyChange(JCMediaChannel.PropChangeParam propChangeParam) {

    }

    @Override
    public void onJoin(boolean b, int i, String s) {

    }

    @Override
    public void onLeave(int i, String s) {

    }

    @Override
    public void onStop(boolean b, int i) {

    }

    @Override
    public void onQuery(int i, boolean b, int i1, JCMediaChannelQueryInfo jcMediaChannelQueryInfo) {

    }

    @Override
    public void onParticipantJoin(JCMediaChannelParticipant jcMediaChannelParticipant) {

    }

    @Override
    public void onParticipantLeft(JCMediaChannelParticipant jcMediaChannelParticipant) {

    }

    @Override
    public void onParticipantUpdate(JCMediaChannelParticipant jcMediaChannelParticipant, JCMediaChannelParticipant.ChangeParam changeParam) {

    }

    @Override
    public void onMessageReceive(String s, String s1, String s2) {

    }

    @Override
    public void onInviteSipUserResult(int i, boolean b, int i1) {

    }

    @Override
    public void onCameraUpdate() {

    }

    @Override
    public void onAudioOutputTypeChange(boolean b) {

    }

    @Override
    public void onMessageSendUpdate(JCMessageChannelItem jcMessageChannelItem) {

    }

    @Override
    public void onMessageRecv(JCMessageChannelItem jcMessageChannelItem) {

    }

    @Override
    public void onFileUpdate(JCStorageItem jcStorageItem) {

    }
}
