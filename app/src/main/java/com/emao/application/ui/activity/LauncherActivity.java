package com.emao.application.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.ui.callback.CountDownCallBack;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.emao.application.ui.view.MyCount;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

/**
 * @author keybon
 */

public class LauncherActivity extends BaseLoginActivity implements CountDownCallBack{

    private static final int REQUEST_CODE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_PHONE_STATE = 2;
    private static final int REQUEST_CODE_LOCATION = 3;
    private static final int REQUEST_CODE_CAMERA = 4;
    private static final int REQUEST_CODE_RECORD_AUDIO = 5;

    private TextView time_tv;
    private MyCount myCount;
    private SimpleDraweeView launcher_img;
    private TextView launcher_hint_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_launcher);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView(){
        time_tv = findViewById(R.id.launcher_time_tv);
        launcher_img = findViewById(R.id.launcher_img);
        launcher_hint_tv = findViewById(R.id.launcher_hint_tv);
        time_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCount.cancel();
                jumpMain();
            }
        });
        time_tv.setVisibility(View.GONE);

        downLoadData();

        initDownLoadData();

    }

    private void initDownLoadData(){
        initFile();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            myCount = new MyCount(1*1000 + 50,1000,this);
            myCount.start();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_EXTERNAL_STORAGE);
        }
    }

    private void initFile(){

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FILE_NAME_PATH);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                ToastUtils.showShortToast("文件夹崩溃了");
            }
        }
    }

    private void downLoadData(){
//        OkHttpManager.getInstance().postAsync(Constants.GOOSE_URL_LAUNCHER_BG,null, new OkHttpManager.DataCallBack() {
//            @Override
//            public void requestFailure(Request request, IOException e) {
//                LogUtils.e("启动图失败了   request = "+request.toString());
//            }
//
//            @Override
//            public void requestSuccess(String result) throws Exception {
//                LogUtils.e("启动图  result = "+result);
//                HashMap<String, String> map = GsonUtils.getMapParams(result);
//                String picture = map.get("picture");
//                launcher_img.setImageURI(Constants.GOOSE_IMAGE_URL+picture);
//                launcher_hint_tv.setText(map.get("title"));
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(int grantResult : grantResults) {
            if(grantResult != PackageManager.PERMISSION_GRANTED) {
                showPermissionTipDialog(requestCode);
                return;
            }
        }
        myCount = new MyCount(1*1000 + 1050,1000,this);
        myCount.start();
    }

    private void checkPermissions() {
        if(!requestExternalStorage()) {
            return;
        }
//        if(!requestPhoneState()) {
//            return;
//        }
//        if(locationFlag && !requestLocation()) {
//            locationFlag = false;
//            return;
//        }
//        if(!requestCamera()) {
//            return;
//        }
//        if(!requestRecordAudio()) {
//            return;
//        }

    }

    private boolean requestExternalStorage() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    private void showPermissionTipDialog(int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("权限申请")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false);
        switch (requestCode) {
            case REQUEST_CODE_EXTERNAL_STORAGE:
                builder.setMessage(R.string.permission_external_storage);
                break;
            case REQUEST_CODE_PHONE_STATE:
                builder.setMessage(R.string.permission_phone_state);
                break;
            case REQUEST_CODE_LOCATION:
                builder.setMessage(R.string.permission_location);
                break;
            case REQUEST_CODE_CAMERA:
                builder.setMessage(R.string.permission_camera);
                break;
            case REQUEST_CODE_RECORD_AUDIO:
                builder.setMessage(R.string.permission_record_audio);
                break;
            default:return;
        }
        builder.show();
    }

    public void jumpMain(){
        if(TextUtils.isEmpty((CharSequence) SharedPreUtils.get(SharedPreUtils.SP_OPENID,""))){
            startActivity(new Intent(LauncherActivity.this,LoginActivity.class));
        } else {
            startActivity(new Intent(LauncherActivity.this,MainActivity.class));
        }
        finish();
    }

    @Override
    public void CountDownOnTick(long millisUntilFinished) {
        time_tv.setText("跳过："+(millisUntilFinished/1000 - 1)+"S");
    }

    @Override
    public void CountDownOnFinish() {
        myCount = null;
        jumpMain();
    }

}
