package com.toolsbox.contractor.view.customUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.toolsbox.contractor.R;

import static com.toolsbox.contractor.common.utils.FONT.Montserrat_light;

public class IconEditText extends androidx.appcompat.widget.AppCompatEditText {

    private static final int DEFAULT_COMPOUND_DRAWABLE_SIZE = -1;
    private int compoundDrawableWidth;
    private int compoundDrawableHeight;

    public IconEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public IconEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public IconEditText(Context context) {
        super(context);
        init(context, null);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
        resizeCompoundDrawables();
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        resizeCompoundDrawables();
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right,
                                                        Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        resizeCompoundDrawables();
    }

    @Override public void setCompoundDrawablesRelative(Drawable start, Drawable top, Drawable end,
                                                       Drawable bottom) {
        super.setCompoundDrawablesRelative(start, top, end, bottom);
        resizeCompoundDrawables();
    }

    @Override public void setCompoundDrawablesRelativeWithIntrinsicBounds(int start, int top, int end,
                                                                          int bottom) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        resizeCompoundDrawables();
    }

    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(Drawable start, Drawable top,
                                                                Drawable end, Drawable bottom) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        resizeCompoundDrawables();
    }

    public void init(Context context, AttributeSet attrs) {
        final TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.IconEditText);
        compoundDrawableWidth =
                typedArray.getDimensionPixelSize(R.styleable.IconEditText_compoundDrawableWidth,
                        DEFAULT_COMPOUND_DRAWABLE_SIZE);
        compoundDrawableHeight =
                typedArray.getDimensionPixelSize(R.styleable.IconEditText_compoundDrawableWidth,
                        DEFAULT_COMPOUND_DRAWABLE_SIZE);
        typedArray.recycle();

        resizeCompoundDrawables();

    }

    private void resizeCompoundDrawables() {
        Drawable[] drawables = getCompoundDrawables();
        if (compoundDrawableWidth > 0 || compoundDrawableHeight > 0) {
            for (Drawable drawable : drawables) {
                if (drawable == null) {
                    continue;
                }

                Rect realBounds = drawable.getBounds();
                float scaleFactor = realBounds.height() / (float) realBounds.width();

                float drawableWidth = realBounds.width();
                float drawableHeight = realBounds.height();

                if (this.compoundDrawableWidth > 0) {
                    if (drawableWidth > this.compoundDrawableWidth) {
                        drawableWidth = this.compoundDrawableWidth;
                        drawableHeight = drawableWidth * scaleFactor;
                    }
                }
                if (this.compoundDrawableHeight > 0) {
                    if (drawableHeight > this.compoundDrawableHeight) {
                        drawableHeight = this.compoundDrawableHeight;
                        drawableWidth = drawableHeight / scaleFactor;
                    }
                }

                realBounds.right = realBounds.left + Math.round(drawableWidth);
                realBounds.bottom = realBounds.top + Math.round(drawableHeight);

                drawable.setBounds(realBounds);
            }
        }
        super.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    public int getCompoundDrawableWidth() {
        return compoundDrawableWidth;
    }

    public void setCompoundDrawableWidth(int compoundDrawableWidth) {
        this.compoundDrawableWidth = compoundDrawableWidth;
    }

    public int getCompoundDrawableHeight() {
        return compoundDrawableHeight;
    }

    public void setCompoundDrawableHeight(int compoundDrawableHeight) {
        this.compoundDrawableHeight = compoundDrawableHeight;
    }

}
