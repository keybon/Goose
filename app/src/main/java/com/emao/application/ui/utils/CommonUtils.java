package com.emao.application.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.emao.application.R;
import com.emao.application.ui.application.MainApp;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author keybon
 */

public class CommonUtils {


    public static int dip2px(Context context, float dbValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dbValue * scale + 0.5f);
    }

    public static int sp2dip(Context context, float spValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }

    public static String getResourceString(int resId) {
        return MainApp.IMApp.getResources().getString(resId);
    }

    /**
     * 设置添加屏幕的背景透明度
     * 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public static void setBackgroundAlpha(Context mContext, float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    public static Display getWindowDiaplay(Context context) {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display;
    }

    //字符串转时间戳
    public static long getLongTime(String timeString){
        long time = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date ;
        try{
            date = sdf.parse(timeString);
            time = date.getTime();
        } catch(ParseException e){
            e.printStackTrace();
        }
        return time;
    }

    //时间戳转字符串
    public static String getStrTime(String timeStamp){
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        long  l = Long.valueOf(timeStamp);
        timeString = sdf.format(new Date(l));
        return timeString;
    }


    /**
     * 5分钟内 显示刚刚
     * 5-59分钟 XX分钟前
     * 当天 上午/下午 HH:MM
     * 前一天 昨天 上午/下午 HH:MM
     * 更早 月-日 上午/下午 HH:MM
     *
     * @param timeStamp
     * @return
     */

    private static long secFormat = 1000;
    private static long minFormat = secFormat * 60;
    private static long fiveMin = minFormat * 5;
    private static long hourFormat = minFormat * 60;
    private static long dayFormat = hourFormat * 24;
    private static long twoDayFormat = dayFormat * 2;


    // 1分钟
    private final static long minute = 60 * 1000;
    // 1小时
    private final static long hour = 60 * minute;
    // 1天
    private final static long day = 24 * hour;
    // 月
    private final static long month = 31 * day;
    // 年
    private final static long year = 12 * month;


    public static String getTimeFormatText(long timeStamp) {

        if (timeStamp == 0) {
            return "";
        }

        //当前时间
        long currentTime = System.currentTimeMillis();
        // 得到毫秒值
        long diff = currentTime - timeStamp;

        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }



    public static String showMessageTime(long timeStamp) {

        if (timeStamp == 0) {
            return "";
        }

        //当前时间
        long currentTime = System.currentTimeMillis();
        // 得到毫秒值
        long timeDiff = currentTime - timeStamp;
        if (timeDiff < 0) {
            timeDiff = Math.abs(timeDiff);
        }

        Date date = new Date(timeStamp);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        String formatStr = "hh:mm";

        if (0 <= timeDiff && timeDiff < fiveMin) {
            //5分钟内
            return "刚刚";
        } else if (fiveMin <= timeDiff && timeDiff < hourFormat) {
            //5-59分钟
            return (timeDiff / minFormat) + "分钟前";
        } else if (hourFormat <= timeDiff && timeDiff < dayFormat) {
            //当天
            formatStr = showUpOrDown(hours);
        } else if (dayFormat <= timeDiff && timeDiff < twoDayFormat) {
            //前一天
            formatStr = "昨天 " + showUpOrDown(hours);
        } else if (twoDayFormat <= timeDiff) {
            //更早
            formatStr = "M月d日 " + showUpOrDown(hours);
        }
        return new SimpleDateFormat(formatStr).format(date);
    }

    public static String showUpOrDown(int hours) {
        if (hours >= 0 && hours <= 12) {
            return "上午 hh:mm";
        } else if (hours > 12 && hours < 24) {
            return "下午 hh:mm";
        } else {
            return "hh:mm";
        }
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/M", Locale.ENGLISH);

    public static String getRelativeDateString(long dateTime) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTimeInMillis(dateTime);
        if (currentCalendar.get(Calendar.WEEK_OF_YEAR) == tempCalendar.get(Calendar.WEEK_OF_YEAR)) {
            return "本周";
        }
        if (currentCalendar.get(Calendar.YEAR) == tempCalendar.get(Calendar.YEAR)
                && currentCalendar.get(Calendar.MONTH) == tempCalendar.get(Calendar.MONTH)) {
            return "这个月";
        }
        return DATE_FORMAT.format(tempCalendar.getTime());
    }

    public static final String getNaviItemDate(long time) {
        String dateStr = "";
        if (isInTheSameYear(time, System.currentTimeMillis())) {
            if (isInTheSameDay(time, System.currentTimeMillis())) {
                // 今天
                dateStr = hh_mm_Format(time);
            } else if (isYesterdayByDate(time)) {
                // 昨天
                dateStr = CommonUtils.getResourceString(R.string.app_name) + hh_mm_Format(time);
            } else if (isThisWeek(time)) {
                // 同一周
                dateStr = getWeek(time) + hh_mm_Format(time);
            } else {
                dateStr = yyyy_MM_DD_HH_mm_format(time);
            }
        } else {
            // 不是今年
            dateStr = yyyy_MM_DD_format(time);
        }
        return dateStr;
    }

    /**
     * 判断两个时间是否在同一天,用于导航获取域名对应IP及端口时使用
     */
    public static boolean isInTheSameDay(long src, long dest) {
        Calendar last = Calendar.getInstance();
        last.setTimeInMillis(src);
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(dest);
        int lastDay = last.get(Calendar.DAY_OF_YEAR);
        int currentDay = current.get(Calendar.DAY_OF_YEAR);
        return (lastDay == currentDay);
    }

    public static boolean isInTheSameYear(long src, long dest) {
        Calendar last = Calendar.getInstance();
        last.setTimeInMillis(src);
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(dest);
        int lastYear = last.get(Calendar.YEAR);
        int currentYear = current.get(Calendar.YEAR);
        return (lastYear == currentYear);
    }

    /**
     * HH:mm 上午/下午
     */
    public static String hh_mm_Format(long times) {
        String formatStr = "HH:mm";
        Date date = new Date(times);
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);
        String result = dateFormat.format(date);
        return result;
    }

    public static boolean isYesterdayByDate(long times) {
        Date msgTime = new Date(times);
        Date newTime = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(newTime);
        int hours = newTime.getHours();
        int minutes = newTime.getMinutes();
        int seconds = newTime.getSeconds();
        //得到当天0点对应的毫秒数
        long currentDay00 = newTime.getTime() - (hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000);
        long yesterdayDay00 = currentDay00 - 24 * 60 * 60 * 1000;
        int dayMinus = Math.abs(newTime.getDay() - msgTime.getDay());
        if (msgTime.getDay() != newTime.getDay() && (times > yesterdayDay00 && times < currentDay00) && (dayMinus == 1 || dayMinus == 6)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 方法简介：判断同一年中的两天是否在同一周
     */
    public static boolean isThisWeek(long time) {
        Calendar now = Calendar.getInstance();
        Calendar theDay = Calendar.getInstance();
        theDay.setTimeInMillis(time);
        now.setFirstDayOfWeek(Calendar.MONDAY);
        theDay.setFirstDayOfWeek(Calendar.MONDAY);

        int nowDayOfWeek = now.get(Calendar.WEEK_OF_YEAR);
        int theDayOfWeek = theDay.get(Calendar.WEEK_OF_YEAR);
        if (nowDayOfWeek == theDayOfWeek) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 方法简介：返回一天是周几
     */
    public static String getWeek(long time) {
        String[] weekArray = {"", "周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar theDay = Calendar.getInstance();
        theDay.setTimeInMillis(time);
        int week = theDay.get(Calendar.DAY_OF_WEEK);
        String weekStr = "";
        if (week >= 1 && week <= 7) {
            weekStr = weekArray[week];
        }
        return weekStr;
    }

    public static String yyyy_MM_DD_HH_mm_format(long times) {
        String formatString = "yyyy-MM-dd HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
        return dateFormat.format(new Date(times));
    }

    public static String yyyy_MM_DD_format(long times) {
        String formatString = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
        return dateFormat.format(new Date(times));
    }

    /**
     * 判断是否有网络连接
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断WIFI网络是否可用
     */
    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断4g网络是否可用
     */
    public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取当前网络连接的类型信息
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * 获取sd卡绝对路径
     */
    public static String getSDCardPath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FILE_NAME_PATH + Constants.FILE_NAME_PATH_;
    }

    public static String getVideoName() {
        String path ;
        File videoFile = new File(CommonUtils.getSDCardPath() + "/Video");
        if(!videoFile.exists()){
            videoFile.mkdirs();
        }
        path = videoFile + "/GOOSE_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
        return path;
    }
    public static String getVideoPath(){
        String path ;
        File videoFile = new File(CommonUtils.getSDCardPath() + "/Video");
        if(!videoFile.exists()){
            videoFile.mkdirs();
        }
        path = videoFile + "/GOOSE/feather_";
        return path;
    }

    /**
     * encodeBase64File:(将文件转成base64 字符串). <br/>
     * @author guhaizhou@126.com
     * @param path 文件路径
     * @return
     * @throws Exception
     * @since JDK 1.6
     */
    public static String encodeBase64File(String path) throws Exception {
        File  file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer,Base64.DEFAULT);
    }


    public static Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }


    /**
     *  图片大小控制
     */
    public static DraweeController getDraweeController(Context mContext,DraweeView targetView, String path) {
        Uri uri = Uri.parse(path);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                //根据View的尺寸放缩图片
                .setResizeOptions(new ResizeOptions(CommonUtils.dip2px(mContext,50), CommonUtils.dip2px(mContext,50)))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(targetView.getController())
                .setImageRequest(request)
                .setCallerContext(uri)
                .build();

        return controller;
    }

    /**
     *  详情页 特殊
     */
    public static DraweeController getDraweeDetailsController(Context mContext,DraweeView targetView, String path) {
        Uri uri = Uri.parse(path);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                //根据View的尺寸放缩图片
                .setResizeOptions(new ResizeOptions(CommonUtils.dip2px(mContext,80), CommonUtils.dip2px(mContext,80)))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(targetView.getController())
                .setImageRequest(request)
                .setCallerContext(uri)
                .build();

        return controller;
    }
    /**
     *
     */
    public static DraweeController getDraweeDetailsController(Context mContext,DraweeView targetView, String path,int widht,int height) {
        Uri uri = Uri.parse(path);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                //根据View的尺寸放缩图片
                .setResizeOptions(new ResizeOptions(CommonUtils.dip2px(mContext,widht), CommonUtils.dip2px(mContext,height)))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(targetView.getController())
                .setImageRequest(request)
                .setCallerContext(uri)
                .build();

        return controller;
    }

    /**
     * 判断 当前时间 在 0点-几点中间
     */
    public static boolean timeInBetween(int clock){
        // 当前日期
        Calendar cal = Calendar.getInstance();
        // 获取小时
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        Log.e("time_test","获取小时 hour = "+hour);
        // 获取分钟
        int minute = cal.get(Calendar.MINUTE);
        Log.e("time_test","获取分钟 minute = "+minute);
        // 从0:00分开是到目前为止的分钟数
        int minuteOfDay = hour * 60 + minute;
        Log.e("time_test","从0:00分开是到目前为止的分钟数 minuteOfDay = "+minuteOfDay);
        // 起始时间 00:20的分钟数
        final int start = 0 * 60;
        // 结束时间 8:00的分钟数
        final int end = clock * 60;
        Log.e("time_test","start = "+start +" end = "+end);
        if (minuteOfDay >= start && minuteOfDay <= end) {
            Log.e("time_test","1111 ");
            return true;
        } else {
            return false;
        }
    }

}
