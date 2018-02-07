package com.emao.application.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * @author keybon
 */

public class LongTouchBtn extends android.support.v7.widget.AppCompatButton {


    private LongTouchListener mListener;
    private long downTime;

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     */
    public LongTouchBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    /**
     * 处理touch事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downTime = System.currentTimeMillis();
            mListener.onLongDownTouch(downTime);
            Log.i("huahua", "按下");
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mListener.onLongUpTouch(downTime);
            Log.i("huahua", "弹起");
        }
        return super.onTouchEvent(event);
    }

    public void setOnLongTouchListener(LongTouchListener listener) {
        mListener = listener;
    }

    /**
     * 长按监听接口，使用按钮长按的地方应该注册此监听器来获取回调。
     */
    public interface LongTouchListener {

        /**
         * 按下
         */
        void onLongDownTouch(long downTime);
        /**
         * 抬起
         */
        void onLongUpTouch(long downTime);
    }


}
