package com.emao.application.ui.utils.fresco;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.emao.application.ui.utils.CommonUtils;

import cn.jzvd.JZVideoPlayerStandard;

/**
 *
 * @author keybon
 * @date 2018/1/18
 */

public class AsyncTaskVideoLoad extends AsyncTask<String, Integer, Bitmap> {

    private JZVideoPlayerStandard Image = null;

    public AsyncTaskVideoLoad(JZVideoPlayerStandard img)
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
            Image.thumbImageView.setImageBitmap(bitmap);
        }
    }
}
