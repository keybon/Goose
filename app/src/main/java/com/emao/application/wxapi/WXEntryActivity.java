package com.emao.application.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.emao.application.R;
import com.emao.application.http.OkHttpManager;
import com.emao.application.ui.activity.MainActivity;
import com.emao.application.ui.activity.MobileLoginActivity;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;


/**
 * @author keybon
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI wechatApi;

    private String openid,access_token,nickname,sex,headimgurl,unionid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wechatApi = WXAPIFactory.createWXAPI(this, MainApp.WECHAT_APP_ID, true);
        initView();
    }

    public void initView() {
        wechatApi.handleIntent(this.getIntent(),this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        wechatApi.handleIntent(intent, this);
    }




    @Override
    public void onReq(BaseReq baseReq) {
        //微信发送的请求将回调到onReq方法
        LogUtils.e("onReq");
        ToastUtils.showShortToast(baseReq.toString());
        this.finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtils.e("onResp");
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                LogUtils.e("ERR_OK");
                //发送成功
                try{
                    SendAuth.Resp sendResp = (SendAuth.Resp) baseResp;
                    if (sendResp != null) {
                        String code = sendResp.code;
                        getAccess_token(code);
                    }
                } catch (Exception e){
                    this.finish();
                }

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                LogUtils.e("ERR_USER_CANCEL");
                //发送取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                LogUtils.e("ERR_AUTH_DENIED");
                ToastUtils.showShortToast("DENIED");
                //发送被拒绝
                break;
            default:
                //发送返回
                break;
        }
        this.finish();
    }

    /**
     * 获取openid accessToken值用于后期操作
     * @param code 请求码
     */
    private void getAccess_token(final String code) {
        String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + MainApp.WECHAT_APP_ID
                + "&secret="
                + MainApp.WECHAT_APP_SECRET
                + "&code="
                + code
                + "&grant_type=authorization_code";
        LogUtils.e("getAccess_token：" + path);
        //网络请求，根据自己的请求方式
        OkHttpManager.getInstance().getAsync(path, new OkHttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                ToastUtils.showShortToast(CommonUtils.getResourceString(R.string.login_failed));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("getAccess_token_result:" + result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    openid = jsonObject.getString("openid").toString().trim();
                    access_token = jsonObject.getString("access_token").toString().trim();
                    unionid = jsonObject.getString("unionid").toString().trim();

                    getUserMesg(access_token, openid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取微信的个人信息
     * @param access_token
     * @param openid
     */
    private void getUserMesg(final String access_token, final String openid) {
        String path = "https://api.weixin.qq.com/sns/userinfo?access_token="
                + access_token
                + "&openid="
                + openid;
        LogUtils.e("getUserMesg：" + path);
        //网络请求，根据自己的请求方式
        OkHttpManager.getInstance().getAsync(path, new OkHttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                ToastUtils.showShortToast(CommonUtils.getResourceString(R.string.login_failed));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("getUserMesg_result:" + result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    nickname = jsonObject.getString("nickname");
                    sex = jsonObject.get("sex").toString();
                    headimgurl = jsonObject.getString("headimgurl");


                    LogUtils.e("用户基本信息:");
                    LogUtils.e("nickname:" + nickname);
                    LogUtils.e("sex:" + sex);
                    LogUtils.e("headimgurl:" + headimgurl);

                    gotoLogin();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }

    public void gotoLogin(){
        HashMap<String,String> params = new HashMap<String,String> ();
        params.put("openid",openid);
        params.put("nickname",nickname);
        params.put("sex",sex);
        params.put("headimgurl",headimgurl);
        params.put("unionid",unionid);
        if(MobileLoginActivity.mobiel == null){
            MobileLoginActivity.mobiel = "";
        }
        params.put("mobile", MobileLoginActivity.mobiel);
        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_WECHAT_LOGIN, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("微信登录成功了  "+result);
                JSONObject jsonObject = new JSONObject(result);
                if(!jsonObject.isNull("error")){
                    if(Constants.ERROR_CODE_200.equals(jsonObject.getString("error"))){
                        JSONObject userData = new JSONObject(jsonObject.getString("data"));

                        SharedPreUtils.put(SharedPreUtils.SP_OPENID,openid);
                        SharedPreUtils.put(SharedPreUtils.SP_USERID,userData.getString("id"));
                        SharedPreUtils.put(SharedPreUtils.SP_ACCESS_TOKEN,access_token);
                        SharedPreUtils.put(SharedPreUtils.SP_NICKNAME,userData.getString("username"));
                        SharedPreUtils.put(SharedPreUtils.SP_SEX,userData.getString("sex"));
                        SharedPreUtils.put(SharedPreUtils.SP_HEADIMGURL,userData.getString("headimgurl"));
                        SharedPreUtils.put(SharedPreUtils.SP_SIGNATURE,userData.getString("signature"));
                        SharedPreUtils.put(SharedPreUtils.SP_MOBILE,userData.getString("mobile"));

                        ToastUtils.showShortToast(CommonUtils.getResourceString(R.string.login_sucess));
                        Intent intent = new Intent();
                        intent.setClass(WXEntryActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else if(Constants.ERROR_CODE_201.equals(jsonObject.getString("error"))){
                        ToastUtils.showShortToast("添加失败");
                    } else if(Constants.ERROR_CODE_202.equals(jsonObject.getString("error"))){
                        ToastUtils.showShortToast("缺少参数");
                    }
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                ToastUtils.showShortToast(CommonUtils.getResourceString(R.string.login_failed));
            }
        });
    }
}
