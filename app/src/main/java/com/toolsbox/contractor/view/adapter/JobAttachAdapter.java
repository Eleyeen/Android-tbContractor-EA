package com.toolsbox.contractor.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.AttachFileInfo;
import com.toolsbox.contractor.view.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;


public class JobAttachAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private static final String TAG = "JobAttachAdapter";
    private ArrayList<AttachFileInfo> dataSet;
    private Context context;

    private static final int VIEW_TYPE_ADD = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private OnItemClickListener listener;

    public JobAttachAdapter(Context context, ArrayList<AttachFileInfo> data) {
        this.context = context;
        this.dataSet = data;
    }

    public void addListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void add(AttachFileInfo response) {
        dataSet.add(response);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(List<AttachFileInfo> postItems) {
        for (AttachFileInfo response : postItems) {
            add(response);
        }
    }

    public void remove(AttachFileInfo currentItem) {
        int position = dataSet.indexOf(currentItem);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeLoading() {
        int position = dataSet.size() - 1;
        AttachFileInfo item = getItem(position);
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

    public void addLoading() {
        add(new AttachFileInfo());
    }

    AttachFileInfo getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_details_attach_row_, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int listPosition) {
        holder.onBind(listPosition);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    // ViewHolders

    public  class ViewHolder extends BaseViewHolder {
        TextView tvTitle;
        ImageButton btnDownload;
        ProgressBar pbLoading;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvTitle =  itemView.findViewById(R.id.tv_title);
            this.btnDownload =  itemView.findViewById(R.id.btn_download);
            this.pbLoading = itemView.findViewById(R.id.pb_loading);
        }

        @Override
        protected void clear() {

        }
        public void onBind(int position) {
            super.onBind(position);
            AttachFileInfo item = dataSet.get(position);
            tvTitle.setText(item.name);
            if (item.isDownloading) {
                btnDownload.setVisibility(View.GONE);
                pbLoading.setVisibility(View.VISIBLE);
            } else {
                btnDownload.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.GONE);
            }

            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemOpen(item);
                    }
                }
            });
        }

    }


    public interface OnItemClickListener {
        void onItemOpen(AttachFileInfo item);
    }

}
