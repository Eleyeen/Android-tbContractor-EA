package com.toolsbox.contractor.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.model.GeneralJobHistoryInfo;
import com.toolsbox.contractor.common.model.JobInfo;
import com.toolsbox.contractor.common.model.ProjectInfo;
import com.toolsbox.contractor.common.model.api.HistoryInfo;
import com.toolsbox.contractor.common.utils.PreferenceHelper;
import com.toolsbox.contractor.common.utils.TimeHelper;
import com.toolsbox.contractor.view.activity.main.jobs.JobDetailActivity;
import com.toolsbox.contractor.view.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class JobHistoryAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private static final String TAG = "JobHistoryAdapter";
    private ArrayList<JobInfo> dataSet;
    private Context context;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private OnItemClickListener listener;

    public JobHistoryAdapter(Context context, ArrayList<JobInfo> data) {
        this.context = context;
        this.dataSet = data;
    }

    public void addListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void add(JobInfo response) {
        dataSet.add(response);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void updateAll(ArrayList<JobInfo> postItems) {
        for (JobInfo item : postItems) {
            if (!dataSet.contains(item)) {
                add(item);
            }
        }
    }

    public void addAll(ArrayList<JobInfo> postItems) {
        for (JobInfo response : postItems) {
            add(response);
        }
    }

    public void remove(JobInfo currentItem) {
        int position = dataSet.indexOf(currentItem);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = dataSet.size() - 1;
        JobInfo item = getItem(position);
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
        add(new JobInfo());
    }

    JobInfo getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        switch (viewType){
            case VIEW_TYPE_LOADING:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_job_history, parent, false));
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible)
            return position == dataSet.size() - 1? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        else
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

    public class FooterHolder extends BaseViewHolder {
        ProgressBar mProgressBar;
        FooterHolder(View itemView) {
            super(itemView);
            this.mProgressBar = itemView.findViewById(R.id.progressbar);
        }

        @Override
        protected void clear() {
        }

    }



    // ViewHolders

    public  class ViewHolder extends BaseViewHolder {
        CardView cvItem;
        TextView tvJobName;
        TextView tvStatus;
        TextView tvIndustry;
        TextView tvPostedDate;
        TextView tvValue;
        TextView tvDateTitle;
        LinearLayout llValue, llIndustry;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvJobName =  itemView.findViewById(R.id.tv_job_name);
            this.tvStatus =  itemView.findViewById(R.id.tv_status);
            this.tvIndustry = itemView.findViewById(R.id.tv_industry);
            this.tvPostedDate =  itemView.findViewById(R.id.tv_posted_date);
            this.tvValue = itemView.findViewById(R.id.tv_value);
            this.tvDateTitle = itemView.findViewById(R.id.tv_date_title);
            this.cvItem = itemView.findViewById(R.id.cv_item);
            this.llValue = itemView.findViewById(R.id.ll_value);
            this.llIndustry = itemView.findViewById(R.id.ll_industry);
        }

        @Override
        protected void clear() {

        }
        public void onBind(int position) {
            super.onBind(position);
            JobInfo item = dataSet.get(position);
            tvJobName.setText(item.name);
            llValue.setVisibility(View.GONE);
            if (item.status == Constant.PENDING_APPROVAL && item.contractor_id == PreferenceHelper.getId()){
                tvStatus.setText("New Request");
            } else {
                tvStatus.setText("New Job");
            }
            tvIndustry.setText(Constant.gArrSpecialization[item.industry - 1]);
            String friendlyDates = TimeHelper.convertFrindlyTime(item.job_posted_date);
            tvPostedDate.setText(friendlyDates);

            cvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }


    public interface OnItemClickListener {
        void onItemClick(JobInfo item);
    }
}
