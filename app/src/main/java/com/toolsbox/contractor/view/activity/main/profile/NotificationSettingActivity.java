package com.toolsbox.contractor.view.activity.main.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.model.ContractorInfo;
import com.toolsbox.contractor.common.utils.DeviceUtil;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.login.Signup2Activity;
import com.toolsbox.contractor.view.activity.login.SignupActivity;
import com.toolsbox.contractor.view.activity.main.home.IndustrySelectActivity;
import com.toolsbox.contractor.view.customUI.IconEditText;
import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;

import co.lujun.androidtagview.TagContainerLayout;

import static com.toolsbox.contractor.common.Constant.FROM_SUBSCRIBE_AREA;

public class NotificationSettingActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "NotificationSettingActivity";
    private static final int REQUEST_CODE_INDUSTRY_SELECT = 1;

    private Toolbar toolbar;
    private LinearLayout llArea, llIndustry;
    private TextView tvTitle;
    private IndicatorSeekBar isbRange;
    private IconEditText etIndustry;
    private RelativeLayout rlIndustry;
    private TagContainerLayout tagIndustry;
    private Button btnUpdate;
    private ArrayList<Integer> selectedArrIndustry;
    private int from = FROM_SUBSCRIBE_AREA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);
        initVariable();
        initUI();
    }

    void initVariable(){
        from = getIntent().getIntExtra("from", FROM_SUBSCRIBE_AREA);
        selectedArrIndustry = new ArrayList<>();
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        llArea = findViewById(R.id.ll_area);
        llIndustry = findViewById(R.id.ll_industry);
        tvTitle = findViewById(R.id.tv_title);
        etIndustry = findViewById(R.id.et_industry);
        rlIndustry = findViewById(R.id.rl_industry);
        rlIndustry.setOnClickListener(this);
        tagIndustry = findViewById(R.id.tag_industry);
        isbRange = findViewById(R.id.isb_range);
        isbRange.setIndicatorTextFormat("${PROGRESS}km");
        btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(this);

        tvTitle.setText(from == FROM_SUBSCRIBE_AREA ? R.string.subscribe_address_radius : R.string.subscribe_jobs_in);
        if (from == FROM_SUBSCRIBE_AREA){
            llIndustry.setVisibility(View.GONE);
        } else {
            llArea.setVisibility(View.GONE);
        }
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.notification_setting);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_industry:
                Intent intent1 = new Intent(this, IndustrySelectActivity.class);
                intent1.putExtra("isMultiple", true);
                startActivityForResult(intent1, REQUEST_CODE_INDUSTRY_SELECT);
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_INDUSTRY_SELECT:
                    tagIndustry.removeAllTags();
                    String temp = data.getStringExtra("selectedIndustry");
                    Gson gson = new Gson();
                    selectedArrIndustry = new ArrayList<>();
                    selectedArrIndustry = gson.fromJson(temp, new TypeToken<ArrayList<Integer>>(){}.getType());
                    if (selectedArrIndustry.size() > 0){
                        for (Integer i : selectedArrIndustry){
                            tagIndustry.addTag(Constant.gArrSpecialization[i]);
                        }
                    }
                    break;
            }
        }
    }
}
