package com.emao.application.http;

import android.os.Handler;
import android.os.Looper;

import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.Encrypt;
import com.emao.application.ui.utils.GsonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.valueOf;

/**
 *
 * @author keybon
 */

public class OkHttpManager {


    /**
     * 静态实例
     */
    private static OkHttpManager instance;

    /**
     * okhttpclient实例
     */
    private OkHttpClient mClient;


    /**
     * 单例模式  获取OkHttpManager实例
     *
     * @return
     */
    public static OkHttpManager getInstance() {

        if (instance == null) {
            synchronized (OkHttpManager.class){
                if (instance == null){
                    instance = new OkHttpManager();
                }
            }
        }
        return instance;
    }

    private static Handler handler;


    /**
     * 构造方法
     */
    private OkHttpManager() {

        mClient = new OkHttpClient();

        /**
         * 在这里直接设置连接超时.读取超时，写入超时
         */
        mClient.newBuilder().connectTimeout(3000, TimeUnit.MILLISECONDS);
        mClient.newBuilder().readTimeout(3000, TimeUnit.MILLISECONDS);
        mClient.newBuilder().writeTimeout(3000, TimeUnit.MILLISECONDS);

        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 分发失败的时候调用
     */
    private void deliverDataFailure(final Request request, final IOException e, final DataCallBack callBack) {
        /**
         * 在这里使用异步处理
         */
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.requestFailure(request, e);
                }
            }
        });
    }


    /**
     * 分发成功的时候调用
     */
    private void deliverDataSuccess(final String result, final DataCallBack callBack) {
        /**
         * 在这里使用异步线程处理
         */
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.requestSuccess(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //-------------------------异步的方式请求数据--------------------------

    public void getAsync(String url, DataCallBack callBack) {
        getInstance().inner_getAsync(url, callBack);
    }

    /**
     * okHttp get同步请求
     * @param actionUrl  接口地址
     * @param paramsMap   请求参数
     */
    public void getAsyncParams(String actionUrl, HashMap<String, String> paramsMap, final DataCallBack callBack) {
        getInstance().inner_getAsyncParams(actionUrl,paramsMap,callBack);
    }

    //https://api.mch.weixin.qq.com/pay/unifiedorder  /appid=22 ?
    private void inner_getAsyncParams(String actionUrl,HashMap<String, String> paramsMap,final DataCallBack callBack){
        StringBuilder tempParams = new StringBuilder();
        if (paramsMap == null) {
            paramsMap = new HashMap<>();
        }
        paramsMap.put("signature", Encrypt.getSignature());
        paramsMap.put("timeStamp", Encrypt.timeStamp);
        paramsMap.put("randomStr", Encrypt.randomStr);
        try {
            //处理参数
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                //对参数进行URLEncoder
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求地址
            String requestUrl = String.format("%s?%s",actionUrl, tempParams.toString());
            //创建一个请求
            final Request request = new Request.Builder().url(requestUrl).build();
            //创建一个Call
            mClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    deliverDataFailure(request, e, callBack);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = null;
                    try {
                        result = response.body().string();
                    } catch (IOException e) {
                        deliverDataFailure(request, e, callBack);
                    }
                    deliverDataSuccess(result, callBack);
                }
            });
        } catch (Exception e) {

        }
    }

    /**
     * 内部逻辑请求的方法
     */
    private void inner_getAsync(String url, final DataCallBack callBack) {
        final Request request = new Request.Builder().url(url).build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = null;
                try {
                    result = response.body().string();
                } catch (IOException e) {
                    deliverDataFailure(request, e, callBack);
                }
                deliverDataSuccess(result, callBack);
            }
        });
    }

    public void nomalPostAsync(String url, Map<String, String> params, final DataCallBack callBack){
        nomal_postAsync(url,params,callBack);
    }


    private void nomal_postAsync(String url, Map<String, String> params, final DataCallBack callBack) {

        RequestBody requestBody = null;
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("signature", Encrypt.getSignature());
        params.put("timeStamp", Encrypt.timeStamp);
        params.put("randomStr", Encrypt.randomStr);

        /**
         * 如果是3.0之前版本的，构建表单数据是下面的一句
         */
        //FormEncodingBuilder builder = new FormEncodingBuilder();

        /**
         * 3.0之后版本
         */
        FormBody.Builder builder = new FormBody.Builder();

        /**
         * 在这对添加的参数进行遍历，map遍历有四种方式
         */
        for (Map.Entry<String, String> map : params.entrySet()) {
            String key = map.getKey().toString();
            String value = null;
            if (map.getValue() == null) {
                value = "";
            } else {
                value = map.getValue();
            }
            builder.add(key, value);
        }
        requestBody = builder.build();
        //结果返回
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                deliverDataSuccess(result, callBack);
            }


        });
    }




    //-------------------------提交表单--------------------------

    public void postAsync(String url, Map<String, String> params, DataCallBack callBack) {
        getInstance().inner_postAsync(url, params, callBack);
    }

    private void inner_postAsync(String url, Map<String, String> params, final DataCallBack callBack) {

        RequestBody requestBody = null;
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("signature", Encrypt.getSignature());
        params.put("timeStamp", Encrypt.timeStamp);
        params.put("randomStr", Encrypt.randomStr);

        /**
         * 如果是3.0之前版本的，构建表单数据是下面的一句
         */
        //FormEncodingBuilder builder = new FormEncodingBuilder();

        /**
         * 3.0之后版本
         */
        FormBody.Builder builder = new FormBody.Builder();

        for (Map.Entry<String, String> map : params.entrySet()) {
            String key = map.getKey().toString();
            String value = null;
            if (map.getValue() == null) {
                value = "";
            } else {
                value = map.getValue();
            }
            builder.add(key, value);
        }
        requestBody = builder.build();
        //结果返回
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    HashMap<String,String> map = GsonUtils.getMapParams(result);
                    if(Constants.ERROR_CODE_200.equals(map.get("error"))){
                        deliverDataSuccess(map.get("data"), callBack);
                    } else {

                    }
                } catch (Exception e){

                }
            }


        });
    }


    /**
     * 编辑资料
     */

    public void editDataAsync(String url,File file,Map<String, String> params,DataCallBack callBack){

        getInstance().inner_editDataAsync(url,file,params,callBack);

    }

    private void inner_editDataAsync(String url, File file, Map<String, String> params, final DataCallBack callBack){

        if (params == null) {
            params = new HashMap<>();
        }
        params.put("signature", Encrypt.getSignature());
        params.put("timeStamp", Encrypt.timeStamp);
        params.put("randomStr", Encrypt.randomStr);

        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(file != null){
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("headimgurl", file.getName(), body);
        }
        if (params != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : params.entrySet()) {
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
            }
        }

        //创建一个请求
        final Request request = new Request.Builder().url(url).post(requestBody.build()).build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                deliverDataSuccess(result, callBack);
            }


        });
    }







    //-------------------------文件上传--------------------------

    public void uploadAsync1(String url,Map<String, Object> params,DataCallBack callBack){

        getInstance().inner_uploadAsync1(url,params,callBack);

    }

    private void inner_uploadAsync1(String url, Map<String, Object> params, final DataCallBack callBack){

        if (params == null) {
            params = new HashMap<>();
        }
        params.put("signature", Encrypt.getSignature());
        params.put("timeStamp", Encrypt.timeStamp);
        params.put("randomStr", Encrypt.randomStr);

        // form 表单形式上传
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        //追加参数
        for (String key : params.keySet()) {
            Object object = params.get(key);
            if (!(object instanceof File)) {
                builder.addFormDataPart(key, object.toString());
            } else {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("video/mp4"), file));
            }
        }

        //创建RequestBody
        RequestBody body = builder.build();

        //创建Request
        final Request request = new Request.Builder().url(url).post(body).build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                deliverDataSuccess(result, callBack);
            }


        });
    }


    public void uploadAsync(String url,File file,Map<String, String> params,DataCallBack callBack){

        getInstance().inner_uploadAsync(url,file,params,callBack);

    }


    private void inner_uploadAsync(String url, File file, Map<String, String> params, final DataCallBack callBack){

        if (params == null) {
            params = new HashMap<>();
        }
        params.put("signature", Encrypt.getSignature());
        params.put("timeStamp", Encrypt.timeStamp);
        params.put("randomStr", Encrypt.randomStr);

        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(file != null){
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("video/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("video", file.getName(), body);
        }
        if (params != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : params.entrySet()) {
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
            }
        }

        //创建一个请求
        final Request request = new Request.Builder().url(url).post(requestBody.build()).build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                deliverDataSuccess(result, callBack);
            }


        });
    }

    //-------------------------文件批量上传--------------------------

    /**
     * 文件 批量上传
     * @param url
     * @param callBack
     */
    public void applyUploadAsync(String url,Map<String, String> params,List<String> photoPaths , DataCallBack callBack){

        getInstance().inner_applyUploadAsync(url, params, photoPaths, callBack);

    }

    public void inner_applyUploadAsync(String url, Map<String, String> params , List<String> photoPaths , final DataCallBack callBack){


        if (params == null) {
            params = new HashMap<>();
        }
        params.put("signature", Encrypt.getSignature());
        params.put("timeStamp", Encrypt.timeStamp);
        params.put("randomStr", Encrypt.randomStr);

        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (params != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : params.entrySet()) {
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
            }

            //遍历paths中所有图片绝对路径到builder，并约定key如“upload”作为后台接受多张图片的key
//            for (String path : photoPaths) {
//                RequestBody body = RequestBody.create(MediaType.parse("image/*"), new File(path));
//                requestBody.addFormDataPart("image", path, body);
////                requestBody.addFormDataPart("image", null, RequestBody.create(MediaType.parse("image/*"), new File(path)));
//            }
            for(int i  = 0 ; i < photoPaths.size() ; i ++ ){
                String path = photoPaths.get(i);
                RequestBody body = RequestBody.create(MediaType.parse("image/*"), new File(path));
                requestBody.addFormDataPart("image_"+i, path, body);
            }

        }

        //创建一个请求
        final Request request = new Request.Builder().url(url).post(requestBody.build()).build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                deliverDataSuccess(result, callBack);
            }


        });
    }


    //-------------------------文件下载--------------------------
    public void downloadAsync(String url, String desDir, DataCallBack callBack) {
        getInstance().inner_downloadAsync(url, desDir, callBack);
    }


    /**
     * 下载文件的内部逻辑处理类
     *
     * @param url      下载地址
     * @param desDir   目标地址
     * @param callBack
     */
    private void inner_downloadAsync(final String url, final String desDir, final DataCallBack callBack) {
        final Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                /**
                 * 在这里进行文件的下载处理
                 */
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    //文件名和目标地址
                    File file = new File(desDir, getFileName(url));
                    //把请求回来的response对象装换为字节流
                    inputStream = response.body().byteStream();
                    fileOutputStream = new FileOutputStream(file);
                    int len = 0;
                    byte[] bytes = new byte[2048];
                    //循环读取数据
                    while ((len = inputStream.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, len);
                    }
                    //关闭文件输出流
                    fileOutputStream.flush();
                    //调用分发数据成功的方法
                    deliverDataSuccess(file.getAbsolutePath(), callBack);
                } catch (IOException e) {
                    //如果失败，调用此方法
                    deliverDataFailure(request, e, callBack);
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }

                }
            }

        });
    }

    /**
     * 根据文件url获取文件的路径名字
     */
    private String getFileName(String url) {
        int separatorIndex = url.lastIndexOf("/");
        String path = (separatorIndex < 0) ? url : url.substring(separatorIndex + 1, url.length());
        return path;
    }



    /**
     * 数据回调接口
     */
    public interface DataCallBack {
        //请求成功
        void requestSuccess(String result) throws Exception;
        //请求失败
        void requestFailure(Request request, IOException e);
    }

}
