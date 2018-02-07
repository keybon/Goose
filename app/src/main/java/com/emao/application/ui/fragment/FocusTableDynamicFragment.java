package com.emao.application.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alipay.sdk.app.AuthTask;
import com.emao.application.R;
import com.emao.application.http.HttpCallBack;
import com.emao.application.http.HttpService;
import com.emao.application.http.OkHttpManager;
import com.emao.application.share.AuthResult;
import com.emao.application.share.PayResult;
import com.emao.application.share.WeChatPay;
import com.emao.application.ui.activity.ContentDetailsActivity;
import com.emao.application.ui.activity.UserInfoActivity;
import com.emao.application.ui.adapter.RecommendAdapter;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.bean.RecommendFriendBean;
import com.emao.application.ui.callback.OnPayCallBackListener;
import com.emao.application.ui.callback.OnRecyclerViewClickListener;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.emao.application.ui.view.DialogFactory;
import com.emao.application.ui.view.ShowPopWinFactor;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 *
 * @author keybon
 */

public class FocusTableDynamicFragment extends BaseFragment implements OnRecyclerViewClickListener {


    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecommendAdapter mAdapter;
    private List<RecommendFriendBean> dataList ;
    private ShowPopWinFactor popupWindow;

    private int limitCount = 5;

    public static FocusTableDynamicFragment newInstance(int tag){
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE,tag);
        FocusTableDynamicFragment focusTableDynamicFragment = new FocusTableDynamicFragment();
        focusTableDynamicFragment.setArguments(args);
        return focusTableDynamicFragment;
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MainApp.SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     // 同步返回需要验证的信息
                     */
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtils.showShortToast("支付成功");
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtils.showShortToast("支付失败");
                    }
                    break;
                case MainApp.SDK_AUTH_FLAG:
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatusAuth = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatusAuth, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        ToastUtils.showShortToast("\"授权成功\\n\" + String.format(\"authCode:%s\", authResult.getAuthCode())");
                    } else {
                        // 其他状态值则为授权失败
                        ToastUtils.showShortToast("授权失败" + String.format("authCode:%s", authResult.getAuthCode()));
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void initRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.focus_table_dynamic_fragment, container, false);

        recyclerView = mRootView.findViewById(R.id.dynamic_recyclerview);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initData() {

        dataList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL,false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayout.VERTICAL));

    }

    @Override
    public void onResume() {
        super.onResume();
        downLoadData("5");
    }

    private void downLoadData(String count){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        params.put("limit", count);
        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_CONTENT_FOCUS_DYNAMIC, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("动态文章查询"+result);
                if(TextUtils.isEmpty(result)){
                    return;
                }
                dataList = GsonUtils.getRecommendParams(result);
                initAdapter();

            }

            @Override
            public void requestFailure(Request request, IOException e) {
                DialogFactory.hideLoading(getActivity());
                LogUtils.e("动态文章查询失败"+request.toString());
            }
        });
    }

    private void initAdapter(){

        if(mAdapter != null){
            mAdapter.setList(dataList);
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter = new RecommendAdapter(getActivity(),dataList);
            mAdapter.setOnRecyclerViewClickListener(this);
            recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onClickItem1(View view, int position) {
        // 点赞
        addAdmire(position);
    }

    @Override
    public void onClickItem2(View view, int position) {
        // 评论
        Intent intent = new Intent();
        intent.setClass(getActivity(),ContentDetailsActivity.class);
        intent.putExtra(ContentDetailsActivity.CONTENT_RECOMMEND,dataList.get(position));
        startActivity(intent);
    }

    @Override
    public void onClickItem3(View view, final int position) {
        // 打赏
        if(popupWindow == null){
            popupWindow = new ShowPopWinFactor(getActivity());
        }
        popupWindow.showAdmirePop(getActivity(),dataList.get(position).getHeadimgurl(), dataList.get(position).getUsername(), new OnPayCallBackListener() {
            @Override
            public void onClickPay(View v, String str) {
                dismissPopWindow();
                showPayStylePop(position,str);

            }

            @Override
            public void onClickClose(View v, String str) {
                dismissPopWindow();
            }
        });
        popupWindow.showAtLocation(getActivity().getWindow().getDecorView(),Gravity.CENTER,0,0);
    }

    public void dismissPopWindow(){
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    /**
     * 支付选择框
     */
    public void showPayStylePop(final int position, final String str){
        if(popupWindow == null){
            popupWindow = new ShowPopWinFactor(getActivity());
        }
        popupWindow.showSelectorCollection(new OnPayCallBackListener() {
            @Override
            public void onClickPay(View v, String paystyle) {

                rewardService(position,str,paystyle);

                dismissPopWindow();
            }

            @Override
            public void onClickClose(View v, String str) {
                dismissPopWindow();
            }
        });
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    /**
     * 打赏传入后台
     */
    public void rewardService(int position, String str, final String status) {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID, ""));
        params.put("cid", dataList.get(position).getId());
        params.put("hid", dataList.get(position).getUid());
        params.put("type", dataList.get(position).getType());
        params.put("reward", str);
        // 微信 1  支付宝 2
        if (TextUtils.equals(status, Constants.COLLECTION_ALI_TYPE)) {
            params.put("status", status);
        } else {
            params.put("status", status);
        }

        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_REWARD_ADD, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("文章打赏成功 result = " + result);
                HashMap<String, String> map = GsonUtils.getMapParams(result);
                if (status.equals("2")) {
                    if (!TextUtils.isEmpty(map.get("key"))) {
                        aliPay(map.get("key"));
                    } else {
                        ToastUtils.showShortToast("打赏出了点问题 ");
                    }
                } else {
                    HashMap<String, String> resultMap = GsonUtils.getMapParams(map.get("data"));
                    if (!TextUtils.isEmpty(map.get("data"))) {
                        WeChatPay weChatPay = new WeChatPay(MainApp.IMApp);
                        weChatPay.toPay(resultMap);
                    } else {
                        ToastUtils.showShortToast("打赏出了点问题 " + map.get("error"));
                    }
                }


            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("文章打赏失败 request = " + request.toString());
                ToastUtils.showShortToast("文章打赏失败");
            }
        });
    }

    /**
     * 支付宝支付
     */

    public void aliPay(final String orderInfo) {
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(getActivity());
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(orderInfo, true);

                Message msg = new Message();
                msg.what = MainApp.SDK_AUTH_FLAG;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }


    @Override
    public void onClickItem4(View view, int position) {
        // 头像
        Intent intent = new Intent();
        intent.setClass(getActivity(),UserInfoActivity.class);
        intent.putExtra(UserInfoActivity.USERINFO_USERID,dataList.get(position).getUid());
        intent.putExtra(UserInfoActivity.USERINFO_USERNAME,dataList.get(position).getUsername());
        startActivity(intent);
    }

    private void addAdmire(final int position){
        RecommendFriendBean bean = dataList.get(position);
        HashMap<String,String> params = new HashMap<>();
        params.put("uid",(String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        params.put("cid",bean.getId());
        params.put("hid",bean.getUid());
        params.put("type",bean.getType());

        HttpService.addAdmire(params, new HttpCallBack() {
            @Override
            public void OnSuccessCallBack(String result) {

                HashMap<String,String> map = GsonUtils.getMapParams(result);
                if(Constants.ERROR_CODE_200.equals(map.get("error"))){
                    ToastUtils.showShortToast("点赞成功！点亮～");
                    dataList.get(position).setGive(String.valueOf(Integer.parseInt(dataList.get(position).getGive())+1));
                    mAdapter.setList(dataList);
                    mAdapter.notifyItemChanged(position);
                } else if (Constants.ERROR_CODE_209.equals(map.get("error"))){
                    ToastUtils.showShortToast(map.get("msg"));
                }
            }

            @Override
            public void OnFailedCallBack(String result) {

            }
        });
    }

    @Override
    public void onLoadData(RefreshLayout refreshlayout) {
        super.onLoadData(refreshlayout);
        limitCount += 5;
        downLoadData(String.valueOf(limitCount));
        refreshlayout.finishLoadmore();
    }

    @Override
    public void onRefreshData(RefreshLayout refreshlayout) {
        super.onRefreshData(refreshlayout);
        dataList.clear();
        downLoadData("5");
        refreshlayout.finishRefresh();
    }
}
