package com.emao.application.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.ui.adapter.ReportAdapter;
import com.emao.application.ui.adapter.TemplateItemPopAdapter;
import com.emao.application.ui.bean.ReportBean;
import com.emao.application.ui.callback.OnPayCallBackListener;
import com.emao.application.ui.callback.OnPopOkListener;
import com.emao.application.ui.callback.OnRecyclerViewItemClickListener;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author keybon
 */

public class ShowPopWinFactor extends PopupWindow {

    private Context mContext;
    private View view;

    public ShowPopWinFactor(Context context) {
        this.mContext = context;
    }

    /**
     * 全屏 从底部弹起
     */
    public void showBottomPop(View.OnClickListener listener) {
        view = LayoutInflater.from(mContext).inflate(R.layout.menu_compile_pop, null);
        // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
        // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点

        LinearLayout pictureBtn = view.findViewById(R.id.menu_compile_picture);
        LinearLayout videoBtn = view.findViewById(R.id.menu_compile_video);
        ImageView closeBtn = view.findViewById(R.id.menu_compile_close);
        pictureBtn.setOnClickListener(listener);
        videoBtn.setOnClickListener(listener);
        closeBtn.setOnClickListener(listener);
        setContentView(this.view);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        // 设置PopupWindow是否能响应外部点击事件
        setOutsideTouchable(true);
        setAnimationStyle(R.style.main_pop_anim);
        showAsDropDown(view, Gravity.BOTTOM, 0, 0);
    }


    /**
     * 选择模版
     */
    public void showTemplatePop(final int activity_flag, View.OnClickListener onClickListener, AdapterView.OnItemClickListener listener) {

        List<Drawable> drawables = new ArrayList<Drawable>();
        drawables.add(mContext.getResources().getDrawable(R.drawable.template_2));
//        drawables.add(mContext.getResources().getDrawable(R.drawable.copylink_small));
//        drawables.add(mContext.getResources().getDrawable(R.drawable.copylink_small));

        view = LayoutInflater.from(mContext).inflate(R.layout.menu_template_pop, null);
        LinearLayout actionbar_back_left = view.findViewById(R.id.actionbar_back_left);
        TextView actionbar_title = view.findViewById(R.id.actionbar_title);
        GridView template_pop_gridview = view.findViewById(R.id.template_pop_gridview);
        TemplateItemPopAdapter adapter = new TemplateItemPopAdapter(mContext, drawables);
        template_pop_gridview.setAdapter(adapter);
        actionbar_title.setText(CommonUtils.getResourceString(R.string.template_pop_title));
        actionbar_back_left.setOnClickListener(onClickListener);

        template_pop_gridview.setOnItemClickListener(listener);

        setContentView(this.view);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        setFocusable(true);
        // 设置PopupWindow是否能响应外部点击事件
        setOutsideTouchable(true);
        setAnimationStyle(R.style.main_pop_anim);
    }

    /**
     * 选择头像方式
     */
    public void showSelectorPortrait(View.OnClickListener onClickListener) {

        view = LayoutInflater.from(mContext).inflate(R.layout.pop_portrait, null);

        TextView camera = view.findViewById(R.id.pop_portrait_camera);
        TextView photo = view.findViewById(R.id.pop_portrait_photo);
        TextView cancel = view.findViewById(R.id.pop_portrait_cancel);

        camera.setOnClickListener(onClickListener);
        photo.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);

        setContentView(this.view);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        setFocusable(true);
        // 设置PopupWindow是否能响应外部点击事件
        setOutsideTouchable(false);
        setAnimationStyle(R.style.main_pop_anim);
//        showAsDropDown(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 打赏
     */
    private String money = "";

    public void showAdmirePop(Context mContext,String portrait, String nickName, final OnPayCallBackListener onPayCallBackListener) {

        view = LayoutInflater.from(mContext).inflate(R.layout.pop_admire, null);

        final InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        SimpleDraweeView pop_admire_portrait = view.findViewById(R.id.pop_admire_portrait);
        TextView pop_admrie_nickname = view.findViewById(R.id.pop_admrie_nickname);
        Button pop_admire_btn = view.findViewById(R.id.pop_admire_btn);
        final LinearLayout pop_admire_ll = view.findViewById(R.id.pop_admire_ll);
        final EditText pressMoney = view.findViewById(R.id.pop_admire_press_money);
        ImageView pop_admire_close = view.findViewById(R.id.pop_admire_close);
        final RadioGroup radioGroup1 = view.findViewById(R.id.pop_money_r1);
        final RadioGroup radioGroup2 = view.findViewById(R.id.pop_money_r2);
        final RadioButton bt1 = view.findViewById(R.id.bt1);
        final RadioButton bt2 = view.findViewById(R.id.bt2);
        final RadioButton bt3 = view.findViewById(R.id.bt3);
        final RadioButton bt4 = view.findViewById(R.id.bt4);
        final RadioButton bt5 = view.findViewById(R.id.bt5);
        final RadioButton bt6 = view.findViewById(R.id.bt6);

        pop_admire_portrait.setImageURI(portrait);
        pop_admrie_nickname.setText(nickName);

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId > 0){
                    radioGroup2.clearCheck();
                    radioGroup1.check(checkedId);
                    //设置输入框不可聚焦，即失去焦点和光标
                    pressMoney.setFocusable(false);
                    if (mInputMethodManager.isActive()) {
                        // 隐藏输入法
                        mInputMethodManager.hideSoftInputFromWindow(pressMoney.getWindowToken(), 0);
                    }
                }
                if (checkedId == bt1.getId()) {
                    money = bt1.getText().toString();
                } else if (checkedId == bt2.getId()) {
                    money = bt2.getText().toString();
                } else if (checkedId == bt3.getId()) {
                    money = bt3.getText().toString();
                }
                pressMoney.setText("");
            }
        });
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId > 0){
                    radioGroup1.clearCheck();
                    radioGroup2.check(checkedId);
                    //设置输入框不可聚焦，即失去焦点和光标
                    pressMoney.setFocusable(false);
                }
                if (checkedId == bt4.getId()) {
                    money = bt4.getText().toString();
                } else if (checkedId == bt5.getId()) {
                    money = bt5.getText().toString();
                } else if (checkedId == bt6.getId()) {
                    money = bt6.getText().toString();
                }
                pressMoney.setText("");
            }
        });
        pressMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressMoney.setFocusable(true);
                //设置触摸聚焦
                pressMoney.setFocusableInTouchMode(true);
                //请求焦点
                pressMoney.requestFocus();
                //获取焦点
                pressMoney.findFocus();
            }
        });
        pressMoney.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    radioGroup1.clearCheck();
                    radioGroup2.clearCheck();
                    money = "";
                    // 显示输入法
                    mInputMethodManager.showSoftInput(pressMoney, InputMethodManager.SHOW_FORCED);
                } else {
                    if (mInputMethodManager.isActive()) {
                        // 隐藏输入法
                        mInputMethodManager.hideSoftInputFromWindow(pressMoney.getWindowToken(), 0);
                    }
                }
            }
        });
        pop_admire_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPayCallBackListener.onClickClose(v,"");
            }
        });

        pop_admire_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(pressMoney.getText().toString().trim()) && TextUtils.isEmpty(money)) {
                    ToastUtils.showShortToast(CommonUtils.getResourceString(R.string.pop_admire_money_hint));
                } else if(!TextUtils.isEmpty(pressMoney.getText().toString().trim())){
                    money = pressMoney.getText().toString().trim();
                    int count = Integer.parseInt(money);
//                    if(count < 2){
//                        ToastUtils.showShortToast("赞赏金额必须大于2元哦～");
//                    } else {
                        onPayCallBackListener.onClickPay(v,money);
//                    }
                } else {
                    money = money.replace("元","");
                    int count = Integer.parseInt(money);
                    if(count < 2){
                        ToastUtils.showShortToast("赞赏金额必须大于2元哦～");
                    } else {
                        onPayCallBackListener.onClickPay(v,money);
                    }
                }
            }
        });

        setContentView(this.view);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //防止虚拟软键盘被弹出菜单遮住
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //设置可以获取焦点，否则弹出菜单中的EditText是无法获取输入的
        setFocusable(true);
//        setFocusable(true);
        // 设置PopupWindow是否能响应外部点击事件
//        setOutsideTouchable(true);
        setAnimationStyle(R.style.main_pop_anim);
    }

    /**
     * 分享
     */
    public void showShareMenu(View.OnClickListener onClickListener) {

        view = LayoutInflater.from(mContext).inflate(R.layout.pop_share_menu, null);

        LinearLayout pop_share_qq = view.findViewById(R.id.pop_share_qq);
        LinearLayout pop_share_zone = view.findViewById(R.id.pop_share_zone);
        LinearLayout pop_share_wechat = view.findViewById(R.id.pop_share_wechat);
        LinearLayout pop_share_wechat_friends = view.findViewById(R.id.pop_share_wechat_friends);
        LinearLayout pop_share_weibo = view.findViewById(R.id.pop_share_weibo);
        LinearLayout pop_share_copy = view.findViewById(R.id.pop_share_copy);
        Button pop_share_cancel = view.findViewById(R.id.pop_share_cancel);

        pop_share_qq.setOnClickListener(onClickListener);
        pop_share_zone.setOnClickListener(onClickListener);
        pop_share_wechat.setOnClickListener(onClickListener);
        pop_share_wechat_friends.setOnClickListener(onClickListener);
        pop_share_weibo.setOnClickListener(onClickListener);
        pop_share_copy.setOnClickListener(onClickListener);
        pop_share_cancel.setOnClickListener(onClickListener);

        setContentView(this.view);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        setFocusable(true);
        // 设置PopupWindow是否能响应外部点击事件
        setOutsideTouchable(true);
        setAnimationStyle(R.style.main_pop_anim);
//        showAsDropDown(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 选择分类
     */
    private int checkid = 0;

    public void showSelectorClassify(final OnPayCallBackListener onPayCallBackListener) {

        view = LayoutInflater.from(mContext).inflate(R.layout.pop_classify_type, null);

        RadioGroup classify_radiogroup = view.findViewById(R.id.classify_radiogroup);
        final RadioButton dialog_classify_1 = view.findViewById(R.id.dialog_classify_1);
        final RadioButton dialog_classify_2 = view.findViewById(R.id.dialog_classify_2);
        final RadioButton dialog_classify_3 = view.findViewById(R.id.dialog_classify_3);
        final RadioButton dialog_classify_4 = view.findViewById(R.id.dialog_classify_4);
        final RadioButton dialog_classify_5 = view.findViewById(R.id.dialog_classify_5);
        TextView ll_sure_cancel = view.findViewById(R.id.ll_sure_cancel);
        TextView ll_sure_ok = view.findViewById(R.id.ll_sure_ok);


        classify_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == dialog_classify_1.getId()) {
                    checkid = 1;
                } else if (checkedId == dialog_classify_2.getId()){
                    checkid = 2;
                } else if (checkedId == dialog_classify_3.getId()){
                    checkid = 3;
                } else if (checkedId == dialog_classify_4.getId()){
                    checkid = 4;
                } else if (checkedId == dialog_classify_5.getId()){
                    checkid = 5;
                }
            }
        });
        ll_sure_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPayCallBackListener.onClickClose(v, "");
            }
        });
        ll_sure_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPayCallBackListener.onClickPay(v, String.valueOf(checkid));
            }
        });


        setContentView(this.view);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        setFocusable(true);
        // 设置PopupWindow是否能响应外部点击事件
        setOutsideTouchable(true);
        setAnimationStyle(R.style.main_pop_anim);
//        showAsDropDown(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 设置收款方式
     * @param onPayCallBackListener
     */
    private int collectionCheckid = 0;

    public void showSelectorCollection(final OnPayCallBackListener onPayCallBackListener) {

        view = LayoutInflater.from(mContext).inflate(R.layout.pop_collection_type, null);
        RadioGroup classify_radiogroup = view.findViewById(R.id.collection_radiogroup);
        final RadioButton alipay = view.findViewById(R.id.collection_alipay);
        final RadioButton wechat = view.findViewById(R.id.collection_wechat);

        TextView ll_sure_cancel = view.findViewById(R.id.ll_sure_cancel);
        TextView ll_sure_ok = view.findViewById(R.id.ll_sure_ok);


        classify_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == alipay.getId()) {
                    collectionCheckid = 2;
                } else if (checkedId == wechat.getId()){
                    collectionCheckid = 1;
                }
            }
        });
        ll_sure_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPayCallBackListener.onClickClose(v, String.valueOf(collectionCheckid));
            }
        });
        ll_sure_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPayCallBackListener.onClickPay(v, String.valueOf(collectionCheckid));
            }
        });

        if(!Constants.COLLECTION_ALI_TYPE.equals(SharedPreUtils.get(SharedPreUtils.SP_COLLECTION,""))){
            wechat.setChecked(true);
            collectionCheckid = 1;
        } else {
            alipay.setChecked(true);
            collectionCheckid = 2;
        }


        setContentView(this.view);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);
        setAnimationStyle(R.style.main_pop_anim);
    }


    /**
     *  举报
     */
    public void showReportPopWindow(List<ReportBean> contentList, final OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {

        view = LayoutInflater.from(mContext).inflate(R.layout.pop_report, null);

        LinearLayout report_close = view.findViewById(R.id.report_close);
        RecyclerView  report_recycler = view.findViewById(R.id.report_recycler);
        TextView ll_sure_ok = view.findViewById(R.id.ll_sure_ok);

        ll_sure_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onRecyclerViewItemClickListener != null){
                    onRecyclerViewItemClickListener.onPhotoClick(v,0);
                }
            }
        });
        report_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onRecyclerViewItemClickListener != null){
                    onRecyclerViewItemClickListener.onItemLongClick(v,0);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        report_recycler.setLayoutManager(layoutManager);


        ReportAdapter adapter = new ReportAdapter(mContext,contentList);
        report_recycler.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(onRecyclerViewItemClickListener);


        setContentView(this.view);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);
        setAnimationStyle(R.style.main_pop_anim);
    }



    /**
     * 全屏 从底部弹起
     */
    public void showUserSettingPop(String title, String cancel, String ok, final OnPopOkListener onPopOkListener) {

        view = LayoutInflater.from(mContext).inflate(R.layout.dialog_mobile_type, null);

        TextView ll_sure_cancel = view.findViewById(R.id.ll_sure_cancel);
        TextView ll_sure_line = view.findViewById(R.id.ll_sure_line);
        TextView ll_sure_ok = view.findViewById(R.id.ll_sure_ok);
        TextView tv_content = view.findViewById(R.id.tv_content);
        final EditText nickname_edt = view.findViewById(R.id.nickname_edt);

        tv_content.setText(title);

        if (ok != null) {
            ll_sure_ok.setText(ok);
            ll_sure_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPopOkListener != null) {
                        if(TextUtils.isEmpty(nickname_edt.getText().toString().trim())){
                            ToastUtils.showShortToast("请输入~");
                        } else {
                            onPopOkListener.okCallBack(nickname_edt.getText().toString().trim());
                        }
                    }
                }
            });
        }
        if (cancel != null) {
            ll_sure_line.setVisibility(View.VISIBLE);
            ll_sure_cancel.setVisibility(View.VISIBLE);
            ll_sure_cancel.setText(cancel);
            ll_sure_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onPopOkListener != null){
                        onPopOkListener.cancelCallBack("");
                    }
                }
            });
        }

        setContentView(this.view);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        // 设置PopupWindow是否能响应外部点击事件
        setOutsideTouchable(true);
    }


}
