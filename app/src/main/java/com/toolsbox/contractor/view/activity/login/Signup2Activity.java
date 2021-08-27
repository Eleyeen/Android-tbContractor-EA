package com.toolsbox.contractor.view.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.ContractorInfo;
import com.toolsbox.contractor.common.model.api.LoginData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.DeviceUtil;
import com.toolsbox.contractor.common.utils.PreferenceHelper;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.common.utils.ValidationHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.main.HomeActivity;
import com.toolsbox.contractor.view.customUI.IconEditText;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.cardview.widget.CardView;

public class Signup2Activity extends BaseActivity implements View.OnClickListener{
    private static String TAG = "Signup2Activity";
    private ImageButton btnBack;
    private CardView cvSignup;
    private IconEditText etEmail, etPassword;
    private AppCompatCheckBox cbTerms;
    private TextView tvTerms;

    private ContractorInfo currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
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
        currentItem = (ContractorInfo) getIntent().getSerializableExtra("item");
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        btnBack = findViewById(R.id.btn_back);
        cvSignup = findViewById(R.id.cv_register);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        cbTerms = findViewById(R.id.cb_terms);
        tvTerms = findViewById(R.id.tv_terms);
        btnBack.setOnClickListener(this);
        cvSignup.setOnClickListener(this);

        initTermsText();
    }

    void initTermsText(){
        // Terms text initialize
        // Initialize a new ClickableSpan to display red background
        String text = "I agree to the Terms of Service and Privacy Policy";
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(getResources().getString(R.string.i_agree_to_the_terms));

        ClickableSpan TermsClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Do something
                Toast.makeText(Signup2Activity.this, "Terms of Service", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorWhite));
            }
        };

        ClickableSpan PrivacyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Do something
                Toast.makeText(Signup2Activity.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorWhite));
            }
        };


        // Apply the clickable text to the span
        ssBuilder.setSpan(
                TermsClickableSpan, // Span to add
                text.indexOf("Terms of Service"), // Start of the span (inclusive)
                text.indexOf("Terms of Service") + String.valueOf("Terms of Service").length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        );

        // Apply the clickable text to the span
        ssBuilder.setSpan(
                PrivacyClickableSpan,
                text.indexOf("Privacy Policy"),
                text.indexOf("Privacy Policy") + String.valueOf("Privacy Policy").length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        // Display the spannable text to TextView
        tvTerms.setText(ssBuilder);
     //   tvTerms.setHighlightColor(getResources().getColor(R.color.colorWhite));

        // Specify the TextView movement method
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }

    boolean checkValidationSignTypeEmail(){
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

        // Check Email
        if (StringHelper.isEmpty(email) || !ValidationHelper.isValidEmail(email)){
            focusView = etEmail;
            etEmail.setError(getString(R.string.invalid_email));
            cancel = true;
        }
        if (cancel){
            focusView.requestFocus();
        }
        return cancel;
    }

    void goToMainScreen(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    void doSignup(){
        showProgressDialog();
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("email",  etEmail.getText().toString());
            jsonParams.put("password", etPassword.getText().toString());
            jsonParams.put("business_name", currentItem.business_name);
            jsonParams.put("business_number", currentItem.business_number);
            jsonParams.put("business_structure", currentItem.business_structure);
            jsonParams.put("industry", currentItem.industry);
            jsonParams.put("speciality_title", currentItem.speciality_title);
            jsonParams.put("phone", currentItem.phone);
            jsonParams.put("address", currentItem.address);
            jsonParams.put("address_lati", currentItem.address_lati);
            jsonParams.put("address_longi", currentItem.address_longi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiUtils.doSignup(this, jsonParams, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                LoginData data = (LoginData) object;
                if (data != null){
                    switch (data.status){
                        case 0:
                            PreferenceHelper.removePreference();
                            PreferenceHelper.savePreference(data.info.id, "", data.info.email, etPassword.getText().toString(),
                                    data.info.business_name, data.info.business_number, data.info.business_structure, data.info.industry, data.info.speciality_title,
                                    data.info.phone, data.info.address, data.info.address_lati, data.info.address_longi, data.info.fcm_token,
                                    data.info.token);
                            goToMainScreen();
                            break;
                        case 1:
                            Toast.makeText(Signup2Activity.this, R.string.duplicated_email_address, Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(Signup2Activity.this, R.string.duplicated_phone_number, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(Signup2Activity.this, R.string.registration_fail, Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    Toast.makeText(Signup2Activity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(Signup2Activity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.cv_register:
                if (!checkValidationSignTypeEmail()){
                    DeviceUtil.closeKeyboard(Signup2Activity.this);
                    if (cbTerms.isChecked())
                        doSignup();
                    else
                        Toast.makeText(this, R.string.you_must_agree, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
