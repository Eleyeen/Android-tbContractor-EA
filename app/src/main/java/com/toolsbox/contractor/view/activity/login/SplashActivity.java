package com.toolsbox.contractor.view.activity.login;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.api.LoginData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.PreferenceHelper;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.main.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static com.toolsbox.contractor.common.Constant.PRE_EMAIL;
import static com.toolsbox.contractor.common.Constant.PRE_FCM_TOKEN;
import static com.toolsbox.contractor.common.Constant.PRE_PASSWORD;
import static com.toolsbox.contractor.common.Constant.PRE_PHONE;
import static com.toolsbox.contractor.common.Constant.PRE_TOKEN;
import static com.toolsbox.contractor.common.Constant.RC_HANDLE_PERMISSION;

public class SplashActivity extends BaseActivity {
    private static String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarUtil.setTranslucent(this, 0);
        initVariable();
    }

    void initVariable(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                checkAllPermission();
            }
        }, 2000);
    }



    void checkAllPermission(){
        int phone_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int write_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int camera_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (write_permission == PackageManager.PERMISSION_GRANTED
                &&camera_permission == PackageManager.PERMISSION_GRANTED
                && phone_permission == PackageManager.PERMISSION_GRANTED
                && read_permission == PackageManager.PERMISSION_GRANTED) {
            splashProcess();
        } else {
            requestPermission();
        }
    }

    void splashProcess() {
        goToStartupActivity();
    }

    void goToStartupActivity(){
        Intent intent = new Intent(this, StartupActivity.class);
        startActivity(intent);
        finish();
    }


    void requestPermission(){
        final String[] permissions = new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_PERMISSION);
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean getPermission = false;
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
            splashProcess();
            getPermission = true;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        if (!getPermission) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
            builder.setTitle(getString(R.string.permission_title_alert))
                    .setMessage(getString(R.string.permission_content_alert))
                    .setPositiveButton(getString(R.string.ok), listener)
                    .show();
        }
    }



}
