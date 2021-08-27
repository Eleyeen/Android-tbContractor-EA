package com.toolsbox.contractor.view.activity.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.ServiceItem;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.adapter.ServiceAdapter;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class IndustrySelectActivity extends BaseActivity {
    private static String TAG = "IndustrySelectActivity";
    private static final int REQUEST_POST_JOB_ACTIVITY = 1;

    private Toolbar toolbar;
    private ArrayList<ServiceItem> arrService;
    private GridLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industry_select);
        initVariable();
        initUI();
    }

    void initVariable(){
        arrService = GlobalUtils.getAllService();
    }

    void initUI(){
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        setupToolbar();
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ServiceAdapter(this, arrService);
        adapter.addListener(new ServiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ServiceItem item) {
                Intent intent1 = new Intent();
                setResult(RESULT_OK, intent1);
                intent1.putExtra("service", item);
                finish();

            }
        });
        recyclerView.setAdapter(adapter);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.services);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_POST_JOB_ACTIVITY:
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
