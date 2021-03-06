package com.toolsbox.contractor.view.customUI.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.toolsbox.contractor.R;


public class OutgoingPlainTextView extends PlainTextView {
	private ProgressBar progressBar;
	
	public OutgoingPlainTextView(Context context) {
		this(context, null);
	}
	
	public OutgoingPlainTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void init(Context context) {
		super.init(context);

		progressBar = (ProgressBar)findViewById(R.id.sending_progress);
	}


	protected int getLayoutResource() {
		return R.layout.outgoing_plain_text_view;
	}

	@Override
	public void showProgress(boolean sent, boolean isPending) {
		if (sent) {
			progressBar.setVisibility(View.INVISIBLE);
		} else {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
}