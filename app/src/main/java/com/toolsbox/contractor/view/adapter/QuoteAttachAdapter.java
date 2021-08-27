package com.toolsbox.contractor.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.AttachFileInfo;
import com.toolsbox.contractor.view.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;


public class QuoteAttachAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private static final String TAG = "QuoteAttachAdapter";
    private ArrayList<AttachFileInfo> dataSet;
    private Context context;

    private static final int VIEW_TYPE_ADD = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private OnItemClickListener listener;

    public QuoteAttachAdapter(Context context, ArrayList<AttachFileInfo> data) {
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
        switch (viewType){
            case VIEW_TYPE_ADD:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_attach_add, parent, false));
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_attach_row, parent, false));
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == dataSet.size() ? VIEW_TYPE_ADD : VIEW_TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int listPosition) {
        holder.onBind(listPosition);
    }

    @Override
    public int getItemCount() {
        return dataSet == null? 1 : dataSet.size() + 1;
    }

    public class FooterHolder extends BaseViewHolder {
        LinearLayout llAdd;
        FooterHolder(View itemView) {
            super(itemView);
            this.llAdd = itemView.findViewById(R.id.ll_upload);
            this.llAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemAddClick();
                    }
                }
            });
        }

        @Override
        protected void clear() {
        }

    }



    // ViewHolders

    public  class ViewHolder extends BaseViewHolder {
        TextView tvTitle;
        ImageButton btnDelete;
        ProgressBar pbLoading;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvTitle =  itemView.findViewById(R.id.tv_title);
            this.btnDelete =  itemView.findViewById(R.id.btn_delete);
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
                btnDelete.setVisibility(View.GONE);
                pbLoading.setVisibility(View.VISIBLE);
            } else {
                btnDelete.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.GONE);
            }
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemOpen(item);
                    }
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemRemoveClick(item);
                    }
                }
            });
        }

    }


    public interface OnItemClickListener {
        void onItemAddClick();
        void onItemRemoveClick(AttachFileInfo item);
        void onItemOpen(AttachFileInfo item);
    }

}
