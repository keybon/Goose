package com.emao.application.ui.photo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.provider.MediaStore;

import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.utils.CommonUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 此文件涉及嵌入版，请在同步独立版程序时，同步嵌入版程序
 */

public class BitmapUtils {

    /***
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 这个是等比例缩放
     */
    public static byte[] getZoomImage(Context context, Bitmap sourceBitmap, int widthLimit, int heightLimit, boolean isThumbnail, int quality) {
        InputStream input = null;
        try {
            int sourceWidth = sourceBitmap.getWidth();
            int sourceHeight = sourceBitmap.getHeight();
            float scale = 1f;
            if (isThumbnail) {
                if (sourceWidth > sourceHeight) {
                    scale = widthLimit * 1.0f / sourceWidth;
                    if (scale * sourceHeight > heightLimit) {
                        scale = heightLimit * 1.0f / sourceHeight;
                    }
                } else {
                    scale = heightLimit * 1.0f / sourceHeight;
                    if (scale * sourceWidth > widthLimit) {
                        scale = widthLimit * 1.0f / sourceWidth;
                    }
                }
            } else {
                if (sourceWidth > sourceHeight) {
                    if (sourceWidth > widthLimit) {
                        scale = widthLimit * 1.0f / sourceWidth;
                    }
                } else {
                    if (sourceHeight > heightLimit) {
                        scale = heightLimit * 1.0f / sourceHeight;
                    }
                }
            }

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap bitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceWidth, sourceHeight, matrix, true);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, quality, os);
            //bitmap.recycle();
            bitmap = null;
            return os.toByteArray();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 图片圆角
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, final float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        //int roundColor = 0xff424242;
        //paint.setColor(roundColor);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.reset();
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }

    public static Bitmap createRoundedCornerBackground(int width, int height, int color) {
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(color);
        return output;
    }

    public static Bitmap compositeBitmap(Bitmap baseBM, Bitmap frontBM) {
        Canvas canvas = new Canvas(baseBM);
        final Paint paint = new Paint();
        canvas.drawBitmap(baseBM, 0, 0, paint);
        final Rect rect = new Rect(0, 0, baseBM.getWidth(), baseBM.getHeight());
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
        canvas.drawBitmap(frontBM, rect, rect, paint);
        return baseBM;
    }

    /**
     * 根据路径获取图标的bitmap
     *
     * @return
     */
    public static Bitmap createBitmapByPath(Context context, String path) {
        InputStream input = null;
        try {
            Options opt = new Options();
            input = context.getContentResolver().openInputStream(LocalStringUtils.toUriByStr(path));
            opt.inJustDecodeBounds = true;
            opt.inPreferredConfig = Config.ARGB_8888;
            BitmapFactory.decodeStream(input, null, opt);
            int outWidth = opt.outWidth;
            int outHeight = opt.outHeight;

            int s = 1;
            while ((outWidth / s > 240) || (outHeight / s > 240)) {
                s *= 2;
            }
            Options options = new Options();
            options.inSampleSize = s;
            options.inPreferredConfig = Config.ARGB_8888;
            input = context.getContentResolver().openInputStream(LocalStringUtils.toUriByStr(path));
            return BitmapFactory.decodeStream(input, null, options);
        } catch (Exception e) {

        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static byte[] getThumbFromHD(byte[] hd) {
        Bitmap bm;
        try {
            Options tempOpt = new Options();
            tempOpt.inJustDecodeBounds = true;
            bm = BitmapFactory.decodeByteArray(hd, 0, hd.length, tempOpt);
            int s = 1;
            while ((tempOpt.outHeight / s > 240) || (tempOpt.outHeight / s > 240)) {
                s *= 2;
            }
            Options options = new Options();
            options.inSampleSize = s;
            options.inPreferredConfig = Config.ARGB_8888;
            bm = BitmapFactory.decodeByteArray(hd, 0, hd.length, options);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bm.compress(CompressFormat.JPEG, 75, os);
            bm.recycle();
            bm = null;
            return os.toByteArray();

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Bitmap获取byte[]
     *
     * @param bm
     * @return
     */
    public static byte[] getPortraitByteArrayNoRecycle(Bitmap bm) {
        InputStream input = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bm.compress(CompressFormat.PNG, 0, os);
            return os.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 从Bitmap获取byte[]
     *
     * @param bm
     * @return
     */
    public static byte[] getPortraitByteArray(Bitmap bm) {
        InputStream input = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bm.compress(CompressFormat.JPEG, 100, os);
            bm.recycle();
            return os.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] getCompressedBytes(Bitmap bm) {
        int quality = 95;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bm.compress(CompressFormat.JPEG, quality, os);
        while (os.toByteArray().length > 1024 * 31) {
            os.reset();
            quality = quality - 5;
            bm.compress(CompressFormat.JPEG, quality, os);
        }
        return os.toByteArray();
    }

    public static byte[] getPortraitByteArray(Bitmap bm, int quality) {
        InputStream input = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bm.compress(CompressFormat.JPEG, quality, os);
            bm.recycle();
            return os.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * 这个是等比例缩放: bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
     */
    public static byte[] getResizedImageData(Context context, byte[] data, int quality, int widthLimit, int heightLimit, boolean isThumb) {
        if (widthLimit == 800) {
            widthLimit = 1000;
        }
        if (heightLimit == 800) {
            heightLimit = 1000;
        }
        if (isThumb) {
            widthLimit = 115;
            heightLimit = 115;
        }
        return getCompressedImage(context, data, widthLimit * heightLimit);
//		Options opt =new Options();opt.inJustDecodeBounds=true;
//		Bitmap decodeByteArray = BitmapFactory.decodeByteArray(data, 0,data.length, opt);
//		int outWidth = opt.outWidth;
//		int outHeight = opt.outHeight;
//		String memeType=opt.outMimeType;
//		CompressFormat format=CompressFormat.JPEG;
//		if(memeType!=null&&memeType.endsWith("png")){
//			format=CompressFormat.PNG;
//		}
//		 int s = 1;
//		 while ((outWidth / s > widthLimit) || (outHeight / s > heightLimit)) {
//			 s *= 2;
//		  } Options options = new BitmapFactory.Options();
//		    options.inSampleSize = s;
//			try {
//				Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length,options);
//			    ByteArrayOutputStream os = new ByteArrayOutputStream();
//			    int size = (b.getRowBytes() * b.getHeight())/1024 ;
//			    quality = computeQuality(size , isThumb);
//			    b.compress(format, quality, os);
//			    b.recycle();
//			    b = null;
//			    return os.toByteArray();
//			} catch (Exception e) {
//				try {
//					Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
//				    ByteArrayOutputStream os = new ByteArrayOutputStream();
//				    int size = (b.getRowBytes() * b.getHeight())/1024 ;
//				    quality = computeQuality(size , isThumb);
//				    b.compress(format, quality, os);
//				    b.recycle();
//				    b = null;
//				    return os.toByteArray();
//				}catch(Exception e2) {
//					e2.printStackTrace();
//					return null;
//				}
//			}
    }

    public static byte[] getResizedImageData(Context context, Uri uri, int quality, int widthLimit, int heightLimit, boolean isThumb) {
        if (widthLimit == 800) {
            widthLimit = 1000;
        }
        if (heightLimit == 800) {
            heightLimit = 1000;
        }
        if (isThumb) {
            widthLimit = 115;
            heightLimit = 115;
        }
        return getCompressedImage(context, uri, widthLimit * heightLimit);
        /*InputStream input = null;
		Options opt = decodeBitmapOptionsInfo(context, uri);
		if(opt == null) {
			return null;
		}
	    int outWidth = opt.outWidth;
	    int outHeight = opt.outHeight;
	    int s = 1;
	    while ((outWidth / s > widthLimit) || (outHeight / s > heightLimit)) {
		s *= 2;
	    }

	    Options options = new BitmapFactory.Options();
	    options.inSampleSize = s;
	    options.inPreferredConfig = Bitmap.Config.RGB_565;
		try {
		    // options.inSampleSize = computeSampleSize(opt, -1, widthLimit *
		    // heightLimit);

		    input = context.getContentResolver().openInputStream(uri);
		    Bitmap b = BitmapFactory.decodeStream(input, null, options);
		    ByteArrayOutputStream os = new ByteArrayOutputStream();
		    int size = (b.getRowBytes() * b.getHeight())/1024 ;
		    quality = computeQuality(size , isThumb);
		    b.compress(CompressFormat.JPEG, quality, os);
		    b.recycle();
		    b = null;
		    return os.toByteArray();

		} catch (Exception e) {
			try {
				input = new FileInputStream(new File(uri.toString()));
				Bitmap b = BitmapFactory.decodeStream(input, null, options);
			    ByteArrayOutputStream os = new ByteArrayOutputStream();
			    int size = (b.getRowBytes() * b.getHeight())/1024 ;
			    quality = computeQuality(size , isThumb);
			    b.compress(CompressFormat.JPEG, quality, os);
			    b.recycle();
			    b = null;
			    return os.toByteArray();
			}
			catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return null;
			} catch(Exception e2) {
				e2.printStackTrace();
				return null;
			}
		} finally {
		    if (input != null) {
			try {
			    input.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		    }
		}*/
    }

    public static int computeSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;

    }

    private static int computeInitialSampleSize(Options options, int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),

                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static Options decodeBitmapOptionsInfo(Context context, Uri uri) {
        InputStream input = null;
        Options opt = new Options();
        try {
            input = context.getContentResolver().openInputStream(uri);
            opt.inJustDecodeBounds = true;
            opt.inPreferredConfig = Config.ARGB_8888;
            BitmapFactory.decodeStream(input, null, opt);
            return opt;
        } catch (FileNotFoundException e) {
            String path = null;
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(1);
            } else {
                path = uri.toString();
                if (path.indexOf("file:///mnt") > -1) {
                    path = path.substring("file:///mnt".length());
                } else if (path.indexOf("file://") > -1) {
                    path = path.substring("file://".length());
                }
            }

            if (path != null) {
                try {
                    File file = new File(path);
                    if (file.exists()) {

                    }
                    input = new FileInputStream(new File(path));
                    opt.inJustDecodeBounds = true;
                    opt.inPreferredConfig = Config.ARGB_8888;
                    BitmapFactory.decodeStream(input, null, opt);
                    return opt;
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static byte[] getScaleddImage(Context context, Uri uri, int widthLimit, int heightLimit) {
        InputStream input = null;
        try {
            input = context.getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(input, null, getBitmapOptions());
            Bitmap c = Bitmap.createScaledBitmap(b, widthLimit, heightLimit, true);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            c.compress(CompressFormat.JPEG, 100, os);
            b.recycle();
            c.recycle();
            b = null;
            c = null;
            return os.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存图片到SDCard的默认路径下.
     *
     * @throws IOException
     */
    public static void saveBitmapToSDCard(File f, Bitmap bitmap) throws IOException {
        saveBitmapToSDCard(f, bitmap, CompressFormat.JPEG);
    }

    /**
     * 保存图片到SDCard的默认路径下.
     *
     */
    public static void saveBitmapToSDCard(File f, Bitmap mBitmap, CompressFormat imageType) throws IOException {
        if (f.exists()) {
            f.delete();
        } else {
            f.createNewFile();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(imageType, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 这个是等比例缩放: bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
     */
    public static Bitmap getImageDataByUri(Context context, Uri uri) {
        InputStream input = null;
        Bitmap b = null;
        try {
            input = context.getContentResolver().openInputStream(uri);
            b = BitmapFactory.decodeStream(input, null, getBitmapOptions());

        } catch (Exception e) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }

        return b;
    }

    public static Options getBitmapOptions() {
        Options opts = new Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Config.RGB_565;
        return opts;
    }

    public static Options getThumbBitmapOptions() {
        Options opts = new Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inSampleSize = 2;
        opts.inPreferredConfig = Config.RGB_565;
        return opts;
    }

    public static byte[] getImageDataByUri(Uri uri, Context context) {
        return getResizedImageData(context, uri, 100, 1000, 1000, false);
    }

    public static byte[] getCardBackgroundImageDataByUri(Uri uri, Context context) {
        byte[] data = getResizedImageData(context, uri, 100, 960, 960, false);

        Bitmap sourceBitmap = null;

        int quality = 100;
        int len = data.length;
        while (len > 1024L * 100) {
            quality = quality - 4;
            // data = getZoomImage(context, sourceBitmap, 640, 960, false, 90);
            Options opts = new Options();
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            opts.inPreferredConfig = Config.RGB_565;
            sourceBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            sourceBitmap.compress(CompressFormat.JPEG, quality, os);
            sourceBitmap.recycle();
            sourceBitmap = null;
            len = os.toByteArray().length;
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (len <= 1024L * 100) {

                return data = os.toByteArray();
            }
        }

        return data;
    }

    public static byte[] getImageThumbDataByUri(Uri uri, Context context) {
        return getResizedImageData(context, uri, 100, 160, 160, true);
    }

    public static Bitmap imageCrop(Bitmap src, int width, int height) {
        int srcWid = src.getWidth();
        int srcHei = src.getHeight();
        int destW = Math.min(width, srcWid);
        int destH = Math.min(srcHei, height);
        return Bitmap.createBitmap(src, 0, 0, destW, destH, null, false);
    }

    public static Bitmap scaleToAdapterWh(Bitmap bm, int limitWidth, int limitHeight) {
        int srcWid = bm.getWidth();
        int srcHei = bm.getHeight();
        float scaleW = limitWidth * 1.0f / srcWid;
        float scaleH = limitHeight * 1.0f / srcHei;
        float destScale = Math.min(scaleW, scaleH);
        return Bitmap.createScaledBitmap(bm, (int) (srcWid * destScale), (int) (srcHei * destScale), false);
    }

    /**
     * 获取视频缩略图
     *
     * @param videoName
     * @param activity
     * @return
     */
    public static Bitmap loadThumbnail(String videoName, Activity activity) {

        String[] proj = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor videocursor = activity.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, MediaStore.Video.Media.DISPLAY_NAME + "='" + videoName + "'", null, null);
        Bitmap curThumb = null;

        if (videocursor.getCount() > 0) {
            videocursor.moveToFirst();
            ContentResolver crThumb = activity.getContentResolver();
            Options options = new Options();
            options.inSampleSize = 1;
            curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, videocursor.getInt(0), MediaStore.Video.Thumbnails.MICRO_KIND, (Options) null);

        }

        return curThumb;
    }

    public static String getFileName(String entireFilePath) {
        File imageFile = null;
        if (ContentResolver.SCHEME_CONTENT.equals(Uri.parse(entireFilePath).getScheme())) {
            ContentResolver cr = MainApp.IMApp.getContentResolver();
            Uri imageUri = Uri.parse(entireFilePath);
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = cr.query(imageUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            imageFile = new File(cursor.getString(column_index));
        } else if (ContentResolver.SCHEME_FILE.equals(Uri.parse(entireFilePath).getScheme())) {
            imageFile = new File(Uri.parse(entireFilePath).getPath());
        } else {
            imageFile = new File(entireFilePath);
        }
        return imageFile.getName();
    }

    private static final long DEFAULT_MAX_BM_SIZE = 1000 * 250;

    public static Bitmap loadBitmap(String bmPath) {
        if (LocalStringUtils.isEmpty(bmPath)) {
            return null;
        }
        File file = new File(bmPath);
        if (!file.exists()) {
            return null;
        } else {
            Bitmap bm;
            Options opts = new Options();
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            opts.inPreferredConfig = Config.RGB_565;
            long length = file.length();

            if (length > DEFAULT_MAX_BM_SIZE) {
                long ratio = length / DEFAULT_MAX_BM_SIZE;
                long simpleSize = (long) Math.ceil(Math.sqrt(ratio));
                opts.inSampleSize = (int) simpleSize;
                try {
                    bm = BitmapFactory.decodeFile(bmPath, opts);
                } catch (Exception e) {
                    bm = null;
                }
            } else {
                bm = BitmapFactory.decodeFile(bmPath, opts);
            }
            return bm;
        }
    }

    public static Bitmap loadBitmap(byte[] bmByte) {
        if (bmByte == null || bmByte.length == 0) {
            return null;
        }
        Bitmap bm;
        Options opts = new Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Config.RGB_565;
        long length = bmByte.length;
        if (length > DEFAULT_MAX_BM_SIZE) {
            long ratio = length / DEFAULT_MAX_BM_SIZE;
            long simpleSize = (long) Math.ceil(Math.sqrt(ratio));
            opts.inSampleSize = (int) simpleSize;
            try {
                bm = BitmapFactory.decodeByteArray(bmByte, 0, bmByte.length, opts);
            } catch (Exception e) {
                bm = null;
            }
        } else {
            bm = BitmapFactory.decodeByteArray(bmByte, 0, bmByte.length, opts);
        }
        return bm;
    }

    /**
     * 获取SD卡中最新图片路径,可能获取到倒数第二张
     *
     * @return /mnt/sdcard/Camer/xxx.ic
     */
    public static String getLatestImagePath(Activity context) {
        String latestImagePath = null;
        String[] items = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items, null, null, MediaStore.Images.Media._ID
                + " desc");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (cursor.moveToNext(); !cursor.isAfterLast(); ) {
                latestImagePath = cursor.getString(1);
                break;
            }
            cursor.close();
        }
        return latestImagePath;
    }

    /**
     * 加载进图片后进行分辨率适配
     */
    public static void chatBgAdapter(String chatBgPath) throws IOException {
        if (LocalStringUtils.isEmpty(chatBgPath)) {
            return;
        }
        File file = new File(chatBgPath);
        if (file.exists()) {
            Options opts = new Options();
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            opts.inPreferredConfig = Config.RGB_565;
            Bitmap bm = BitmapFactory.decodeFile(chatBgPath, opts);
            //生成合适分辨率的图,刚好合适时无动作.
            Bitmap cropedBitmap = scaleChatBgBitmap(bm);
            //保存覆盖原图
            if (cropedBitmap != null) {
                saveBitmapToSDCard(file, cropedBitmap, CompressFormat.JPEG);
                cropedBitmap.recycle();
            }
        }
    }

    /**
     * 按照聊天背景高宽 裁剪出合适分辨率的图片 (无需裁剪时返回空)
     */
    public static Bitmap scaleChatBgBitmap(Bitmap bm) {
        Bitmap cropedBitmap;
        try {
            int bmWidth = bm.getWidth();
            int bmHeight = bm.getHeight();
            int[] winWH = new int[]{CommonUtils.getWindowDiaplay(MainApp.IMApp).getWidth(),
                    CommonUtils.getWindowDiaplay(MainApp.IMApp).getWidth()};
            double wScale = ((double) winWH[0]) / bmWidth;
            double hScale = ((double) winWH[1]) / bmHeight;
            double destScale = Math.max(wScale, hScale);

            if (bmWidth == winWH[0] && bmHeight == winWH[1]) {
                return null;
            } else if (bmWidth < winWH[0] || bmHeight < winWH[1]) { // 如果小于屏幕尺寸,先放大到与屏幕比例相适应的尺寸才截取
                if (wScale > hScale) { // 如果是一张长图
                    Bitmap scaledBitmp = Bitmap.createScaledBitmap(bm, (int) (bmWidth * destScale), (int) (bmHeight * destScale), false);
                    //				bm.recycle();
                    cropedBitmap = Bitmap.createBitmap(scaledBitmp, 0, (int) ((scaledBitmp.getHeight() - winWH[1]) / 2), scaledBitmp.getWidth(), winWH[1]);
                    scaledBitmp = null;
                    //				scaledBitmp.recycle();	//置空后让系统自动回收,否则Nexus机型出错
                } else { // 如果是一张宽图，或着比例合适的图
                    Bitmap scaledBitmp = Bitmap.createScaledBitmap(bm, (int) (bmWidth * destScale), (int) (bmHeight * destScale), false);
                    //				bm.recycle();
                    cropedBitmap = Bitmap.createBitmap(scaledBitmp, (int) ((scaledBitmp.getWidth() - winWH[0]) / 2), 0, winWH[0], scaledBitmp.getHeight());
                    scaledBitmp = null;
                    //				scaledBitmp.recycle();
                }
            } else { // 先缩小到与屏幕相等尺寸才截取
                if (wScale > hScale) { // 如果是一张长图
                    if (wScale == 1) {
                        //bm宽度刚好,高度略长
                        cropedBitmap = Bitmap.createBitmap(bm, 0, (int) ((bm.getHeight() - winWH[1]) / 2), bm.getWidth(), winWH[1]);
                    } else {
                        Bitmap scaledBitmp = Bitmap.createScaledBitmap(bm, (int) (bmWidth * destScale), (int) (bmHeight * destScale), false);
                        //					bm.recycle();
                        cropedBitmap = Bitmap.createBitmap(scaledBitmp, 0, (int) ((scaledBitmp.getHeight() - winWH[1]) / 2), scaledBitmp.getWidth(), winWH[1]);
                        scaledBitmp = null;
                        //					scaledBitmp.recycle();
                    }
                } else { // 如果是一张宽图，或着比例合适的图
                    if (hScale == 1) {
                        //bm 高度刚好,宽度略宽
                        cropedBitmap = Bitmap.createBitmap(bm, (int) ((bm.getWidth() - winWH[0]) / 2), 0, winWH[0], bm.getHeight());
                    } else {
                        Bitmap scaledBitmp = Bitmap.createScaledBitmap(bm, (int) (bmWidth * destScale), (int) (bmHeight * destScale), false);
                        //					bm.recycle();
                        cropedBitmap = Bitmap.createBitmap(scaledBitmp, (int) ((scaledBitmp.getWidth() - winWH[0]) / 2), 0, winWH[0], scaledBitmp.getHeight());
                        scaledBitmp = null;
                        //					scaledBitmp.recycle();
                    }
                }
            }
            bm = null;
        } catch (Exception ex) {
            cropedBitmap = null;
            ex.printStackTrace();
        }
        return cropedBitmap;
    }

    // 将图片加入media store
    public static void saveToMediaStore(final String filePath, final Context context) {
        MediaScannerConnectionClient mediaScannerClient = new
                MediaScannerConnectionClient() {
                    private MediaScannerConnection msc = null;

                    {
                        msc = new MediaScannerConnection(context, this);
                        msc.connect();
                    }

                    @Override
                    public void onMediaScannerConnected() {
                        // Optionally specify a MIME Type, or have the Media Scanner imply one based on the filename.
                        String mimeType = null;
                        msc.scanFile(filePath, mimeType);
                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        msc.disconnect();
                    }
                };
    }

    public synchronized static byte[] getCompressedImage(Context context, Uri imageUri, int maxPixels) {
        byte[] ret = null;
        InputStream in = null;
        ByteArrayOutputStream os = null;
        try {
            final int IMAGE_MAX_SIZE = (maxPixels == 0) ? 1000000 : maxPixels;

            String uriContent = imageUri.toString();
            Uri uri = null;
            if (uriContent.indexOf("://") <= 0) {
                uri = Uri.fromFile(new File(uriContent));
            } else {
                uri = imageUri;
            }
            in = context.getContentResolver().openInputStream(uri);
            // Decode image size
            Options o = new Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();
            int scale = 1;

            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }

            Bitmap b = null;
            in = context.getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);
                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
                //b.recycle();
                b = scaledBitmap;
                os = new ByteArrayOutputStream();
                b.compress(CompressFormat.JPEG, 70, os);
                b.recycle();
                b = null;
                ret = os.toByteArray();
            } else {
                o = new Options();
                o.inSampleSize = 1;
                b = BitmapFactory.decodeStream(in, null, o);
                os = new ByteArrayOutputStream();
                b.compress(CompressFormat.JPEG, 70, os);
                b.recycle();
                b = null;
                ret = os.toByteArray();
                System.gc();
            }
            in.close();
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (os != null) {
                try {
                    os.close();
                    os = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized static byte[] getCompressedImage(Context context, byte[] data, int maxPixels) {
        byte[] ret = null;
        InputStream in = null;
        ByteArrayOutputStream os = null;
        try {
            final int IMAGE_MAX_SIZE = (maxPixels == 0) ? 1000000 : maxPixels;
            in = new ByteArrayInputStream(data);
            // Decode image size
            Options o = new Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();
            int scale = 1;

            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }

            Bitmap b = null;
            in = new ByteArrayInputStream(data);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);
                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
                //b.recycle();
                b = scaledBitmap;
                os = new ByteArrayOutputStream();
                b.compress(CompressFormat.JPEG, 70, os);
                b.recycle();
                b = null;
                ret = os.toByteArray();
            } else {
                o = new Options();
                o.inSampleSize = 1;
                b = BitmapFactory.decodeStream(in, null, o);
                os = new ByteArrayOutputStream();
                b.compress(CompressFormat.JPEG, 70, os);
                b.recycle();
                b = null;
                ret = os.toByteArray();
                System.gc();
            }
            in.close();
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (os != null) {
                try {
                    os.close();
                    os = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap paintCornerBitmap(Bitmap bit, int width, int height, float roundPX) {
        Bitmap output = Bitmap.createBitmap(width,
                height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#020202"));
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bit, rect, rect, paint);

        return output;
    }

    public static double IMAGE_MAX_SIZE = 410.0;
    public static double IMAGE_THUMB_MAX_SIZE = 80.0;

    private static int computeQuality(int size, boolean isThumb) {
        int quality = 0;
        if (isThumb) {
            if (size <= IMAGE_THUMB_MAX_SIZE) {
                quality = 100;
            } else {
                quality = (int) (IMAGE_THUMB_MAX_SIZE * 100 / size);
            }
        } else {
            if (size <= IMAGE_MAX_SIZE) {
                quality = 100;
            } else {
                quality = (int) (IMAGE_MAX_SIZE * 100 / size);
            }
        }
        return quality;
    }

    public static Bitmap resolveUri(Uri uri, Context context) {
        String path = uri.toString().toLowerCase();
        InputStream is = null;
        Bitmap bitmap = null;
        if (path.startsWith("file:")) {
            if (!(path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".ic"))) {
                return bitmap;
            }
            try {
                is = context.getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else if (path.startsWith("content:")) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            bitmap = BitmapFactory.decodeFile(cursor.getString(column_index));
        }
        return bitmap;
    }


    /**
     * zsl 用于聊天发送方拍照图片较大时采用的图片压缩方法
     * @param path  图片路径
     * @param reqWidth 需要的图片宽度
     * @param reqHeight 需要的图片高度
     * @return 返回给微信的压缩好的bitmap
     */

    public static Bitmap decodeSampleBitmapFromPath(String path, int reqWidth, int reqHeight) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calcuateInSampleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds = false ;
        return BitmapFactory.decodeFile(path,options);
    }

    public static int calcuateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
