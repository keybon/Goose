package com.emao.application.ui.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 *
 * @author keybon
 * @date 2018/1/18
 */

public class AsyncTaskImageLoad extends AsyncTask<String, Integer, Bitmap> {

    private ImageView Image = null;

    public AsyncTaskImageLoad(ImageView img)
    {
        Image=img;
    }


    @Override
    protected Bitmap doInBackground(String... strings) {
        return CommonUtils.createVideoThumbnail(strings[0],640,480);
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(Image != null && bitmap != null){
            Image.setImageBitmap(bitmap);
        }
    }
}
