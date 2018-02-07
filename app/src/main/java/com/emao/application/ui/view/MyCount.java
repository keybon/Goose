package com.emao.application.ui.view;

import android.os.CountDownTimer;

import com.emao.application.ui.callback.CountDownCallBack;

/**
 *
 * @author keybon
 */

public class MyCount extends CountDownTimer {

    public CountDownCallBack countDownCallBack;

    public MyCount(long millisInFuture, long countDownInterval,CountDownCallBack mCountCallBack) {
        super(millisInFuture, countDownInterval);
        this.countDownCallBack = mCountCallBack;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        countDownCallBack.CountDownOnTick(millisUntilFinished);
    }

    @Override
    public void onFinish() {
        countDownCallBack.CountDownOnFinish();
    }
}
