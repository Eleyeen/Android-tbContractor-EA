package com.toolsbox.contractor.view.customUI;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.model.ProposalInfo;

import androidx.annotation.NonNull;

public class PaymentHoldDialog extends AlertDialog implements
        View.OnClickListener {

    public PaymentHoldDialog(@NonNull Context context, String message, ProposalInfo item, OnPositiveClickListener listener) {
        super(context);
        this.message = message;
        this.listener = listener;
        this.item = item;
    }

    Button btnContinue, btnCancel;
    TextView tvMessage;
    String message;
    OnPositiveClickListener listener;
    ProposalInfo item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_payment_hold);
        setCancelable(false);
        initUI();
    }

    void initUI(){
        btnContinue = findViewById(R.id.btn_continue);
        btnCancel = findViewById(R.id.btn_cancel);
        tvMessage = findViewById(R.id.tv_message);
        btnContinue.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        tvMessage.setText(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_continue:
                listener.onClickPositive(item);
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
        }
    }

    public interface OnPositiveClickListener {
        void onClickPositive(ProposalInfo item);
    }
}
