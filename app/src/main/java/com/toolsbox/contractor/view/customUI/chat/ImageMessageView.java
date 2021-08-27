package com.toolsbox.contractor.view.customUI.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.toolsbox.contractor.R;

/**
 * Created by dilli on 1/29/2016.
 */
public abstract class ImageMessageView extends MessageView {
    private ImageView m_ivImage;
    private ProgressBar progressBar;
    private RelativeLayout m_rlSendingProgress;

    public ImageMessageView(Context context) {
        this(context, null);
    }

    public ImageMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        m_ivImage = findViewById(R.id.image);
        progressBar = (ProgressBar)findViewById(R.id.sending_progress);
        m_rlSendingProgress = findViewById(R.id.rl_sending_progress);

        setOrientation(LinearLayout.VERTICAL);
    }

    public ImageView getImageView() {
        return m_ivImage;
    }


    @Override
    public void showProgress(boolean sent, boolean isPending) {
        if (isPending){
            m_rlSendingProgress.setVisibility(View.VISIBLE);
        } else {
            m_rlSendingProgress.setVisibility(View.INVISIBLE);
        }
    }


}