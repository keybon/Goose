package com.emao.application.ui.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.emao.application.ui.application.MainApp;

/**
 *
 * @author keybon
 */

public class ToastUtils {

    private static Toast toast = null;

    private static Handler mHandler = null;

    public static int mDuration = Toast.LENGTH_LONG;

    public static String mMsg = null;

    private static void initToast() {
//        if (toast == null) {
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    toast = Toast.makeText(MainApp.IMApp, "", Toast.LENGTH_SHORT);
                }
            });
//        }
    }

    private static void show(final int duration, final String msg) {
        mDuration = duration;
        mMsg = msg;
        mHandler.removeCallbacks(showRunnable);
        mHandler.postDelayed(showRunnable, 100);
    }

    public static Runnable showRunnable = new Runnable() {

        @Override
        public void run() {
            if (toast != null) {
                toast.setDuration(mDuration);
                toast.setText(mMsg);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    };

    public static void showShortToast(String msg){
        initToast();
        show(Toast.LENGTH_SHORT, msg);
    }
    public static void showShortToast(int resID){
        initToast();
        show(Toast.LENGTH_LONG, MainApp.IMApp.getResources().getString(resID));
    }

    public static void showLongToast(String msg){
        initToast();
        show(Toast.LENGTH_LONG, msg);
    }
    public static void showLongToast(int resID){
        initToast();
        show(Toast.LENGTH_LONG, MainApp.IMApp.getResources().getString(resID));
    }

}
