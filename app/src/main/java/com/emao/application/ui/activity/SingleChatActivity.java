package com.emao.application.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.http.OkHttpManager;
import com.emao.application.ui.adapter.SingleChatAdapter;
import com.emao.application.ui.bean.SingleChatBean;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 *
 * @author keybon
 */

public class SingleChatActivity extends BaseActivity implements View.OnClickListener{

    public static final String SINGLE_TID = "SINGLE_TID";
    public static final String SINGLE_USERNAME = "SINGLE_USERNAME";
    public static final String SINGLE_HEADURL = "SINGLE_HEADURL";
    public static final String SINGLE_FID = "SINGLE_FID";
    public static final String SINGLE_FROM_FLAG = "SINGLE_FROM_FLAG";

    private RecyclerView recyclerView;
    private SingleChatAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<SingleChatBean> list = new ArrayList<>();

    private InputMethodManager mInputMethodManager;

    private EditText pressEd;
    private TextView send;
    private Intent intent ;
    // 会话 id
    private String TID;
    // 接收方 id
    private String fid;
    // 自己的 id
    private String uid;

    private Handler mHander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                downLoadData();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_single_chat);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();

        recyclerView = findViewById(R.id.single_chat_recyclerview);
        send = findViewById(R.id.single_chat_bottom_send);
        pressEd = findViewById(R.id.single_chat_bottom_edit);

        send.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        intent = getIntent();
        setActionBarTitle("");

        TID = intent.getStringExtra(SINGLE_TID);
        fid = intent.getStringExtra(SINGLE_FID);

        uid = SharedPreUtils.get(SharedPreUtils.SP_USERID,"");

        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        pressEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() > 50){
                    ToastUtils.showShortToast(R.string.text_num_above);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){
                    send.setClickable(true);
                } else {
                    send.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 不支持连续换行5次
                int start = pressEd.getSelectionStart();
                int end = pressEd.getSelectionEnd();
                int lineCount = countStrNum(s.toString(), "\n");
                if (lineCount == 15) {
                    s.delete(start - 1, end);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        downLoadData();
    }

    private void initAdapter(){

        if(mAdapter == null){
            mAdapter = new SingleChatAdapter(this,list,uid);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setList(list);
            mAdapter.notifyDataSetChanged();
        }
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

    }

    private void downLoadData(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", TID);
        params.put("uid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
//        params.put("time", String.valueOf(System.currentTimeMillis()/1000));
        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_CONTENT_SINGLE_CHAT_SELECT, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("单聊页面 "+result);
                list = GsonUtils.getSingheChatParams(result);
                pressEd.setText("");
                initAdapter();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                ToastUtils.showShortToast("失败");
                LogUtils.e("单聊页面 "+request.toString()+"  e =  "+e.toString());
            }
        });
    }

    /**
     * 指定字符包含个数
     *
     * @param str1
     * @param str2
     * @return
     */
    private int countStrNum(String str1, String str2) {
        int countStr = 0;
        if (str1.indexOf(str2) == -1) {
            return 0;
        } else {
            while (str1.indexOf(str2) != -1) {
                countStr++;
                str1 = str1.substring(str1.indexOf(str2) + str2.length(), str1.length());
            }
        }
        return countStr;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.single_chat_bottom_send:
                sendText(pressEd.getText().toString().trim());
                if (mInputMethodManager.isActive()) {
                    mInputMethodManager.hideSoftInputFromWindow(pressEd.getWindowToken(), 0);
                }
                break;
            default:
                break;
        }
    }

    private void sendText(final String content){
        // fid 发送方  tid 会话列表id
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid",uid );
        params.put("tid", TID);
        params.put("content", content);


        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_SINGLE_CHAT_ADD, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                HashMap<String,String> map = GsonUtils.getMapParams(result);
                if(Constants.ERROR_CODE_200.equals(map.get("error"))){
                    if(mAdapter != null){
                        pressEd.setText("");

                        mHander.sendEmptyMessageDelayed(1,2000);
                    }
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                ToastUtils.showShortToast("发送失败");
            }
        });
    }
}
