package com.toolsbox.contractor.view.customUI;

import android.content.Context;
import android.util.AttributeSet;

import static com.toolsbox.contractor.common.utils.FONT.Montserrat_light;

public class MyButton extends androidx.appcompat.widget.AppCompatButton {
    public MyButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyButton(Context context) {
        super(context);
        init();
    }

    public void init() {
  //      setTypeface(Montserrat_light);
    }
}
