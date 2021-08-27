package com.toolsbox.contractor.view.fragment.Jobs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.model.QuoteInfo;
import com.toolsbox.contractor.view.activity.main.jobs.JobDetail2Activity;
import com.toolsbox.contractor.view.adapter.JobAppliedAdapter;
import com.toolsbox.contractor.view.fragment.JobFragment;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ScheduledJobsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, JobAppliedAdapter.OnItemClickListener{
    private static String TAG = "ScheduledJobsFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private JobAppliedAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RelativeLayout rlNodata, rlLoadingData;
    private TextView tvNoTitle;
    private RefreshJobReceiver refreshJobReceiver;

    private static final int REQUEST_CODE_JOB_DETAIL = 1;
    private static final int REQUEST_CODE_JOB_DETAIL2 = 2;

    public ScheduledJobsFragment() {}

    public static ScheduledJobsFragment newInstance(String param1, String param2) {
        ScheduledJobsFragment fragment = new ScheduledJobsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_jobs, container, false);
        initUI(view);
        return view;
    }

    void initUI(View view){
        refreshJobReceiver = new RefreshJobReceiver();
        if (getActivity() != null)
            getActivity().registerReceiver(refreshJobReceiver, new IntentFilter(Constant.ACTION_REFRESH_APPLIED_JOBS));
        rlNodata = view.findViewById(R.id.rl_no_data);
        rlLoadingData = view.findViewById(R.id.rl_loading_data);
        tvNoTitle = view.findViewById(R.id.tv_no_title);
        tvNoTitle.setText(R.string.no_scheduled_job);
        swipeRefresh = view.findViewById(R.id.refresh_swipe);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView =  view.findViewById(R.id.recycler_jobs);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new JobAppliedAdapter(getActivity(), new ArrayList<>());
        adapter.addListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (getActivity() != null)
                getActivity().unregisterReceiver(refreshJobReceiver);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        adapter.clear();
        // call parent job fragment
        JobFragment parentFrag = (JobFragment) ScheduledJobsFragment.this.getParentFragment();
        parentFrag.refreshAppliedJobs();
    }


    void fetchJobs(){
        swipeRefresh.setRefreshing(false);
        JobFragment parentFrag = (JobFragment) ScheduledJobsFragment.this.getParentFragment();
        if (parentFrag.isFirstLoadMyJob) {
            parentFrag.refreshAppliedJobs();
        }
        ArrayList<QuoteInfo> arrTemp = new ArrayList<>();
        for (QuoteInfo item : parentFrag.arrHistoryJobs) {
            if (item.job.status == Constant.SCHEDULED || item.job.status == Constant.EN_ROUTE) {
                arrTemp.add(item);
            }
        }
        adapter.updateAll(arrTemp);
        updateBackground();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchJobs();
    }

    void updateBackground(){
        JobFragment parentFrag = (JobFragment) ScheduledJobsFragment.this.getParentFragment();
        if (adapter.getItemCount() == 0) {
            if (parentFrag.isHistoryLoading) {
                rlLoadingData.setVisibility(View.VISIBLE);
                rlNodata.setVisibility(View.GONE);
            } else {
                rlLoadingData.setVisibility(View.GONE);
                rlNodata.setVisibility(View.VISIBLE);
            }
        } else {
            rlLoadingData.setVisibility(View.GONE);
            rlNodata.setVisibility(View.GONE);
        }
    }


    @Override
    public void onItemClick(QuoteInfo item) {
        Intent intent = new Intent(getActivity(), JobDetail2Activity.class);
        intent.putExtra("jobId", item.job.id);
        startActivityForResult(intent, REQUEST_CODE_JOB_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_JOB_DETAIL:
                    onRefresh();
                    break;

            }
        }
    }

    private class RefreshJobReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "received broad cast");
            fetchJobs();
        }
    }
}
