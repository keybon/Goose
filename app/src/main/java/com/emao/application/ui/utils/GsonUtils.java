package com.emao.application.ui.utils;

import com.emao.application.ui.bean.BannerBean;
import com.emao.application.ui.bean.ClassifyBean;
import com.emao.application.ui.bean.ContentDetailsBean;
import com.emao.application.ui.bean.FocusTableAttentionBean;
import com.emao.application.ui.bean.FocusTableLetterBean;
import com.emao.application.ui.bean.OpinionBean;
import com.emao.application.ui.bean.RecommendFriendBean;
import com.emao.application.ui.bean.RewardBean;
import com.emao.application.ui.bean.SingleChatBean;
import com.emao.application.ui.bean.User;
import com.emao.application.ui.bean.UserData;
import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author keybon
 */

public class GsonUtils {


    public static HashMap<String,String> getMapParams(String result){

        try {

            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            HashMap<String,String> params = gson.fromJson(result, type);

            return params;

        } catch (Exception e){
            HashMap<String,String> map = new HashMap<>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                map.put("msg",jsonObject.getString("msg"));
                return map;

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            return map;
        }
    }

    public static <T> T getClassParams(String json, Class<T> classOfT){
        Gson gson = new Gson();
        Object object = gson.fromJson((String)json, (Type)classOfT);
        return Primitives.wrap(classOfT).cast(object);
    }


    public static List<RecommendFriendBean> getRecommendParams(String string){
        Gson gson = new Gson();
        Type type = new TypeToken<List<RecommendFriendBean>>() {
        }.getType();
        return gson.fromJson(string,type);
    }

    public static List<UserData> getUserDataParams(String string){
        Gson gson = new Gson();
        Type type = new TypeToken<List<UserData>>() {
        }.getType();
        return gson.fromJson(string,type);
    }

    public static List<ClassifyBean> getClassifyParams(String string){
        Gson gson = new Gson();
        Type type = new TypeToken<List<ClassifyBean>>() {
        }.getType();
        return gson.fromJson(string,type);
    }

    public static List<BannerBean> getBannerParams(String string){
        Gson gson = new Gson();
        Type type = new TypeToken<List<BannerBean>>() {
        }.getType();
        return gson.fromJson(string,type);
    }

    public static List<ContentDetailsBean> getContentDetailsParams(String string){
        Gson gson = new Gson();
        Type type = new TypeToken<List<ContentDetailsBean>>() {
        }.getType();
        return gson.fromJson(string,type);
    }

    public static List<OpinionBean> getContentOpinionParams(String string){
        Gson gson = new Gson();
        Type type = new TypeToken<List<OpinionBean>>() {
        }.getType();
        return gson.fromJson(string,type);
    }

    public static List<FocusTableLetterBean> getLetterBeanParams(String string){
        Gson gson = new Gson();
        Type type = new TypeToken<List<FocusTableLetterBean>>() {
        }.getType();
        return gson.fromJson(string,type);
    }

    public static List<RewardBean> getRewardParams(String string){
        Gson gson = new Gson();
        Type type = new TypeToken<List<RewardBean>>() {
        }.getType();
        return gson.fromJson(string,type);
    }

    public static List<FocusTableAttentionBean> getAttentionParams(String string){
        Gson gson = new Gson();
        Type type = new TypeToken<List<FocusTableAttentionBean>>() {
        }.getType();
        return gson.fromJson(string,type);
    }

    public static List<SingleChatBean> getSingheChatParams(String string){
        Gson gson = new Gson();
        Type type = new TypeToken<List<SingleChatBean>>() {
        }.getType();
        return gson.fromJson(string,type);
    }

    public static List<User> getUserParams(String string){
        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>() {
        }.getType();
        return gson.fromJson(string,type);
    }
}
