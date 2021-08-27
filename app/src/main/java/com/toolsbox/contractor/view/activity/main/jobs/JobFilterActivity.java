package com.toolsbox.contractor.view.activity.main.jobs;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.TBApplication;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.main.HomeActivity;


import java.util.Arrays;
import java.util.LinkedList;

import static com.toolsbox.contractor.common.Constant.FRAG_JOBS;
import static com.toolsbox.contractor.common.Constant.gArrPostedBy;

public class JobFilterActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "JobFilterActivity";
    private Toolbar toolbar;
    private CardView cvFilterResult;
    private Spinner spPostedBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_filter);
        initVariable();
        initUI();
    }

    void initVariable(){

    }

    void initUI(){
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorWhite), 0);
        setupToolbar();
        spPostedBy = findViewById(R.id.sp_posted_by);
        ArrayAdapter<String> spinnerPostedBy = new ArrayAdapter<>(this, R.layout.simple_spinner_item2, Arrays.asList(gArrPostedBy));
        spinnerPostedBy.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spPostedBy.setAdapter(spinnerPostedBy);


        cvFilterResult = findViewById(R.id.cv_filter_result);
        cvFilterResult.setOnClickListener(this);
        spPostedBy.setSelection(TBApplication.selectedPostedby);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.filter_jobs);
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
        switch (view.getId()){
            case R.id.cv_filter_result:
                TBApplication.selectedPostedby = spPostedBy.getSelectedItemPosition();

                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("indexFrag", FRAG_JOBS);
                startActivity(intent);
                break;
        }
    }
}
