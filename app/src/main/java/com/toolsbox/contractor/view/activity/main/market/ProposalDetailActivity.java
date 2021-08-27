package com.toolsbox.contractor.view.activity.main.market;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.ProposalInfo;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;

public class ProposalDetailActivity extends BaseActivity {
    private static String TAG = "ProposalDetailActivity";
    private Toolbar toolbar;
    private TextView tvTitle, tvCustomer, tvAddress, tvScheduledDate, tvValue, tvDescription, tvValueTitle;
    private ProposalInfo bid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal_detail);
        initVariable();
        initUI();
    }

    void initVariable(){
        bid = (ProposalInfo) getIntent().getSerializableExtra("bid");
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        tvTitle = findViewById(R.id.tv_title);
        tvCustomer = findViewById(R.id.tv_customer);
        tvAddress = findViewById(R.id.tv_address);
        tvScheduledDate = findViewById(R.id.tv_scheduled_date);
        tvValue = findViewById(R.id.tv_value);
        tvDescription = findViewById(R.id.tv_description);
        tvValueTitle = findViewById(R.id.tv_value_title);

        tvTitle.setText(bid.job.name);
        tvCustomer.setText(bid.job.poster_name);
        tvAddress.setText(bid.job.area);
        if (bid.type == 0) {
            tvValueTitle.setText("Quotation Value");
            tvValue.setText("$" + bid.budget);
        } else {
            tvValueTitle.setText("Hourly Rate");
            tvValue.setText("$" + bid.hourly_rate);
        }
        tvScheduledDate.setText(GlobalUtils.convertScheduleRange(bid.availability_start_dates));
        tvDescription.setText(bid.description);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.proposal);
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
}
