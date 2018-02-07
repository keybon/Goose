package com.emao.application.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Toast;

import com.emao.application.R;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.LogUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @author keybon
 */
public class WeChatShare implements IShareInfo{
    public static boolean isLastTimeline = false;
    private Context mContext;
    private boolean mIsTimeline = false;
    public static IWXAPI wechatApi;

    public WeChatShare(Context context) {
        mContext = context;
        wechatApi = WXAPIFactory.createWXAPI(context, MainApp.WECHAT_APP_ID, false);
        auth();
    }

    /**
     * 方法简介：设置是否分享到朋友圈</br>
     * 输入项说明：flag为true表示分享到朋友圈, 否则分享到会话.</br>
     * 返回项说明：
     *
     * @param flag true: 朋友圈; false: 会话
     */
    public void setTimeline(boolean flag) {
        mIsTimeline = flag;
    }

    public boolean isInstalled(){
        return wechatApi.isWXAppInstalled();
    }

    @Override
    public void auth() {
        boolean registerflag = wechatApi.registerApp(MainApp.WECHAT_APP_ID);
        LogUtils.e("--->registerflag==" + registerflag);
    }

    @Override
    public void shareText(String text) {
        if (null == text || TextUtils.isEmpty(text)) {
            Toast.makeText(mContext, "内容为空", Toast.LENGTH_SHORT).show();
            return;
        }

        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.title = "haha";
        msg.mediaObject = textObject;
        msg.description = text;

        sendReq(msg, mIsTimeline, Constants.TYPE_TEXT);
    }

    @Override
    public void shareImage(Bitmap image) {

        WXImageObject imgObj = new WXImageObject(image);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap thumb = Bitmap.createScaledBitmap(image, Constants.WECHAT_IMG_THUMB_SIZE, Constants.WECHAT_IMG_THUMB_SIZE, true);
        msg.thumbData = Util.bmpToByteArray(thumb, true);

        sendReq(msg, mIsTimeline, Constants.TYPE_IMAGE);
    }

    @Override
    public void shareUrl(String url) {
        shareUrl(url, "一个鹅毛的有趣故事～");
    }

    public void shareUrl(String url, String title) {
        WXWebpageObject wpObj = new WXWebpageObject();
        wpObj.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(wpObj);
        msg.title = title;
        msg.description = "来自鹅毛的分享";
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ico_logo);
        msg.thumbData = Util.bmpToByteArray(bitmap, true);
        int size = msg.thumbData.length;
        if (size >= 32 * 1024) {    // thumbData greater than 32kb
            Bitmap m = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ico_like);
            msg.thumbData = Util.bmpToByteArray(m, true);
        }
        sendReq(msg, mIsTimeline, Constants.TYPE_URL);
    }

    public void shareUrl(String url, String title, String content, Bitmap sharedBitmap) {
        // 32KB
        int MAX_SIZE_THUMBNAIL_BYTE = 1 << 15;
        WXWebpageObject wpObj = new WXWebpageObject();
        wpObj.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(wpObj);

        //标题
        if (!TextUtils.isEmpty(title)) {
            msg.title = title;
        } else {
            msg.title = "来自鹅毛的分享";
        }

        //正文
        if (!TextUtils.isEmpty(content)) {
            //description如果设置过长会分享失败
            if (content.length() > 200) {
                msg.description = content.substring(0, 199);
            } else {
                msg.description = content;
            }
        } else {
            msg.description = "来自鹅毛的分享";
        }

        //缩略图
        Bitmap thumbnailImg;
        if (sharedBitmap != null) {
            thumbnailImg = sharedBitmap;
        } else {
            thumbnailImg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ico_like);
        }
        //缩略图不能大于32K
        if (thumbnailImg.getByteCount() > MAX_SIZE_THUMBNAIL_BYTE) {
            double scale = Math.sqrt(1.0 * thumbnailImg.getByteCount() / MAX_SIZE_THUMBNAIL_BYTE);
            int scaledW = (int) (thumbnailImg.getWidth() / scale);
            int scaledH = (int) (thumbnailImg.getHeight() / scale);
            thumbnailImg = Bitmap.createScaledBitmap(thumbnailImg, scaledW, scaledH, true);
            if (thumbnailImg.getByteCount() > MAX_SIZE_THUMBNAIL_BYTE) {
                thumbnailImg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ico_like);
            }
        }
        msg.thumbData = Util.bmpToByteArray(thumbnailImg, true);
        sendReq(msg, mIsTimeline, Constants.TYPE_URL);
    }

    private void sendReq(WXMediaMessage msg, boolean isTimeline, final String type) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        // transaction字段唯一标识一个请求
        req.transaction = buildTransaction(type);
        req.message = msg;
        req.scene = mIsTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        isLastTimeline = isTimeline;
        wechatApi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
