package com.emao.application.share;

import android.content.Context;

import com.emao.application.R;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.utils.ToastUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;

/**
 * @author keybon
 */
public class WeChatPay {

    private Context mContext;
    public static IWXAPI wechatApi;

    public WeChatPay(Context context) {
        mContext = context;
        wechatApi = WXAPIFactory.createWXAPI(context, MainApp.WECHAT_APP_ID, false);
        wechatApi.registerApp(MainApp.WECHAT_APP_ID);
    }

    public void toPay(HashMap<String, String> map) {
        if (wechatApi != null && wechatApi.isWXAppInstalled()) {
            PayReq req = new PayReq();
            req.appId = map.get("appid");
            req.partnerId = map.get("partnerid");
            req.prepayId = map.get("prepayid");
            req.nonceStr = map.get("noncestr");
            req.timeStamp = map.get("timestamp");
            req.packageValue = map.get("package");
            req.sign = map.get("sign");
            req.extData = "app data";
            wechatApi.sendReq(req);
        } else {
            ToastUtils.showShortToast(mContext.getResources().getString(R.string.wechat_login_word));
        }
    }
}
