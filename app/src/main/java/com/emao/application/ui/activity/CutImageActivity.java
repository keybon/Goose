package com.emao.application.ui.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.emao.application.R;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.utils.CommonUtils;
import com.zhouztashin.android.enjoycrop.EnjoyCropLayout;
import com.zhouztashin.android.enjoycrop.MyCompressBitUtils;
import com.zhouztashin.android.enjoycrop.SamsangPhotoPicUtil;
import com.zhouztashin.android.enjoycrop.core.BaseLayerView;
import com.zhouztashin.android.enjoycrop.core.clippath.ClipPathLayerView;
import com.zhouztashin.android.enjoycrop.core.clippath.ClipPathSquare;
import com.zhouztashin.android.enjoycrop.core.mask.ColorMask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author keybon
 */

public class CutImageActivity extends BaseActivity {

    public static final String IMAGE_CROP_THUMB_PATH = CommonUtils.getSDCardPath() + "/crop/" + "cropimage_thumb.jpg";
    public static final String IMAGE_CROP_THUMB_FILE = CommonUtils.getSDCardPath() + "/crop/";

    private EnjoyCropLayout ecl;
    private Bitmap bitmap;

    protected String mImageCroppedUri;
    private Uri data;
    private int degree = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cutimage);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();
        setActionBarTitle("");
        setTextViewRight(true);
        setTextRight("截取");

        ecl = findViewById(R.id.ecl);
        Intent intent = getIntent();
        mImageCroppedUri = intent.getStringExtra("uri");
        data = intent.getData();

    }

    @Override
    public void initData() {
        super.initData();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;

        if (mImageCroppedUri != null && mImageCroppedUri.contains("file:")) {
            bitmap = BitmapFactory.decodeFile(mImageCroppedUri,options);
            //处理图片旋转的情况 计算旋转值
            degree = SamsangPhotoPicUtil.readPictureDegree(mImageCroppedUri);
        } else {
            if (data != null) {
                String realFilePath = getRealFilePath(MainApp.IMApp, data);
                bitmap = BitmapFactory.decodeFile(realFilePath,options);
                //处理图片旋转的情况 计算旋转值
                degree = SamsangPhotoPicUtil.readPictureDegree(realFilePath);
            }
        }
        //处理图片旋转的情况 旋转图片
        if (bitmap != null) {
            Bitmap bitmap_new = SamsangPhotoPicUtil.changeBitmapDegree(bitmap, degree);
            //比例缩放后的bitmap
            Bitmap ratio = MyCompressBitUtils.ratio(bitmap_new, 800, 800);
            ecl.setImage(ratio);
            if(!ContentResolver.SCHEME_CONTENT.equals(data.getScheme())){
                MyCompressBitUtils.saveBitmapToFile(ratio, new File(mImageCroppedUri));
            }
        }

        defineCropParams();
    }

    @Override
    public void commitPress() {
        super.commitPress();


        if (bitmap != null && bitmap.isRecycled()) {
            bitmap.recycle();
        }
        Bitmap bitmap = ecl.crop();

        File file = new File(IMAGE_CROP_THUMB_FILE);
        if(!file.exists()){
            file.mkdirs();
        }

        File thumbFile = new File(IMAGE_CROP_THUMB_PATH);

        if(thumbFile.exists()){
            thumbFile.delete();
        }

        FileOutputStream out = null;
        try {

            out = new FileOutputStream(thumbFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            Bitmap commpressImage = MyCompressBitUtils.ratio(thumbFile.getPath(), 800, 800);

            MyCompressBitUtils.saveBitmapToFile(commpressImage, thumbFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        setResult(RESULT_OK);
        finish();

    }

    private void defineCropParams() {
        //设置裁剪集成视图，这里通过一定的方式集成了遮罩层与预览框
        BaseLayerView layerView = new ClipPathLayerView(this);
        //设置遮罩层,这里使用半透明的遮罩层
        layerView.setMask(ColorMask.getTranslucentMask());
        layerView.setShape(new ClipPathSquare(600));
        //设置裁剪集成视图
        ecl.setLayerView(layerView);
        //设置边界限制，如果设置了该参数，预览框则不会超出图片
        ecl.setRestrict(true);
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
