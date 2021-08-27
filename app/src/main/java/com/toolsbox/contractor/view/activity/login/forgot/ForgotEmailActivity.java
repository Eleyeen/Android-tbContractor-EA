package com.toolsbox.contractor.view.activity.login.forgot;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.api.GeneralData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.common.utils.ValidationHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.main.profile.PinCodeActivity;
import com.toolsbox.contractor.view.customUI.IconEditText;

public class ForgotEmailActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "ForgotEmailActivity";

    private Toolbar toolbar;
    private CardView cvSubmit;
    private IconEditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_email);
        initUI();
    }

    void initUI(){
        StatusBarUtil.setTransparent(this);
        setupToolbar();
        cvSubmit = findViewById(R.id.cv_submit);
        cvSubmit.setOnClickListener(this);
        etEmail = findViewById(R.id.et_email);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.forgot_password);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
        }
    }

    boolean checkValidation(){
        etEmail.setError(null);

        boolean valid = true;
        String email = etEmail.getText().toString().trim();
        if (StringHelper.isEmpty(email)){
            etEmail.setError(getResources().getString(R.string.empty_field));
            valid = false;
        } else if (!ValidationHelper.isValidEmail(email)) {
            etEmail.setError(getResources().getString(R.string.invalid_email));
            valid = false;
        }

        return valid;
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

    void goToPinCodeActivity(String email){
        Intent intent = new Intent(ForgotEmailActivity.this, PinCodeActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    void requestForgotByEmail(String email){
        showProgressDialog();
        ApiUtils.forgotEmailVerify(this, email, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData) object;
                if (data != null) {
                    if (data.status == 0) {
                        goToPinCodeActivity(email);
                    } else {
                        Toast.makeText(ForgotEmailActivity.this, data.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotEmailActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(ForgotEmailActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_submit:
                if (checkValidation()) {
                    requestForgotByEmail(etEmail.getText().toString());
                }
                break;
        }
    }
}
