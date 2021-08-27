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
import android.widget.Toast;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.GeneralJobHistoryInfo;
import com.toolsbox.contractor.common.model.JobInfo;
import com.toolsbox.contractor.common.model.QuoteInfo;
import com.toolsbox.contractor.common.model.api.HistoryInfo;
import com.toolsbox.contractor.common.model.api.JobAppliedData;
import com.toolsbox.contractor.common.model.api.JobHistoryData;
import com.toolsbox.contractor.common.model.api.JobSearchData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.view.activity.main.jobs.JobDetail2Activity;
import com.toolsbox.contractor.view.activity.main.jobs.JobDetailActivity;
import com.toolsbox.contractor.view.activity.main.market.JobResultActivity;
import com.toolsbox.contractor.view.adapter.JobHistoryAdapter;
import com.toolsbox.contractor.view.adapter.listener.PaginationScrollListener;
import com.toolsbox.contractor.view.fragment.JobFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.app.Activity.RESULT_OK;
import static com.toolsbox.contractor.TBApplication.selectedPostedby;
import static com.toolsbox.contractor.common.Constant.FROM_SEARCH_JOB;

public class NewJobFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, JobHistoryAdapter.OnItemClickListener{
    private static String TAG = "PendingJobsFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private JobHistoryAdapter  adapter;
    private LinearLayoutManager layoutManager;
    private RelativeLayout rlNodata, rlLoadingData;
    private TextView tvNoTitle;
    private RefreshJobReceiver refreshJobReceiver;

    private static final int REQUEST_CODE_JOB_DETAIL = 1;
    private static final int REQUEST_CODE_JOB_DETAIL2 = 2;

    public NewJobFragment() {}

    public static NewJobFragment newInstance(String param1, String param2) {
        NewJobFragment fragment = new NewJobFragment();
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
        fetchJobs();
        return view;
    }

    void initUI(View view){
        refreshJobReceiver = new RefreshJobReceiver();
        if (getActivity() != null)
            getActivity().registerReceiver(refreshJobReceiver, new IntentFilter(Constant.ACTION_REFRESH_NEWLY_JOBS));
        rlNodata = view.findViewById(R.id.rl_no_data);
        tvNoTitle = view.findViewById(R.id.tv_no_title);
        rlLoadingData = view.findViewById(R.id.rl_loading_data);
        tvNoTitle.setText(R.string.no_job_is_available_in_your_area);
        swipeRefresh = view.findViewById(R.id.refresh_swipe);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView =  view.findViewById(R.id.recycler_jobs);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new JobHistoryAdapter(getActivity(), new ArrayList<>());
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
        JobFragment parentFrag = (JobFragment) NewJobFragment.this.getParentFragment();
        parentFrag.refreshNewlyJobs();
    }

    void fetchJobs(){
        swipeRefresh.setRefreshing(false);
        JobFragment parentFrag = (JobFragment) NewJobFragment.this.getParentFragment();
        adapter.updateAll(parentFrag.arrNewlyJobs);
        updateBackground();
    }


    void updateBackground(){
        JobFragment parentFrag = (JobFragment) NewJobFragment.this.getParentFragment();
        if (adapter.getItemCount() == 0) {
            if (parentFrag.isNewlyLoading) {
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
    public void onItemClick(JobInfo item) {
        Intent intent = new Intent(getActivity(), JobDetail2Activity.class);
        intent.putExtra("jobId", item.id);
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
            fetchJobs();
        }
    }
}
