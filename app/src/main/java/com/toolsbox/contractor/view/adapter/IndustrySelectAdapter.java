package com.toolsbox.contractor.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.IndustryItem;

import java.util.List;

public class IndustrySelectAdapter extends BaseAdapter {
    private static String TAG = "IndustrySelectAdapter";
    Context context;
    List<IndustryItem> arrData;
    LayoutInflater inflater;
    boolean isMultiple;

    public IndustrySelectAdapter(Context context, List<IndustryItem> data, boolean isMultiple){
        this.context = context;
        this.arrData = data;
        this.isMultiple = isMultiple;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrData.size();
    }

    @Override
    public Object getItem(int position) {
        return arrData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.row_industry, null);
            holder = new ViewHolder();
            holder.ctvName = convertView.findViewById(R.id.ctv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        IndustryItem item = arrData.get(position);
        holder.ctvName.setText(item.getIndustryName());
        holder.ctvName.setChecked(item.isChecked());
        if (item.isChecked()){
            holder.ctvName.setCheckMarkDrawable(R.drawable.ic_check);
        } else {
            holder.ctvName.setCheckMarkDrawable(R.drawable.ic_uncheck);
        }

        holder.ctvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Clicked: " + position);
                if (!isMultiple){
                    allClearItem(holder);
                    holder.ctvName.setChecked(true);
                    holder.ctvName.setCheckMarkDrawable(R.drawable.ic_check);
                    arrData.get(position).setChecked(true);
                } else {
                    if (arrData.get(position).isChecked()){
                        holder.ctvName.setChecked(false);
                        arrData.get(position).setChecked(false);
                        holder.ctvName.setCheckMarkDrawable(R.drawable.ic_uncheck);
                    } else {
                        holder.ctvName.setChecked(true);
                        arrData.get(position).setChecked(true);
                        holder.ctvName.setCheckMarkDrawable(R.drawable.ic_check);
                    }
                }
            }
        });
        return convertView;
    }

    void allClearItem(ViewHolder holder){
        for (int i = 0; i < arrData.size(); i++){
            arrData.get(i).setChecked(false);
            holder.ctvName.setChecked(false);
            holder.ctvName.setCheckMarkDrawable(R.drawable.ic_uncheck);
        }
        notifyDataSetChanged();
    }

    //ViewHolder

    private static  class ViewHolder {
        CheckedTextView ctvName;
    }

}
