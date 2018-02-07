package com.emao.application.ui.activity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.AuthTask;
import com.emao.application.R;
import com.emao.application.http.HttpCallBack;
import com.emao.application.http.HttpService;
import com.emao.application.http.OkHttpManager;
import com.emao.application.share.AuthResult;
import com.emao.application.share.OrderInfoUtil2_0;
import com.emao.application.share.PayResult;
import com.emao.application.share.SignUtils;
import com.emao.application.share.WeChatPay;
import com.emao.application.share.WeChatShare;
import com.emao.application.ui.adapter.ContentDetailsAdmireAdapter;
import com.emao.application.ui.adapter.ContentDetailsOpinionAdapter;
import com.emao.application.ui.adapter.ContentDetailsRewardAdapter;
import com.emao.application.ui.adapter.ContentDetailsUserAdapter;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.bean.ContentDetailsBean;
import com.emao.application.ui.bean.OpinionBean;
import com.emao.application.ui.bean.RecommendFriendBean;
import com.emao.application.ui.bean.RewardBean;
import com.emao.application.ui.callback.OnItemClickListener;
import com.emao.application.ui.callback.OnPayCallBackListener;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.emao.application.ui.view.GooseGridView;
import com.emao.application.ui.view.ShowPopWinFactor;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * @author keybon
 */

public class ContentDetailsActivity extends BaseActivity implements View.OnClickListener, WbShareCallback, IUiListener {

    public static final String CONTENT_FROM = "content_from";
    public static final String CONTENT_RECOMMEND = "content_recommend";

    private final int OPINION_FLAG = 3;
    private final int ADDMIRE_FLAG = 4;

    private SimpleDraweeView portrait;
    private TextView nickname;
    private TextView time;
    private TextView signature;
    private ImageView template;
//    private RelativeLayout message_friend_title;

    private LinearLayout admireBtn, opinionBtn, rewardBtn;
    private TextView admireNum, opinionNum, rewardNum;
    //    private TextView content;
    private LinearLayoutManager admireLayout, opinionLayout, rewardLayout, userLayout;
    private ShowPopWinFactor popupWindow;
    private TextView common_bottom_cutline;
    private LinearLayout bottom_btn, bottom_send;
    private EditText chat_edit;
    private TextView chat_send;
    private RelativeLayout content_details_opinion_empty,content_details_reward_empty;

    private InputMethodManager mInputMethodManager;

    private WeChatShare mWeChatShare;
    //    private WeiboShare mWeiboShare;
    private WbShareHandler shareHandler;

    private Intent intent;

    private Tencent mTencent;
    private int shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
    private int mExtarFlag = 0x00;

    private List<ContentDetailsBean> dataList = new ArrayList<>();

    private RecommendFriendBean recommendBean;

    private LinearLayout content_details_title_ll;


    /**
     * 头像
     */
    private ArrayList<String> portraitList = new ArrayList<>();
    private ContentDetailsAdmireAdapter admireAdapter;
    private GooseGridView admireGridView;
    /**
     * 评论
     */
    private List<OpinionBean> opinionList = new ArrayList<>();
    private ContentDetailsOpinionAdapter opinionAdapter;
    /**
     * 打赏
     */
    private List<RewardBean> rewardList = new ArrayList<>();
    private ContentDetailsRewardAdapter rewardAdapter;
    /**
     * 内容 图片 or 视频
     */
    private ContentDetailsUserAdapter userAdapter;

    private RecyclerView opinionRecyc, rewardRecyc;
    private RecyclerView userRecyc;


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
                case OPINION_FLAG:
                    opinionNum.setText(String.valueOf(msg.arg1));
                    break;
                case ADDMIRE_FLAG:
                    admireNum.setText(String.valueOf(msg.arg1));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_content_details);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();

        setActionBarTitle(CommonUtils.getResourceString(R.string.content_details));
        setShareBtn(true);

//        portrait = findViewById(R.id.message_friend_portrait);
        portrait = findViewById(R.id.userinfo_activity_portrait);
        template = findViewById(R.id.content_details_template);
//        nickname = findViewById(R.id.message_friend_nickname);
        nickname = findViewById(R.id.userinfo_activity_nickname);
        signature = findViewById(R.id.userinfo_activity_signature);
//        time = findViewById(R.id.message_friend_time);
        admireGridView = findViewById(R.id.content_details_admire_gridview);
        opinionRecyc = findViewById(R.id.content_details_opinion_recycler);
        rewardRecyc = findViewById(R.id.content_details_reward_recycler);
        userRecyc = findViewById(R.id.content_details_recyclerview);
        admireBtn = findViewById(R.id.message_friend_comment_ll);
        opinionBtn = findViewById(R.id.message_friend_opinion_ll);
        rewardBtn = findViewById(R.id.message_friend_admire_ll);
        admireNum = findViewById(R.id.message_friend_comment);
        opinionNum = findViewById(R.id.message_friend_opinion);
        rewardNum = findViewById(R.id.message_friend_admire);
        common_bottom_cutline = findViewById(R.id.common_bottom_cutline);
        bottom_btn = findViewById(R.id.content_details_bottom_btn);
        bottom_send = findViewById(R.id.content_details_bottom_send);
        chat_edit = findViewById(R.id.single_chat_bottom_edit);
        chat_send = findViewById(R.id.single_chat_bottom_send);
        content_details_title_ll = findViewById(R.id.content_details_title_ll);
        content_details_opinion_empty = findViewById(R.id.content_details_opinion_empty);
        content_details_reward_empty = findViewById(R.id.content_details_reward_empty);
//        content = findViewById(R.id.content_details_content);
//        message_friend_title = findViewById(R.id.message_friend_title);
//
//        message_friend_title.setBackground(null);

        mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        common_bottom_cutline.setVisibility(View.GONE);
        admireBtn.setOnClickListener(this);
        opinionBtn.setOnClickListener(this);
        rewardBtn.setOnClickListener(this);
        chat_send.setOnClickListener(this);

        admireGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(ContentDetailsActivity.this, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.USERINFO_USERID, dataList.get(position).getUid());
                intent.putExtra(UserInfoActivity.USERINFO_USERNAME, dataList.get(position).getUsername());
                startActivity(intent);
            }
        });

        chat_edit.setHint("回复" + "" + "：");
        chat_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 500) {
                    ToastUtils.showShortToast(R.string.text_num_above);
                }
                if (s.length() > 0) {
                    chat_send.setBackground(getResources().getDrawable(R.drawable.shape_coner_blue));
                    chat_send.setTextColor(getResources().getColor(R.color.common_white));
                } else {
                    chat_send.setBackground(getResources().getDrawable(R.drawable.shape_coner_white));
                    chat_send.setTextColor(getResources().getColor(R.color.launcher_text));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void initData() {
        super.initData();

        intent = getIntent();

        if (MainActivity.contentBitmap != null) {
            template.setImageBitmap(MainActivity.contentBitmap);
        }

        recommendBean = (RecommendFriendBean) intent.getSerializableExtra(CONTENT_RECOMMEND);

        portrait.setImageURI(recommendBean.getHeadimgurl());
        nickname.setText(recommendBean.getUsername());
//        time.setText(recommendBean.getCtime());
        signature.setText(recommendBean.getTitle());
        admireNum.setText(recommendBean.getGive());
        opinionNum.setText(recommendBean.getComment());

        nickname.setTextColor(getResources().getColor(R.color.login_edit_text));
        signature.setTextColor(getResources().getColor(R.color.login_edit_text));

        mTencent = Tencent.createInstance(MainApp.QQ_APP_ID, this.getApplicationContext());
        mWeChatShare = new WeChatShare(this);
//        mWeiboShare = new WeiboShare(this);
        WbSdk.install(this, new AuthInfo(this, MainApp.WEIBO_APP_ID, MainApp.WEIBO_REDIRECT_URL, MainApp.WEIBO_SCOPE));
        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();

        initUserRecyclerView();
        initOpinionRecyclerView();
        initRewardRecyclerView();

        updataBgHeight();
    }

    @Override
    protected void onResume() {
        super.onResume();

        downLoadAdmireData();
        downLoadOpinionData();
        downLoadRewardData();
    }

    /**
     * 动态改变背景图
     */

    private void updataBgHeight() {
        content_details_title_ll.getHeight();
        ViewTreeObserver vto = content_details_title_ll.getViewTreeObserver();
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) template.getLayoutParams();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                content_details_title_ll.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int height = content_details_title_ll.getMeasuredHeight();
                layoutParams.height = height + 300;
                template.setLayoutParams(layoutParams);
            }
        });


    }

    /**
     * 文章点赞查询
     */
    private void downLoadAdmireData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("cid", recommendBean.getId());
        params.put("type", recommendBean.getType());
        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_CONTENT_ADMIRE, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("result 点赞的 == " + result);
                dataList = GsonUtils.getContentDetailsParams(result);
                portraitList.clear();
                for (int i = 0; i < dataList.size(); i++) {
                    portraitList.add(dataList.get(i).getHeadimgurl());
                }

                Message msg = new Message();
                msg.what = ADDMIRE_FLAG;
                msg.arg1 = dataList.size();
                handler.sendMessage(msg);

                initAdmireRecyclerView();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("点赞的失败 == " + request.toString());
            }
        });
    }

    /**
     * 文章评论查询
     */
    private void downLoadOpinionData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("cid", recommendBean.getId());
        params.put("type", recommendBean.getType());
        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_CONTENT_OPINION, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("result 评论的 == " + result);
                opinionList = GsonUtils.getContentOpinionParams(result);

                Message msg = new Message();
                msg.what = OPINION_FLAG;
                msg.arg1 = opinionList.size();
                handler.sendMessage(msg);

                initOpinionAdapter();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("文章评论接口查询失败 == " + request.toString());
            }
        });
    }

    /**
     * 文章打赏查询
     */
    private void downLoadRewardData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("cid", recommendBean.getId());
        params.put("type", recommendBean.getType());
        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_CONTENT_REWARD, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("result 打赏的 == " + result);
                rewardList = GsonUtils.getRewardParams(result);
                initRewardAdapter();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("文章打赏接口查询失败 == " + request.toString());
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        mWeiboShare.handleWeiboResponse(intent,this);
        shareHandler.doResultIntent(intent, this);
    }

    private void initAdmireRecyclerView() {

        if (admireAdapter == null) {
            admireAdapter = new ContentDetailsAdmireAdapter(this, portraitList);
            admireGridView.setAdapter(admireAdapter);
        } else {
            admireAdapter.setmList(portraitList);
            admireAdapter.notifyDataSetChanged();
        }

    }

    private void initUserRecyclerView() {

        userLayout = new LinearLayoutManager(this, LinearLayout.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        userRecyc.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                outRect.bottom = CommonUtils.dip2px(ContentDetailsActivity.this, 15f);
            }
        });
        userRecyc.setLayoutManager(userLayout);
        userRecyc.setNestedScrollingEnabled(false);
        if ("2".equals(recommendBean.getType())) {
            userAdapter = new ContentDetailsUserAdapter(this, 2);
            userAdapter.setVideoPath(recommendBean.getVideo());
        } else {
            userAdapter = new ContentDetailsUserAdapter(this, 1);
            if (recommendBean.getImage() != null) {
                userAdapter.setPictureList(recommendBean.getImage());
            }
            userAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    ZoomableActivity.goToPage(ContentDetailsActivity.this,recommendBean,position);
                }
            });
        }
        userRecyc.setAdapter(userAdapter);

    }

    private void initOpinionRecyclerView() {
        opinionLayout = new LinearLayoutManager(this, LinearLayout.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        opinionRecyc.setLayoutManager(opinionLayout);
        opinionRecyc.setNestedScrollingEnabled(false);
    }

    private void initOpinionAdapter() {

        if(opinionList.size() == 0){
            content_details_opinion_empty.setVisibility(View.VISIBLE);
            opinionRecyc.setVisibility(View.GONE);
        } else {
            content_details_opinion_empty.setVisibility(View.GONE);
            opinionRecyc.setVisibility(View.VISIBLE);
        }

        if (opinionAdapter == null) {
            opinionAdapter = new ContentDetailsOpinionAdapter(this, opinionList);
            opinionRecyc.setAdapter(opinionAdapter);
            opinionAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    setOpinionBtn();
                }
            });
        } else {
            opinionAdapter.setDataList(opinionList);
            opinionAdapter.notifyDataSetChanged();
        }

    }

    private void initRewardAdapter() {

        if(rewardList.size() == 0){
            content_details_reward_empty.setVisibility(View.VISIBLE);
            rewardRecyc.setVisibility(View.GONE);
        } else {
            content_details_reward_empty.setVisibility(View.GONE);
            rewardRecyc.setVisibility(View.VISIBLE);
        }

        if (rewardAdapter == null) {
            rewardAdapter = new ContentDetailsRewardAdapter(this, rewardList);
            rewardRecyc.setAdapter(rewardAdapter);
        } else {
            rewardAdapter.setDataList(rewardList);
            rewardAdapter.notifyDataSetChanged();
        }


    }

    private void initRewardRecyclerView() {

        rewardLayout = new LinearLayoutManager(this, LinearLayout.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rewardRecyc.setLayoutManager(rewardLayout);
        rewardRecyc.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setShareBtn(false);
    }

    @Override
    public void showSharePopWindow(View v) {
        super.showSharePopWindow(v);
        if (popupWindow == null) {
            popupWindow = new ShowPopWinFactor(this);
        }
        popupWindow.showShareMenu(this);
        popupWindow.showAtLocation(findViewById(R.id.content_details_activity), Gravity.BOTTOM, 0, 0);

    }

    public void showAdmirePopWindow(View view) {
        if (popupWindow == null) {
            popupWindow = new ShowPopWinFactor(this);
        }
        popupWindow.showAdmirePop(this, recommendBean.getHeadimgurl(), recommendBean.getUsername(), new OnPayCallBackListener() {
            @Override
            public void onClickPay(View v, String str) {

                HashMap<String, String> params = new HashMap<>();
                params.put("uid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID, ""));
                params.put("cid", recommendBean.getCid());
                params.put("hid", recommendBean.getUid());
                params.put("type", recommendBean.getType());
                params.put("reward", str);
                // 微信 1  支付宝 2
                final String status = SharedPreUtils.get(SharedPreUtils.SP_COLLECTION, "");

                if (TextUtils.equals(status, Constants.COLLECTION_ALI_TYPE)) {
                    params.put("status", "2");
                } else {
                    params.put("status", "1");
                }

                OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_REWARD_ADD, params, new OkHttpManager.DataCallBack() {
                    @Override
                    public void requestSuccess(String result) throws Exception {
                        LogUtils.e("文章打赏成功 result = " + result);
                        HashMap<String, String> map = GsonUtils.getMapParams(result);

                        if (status.equals(Constants.COLLECTION_ALI_TYPE)) {
                            if(!TextUtils.isEmpty(map.get("key"))){
                                aliPay(map.get("key"));
                            } else {
                                ToastUtils.showShortToast("打赏出了点问题 " + map.get("error"));
                            }
                        } else {
                            HashMap<String,String> resultMap = GsonUtils.getMapParams(map.get("data"));
                            if (!TextUtils.isEmpty(map.get("data"))) {
                                ToastUtils.showShortToast("文章打赏成功");
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

            @Override
            public void onClickClose(View v, String str) {
                disPopWindow();
            }
        });
        popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    public void disPopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_friend_comment_ll:
                addAdmire();
                break;
            case R.id.message_friend_opinion_ll:
                // 点击评论

                setOpinionBtn();

                break;
            case R.id.single_chat_bottom_send:
                // 发送评论
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
            case R.id.message_friend_admire_ll:

                // 打赏
                if(popupWindow == null){
                    popupWindow = new ShowPopWinFactor(this);
                }
                popupWindow.showAdmirePop(this,recommendBean.getHeadimgurl(), recommendBean.getUsername(), new OnPayCallBackListener() {
                    @Override
                    public void onClickPay(View v, String str) {
                        disPopWindow();
                        showPayStylePop(str);

                    }

                    @Override
                    public void onClickClose(View v, String str) {
                        disPopWindow();
                    }
                });
                popupWindow.showAtLocation(this.getWindow().getDecorView(),Gravity.CENTER,0,0);

                break;
            case R.id.pop_share_qq:

                shareToQQ();

                break;
            case R.id.pop_share_zone:

                shareToQzone();

                break;
            case R.id.pop_share_wechat:

                if (mWeChatShare.isInstalled()) {
                    mWeChatShare.setTimeline(false);
                    mWeChatShare.shareUrl(String.format(Constants.GOOSE_SHARE_URL, recommendBean.getId()));
                }
                break;
            case R.id.pop_share_wechat_friends:

                if (mWeChatShare.isInstalled()) {
                    mWeChatShare.setTimeline(true);
                    mWeChatShare.shareUrl(String.format(Constants.GOOSE_SHARE_URL, recommendBean.getId()));
                }
                break;
            case R.id.pop_share_weibo:

                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                weiboMessage.textObject = getTextObj();
                shareHandler.shareMessage(weiboMessage, false);

                break;
            case R.id.pop_share_copy:

                CopyUrlToClipboard(String.format(Constants.GOOSE_SHARE_URL, recommendBean.getId()));

                break;
            case R.id.pop_share_cancel:

                disPopWindow();

                break;
            default:
                break;
        }
    }

    /**
     * 支付选择框
     */
    public void showPayStylePop(final String str){
        if(popupWindow == null){
            popupWindow = new ShowPopWinFactor(this);
        }
        popupWindow.showSelectorCollection(new OnPayCallBackListener() {
            @Override
            public void onClickPay(View v, String paystyle) {

                rewardService(str,paystyle);

                disPopWindow();
            }

            @Override
            public void onClickClose(View v, String str) {
                disPopWindow();
            }
        });
        popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    /**
     * 打赏传入后台
     */
    public void rewardService(String str, final String status) {

        HashMap<String, String> params = new HashMap<>();
        params.put("uid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID, ""));
        params.put("cid", recommendBean.getCid());
        params.put("hid", recommendBean.getUid());
        params.put("type", recommendBean.getType());
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
     * 点击评论
     */

    private void setOpinionBtn() {
        //  回复内容本人
        chat_edit.setHint("回复" + recommendBean.getUsername() + "：");
        bottom_btn.setVisibility(View.GONE);
        bottom_send.setVisibility(View.VISIBLE);
        chat_edit.requestFocus();
        chat_edit.findFocus();
        mInputMethodManager.showSoftInput(chat_edit, InputMethodManager.SHOW_FORCED);

    }

    /**
     * 点赞
     */
    private void addAdmire() {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID, ""));
        params.put("cid", recommendBean.getId());
        params.put("hid", recommendBean.getUid());
        params.put("type", recommendBean.getType());

        HttpService.addAdmire(params, new HttpCallBack() {
            @Override
            public void OnSuccessCallBack(String result) {

                HashMap<String, String> map = GsonUtils.getMapParams(result);
                if (Constants.ERROR_CODE_200.equals(map.get("error"))) {
                    ToastUtils.showShortToast("点赞成功！点亮～");
                    downLoadAdmireData();
                } else if (Constants.ERROR_CODE_209.equals(map.get("error"))) {
                    ToastUtils.showShortToast(map.get("msg"));
                }
            }

            @Override
            public void OnFailedCallBack(String result) {

            }
        });
    }

    /**
     * 发表评论
     */
    private void sendOpinion(final String content) {

        HashMap<String, String> params = new HashMap<>();
        params.put("uid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID, ""));
//        if(opinionPosition == -1){
        params.put("cid", recommendBean.getId());
        params.put("type", recommendBean.getType());
//        } else {
//            params.put("cid",opinionList.get(opinionPosition).getId());
//            params.put("hid",recommendBean.getType());
//        }


        params.put("comment", content);

        HttpService.sendOpinion(params, new HttpCallBack() {
            @Override
            public void OnSuccessCallBack(String result) {

                HashMap<String, String> map = GsonUtils.getMapParams(result);
                if ("200".equals(map.get("error"))) {
                    ToastUtils.showShortToast("发表评论成功～");
                    chat_edit.setText("");
                    downLoadOpinionData();
                } else {
                    ToastUtils.showShortToast("发表评论失败～");
                }
            }

            @Override
            public void OnFailedCallBack(String result) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Tencent.onActivityResultData(requestCode, resultCode, data, this);

    }

    /**
     * 支付宝支付
     */

    /**
     * 支付宝支付
     */

    public void aliPay(final String orderInfo) {
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(ContentDetailsActivity.this);
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


    /**
     * 支付宝账户授权业务
     */
    public void aliAuth() {
        boolean rsa2 = (MainApp.RSA_PRIVATE.length() > 0);
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(MainApp.PID, MainApp.ALIPAY_APP_ID, MainApp.TARGET_ID, rsa2);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

        String privateKey = rsa2 ? MainApp.RSA_PRIVATE : MainApp.RSA2_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
        final String authInfo = info + "&" + sign;
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(ContentDetailsActivity.this);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(authInfo, true);

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
    public void backKeyPress() {
        if (bottom_send.getVisibility() == View.VISIBLE) {
            bottom_send.setVisibility(View.GONE);
            bottom_btn.setVisibility(View.VISIBLE);
        } else {
            if (mInputMethodManager.isActive()) {
                mInputMethodManager.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0);
            }
            finish();
        }

    }

    /**
     * QQ分享
     */
    public void shareToQQ() {
        Bundle bundle = new Bundle();
        //这条分享消息被好友点击后的跳转URL。
        bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, String.format(Constants.GOOSE_SHARE_URL, recommendBean.getId()));
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_	SUMMARY不能全为空，最少必须有一个是有值的。
        bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, "这是一个鹅毛的有趣故事～");
        //分享的图片URL
        bundle.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL,
                "http://img3.cache.netease.com/photo/0005/2013-03-07/8PBKS8G400BV0005.jpg");
        //分享的消息摘要，最长50个字
        bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "我是鹅毛");
        //手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
        bundle.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, "鹅毛" + MainApp.QQ_APP_ID);
        //标识该消息的来源应用，值为应用名称+AppId。
//        bundle.putString(QzoneShare.PARAM_APP_SOURCE, "鹅毛" + MainApp.QQ_APP_ID);

        mTencent.shareToQQ(this, bundle, this);
    }

    /**
     * QQ空间分享
     */
    private void shareToQzone() {
        Bundle params = new Bundle();
        //这条分享消息被好友点击后的跳转URL。
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, String.format(Constants.GOOSE_SHARE_URL, recommendBean.getId()));
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_	SUMMARY不能全为空，最少必须有一个是有值的。
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "这是一个鹅毛的有趣故事～");
        //分享的图片URL
        params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL,
                Constants.uri2);
        //分享的消息摘要，最长50个字
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "我是鹅毛");
        mTencent.shareToQzone(this, params, this);
    }


    /**
     * 微博 分享 callback
     */

    @Override
    public void onWbShareSuccess() {
        ToastUtils.showShortToast("分享成功");
    }

    @Override
    public void onWbShareCancel() {
        ToastUtils.showShortToast("分享取消");
    }

    @Override
    public void onWbShareFail() {
        ToastUtils.showShortToast("分享失败");
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = getSharedText();
        textObject.title = "这是一个鹅毛的有趣故事～";
        textObject.actionUrl = getSharedText();
        return textObject;
    }

    /**
     * 获取分享的文本模板。
     */
    private String getSharedText() {
        return String.format(Constants.GOOSE_SHARE_URL, recommendBean.getId());
    }

    /**
     * 复制链接
     */
    public void CopyUrlToClipboard(String copyUrl) {
        ClipboardManager clip = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        if (!TextUtils.isEmpty(copyUrl)) {
            try {
                String url = URLDecoder.decode(copyUrl, "UTF-8");
                String jsonString = url.substring(url.indexOf('?') + 1);
                Map<String, String> map = new Gson().fromJson(jsonString, new TypeToken<HashMap<String, String>>() {
                }.getType());
                String link = map.get("produrl");
                // 复制
                clip.setText(link);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ToastUtils.showLongToast("复制成功");
        disPopWindow();
    }

    /**
     * QQ share callback
     */

    @Override
    public void onComplete(Object o) {

        ToastUtils.showShortToast(o.toString());

    }

    @Override
    public void onError(UiError uiError) {
        ToastUtils.showShortToast("errorDetail = " + uiError.errorDetail + "  errorMessage = " + uiError.errorMessage);
    }

    @Override
    public void onCancel() {
        ToastUtils.showShortToast("分享取消");
    }
}
