package com.emao.application.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.http.OkHttpManager;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.callback.CountDownCallBack;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.emao.application.ui.view.ClearableEditText;
import com.emao.application.ui.view.MyCount;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import okhttp3.Request;

/**
 * @author keybon
 */

public class MobileLoginActivity extends BaseLoginActivity implements View.OnClickListener, CountDownCallBack {

    public static final String MOBILE_TITLE = "MOBILE_TITLE";
    public static final String FROM = "from";

    public static String mobiel = "";

    private ClearableEditText phoneNum;
    private ClearableEditText verifycodeEt;
    private TextView mobile_verifycode_btn;
    private Button loginBtn;

    private String userName, password, verifycode;
    private MyCount myCount;
    private String title;

    private static Pattern mobilePattern = Pattern.compile("^([1]\\d{10})|((852|853)\\d{8})$");
    private static Pattern smsCodePattern = Pattern.compile("\\d{5}");

    private IWXAPI wechatApi;
    private String from;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_mobile_login);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView() {
        title = getIntent().getStringExtra(MOBILE_TITLE);
        if (TextUtils.isEmpty(title)) {
            setCommon_title(this.getResources().getString(R.string.mobile_title));
        } else {
            setCommon_title(title);
        }
        from = getIntent().getStringExtra(FROM);
        wechatApi = WXAPIFactory.createWXAPI(this, MainApp.WECHAT_APP_ID);
        wechatApi.registerApp(MainApp.WECHAT_APP_ID);

        phoneNum = findViewById(R.id.mobile_phone_num);
        verifycodeEt = findViewById(R.id.mobile_verifycode);
        loginBtn = findViewById(R.id.mobile_login_button);
        mobile_verifycode_btn = findViewById(R.id.mobile_verifycode_btn);
        mobile_verifycode_btn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void initData() {
        userName = phoneNum.getText().toString().trim();
        password = verifycodeEt.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mobile_verifycode_btn:

                initData();

                if ("".equals(userName)) {
                    ToastUtils.showLongToast("手机号不能为空");
                    loginBtn.setClickable(true);
                    return;
                }

                if (!mobilePattern.matcher(userName).matches()) {
                    ToastUtils.showLongToast("请输入正确的手机号码");
                    loginBtn.setClickable(true);
                    return;
                }

                refreshSmsTimer();
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("mobile", userName);
                OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_MOBILE_LOGIN_VERIFYCODE, params, new OkHttpManager.DataCallBack() {

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        LogUtils.e("result = " + result);
                        HashMap<String,String> map = GsonUtils.getMapParams(result);
                        if("200".equals(map.get("error"))){
                            verifycode = map.get("code");
                        }
                    }

                    @Override
                    public void requestFailure(Request request, IOException e) {

                    }
                });

                break;
            case R.id.mobile_login_button:

                hideSystemKeyBoard(v);
                login();

                break;
            default:
                break;
        }
    }


    /**
     * 登陆
     */
    private void login() {

        loginBtn.setClickable(false);

//        DialogFactory.showProgressDialog(this);

        initData();

        if ("".equals(userName)) {
            ToastUtils.showLongToast("手机号不能为空");
            loginBtn.setClickable(true);
            return;
        }

        if (!mobilePattern.matcher(userName).matches()) {
            ToastUtils.showLongToast("请输入正确的手机号码");
            loginBtn.setClickable(true);
            return;
        }

        if ("".equals(password)) {
            ToastUtils.showLongToast("短信验证码不能为空");
            loginBtn.setClickable(true);
            return;
        }

        if (!smsCodePattern.matcher(password).matches()) {
            ToastUtils.showLongToast("短信验证码需5位数字");
            loginBtn.setClickable(true);
            return;
        }

        if(!password.equals(verifycode)){
            ToastUtils.showLongToast("请输入正确的验证码");
            loginBtn.setClickable(true);
            return;
        }

        // 发起登录
        HashMap<String,String> params = new HashMap<>();
        params.put("mobile",userName);
        params.put("code",password);
        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_MOBILE_LOGIN, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("手机登录成功 = "+result);
                JSONObject jsonObject = new JSONObject(result);
                if(Constants.ERROR_CODE_200.equals(jsonObject.getString("error"))){
                    verifycodeEt.setText("");
                    String data = jsonObject.getString("data");
                    if(!TextUtils.isEmpty(data)){

                        JSONObject userData = new JSONObject(data);

                        SharedPreUtils.put(SharedPreUtils.SP_OPENID,userData.getString("openid"));
                        SharedPreUtils.put(SharedPreUtils.SP_USERID,userData.getString("id"));
                        SharedPreUtils.put(SharedPreUtils.SP_NICKNAME,userData.getString("username"));
                        SharedPreUtils.put(SharedPreUtils.SP_SEX,userData.getString("sex"));
                        SharedPreUtils.put(SharedPreUtils.SP_HEADIMGURL,userData.getString("headimgurl"));
                        SharedPreUtils.put(SharedPreUtils.SP_SIGNATURE,userData.getString("signature"));
                        SharedPreUtils.put(SharedPreUtils.SP_MOBILE,userData.getString("mobile"));



                        Intent intent = new Intent();
                        intent.setClass(MobileLoginActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();



                    } else {
                        mobiel = userName;
                        startActivity(new Intent(MobileLoginActivity.this,WeChatLoginActivity.class));
                        myCount.cancel();
                        myCount.onFinish();

                    }
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("手机登录失败 = " + request.toString());
            }
        });
    }

    public void refreshSmsTimer() {
        //时间误差问题
        myCount = new MyCount(59 * 1000L + 1080, 1000L, this);
        myCount.start();
    }

    private void hideSystemKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void CountDownOnTick(long millisUntilFinished) {
        mobile_verifycode_btn.setClickable(false);
        mobile_verifycode_btn.setEnabled(false);
        mobile_verifycode_btn.setText(millisUntilFinished / 1000 - 1 + "秒后重发");
    }

    @Override
    public void CountDownOnFinish() {
        mobile_verifycode_btn.setClickable(true);
        mobile_verifycode_btn.setEnabled(true);
        mobile_verifycode_btn.setText("获取验证码");
    }
}
