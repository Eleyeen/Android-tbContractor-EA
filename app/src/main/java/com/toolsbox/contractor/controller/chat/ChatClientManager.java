package com.toolsbox.contractor.controller.chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.interFace.TaskCompletionListener;
import com.toolsbox.contractor.common.model.api.TwilioAccessTokenData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.controller.chat.channels.ChannelManager;
import com.twilio.chat.Channel;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.StatusListener;
import com.twilio.chat.User;

import org.json.JSONException;
import org.json.JSONObject;

import static com.toolsbox.contractor.common.utils.DeviceUtil.getAndroidUniqueID;

public class ChatClientManager implements ChatClientListener{
    private static final String TAG = "ChatClientManager";
    private ChatClient chatClient;
    private Context context;
    private ChatClientBuilder chatClientBuilder;
    private AccessTokenFetcher accessTokenFetcher;


    public ChatClientManager(Context context) {
        this.context = context;
        this.chatClientBuilder = new ChatClientBuilder(this.context);
        this.accessTokenFetcher = new AccessTokenFetcher(this.context);
    }

    public void setClientListener(ChatClientListener listener) {
        if (this.chatClient != null) {
            this.chatClient.setListener(listener);
        }
    }

    public AccessTokenFetcher getAccessTokenFetcher() {
        return accessTokenFetcher;
    }

    public ChatClient getChatClient() {
        return this.chatClient;
    }


    public void setChatClient(ChatClient client) {
        this.chatClient = client;
    }

    public void connectClient(final TaskCompletionListener<Void, String> listener) {
        accessTokenFetcher.fetch(new TaskCompletionListener<String, String>() {
            @Override
            public void onSuccess(String token) {
                buildClient(token, listener);
            }

            @Override
            public void onError(String message) {
                if (listener != null) {
                    listener.onError(message);
                }
            }
        });

    }

    private void buildClient(String token, final TaskCompletionListener<Void, String> listener) {
        chatClientBuilder.build(token, new TaskCompletionListener<ChatClient, String>() {
            @Override
            public void onSuccess(ChatClient chatClient) {
                ChatClientManager.this.chatClient = chatClient;
                ChatClientManager.this.chatClient.setListener(ChatClientManager.this);

                String fcmToken = AppPreferenceManager.getString(Constant.PRE_FCM_TOKEN, "");
                if (!StringHelper.isEmpty(fcmToken)) {
                    setFCMToken(fcmToken);
                }
                listener.onSuccess(null);
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });
    }

    public void setFCMToken(String fcmToken) {
        if (chatClient != null) {
            chatClient.registerFCMToken(fcmToken,
                    new StatusListener() {
                        @Override
                        public void onSuccess() {
                            Log.e(TAG, "Firebase Messaging registration successful");
                        }

                        public void onError(ErrorInfo errorInfo) {
                            Log.e(TAG, "Firebase Messaging registration not successful" + errorInfo.getMessage());
                        }
                    });
        } else {

        }
    }


    public void unsetFCMToken() {
        if (chatClient != null) {
            String fcmToken = AppPreferenceManager.getString(Constant.PRE_FCM_TOKEN, "");
            chatClient.unregisterFCMToken(fcmToken, new StatusListener() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "Firebase Messaging un registration successful");
                }
            });
        }
    }

    public void shutdown() {
        if (chatClient != null) {
            chatClient.shutdown();
        }
    }

    void updateCurrentUserStatus() {
        User currentUser = chatClient.getUsers().getMyUser();
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

    @Override
    public void onChannelJoined(Channel channel) {
        // ChatManager sync
    }

    @Override
    public void onChannelInvited(Channel channel) {

    }

    @Override
    public void onChannelAdded(Channel channel) {
        ChannelManager.getInstance().onChannelAdded(channel);
    }

    @Override
    public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {
        ChannelManager.getInstance().onChannelUpdated(channel, updateReason);
    }

    @Override
    public void onChannelDeleted(Channel channel) {
        ChannelManager.getInstance().onChannelDeleted(channel);
    }

    @Override
    public void onChannelSynchronizationChange(Channel channel) {
        ChannelManager.getInstance().onChannelSynchronizationChange(channel);
    }

    @Override
    public void onError(ErrorInfo errorInfo) {

    }

    @Override
    public void onUserUpdated(User user, User.UpdateReason updateReason) {
        ChannelManager.getInstance().onUserUpdated(user, updateReason);
    }

    @Override
    public void onUserSubscribed(User user) {
        ChannelManager.getInstance().onUserSubscribed(user);
    }

    @Override
    public void onUserUnsubscribed(User user) {

    }

    @Override
    public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {
        Log.e(TAG, "onClientSynchronization" + synchronizationStatus);
        if (synchronizationStatus == ChatClient.SynchronizationStatus.COMPLETED) {
            //Set Attribute for User to show avatar image and name
            updateCurrentUserStatus();

            // ChatManager sync
            ChannelManager.getInstance().channelsObject = chatClient.getChannels();
            ChannelManager.getInstance().channelsUsers = chatClient.getUsers();
            ChannelManager.getInstance().populateChannels();
        }

    }

    @Override
    public void onNewMessageNotification(String s, String s1, long l) {
        ChannelManager.getInstance().onNewMessageNotification(s, s1, l);
    }

    @Override
    public void onAddedToChannelNotification(String s) {
        ChannelManager.getInstance().onAddedToChannelNotification(s);
    }

    @Override
    public void onInvitedToChannelNotification(String s) {
        ChannelManager.getInstance().onInvitedToChannelNotification(s);
    }

    @Override
    public void onRemovedFromChannelNotification(String s) {
        ChannelManager.getInstance().onRemovedFromChannelNotification(s);
    }

    @Override
    public void onNotificationSubscribed() {
        ChannelManager.getInstance().onNotificationSubscribed();
    }

    @Override
    public void onNotificationFailed(ErrorInfo errorInfo) {

    }

    @Override
    public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {
        ChannelManager.getInstance().onConnectionStateChange(connectionState);
    }

    @Override
    public void onTokenExpired() {
        Log.e(TAG, "onTokenExpired");
        getAccessTokenFetcher().fetch(new TaskCompletionListener<String, String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "new Access token" + s);
                chatClient.updateToken(s, new StatusListener() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "Update Expired Token success");
                    }
                });
            }

            @Override
            public void onError(String s) {
                Log.e(TAG, "Get AccessToken error" + s);
            }
        });
    }

    @Override
    public void onTokenAboutToExpire() {

    }
}
