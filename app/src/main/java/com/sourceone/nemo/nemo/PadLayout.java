package com.sourceone.nemo.nemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.GridView;

/**
 * Created by SourceOne on 12.09.2016.
 */
public class PadLayout extends FrameLayout {
    public PadLayout(Context context) {
        super(context);
    }

    public PadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PadLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // This is the key that will make the height equivalent to its width
    }
}
