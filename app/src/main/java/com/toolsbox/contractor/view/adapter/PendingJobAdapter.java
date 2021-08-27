package com.toolsbox.contractor.view.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.ProjectInfo;

import java.util.ArrayList;


public class PendingJobAdapter extends RecyclerView.Adapter<PendingJobAdapter.MyViewHolder>{

    private ArrayList<ProjectInfo> dataSet;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvJobName;
        TextView tvJobType;
        TextView tvRegion;
        TextView tvSkills;
        TextView tvEstimatedBudget;
        TextView tvDescription;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvJobName =  itemView.findViewById(R.id.tv_job_name);
            this.tvJobType =  itemView.findViewById(R.id.tv_job_type);
            this.tvRegion =  itemView.findViewById(R.id.tv_region);
            this.tvSkills =  itemView.findViewById(R.id.tv_skill);
            this.tvEstimatedBudget =  itemView.findViewById(R.id.tv_estimated_budget);
            this.tvDescription =  itemView.findViewById(R.id.tv_job_description);

        }
    }

    public PendingJobAdapter(Context context, ArrayList<ProjectInfo> data) {
        this.context = context;
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_pending_job, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        holder.tvJobName.setText(dataSet.get(listPosition).getProjectName());
        holder.tvJobType.setText(dataSet.get(listPosition).getContractorSpecialization());
        holder.tvRegion.setText("China");
        holder.tvSkills.setText("kkkk");
        holder.tvEstimatedBudget.setText("$800");
        holder.tvDescription.setText(dataSet.get(listPosition).getProjectDescription());

    }



    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
