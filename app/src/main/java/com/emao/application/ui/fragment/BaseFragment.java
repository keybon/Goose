package com.emao.application.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emao.application.ui.activity.BaseActivity;
import com.scwang.smartrefresh.layout.api.RefreshLayout;


/**
 *
 * @author keybon
 */

public abstract class BaseFragment extends Fragment {


    public static final String ARG_TYPE = "ARG_TYPE";

    public static final int ACTION_SEARCH = 0x00;
    public static final int ACTION_QRCODE = 0x10;
    public static final int ACTION_EPAY = 0x11;
    public static final int ACTION_MESSAGE = 0x12;
    public static final int ACTION_FRIEND = 0x13;
    public static final int ACTION_PUBLIC_ACCOUNT = 0x14;
    public static final int ACTION_GROUP_CHAT = 0x15;

    public BaseActivity activity;
    protected View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initRootView(inflater, container, savedInstanceState);
        activity = (BaseActivity) getActivity();
        initEvents();
        initData();
        return mRootView;
    }

    /**
     * 初始化根布局
     *
     * @return View 视图
     */
    protected abstract void initRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 初始化监听事件等
     */
    protected abstract void initEvents();

    /**
     * 加载数据
     */
    protected abstract void initData();

    public void onMessageRefreshData(RefreshLayout refreshlayout){};
    public void onFocusRefreshData(RefreshLayout refreshlayout){};
    public void onRefreshData(RefreshLayout refreshlayout){};

    public void onMessageLoadData(RefreshLayout refreshlayout){};
    public void onFocusLoadData(RefreshLayout refreshlayout){};
    public void onLoadData(RefreshLayout refreshlayout){};

}
