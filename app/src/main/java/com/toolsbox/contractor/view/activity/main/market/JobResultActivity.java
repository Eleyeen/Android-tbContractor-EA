package com.toolsbox.contractor.view.activity.main.market;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.JobInfo;
import com.toolsbox.contractor.common.model.api.JobSearchData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.controller.chat.channels.ChannelManager;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.main.jobs.JobDetail2Activity;
import com.toolsbox.contractor.view.activity.main.messages.ChatActivity;
import com.toolsbox.contractor.view.adapter.JobResultAdapter;
import com.toolsbox.contractor.view.adapter.listener.PaginationScrollListener;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ErrorInfo;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.toolsbox.contractor.common.Constant.FROM_QUOTE_SEND;
import static com.toolsbox.contractor.common.Constant.FROM_SEARCH_JOB;

public class JobResultActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, JobResultAdapter.OnItemClickListener {
    private static String TAG = "JobResultActivity";
    private JobResultAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout rlNodata, rlLoadingData;
    private TextView tvNoTitle;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int selectedIndustry;
    private double lati, longi;
    private int radius;
    private int CURRENT_PAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_result);
        initVariable();
        initUI();
        swipeRefresh.setRefreshing(true);
        fetchJob();
    }

    void initVariable(){
        selectedIndustry = getIntent().getIntExtra("Industry", 1);
        lati = getIntent().getDoubleExtra("Lati", 0);
        longi = getIntent().getDoubleExtra("Longi", 0);
        radius = getIntent().getIntExtra("Radius", 0);
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        rlNodata = findViewById(R.id.rl_no_data);
        rlLoadingData = findViewById(R.id.rl_loading_data);
        tvNoTitle = findViewById(R.id.tv_no_title);
        tvNoTitle.setText("No " + Constant.gArrSpecialization[selectedIndustry - 1] +  "\n Jobs is available in your area");
        swipeRefresh = findViewById(R.id.refresh_swipe);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView =  findViewById(R.id.recycler_jobs);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new JobResultAdapter(this, new ArrayList<>());
        adapter.addListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                Log.e(TAG, "loadMoreItems");
                isLoading = true;
                CURRENT_PAGE ++;
                fetchJob();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(Constant.gArrSpecialization[selectedIndustry - 1] + " Jobs");
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
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

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRefresh() {
        CURRENT_PAGE = 1;
        isLastPage = false;
        adapter.clear();
        fetchJob();
    }

    void fetchJob(){
        ApiUtils.searchJob(this, CURRENT_PAGE, Constant.PER_PAGE, selectedIndustry, String.valueOf(lati), String.valueOf(longi), radius, new Notify() {
            @Override
            public void onSuccess(Object object) {
                isLoading = false;
                if (swipeRefresh.isShown())
                    swipeRefresh.setRefreshing(false);
                JobSearchData data = (JobSearchData) object;
                if (data != null){
                    if (data.status == 0){
                        List<JobInfo> arrJobs = data.info;
                        int totalJobs = data.total_number;
                        if (CURRENT_PAGE != 1) adapter.removeLoading();
                        adapter.addAll(arrJobs);

                        // Check if need more loading
                        int totalPage = (totalJobs + Constant.PER_PAGE -1) / Constant.PER_PAGE;
                        if (CURRENT_PAGE < totalPage)
                            adapter.addLoading();
                        else
                            isLastPage = true;

                    } else {
                        Toast.makeText(JobResultActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JobResultActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
                updateBackground();
            }

            @Override
            public void onFail() {
                isLoading = false;
                if (swipeRefresh.isShown())
                    swipeRefresh.setRefreshing(false);
                Toast.makeText(JobResultActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                updateBackground();
            }
        });
    }

    void updateBackground(){
        if (adapter.getItemCount() == 0) {
            if (swipeRefresh.isRefreshing()) {
                rlNodata.setVisibility(View.GONE);
                rlLoadingData.setVisibility(View.VISIBLE);
            } else {
                rlNodata.setVisibility(View.VISIBLE);
                rlLoadingData.setVisibility(View.GONE);
            }
        } else {
            rlNodata.setVisibility(View.GONE);
            rlLoadingData.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTapChat(JobInfo item) {
        showProgressDialog();
        ChannelManager channelManager = ChannelManager.getInstance();
        channelManager.joinOrCreateChannelWithCompletion(item.poster_id, item.poster_type, new CallbackListener<Channel>() {
            @Override
            public void onSuccess(Channel channel) {
                hideProgressDialog();
                goToChatActivity(channel.getSid());
            }

            public void onError(ErrorInfo errorInfo) {
                hideProgressDialog();
                Toast.makeText(JobResultActivity.this, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void goToChatActivity(String  channelSid){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("sid", channelSid);
        startActivity(intent);
    }

    @Override
    public void onTapQuote(JobInfo item) {
        Intent intent = new Intent(JobResultActivity.this, QuoteActivity.class);
        intent.putExtra("from", FROM_QUOTE_SEND);
        intent.putExtra("currentJob", item);
        startActivity(intent);
    }

    @Override
    public void onTapItem(JobInfo item) {
        Intent intent = new Intent(JobResultActivity.this, JobDetail2Activity.class);
        intent.putExtra("jobId", item.id);
        startActivity(intent);
    }
}
