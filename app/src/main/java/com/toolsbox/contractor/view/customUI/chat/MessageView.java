package com.toolsbox.contractor.view.customUI.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;

public abstract class MessageView extends LinearLayout {
	protected TextView m_tvTime;
	protected TextView m_tvDateSeparator;

	public MessageView(Context context) {
		this(context, null);
	}
	
	public MessageView(Context context, AttributeSet attrs) {
		super(context, attrs, R.attr.messageViewStyle);
		init(context);
	}

	protected void init(Context context) {
		LayoutInflater.from(context).inflate(getLayoutResource(), this);
		m_tvTime = (TextView)findViewById(R.id.tv_time);
		m_tvDateSeparator = (TextView)findViewById(R.id.tv_date);
	}

	public void setTimeText(String text) {
		m_tvTime.setText(text);
	}
	
	public void displayDateSeparator(String text) {
		m_tvDateSeparator.setVisibility(View.VISIBLE);
		m_tvDateSeparator.setText(text);
	}
	
	public void hideDateSeparator() {
		m_tvDateSeparator.setVisibility(View.GONE);
	}

	public abstract void showProgress(boolean sent, boolean isPending);

	public static MessageView newView(int type, Context context) {
		MessageView view = null;
			switch (type) {
				case Constant.TYPE_INCOMING_PLAIN_TEXT:
					view = new IncomingPlainTextView(context);
					break;

				case Constant.TYPE_OUTGOING_PLAIN_TEXT:
					view = new OutgoingPlainTextView(context);
					break;

				case Constant.TYPE_INCOMING_IMAGE:
					view = new IncomingImageMessageView(context);
					break;

				case Constant.TYPE_OUTGOING_IMAGE:
					view = new OutgoingImageMessageView(context);
					break;

		}

		if (view != null) {
			view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		}

		return view;
	}

	protected abstract int getLayoutResource();
}