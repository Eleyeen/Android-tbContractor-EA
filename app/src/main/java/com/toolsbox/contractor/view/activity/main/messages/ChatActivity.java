package com.toolsbox.contractor.view.activity.main.messages;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.TBApplication;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.TaskCompletionListener;
import com.toolsbox.contractor.common.model.ChatEntityInfo;
import com.toolsbox.contractor.common.utils.FileUtils;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.controller.chat.ChatClientManager;
import com.toolsbox.contractor.controller.chat.channels.ChannelManager;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.adapter.ChatAdapter;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelListener;
import com.twilio.chat.Channels;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Member;
import com.twilio.chat.Message;
import com.twilio.chat.Messages;
import com.twilio.chat.ProgressListener;
import com.twilio.chat.StatusListener;
import com.twilio.chat.User;
import com.twilio.chat.UserDescriptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends BaseActivity implements View.OnClickListener, TextWatcher, ChannelListener, ChatAdapter.callbackZoomImage {
    private static final String TAG = "ChatActivity";
    private static final int REQUEST_CODE_SELECT_PICTURE = 11;

    private Toolbar toolbar;
    private FloatingActionButton btnSend;
    private EditText etMessage;
    private ImageButton btnAdd;
    private TextView tvName, tvStatus;
    private CircleImageView ivPhoto;
    private ListView listView;
    private ChatAdapter adapter;
    private ArrayList<ChatEntityInfo> arrMessages = new ArrayList<>();

    String userStatus;
    String channelSid;
    Channel currentChannel;
    Messages messagesObject;
    ChatClient chatClient;
    ChatClientManager chatClientManager;
    User currentUser;
    UserReachabilityReceiver reachabilityReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initVariable();
        initUI();
        loadChannelAndMessages();
    }

    void initVariable(){
        channelSid = getIntent().getStringExtra("sid");
        chatClientManager = TBApplication.get().getChatClientManager();
        chatClient = chatClientManager.getChatClient();

        reachabilityReceiver = new UserReachabilityReceiver();
        registerReceiver(reachabilityReceiver, new IntentFilter(Constant.ACTION_CHAT_USER_REACHABILITY));
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        listView = findViewById(R.id.message_list);
        tvName = findViewById(R.id.tv_name);
        tvStatus = findViewById(R.id.tv_status);
        btnSend = findViewById(R.id.btn_send);
        ivPhoto = findViewById(R.id.iv_photo);
        etMessage = findViewById(R.id.et_message);
        etMessage.addTextChangedListener(this);
        btnAdd = findViewById(R.id.btn_add);
        btnSend.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        adapter = new ChatAdapter(this, arrMessages);
        adapter.setImageZoomCallback(this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TBApplication.setActiveChannelSid(channelSid);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TBApplication.setActiveChannelSid("");
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void showAttachOption(){
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.profile_photo));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {captureImageIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_SELECT_PICTURE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                sendPlainMessage();
                break;

            case R.id.btn_add:
                showAttachOption();
                break;
        }
    }

    void sendPlainMessage(){
        if (StringHelper.isEmpty(getMessage()))
            return;
        if (currentChannel == null)
            return;
        if (messagesObject == null) {
            messagesObject = currentChannel.getMessages();
        }
        Message.Options messageOptions = Message.options().withBody(getMessage());
        this.messagesObject.sendMessage(messageOptions, null);
        ChatEntityInfo newMessage = new ChatEntityInfo(ChatEntityInfo.MESSAGE_TYPE.TEXT, ChatEntityInfo.USER_TYPE.I, getMessage(), new Date(), GlobalUtils.getChatIdentity(), false);
        arrMessages.add(newMessage);
        adapter.notifyDataSetChanged();
        clearText();
    }

    void sendImage(File file){
        if (currentChannel == null)
            return;
        if (messagesObject == null) {
            messagesObject = currentChannel.getMessages();
        }
        Uri fileUri = Uri.fromFile(file);
        // show image loading
        ChatEntityInfo newItem = new ChatEntityInfo(ChatEntityInfo.MESSAGE_TYPE.IMAGE, ChatEntityInfo.USER_TYPE.I, fileUri.toString(), new Date(), GlobalUtils.getChatIdentity(), true);
        arrMessages.add(newItem);
        adapter.notifyDataSetChanged();
        try {
            messagesObject.sendMessage(
                    Message.options()
                            .withMedia(new FileInputStream(file), "image/png")
                            .withMediaFileName(file.getName())
                            .withMediaProgressListener(new ProgressListener() {
                                @Override
                                public void onStarted() {
                                    Log.e(TAG, "fileUpload started");
                                }

                                @Override
                                public void onProgress(long bytes) {
                                    Log.e(TAG, "Uploaded " + bytes + " bytes");
                                }

                                @Override
                                public void onCompleted(String s) {
                                    Log.e(TAG, "fileUpload completed");
                                }
                            }),
                    new CallbackListener<Message>() {
                        @Override
                        public void onSuccess(Message message) {
                            Log.e(TAG, "Successfully sent MEDIA message");
                            // hide image loading
                            newItem.isLoading = false;
                            adapter.notifyDataSetChanged();

                        }
                        @Override
                        public void onError(ErrorInfo error) {
                            Log.e(TAG, "Error sending MEDIA message");

                        }
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    void loadChannelAndMessages() {
        if (chatClient != null) {
            Channels channelsObject = chatClient.getChannels();
            showProgressDialog();
            channelsObject.getChannel(channelSid, new CallbackListener<Channel>() {
                @Override
                public void onSuccess(Channel channel) {
                    currentChannel = channel;
                    currentChannel.addListener(ChatActivity.this);
                    messagesObject = currentChannel.getMessages();
                    hideProgressDialog();
                    loadUnreadMessages();
                }

                public void onError(ErrorInfo errorInfo) {
                    hideProgressDialog();
                    Toast.makeText(ChatActivity.this, "can not find sid " + errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            // need to initialize chatClient object.
            initializeClient();
        }
    }

    boolean checkActionType(Intent data) {
        boolean isCamera = true;
        if (data != null ) {
            String action = data.getAction();
            if ((data.getData() == null) && (data.getClipData() == null)) {
                isCamera = true;
            } else {
                isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }
        }
        return isCamera;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_PICTURE:
                    if (checkActionType(data)) {  // camera
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        File originFile =  FileUtils.saveImage(photo);
                        Log.e(TAG, "camera path=" + originFile.getAbsolutePath());
                        sendImage(originFile);
                    } else {
                        if (data.getData() != null){
                            try {
                                final Uri imageUri = data.getData();
                                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                File originFile =  FileUtils.saveImage(selectedImage);
                                Log.e(TAG, "gallery path=" + originFile.getAbsolutePath());
                                sendImage(originFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(ChatActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    break;
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentChannel != null)
            currentChannel.removeListener(this);
        if (reachabilityReceiver != null) {
            unregisterReceiver(reachabilityReceiver);
            reachabilityReceiver = null;
        }
    }

    private void initializeClient() {
        showProgressDialog();
        chatClientManager.connectClient(new TaskCompletionListener<Void, String>() {
            @Override
            public void onSuccess(Void aVoid) {
                chatClient = TBApplication.get().getChatClientManager().getChatClient();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressDialog();
                                loadChannelAndMessages();
                            }
                        });

                    }
                }, 2000);
            }

            @Override
            public void onError(String errorMessage) {
                hideProgressDialog();
                Toast.makeText(ChatActivity.this, "Create ChatClient fail.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void loadUnreadMessages(){
        // load endpoint info
        String endpointIdentity = GlobalUtils.getEndpointIdentity(currentChannel.getUniqueName());
        ChannelManager.getInstance().getChannelUserInstance().getAndSubscribeUser(endpointIdentity, new CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                updateUserStatus(user);
            }
        });

        currentChannel.getMessagesCount(new CallbackListener<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                if (messagesObject == null) {
                    messagesObject = currentChannel.getMessages();
                }

                messagesObject.getLastMessages(100, new CallbackListener<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> messageList) {
                        addMessages(messageList);
                        confirmAllMessage();
                    }
                });

            }

            public void onError(ErrorInfo errorInfo) {
                Log.e(TAG, "*****************************************loadUnreadMessages" + errorInfo.getMessage());

            }
        });
    }

    void addNewMessage(Message item){
        ChatEntityInfo.USER_TYPE senderType =  GlobalUtils.isMe(item.getAuthor()) ? ChatEntityInfo.USER_TYPE.I : ChatEntityInfo.USER_TYPE.YOU;
        if (item.hasMedia()) {
            Message.Media media = item.getMedia();
            String type = media.getType();
            if (type.contains("image")) {
                ChatEntityInfo entity;
                if (FileUtils.existFileInChatDirectory(media.getFileName())) {
                    File existFile = FileUtils.getFileInChatDirectory(media.getFileName());
                    Uri fileUri = Uri.fromFile(existFile);
                    entity = new ChatEntityInfo(ChatEntityInfo.MESSAGE_TYPE.IMAGE, senderType, fileUri.toString(), item.getDateCreatedAsDate(), item.getAuthor(), false);
                    arrMessages.add(entity);
                    adapter.notifyDataSetChanged();
                } else {
                    entity = new ChatEntityInfo(ChatEntityInfo.MESSAGE_TYPE.IMAGE, senderType, "", item.getDateCreatedAsDate(), item.getAuthor(), true);
                    arrMessages.add(entity);
                    adapter.notifyDataSetChanged();
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    media.download(out, new StatusListener() {
                        @Override
                        public void onSuccess() {
                            File file = null;
                            if (type.contains("image")){
                                file = new File(FileUtils.getFileDir(getApplicationContext()), media.getFileName());
                            }
                            if (file == null)
                                return;
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(out.toByteArray());
                                fileOutputStream.flush();
                                fileOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Uri fileUri = Uri.fromFile(file);
                            Log.e(TAG, "-------------file Uri path : " + fileUri.toString());
                            entity.text = fileUri.toString();
                            entity.isLoading = false;
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(ErrorInfo error) {
                            Log.e(TAG, "Error downloading media");
                            entity.isLoading = false;
                            adapter.notifyDataSetChanged();
                        }
                    }, new ProgressListener() {
                        @Override
                        public void onStarted() {
                            entity.isLoading = true;
                        }

                        @Override
                        public void onProgress(long bytes) {
                            Log.e(TAG, "Downloaded " + bytes + " bytes");
                        }

                        @Override
                        public void onCompleted(String mediaSid) {
                            Log.e(TAG, "Download completed");
                        }
                    });
                }
            }
        } else {
            ChatEntityInfo entity = new ChatEntityInfo(ChatEntityInfo.MESSAGE_TYPE.TEXT, senderType, item.getMessageBody(), item.getDateCreatedAsDate(), item.getAuthor(), false);
            arrMessages.add(entity);
            adapter.notifyDataSetChanged();
        }
    }

    void addMessages(List<Message> messageList) {
        for (Message item : messageList) {
            addNewMessage(item);
        }
    }

    void updateUserStatus(User user){
        try {
            JSONObject att = user.getAttributes();
            String  channelName = att.getString("name");
            String  imageURL = att.getString("imageURL");
            if (!StringHelper.isEmpty(imageURL)){
                Picasso.get().load(imageURL).into(ivPhoto);
            }
            tvName.setText(channelName);
            userStatus = user.isOnline()? "online" : "offline";
            tvStatus.setText(userStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void confirmAllMessage(){
        Log.e(TAG, "confirmAllMessage");
        if (currentChannel == null)
            return;
        if (currentChannel.getMessages() != null){
            currentChannel.getMessages().setAllMessagesConsumedWithResult(new CallbackListener<Long>() {
                @Override
                public void onSuccess(Long aLong) {
                    Log.e(TAG, "Confirm message, need to notify chat list activity");
                    ChannelManager.getInstance().updateChannelWithUnreadMsg(currentChannel);
                }
            });
        }
    }


    private String getMessage() {
        return etMessage.getText().toString();
    }


    private void clearText() {
        etMessage.setText("");
    }

    // Text Watch

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (currentChannel != null){
            currentChannel.typing();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    // Channel Listener

    @Override
    public void onMessageAdded(Message message) {
        Log.e(TAG, "onMessageAdd" + message.getMessageBody());
        if (!GlobalUtils.isMe(message.getAuthor())) {
            addNewMessage(message);
        }
        confirmAllMessage();
    }

    @Override
    public void onMessageUpdated(Message message, Message.UpdateReason updateReason) {

    }

    @Override
    public void onMessageDeleted(Message message) {

    }

    @Override
    public void onMemberAdded(Member member) {

    }

    @Override
    public void onMemberUpdated(Member member, Member.UpdateReason updateReason) {

    }

    @Override
    public void onMemberDeleted(Member member) {

    }

    @Override
    public void onTypingStarted(Channel channel, Member member) {
        if (channel.getSid().equals(currentChannel.getSid())){
            tvStatus.setText("typing...");
        }
    }

    @Override
    public void onTypingEnded(Channel channel, Member member) {
        if (channel.getSid().equals(currentChannel.getSid())){
            tvStatus.setText(userStatus);
        }
    }

    @Override
    public void onSynchronizationChanged(Channel channel) {

    }

    @Override
    public void onZoomImage(String filePath) {
        // click on Image Message
        String[] list = new String[]{filePath};

        new StfalconImageViewer.Builder<>(this, list, new ImageLoader<String>() {
            @Override
            public void loadImage(ImageView imageView, String image) {
                //load your image here
                Picasso.get().load(image).into(imageView);
            }
        }).show();

    }

    // Register Receiver for updating favourites
    private class UserReachabilityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean status = intent.getBooleanExtra("status", false);
            String identity = intent.getStringExtra("identity");
            if (currentUser.getIdentity().equals(identity)){
                userStatus = status? "online" : "offline";
                tvStatus.setText(userStatus);
            }
        }
    }


}

