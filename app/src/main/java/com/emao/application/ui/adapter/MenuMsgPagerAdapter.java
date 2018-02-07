package com.emao.application.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.emao.application.ui.bean.ClassifyBean;
import com.emao.application.ui.fragment.BaseFragment;

import java.util.List;

/**
 *
 * @author keybon
 */

public class MenuMsgPagerAdapter extends FragmentStatePagerAdapter {


    private List<BaseFragment> mFragments;
    private List<ClassifyBean> titles;

    public MenuMsgPagerAdapter(FragmentManager fragmentManager, List<BaseFragment> fragments,List<ClassifyBean> titles){
        super(fragmentManager);
        this.mFragments = fragments;
        this.titles = titles;
    }
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position).getName();
    }

}
