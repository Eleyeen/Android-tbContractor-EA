package com.toolsbox.contractor.view.activity.main.jobs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.GeneralJobHistoryInfo;
import com.toolsbox.contractor.common.model.api.GeneralData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.common.utils.TimeHelper;
import com.toolsbox.contractor.controller.chat.channels.ChannelManager;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.main.home.CalendarActivity;
import com.toolsbox.contractor.view.activity.main.home.PostJobActivity;
import com.toolsbox.contractor.view.activity.main.messages.ChatActivity;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ErrorInfo;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import static com.toolsbox.contractor.common.Constant.FROM_JOB_EDIT;

public class JobDetailActivity extends BaseActivity implements View.OnClickListener{
    private static String TAG = "JobDetailActivity";
    private Toolbar toolbar;
    private Button btnViewProposal, btnPayment, btnEditJob, btnCompleteJob, btnRemovePosting, btnDisputeJob, btnChat;
    private TextView tvJobTitle, tvStatus, tvContractorName, tvIndustry, tvPostDate, tvStartDate, tvDuration, tvUrgency, tvValue,
            tvLocation, tvDetails;
    private ImageButton btnAvailability;
    private LinearLayout llContractorName, llStartDate, llDuration, llValue;
    private GeneralJobHistoryInfo currentHistoryInfo;
    private static final int REQUEST_POST_JOB_ACTIVTY = 1;
    private static final int REQUEST_POST_JOB_Applied = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        initVariable();
        initUI();
    }

    void initVariable(){
        currentHistoryInfo = (GeneralJobHistoryInfo) getIntent().getSerializableExtra("item");
    }

    void initUI(){
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorWhite), 0);
        setupToolbar();
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvStatus = findViewById(R.id.tv_status);
        tvContractorName = findViewById(R.id.tv_contractor);
        tvIndustry = findViewById(R.id.tv_industry);
        tvPostDate = findViewById(R.id.tv_post_date);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvDuration = findViewById(R.id.tv_duration);
        tvUrgency = findViewById(R.id.tv_urgency);
        tvValue = findViewById(R.id.tv_value);
        tvLocation = findViewById(R.id.tv_location);
        tvDetails = findViewById(R.id.tv_description);

        btnAvailability = findViewById(R.id.btn_availability);
        llContractorName = findViewById(R.id.ll_contractor);
        llStartDate = findViewById(R.id.ll_start_date);
        llDuration = findViewById(R.id.ll_duration);
        llValue = findViewById(R.id.ll_value);

        btnViewProposal = findViewById(R.id.btn_view_proposal);
        btnPayment = findViewById(R.id.btn_payment);
        btnEditJob = findViewById(R.id.btn_edit_job);
        btnCompleteJob = findViewById(R.id.btn_complete_job);
        btnRemovePosting = findViewById(R.id.btn_remove_posting);
        btnDisputeJob = findViewById(R.id.btn_dispute_job);
        btnChat = findViewById(R.id.btn_chat);
        btnViewProposal.setOnClickListener(this);
        btnPayment.setOnClickListener(this);
        btnEditJob.setOnClickListener(this);
        btnCompleteJob.setOnClickListener(this);
        btnRemovePosting.setOnClickListener(this);
        btnDisputeJob.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnAvailability.setOnClickListener(this);

        // add value
        tvJobTitle.setText(currentHistoryInfo.jobName);
        switch (currentHistoryInfo.jobStatus){
            case Constant.IN_BIDDING_PROCESS:
                if (currentHistoryInfo.n_quotes > 0)
                    tvStatus.setText(R.string.quotes_available);
                else
                    tvStatus.setText(R.string.pending_quotes);
                break;
            case Constant.PENDING_APPROVAL:
                tvStatus.setText(R.string.proposal_pending);
                break;
            case Constant.IN_PROGRESS:
                tvStatus.setText(R.string.in_progress);
                break;
            case Constant.FINISH:
                tvStatus.setText(R.string.in_progress);
                break;
        }

        tvIndustry.setText(Constant.gArrSpecialization[currentHistoryInfo.industry - 1]);
        tvPostDate.setText(TimeHelper.convertFrindlyTime(currentHistoryInfo.postedDate));
        tvUrgency.setText(Constant.gArrUrgency[currentHistoryInfo.urgency]);
        tvLocation.setText(currentHistoryInfo.area);
        tvDetails.setText(currentHistoryInfo.detail);
        if (currentHistoryInfo.jobStatus < Constant.IN_PROGRESS){
            llContractorName.setVisibility(View.GONE);
            llStartDate.setVisibility(View.GONE);
            llDuration.setVisibility(View.GONE);
            llValue.setVisibility(View.GONE);
        } else {
            tvContractorName.setText(StringHelper.isEmpty(currentHistoryInfo.jobContractorName) ? "" : currentHistoryInfo.jobContractorName);
            tvStartDate.setText(StringHelper.isEmpty(currentHistoryInfo.startDate) ? "" : TimeHelper.convertFrindlyTime(currentHistoryInfo.startDate));
            tvDuration.setText(""+currentHistoryInfo.duration);
            tvValue.setText(StringHelper.isEmpty(currentHistoryInfo.budget) ? "" : "$" + currentHistoryInfo.budget);
        }

        // set disabled or enabled button
        btnPayment.setEnabled(false);
        if (currentHistoryInfo.jobStatus == Constant.IN_BIDDING_PROCESS){
            btnChat.setEnabled(false);
            btnViewProposal.setText(R.string.view_quotes);
        } else
            btnViewProposal.setText(R.string.view_proposal);
        if (currentHistoryInfo.jobStatus != Constant.FINISH)
            btnCompleteJob.setEnabled(false);
        if (currentHistoryInfo.jobStatus < Constant.IN_PROGRESS)
            btnDisputeJob.setEnabled(false);
        else
            btnRemovePosting.setEnabled(false);


    }
    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.job_details);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_availability:
                List<CalendarDay> arrDates = GlobalUtils.getArrayCalendarDateFromString(currentHistoryInfo.jobAvailability, Constant.DATE_FORMAT_DEFAULT);
                Gson gson = new Gson();
                String strArr = gson.toJson(arrDates);
                Intent intent1 =  new Intent(JobDetailActivity.this, CalendarActivity.class);
                intent1.putExtra("selectedDates", strArr);
                intent1.putExtra("isEditable", false);
                startActivity(intent1);
                break;
            case R.id.btn_view_proposal:
                Intent intent = new Intent(JobDetailActivity.this, AppliedJobActivity.class);
                intent.putExtra("currentJob", currentHistoryInfo);
                startActivityForResult(intent, REQUEST_POST_JOB_Applied);
                break;
            case R.id.btn_payment:
                break;
            case R.id.btn_edit_job:
                Intent intent4 = new Intent(JobDetailActivity.this, PostJobActivity.class);
                intent4.putExtra("job", currentHistoryInfo);
                intent4.putExtra("from", FROM_JOB_EDIT);
                startActivityForResult(intent4, REQUEST_POST_JOB_ACTIVTY);
                break;
            case R.id.btn_complete_job:
                completeJob();
                break;
            case R.id.btn_remove_posting:
                removeJob();
                break;
            case R.id.btn_dispute_job:
                Intent intent2 = new Intent(JobDetailActivity.this, DisputeJobActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_chat:
                showProgressDialog();
                ChannelManager channelManager = ChannelManager.getInstance();
                channelManager.joinOrCreateChannelWithCompletion(currentHistoryInfo.jobContractorId, 1,new CallbackListener<Channel>() {
                    @Override
                    public void onSuccess(Channel channel) {
                        hideProgressDialog();
                        goToChatActivity(channel.getSid());
                    }

                    public void onError(ErrorInfo errorInfo) {
                        hideProgressDialog();
                        Toast.makeText(JobDetailActivity.this, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
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

    void removeJob(){
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.removeJob(this, currentHistoryInfo.jobId, token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                        finish();
                    } else {
                        Toast.makeText(JobDetailActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JobDetailActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(JobDetailActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void completeJob(){
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.completeJob(this, 0, currentHistoryInfo.jobId, token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                        finish();
                    } else {
                        Toast.makeText(JobDetailActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JobDetailActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(JobDetailActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_POST_JOB_ACTIVTY:
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;

                case REQUEST_POST_JOB_Applied:
                    Intent intent1 = new Intent();
                    setResult(Activity.RESULT_OK, intent1);
                    finish();
                    break;

            }
        }
    }

}
