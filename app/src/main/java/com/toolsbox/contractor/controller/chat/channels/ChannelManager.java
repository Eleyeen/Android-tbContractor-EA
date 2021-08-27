package com.toolsbox.contractor.controller.chat.channels;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.toolsbox.contractor.TBApplication;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.ChannelChangeListener;
import com.toolsbox.contractor.common.interFace.LoadChannelListener;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.interFace.TaskCompletionListener;
import com.toolsbox.contractor.common.interFace.UnreadMsgListener;
import com.toolsbox.contractor.common.model.ChatContactInfo;
import com.toolsbox.contractor.common.model.api.TwilioAccessTokenData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.controller.chat.ChatClientManager;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.Channel.ChannelType;
import com.twilio.chat.ChannelDescriptor;
import com.twilio.chat.Channels;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Message;
import com.twilio.chat.Messages;
import com.twilio.chat.Paginator;
import com.twilio.chat.StatusListener;
import com.twilio.chat.User;
import com.twilio.chat.Users;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.toolsbox.contractor.common.utils.DeviceUtil.getAndroidUniqueID;

public class ChannelManager implements ChatClientListener {
    private static final String TAG = "ChannelManager";

    private static ChannelManager sharedManager = new ChannelManager();
    private ChatClientManager chatClientManager;
    public List<Channel> channels;
    public List<ChatContactInfo> arrContactInfo;
    public Channels channelsObject;
    public Users channelsUsers;
    private ChatClientListener listener;
    private Handler handler;
    private ChannelChangeListener channelChangeListener;
    private UnreadMsgListener unreadMsgListener;
    private boolean isSyncedChannels;

    private ChannelManager() {
        this.chatClientManager = TBApplication.get().getChatClientManager();
        this.listener = this;
        this.channelChangeListener = null;
        this.channels = new ArrayList<>();
        this.arrContactInfo = new ArrayList<>();
        handler = setupListenerHandler();
        isSyncedChannels = false;
    }


    public static ChannelManager getInstance() {
        return sharedManager;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public boolean isSyncedChannels(){
        return isSyncedChannels;
    }

    public List<ChatContactInfo> getContactInfo() {
        return arrContactInfo;
    }

    public void leaveChannelWithHandler(Channel channel, StatusListener handler) {
        channel.leave(handler);
    }

    public void deleteChannelWithHandler(Channel channel, StatusListener handler) {
        channel.destroy(handler);
    }

    public Users getChannelUserInstance() {
        return channelsUsers;
    }

    public void setChannelChangeListener(ChannelChangeListener channelChangeListener) {
        this.channelChangeListener = channelChangeListener;
    }

    public void setUnreadMsgListener(UnreadMsgListener unreadMsgListener){
        this.unreadMsgListener = unreadMsgListener;
    }

    public void populateChannels() {
        if (this.chatClientManager == null) {
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                isSyncedChannels = true;
                channels = new ArrayList<>();
                arrContactInfo = new ArrayList<>();
                channels = channelsObject.getSubscribedChannelsSortedBy(Channels.SortCriterion.LAST_MESSAGE, Channels.SortOrder.DESCENDING);

                for (int i = 0; i < channels.size(); i++) {
                    ChatContactInfo item = new ChatContactInfo();
                    item.sid = channels.get(i).getSid();
                    item.unique_name = channels.get(i).getUniqueName();
                    item.last_date = channels.get(i).getLastMessageDate();
                    arrContactInfo.add(item);
                }

                if (channelChangeListener != null) {
                    channelChangeListener.onReloadChannel();
                }

                for (int i = 0; i < arrContactInfo.size(); i++) {
                    int finalI = i;

                    // User image, name
                    String endpointIdentity = GlobalUtils.getEndpointIdentity(arrContactInfo.get(i).unique_name);
                    channelsUsers.getAndSubscribeUser(endpointIdentity, new CallbackListener<User>() {
                        @Override
                        public void onSuccess(User user) {
                            try {
                                JSONObject att = user.getAttributes();
                                String name = att.getString("name");
                                String imageURL = att.getString("imageURL");
                                arrContactInfo.get(finalI).contact_name = name;
                                arrContactInfo.get(finalI).contact_photo = imageURL;
                                // delegate
                                if (channelChangeListener != null) {
                                    channelChangeListener.onUpdateChannel(finalI);
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "getAndSubscribeUser fail" + e.getLocalizedMessage());
                                e.printStackTrace();
                            }
                        }
                    });

                    // unread message
                    Channel itemChannel = fetchChannel(arrContactInfo.get(i).sid);
                    itemChannel.getUnconsumedMessagesCount(new CallbackListener<Long>() {
                        @Override
                        public void onSuccess(Long aLong) {
                            int unreadMessage = aLong.intValue();
                            arrContactInfo.get(finalI).n_unread_msg = unreadMessage;
                            if (finalI == arrContactInfo.size() - 1){
                                notifyUnreadMsgNumber();
                            }
                            // delegate
                            if (channelChangeListener != null) {
                                channelChangeListener.onUpdateChannel(finalI);
                            }
                        }
                    });


                    // last message
                    Messages messageObject = itemChannel.getMessages();
                    if (messageObject == null) {
                        Log.e(TAG, "messageObject == null");
                    } else {
                        messageObject.getLastMessages(1, new CallbackListener<List<Message>>() {
                            @Override
                            public void onSuccess(List<Message> messages) {
                                if (messages.size() > 0) {
                                    arrContactInfo.get(finalI).last_msg_type = messages.get(0).getType() == Message.Type.TEXT ? 0 : 1;
                                    arrContactInfo.get(finalI).last_msg = messages.get(0).getMessageBody();
                                    if (messages.get(0).getType() == Message.Type.MEDIA) {
                                        if ( messages.get(0).getMedia().getType().contains("image")) {
                                            arrContactInfo.get(finalI).last_msg = "Photo";
                                        } else if (messages.get(0).getMedia().getType().contains("video")) {
                                            arrContactInfo.get(finalI).last_msg = "Video";
                                        }
                                    }
                                    // delegate
                                    if (channelChangeListener != null) {
                                        channelChangeListener.onUpdateChannel(finalI);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void updateTwilioUserInfo(){
        if (channelsUsers == null)
            return;
        User currentUser = channelsUsers.getMyUser();
        String displayName = AppPreferenceManager.getString(Constant.PRE_BUSINESS_NAME, "");
        if (StringHelper.isEmpty(displayName))
            displayName = AppPreferenceManager.getString(Constant.PRE_EMAIL, "");
        String photo = AppPreferenceManager.getString(Constant.PRE_IMAGE_URL, "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", displayName);
            jsonObject.put("imageURL", photo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        currentUser.setAttributes(jsonObject, new StatusListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "Set Attribute Successfully");
            }
        });
    }

    void addNewChannel(Channel channel){
        Log.e(TAG, "addNewChannel");
        Channel currentChannel = fetchChannel(channel.getSid());
        if (currentChannel == null){
            channels.add(channel);

            ChatContactInfo item = new ChatContactInfo();
            item.sid = channel.getSid();
            item.unique_name = channel.getUniqueName();
            item.last_date = channel.getLastMessageDate();
            arrContactInfo.add(item);

            // User image, name
            String endpointIdentity = GlobalUtils.getEndpointIdentity(channel.getUniqueName());
            channelsUsers.getAndSubscribeUser(endpointIdentity, new CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    try {
                        JSONObject att = user.getAttributes();
                        String name = att.getString("name");
                        String imageURL = att.getString("imageURL");
                        item.contact_name = name;
                        item.contact_photo = imageURL;

                        if (channelChangeListener != null) {
                            channelChangeListener.onReloadChannel();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "getAndSubscribeUser fail" + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            });

        }
    }


    void updateChannelUserProperty(User user){
        Log.e(TAG, "User Updated!" + user.getIdentity());

        // Make unique_name
        if (StringHelper.isEmpty(user.getIdentity()) || GlobalUtils.getChatIdentity().equals(user.getIdentity()))
            return;

        int endpointType = user.getIdentity().contains("C") ? 0 : 1;
        String prefix = endpointType == 0 ? "C" : "S";
        int endpointId = Integer.valueOf(user.getIdentity().replace(prefix, ""));

        String channelUniqueName = GlobalUtils.makeChatUniqueName(endpointId, endpointType);
        for (int i = 0; i < arrContactInfo.size(); i++){
            if (arrContactInfo.get(i).unique_name.equals(channelUniqueName)){
                try {
                    JSONObject att = user.getAttributes();
                    String name = att.getString("name");
                    String imageURL = att.getString("imageURL");
                    arrContactInfo.get(i).contact_name = name;
                    arrContactInfo.get(i).contact_photo = imageURL;
                    // delegate
                    if (channelChangeListener != null) {
                        channelChangeListener.onUpdateChannel(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void updateChannelWithNewMessage(Channel channel){
        int index = indexofChannel(channel.getUniqueName());
        if (index > -1){
            arrContactInfo.get(index).last_date = channel.getLastMessageDate();

            Messages messageObject = channel.getMessages();
            if (messageObject == null) {
                Log.e(TAG, "messageObject == null");
            } else {
                messageObject.getLastMessages(1, new CallbackListener<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> messages) {
                        if (messages.size() > 0) {
                            arrContactInfo.get(index).last_msg_type = messages.get(0).getType() == Message.Type.TEXT ? 0 : 1;
                            arrContactInfo.get(index).last_msg = messages.get(0).getMessageBody();

                        }
                    }
                });

                channel.getUnconsumedMessagesCount(new CallbackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        int unreadMessage = aLong.intValue();
                        arrContactInfo.get(index).n_unread_msg = unreadMessage;
                        sortChannels();
                        notifyUnreadMsgNumber();
                        // delegate
                        if (channelChangeListener != null) {
                            channelChangeListener.onReloadChannel();
                        }
                    }
                });
            }
        }
    }

    public void updateChannelWithUnreadMsg(Channel channel){
        int index = indexofChannel(channel.getUniqueName());
        if (index > -1){
            arrContactInfo.get(index).last_date = channel.getLastMessageDate();

            Messages messageObject = channel.getMessages();
            if (messageObject == null) {
                Log.e(TAG, "messageObject == null");
            } else {
                channel.getUnconsumedMessagesCount(new CallbackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        int unreadMessage = aLong.intValue();
                        arrContactInfo.get(index).n_unread_msg = unreadMessage;
                        sortChannels();
                        notifyUnreadMsgNumber();
                        // delegate
                        if (channelChangeListener != null) {
                            channelChangeListener.onReloadChannel();
                        }
                    }
                });
            }
        }
    }

    public Channel fetchChannel(String sid) {
        Channel returnValue = null;
        for (Channel item : channels) {
            if (item.getSid().equals(sid)) {
                returnValue = item;
            }
        }
        return returnValue;
    }

    int indexofChannel(String uniqueName){
        int returnValue = -1;
        for (int i = 0; i < arrContactInfo.size(); i ++){
            if (arrContactInfo.get(i).unique_name.equals(uniqueName))
                returnValue = i;
        }

        return returnValue;
    }

    void sortChannels(){
        Collections.sort(arrContactInfo, new Comparator<ChatContactInfo>() {
            @Override
            public int compare(ChatContactInfo o1, ChatContactInfo o2) {
                return o2.last_date.compareTo(o1.last_date);
            }
        });
    }

    void notifyUnreadMsgNumber(){
        if (unreadMsgListener != null)
            unreadMsgListener.onUpdateBadge(totalUnreadMsgNumber());
    }

    int totalUnreadMsgNumber(){
        int total = 0;
        for (ChatContactInfo item : arrContactInfo){
            total = total + item.n_unread_msg;
        }
        return total;
    }

    public void joinOrCreateChannelWithCompletion(int endpointId, int endpointType, final CallbackListener<Channel> listener) {
        String channelUniqueName = GlobalUtils.makeChatUniqueName(endpointId, endpointType);
        String endpointIdentity = GlobalUtils.getChatIdentityById(endpointId, endpointType);
        if (channelsObject == null) {
            listener.onError(new ErrorInfo(0, "Channel did not initialized! Please check network connection."));
            return;
        }
        Log.e(TAG, "Channel Unique name:" + channelUniqueName);
        channelsObject.getChannel(channelUniqueName, new CallbackListener<Channel>() {
            @Override
            public void onSuccess(Channel channel) {
                if (channel != null) {
                    channel.getMembersCount(new CallbackListener<Long>() {
                        @Override
                        public void onSuccess(Long aLong) {
                            int currentMemberCount = aLong.intValue();
                            if (currentMemberCount == 0) {
                                Log.e(TAG, "Current Channel member is 0");
                                channel.getMembers().addByIdentity(endpointIdentity, new StatusListener() {
                                    @Override
                                    public void onSuccess() {
                                        Log.e(TAG, "endpoint" + endpointIdentity + "  is added successfully");
                                    }
                                });
                            }
                        }
                    });
                    if (channel.getStatus() == Channel.ChannelStatus.JOINED) {
                        listener.onSuccess(channel);
                    } else {
                        joinChannelWithCompletion(channel, listener);
                    }

                } else {
                    createChannelWithCompletion(endpointId, endpointType, listener);
                }
            }

            public void onError(ErrorInfo errorInfo) {
                Log.e(TAG, "getChannel Error:" + errorInfo.getMessage() + "error code:" + errorInfo.getCode() + "status:" + errorInfo.getStatus());
                createChannelWithCompletion(endpointId, endpointType, listener);
            }
        });
    }

    private void joinChannelWithCompletion(Channel channel, final CallbackListener<Channel> listener) {
        channel.join(new StatusListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess(channel);
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                listener.onError(errorInfo);
            }
        });
    }

    private void createChannelWithCompletion(int endpointId, int endpointType, final CallbackListener<Channel> listener) {
        String channelUniqueName = GlobalUtils.makeChatUniqueName(endpointId, endpointType);
        String endpointIdentity = GlobalUtils.getChatIdentityById(endpointId, endpointType);
        this.channelsObject
                .channelBuilder()
                .withFriendlyName(channelUniqueName)
                .withUniqueName(channelUniqueName)
                .withType(ChannelType.PRIVATE)
                .build(new CallbackListener<Channel>() {
                    @Override
                    public void onSuccess(final Channel channel) {
                        ChannelManager.this.channels.add(channel);
                        joinChannelWithCompletion(channel, listener);

                        // Add endpoint in this channel, not invite
                        channel.getMembers().addByIdentity(endpointIdentity, new StatusListener() {
                            @Override
                            public void onSuccess() {
                                Log.e(TAG, "endpoint" + endpointIdentity + "  is added successfully");
                            }
                        });
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        listener.onError(errorInfo);
                    }
                });
    }

    public void setChannelListener(ChatClientListener listener) {
        this.listener = listener;
    }

    private String getStringResource(int id) {
        Resources resources = TBApplication.get().getResources();
        return resources.getString(id);
    }


    @Override
    public void onChannelAdded(Channel channel) {
        if (listener != null) {
            listener.onChannelAdded(channel);
        }
    }

    @Override
    public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {
        if (listener != null) {
            listener.onChannelUpdated(channel, updateReason);
        }
        if (updateReason == Channel.UpdateReason.LAST_MESSAGE){
            updateChannelWithNewMessage(channel);
        }
    }

    @Override
    public void onChannelDeleted(Channel channel) {
        if (listener != null) {
            listener.onChannelDeleted(channel);
        }
    }

    @Override
    public void onChannelSynchronizationChange(Channel channel) {
        if (listener != null) {
            listener.onChannelSynchronizationChange(channel);
        }
        Log.e(TAG, "onChannelSynchronizationChange");
        if (channel.getSynchronizationStatus() == Channel.SynchronizationStatus.ALL){
            addNewChannel(channel);
        }
    }

    @Override
    public void onError(ErrorInfo errorInfo) {
        if (listener != null) {
            listener.onError(errorInfo);
        }
    }

    @Override
    public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {
        if (listener != null) {
            listener.onClientSynchronization(synchronizationStatus);
        }

    }

    @Override
    public void onChannelJoined(Channel channel) {
        if (listener != null) {
            listener.onChannelJoined(channel);
        }
    }

    @Override
    public void onChannelInvited(Channel channel) {

    }

    @Override
    public void onUserUpdated(User user, User.UpdateReason updateReason) {
        if (updateReason == User.UpdateReason.ATTRIBUTES){
            updateChannelUserProperty(user);
        }
        if (listener != null) {
            listener.onUserUpdated(user, updateReason);
        }
    }

    @Override
    public void onUserSubscribed(User user) {

    }

    @Override
    public void onUserUnsubscribed(User user) {

    }

    @Override
    public void onNewMessageNotification(String s, String s1, long l) {
        Log.e(TAG, "onNewMessageNotification:" + s + ":" + s1);
    }

    @Override
    public void onAddedToChannelNotification(String s) {

    }

    @Override
    public void onInvitedToChannelNotification(String s) {

    }

    @Override
    public void onRemovedFromChannelNotification(String s) {

    }

    @Override
    public void onNotificationSubscribed() {

    }

    @Override
    public void onNotificationFailed(ErrorInfo errorInfo) {

    }

    @Override
    public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {

    }

    // Access Token

    @Override
    public void onTokenExpired() {
        Log.e(TAG, "onTokenExpired");

    }

    @Override
    public void onTokenAboutToExpire() {

    }

    private Handler setupListenerHandler() {
        Looper looper;
        Handler handler;
        if ((looper = Looper.myLooper()) != null) {
            handler = new Handler(looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            handler = new Handler(looper);
        } else {
            throw new IllegalArgumentException("Channel Listener must have a Looper.");
        }
        return handler;
    }
}
