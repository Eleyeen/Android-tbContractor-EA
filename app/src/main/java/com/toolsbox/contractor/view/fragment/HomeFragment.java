package com.toolsbox.contractor.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.view.activity.main.home.PostJobActivity;

import static android.app.Activity.RESULT_OK;
import static com.toolsbox.contractor.common.Constant.FRAG_JOBS;
import static com.toolsbox.contractor.common.Constant.FRAG_MARKET;
import static com.toolsbox.contractor.common.Constant.FROM_JOB_POST;


public class HomeFragment extends Fragment implements View.OnClickListener{
    private static String TAG = "HomeFragment";
    private CardView cvExploreMarket;
    private View fakeStatusBar;
    private static final int REQUEST_POST_JOB_ACTIVTY = 1;

    public HomeFragment() { }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initVariable();
        initUI(view);
        return view;
    }

    void initVariable(){
    }

    void initUI(View view){
        cvExploreMarket = view.findViewById(R.id.cv_explore_market);
        cvExploreMarket.setOnClickListener(this);
        fakeStatusBar = view.findViewById(R.id.fake_statusbar_view);
        fakeStatusBar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_explore_market:
                Intent intentBroadcast = new Intent(Constant.ACTION_CHANGED_SECTION);
                intentBroadcast.putExtra("Section", FRAG_MARKET);
                getActivity().sendBroadcast(intentBroadcast);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_POST_JOB_ACTIVTY:
                    Intent intentBroadcast = new Intent(Constant.ACTION_CHANGED_SECTION);
                    intentBroadcast.putExtra("Section", FRAG_JOBS);
                    getActivity().sendBroadcast(intentBroadcast);
                    break;

            }
        }
    }

}
