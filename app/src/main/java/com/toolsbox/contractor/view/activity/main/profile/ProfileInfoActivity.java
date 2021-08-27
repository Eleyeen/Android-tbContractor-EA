package com.toolsbox.contractor.view.activity.main.profile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.model.ProposalInfo;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.PreferenceHelper;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileInfoActivity extends BaseActivity {
    private static String TAG = "ProfileInfoActivity";
    private Toolbar toolbar;
    private CircleImageView ivProfile;
    private TextView tvBusinessName, tvIndustry, tvAddress, tvEmail, tvPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        initVariable();
        initUI();
    }

    void initVariable(){

    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        ivProfile = findViewById(R.id.iv_profile);
        tvBusinessName = findViewById(R.id.tv_business_name);
        tvIndustry = findViewById(R.id.tv_industry);
        tvAddress = findViewById(R.id.tv_address);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone_number);

        String imageURL = AppPreferenceManager.getString(Constant.PRE_IMAGE_URL, "");
        if (!StringHelper.isEmpty(imageURL)){
            Picasso.get().load(imageURL).into(ivProfile);
        }

        tvBusinessName.setText(PreferenceHelper.getName());

        if (PreferenceHelper.getIndustry() != 0)
            tvIndustry.setText(Constant.gArrSpecialization[PreferenceHelper.getIndustry() - 1]);
        tvAddress.setText(PreferenceHelper.getAddress());
        tvEmail.setText(PreferenceHelper.getEmail());
        tvPhone.setText(PreferenceHelper.getPhone());
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.profile_info);
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
