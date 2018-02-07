package com.emao.application.ui.callback;

import android.view.View;

/**
 *
 * @author keybon
 */

public interface OnRecyclerViewItemClickListener {

    public void onItemClick(View view,int position);
    public void onItemLongClick(View view,int position);
    public void onPhotoClick(View view,int position);
}
