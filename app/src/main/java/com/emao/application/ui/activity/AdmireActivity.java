package com.emao.application.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.emao.application.ui.bean.RewardBean;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 *
 * @author keybon
 */

public class AdmireActivity extends BaseActivity implements OnLoadmoreListener{

    public static final int ADMIRE_SEND = 1;
    public static final int ADMIRE_REAP = 2;
    public static final String ADMIR_FLAG = "admire_flag";

    private TextView admire_direction;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private AdmireAdapter mAdapter;
    private List<RewardBean> mLists;

    private SmartRefreshLayout smartRefreshLayout;

    private int mType;
    private TextView admire_hint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_admire);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();

        actionBar.setElevation(0);
        admire_direction = findViewById(R.id.admire_direction);
        recyclerView = findViewById(R.id.admire_recyclerview);
        smartRefreshLayout = findViewById(R.id.admire_smart_refresh);
        admire_hint = findViewById(R.id.admire_hint);

        mLists = new ArrayList<>();

    }

    @Override
    public void initData() {
        super.initData();
        mType = getIntent().getIntExtra(ADMIR_FLAG,0);

        rewardTotal();
        initMoney("0");
        smartRefreshLayout.setOnLoadmoreListener(this);
        smartRefreshLayout.setEnableRefresh(false);

        initRecyclerView();
        downLoadRewardData(mType);
    }

    public void initMoney(String money){
        if(ADMIRE_SEND == mType){
            setActionBarTitle(CommonUtils.getResourceString(R.string.mine_send_admire));
            admire_direction.setText("共送出"+money+"元");
        } else if (ADMIRE_REAP == mType){
            setActionBarTitle(CommonUtils.getResourceString(R.string.mine_reap_admire));
            admire_direction.setText("共收到"+money+"元");
        }
    }


    private void initRecyclerView(){

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

    }

    /**
     * uid  获取 到 我打赏的总额
     * hid  收到 的打赏总额
     */
    public void rewardTotal(){

        HashMap<String,String> params = new HashMap<>();
        if(mType == ADMIRE_SEND){
            params.put("uid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        } else {
            params.put("hid",(String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        }
        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_REWARD_ALL, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("获取打赏总额 成功 == "+result);
                JSONObject jsonObject = new JSONObject(result);
                JSONObject data = new JSONObject(jsonObject.getString("data"));
                if(!data.isNull("number")){
                    initMoney(data.getString("number"));
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("获取打赏总额 失败 == "+request.toString());
            }
        });
    }


    /**
     * 赞赏  收到 hid  dou是自己的id
     */
    private void downLoadRewardData(int type){
        HashMap<String,String> params = new HashMap<>();
        if(type == ADMIRE_SEND){
            params.put("uid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        } else {
            params.put("hid",(String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        }
        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_CONTENT_REWARD, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("打赏记录查询 成功 == "+result);
                mLists = GsonUtils.getRewardParams(result);
                initRewardAdapter();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("打赏记录查询 失败 == "+request.toString());
            }
        });
    }

    private void initRewardAdapter(){
        if(mLists.size() == 0){
            if(mType == ADMIRE_SEND){
                admire_hint.setText("你还没有送出赏钱哦～");
            } else {
                admire_hint.setText("你还没有收到赏钱哦～");
            }
            admire_hint.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            admire_hint.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        if(mAdapter == null){
            mAdapter = new AdmireAdapter(this, mLists);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setList(mLists);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {

        refreshlayout.finishLoadmore(1000);

    }

    class AdmireAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private Context mContext;
        private List<RewardBean> list;

        public AdmireAdapter(Context mContext, List<RewardBean> list) {
            this.mContext = mContext;
            this.list = list;
        }

        public void setList(List<RewardBean> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == ADMIRE_SEND){
                View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_admire_send_item,parent,false);
                AdmireSendViewholder admireSendViewholder = new AdmireSendViewholder(view);
                return admireSendViewholder;
            } else {
                View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_admire_reap_item,parent,false);
                AdmireReapViewholder admireReapViewholder = new AdmireReapViewholder(view);
                return admireReapViewholder;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof AdmireSendViewholder){
                RewardBean rewardBean = mLists.get(position);
                ((AdmireSendViewholder) holder).send_nickname.setText(rewardBean.getUsername());
                ((AdmireSendViewholder) holder).send_time.setText(rewardBean.getCtime());
                ((AdmireSendViewholder) holder).send_count.setText(rewardBean.getReward());
            } else if(holder instanceof AdmireReapViewholder){
                RewardBean rewardBean = mLists.get(position);
                ((AdmireReapViewholder) holder).reap_nickname.setText(rewardBean.getUsername());
                ((AdmireReapViewholder) holder).reap_time.setText(rewardBean.getCtime());
                ((AdmireReapViewholder) holder).reap_count.setText(rewardBean.getReward());
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            if(mType == ADMIRE_SEND){
                return ADMIRE_SEND;
            } else {
                return ADMIRE_REAP;
            }
        }
    }

    class AdmireSendViewholder extends RecyclerView.ViewHolder{

        private TextView send_nickname,send_time,send_count;

        public AdmireSendViewholder(View itemView) {
            super(itemView);
            send_nickname = itemView.findViewById(R.id.admire_send_nickname);
            send_time = itemView.findViewById(R.id.admire_send_time);
            send_count = itemView.findViewById(R.id.admire_send_count);
        }
    }
    class AdmireReapViewholder extends RecyclerView.ViewHolder{

        private TextView reap_nickname,reap_time,reap_count;

        public AdmireReapViewholder(View itemView) {
            super(itemView);
            reap_nickname = itemView.findViewById(R.id.admire_reap_nickname);
            reap_time = itemView.findViewById(R.id.admire_reap_time);
            reap_count = itemView.findViewById(R.id.admire_reap_count);
        }
    }
}
