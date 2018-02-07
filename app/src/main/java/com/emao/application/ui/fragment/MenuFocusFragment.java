package com.emao.application.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.emao.application.R;
import com.emao.application.ui.adapter.MenuMsgPagerAdapter;
import com.emao.application.ui.bean.ClassifyBean;
import com.emao.application.ui.utils.Constants;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * @author keybon
 */

public class MenuFocusFragment extends BaseFragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<BaseFragment> fragments;
    private List<ClassifyBean> titles;

    public static MenuFocusFragment newInstance() {

        Bundle args = new Bundle();

        MenuFocusFragment menuFocusFragment = new MenuFocusFragment();
        menuFocusFragment.setArguments(args);
        return menuFocusFragment;
    }

    @Override
    protected void initRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.menu_focus_fragment, container, false);
    }

    @Override
    protected void initEvents() {
        fragments = new ArrayList<>();
        titles = new ArrayList<>();
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        downLoadTitles();
    }

    private void downLoadTitles() {

        titles.add(new ClassifyBean("关注", "1"));
        titles.add(new ClassifyBean("动态", "2"));
        titles.add(new ClassifyBean("私信", "3"));
        initAdapter();

//        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_MAIN_CLASSIFY, null, new OkHttpManager.DataCallBack() {
//            @Override
//            public void requestSuccess(String result) throws Exception {
//                LogUtils.e("result = " + result);
//                titles = GsonUtils.getClassifyParams(result);
//                initAdapter();
//            }
//
//            @Override
//            public void requestFailure(Request request, IOException e) {
//                LogUtils.e("标题获取失败了");
//            }
//        });
    }

    private void initAdapter() {

        mViewPager.setAdapter(new MenuMsgPagerAdapter(getChildFragmentManager(), fragments, titles));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);

    }


    private void init() {

        mViewPager = mRootView.findViewById(R.id.menu_focus_viewPager);
        mTabLayout = mRootView.findViewById(R.id.menu_focus_tabLayout);

        fragments.add(FocusTableFocusFragment.newInstance(Constants.TYPE_ITEM_FOCUS_FOCUS));
        fragments.add(FocusTableDynamicFragment.newInstance(Constants.TYPE_ITEM_DYNAMIC));
        fragments.add(FocusTableLetterFragment.newInstance(Constants.TYPE_ITEM_LETTER));
    }

    @Override
    public void onFocusRefreshData(RefreshLayout refreshlayout) {
        super.onFocusRefreshData(refreshlayout);
        fragments.get(mViewPager.getCurrentItem()).onRefreshData(refreshlayout);
    }

    @Override
    public void onFocusLoadData(RefreshLayout refreshlayout) {
        super.onFocusLoadData(refreshlayout);
        fragments.get(mViewPager.getCurrentItem()).onLoadData(refreshlayout);
    }
}
