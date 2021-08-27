package com.toolsbox.contractor.view.activity.basic;

import android.os.Bundle;

import com.kaopiz.kprogresshud.KProgressHUD;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    private OverlayProgressDialog overlayProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public void showProgressDialog() {
        overlayProgressDialog = new OverlayProgressDialog();
        if (getActivity() != null){
            overlayProgressDialog.show(getActivity().getSupportFragmentManager(), "");
        }
    }

    public void hideProgressDialog() {
        overlayProgressDialog.dismissAllowingStateLoss();
    }
}
