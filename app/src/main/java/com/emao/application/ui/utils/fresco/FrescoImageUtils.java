package com.emao.application.ui.utils.fresco;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.io.File;

/**
 * @author keybon
 */
public class FrescoImageUtils {
    //单例
    private static FrescoImageUtils ourInstance;

    public static FrescoImageUtils getInstance() {
        if (ourInstance == null) {
            ourInstance = new FrescoImageUtils();
        }
        return ourInstance;
    }

    private FrescoImageUtils() {

    }

    /**
     * 创建加载本地图片的Builder
     *
     * @param path 加载本地图片必须传递进来path
     * @return SdcardControllerBuilder
     */
    public SdcardImageController.SdcardControllerBuilder createSdcardBuilder(@NonNull String path) {
        SdcardImageController.SdcardControllerBuilder builder = new SdcardImageController.SdcardControllerBuilder(path);
        return builder;
    }

    /**
     * 创建加载网络图片的builder
     *
     * @param url 加载
     * @return WebImageControllerBuilder
     */
    public WebImageController.WebImageControllerBuilder createWebImageParamsBuilder(String url) {
        WebImageController.WebImageControllerBuilder builder = new WebImageController.WebImageControllerBuilder(url);
        return builder;
    }

    /**
     * 判断该文件是否是图片
     *
     * @param imagePath 文件路径
     * @return true 是图片 false 图片文件损坏
     */
    public static boolean isImage(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return false;
        }
        File file = new File(imagePath);
        if (!file.exists()) {
            return false;
        }
        //只读取图片的宽高，不将该文件读取到内存中，目的是下方设置图片的宽高时会使用，加载图片更改成了fresco
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        if (options.mCancel || options.outWidth == -1
                || options.outHeight == -1 || options.outWidth == 0 || options.outHeight == 0) {
            //表示图片已损毁
            return false;
        }
        return true;
    }

    public static void clearCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
    }
}
