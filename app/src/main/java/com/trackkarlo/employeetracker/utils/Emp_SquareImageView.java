package com.trackkarlo.employeetracker.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by precise on 4/25/2017.
 */

public class Emp_SquareImageView extends ImageView {

    int Measuredwidth = 0;
    Context mContext = null;

    public Emp_SquareImageView(Context context) {
        super(context);
        this.mContext = context;
    }

    public Emp_SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public Emp_SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Measuredwidth = display.getWidth();  // deprecated

        setMeasuredDimension(Measuredwidth/2, Measuredwidth/2);
    }

}