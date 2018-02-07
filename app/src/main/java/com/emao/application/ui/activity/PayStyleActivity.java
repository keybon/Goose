package com.emao.application.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.AuthTask;
import com.emao.application.R;
import com.emao.application.http.OkHttpManager;
import com.emao.application.share.AuthResult;
import com.emao.application.share.PayResult;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

/**
 *
 * @author keybon
 * @date 2018/1/28
 */

public class PayStyleActivity extends BaseActivity implements View.OnClickListener{


    private RelativeLayout pay_style_wechat;
    private TextView pay_style_wechat_tv;

    private RelativeLayout pay_style_alipay;
    private TextView pay_style_alipay_tv;

    private String payStyle;




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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_pay_style);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();

        pay_style_wechat = findViewById(R.id.pay_style_wechat);
        pay_style_wechat_tv = findViewById(R.id.pay_style_wechat_tv);
        pay_style_alipay = findViewById(R.id.pay_style_alipay);
        pay_style_alipay_tv = findViewById(R.id.pay_style_alipay_tv);

//        pay_style_wechat.setOnClickListener(this);
//        pay_style_alipay.setOnClickListener(this);

    }


    @Override
    public void initData() {
        super.initData();

        if(Constants.COLLECTION_ALI_TYPE.equals(SharedPreUtils.get(SharedPreUtils.SP_COLLECTION,""))){
            payStyle = Constants.COLLECTION_ALI_TYPE;
        } else {
            payStyle = Constants.COLLECTION_WECHAT_TYPE;
        }

//        initShowPay(payStyle);

    }

    private void initShowPay(String payStyle){
        if(payStyle.equals(Constants.COLLECTION_ALI_TYPE)){
            pay_style_alipay_tv.setText("已授权");
            pay_style_wechat_tv.setText("未授权");
        } else {
            pay_style_wechat_tv.setText("已授权");
            pay_style_alipay_tv.setText("未授权");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pay_style_wechat:
                SharedPreUtils.put(SharedPreUtils.SP_COLLECTION,Constants.COLLECTION_WECHAT_TYPE);
                initShowPay(Constants.COLLECTION_WECHAT_TYPE);
                break;
            case R.id.pay_style_alipay:
                SharedPreUtils.put(SharedPreUtils.SP_COLLECTION, Constants.COLLECTION_ALI_TYPE);
                initShowPay(Constants.COLLECTION_ALI_TYPE);
                OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_ALIPAY_AUTH, null, new OkHttpManager.DataCallBack() {
                    @Override
                    public void requestSuccess(String result) throws Exception {
                        HashMap<String,String> map = GsonUtils.getMapParams(result);
                        if(!TextUtils.isEmpty(map.get("key"))){
                            aliAuth(map.get("key"));
                        }
                    }

                    @Override
                    public void requestFailure(Request request, IOException e) {

                    }
                });
                break;
            default:
                break;
        }
    }


    /**
     * 支付宝账户授权业务
     */
    public void aliAuth(final String authInfo){
//        boolean rsa2 = (MainApp.RSA_PRIVATE.length() > 0);
//        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(MainApp.PID, MainApp.ALIPAY_APP_ID, MainApp.TARGET_ID, rsa2);
//        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
//
//        String privateKey = rsa2 ? MainApp.RSA_PRIVATE : MainApp.RSA2_PRIVATE;
//        String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
//        final String authInfo = info + "&" + sign;
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(PayStyleActivity.this);
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
