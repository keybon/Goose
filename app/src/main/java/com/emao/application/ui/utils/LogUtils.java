package com.emao.application.ui.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by keybon on 2017/12/9.
 */

public class LogUtils {

//    private static final int LEVEL = 1;
    private static final int LEVEL = 0;
    public static int count = 0;

    private static final String TAG = "goose.test";


    public static void e(String text){
        if(LEVEL < 1){
            Log.e(TAG,text);
        }
    }

    public static void i(String text){
        if(LEVEL < 1){
            Log.e(TAG,text);
        }
    }

    public static void w(String text){
        if(LEVEL < 1){
            Log.e(TAG,text);
        }
    }

    /** 具体先不实现 */
	public static void f(final String content)
	{
		if (LEVEL < 1)
		{
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Date now = new Date();
						String dirName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Goose/logger";
						File dir = new File(dirName);
						if (!dir.exists()) {
                            dir.mkdirs();
                        }
						String fileName = dirName + "/logger"+count+".txt";
						count+=1;
						do {
							PrintWriter ps = null;

							try {
								ps = new PrintWriter(fileName);
							} catch (FileNotFoundException e) {
								break;
							}
							String msg = "--------" + now.toGMTString() + "--------\n";
							ps.write(msg);
							ps.write(content);
							ps.flush();
							ps.close();
						} while (false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}
