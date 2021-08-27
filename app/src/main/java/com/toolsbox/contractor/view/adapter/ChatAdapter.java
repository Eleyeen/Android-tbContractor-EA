package com.toolsbox.contractor.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.model.ChatEntityInfo;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.view.customUI.chat.ImageMessageView;
import com.toolsbox.contractor.view.customUI.chat.MessageView;
import com.toolsbox.contractor.view.customUI.chat.PlainTextView;
import com.twilio.chat.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ChatAdapter extends BaseAdapter {
    private static String TAG = "ChatAdapter";
    private DateFormat timeFormat, dateFormat;
    callbackZoomImage imageZoomCallback = null;
    Context context;
    List<ChatEntityInfo> arrData;
    LayoutInflater inflater;

    public ChatAdapter(Context context, List<ChatEntityInfo> data){
        this.context = context;
        this.arrData = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        timeFormat = new SimpleDateFormat("hh:mm");
        dateFormat = DateFormat.getDateInstance();
    }

    public void setImageZoomCallback(callbackZoomImage callback){
        this.imageZoomCallback = callback;
    }

    public void addAll(List<ChatEntityInfo> data){
        this.arrData = data;
        notifyDataSetChanged();
    }

    public void addMessage(ChatEntityInfo message){
        arrData.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return arrData.size();
    }

    @Override
    public Object getItem(int position) {
        return arrData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatEntityInfo item = arrData.get(position);
        ChatEntityInfo lastMessage = null;
        if (position != 0){
            lastMessage = arrData.get(position - 1);
        }
        if (item.type == ChatEntityInfo.MESSAGE_TYPE.TEXT) {
            convertView = item.userType == ChatEntityInfo.USER_TYPE.I ?  MessageView.newView(Constant.TYPE_OUTGOING_PLAIN_TEXT, context) : MessageView.newView(Constant.TYPE_INCOMING_PLAIN_TEXT, context);
            bindPlainTextMessage((PlainTextView)convertView, item);
        } else if (item.type == ChatEntityInfo.MESSAGE_TYPE.IMAGE) {
            convertView = item.userType == ChatEntityInfo.USER_TYPE.I ?  MessageView.newView(Constant.TYPE_OUTGOING_IMAGE, context) : MessageView.newView(Constant.TYPE_INCOMING_IMAGE, context);
            bindImage((ImageMessageView)convertView, item);
        } else if (item.type == ChatEntityInfo.MESSAGE_TYPE.VIDEO) {

        }

        // whether to display date at header
        bindDateSeparator((MessageView) convertView, item, lastMessage);

        return convertView;
    }

    private void bindPlainTextMessage(PlainTextView view, ChatEntityInfo item) {
        view.setMessage(item.text);
    }

    private void bindImage(ImageMessageView view, ChatEntityInfo item) {
        view.showProgress(true, item.isLoading);
        if (!StringHelper.isEmpty(item.text)) {
            Picasso.get().load(item.text).into(view.getImageView());
            view.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (imageZoomCallback != null) {
                        imageZoomCallback.onZoomImage(item.text);
                    }
                }
            });
        }
    }

    private void bindDateSeparator(MessageView view, ChatEntityInfo currentItem, ChatEntityInfo lastItem) {
        view.setTimeText(timeFormat.format(currentItem.date));
        if (isSameDayToPreviousPosition(currentItem, lastItem)){
            view.hideDateSeparator();
        } else {
            view.displayDateSeparator(GlobalUtils.lastChatDate2(currentItem.date));
        }
    }

    private boolean isSameDayToPreviousPosition(ChatEntityInfo currentItem, ChatEntityInfo lastItem) {
        // get previous item's date, for comparison
        if (lastItem != null) {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(currentItem.date);
            cal2.setTime(lastItem.date);
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } else {
            return false;
        }
    }


    public interface callbackZoomImage{
        void onZoomImage(String filePath);
    }

}
