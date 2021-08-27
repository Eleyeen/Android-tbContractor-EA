package com.toolsbox.contractor.view.adapter;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.ContractorInfo;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.view.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ContractorAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    private static final String TAG = "ContractorAdapter";
    private ArrayList<ContractorInfo> dataSet;
    private Context context;
    private OnItemClickListener listener;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public ContractorAdapter(Context context, ArrayList<ContractorInfo> data) {
        this.dataSet = data;
        this.context = context;
    }

    public void addListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void add(ContractorInfo response) {
        dataSet.add(response);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(List<ContractorInfo> postItems) {
        for (ContractorInfo response : postItems) {
            add(response);
        }
    }

    public void remove(ContractorInfo currentItem) {
        int position = dataSet.indexOf(currentItem);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = dataSet.size() - 1;
        ContractorInfo item = getItem(position);
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
        add(new ContractorInfo());
    }

    ContractorInfo getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        switch (viewType){
            case VIEW_TYPE_LOADING:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_contractors, parent, false));
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

    public class ViewHolder extends BaseViewHolder {

        TextView tvContractorName;
        TextView tvContractorTitle;
        TextView tvCompletedJobs;
        TextView tvReviews;
        MaterialRatingBar overallRating;
        MaterialRatingBar timeRating;
        CardView cvSeeReviews;
        CardView cvChat, cvHire;
        CircleImageView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvContractorName =  itemView.findViewById(R.id.tv_name);
            this.tvContractorTitle =  itemView.findViewById(R.id.tv_title);
            this.tvCompletedJobs =  itemView.findViewById(R.id.tv_completed_job);
            this.tvReviews =  itemView.findViewById(R.id.tv_review);
            this.overallRating =  itemView.findViewById(R.id.overall_rating);
            this.timeRating =  itemView.findViewById(R.id.time_rating);
            this.cvSeeReviews = itemView.findViewById(R.id.cv_see_reviews);
            this.ivPhoto = itemView.findViewById(R.id.iv_profile);
            this.cvChat = itemView.findViewById(R.id.cv_chat);
            this.cvHire = itemView.findViewById(R.id.cv_hire);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            ContractorInfo item = dataSet.get(position);
            tvContractorName.setText(item.business_name);
            tvContractorTitle.setText(item.speciality_title);
            tvCompletedJobs.setText("1");
            tvReviews.setText("0");
            overallRating.setRating(5);
            timeRating.setRating(5);
            if (!StringHelper.isEmpty(item.image_url))
                Picasso.get().load(item.image_url).into(ivPhoto);
            cvSeeReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTapReview(item);
                }
            });
            cvChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTapChat(item);
                }
            });

            cvHire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTapHire(item);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onTapReview(ContractorInfo item);
        void onTapChat(ContractorInfo item);
        void onTapHire(ContractorInfo item);
    }
}
