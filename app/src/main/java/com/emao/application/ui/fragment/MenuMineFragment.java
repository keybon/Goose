package com.emao.application.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.AuthTask;
import com.emao.application.R;
import com.emao.application.share.AuthResult;
import com.emao.application.share.OrderInfoUtil2_0;
import com.emao.application.share.PayResult;
import com.emao.application.ui.activity.AdmireActivity;
import com.emao.application.ui.activity.LogOutActivity;
import com.emao.application.ui.activity.MineCommentActivity;
import com.emao.application.ui.activity.PayStyleActivity;
import com.emao.application.ui.activity.UserInfoSetting;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.callback.OnPayCallBackListener;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.emao.application.ui.utils.fresco.FrescoImageUtils;
import com.emao.application.ui.view.ShowPopWinFactor;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Map;

/**
 * @author keybon
 */

public class MenuMineFragment extends BaseFragment implements View.OnClickListener {


    private RelativeLayout mine_setting;
    private RelativeLayout mine_comment;
    private RelativeLayout mine_send_admire;
    private RelativeLayout mine_reap_admire;
    private RelativeLayout mine_collection;
    private LinearLayout menu_mine_userinfo;
    private SimpleDraweeView mine_portrait;
    private TextView nickName,singature;
    private ShowPopWinFactor popupWindow;

    private String portraitPath;

    @Override
    protected void initRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.menu_mine_fragment, container, false);
    }

    @Override
    protected void initEvents() {
        mine_setting = mRootView.findViewById(R.id.mine_setting);
        mine_comment = mRootView.findViewById(R.id.mine_comment);
        mine_send_admire = mRootView.findViewById(R.id.mine_send_admire);
        mine_reap_admire = mRootView.findViewById(R.id.mine_reap_admire);
        menu_mine_userinfo = mRootView.findViewById(R.id.menu_mine_userinfo);
        mine_portrait = mRootView.findViewById(R.id.mine_portrait);
        mine_collection = mRootView.findViewById(R.id.mine_collection);
        nickName = mRootView.findViewById(R.id.mine_user_name);
        singature = mRootView.findViewById(R.id.mine_sign_text);

        mine_setting.setOnClickListener(this);
        mine_comment.setOnClickListener(this);
        mine_send_admire.setOnClickListener(this);
        mine_reap_admire.setOnClickListener(this);
        menu_mine_userinfo.setOnClickListener(this);
        mine_collection.setOnClickListener(this);
    }

    @Override
    protected void  initData() {

        portraitPath = (String) SharedPreUtils.get(SharedPreUtils.SP_HEADIMGURL, "");
        if(portraitPath.contains(CommonUtils.getSDCardPath())){
            FrescoImageUtils.getInstance()
                    .createSdcardBuilder(portraitPath)
                    .setClearCahe(true)
                    .build()
                    .showSdcardImage(mine_portrait);
        } else {
            mine_portrait.setImageURI(portraitPath);
        }
        nickName.setText((String) SharedPreUtils.get(SharedPreUtils.SP_NICKNAME,""));
        String sign = SharedPreUtils.get(SharedPreUtils.SP_SIGNATURE,"");
        if(!TextUtils.isEmpty(sign)){
            singature.setText(sign);
        } else {
            singature.setText("暂未设置");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_setting:
                activity.startActivity(new Intent(activity,LogOutActivity.class));
                break;
            case R.id.mine_comment:
                activity.startActivity(new Intent(activity,MineCommentActivity.class));
                break;
            case R.id.mine_send_admire:
                Intent sendIntent = new Intent(activity, AdmireActivity.class);
                sendIntent.putExtra(AdmireActivity.ADMIR_FLAG,AdmireActivity.ADMIRE_SEND);
                activity.startActivity(sendIntent);
                break;
            case R.id.mine_reap_admire:
                Intent reapIntent = new Intent(activity, AdmireActivity.class);
                reapIntent.putExtra(AdmireActivity.ADMIR_FLAG,AdmireActivity.ADMIRE_REAP);
                activity.startActivity(reapIntent);
                break;
            case R.id.menu_mine_userinfo:
                activity.startActivity(new Intent(activity,UserInfoSetting.class));
                break;
            case R.id.mine_collection:
//                showCollectionPopWindow();
                activity.startActivity(new Intent(activity,PayStyleActivity.class));
                break;
            default:
                break;
        }
    }

    public void showCollectionPopWindow(){
        if( popupWindow == null ){
            popupWindow = new ShowPopWinFactor(activity);
        }
        popupWindow.showSelectorCollection(new OnPayCallBackListener() {
            @Override
            public void onClickPay(View v, String str) {
                if(TextUtils.equals("2",str)){
//                    SharedPreUtils.put(SharedPreUtils.SP_COLLECTION, Constants.COLLECTION_ALI_TYPE);
                } else {
//                    SharedPreUtils.put(SharedPreUtils.SP_COLLECTION,Constants.COLLECTION_WECHAT_TYPE);

                }
                dimissCollectionPop();
            }

            @Override
            public void onClickClose(View v, String str) {
                dimissCollectionPop();
            }
        });
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    public void dimissCollectionPop(){
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
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

    /**
     * 支付宝账户授权业务
     */
    public void aliAuth(){
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
                AuthTask authTask = new AuthTask(activity);
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

    /**
     * 支付宝支付
     */

    public void aliPay() {
        boolean rsa2 = (MainApp.RSA_PRIVATE.length() > 0);
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildOrderParamMap(MainApp.ALIPAY_APP_ID, rsa2);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

//        String privateKey = rsa2 ? MainApp.RSA_PRIVATE : MainApp.RSA2_PRIVATE;
        String privateKey = MainApp.RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
        final String authInfo = info + "&" + sign;
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(getActivity());
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

}
