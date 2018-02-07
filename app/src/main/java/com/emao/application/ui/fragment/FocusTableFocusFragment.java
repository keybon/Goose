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

import com.emao.application.R;
import com.emao.application.http.OkHttpManager;
import com.emao.application.ui.activity.UserInfoActivity;
import com.emao.application.ui.adapter.FocusTableFocusAdapter;
import com.emao.application.ui.bean.FocusTableAttentionBean;
import com.emao.application.ui.callback.OnRecyclerViewClickListener;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;
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

public class FocusTableFocusFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private FocusTableFocusAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<FocusTableAttentionBean> list ;


    public static FocusTableFocusFragment newInstance(int tag){
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE,tag);
        FocusTableFocusFragment focusTableFocusFragment = new FocusTableFocusFragment();
        focusTableFocusFragment.setArguments(args);
        return focusTableFocusFragment;
    }


    @Override
    protected void initRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.focus_table_focus_fragment,container,false);
        recyclerView = mRootView.findViewById(R.id.focus_table_recyclerview);

    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initData() {

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

    /**
     * 关注查询
     */
    private void downLoadData(){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("gid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_CONTENT_ATTENTION_SELECT, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("关注查询 "+result);
                list = GsonUtils.getAttentionParams(result);
                initAdapter();
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    /**
     * 查询是否已关注
     */
    private void selectAttention(final int position, String gid){
        HashMap<String,String> params = new HashMap<>();
        params.put("gid", gid);
        params.put("bid", "1");
        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_CONTENT_ATTENTION_SELECT, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("查询是否已关注 "+result);
                List<FocusTableAttentionBean> mList = GsonUtils.getAttentionParams(result);
                for(int i = 0 ; i < list.size() ; i ++ ){
                    if(list.get(position).getGid().equals(mList.get(i).getGid())){
                        //关注的ID 跟 自己的id 相同
                        list.get(position).setAttention(true);
                    } else {
                        list.get(position).setAttention(false);
                    }
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


    private void initAdapter(){

        if(mAdapter == null){

            mAdapter = new FocusTableFocusAdapter(activity,list);
            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnRecyclerViewClickListener(new OnRecyclerViewClickListener() {
                @Override
                public void onClickItem1(View view, int position) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.USERINFO_USERID,list.get(position).getBid());
                    intent.putExtra(UserInfoActivity.USERINFO_USERNAME,list.get(position).getUsername());
                    startActivity(intent);
                }

                @Override
                public void onClickItem2(View view, int position) {
                    ToastUtils.showShortToast("我是昵称");
                }

                @Override
                public void onClickItem3(View view, int position) {
                    // 取消关注
                    deleteAttention(position);

                }

                @Override
                public void onClickItem4(View view, int position) {
                    // 赞不实现
                }
            });
        } else {

            mAdapter.setList(list);
            mAdapter.notifyDataSetChanged();

        }
    }

    /**
     * 取消关注
     */
    private void deleteAttention(final int position){

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("gid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        params.put("bid", list.get(position).getBid());
        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_ATTENTION_DELETE, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("取消关注接口 成功");
                HashMap<String,String> map = GsonUtils.getMapParams(result);
                if("200".equals(map.get("error"))){
                    downLoadData();
                    ToastUtils.showShortToast("取消成功～");
                } else {
                    ToastUtils.showShortToast("取消失败,请不要多次点击");
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("取消关注接口 成功");
                ToastUtils.showShortToast("取消失败");
            }
        });
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
