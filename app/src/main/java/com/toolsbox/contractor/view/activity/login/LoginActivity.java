package com.toolsbox.contractor.view.activity.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.TBApplication;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.api.LoginData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.PreferenceHelper;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.common.utils.ValidationHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.login.forgot.ForgotEmailActivity;
import com.toolsbox.contractor.view.activity.main.HomeActivity;
import com.toolsbox.contractor.view.customUI.IconEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private static String TAG = "LoginActivity";

    private ImageButton btnBack;
    private CardView cvLogin;
    private IconEditText etEmail, etPassword;
    private Button btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initVariable();
        initUI();
    }

    void initVariable(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                AppPreferenceManager.setString(Constant.PRE_FCM_TOKEN, token);
                Log.e(TAG, "FCM Token = " + token);
            }
        });
    }

    void initUI(){
        StatusBarUtil.setTransparent(this);
        cvLogin = findViewById(R.id.cv_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        cvLogin.setOnClickListener(this);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);
        btnForgotPassword.setOnClickListener(this);
    }

    void goToHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TBApplication.setgLoggedIn(false);
    }

    boolean checkValidation(){
        boolean cancel = false;
        View focusView = null;
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        etEmail.setError(null);
        etPassword.setError(null);

        // Check password
        if (StringHelper.isEmpty(password) || !ValidationHelper.isValidPassword(password)){
            focusView = etPassword;
            etPassword.setError(getString(R.string.invalid_password));
            cancel = true;
        }

        if (StringHelper.isEmpty(email)){
            focusView = etEmail;
            etEmail.setError(getString(R.string.empty_field));
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        }
        return cancel;
    }

    int signupType() {
        if (ValidationHelper.isValidEmail(etEmail.getText().toString())){
            return Constant.SIGN_TYPE_EMAIL;
        } else if(ValidationHelper.isValidPhoneNumber(etEmail.getText().toString())){
            return Constant.SIGN_TYPE_PHONE;
        } else {
            return 99;
        }
    }

    void doLogin(int loginType){
        showProgressDialog();
        JSONObject jsonParams = new JSONObject();
        try {
            switch (loginType){
                case Constant.SIGN_TYPE_EMAIL:
                    jsonParams.put("email", etEmail.getText().toString());
                    break;
                case Constant.SIGN_TYPE_PHONE:
                    jsonParams.put("phone","+" + etEmail.getText().toString());
                    break;
            }
            jsonParams.put("login_type", loginType);
            jsonParams.put("password", etPassword.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiUtils.doLogin(this, jsonParams, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                LoginData data = (LoginData) object;

                if (data != null){
                    if (data.status == 0){
                        PreferenceHelper.removePreference();
                        PreferenceHelper.savePreference(data.info.id, data.info.image_url, data.info.email, etPassword.getText().toString(),
                                data.info.business_name, data.info.business_number, data.info.business_structure, data.info.industry, data.info.speciality_title,
                                data.info.phone, data.info.address, data.info.address_lati, data.info.address_longi, data.info.fcm_token,
                                data.info.token);
                        goToHomeActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, data.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(LoginActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_login:
                if (!checkValidation()){
                    switch (signupType()){
                        case Constant.SIGN_TYPE_EMAIL:
                            doLogin(Constant.SIGN_TYPE_EMAIL);
                            break;

                        case Constant.SIGN_TYPE_PHONE:
                            doLogin(Constant.SIGN_TYPE_PHONE);
                    }
                }
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_forgot_password:
                goToForgotOptionActivity();
                break;
        }
    }

    void goToForgotOptionActivity(){
        Intent intent = new Intent(LoginActivity.this, ForgotEmailActivity.class);
        startActivity(intent);
    }
}
