package com.emao.application.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.emao.application.R;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.utils.ToastUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @author keybon
 */

public class WeChatLoginActivity extends BaseLoginActivity {

    private Button wechat_login;
    private IWXAPI wechatApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        wechatApi = WXAPIFactory.createWXAPI(this, MainApp.WECHAT_APP_ID);
        setContentView(R.layout.activity_wechat_login);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();
        setCommon_title(this.getResources().getString(R.string.wechat_title));


        wechatApi.registerApp(MainApp.WECHAT_APP_ID);

        wechat_login = findViewById(R.id.wechat_login);
        wechat_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(WeChatLoginActivity.this,MainActivity.class));
                loginToWeiXin();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
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
