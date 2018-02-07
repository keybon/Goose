package com.emao.application.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.ui.callback.OnDialogCallBackListener;
import com.emao.application.ui.callback.OnDialogOkListener;
import com.emao.application.ui.utils.ToastUtils;

/**
 *
 * @author keybon
 */

public class DialogFactory {

    public static Dialog mProgressDialog;

    /**
     * 公用的  正在加载 提示框
     * @param context
     * @return
     */
    public static Dialog createLoadingDialog(Context context){
        Dialog dialog = new Dialog(context, R.style.common_progress_dialog);
//        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static void showProgressDialog(final Activity mActivity){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog == null) {
                    mProgressDialog = createLoadingDialog(mActivity);
                }
                if(!mProgressDialog.isShowing()){
                    mProgressDialog.show();
                }
            }
        });
    }

    public static void hideProgressDialog(Activity mActivity){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        });
    }

    /**
     * 两个按钮   只展示内容  单按钮 参数为null
     * @param mContext
     * @param content
     * @param ok
     * @param cancel
     * @param dialogListener
     * @return
     */
    public static Dialog OKAndCancelDialog(Context mContext, String content, String ok, String cancel, final OnDialogCallBackListener dialogListener) {
        final Dialog dialog = new Dialog(mContext,R.style.bottomdialog);
        dialog.setContentView(R.layout.dialog_common_type);
        dialog.getWindow().setBackgroundDrawable(new BitmapDrawable());
        dialog.setCanceledOnTouchOutside(false);
        TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        TextView ll_sure_cancel = (TextView) dialog.findViewById(R.id.ll_sure_cancel);
        TextView ll_sure_line = (TextView) dialog.findViewById(R.id.ll_sure_line);
        TextView ll_sure_ok = (TextView) dialog.findViewById(R.id.ll_sure_ok);


        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int mWidth = wm.getDefaultDisplay().getWidth();
        tv_content.setWidth((int) (mWidth * 0.8));
        tv_content.setGravity(Gravity.CENTER);

        tv_content.setText(content);
        ll_sure_ok.setText(ok);
        if (ok != null) {
            if (ok.length() > 4) {
                ll_sure_ok.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
            }
            ll_sure_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialogListener != null) {
                        dialogListener.OnOkClick();
                    }
                    dialog.dismiss();
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
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
        return dialog;
    }

    public static Dialog OKButCancelDialog(Context mContext, String content, String ok, String cancel, final OnDialogCallBackListener dialogListener) {
        final Dialog dialog = new Dialog(mContext,R.style.bottomdialog);
        dialog.setContentView(R.layout.dialog_common_type);
        dialog.getWindow().setBackgroundDrawable(new BitmapDrawable());
        dialog.setCanceledOnTouchOutside(false);
        TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        TextView ll_sure_cancel = (TextView) dialog.findViewById(R.id.ll_sure_cancel);
        TextView ll_sure_line = (TextView) dialog.findViewById(R.id.ll_sure_line);
        TextView ll_sure_ok = (TextView) dialog.findViewById(R.id.ll_sure_ok);


        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int mWidth = wm.getDefaultDisplay().getWidth();
        tv_content.setWidth((int) (mWidth * 0.8));
        tv_content.setGravity(Gravity.CENTER);

        tv_content.setText(content);
        ll_sure_ok.setText(ok);
        if (ok != null) {
            if (ok.length() > 4) {
                ll_sure_ok.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
            }
            ll_sure_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialogListener != null) {
                        dialogListener.OnOkClick();
                    }
                    dialog.dismiss();
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
                    if (dialogListener != null) {
                        dialogListener.OnOkClick();
                    }
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
        return dialog;
    }

    public static Dialog portraitDialog(Context mContext, String ok, String cancel, final OnDialogOkListener dialogListener) {

        final Dialog dialog = new Dialog(mContext,R.style.bottomdialog);
        dialog.setContentView(R.layout.dialog_nickname_type);
        dialog.getWindow().setBackgroundDrawable(new BitmapDrawable());
        dialog.setCanceledOnTouchOutside(false);
        final EditText dialog_edit_nickname = dialog.findViewById(R.id.dialog_edit_nickname);
        TextView ll_sure_cancel = dialog.findViewById(R.id.ll_sure_cancel);
        TextView ll_sure_line = dialog.findViewById(R.id.ll_sure_line);
        TextView ll_sure_ok = dialog.findViewById(R.id.ll_sure_ok);

        ll_sure_ok.setText(ok);
        if (ok != null) {
            if (ok.length() > 4) {
                ll_sure_ok.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
            }
            ll_sure_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialogListener != null) {
                        dialogListener.OnOkClick(dialog_edit_nickname.getText().toString().trim());
                    }
                    dialog.dismiss();
                }
            });
        }
        if (cancel != null) {
            ll_sure_cancel.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.6f));
            ll_sure_line.setVisibility(View.VISIBLE);
            ll_sure_cancel.setVisibility(View.VISIBLE);
            ll_sure_cancel.setText(cancel);
            ll_sure_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
        return dialog;
    }

    private static String sexStr;

    /**
     * 性别
     */
    public static Dialog sexDialog(Context mContext, String ok, String cancel, final OnDialogOkListener dialogListener) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_sex_type);
        dialog.getWindow().setBackgroundDrawable(new BitmapDrawable());
        dialog.setCanceledOnTouchOutside(false);
        TextView ll_sure_cancel = dialog.findViewById(R.id.ll_sure_cancel);
        TextView ll_sure_line = dialog.findViewById(R.id.ll_sure_line);
        TextView ll_sure_ok = dialog.findViewById(R.id.ll_sure_ok);
        RadioGroup sex_radiogroup = dialog.findViewById(R.id.sex_radiogroup);
        final RadioButton boy = dialog.findViewById(R.id.sex_btboy);
        final RadioButton girl = dialog.findViewById(R.id.sex_btgirl);

        sex_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == boy.getId()){
                    sexStr = boy.getText().toString();
                }
                if(checkedId == girl.getId()){
                    sexStr = girl.getText().toString();
                }
            }
        });

        ll_sure_ok.setText(ok);
        if (ok != null) {
            if (ok.length() > 4) {
                ll_sure_ok.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
            }
            ll_sure_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialogListener != null) {
                        dialogListener.OnOkClick(sexStr);
                    }
                    dialog.dismiss();
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
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
        return dialog;
    }


    /**
     * 性别
     */
    public static Dialog setMobileDialog(Context mContext, String title,String ok, String cancel, final OnDialogOkListener dialogListener) {
        final Dialog dialog = new Dialog(mContext,R.style.bottomdialog);
        dialog.setContentView(R.layout.dialog_mobile_type);
        dialog.getWindow().setBackgroundDrawable(new BitmapDrawable());
        dialog.setCanceledOnTouchOutside(false);
        TextView ll_sure_cancel = dialog.findViewById(R.id.ll_sure_cancel);
        TextView ll_sure_line = dialog.findViewById(R.id.ll_sure_line);
        TextView ll_sure_ok = dialog.findViewById(R.id.ll_sure_ok);
        TextView tv_content = dialog.findViewById(R.id.tv_content);
        final EditText nickname_edt = dialog.findViewById(R.id.nickname_edt);

        tv_content.setText(title);

        ll_sure_ok.setText(ok);
        if (ok != null) {
            if (ok.length() > 4) {
                ll_sure_ok.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
            }
            ll_sure_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialogListener != null) {
                        if(TextUtils.isEmpty(nickname_edt.getText().toString().trim())){
                            ToastUtils.showShortToast("请您输入下昵称吧～");
                        } else {
                            dialogListener.OnOkClick(nickname_edt.getText().toString().trim());
                        }
                    }
                    dialog.dismiss();
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
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
        return dialog;
    }

    private static Dialog loadingDialog;

    public static Dialog createLoadingDialog(Activity activity){

        Dialog loadingDialog = new Dialog(activity,R.style.bottomdialog);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new BitmapDrawable());
        loadingDialog.setCanceledOnTouchOutside(false);
//        loadingDialog.setCancelable(false);
        return loadingDialog;
    }

    public static void showLoading(final Activity mActivity){

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog == null) {
                    loadingDialog = createLoadingDialog(mActivity);
                }
                if (!loadingDialog.isShowing()) {
                    loadingDialog.show();
                }
            }
        });
    }

    public static void hideLoading(Activity mActivity){

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }

}
