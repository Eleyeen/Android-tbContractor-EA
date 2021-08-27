package com.toolsbox.contractor.view.activity.basic;

import android.os.Bundle;

import com.kaopiz.kprogresshud.KProgressHUD;

import androidx.appcompat.app.AppCompatActivity;



/**
 * Created by LS on 9/14/2017.
 */

public class BaseActivity extends AppCompatActivity {
    private static String TAG = "BaseActivity";
    private OverlayProgressDialog overlayProgressDialog;

    public  void showProgressDialog() {
        overlayProgressDialog = new OverlayProgressDialog();
        overlayProgressDialog.show(getSupportFragmentManager(), "");
    }

    public void hideProgressDialog() {
        overlayProgressDialog.dismissAllowingStateLoss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



}
