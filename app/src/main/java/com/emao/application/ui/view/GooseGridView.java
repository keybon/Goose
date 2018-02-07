package com.emao.application.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 *
 * @author keybon
 */

public class GooseGridView extends GridView {

    public GooseGridView(Context context) {
        super(context);
    }

    public GooseGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GooseGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
