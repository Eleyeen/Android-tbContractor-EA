package com.toolsbox.contractor.view.customUI;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.toolsbox.contractor.R;

import androidx.annotation.NonNull;

public class ConfirmDialog extends AlertDialog implements
        View.OnClickListener {

    public ConfirmDialog(@NonNull Context context, String message, OnPositiveClickListener listener) {
        super(context);
        this.message = message;
        this.listener = listener;
    }

    Button btnOk;
    TextView tvMessage;
    String message;
    OnPositiveClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_confirm);
        setCancelable(false);
        initUI();
    }

    void initUI(){
        btnOk = findViewById(R.id.btn_ok);
        tvMessage = findViewById(R.id.tv_message);
        btnOk.setOnClickListener(this);
        tvMessage.setText(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                listener.onClickPositive();
                dismiss();
                break;
        }
    }

    public interface OnPositiveClickListener {
        void onClickPositive();
    }
}
