package com.toolsbox.contractor.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.ServiceItem;
import com.toolsbox.contractor.view.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;


public class ServiceAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private static final String TAG = "ServiceAdapter";
    private ArrayList<ServiceItem> dataSet;
    private Context context;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private OnItemClickListener listener;

    public ServiceAdapter(Context context, ArrayList<ServiceItem> data) {
        this.dataSet = data;
        this.context = context;
    }

    public void addListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void add(ServiceItem response) {
        dataSet.add(response);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(List<ServiceItem> postItems) {
        for (ServiceItem response : postItems) {
            add(response);
        }
    }

    public void remove(ServiceItem currentItem) {
        int position = dataSet.indexOf(currentItem);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = dataSet.size() - 1;
        ServiceItem item = getItem(position);
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
        isLoaderVisible = true;
        add(new ServiceItem());
    }

    ServiceItem getItem(int position) {
        return dataSet.get(position);
    }




    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false));
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
        return dataSet == null? 0 : dataSet.size();
    }

    // ViewHolders

    public class ViewHolder extends BaseViewHolder {
        LinearLayout llItem;
        TextView tvTitle;
        ImageView ivIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            this.llItem = itemView.findViewById(R.id.ll_item);
            this.tvTitle =  itemView.findViewById(R.id.tv_title);
            this.ivIcon =  itemView.findViewById(R.id.iv_icon);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            ServiceItem item = dataSet.get(position);
            tvTitle.setText(item.getTitle());
            ivIcon.setImageDrawable(context.getDrawable(item.getIcon()));
            ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));
            llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ServiceItem item);
    }

}
