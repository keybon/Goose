package com.emao.application.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.AuthTask;
import com.emao.application.R;
import com.emao.application.http.HttpCallBack;
import com.emao.application.http.HttpService;
import com.emao.application.http.OkHttpManager;
import com.emao.application.share.AuthResult;
import com.emao.application.share.PayResult;
import com.emao.application.share.SignUtils;
import com.emao.application.share.WeChatPay;
import com.emao.application.ui.adapter.UserInfoAdapter;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.bean.RecommendFriendBean;
import com.emao.application.ui.bean.ReportBean;
import com.emao.application.ui.bean.User;
import com.emao.application.ui.callback.OnPayCallBackListener;
import com.emao.application.ui.callback.OnRecyclerViewClickListener;
import com.emao.application.ui.callback.OnRecyclerViewItemClickListener;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.emao.application.ui.view.ShowPopWinFactor;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONObject;

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

public class UserInfoActivity extends BaseLoginActivity implements View.OnClickListener,OnRecyclerViewClickListener,OnLoadmoreListener,OnRefreshListener {

    public static final String USERINFO_USERID = "userinfo_userid";
    public static final String USERINFO_USERNAME = "userinfo_username";

    private SmartRefreshLayout refreshLayout;
    private SimpleDraweeView portrait;
    private ImageView userinfo_activity_bg;
    private TextView nickname,aettionNum,FansNum,signature;
    private TextView commentNum,opinionNum;
    private RecyclerView recyclerView;
    private LinearLayout left_back;
    private LinearLayout aettionBottom,opinionBottom,admireBottom;


    private LinearLayout bottom_btn, bottom_send;
    private EditText chat_edit;
    private TextView chat_send;
    private InputMethodManager mInputMethodManager;


    private LinearLayoutManager layoutManager;
    private UserInfoAdapter mAdapter;
    private ShowPopWinFactor popupWindow;
    private List<RecommendFriendBean> dataList ;


    private List<ReportBean> reportBeans;

    private String portraitPath,nicknameStr,fansStr,signatureStr;
    private int count = 5;

    private String userid;
    private String username;



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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_userinfo);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();

        refreshLayout = findViewById(R.id.userinfo_activity_refresh);
        recyclerView = findViewById(R.id.userinfo_activity_recyclerview);
        portrait = findViewById(R.id.userinfo_activity_portrait);
        nickname = findViewById(R.id.userinfo_activity_nickname);
        aettionNum = findViewById(R.id.userinfo_activity_aettion);
        FansNum = findViewById(R.id.userinfo_activity_fans);
        signature = findViewById(R.id.userinfo_activity_signature);
        left_back = findViewById(R.id.userinfo_left_back);
        userinfo_activity_bg = findViewById(R.id.userinfo_activity_bg);

        commentNum = findViewById(R.id.message_friend_comment);
        opinionNum = findViewById(R.id.message_friend_opinion);

        aettionBottom = findViewById(R.id.userinfo_bottom_comment_ll);
        opinionBottom = findViewById(R.id.userinfo_bottom_opinion_ll);
        admireBottom = findViewById(R.id.userinfo_bottom_admire_ll);

        bottom_btn = findViewById(R.id.userinfo_activity_bottom_btn);
        bottom_send = findViewById(R.id.userinfo_activity_bottom_send);
        chat_edit = findViewById(R.id.single_chat_bottom_edit);
        chat_send = findViewById(R.id.single_chat_bottom_send);

        left_back.setOnClickListener(this);
        aettionBottom.setOnClickListener(this);
        opinionBottom.setOnClickListener(this);
        admireBottom.setOnClickListener(this);
        chat_send.setOnClickListener(this);

//        userinfo_activity_bg.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.copylink_small));

        mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);


        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setEnableAutoLoadmore(false);
        refreshLayout.setFooterTriggerRate(1.3f);

    }

    @Override
    public void initData() {
        super.initData();

        dataList = new ArrayList<>();
        reportBeans = new ArrayList<>();

        userid = getIntent().getStringExtra(USERINFO_USERID);
        username = getIntent().getStringExtra(USERINFO_USERNAME);

        reportBeans.add(new ReportBean("广告",false));
        reportBeans.add(new ReportBean("重复、旧闻",false));
        reportBeans.add(new ReportBean("低俗色情",false));
        reportBeans.add(new ReportBean("违法犯罪",false));
        reportBeans.add(new ReportBean("标题夸张",false));
        reportBeans.add(new ReportBean("与事实不符",false));
        reportBeans.add(new ReportBean("内容质量差",false));
        reportBeans.add(new ReportBean("疑似抄袭",false));
        reportBeans.add(new ReportBean("其它问题，我要吐槽",false));

        initUser();
        initFocusAndFans();
        initRecyclerView();


//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();

        if(SharedPreUtils.get(SharedPreUtils.SP_USERID,"").equals(userid)){
            bottom_btn.setVisibility(View.GONE);
            bottom_send.setVisibility(View.GONE);
        } else {
            bottom_btn.setVisibility(View.VISIBLE);
            bottom_send.setVisibility(View.GONE);
//            layoutParams.bottomMargin = 60;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserinfo("5");
    }

    public void initFocusAndFans(){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("uid", userid);
        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_SELECT_FOCUS_FANS, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("个人详情关注粉丝查询成功 "+result);
                JSONObject jsonObject = new JSONObject(result);
                if(!jsonObject.isNull("error")){
                    if(Constants.ERROR_CODE_200.equals(jsonObject.getString("error"))){
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        aettionNum.setText(data.getString("gid"));
                        FansNum.setText(data.getString("bid"));
                    }
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("个人详情关注粉丝查询失败 "+request.toString());
            }
        });
    }

    /**
     * 详情 标题栏
     */
    public void initUser(){
            HashMap<String,String> params = new HashMap<>();
            params.put("id",userid);
            OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_SELECT_USER, params, new OkHttpManager.DataCallBack() {
                @Override
                public void requestSuccess(String result) throws Exception {
                    LogUtils.e("个人主页 信息查询成功 result == "+result);
                    List<User> userList = GsonUtils.getUserParams(result);
                    if(userList.size() > 0){
                        User user = userList.get(0);
                        fillUserData(user);
                    }
                }

                @Override
                public void requestFailure(Request request, IOException e) {
                    LogUtils.e("个人主页 信息查询失败 request == "+request.toString());
                }
            });
    }

    private void fillUserData(User user){
        if(user != null) {
            nickname.setText(user.getUsername());
            signature.setText(user.getSignature());
            portrait.setImageURI(user.getHeadimgurl());
        }
    }

    /**
     * 初始化列表
     */
    public void initRecyclerView(){
        layoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL,false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayout.VERTICAL));
    }

    /**
     * adapter 填充
     */
    public void initAdapter(){

        if(mAdapter == null){
            mAdapter = new UserInfoAdapter(this,dataList);
            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnRecyclerViewClickListener(this);
        } else {
            mAdapter.setList(dataList);
            mAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 拉取数据
     */
    public void initUserinfo(String count){
        HashMap<String,String> params = new HashMap<>();
        params.put("uid",userid);
        params.put("limit",count);
        params.put("by", "ctime desc");
        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_CONTENT_DETAILS, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("requestSuccess"+result);
                dataList = GsonUtils.getRecommendParams(result);
                initAdapter();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("requestFailure"+request.toString());
            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.userinfo_left_back:
                backKeyPress();
                break;
            case R.id.userinfo_bottom_comment_ll:
                addAttention();
                break;
            case R.id.userinfo_bottom_opinion_ll:
                // 私信

                setOpinionBtn();

                break;
            case R.id.single_chat_bottom_send:
                // 发送私信
                String opinionContent = chat_edit.getText().toString().trim();
                if (!TextUtils.isEmpty(opinionContent)) {
                    sendOpinion(opinionContent);
                }
                if (mInputMethodManager.isActive()) {
                    mInputMethodManager.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0);
                }
                bottom_btn.setVisibility(View.VISIBLE);
                bottom_send.setVisibility(View.GONE);

                break;
            case R.id.userinfo_bottom_admire_ll:
                // 举报
                if(popupWindow == null){
                   popupWindow = new ShowPopWinFactor(this);
                }

                popupWindow.showReportPopWindow(reportBeans, new OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // 选中
                        ReportBean reportBean = reportBeans.get(position);
                        if(reportBean.getSelect()){
                            reportBean.setSelect(false);
                        } else {
                            reportBean.setSelect(true);
                        }


                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        // 关闭
                        dismissReportPop();
                    }

                    @Override
                    public void onPhotoClick(View view, int position) {

                        // 提交
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0 ; i < reportBeans.size() ; i ++ ){
                            stringBuilder.append(reportBeans.get(i).getItemname()+"&");
                        }
                        if(TextUtils.isEmpty(stringBuilder)){
                            dismissReportPop();
                            return;
                        }

                        HashMap<String,String> params = new HashMap<>();
                        params.put("uid",userid);
//                        params.put("jid",user.getId());
                        params.put("text", String.valueOf(stringBuilder));
                        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_REPORT, params, new OkHttpManager.DataCallBack() {
                            @Override
                            public void requestSuccess(String result) throws Exception {
                                LogUtils.e("举报"+result);
                                HashMap<String,String> params = GsonUtils.getMapParams(result);
                                if(Constants.ERROR_CODE_200.equals(params.get("error"))){
                                    ToastUtils.showShortToast("举报成功");
                                    dismissReportPop();
                                } else {
                                    ToastUtils.showShortToast("举报失败");
                                    dismissReportPop();
                                }
                            }

                            @Override
                            public void requestFailure(Request request, IOException e) {
                                LogUtils.e("举报失败");
                            }
                        });

                    }
                });


                popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                break;
            default:
                break;
        }
    }


    /**
     * 点击评论
     */

    private void setOpinionBtn() {
        //  回复内容本人
        chat_edit.setHint("私信" + username + "：");
        bottom_btn.setVisibility(View.GONE);
        bottom_send.setVisibility(View.VISIBLE);
        chat_edit.requestFocus();
        chat_edit.findFocus();
        mInputMethodManager.showSoftInput(chat_edit, InputMethodManager.SHOW_FORCED);

    }


    /**
     * 发表私信
     */
    private void sendOpinion(final String content) {

        HashMap<String, String> params = new HashMap<>();
        params.put("fid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID, ""));
        params.put("sid", userid);
        params.put("ftext", content);
//        params.put("time", String.valueOf(System.currentTimeMillis()/1000));

        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_LETTER_ADD, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("私信插入  result = "+result);
                HashMap<String, String> map = GsonUtils.getMapParams(result);
                if ("200".equals(map.get("error"))) {
                    ToastUtils.showShortToast("发表私信成功～");
                    chat_edit.setText("");
                } else {
                    ToastUtils.showShortToast("发表私信失败～");
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


    private void dismissReportPop(){
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    @Override
    public void backKeyPress() {
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
            return;
        }
        if (bottom_send.getVisibility() == View.VISIBLE) {
            bottom_send.setVisibility(View.GONE);
            bottom_btn.setVisibility(View.VISIBLE);
        } else {
            if (mInputMethodManager.isActive()) {
                mInputMethodManager.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0);
            }
            finish();
        }

        super.backKeyPress();
    }

    @Override
    public void onClickItem1(View view, int position) {
        // 点赞
        addAdmire(position);
    }

    @Override
    public void onClickItem2(View view, int position) {
        // 评论
//        ToastUtils.showShortToast("暂未开启个人详情！");
        Intent intent = new Intent();
        intent.setClass(this,ContentDetailsActivity.class);
        intent.putExtra(ContentDetailsActivity.CONTENT_RECOMMEND,dataList.get(position));
        startActivity(intent);
    }

    @Override
    public void onClickItem3(View view, final int position) {
        // 打赏
        if(popupWindow == null){
            popupWindow = new ShowPopWinFactor(this);
        }
        popupWindow.showAdmirePop(this,dataList.get(position).getHeadimgurl(), dataList.get(position).getUsername(), new OnPayCallBackListener() {
            @Override
            public void onClickPay(View v, String str) {
                dismissReportPop();
                showPayStylePop(position,str);

            }

            @Override
            public void onClickClose(View v, String str) {
                dismissReportPop();
            }
        });
        popupWindow.showAtLocation(this.getWindow().getDecorView(),Gravity.CENTER,0,0);
    }

    @Override
    public void onClickItem4(View view, int position) {

    }

    /**
     * 支付选择框
     */
    public void showPayStylePop(final int position, final String str){
        if(popupWindow == null){
            popupWindow = new ShowPopWinFactor(this);
        }
        popupWindow.showSelectorCollection(new OnPayCallBackListener() {
            @Override
            public void onClickPay(View v, String paystyle) {

                rewardService(position,str,paystyle);

                dismissReportPop();
            }

            @Override
            public void onClickClose(View v, String str) {
                dismissReportPop();
            }
        });
        popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    /**
     * 打赏传入后台
     */
    public void rewardService(int position, String str, final String status) {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID, ""));
        params.put("cid", dataList.get(position).getCid());
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
     * 点赞
     */
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
                    mAdapter.notifyDataSetChanged();
                } else if (Constants.ERROR_CODE_209.equals(map.get("error"))){
                    ToastUtils.showShortToast(map.get("msg"));
                }
            }

            @Override
            public void OnFailedCallBack(String result) {

            }
        });
    }
    /**
     * 关注
     */
    private void addAttention(){

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("gid", (String)SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        params.put("bid", userid);

        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_ATTENTION, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                HashMap<String,String> map = GsonUtils.getMapParams(result);
                if("200".equals(map.get("error"))){
                    commentNum.setSelected(true);
                    ToastUtils.showShortToast("关注成功！点亮～");
                } else if (Constants.ERROR_CODE_209.equals(map.get("error"))){
                    ToastUtils.showShortToast(map.get("msg"));
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                ToastUtils.showShortToast("关注失败");
            }
        });
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        count  += 5 ;
        dataList.clear();
        initUserinfo(String.valueOf(count));
        refreshLayout.finishLoadmore(1000);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        count = 5 ;
        initUserinfo("5");
        refreshLayout.finishRefresh(1000);
    }

    /**
     * 支付宝支付
     */

    public void aliPay(final String orderInfo) {
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(UserInfoActivity.this);
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

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content) {
        boolean rsa2 = (MainApp.RSA_PRIVATE.length() > 0);
        return SignUtils.sign(content, MainApp.RSA_PRIVATE, rsa2);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }




}
