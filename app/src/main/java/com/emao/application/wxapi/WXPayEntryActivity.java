package com.emao.application.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.utils.ToastUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @author keybon
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	api = WXAPIFactory.createWXAPI(this, MainApp.WECHAT_APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {

	}

	@Override
	public void onResp(BaseResp baseResp) {

		/**
		 * -1 错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
		 * -2 用户取消 无需处理。发生场景：用户不支付了，点击取消，返回APP
		 */

		if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			int code = baseResp.errCode;
			switch (code) {
				case 0:
					ToastUtils.showShortToast("支付成功");
					setResult(RESULT_OK);
					finish();
					break;
				case -1:
					ToastUtils.showShortToast("支付失败");
					finish();
					break;
				case -2:
					ToastUtils.showShortToast("支付取消");
					finish();
					break;
				default:
					ToastUtils.showShortToast("支付失败");
					setResult(RESULT_OK);
					finish();
					break;
			}
		}
	}
}