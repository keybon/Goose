package com.emao.application.ui.common.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emao.application.R;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.bean.RecommendImageBean;
import com.emao.application.ui.common.zoomable.DoubleTapGestureListener;
import com.emao.application.ui.view.ZoomableDraweeView;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.Constants;

import java.util.ArrayList;


/**
 * @author keybon
 */
public class ZoomableViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<RecommendImageBean> mPaths;

    public ZoomableViewPagerAdapter(Context context, ArrayList<RecommendImageBean> paths) {
        mContext = context;
        mPaths = paths;
    }

    @Override
    public int getCount() {
        return mPaths != null ? mPaths.size() : 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.zoomable_view_pager_item, null);
        ZoomableDraweeView zoomableDraweeView = (ZoomableDraweeView) view.findViewById(R.id.zoomable_image);
        //允许缩放时切换
        zoomableDraweeView.setAllowTouchInterceptionWhileZoomed(true);
        //长按
        zoomableDraweeView.setIsLongpressEnabled(false);
        //双击击放大或缩小
        zoomableDraweeView.setTapListener(new DoubleTapGestureListener(zoomableDraweeView));

        String picture = Constants.GOOSE_IMAGE_URL + mPaths.get(position).getPicture();
        zoomableDraweeView.setController(CommonUtils.getDraweeDetailsController(MainApp.IMApp,zoomableDraweeView,picture,200,200));


        container.addView(view);
        view.requestLayout();
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        ZoomableDraweeView zoomableDraweeView = view.findViewById(R.id.zoomable_image);
        zoomableDraweeView.setController(null);
        container.removeView(view);
    }

}
