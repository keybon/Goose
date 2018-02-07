package com.emao.application.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.emao.application.R;
import com.emao.application.share.WeChatShare;
import com.emao.application.ui.callback.OnDialogCallBackListener;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.emao.application.ui.view.DialogFactory;

import java.io.File;

/**
 * @author keybon
 */

public class LogOutActivity extends BaseLoginActivity implements View.OnClickListener {

    private RelativeLayout setting_clear;
    private RelativeLayout setting_opinion;
    private RelativeLayout setting_about;
    private Button log_out;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_log_out);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();
        setting_clear = findViewById(R.id.setting_clear);
        setting_opinion = findViewById(R.id.setting_opinion);
        setting_about = findViewById(R.id.setting_about);
        log_out = findViewById(R.id.log_out);

        setting_clear.setOnClickListener(this);
        setting_opinion.setOnClickListener(this);
        setting_about.setOnClickListener(this);
        log_out.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_clear:
                DialogFactory.OKAndCancelDialog(this, CommonUtils.getResourceString(R.string.logout_clear),
                        CommonUtils.getResourceString(R.string.dialog_right_sure),
                        CommonUtils.getResourceString(R.string.dialog_init_cancel),
                        new OnDialogCallBackListener() {
                            @Override
                            public void OnOkClick() {

                                File file = new File(CommonUtils.getSDCardPath());

                                if(file.exists()){
                                    file.delete();
                                }

                            }
                        });
                break;
            case R.id.setting_opinion:
                startActivity(new Intent(this,OpinionActivity.class));
                break;
            case R.id.setting_about:
                ToastUtils.showLongToast(CommonUtils.getResourceString(R.string.emao_info));
                break;
            case R.id.log_out:

                DialogFactory.OKAndCancelDialog(this, CommonUtils.getResourceString(R.string.logout_hint),
                        CommonUtils.getResourceString(R.string.dialog_right_sure),
                        CommonUtils.getResourceString(R.string.dialog_init_cancel),
                        new OnDialogCallBackListener() {
                            @Override
                            public void OnOkClick() {
                                if(WeChatShare.wechatApi != null){
                                    WeChatShare.wechatApi.unregisterApp();
                                }
                                SharedPreUtils.put(SharedPreUtils.SP_OPENID,"");
                                SharedPreUtils.put(SharedPreUtils.SP_COLLECTION, "");
                                SharedPreUtils.put(SharedPreUtils.SP_SIGNATURE, "");
                                SharedPreUtils.put(SharedPreUtils.SP_ACCESS_TOKEN, "");
                                startActivity(new Intent(LogOutActivity.this,LoginActivity.class));
                                finish();
                            }
                        });
                break;
            default:
                break;
        }
    }
}
