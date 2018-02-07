package com.emao.application.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.http.OkHttpManager;
import com.emao.application.ui.callback.OnDialogOkListener;
import com.emao.application.ui.callback.OnPopOkListener;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.emao.application.ui.utils.fresco.FrescoImageUtils;
import com.emao.application.ui.view.DialogFactory;
import com.emao.application.ui.view.ShowPopWinFactor;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Request;

/**
 * @author keybon
 */

public class UserInfoSetting extends BaseActivity implements View.OnClickListener {

    //Uri获取类型判断
    public final int TYPE_TAKE_CAMERA = 1;

    //相机RequestCode
    public final int CODE_TAKE_CAMERA = 1;
    public final int CODE_TAKE_PICTURE = 2;

    private final int REQUEST_CROP_FROM_CAMERA = 0x000011 ;

    public static final int REQUEST_BIND_MOBILE = 0x000012 ;

    private SimpleDraweeView portrait;
    private RelativeLayout nickname_rl, sex_rl, bind_mobile, home_rl, signature;
    private TextView setNickName, setSex, countSign, userid,mobile;
    private EditText pressSign;
    private Uri cameraUri;
    private String portraitPath,mobileNum;

    private String userSex,nickName,imUserid,sinature;

    private ShowPopWinFactor popupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_userinfo_setting);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();

        setTextViewRight(false);
//        setTextRight("保存");
        setRightTextColor(getResources().getColor(R.color.common_white));

        portrait = findViewById(R.id.userinfo_setting_portrait);
        nickname_rl = findViewById(R.id.userinfo_nickname);
        sex_rl = findViewById(R.id.userinfo_sex);
        bind_mobile = findViewById(R.id.userinfo_bind_mobile);
        home_rl = findViewById(R.id.userinfo_home_page);
        signature = findViewById(R.id.userinfo_signature);

        setNickName = findViewById(R.id.userinfo_set_nickname);
        setSex = findViewById(R.id.userinfo_set_sex);
        countSign = findViewById(R.id.userinfo_signature_count);
        pressSign = findViewById(R.id.userinfo_singnature_et);
        userid = findViewById(R.id.userinfo_set_userid);
        mobile = findViewById(R.id.userinfo_bind_mobile_num);

        portrait.setOnClickListener(this);
        nickname_rl.setOnClickListener(this);
        sex_rl.setOnClickListener(this);
        mobile.setOnClickListener(this);
        home_rl.setOnClickListener(this);
        signature.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();

        initUserData();

        fillData();

        pressSign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 50) {
                    countSign.setText(getString(R.string.picture_init_text, s.length()));
                } else {
                    ToastUtils.showShortToast(R.string.text_num_above);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 不支持连续换行5次
                int start = pressSign.getSelectionStart();
                int end = pressSign.getSelectionEnd();
                int lineCount = countStrNum(s.toString(), "\n");
                if (lineCount == 15) {
                    s.delete(start - 1, end);
                }
            }
        });
    }


    /**
     * 初始化 数据
     */
    private void initUserData(){

        portraitPath = SharedPreUtils.get(SharedPreUtils.SP_HEADIMGURL, "");
        mobileNum = SharedPreUtils.get(SharedPreUtils.SP_MOBILE, "");

        if(portraitPath.contains(CommonUtils.getSDCardPath())){
            FrescoImageUtils.getInstance()
                    .createSdcardBuilder(portraitPath)
                    .setClearCahe(true)
                    .build()
                    .showSdcardImage(portrait);
        } else {
            portrait.setImageURI(portraitPath);
        }

        userSex = SharedPreUtils.get(SharedPreUtils.SP_SEX,"");

        nickName = SharedPreUtils.get(SharedPreUtils.SP_NICKNAME, "");

        imUserid = SharedPreUtils.get(SharedPreUtils.SP_USERID, "");

        sinature = SharedPreUtils.get(SharedPreUtils.SP_SIGNATURE,"");
    }

    /**
     * 数据填充
     */
    private void fillData(){

        setSex.setText(userSex.equals("1") ? "男" : "女");
        setNickName.setText(nickName);
        userid.setText(imUserid);
        pressSign.setText(sinature);

        if(TextUtils.isEmpty(mobileNum)){
            mobile.setText("点击设置");
        } else {
            mobile.setText(mobileNum);
        }
        countSign.setText(getString(R.string.picture_init_text, 0));
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
    public void commitPress() {
        super.commitPress();
        SharedPreUtils.put(SharedPreUtils.SP_SIGNATURE,pressSign.getText().toString().trim());
        updateUser("usersignature", pressSign.getText().toString().trim(), new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JSONObject jsonObject = new JSONObject(result);
                if(Constants.ERROR_CODE_200.equals(jsonObject.getString("error"))){
                    setNickName.setText(pressSign.getText().toString().trim());
                    SharedPreUtils.put(SharedPreUtils.SP_SIGNATURE,pressSign.getText().toString().trim());
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.userinfo_setting_portrait:

                if (popupWindow == null) {
                    popupWindow = new ShowPopWinFactor(this);
                }
                popupWindow.showSelectorPortrait(this);
                popupWindow.showAtLocation(findViewById(R.id.userinfo_activity), Gravity.BOTTOM, 0, 0);

                break;
            case R.id.userinfo_nickname:

                if(popupWindow == null ){
                    popupWindow = new ShowPopWinFactor(this);
                }

                popupWindow.showUserSettingPop("请输入您的昵称", CommonUtils.getResourceString(R.string.dialog_init_cancel),
                        CommonUtils.getResourceString(R.string.dialog_nickname_right), new OnPopOkListener() {
                            @Override
                            public void okCallBack(final String str) {

                                updateUser("truename", str, new OkHttpManager.DataCallBack() {
                                    @Override
                                    public void requestSuccess(String result) throws Exception {
                                        JSONObject jsonObject = new JSONObject(result);
                                        if(Constants.ERROR_CODE_200.equals(jsonObject.getString("error"))){
                                            setNickName.setText(str);
                                            SharedPreUtils.put(SharedPreUtils.SP_NICKNAME,str);
                                        } else {
                                            ToastUtils.showShortToast(jsonObject.getString("msg"));
                                        }
                                        dimissPop();
                                    }

                                    @Override
                                    public void requestFailure(Request request, IOException e) {
                                        dimissPop();
                                    }
                                });

                            }

                            @Override
                            public void cancelCallBack(String str) {
                                dimissPop();
                            }
                        });
                popupWindow.showAtLocation(findViewById(R.id.userinfo_activity), Gravity.CENTER, 0, 0);


                break;
            case R.id.userinfo_sex:

                DialogFactory.sexDialog(this, CommonUtils.getResourceString(R.string.dialog_nickname_right),
                        CommonUtils.getResourceString(R.string.dialog_init_cancel), new OnDialogOkListener() {
                            @Override
                            public void OnOkClick(final String str) {

                                updateUser("sex", str.equals("男") ? "1" : "2", new OkHttpManager.DataCallBack() {
                                    @Override
                                    public void requestSuccess(String result) throws Exception {
                                        JSONObject jsonObject = new JSONObject(result);
                                        if(Constants.ERROR_CODE_200.equals(jsonObject.getString("error"))){
                                            setSex.setText(str);
                                            SharedPreUtils.put(SharedPreUtils.SP_SEX,str);
                                        }
                                    }

                                    @Override
                                    public void requestFailure(Request request, IOException e) {

                                    }
                                });

                            }
                        });
                break;
            case R.id.userinfo_bind_mobile_num:

                if(popupWindow == null ){
                    popupWindow = new ShowPopWinFactor(this);
                }

                popupWindow.showUserSettingPop("请输入您的手机号", CommonUtils.getResourceString(R.string.dialog_init_cancel),
                        CommonUtils.getResourceString(R.string.dialog_nickname_right), new OnPopOkListener() {
                            @Override
                            public void okCallBack(final String str) {

                                updateUser("mobile", str, new OkHttpManager.DataCallBack() {
                                    @Override
                                    public void requestSuccess(String result) throws Exception {
                                        JSONObject jsonObject = new JSONObject(result);
                                        if(Constants.ERROR_CODE_200.equals(jsonObject.getString("error"))){
                                            mobile.setText(str);
                                            SharedPreUtils.put(SharedPreUtils.SP_MOBILE,str);
                                        }
                                        dimissPop();
                                    }

                                    @Override
                                    public void requestFailure(Request request, IOException e) {
                                        dimissPop();
                                    }
                                });

                            }

                            @Override
                            public void cancelCallBack(String str) {
                                dimissPop();
                            }
                        });
                popupWindow.showAtLocation(findViewById(R.id.userinfo_activity), Gravity.CENTER, 0, 0);

                break;
            case R.id.userinfo_home_page:
                intent = new Intent();
                intent.setClass(this, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.USERINFO_USERID, (String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
                intent.putExtra(UserInfoActivity.USERINFO_USERNAME, (String) SharedPreUtils.get(SharedPreUtils.SP_NICKNAME,""));
                startActivity(intent);
                break;
            case R.id.userinfo_signature:

                break;

            case R.id.pop_portrait_camera:

                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraUri = getMediaFileUri(TYPE_TAKE_CAMERA);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                    startActivityForResult(intent, CODE_TAKE_CAMERA);
                    dimissPop();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CODE_TAKE_CAMERA);
                }


                break;
            case R.id.pop_portrait_photo:
                intent = new Intent();
                cameraUri = getMediaFileUri(TYPE_TAKE_CAMERA);
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                startActivityForResult(intent, CODE_TAKE_PICTURE);
                dimissPop();
                break;
            case R.id.pop_portrait_cancel:
                dimissPop();
                break;
            default:
                break;
        }
    }

    /**
     * 一改 一 存
     */
    public void updateUser(String params, String value, OkHttpManager.DataCallBack dataCallBack){

        HashMap<String,String> map = new HashMap<>();
        map.put("id",imUserid);
        map.put("openid", String.valueOf(SharedPreUtils.get(SharedPreUtils.SP_OPENID,"")));
        map.put(params,value);
        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_EDIT_DATA, map, dataCallBack);
    }


    /**
     * EditText获取焦点并显示软键盘
     */
    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int results : grantResults){
            if(requestCode == CODE_TAKE_CAMERA && results != PackageManager.PERMISSION_GRANTED) {
                ToastUtils.showShortToast("请开启相机权限");
            }
        }
        if(requestCode == CODE_TAKE_CAMERA){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraUri = getMediaFileUri(TYPE_TAKE_CAMERA);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            startActivityForResult(intent, CODE_TAKE_CAMERA);
            dimissPop();
        }
    }

    public Uri getMediaFileUri(int type) {
        File mediaStorageDir = new File(CommonUtils.getSDCardPath() + "/portrait");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        //创建Media File
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == TYPE_TAKE_CAMERA) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return Uri.fromFile(mediaFile);
    }


    public void dimissPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void backKeyPress() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {

            commitPress();
            finish();
        }
    }

    private void DIYCropImage(Uri uri, int requestCode) {
        Intent i = new Intent(this, CutImageActivity.class);
        i.setData(uri);
        i.putExtra("uri", uri.getPath());
        this.startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_TAKE_CAMERA:
            case CODE_TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            DIYCropImage(uri, REQUEST_CROP_FROM_CAMERA);
                        } else {
                            DIYCropImage(cameraUri, REQUEST_CROP_FROM_CAMERA);
                        }
                    } else {
                        DIYCropImage(cameraUri, REQUEST_CROP_FROM_CAMERA);
                    }
                }
                break;
            case REQUEST_CROP_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    portraitPath = CutImageActivity.IMAGE_CROP_THUMB_PATH;

                    HashMap<String,String> params = new HashMap<>();
                    params.put("openid",(String) SharedPreUtils.get(SharedPreUtils.SP_OPENID,""));
                    params.put("id", imUserid);
                    params.put("headimgurl", portraitPath);

                    OkHttpManager.getInstance().editDataAsync(Constants.GOOSE_URL_CONTENT_EDIT_DATA,new File(portraitPath), params, new OkHttpManager.DataCallBack() {
                        @Override
                        public void requestSuccess(String result) throws Exception {
                            LogUtils.e("资料设置 " + result);
                            JSONObject jsonObject = new JSONObject(result);
                            if(Constants.ERROR_CODE_200.equals(jsonObject.getString("error"))){


                            }
                            SharedPreUtils.put(SharedPreUtils.SP_HEADIMGURL,portraitPath);

                            FrescoImageUtils.getInstance()
                                    .createSdcardBuilder(CutImageActivity.IMAGE_CROP_THUMB_PATH)
                                    .setClearCahe(true)
                                    .build()
                                    .showSdcardImage(portrait);
                        }

                        @Override
                        public void requestFailure(Request request, IOException e) {
                            ToastUtils.showShortToast("资料设置失败了~");
                        }
                    });


                }

                break;
            case REQUEST_BIND_MOBILE:
                if (resultCode == REQUEST_BIND_MOBILE) {
                    if(data != null){
                        mobileNum = data.getStringExtra("mobile");
                        mobile.setText(mobileNum);
                        SharedPreUtils.put(SharedPreUtils.SP_MOBILE,mobileNum);
                    }
                }
                break;
            default:
                break;
        }
    }
}
