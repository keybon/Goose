package com.emao.application.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.http.OkHttpManager;
import com.emao.application.ui.adapter.CommentAdapter;
import com.emao.application.ui.bean.OpinionBean;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.view.KeybonItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * @author keybon
 */

public class MineCommentActivity extends BaseActivity implements OnLoadmoreListener {


    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CommentAdapter mAdapter;

    private TextView comment_hint;

    private List<OpinionBean> opinionList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();

    private int count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_mine_comment);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();

        smartRefreshLayout = findViewById(R.id.comment_refreshlayout);
        recyclerView = findViewById(R.id.comment_recycler);
        comment_hint = findViewById(R.id.comment_hint);

    }

    @Override
    public void initData() {
        super.initData();

        setActionBarTitle("我的评论");

        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setOnLoadmoreListener(this);
        smartRefreshLayout.setEnableAutoLoadmore(false);
        smartRefreshLayout.setFooterTriggerRate(1.3f);

    }

    @Override
    protected void onResume() {
        super.onResume();
        downLoadOpinionData();
    }

    private void initLayoutManager() {

        if (layoutManager == null) {

            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new KeybonItemDecoration(opinionList));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        }
    }

    /**®
     * 文章评论查询
     */
    private void downLoadOpinionData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid",(String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_CONTENT_OPINION, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("result == " + result);
                opinionList = GsonUtils.getContentOpinionParams(result);
                for (int i = 0; i < opinionList.size(); i++) {
                    timeList.add(opinionList.get(i).getCtime());
                }
                Collections.sort(opinionList);
                initLayoutManager();
                initOpinionAdapter();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("文章评论接口查询失败 == " + request.toString());
            }
        });
    }

    private void initOpinionAdapter() {
        if(opinionList.size() == 0){
            comment_hint.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            comment_hint.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        if (mAdapter == null) {
            mAdapter = new CommentAdapter(this, opinionList);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setList(opinionList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        timeList.clear();
        downLoadOpinionData();
        refreshlayout.finishLoadmore(1000);
    }
}
