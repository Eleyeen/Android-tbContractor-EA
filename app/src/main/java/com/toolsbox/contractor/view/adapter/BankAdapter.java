package com.toolsbox.contractor.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.BankInfo;
import com.toolsbox.contractor.common.model.CreditCardInfo;
import com.toolsbox.contractor.view.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class BankAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private static final String TAG = "CreditCardAdapter";
    private ArrayList<BankInfo> dataSet;
    private Context context;
    private OnItemClickListener listener;

    public BankAdapter(Context context, ArrayList<BankInfo> data) {
        this.dataSet = data;
        this.context = context;
    }

    public void addListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void add(BankInfo response) {
        dataSet.add(response);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(List<BankInfo> postItems) {
        for (BankInfo response : postItems) {
            add(response);
        }
    }

    public void updateItem(BankInfo currentItem) {
        int position = dataSet.indexOf(currentItem);
        for (BankInfo item : dataSet){
            item.defaultCurrency = false;
        }
        if (position > -1) {
            dataSet.get(position).defaultCurrency = true;
            notifyDataSetChanged();
        }
    }

    public void remove(BankInfo currentItem) {
        int position = dataSet.indexOf(currentItem);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeLoading() {
        int position = dataSet.size() - 1;
        BankInfo item = getItem(position);
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
        add(new BankInfo());
    }

    BankInfo getItem(int position) {
        return dataSet.get(position);
    }




    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_credit_card, parent, false));

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

        CardView cvItem;
        ImageView ivCard, ivCheck;
        TextView tvCardNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            this.cvItem = itemView.findViewById(R.id.cv_item);
            this.ivCard =  itemView.findViewById(R.id.iv_card);
            this.ivCheck =  itemView.findViewById(R.id.iv_check);
            this.tvCardNumber =  itemView.findViewById(R.id.tv_card_number);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            BankInfo item = dataSet.get(position);
            tvCardNumber.setText("**** **** **** " + item.last4);
            ivCard.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bank));
            if (item.defaultCurrency)
                ivCheck.setVisibility(View.VISIBLE);
            else
                ivCheck.setVisibility(View.INVISIBLE);
            cvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTapItem(item);
                }
            });
        }
    }


    public interface OnItemClickListener {
        void onTapItem(BankInfo item);
    }
}
