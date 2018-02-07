package com.emao.application.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.emao.application.R;

/**
 *
 * @author keybon
 */

public class BaseLoginActivity extends AppCompatActivity{


    private TextView common_title;
    private ImageView back_bt;
    public Activity baseActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initView();
        initData();
    }
    private void init(){
        baseActivity = this;
        View view = findViewById(R.id.common_title);
        if (view instanceof TextView) {
            //标题栏标题
            common_title = (TextView) view;
        }
        View backView = findViewById(R.id.common_back);
        if(backView instanceof ImageView){
            back_bt = (ImageView) backView;
        }
        if(back_bt != null){
            back_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backKeyPress();
                }
            });
        }
    }

    public void initView(){

    }

    public void initData(){

    }

    public void setCommon_title(String text){
        if(common_title != null){
            common_title.setText(text);
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
}
