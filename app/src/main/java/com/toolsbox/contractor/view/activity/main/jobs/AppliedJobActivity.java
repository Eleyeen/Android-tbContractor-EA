package com.toolsbox.contractor.view.activity.main.jobs;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.ContractorInfo;
import com.toolsbox.contractor.common.model.GeneralJobHistoryInfo;
import com.toolsbox.contractor.common.model.ProposalInfo;
import com.toolsbox.contractor.common.model.api.GeneralData;
import com.toolsbox.contractor.common.model.api.JobProposalData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.main.market.ReviewsActivity;
import com.toolsbox.contractor.view.adapter.JobAppliedAdapter;
import com.toolsbox.contractor.view.customUI.ConfirmDialog;
import com.toolsbox.contractor.view.customUI.PaymentHoldDialog;

import java.util.ArrayList;
import java.util.List;

public class AppliedJobActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,  ConfirmDialog.OnPositiveClickListener, PaymentHoldDialog.OnPositiveClickListener {

    private static final String TAG = "AppliedJobActivity";

    private SwipeRefreshLayout swipeRefresh;
    private JobAppliedAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private RelativeLayout rlNodata;
    private TextView tvNoTitle;

    private GeneralJobHistoryInfo currentHistoryInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applied_job);
        initVariable();
        initUI();
        swipeRefresh.setRefreshing(true);
        fetchProposal();
    }

    void initVariable(){
        currentHistoryInfo = (GeneralJobHistoryInfo) getIntent().getSerializableExtra("currentJob");
    }

    void initUI(){
        setupToolbar();
        rlNodata = findViewById(R.id.rl_no_data);
        tvNoTitle = findViewById(R.id.tv_no_title);
        tvNoTitle.setText(R.string.no_quotation);
        recyclerView =  findViewById(R.id.recycler_proposals);
        recyclerView.setHasFixedSize(true);
        swipeRefresh = findViewById(R.id.refresh_swipe);
        swipeRefresh.setOnRefreshListener(this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new JobAppliedAdapter(this, new ArrayList<>());
      //  adapter.addListener(this);
        recyclerView.setAdapter(adapter);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(currentHistoryInfo.jobName);
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
    public void onRefresh() {
        adapter.clear();
        fetchProposal();
    }

    void fetchProposal(){
        int type = currentHistoryInfo.jobStatus > Constant.PENDING_APPROVAL? 1 : 0;
        int contractor_id = currentHistoryInfo.jobStatus > Constant.PENDING_APPROVAL? currentHistoryInfo.jobContractorId : 0;
        ApiUtils.fetchProposal(this, currentHistoryInfo.jobId, type, contractor_id, new Notify() {
            @Override
            public void onSuccess(Object object) {
                swipeRefresh.setRefreshing(false);
                JobProposalData data = (JobProposalData) object;
                if (data != null){
                    if (data.status == 0){
                        List<ProposalInfo> arrProposal = data.info;
                //        adapter.addAll(arrProposal);
                    } else {
                        Toast.makeText(AppliedJobActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AppliedJobActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
                updateBackground();
            }

            @Override
            public void onFail() {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(AppliedJobActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                updateBackground();
            }
        });
    }

    void updateBackground(){
        if (adapter.getItemCount() < 1)
            rlNodata.setVisibility(View.VISIBLE);
        else
            rlNodata.setVisibility(View.GONE);
    }

//    @Override
//    public void onTapReview(ProposalInfo item) {
//        Intent intent = new Intent(this, ReviewsActivity.class);
//        intent.putExtra("contractor", item.contractor);
//        startActivity(intent);
//    }
//
//    @Override
//    public void onTapChat(ProposalInfo item) {
//
//    }
//
//    @Override
//    public void onTapAccept(ProposalInfo item) {
//        showPaymentHoldDialog(item);
//    }
//
//    @Override
//    public void onTapDecline(ProposalInfo item) {
//        showProgressDialog();
//        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
//        ApiUtils.declineBidder(this, item.id, token, new Notify() {
//            @Override
//            public void onSuccess(Object object) {
//                hideProgressDialog();
//                GeneralData data = (GeneralData)object;
//                if (data != null){
//                    if (data.status == 0){
//                        showConfirmAlertDialog(R.string.you_declined_proposal);
//                    } else {
//                        Toast.makeText(AppliedJobActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(AppliedJobActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFail() {
//                hideProgressDialog();
//                Toast.makeText(AppliedJobActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    void showPaymentHoldDialog(ProposalInfo item){
        PaymentHoldDialog dlg = new PaymentHoldDialog(this, getResources().getString(R.string.payment_hold), item, this);
        dlg.show();
    }


    void showConfirmAlertDialog(int strId){
        ConfirmDialog dlg = new ConfirmDialog(this, getResources().getString(strId), this);
        dlg.show();
    }

    // Confirm Dialog button Listener
    @Override
    public void onClickPositive() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    // Payment hold Dialog button Listener
    @Override
    public void onClickPositive(ProposalInfo item) {
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.acceptBidder(this, item.id, token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                        showConfirmAlertDialog(R.string.you_accepted_successfully);
                    } else {
                        Toast.makeText(AppliedJobActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AppliedJobActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(AppliedJobActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
