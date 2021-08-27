package com.toolsbox.contractor.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.ReviewInfo;
import com.toolsbox.contractor.common.model.api.ReviewArrayData;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.common.utils.TimeHelper;
import com.toolsbox.contractor.view.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewsAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private static final String TAG = "ReviewsAdapter";
    private ArrayList<ReviewArrayData> dataSet;
    private Context context;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;


    public ReviewsAdapter(Context context, ArrayList<ReviewArrayData> data) {
        this.dataSet = data;
        this.context = context;
    }


    public void add(ReviewArrayData response) {
        dataSet.add(response);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(List<ReviewArrayData> postItems) {
        for (ReviewArrayData response : postItems) {
            add(response);
        }
    }

    public void remove(ReviewArrayData currentItem) {
        int position = dataSet.indexOf(currentItem);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = dataSet.size() - 1;
        ReviewArrayData item = getItem(position);
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
        add(new ReviewArrayData());
    }

    ReviewArrayData getItem(int position) {
        return dataSet.get(position);
    }




    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        switch (viewType){
            case VIEW_TYPE_LOADING:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_reviews, parent, false));
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

        TextView tvComment;
        TextView tvCustomerName;
        TextView tvReviewDate;
        MaterialRatingBar overallRating;
        MaterialRatingBar workRating;
        MaterialRatingBar timeRating;
        CircleImageView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvComment =  itemView.findViewById(R.id.tv_comment);
            this.tvCustomerName =  itemView.findViewById(R.id.tv_customer_name);
            this.tvReviewDate =  itemView.findViewById(R.id.tv_review_date);
            this.overallRating =  itemView.findViewById(R.id.overall_rating);
            this.workRating =  itemView.findViewById(R.id.work_rating);
            this.timeRating =  itemView.findViewById(R.id.time_rating);
            this.ivPhoto = itemView.findViewById(R.id.iv_photo);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            ReviewArrayData item = dataSet.get(position);
            tvComment.setText(item.review.comment);
            overallRating.setRating(item.review.overall_rate);
            workRating.setRating(item.review.quality_rate);
            timeRating.setRating(item.review.time_rate);
            tvReviewDate.setText(TimeHelper.convertFrindlyTime(item.review.created_date));

            // check if provider is customer or contractor
            if (item.provider_customer != null){  // Customer
                if (!StringHelper.isEmpty(item.provider_customer.image_url))
                    Picasso.get().load(item.provider_customer.image_url).into(ivPhoto);
                if (!StringHelper.isEmpty(item.provider_customer.name))
                    tvCustomerName.setText(item.provider_customer.name);
            } else {   // Contractor
                if (!StringHelper.isEmpty(item.provider_contractor.image_url))
                    Picasso.get().load(item.provider_contractor.image_url).into(ivPhoto);
                if (!StringHelper.isEmpty(item.provider_contractor.business_name))
                    tvCustomerName.setText(item.provider_contractor.business_name);
            }

        }
    }
}
