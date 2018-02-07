package com.emao.application.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.emao.application.R;
import com.emao.application.http.OkHttpManager;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;

import okhttp3.Request;


/**
 *
 * @author keybon
 */

public class LoginActivity extends BaseLoginActivity implements View.OnClickListener{

    private ImageView wechat_login,mobile_login;

    private IWXAPI wechatApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        wechatApi = WXAPIFactory.createWXAPI(this, MainApp.WECHAT_APP_ID);
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView() {

        downLoadTitles();

        wechatApi.registerApp(MainApp.WECHAT_APP_ID);

        wechat_login = findViewById(R.id.weichat_login);
        mobile_login = findViewById(R.id.mobile_login);


        wechat_login.setOnClickListener(this);
        mobile_login.setOnClickListener(this);
    }


    private void downLoadTitles(){

        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_MAIN_CLASSIFY, null, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("标题   result = " + result);
                MainApp.titlesList = GsonUtils.getClassifyParams(result);
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("标题获取失败了");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.weichat_login:
                loginToWeiXin();
                break;
            case R.id.mobile_login:
                startActivity(new Intent(LoginActivity.this,MobileLoginActivity.class));
                break;
            default:
                break;
        }
    }

    private void loginToWeiXin() {

        if (wechatApi != null && wechatApi.isWXAppInstalled()) {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "emao_wx_login";
            wechatApi.sendReq(req);
        } else {
            ToastUtils.showShortToast(baseActivity.getResources().getString(R.string.wechat_login_word));
        }
    }
}
