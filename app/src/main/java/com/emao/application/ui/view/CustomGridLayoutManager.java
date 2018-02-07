package com.emao.application.ui.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

/**
 *
 * @author keybon
 */

public class CustomGridLayoutManager extends GridLayoutManager {


    private boolean isScrollEnabled = true;

    public CustomGridLayoutManager(Context context,int columNum) {
        super(context,columNum);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }

}
