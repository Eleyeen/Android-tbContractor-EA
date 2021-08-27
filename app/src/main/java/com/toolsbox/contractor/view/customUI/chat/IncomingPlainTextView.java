package com.toolsbox.contractor.view.customUI.chat;

import android.content.Context;
import android.util.AttributeSet;

import com.toolsbox.contractor.R;


public class IncomingPlainTextView extends PlainTextView {
	public IncomingPlainTextView(Context context) {
		this(context, null);
	}
	
	public IncomingPlainTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	protected int getLayoutResource() {
		return R.layout.incoming_plain_text_view;
	}

	public void showProgress(boolean sent, boolean isPending) {
		throw new UnsupportedOperationException("progress is not displayed for incoming messages");
	}
}