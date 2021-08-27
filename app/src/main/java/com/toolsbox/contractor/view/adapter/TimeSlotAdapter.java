package com.toolsbox.contractor.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.AvailabilityDateInfo;
import com.toolsbox.contractor.common.model.TimePreferItemInfo;
import com.toolsbox.contractor.view.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;


public class TimeSlotAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private static final String TAG = "TimeSlotAdapter";
    private ArrayList<TimePreferItemInfo> dataSet;
    private Context context;

    private static final int VIEW_TYPE_ADD = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private OnItemClickListener listener;

    public TimeSlotAdapter(Context context, ArrayList<TimePreferItemInfo> data) {
        this.context = context;
        this.dataSet = data;
    }

    public void addListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void add(TimePreferItemInfo response) {
        dataSet.add(response);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(List<TimePreferItemInfo> postItems) {
        for (TimePreferItemInfo response : postItems) {
            add(response);
        }
    }

    public void remove(TimePreferItemInfo currentItem) {
        int position = dataSet.indexOf(currentItem);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeLoading() {
        int position = dataSet.size() - 1;
        TimePreferItemInfo item = getItem(position);
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
        add(new TimePreferItemInfo());
    }

    TimePreferItemInfo getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_time_slot_item, parent, false));
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

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvTitle =  itemView.findViewById(R.id.tv_title);
        }

        @Override
        protected void clear() {

        }
        public void onBind(int position) {
            super.onBind(position);
            TimePreferItemInfo item = dataSet.get(position);
            tvTitle.setText(item.title);
            updateBackground(item);
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isSelected) {
                        item.isSelected = false;
                    } else {
                        if (countSelectedItem() < 2) {
                            item.isSelected = true;
                        }
                    }
                    updateBackground(item);
                }
            });
        }

        int countSelectedItem() {
            int size = 0;
            for (TimePreferItemInfo item : dataSet) {
                if (item.isSelected) {
                    size += 1;
                }
            }
            return size;
        }

        void updateBackground(TimePreferItemInfo item){
            if (item.isSelected) {
                tvTitle.setBackground(context.getDrawable(R.drawable.bg_rounded));
                tvTitle.setTextColor(context.getResources().getColor(R.color.colorWhite));
            } else {
                tvTitle.setBackground(context.getDrawable(R.drawable.bg_round_border));
                tvTitle.setTextColor(context.getResources().getColor(R.color.colorBlue));
            }
        }

    }


    public interface OnItemClickListener {
        void onItemRemoveClick(AvailabilityDateInfo item);
        void onItemAddClick();
    }

}
