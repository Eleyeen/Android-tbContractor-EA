package com.toolsbox.contractor.view.activity.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.api.GeneralData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public class AddBankActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "AddBankActivity";

    private Toolbar toolbar;
    private EditText etInstitution, etBankNumber, etTransit;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);
        initVariable();
        initUI();
    }

    void initVariable(){

    }

    void initUI(){
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorWhite), 0);
        setupToolbar();
        etInstitution = findViewById(R.id.et_institution);
        etBankNumber = findViewById(R.id.et_bank_number);
        etTransit = findViewById(R.id.et_transit);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.add_bank_account);
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
        return !(StringHelper.isEmpty(etInstitution.getText().toString()) || StringHelper.isEmpty(etBankNumber.getText().toString()) ||
                StringHelper.isEmpty(etTransit.getText().toString()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                if (requiredFieldNotEmpty()){
                    addBankAccount();
                } else {
                    Toast.makeText(AddBankActivity.this, R.string.please_fill_all_the_required_info, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    void addBankAccount(){
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        String routingNumber = etTransit.getText().toString() + etInstitution.getText().toString();
        ApiUtils.addBankAccount(this, routingNumber, etBankNumber.getText().toString(), token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(AddBankActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddBankActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(AddBankActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
