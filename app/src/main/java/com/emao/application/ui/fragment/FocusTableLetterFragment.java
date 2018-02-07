package com.emao.application.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.http.OkHttpManager;
import com.emao.application.ui.activity.SingleChatActivity;
import com.emao.application.ui.adapter.FocusTableLetterAdapter;
import com.emao.application.ui.bean.FocusTableLetterBean;
import com.emao.application.ui.callback.OnRecyclerViewItemClickListener;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 *
 * @author keybon
 */

public class FocusTableLetterFragment extends BaseFragment {


    private RecyclerView recyclerView;
    private FocusTableLetterAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<FocusTableLetterBean> list ;

    private TextView hint;

    public static FocusTableLetterFragment newInstance(int tag){
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE,tag);
        FocusTableLetterFragment focusTableLetterFragment = new FocusTableLetterFragment();
        focusTableLetterFragment.setArguments(args);
        return focusTableLetterFragment;
    }

    @Override
    protected void initRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.focus_table_letter_fragment,container,false);
        recyclerView = mRootView.findViewById(R.id.letter_table_recyclerview);
        hint = mRootView.findViewById(R.id.letter_table_no_message);
    }

    @Override
    protected void initEvents() {

        list = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(activity, LinearLayout.VERTICAL));

    }

    @Override
    public void onResume() {
        super.onResume();
        downLoadData();
    }

    private void downLoadData(){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("sid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_CONTENT_SELECT_LETTER, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e(result);
                list = GsonUtils.getLetterBeanParams(result);
                initData();
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    @Override
    protected void initData() {

        if(mAdapter == null){

            mAdapter = new FocusTableLetterAdapter(activity,list);
            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClick(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), SingleChatActivity.class);
                    intent.putExtra(SingleChatActivity.SINGLE_TID,list.get(position).getId());
                    intent.putExtra(SingleChatActivity.SINGLE_USERNAME,list.get(position).getUsername());
                    intent.putExtra(SingleChatActivity.SINGLE_HEADURL,list.get(position).getHeadimgurl());
                    intent.putExtra(SingleChatActivity.SINGLE_FID,list.get(position).getFid());
                    getActivity().startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }

                @Override
                public void onPhotoClick(View view, int position) {

                }
            });
        } else {
            mAdapter.setList(list);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onRefreshData(RefreshLayout refreshlayout) {
        super.onRefreshData(refreshlayout);
        list.clear();
        downLoadData();
        refreshlayout.finishRefresh();
    }

    @Override
    public void onLoadData(RefreshLayout refreshlayout) {
        super.onLoadData(refreshlayout);
        refreshlayout.finishLoadmore();
    }

}
