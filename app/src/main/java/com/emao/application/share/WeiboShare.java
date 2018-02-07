//package com.emao.application.share;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.widget.Toast;
//
//import com.emao.application.R;
//import com.emao.application.ui.application.MainApp;
//import com.emao.application.ui.utils.Constants;
//import com.emao.application.ui.utils.ToastUtils;
//import com.sina.weibo.sdk.api.ImageObject;
//import com.sina.weibo.sdk.api.TextObject;
//import com.sina.weibo.sdk.api.WebpageObject;
//import com.sina.weibo.sdk.api.WeiboMultiMessage;
//import com.sina.weibo.sdk.api.share.IWeiboHandler;
//import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
//import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
//import com.sina.weibo.sdk.api.share.WeiboShareSDK;
//import com.sina.weibo.sdk.auth.AuthInfo;
//import com.sina.weibo.sdk.auth.Oauth2AccessToken;
//import com.sina.weibo.sdk.auth.WeiboAuthListener;
//import com.sina.weibo.sdk.auth.sso.SsoHandler;
//import com.sina.weibo.sdk.exception.WeiboException;
//import com.sina.weibo.sdk.utils.Utility;
//
///**
// *
// * @author keybon
// */
//
//public class WeiboShare implements IShareInfo{
//
//    private Context mContext;
//
//    private IWeiboShareAPI mWeiboShareAPI;
//
//    private AuthInfo authInfo;
//    private SsoHandler ssoHandler;
//    private Oauth2AccessToken accessToken;
//
//    public WeiboShare(Context mContext) {
//        this.mContext = mContext;
//        auth();
//    }
//
//    @Override
//    public void auth() {
//        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, MainApp.WEIBO_APP_ID);
//        mWeiboShareAPI.registerApp();
//        authInfo = new AuthInfo(mContext, MainApp.WEIBO_APP_ID, MainApp.WEIBO_REDIRECT_URL, MainApp.WEIBO_SCOPE);
//
//        ssoHandler = new SsoHandler((Activity) mContext, authInfo);
//        accessToken = readAccessToken(mContext);
//    }
//
//    public void init(){
//        accessToken = readAccessToken(mContext);
//        // 未授权或授权已过期, 重新授权
//        if (null == accessToken || !accessToken.isSessionValid()) {
//            ssoHandler.authorize(new AuthListener());
//        }
//    }
//
//    public boolean hasAuthed() {
//        accessToken = readAccessToken(mContext);
//        return (accessToken != null && accessToken.isSessionValid());
//    }
//
//    @Override
//    public void shareText(String text) {
//        if (hasAuthed()) {
//            if (mWeiboShareAPI.isWeiboAppInstalled()) {
//                TextObject textObj = new TextObject();
//                textObj.text = text;
//                WeiboMultiMessage msg = new WeiboMultiMessage();
//                msg.textObject = textObj;
//                sendReq(msg, Constants.TYPE_TEXT);
//            }
//        } else {
//            showNotAuthed();
//        }
//    }
//
//    public void handleWeiboResponse(Intent intent, IWeiboHandler.Response var2){
//        mWeiboShareAPI.handleWeiboResponse(intent,var2);
//    }
//
//    @Override
//    public void shareImage(Bitmap image) {
//        if (hasAuthed()) {
//            if (mWeiboShareAPI.isWeiboAppInstalled()) {
//                ImageObject imgObj = new ImageObject();
//                imgObj.setImageObject(image);
//                WeiboMultiMessage msg = new WeiboMultiMessage();
//                msg.imageObject = imgObj;
//                sendReq(msg, Constants.TYPE_IMAGE);
//            }
//        } else {
//            showNotAuthed();
//        }
//    }
//
//    private void showNotAuthed() {
//        ToastUtils.showShortToast("请先授权!");
//        init();
//    }
//
//    @Override
//    public void shareUrl(String url) {
//    }
//
//    public void shareUrlAllInOne(String url, String title, String description) {
//        if (hasAuthed()) {
//            if (mWeiboShareAPI.isWeiboAppInstalled()) {
//                WebpageObject webpageObject = new WebpageObject();
//
//                webpageObject.identify = Utility.generateGUID();
//                if (!TextUtils.isEmpty(title)) {
//                    webpageObject.title = title;
//                }
//                if (!TextUtils.isEmpty(description)) {
//                    webpageObject.description = description;
//                } else {
//                    webpageObject.description = "来自鹅毛的分享";
//                }
//                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ico_like);
//                // 设置 Bitmap 类型的图片到视频对象里 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
//                if (bitmap.getByteCount() > 32 * 1024) {
//                    bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ico_like);
//                }
//                webpageObject.setThumbImage(bitmap);
//                webpageObject.actionUrl = url;
//                webpageObject.defaultText = "";
//
//                WeiboMultiMessage msg = new WeiboMultiMessage();
//                msg.mediaObject = webpageObject;
//                sendReqAllInOne(msg, Constants.TYPE_URL);
//            } else {
//                Toast.makeText(mContext, "没有安装新浪微博客户端", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            showNotAuthed();
//        }
//    }
//
//
//    private void sendReq(WeiboMultiMessage msg, final String type) {
//        SendMultiMessageToWeiboRequest req = new SendMultiMessageToWeiboRequest();
//        req.transaction = getTransaction(type);
//        req.multiMessage = msg;
//        mWeiboShareAPI.sendRequest((Activity) mContext, req);
//    }
//
//    private void sendReqAllInOne(WeiboMultiMessage msg, final String type) {
//        SendMultiMessageToWeiboRequest req = new SendMultiMessageToWeiboRequest();
//        req.transaction = getTransaction(type);
//        req.multiMessage = msg;
//
//        AuthInfo authInfo = new AuthInfo(mContext, MainApp.WEIBO_APP_ID, MainApp.WEIBO_REDIRECT_URL, MainApp.WEIBO_SCOPE);
//        Oauth2AccessToken accessToken = readAccessToken(mContext);
//        String token = "";
//        if (accessToken != null) {
//            token = accessToken.getToken();
//        }
//        mWeiboShareAPI.sendRequest((Activity) mContext, req, authInfo, token, new WeiboAuthListener() {
//
//            @Override
//            public void onWeiboException(WeiboException arg0) {
//            }
//
//            @Override
//            public void onComplete(Bundle bundle) {
//                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
//                writeAccessToken(mContext, newToken);
//            }
//
//            @Override
//            public void onCancel() {
//            }
//        });
//    }
//
//
//    public String getTransaction(final String type) {
//        return (null == type) ? String.valueOf(System.currentTimeMillis()) : (type + "_" + String.valueOf(System.currentTimeMillis()));
//    }
//
//    public static void writeAccessToken(Context context, Oauth2AccessToken token) {
//        if (null == context || null == token) {
//            return;
//        }
//
//        SharedPreferences pref = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_APPEND);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString(Constants.KEY_UID, token.getUid());
//        editor.putString(Constants.KEY_ACCESS_TOKEN, token.getToken());
//        editor.putLong(Constants.KEY_EXPIRES_IN, token.getExpiresTime());
//        editor.commit();
//    }
//
//    /**
//     * 从 SharedPreferences 读取 Token 信息。
//     */
//    public static Oauth2AccessToken readAccessToken(Context context) {
//        if (null == context) {
//            return null;
//        }
//        Oauth2AccessToken token = new Oauth2AccessToken();
//        SharedPreferences pref = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_APPEND);
//        if (null == pref || !pref.contains(Constants.KEY_ACCESS_TOKEN)) {
//            return null;
//        }
//        token.setUid(pref.getString(Constants.KEY_UID, ""));
//        token.setToken(pref.getString(Constants.KEY_ACCESS_TOKEN, ""));
//        token.setExpiresTime(pref.getLong(Constants.KEY_EXPIRES_IN, 0));
//        return token;
//    }
//
//    private class AuthListener implements WeiboAuthListener {
//        @Override
//        public void onCancel() {
//            Toast.makeText(mContext, "取消授权", Toast.LENGTH_LONG).show();
//        }
//
//        @Override
//        public void onComplete(Bundle values) {
//            accessToken = Oauth2AccessToken.parseAccessToken(values);
//            if (accessToken.isSessionValid()) {
//                // 保存 Token 到 SharedPreferences
////                ShareUtils.AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
//                ToastUtils.showShortToast("授权成功");
////                if (callback != null) {
////                    callback.onCallBack(null);
////                }
//            } else {
//                /**
//                 * 以下几种情况，您会收到 Code：
//                 * 1. 当您未在平台上注册的应用程序的包名与签名时；
//                 * 2. 当您注册的应用程序包名与签名不正确时；
//                 * 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
//                 */
//                String code = values.getString("code");
//                String message = "授权失败";
//                if (!TextUtils.isEmpty(code)) {
//                    message = message + "\nObtained the code: " + code;
//                }
//                ToastUtils.showShortToast(message);
//            }
//        }
//
//        @Override
//        public void onWeiboException(WeiboException e) {
//            e.printStackTrace();
//            ToastUtils.showShortToast("Auth exception : " + e.getMessage());
//        }
//
//    }
//}
