package com.toolsbox.contractor.view.activity.main.jobs;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.customUI.ConfirmDialog;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public class DisputeJobActivity extends BaseActivity implements View.OnClickListener, ConfirmDialog.OnPositiveClickListener{
    private static String TAG = "DisputeJobActivity";
    private Toolbar toolbar;
    private Button btnSubmit;
    private EditText etDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispute_job);
        initUI();
    }

    void initUI(){
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorWhite), 0);
        setupToolbar();
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
        etDetails = findViewById(R.id.et_details);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.dispute_job);
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

    boolean requiredFieldNotEmpty(){
        return !(StringHelper.isEmpty(etDetails.getText().toString()));
    }


    void disputeJob(){
        showProgressDialog();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        showConfirmAlertDialog(R.string.your_dispute_has_been_sent);
                    }
                });
            }
        }, 3000);
    }

    void showConfirmAlertDialog(int strId){
        ConfirmDialog dlg = new ConfirmDialog(this, getResources().getString(strId), this);
        dlg.show();
    }

    // Confirm Dialog button Listener
    @Override
    public void onClickPositive() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                if (requiredFieldNotEmpty()){
                    disputeJob();
                } else {
                    Toast.makeText(DisputeJobActivity.this, R.string.please_fill_all_the_required_info, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
