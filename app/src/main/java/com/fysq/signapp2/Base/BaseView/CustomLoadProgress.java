package com.fysq.signapp2.Base.BaseView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.fysq.signapp2.R;


/**
 * Created by Miko on 2017/11/22.
 */

public class CustomLoadProgress extends RelativeLayout {
    public void startLoad() {
        this.setVisibility(View.VISIBLE);
    }

    public void endLoad() {
        this.setVisibility(View.GONE);
    }

    public CustomLoadProgress(Context context) {
        this(context, null);
    }

    public CustomLoadProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomLoadProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(this.getContext()).inflate(R.layout.custom_load_progress, this, true);
    }


}
