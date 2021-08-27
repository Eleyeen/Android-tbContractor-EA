package com.toolsbox.contractor.view.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.TBApplication;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.api.GeneralData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.MessageUtils;
import com.toolsbox.contractor.common.utils.PreferenceHelper;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.view.activity.basic.BaseFragment;
import com.toolsbox.contractor.view.activity.login.LoginActivity;
import com.toolsbox.contractor.view.activity.main.market.ReviewsActivity;
import com.toolsbox.contractor.view.activity.main.profile.BanksActivity;
import com.toolsbox.contractor.view.activity.main.profile.NotificationSettingActivity;
import com.toolsbox.contractor.view.activity.main.profile.ProfileInfoActivity;

import static com.toolsbox.contractor.common.Constant.FROM_SUBSCRIBE_AREA;


public class ProfileFragment extends BaseFragment implements View.OnClickListener{
    private static String TAG = "ProfileFragment";

    private Button btnLogout;
    private CircleImageView ivPhoto;
    private LinearLayout llCompanyInfo;
    private RelativeLayout rlMyReview, rlBankAccount, rlSubscribeArea, rlAboutJobs, rlPrivacy, rlTerms, rlCallSupport, rlEmailSupport;

    public ProfileFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initVariable();
        initUI(view);
        return view;
    }

    void initVariable(){
    }

    void initUI(View view){
        btnLogout = view.findViewById(R.id.btn_logout);
        ivPhoto = view.findViewById(R.id.iv_profile);
        llCompanyInfo = view.findViewById(R.id.ll_company_info);
        rlMyReview = view.findViewById(R.id.rl_my_review);
        rlBankAccount = view.findViewById(R.id.rl_bank);
        rlSubscribeArea = view.findViewById(R.id.rl_subscribe_area);
        rlAboutJobs = view.findViewById(R.id.rl_about_jobs);
        rlPrivacy = view.findViewById(R.id.rl_privacy);
        rlTerms = view.findViewById(R.id.rl_terms);
        rlCallSupport = view.findViewById(R.id.rl_call_specialist_support);
        rlEmailSupport = view.findViewById(R.id.rl_email_sepcialist_support);

        btnLogout.setOnClickListener(this);
        llCompanyInfo.setOnClickListener(this);
        rlMyReview.setOnClickListener(this);
        rlBankAccount.setOnClickListener(this);
        rlSubscribeArea.setOnClickListener(this);
        rlAboutJobs.setOnClickListener(this);
        rlAboutJobs.setOnClickListener(this);
        rlPrivacy.setOnClickListener(this);
        rlTerms.setOnClickListener(this);
        rlCallSupport.setOnClickListener(this);
        rlEmailSupport.setOnClickListener(this);

        String imageURL = AppPreferenceManager.getString(Constant.PRE_IMAGE_URL, "");
        if (!StringHelper.isEmpty(imageURL)){
            Picasso.get().load(imageURL).into(ivPhoto);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_logout:
                showLogoutDialog();
                break;
            case R.id.ll_company_info:
                intent = new Intent(getActivity(), ProfileInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_my_review:
                intent = new Intent(getActivity(), ReviewsActivity.class);
                intent.putExtra("from", Constant.FROM_MY_REVIEW);
                startActivity(intent);
                break;
            case R.id.rl_bank:
                intent = new Intent(getActivity(), BanksActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_subscribe_area:
                intent = new Intent(getActivity(), NotificationSettingActivity.class);
                intent.putExtra("from", FROM_SUBSCRIBE_AREA);
                startActivity(intent);
                break;

        }
    }

    void showLogoutDialog(){
        MessageUtils.showCustomAlertDialog(getActivity(), getResources().getString(R.string.logout), getResources().getString(R.string.are_you_sure_you_want_to_logout), new Notify() {
            @Override
            public void onSuccess(Object object) {
                logoutFromServer();
            }

            @Override
            public void onFail() {

            }
        });
    }

    void logoutFromServer(){
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.logout(getActivity(), token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData) object;
                if (data != null){
                    if (data.status == 0){
                        Log.e(TAG, "Logout Success!");
                        TBApplication.get().getChatClientManager().unsetFCMToken();
                        goToLoginActivity();
                    } else {
                        Toast.makeText(getActivity(), data.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(getActivity(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void goToLoginActivity(){
        PreferenceHelper.removePreference();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



}
