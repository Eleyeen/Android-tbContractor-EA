package com.toolsbox.contractor.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.JobInfo;
import com.toolsbox.contractor.common.model.QuoteInfo;
import com.toolsbox.contractor.common.model.api.JobAppliedData;
import com.toolsbox.contractor.common.model.api.JobSearchData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.view.activity.basic.BaseFragment;
import com.toolsbox.contractor.view.fragment.Jobs.CompleteJobsFragment;
import com.toolsbox.contractor.view.fragment.Jobs.NewJobFragment;
import com.toolsbox.contractor.view.fragment.Jobs.PendingJobsFragment;
import com.toolsbox.contractor.view.fragment.Jobs.ProcessingJobsFragment;
import com.toolsbox.contractor.view.fragment.Jobs.ScheduledJobsFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class JobFragment extends BaseFragment {
    private static String TAG = "JobsFragment";
    private int NEWLY_CURRENT_PAGE = 1;
    private int HISTORY_CURRENT_PAGE = 1;
    public ArrayList<JobInfo> arrNewlyJobs = new ArrayList<>();
    public ArrayList<QuoteInfo> arrHistoryJobs = new ArrayList<>();
    public boolean isNewlyLoading = false;
    public boolean isHistoryLoading = false;
    public boolean isFirstLoadMyJob = true;

    public JobFragment() { }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_job, container, false);
        refreshNewlyJobs();
        initVariable();
        initUI(view);
        return view;
    }

    public void refreshNewlyJobs(){
        NEWLY_CURRENT_PAGE = 1;
        arrNewlyJobs.clear();
        fetchNewlyJobs();
    }

    public void refreshAppliedJobs(){
        HISTORY_CURRENT_PAGE = 1;
        arrHistoryJobs.clear();
        fetchJobApplied();
    }

    void initVariable(){
    }

    void initUI(View view){
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getActivity())
                .add(R.string.newly_posted, NewJobFragment.class)
                .add(R.string.pending_approval, PendingJobsFragment.class)
                .add(R.string.scheduled, ScheduledJobsFragment.class)
                .add(R.string.in_progress, ProcessingJobsFragment.class)
                .add(R.string.complete, CompleteJobsFragment.class)
                .create());
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = view.findViewById(R.id.tab_viewpager);
        viewPagerTab.setViewPager(viewPager);
        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    void fetchNewlyJobs(){
        int myId = AppPreferenceManager.getInt(Constant.PRE_ID, 0);
        String industry = AppPreferenceManager.getString(Constant.PRE_INDUSTRY, "");
        String address_lati = AppPreferenceManager.getString(Constant.PRE_ADDRESS_LATI, "");
        String address_longi = AppPreferenceManager.getString(Constant.PRE_ADDRESS_LONGI, "");
        isNewlyLoading = true;
        ApiUtils.newlyPostedJob(getActivity(), NEWLY_CURRENT_PAGE, Constant.PER_PAGE, industry, address_lati, address_longi, 200, new Notify() {
            @Override
            public void onSuccess(Object object) {
                JobSearchData data = (JobSearchData) object;
                if (data != null){
                    if (data.status == 0){
                        List<JobInfo> arrJobs = data.info;
                        int totalJobs = data.total_number;

                        // Parse history
                        for (JobInfo item : arrJobs){
                            if (item.poster_type == 1 && item.poster_id == myId)
                                continue;
                            if (!StringHelper.isEmpty(item.quoted_contractors)) {
                                List<String> arrQuotedContractor = Arrays.asList(item.quoted_contractors.split(","));
                                if (arrQuotedContractor.contains(String.valueOf(myId))){
                                    continue;
                                }
                            }
                            if (item.status == Constant.PENDING_APPROVAL && item.contractor_id != myId) {
                                continue;
                            }
                            arrNewlyJobs.add(item);
                        }
                        // Check if need more loading
                        int totalPage = (totalJobs + Constant.PER_PAGE -1) / Constant.PER_PAGE;
                        if (NEWLY_CURRENT_PAGE < totalPage)
                            fetchNewlyNextPage();
                        else
                            isNewlyLoading = false;

                        // Notify Child Job Fragments
                        Intent intentBroadcast = new Intent(Constant.ACTION_REFRESH_NEWLY_JOBS);
                        getActivity().sendBroadcast(intentBroadcast);
                    } else {
                        isNewlyLoading = false;
                        Toast.makeText(getActivity(), R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isNewlyLoading = false;
                    Toast.makeText(getActivity(), R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                isNewlyLoading = false;
                Toast.makeText(getActivity(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }


    void fetchJobApplied(){
        isFirstLoadMyJob = false;
        isHistoryLoading = true;
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.fetchJobAppliedAll(getActivity(), HISTORY_CURRENT_PAGE, Constant.PER_PAGE, token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                JobAppliedData data = (JobAppliedData) object;
                if (data != null){
                    if (data.status == 0){
                        int totalJobs = data.total_number;
                        List<QuoteInfo> arrJobs = data.info;
                        arrHistoryJobs.addAll(arrJobs);
                        // Check if need more loading
                        int totalPage = (totalJobs + Constant.PER_PAGE -1) / Constant.PER_PAGE;
                        if (HISTORY_CURRENT_PAGE < totalPage)
                            fetchHistoryNextPage();
                        else
                            isHistoryLoading = false;

                        // Notify Child Job Fragments
                        Intent intentBroadcast = new Intent(Constant.ACTION_REFRESH_APPLIED_JOBS);
                        getActivity().sendBroadcast(intentBroadcast);
                    } else {
                        isHistoryLoading = false;
                        Toast.makeText(getActivity(), R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isHistoryLoading = false;
                    Toast.makeText(getActivity(), R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                isHistoryLoading = false;
                Toast.makeText(getActivity(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }


    void fetchNewlyNextPage(){
        NEWLY_CURRENT_PAGE ++;
        fetchNewlyJobs();
    }

    void fetchHistoryNextPage(){
        HISTORY_CURRENT_PAGE ++;
        fetchJobApplied();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
