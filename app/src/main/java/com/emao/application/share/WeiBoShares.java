package com.emao.application.share;

import android.app.Activity;

import com.sina.weibo.sdk.share.WbShareHandler;

/**
 * Created by keybon on 2018/1/20.
 */

public class WeiBoShares {

    private Activity mActivity;
    private WbShareHandler shareHandler;


    public WeiBoShares(Activity activity) {
        this.mActivity = activity;
        initRegister();
    }

    public void initRegister(){
        WbShareHandler shareHandler = new WbShareHandler(mActivity);
        shareHandler.registerApp();
    }
}
