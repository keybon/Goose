package com.emao.application.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.widget.EditText;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.http.OkHttpManager;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;

/**
 *
 * @author keybon
 */

public class OpinionActivity extends BaseActivity {

    public static final String OPINION_FROM = "OPINION_FROM";
    public static final String OPINION_JID = "OPINION_JID";

    private EditText editText;
    private TextView textView;
    private Intent intent;
    private String jid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_opinion);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();

        editText = findViewById(R.id.opinion_et);
        textView = findViewById(R.id.opinion_text);
        setBackBtnRight(false);
        setTextViewRight(true);
        setRightClickEnable(false);
    }

    @Override
    public void initData() {
        super.initData();

        intent = getIntent();
        if("report".equals(intent.getStringExtra(OPINION_FROM))){
            setActionBarTitle(CommonUtils.getResourceString(R.string.userinfo_bottom_report));
        } else {
            setActionBarTitle(CommonUtils.getResourceString(R.string.setting_opinion));
        }
        jid = intent.getStringExtra(OPINION_JID);

        textView.setText(getString(R.string.opinion_init_text,0));

        // 新建一个可以添加属性的文本对象
        SpannableString ss = new SpannableString(CommonUtils.getResourceString(R.string.opinion_hint));
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14,true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        editText.setHint(new SpannedString(ss));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() <= 150){
                    textView.setText(getString(R.string.opinion_init_text,s.length()));
                } else {
                    ToastUtils.showShortToast(R.string.text_num_above);
                }
                if(s.length() > 0){
                    setRightTextColor(getResources().getColor(R.color.common_white));
                    setRightClickEnable(true);
                } else {
                    setRightTextColor(getResources().getColor(R.color.actionbar_text_right));
                    setRightClickEnable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void commitPress() {

//        if("report".equals(intent.getStringExtra(OPINION_FROM))){
//            reportCommit();
//        } else {
            feedCommit();
//        }
    }

    /**
     * 反馈
     */
    public void feedCommit(){

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("uid",(String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        params.put("text",editText.getText().toString().trim());
        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_FEED_BACK, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("反馈  "+result);
                HashMap<String,String> params = GsonUtils.getMapParams(result);
                if(Constants.ERROR_CODE_200.equals(params.get("error"))){
                    ToastUtils.showShortToast("反馈成功");
                    finish();
                } else {
                    ToastUtils.showShortToast("反馈失败");
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                ToastUtils.showShortToast("反馈失败 "+request.toString());
            }
        });
    }

    /**
     * 举报
     */
    public void reportCommit(){

        HashMap<String,String> params = new HashMap<>();
        params.put("uid",(String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        params.put("jid",jid);
        params.put("text",editText.getText().toString().trim());
        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_REPORT, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("举报"+result);
                HashMap<String,String> params = GsonUtils.getMapParams(result);
                if(Constants.ERROR_CODE_200.equals(params.get("error"))){
                    ToastUtils.showShortToast("举报成功");
                    finish();
                } else {
                    ToastUtils.showShortToast("举报失败");
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("举报失败");
            }
        });

    }
}
