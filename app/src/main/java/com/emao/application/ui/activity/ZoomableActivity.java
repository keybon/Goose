package com.emao.application.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.ui.bean.RecommendFriendBean;
import com.emao.application.ui.bean.RecommendImageBean;
import com.emao.application.ui.common.adapter.ZoomableViewPagerAdapter;

import java.util.ArrayList;


/**
 * @author keybon
 */
public class ZoomableActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private static final String EXTRA_ZOOMABLE_BEAN = "extra_zoomable_bean";
    private static final String EXTRA_ZOOMABLE_INDEX = "extra_zoomable_index";

    private ViewPager mViewPager;
    private TextView mZoomableIndex;
    private RecommendFriendBean recommendBean;
    private ArrayList<RecommendImageBean> mPaths;
    private int mIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_zoommable);
        super.onCreate(savedInstanceState);
        getExtraData();
        setupViewPager();
    }

    private void getExtraData() {
        mPaths = new ArrayList<>();
        recommendBean = (RecommendFriendBean) getIntent().getSerializableExtra(EXTRA_ZOOMABLE_BEAN);
        if(recommendBean != null && recommendBean.getImage() != null){
            mPaths =recommendBean.getImage();
        }
        mIndex = getIntent().getIntExtra(EXTRA_ZOOMABLE_INDEX, 0);
    }

    @Override
    public void initView() {
        super.initView();
        mViewPager = findViewById(R.id.view_pager);
        mZoomableIndex = findViewById(R.id.zoomable_index);
    }

    private void setupViewPager() {
        mViewPager.setAdapter(new ZoomableViewPagerAdapter(this, mPaths));
        mViewPager.setCurrentItem(mIndex);
        mZoomableIndex.setText(mIndex + 1 + "/" + mPaths.size());
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mZoomableIndex.setText(position + 1 + "/" + mPaths.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static void goToPage(Context context, RecommendFriendBean bean, int index) {
        Intent intent = new Intent(context, ZoomableActivity.class);
        intent.putExtra(EXTRA_ZOOMABLE_BEAN, bean);
        intent.putExtra(EXTRA_ZOOMABLE_INDEX, index);
        context.startActivity(intent);
    }
}
