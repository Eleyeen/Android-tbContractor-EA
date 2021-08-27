package com.toolsbox.contractor.view.activity.main.jobs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.AttachFileInfo;
import com.toolsbox.contractor.common.model.AvailabilityDateInfo;
import com.toolsbox.contractor.common.model.JobDetailInfo;
import com.toolsbox.contractor.common.model.api.GeneralData;
import com.toolsbox.contractor.common.model.api.JobDetailData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.common.utils.MessageUtils;
import com.toolsbox.contractor.common.utils.PreferenceHelper;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.common.utils.TimeHelper;
import com.toolsbox.contractor.controller.chat.channels.ChannelManager;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.main.market.ProposalDetailActivity;
import com.toolsbox.contractor.view.activity.main.market.QuoteActivity;
import com.toolsbox.contractor.view.activity.main.messages.ChatActivity;
import com.toolsbox.contractor.view.adapter.JobAttachAdapter;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ErrorInfo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static com.toolsbox.contractor.common.Constant.FROM_QUOTE_SEND;
import static com.toolsbox.contractor.common.Constant.FROM_QUOTE_UPDATE;

public class JobDetail2Activity extends BaseActivity implements View.OnClickListener{
    private static String TAG = "JobDetail2Activity";
    private static final int REQUEST_CODE_QUOTE_ACTIVITY = 1;

    private Toolbar toolbar;
    private Button btnAvailability, btnProposal, btnMarkComplete, btnChat;
    private TextView tvJobTitle, tvStatus, tvTotalHour, tvHourlyRate,  tvCustomerName, tvIndustry, tvPostDate, tvStartDate, tvStartDateTitle, tvValue,
            tvLocation, tvDetails;
    private LinearLayout llBackground, llStatus, llTotalHour, llHourlyRate,  llCustomerName, llPostDate,  llStartDate, llValue, llMarkComplete;
    private RecyclerView recyclerView;
    private JobAttachAdapter adapter;
    private JobDetailInfo jobDetailInfo;
    private ArrayList<AttachFileInfo> dataAttach = new ArrayList<>();
    private int jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail2);
        initVariable();
        initUI();
    }

    void initVariable(){
        jobId = getIntent().getIntExtra("jobId", 0);
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvStatus = findViewById(R.id.tv_status);
        tvTotalHour = findViewById(R.id.tv_total_hour);
        tvHourlyRate = findViewById(R.id.tv_hourly_rate);
        tvCustomerName = findViewById(R.id.tv_customer);
        tvIndustry = findViewById(R.id.tv_industry);
        tvPostDate = findViewById(R.id.tv_post_date);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvValue = findViewById(R.id.tv_value);
        tvLocation = findViewById(R.id.tv_location);
        tvDetails = findViewById(R.id.tv_description);
        tvStartDateTitle = findViewById(R.id.tv_start_date_title);

        llBackground = findViewById(R.id.ll_background);
        llStatus = findViewById(R.id.ll_status);
        llTotalHour = findViewById(R.id.ll_total_hour);
        llHourlyRate = findViewById(R.id.ll_hourly_rate);
        llCustomerName = findViewById(R.id.ll_customer);
        llPostDate = findViewById(R.id.ll_post_date);
        llStartDate = findViewById(R.id.ll_start_date);
        llValue = findViewById(R.id.ll_value);
        llMarkComplete = findViewById(R.id.ll_mark_complete);

        btnAvailability = findViewById(R.id.btn_availability);
        btnProposal = findViewById(R.id.btn_proposal);
        btnMarkComplete = findViewById(R.id.btn_mark_complete);
        btnChat = findViewById(R.id.btn_chat);
        btnProposal.setOnClickListener(this);
        btnMarkComplete.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnAvailability.setOnClickListener(this);
        llBackground.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recycler);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JobAttachAdapter(this, dataAttach);
        adapter.addListener(new JobAttachAdapter.OnItemClickListener() {

            @Override
            public void onItemOpen(AttachFileInfo item) {
                Intent intent = new Intent(JobDetail2Activity.this, ImagePreviewActivity.class);
                intent.putExtra("file", item.url);
                intent.putExtra("isLocal", false);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        fetchJobDetails();
    }

    void updateView(){
        llBackground.setVisibility(View.VISIBLE);
        if (jobDetailInfo.bid != null) {
            // init button
            btnChat.setEnabled(true);
            if (jobDetailInfo.job.status == Constant.SCHEDULED) {
                btnMarkComplete.setText("En Route");
            } else if (jobDetailInfo.job.status == Constant.EN_ROUTE) {
                btnMarkComplete.setText("Starting Job");
            } else if (jobDetailInfo.job.status == Constant.IN_PROGRESS) {
                btnMarkComplete.setText("Mark Job as Complete");
            } else {
                btnMarkComplete.setVisibility(View.GONE);
            }
            // init other
            if (jobDetailInfo.job.status < Constant.SCHEDULED) {
                llStartDate.setVisibility(View.GONE);
                llValue.setVisibility(View.GONE);
                llTotalHour.setVisibility(View.GONE);
                llHourlyRate.setVisibility(View.GONE);
            } else {
                if (jobDetailInfo.job.status == Constant.EN_ROUTE) {
                    llStartDate.setVisibility(View.GONE);
                }
                llPostDate.setVisibility(View.GONE);
                if (jobDetailInfo.job.type == 1) {
                    if (jobDetailInfo.job.status != Constant.FINISH) {
                        llValue.setVisibility(View.GONE);
                    }
                } else {
                    llTotalHour.setVisibility(View.GONE);
                    llHourlyRate.setVisibility(View.GONE);
                }
            }
            if (!StringHelper.isEmpty(jobDetailInfo.bid.hourly_rate)) {
                tvHourlyRate.setText("$" + jobDetailInfo.bid.hourly_rate);
            }
        } else {
            // init button
            btnChat.setText("Chat");
            btnProposal.setText("Provide Proposal");
            btnMarkComplete.setVisibility(View.GONE);

            // init other
            llHourlyRate.setVisibility(View.GONE);
            llStatus.setVisibility(View.GONE);
            llStartDate.setVisibility(View.GONE);
            llValue.setVisibility(View.GONE);
            llTotalHour.setVisibility(View.GONE);
        }

        tvJobTitle.setText(jobDetailInfo.job.name);
        tvCustomerName.setText(jobDetailInfo.job.poster_name);
        tvStatus.setText(Constant.gArrJobStatus[jobDetailInfo.job.status]);
        tvIndustry.setText(Constant.gArrSpecialization[jobDetailInfo.job.industry - 1]);
        tvPostDate.setText(TimeHelper.convertFrindlyTime(jobDetailInfo.job.job_posted_date));
        tvLocation.setText(jobDetailInfo.job.area);
        tvDetails.setText(jobDetailInfo.job.description);
        if (!StringHelper.isEmpty(jobDetailInfo.job.accepted_budget)) {
            tvValue.setText("$" + jobDetailInfo.job.accepted_budget);
        }
        if (jobDetailInfo.job.status == Constant.SCHEDULED) {
            tvStartDateTitle.setText("Scheduled Date");
            tvStartDate.setText(GlobalUtils.convertScheduleRange(jobDetailInfo.job.job_scheduled_date));
            tvTotalHour.setText("Pending Completion");
        } else if (jobDetailInfo.job.status == Constant.IN_PROGRESS) {
            tvStartDateTitle.setText(R.string.start_date_time);
            tvStartDate.setText(TimeHelper.convertFrindlyTime(jobDetailInfo.job.job_started_date));
            tvTotalHour.setText("Pending Completion");
        } else if (jobDetailInfo.job.status == Constant.FINISH) {
            tvStartDateTitle.setText("Completion Date");
            tvStartDate.setText(TimeHelper.convertFrindlyTime(jobDetailInfo.job.job_completed_date));
            tvTotalHour.setText("$" + jobDetailInfo.record_hours.hours);
        }
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.job_details);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
        }
    }

    void fetchJobDetails(){
        if (!PreferenceHelper.isLoginIn()){
            return;
        }
        showProgressDialog();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("job_id", jobId);
            jsonObject.put("contractor_id", PreferenceHelper.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiUtils.jobContractorDetail(this, jsonObject, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                JobDetailData data = (JobDetailData) object;
                if (data != null) {
                    if (data.status == 0) {
                        jobDetailInfo = data.info;
                        if (!StringHelper.isEmpty(jobDetailInfo.job.attachment)) {
                            for (String item : Arrays.asList(jobDetailInfo.job.attachment.split(","))) {
                                dataAttach.add(new AttachFileInfo(GlobalUtils.fetchDownloadFileName(item), item, "", false));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        updateView();
                    } else {
                        Toast.makeText(JobDetail2Activity.this, data.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JobDetail2Activity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(JobDetail2Activity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_availability:
                ArrayList<AvailabilityDateInfo> arrSelectedDate = GlobalUtils.convertAvailabilityDateModel(jobDetailInfo.job.availability_dates);
                Intent intent1 = new Intent(this, SeeAvailabilityActivity.class);
                intent1.putExtra("isEditable", false);
                Gson gson = new Gson();
                String strArrDate = gson.toJson(arrSelectedDate);
                intent1.putExtra("arrDates", strArrDate);
                startActivity(intent1);
                break;
            case R.id.btn_proposal:
                if (jobDetailInfo.bid != null) {
                    if (jobDetailInfo.job.status < Constant.SCHEDULED) {
                        Intent intent = new Intent(JobDetail2Activity.this, QuoteActivity.class);
                        intent.putExtra("from", FROM_QUOTE_UPDATE);
                        intent.putExtra("jobId", jobDetailInfo.job.id);
                        startActivityForResult(intent, REQUEST_CODE_QUOTE_ACTIVITY);
                    } else {
                        Intent intent = new Intent(JobDetail2Activity.this, ProposalDetailActivity.class);
                        intent.putExtra("bid", jobDetailInfo.bid);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(JobDetail2Activity.this, QuoteActivity.class);
                    intent.putExtra("from", FROM_QUOTE_SEND);
                    intent.putExtra("currentJob", jobDetailInfo.job);
                    startActivityForResult(intent, REQUEST_CODE_QUOTE_ACTIVITY);
                }
                break;
            case R.id.btn_mark_complete:
                if (jobDetailInfo.job.status == Constant.SCHEDULED) {
                    // En route
                    MessageUtils.showCustomAlertDialog(this, "Are you sure you want to update your status to En Route?", new Notify() {
                        @Override
                        public void onSuccess(Object object) {
                            sendEnRouteJobRequest();
                        }

                        @Override
                        public void onFail() {

                        }
                    });
                } else if (jobDetailInfo.job.status == Constant.EN_ROUTE) {
                    // Start job
                    MessageUtils.showCustomAlertDialog(this, "Are you sure you are starting to work on your job now?", new Notify() {
                        @Override
                        public void onSuccess(Object object) {
                            sendStartJobRequest();
                        }

                        @Override
                        public void onFail() {

                        }
                    });
                } else if (jobDetailInfo.job.status == Constant.IN_PROGRESS) {
                    // Complete job
                    MessageUtils.showCustomAlertDialog(this, "Are you sure you'd like to mark the job as complete?", new Notify() {
                        @Override
                        public void onSuccess(Object object) {
                            completeJob();
                        }

                        @Override
                        public void onFail() {

                        }
                    });
                }
                 break;

            case R.id.btn_chat:
                showProgressDialog();
                ChannelManager channelManager = ChannelManager.getInstance();
                channelManager.joinOrCreateChannelWithCompletion(jobDetailInfo.job.poster_id, jobDetailInfo.job.poster_type, new CallbackListener<Channel>() {
                    @Override
                    public void onSuccess(Channel channel) {
                        hideProgressDialog();
                        goToChatActivity(channel.getSid());
                    }

                    public void onError(ErrorInfo errorInfo) {
                        hideProgressDialog();
                        Toast.makeText(JobDetail2Activity.this, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }

    }

    void goToChatActivity(String  channelSid){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("sid", channelSid);
        startActivity(intent);
    }

    void sendEnRouteJobRequest(){
        showProgressDialog();
        ApiUtils.requestEnRouteJob(this, jobDetailInfo.bid.id, PreferenceHelper.getToken(), new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                        Toast.makeText(JobDetail2Activity.this, R.string.success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(JobDetail2Activity.this, data.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JobDetail2Activity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(JobDetail2Activity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void sendStartJobRequest(){
        showProgressDialog();
        ApiUtils.requestStartJob(this, jobDetailInfo.bid.id, PreferenceHelper.getToken(), new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                        Toast.makeText(JobDetail2Activity.this, R.string.success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(JobDetail2Activity.this, data.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JobDetail2Activity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(JobDetail2Activity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void completeJob(){
        showProgressDialog();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bid_id", jobDetailInfo.bid.id);
            if (jobDetailInfo.bid.job.type == 1) {
                jsonObject.put("working_hours", "");
            }
            ApiUtils.makeJobComplete(this, jsonObject, PreferenceHelper.getToken(), new Notify() {
                @Override
                public void onSuccess(Object object) {
                    hideProgressDialog();
                    GeneralData data = (GeneralData)object;
                    if (data != null){
                        if (data.status == 0){
                            Toast.makeText(JobDetail2Activity.this, R.string.success, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(JobDetail2Activity.this, data.message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(JobDetail2Activity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFail() {
                    hideProgressDialog();
                    Toast.makeText(JobDetail2Activity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_QUOTE_ACTIVITY:
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;

            }
        }
    }

}
