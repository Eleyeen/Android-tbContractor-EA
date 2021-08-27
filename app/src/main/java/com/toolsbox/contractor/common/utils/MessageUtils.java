package com.toolsbox.contractor.common.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.interFace.Notify;

public class MessageUtils {
    public static void showCustomAlertDialog(final Context context, String message, final Notify notify) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dlg_alert_no_title, null);
        final AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogTheme))
                .setView(promptsView)
                .setCancelable(true)
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        final Button btnConfirm = promptsView.findViewById(R.id.btn_confirm);
        final Button btnCancel = promptsView.findViewById(R.id.btn_cancel);
        final TextView tvMessage = promptsView.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.onSuccess(null);
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.onFail();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showCustomAlertDialogNoCancel(final Context context, String title,  String message, final Notify notify) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dlg_alert_title_no_cancel, null);
        final AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogTheme))
                .setView(promptsView)
                .setCancelable(true)
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        final Button btnConfirm = promptsView.findViewById(R.id.btn_confirm);
        final TextView tvTitle = promptsView.findViewById(R.id.tv_title);
        final TextView tvMessage = promptsView.findViewById(R.id.tv_message);
        tvTitle.setText(title);
        tvMessage.setText(message);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.onSuccess(null);
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void showCustomAlertDialog(final Context context, String title,  String message, final Notify notify) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dlg_alert_title, null);
        final AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogTheme))
                .setView(promptsView)
                .setCancelable(true)
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        final Button btnConfirm = promptsView.findViewById(R.id.btn_confirm);
        final Button btnCancel = promptsView.findViewById(R.id.btn_cancel);
        final TextView tvTitle = promptsView.findViewById(R.id.tv_title);
        final TextView tvMessage = promptsView.findViewById(R.id.tv_message);
        tvTitle.setText(title);
        tvMessage.setText(message);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.onSuccess(null);
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.onFail();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public static void showConfirmAlertDialog(final Context context, String message, final Notify notify) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dlg_confirm_new, null);
        final AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogTheme))
                .setView(promptsView)
                .setCancelable(true)
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        final Button btnConfirm = promptsView.findViewById(R.id.btn_confirm);
        final TextView tvMessage = promptsView.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.onSuccess(null);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
