package com.emao.application.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.emao.application.R;
import com.emao.application.http.OkHttpManager;
import com.emao.application.ui.activity.WebViewActivity;
import com.emao.application.ui.adapter.LocalImageHolderView;
import com.emao.application.ui.adapter.MenuMsgPagerAdapter;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.bean.BannerBean;
import com.emao.application.ui.bean.ClassifyBean;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.view.CustomViewPager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

import static android.support.design.widget.TabLayout.MODE_SCROLLABLE;

/**
 *
 * @author keybon
 */

public class MenuMessageFragment extends BaseFragment implements OnItemClickListener {

    private CustomViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<BaseFragment> fragments;

    private ConvenientBanner convenientBanner;
    private List<BannerBean> bannerList;

    public static MenuMessageFragment newInstance() {

        Bundle args = new Bundle();

        MenuMessageFragment menuMessageFragment = new MenuMessageFragment();
        menuMessageFragment.setArguments(args);
        return menuMessageFragment;
    }

    @Override
    protected void initRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.menu_message_fragment,container,false);
    }

    @Override
    protected void initEvents() {

        fragments = new ArrayList<>();
        bannerList = new ArrayList<>();

        convenientBanner = mRootView.findViewById(R.id.convenientBanner);

    }


    @Override
    protected void initData() {
        init();
    }

    private void initBanner(){
        //自定义你的Holder，实现更多复杂的界面，不一定是图片翻页，其他任何控件翻页亦可。
        convenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, bannerList)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.shape_banner_dot, R.drawable.shape_banner_select_dot})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                .setOnItemClickListener(this);
        //设置两秒延时自动轮播
        convenientBanner.startTurning(2000);

        //设置翻页的效果，不需要翻页效果可用不设
        //.setPageTransformer(Transformer.DefaultTransformer);    集成特效之后会有白屏现象，新版已经分离，如果要集成特效的例子可以看Demo的点击响应。
//        convenientBanner.setManualPageable(false);//设置不能手动影响
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void downLoadTitle(){
        LogUtils.e("标题 开始");
        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_MAIN_CLASSIFY, null, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("标题   result = " + result);
                MainApp.titlesList = GsonUtils.getClassifyParams(result);
                initAdapter();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("标题获取失败了");
            }
        });
    }

    private void init() {
        mViewPager = mRootView.findViewById(R.id.viewPager);
        mTabLayout = mRootView.findViewById(R.id.tabLayout);

//        fragments.add(TableRecommendFragment.newInstance(Constants.TYPE_ITEM_RECOMMEND));
//        fragments.add(TableChoicenessFragment.newInstance(Constants.TYPE_ITEM_CHOICENSS));
//        fragments.add(TableLiveFragment.newInstance(Constants.TYPE_ITEM_LIVE));
//        fragments.add(TableFocusFragment.newInstance(Constants.TYPE_ITEM_FOCUS));
//        fragments.add(TableVideoFragment.newInstance(Constants.TYPE_ITEM_VIDEO));
//        fragments.add(TableMusicFragment.newInstance(Constants.TYPE_ITEM_MUSIC));


        downLoadTitle();

        downLoadBanner();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mViewPager.resetHeight(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initAdapter(){
        if(MainApp.titlesList.size() == 0){
            MainApp.titlesList.add(new ClassifyBean("推荐","6"));
            MainApp.titlesList.add(new ClassifyBean("精选","7"));
            MainApp.titlesList.add(new ClassifyBean("直播","8"));
            MainApp.titlesList.add(new ClassifyBean("关注","9"));
            MainApp.titlesList.add(new ClassifyBean("视频","10"));
        }
        if(MainApp.titlesList.size() > 5){
            mTabLayout.setTabMode(MODE_SCROLLABLE);
        }
        for (int i = 0 ; i < MainApp.titlesList.size() ; i ++ ){
            fragments.add(TableRecommendFragment.newInstance(i));
        }


        mViewPager.setAdapter(new MenuMsgPagerAdapter(getChildFragmentManager(), fragments, MainApp.titlesList));
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onResume() {
        super.onResume();
        convenientBanner.startTurning(2000);
    }

    private void downLoadBanner(){

        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_BANNER, null, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("轮播图  result = " + result);
                bannerList = GsonUtils.getBannerParams(result);
                initBanner();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("轮播图   banner失败了");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        convenientBanner.stopTurning();
    }

    @Override
    public void onItemClick(int position) {
        //banner
        if(!TextUtils.isEmpty(bannerList.get(position).getUrl())){
            Intent intent = new Intent();
            intent.setClass(getActivity(), WebViewActivity.class);
            intent.putExtra(WebViewActivity.WEBVIEW_FROM_URL,bannerList.get(position).getUrl());
            startActivity(intent);
        }
    }

    @Override
    public void onMessageRefreshData(RefreshLayout refreshlayout) {
        super.onMessageRefreshData(refreshlayout);
        if(fragments.size() != 0){
            fragments.get(mViewPager.getCurrentItem()).onRefreshData(refreshlayout);
        } else {
            refreshlayout.finishRefresh(1000);
        }
    }

    @Override
    public void onMessageLoadData(RefreshLayout refreshlayout) {
        super.onMessageLoadData(refreshlayout);
        if(fragments.size() != 0){
            fragments.get(mViewPager.getCurrentItem()).onLoadData(refreshlayout);
        } else {
            refreshlayout.finishLoadmore(1000);
        }
    }

}
