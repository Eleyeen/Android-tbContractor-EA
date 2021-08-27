package com.toolsbox.contractor.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.ChatContactInfo;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.controller.chat.channels.ChannelManager;
import com.toolsbox.contractor.view.adapter.holder.BaseViewHolder;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.User;
import com.twilio.chat.UserDescriptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.toolsbox.contractor.common.Constant.DATE_FORMAT_MM_DD_YYYY;

public class ChannelListAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private static final String TAG = "ChannelListAdapter";
    private ArrayList<ChatContactInfo> dataSet;
    private Context context;
    private OnItemClickListener listener;
    private ChannelManager channelManager;


    public ChannelListAdapter(Context context, ArrayList<ChatContactInfo> data) {
        this.dataSet = data;
        this.context = context;
        channelManager = ChannelManager.getInstance();
    }

    public void addListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void add(ChatContactInfo response) {
        dataSet.add(response);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(List<ChatContactInfo> postItems) {
        for (ChatContactInfo response : postItems) {
            add(response);
        }
    }

    public void remove(ChatContactInfo currentItem) {
        int position = dataSet.indexOf(currentItem);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateChannel(int position){
        if (position > -1) {
            notifyItemChanged(position);
        }
    }

    public void removeLoading() {
        int position = dataSet.size() - 1;
        ChatContactInfo item = getItem(position);
        if (item != null) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }


    ChatContactInfo getItem(int position) {
        return dataSet.get(position);
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int listPosition) {
        holder.onBind(listPosition);
    }

    @Override
    public int getItemCount() {
        return dataSet == null? 0 : dataSet.size();
    }


    public class ViewHolder extends BaseViewHolder {
        CardView cvItem;
        CircleImageView ivPhoto;
        TextView tvName, tvLastMessage, tvDate, tvBadge;
        RelativeLayout rlBadge;

        public ViewHolder(View itemView) {
            super(itemView);
            this.cvItem = itemView.findViewById(R.id.cv_item);
            this.ivPhoto = itemView.findViewById(R.id.iv_photo);
            this.tvName = itemView.findViewById(R.id.tv_name);
            this.tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            this.tvDate = itemView.findViewById(R.id.tv_date);
            this.rlBadge = itemView.findViewById(R.id.rl_badge);
            this.tvBadge = itemView.findViewById(R.id.tv_badge);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            ChatContactInfo item = dataSet.get(position);
            ivPhoto.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_avatar));
            tvName.setText(item.contact_name);
            tvLastMessage.setText(item.last_msg);
            if (!StringHelper.isEmpty(item.contact_photo)){
                Picasso.get().load(item.contact_photo).into(ivPhoto);
            }
            tvBadge.setText(""+item.n_unread_msg);
            if (item.n_unread_msg < 1)
                rlBadge.setVisibility(View.INVISIBLE);
            else
                rlBadge.setVisibility(View.VISIBLE);
            if (item.last_date != null){
                String strDate = GlobalUtils.lastChatDate(item.last_date);
                tvDate.setText(strDate);
            }

            cvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTapItem(item);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onTapItem(ChatContactInfo item);

    }
}
