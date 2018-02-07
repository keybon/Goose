package com.emao.application.http;

import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.LogUtils;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;

/**
 *
 * @author keybon
 */

public class HttpService {



    public static void addAdmire(HashMap<String, String> params, final HttpCallBack callBack){

        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_ADMIRE_ADD, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {

                callBack.OnSuccessCallBack(result);

            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("点赞接口失败 == "+ request.toString());
                callBack.OnFailedCallBack(request.toString());
            }
        });
    }

    public static void sendOpinion(HashMap<String, String> params, final HttpCallBack callBack){

        OkHttpManager.getInstance().nomalPostAsync(Constants.GOOSE_URL_CONTENT_OPINION_ADD, params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {

                callBack.OnSuccessCallBack(result);

            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("评论失败 == "+ request.toString());
                callBack.OnFailedCallBack(request.toString());
            }
        });
    }
}
