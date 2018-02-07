package com.emao.application.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emao.application.R;

import cn.jzvd.JZVideoPlayer;

/**
 *
 * @author keybon
 */

public class BaseActivity extends AppCompatActivity {

    public TextView actionBartitle;
    private LinearLayout backBtnLeft;
    private LinearLayout backBtnRight;
    private LinearLayout actionbar_details;
    private TextView actionBarRight;
    public ActionBar actionBar;

    /**
     * 视频相关
     */
    private LinearLayout video_ll;
    private ImageView flashlight,camera_turn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initView();
        initData();
    }

    private void init(){
        actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBartitle = actionBar.getCustomView().findViewById(R.id.actionbar_title);
        backBtnLeft = actionBar.getCustomView().findViewById(R.id.actionbar_back_left);
        backBtnRight = actionBar.getCustomView().findViewById(R.id.actionbar_back_right);
        actionBarRight = actionBar.getCustomView().findViewById(R.id.actionbar_text_right);
        actionbar_details = actionBar.getCustomView().findViewById(R.id.actionbar_details);

        video_ll = actionBar.getCustomView().findViewById(R.id.actionbar_back_video_right);
        flashlight = actionBar.getCustomView().findViewById(R.id.video_flashlight);
        camera_turn = actionBar.getCustomView().findViewById(R.id.video_camera_turn);

        backBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backKeyPress();
            }
        });
        actionBarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitPress();
            }
        });
        backBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compilePopWindow(v);
            }
        });
        actionbar_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSharePopWindow(v);
            }
        });
        flashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashLight(v);
            }
        });
        camera_turn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnCamera(v);
            }
        });
    }

    public void initView(){

    }

    public void initData(){

    }

    public void setActionBarTitle(String title){
        if(actionBartitle != null){
            actionBartitle.setText(title);
        }
    }

    /**
     * true 显示 false 隐藏
     */
    public void setBackBtnLeft(boolean flag){
        if(backBtnLeft != null){
            if(flag){
                backBtnLeft.setVisibility(View.VISIBLE);
            } else {
                backBtnLeft.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置bar 右边自定义字体
     */
    public void setTextRight(String text){
        if(actionBarRight != null){
            actionBarRight.setText(text);
        }
    }
    /**
     * true 显示 false 隐藏
     */
    public void setTextViewRight(boolean flag){
        if(actionBarRight != null){
            if(flag){
                actionBarRight.setVisibility(View.VISIBLE);
            } else {
                actionBarRight.setVisibility(View.GONE);
            }
        }
    }
    /**
     * true 显示 false 隐藏
     */
    public void setShareBtn(boolean flag){
        if(actionbar_details != null){
            if(flag){
                actionbar_details.setVisibility(View.VISIBLE);
            } else {
                actionbar_details.setVisibility(View.GONE);
            }
        }
    }
    public void setRightClickEnable(boolean enable){
        if(actionBarRight != null){
            actionBarRight.setClickable(enable);
        }
    }
    public void setRightTextColor(int resId){
        if(actionBarRight != null){
            actionBarRight.setTextColor(resId);
        }
    }
    /**
     * video 右侧控制
     */
    public void setVideovisibility(boolean visibility){
        if(visibility){
            video_ll.setVisibility(View.VISIBLE);
        } else {
            video_ll.setVisibility(View.GONE);
        }
    }

    /**
     * true 显示 false 隐藏
     */
    public void setBackBtnRight(boolean flag){
        if(backBtnLeft != null){
            if(flag){
                backBtnRight.setVisibility(View.VISIBLE);
            } else {
                backBtnRight.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            backKeyPress();
            return false;
        }
        return false;
    }

    public void backKeyPress() {
        this.finish();
    }
    public void commitPress(){}
    public void compilePopWindow(View v){}
    public void showSharePopWindow(View v){}
    public void flashLight(View v){}
    public void turnCamera(View v){}

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
}
