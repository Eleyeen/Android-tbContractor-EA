package com.toolsbox.contractor.view.customUI;

import android.content.Context;
import android.util.AttributeSet;


import static com.toolsbox.contractor.common.utils.FONT.Montserrat_Bold;
import static com.toolsbox.contractor.common.utils.FONT.Montserrat_light;

public class MyTextView extends androidx.appcompat.widget.AppCompatTextView {

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
//        setTypeface(Montserrat_light);
    }

    public void setBoldBont(){
        setTypeface(Montserrat_Bold);
    }
}
