package com.toolsbox.contractor.view.customUI.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toolsbox.contractor.R;


/**
 * Created by LS on 12/1/2015.
 */
public abstract class PlainTextView extends MessageView {
    protected TextView m_tvMessage;

    public PlainTextView(Context context) {
        this(context, null);
    }

    public PlainTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMessage(String text) {
        m_tvMessage.setText(text);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        m_tvMessage = (TextView)findViewById(R.id.tv_message);

        setOrientation(LinearLayout.VERTICAL);
    }
}