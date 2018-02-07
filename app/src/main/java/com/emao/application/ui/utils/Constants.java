package com.emao.application.ui.utils;

import android.net.Uri;

/**
 *
 * @author keybon
 */

public class Constants {

    public static final int TYPE_ITEM_RECOMMEND = 0;
    public static final int TYPE_ITEM_CHOICENSS = 1;
    public static final int TYPE_ITEM_LIVE = 2;
    public static final int TYPE_ITEM_FOCUS = 3;
    public static final int TYPE_ITEM_VIDEO = 4;
    public static final int TYPE_ITEM_MUSIC = 5;
    public static final int TYPE_ITEM_DYNAMIC = 6;
    public static final int TYPE_ITEM_FOCUS_FOCUS = 7;
    public static final int TYPE_ITEM_LETTER = 8;

    public static final String KEY = "_api";
    public static final String SP_FILE_NAME = "GOOSE_%SETTING%_ID_";
    public static final String FILE_NAME_PATH = "/Goose";
    public static final String FILE_NAME_PATH_ = "/Data";

    public static final String ERROR_CODE_200 = "200";
    public static final String ERROR_CODE_201 = "201";
    public static final String ERROR_CODE_202 = "202";
    public static final String ERROR_CODE_209 = "209";


    // 5 分钟
    public static final int INTERVAL = 5 * 60 * 1000;
    // 消息时间显示间隔条数
    public static final int TIMECOUNT = 24;

    public static final Uri uri = Uri.parse("http://imgsrc.baidu.com/imgad/pic/item/4ec2d5628535e5dd89f09fe37dc6a7efce1b6229.jpg");
    public static final Uri uri1 = Uri.parse("http://mpic.tiankong.com/588/a23/588a23777c96d22595f980e6fc6247fc/640.jpg");



    public static final String uri2 = "http://mpic.tiankong.com/588/a23/588a23777c96d22595f980e6fc6247fc/640.jpg";

    public static final String TYPE_TEXT = "text";
    public static final String TYPE_IMAGE = "img";
    public static final String TYPE_URL = "webpage";

    public static final int WECHAT_IMG_THUMB_SIZE = 150;

    public static String PARAM_TARGET_URL;
    public static String PARAM_TITLE;
    public static String PARAM_IMAGE_URL;
    public static String PARAM_SUMMARY;
    public static String PARAM_APPNAME;
    public static String PARAM_APP_SOURCE;

    public static final String PREFERENCES_NAME = "weibo_token";
    public static final String KEY_UID = "uid";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_EXPIRES_IN = "expires_in";

    public static final String COLLECTION_ALI_TYPE = "alipay_type";
    public static final String COLLECTION_WECHAT_TYPE = "wechat_type";

    /**
     * 分享链接
     *  getString(GOOSE_SHARE_URL, id)   id 自己的id
     */
//    public static final String GOOSE_SHARE_URL = "http://feather.xiaojidiguo.com/wish/info/%1$s.html";
    public static final String GOOSE_SHARE_URL = "http://eh.emaoapp.com/wish/info/%1$s.html";
    /**
     * 相册相关
     */
    public static final int REQUEST_CODE_PROCESS_IMAGE_BATCH = 0x11;

    public static final String GOOSE_IMAGE_URL1 = "http://lonnyk.lanqiulm.com/";
    public static final String GOOSE_IMAGE_URL2 = "http://featherapi.xiaojidiguo.com/";
    public static final String GOOSE_IMAGE_URL = "http://apo.emaoapp.com/";


    /**
     * 视频
     */
    public static final String GOOSE_VIDEO_URL1 = "http://lonnyk.lanqiulm.com/";
    public static final String GOOSE_VIDEO_URL2 = "http://featherapi.xiaojidiguo.com/";
    public static final String GOOSE_VIDEO_URL = "http://apo.emaoapp.com/";


    /**
     * 接口前缀
     */
    public static final String GOOSE_BASE_URL2 = "http://featherapi.xiaojidiguo.com";
    public static final String GOOSE_BASE_URL1 = "http://lonnyk.lanqiulm.com";
    public static final String GOOSE_BASE_URL = "http://apo.emaoapp.com";
    /**
     * 微信支付
     */
    public static final String GOOSE_URL_WECHAT_PAY = "http://wxpay.wxutil.com/pub_v2/app/app_pay.php";
//    public static final String GOOSE_URL_WECHAT_PAY = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    /**
     * 微信支付接口  uid ：用户id  cid ：打赏id 我自己    price   type    sourse
     */
    public static final String GOOSE_URL_WECHAT_PAY_INSERT = GOOSE_BASE_URL+"/order/insertorder";
    /**
     * 启动背景图
     */
    public static final String GOOSE_URL_LAUNCHER_BG = GOOSE_BASE_URL+"/start/data";
    /**
     * 验证码
     */
    public static final String GOOSE_URL_MOBILE_LOGIN_VERIFYCODE = GOOSE_BASE_URL+"/user/sms";
    /**
     * banner
     */
    public static final String GOOSE_URL_BANNER = GOOSE_BASE_URL+"/banner/getbanner";
    /**
     * 微信登录
     */
    public static final String GOOSE_URL_WECHAT_LOGIN = GOOSE_BASE_URL+"/user/weixinuser";
    /**
     * 分类接口
     */
    public static final String GOOSE_URL_MAIN_CLASSIFY = GOOSE_BASE_URL+"/category/cate";
    /**
     * 文章查询接口
     */
    public static final String GOOSE_URL_CONTENT_DETAILS = GOOSE_BASE_URL+"/dynamic/selectdynamic";
    /**
     * 文章点赞
     */
    public static final String GOOSE_URL_CONTENT_ADMIRE = GOOSE_BASE_URL+"/give/selectgive";
    /**
     * 文章评论查询接口
     */
    public static final String GOOSE_URL_CONTENT_OPINION = GOOSE_BASE_URL+"/comment/selectcomment";
    /**
     * 文章打赏
     */
    public static final String GOOSE_URL_CONTENT_REWARD = GOOSE_BASE_URL+"/reward/selectreward";
    /**
     * 文章打赏总额
     */
    public static final String GOOSE_URL_CONTENT_REWARD_ALL = GOOSE_BASE_URL+"/reward/countreward";
    /**
     * 文章点赞插入接口
     */
    public static final String GOOSE_URL_CONTENT_ADMIRE_ADD = GOOSE_BASE_URL+"/give/insertgive";
    /**
     * 文章评论插入接口
     */
    public static final String GOOSE_URL_CONTENT_OPINION_ADD = GOOSE_BASE_URL+"/comment/insertcomment";
    /**
     * 文章打赏插入接口
     */
    public static final String GOOSE_URL_CONTENT_REWARD_ADD = GOOSE_BASE_URL+"/reward/insertreward";
    /**
     * 发表文章接口
     */
    public static final String GOOSE_URL_CONTENT_PUBLISH = GOOSE_BASE_URL+"/dynamic/insertdynamic";
    /**
     * 关注查询接口
     */
    public static final String GOOSE_URL_CONTENT_ATTENTION_SELECT = GOOSE_BASE_URL+"/follow/selectfollow";
    /**
     * 取消关注接口
     */
    public static final String GOOSE_URL_CONTENT_ATTENTION_DELETE = GOOSE_BASE_URL+"/follow/upfollow";
    /**
     * 关注插入接口
     */
    public static final String GOOSE_URL_CONTENT_ATTENTION = GOOSE_BASE_URL+"/follow/insertfollow";
    /**
     * 私信插入接口
     */
    public static final String GOOSE_URL_CONTENT_LETTER_ADD = GOOSE_BASE_URL+"/letter/insertletter";
    /**
     * 聊天插入接口
     */
    public static final String GOOSE_URL_CONTENT_SINGLE_CHAT_ADD = GOOSE_BASE_URL+"/letter/insertletterde";
    /**
     * 聊天查询接口
     */
    public static final String GOOSE_URL_CONTENT_SINGLE_CHAT_SELECT = GOOSE_BASE_URL+"/letter/selectletterde";
    /**
     * 私信查询接口
     */
    public static final String GOOSE_URL_CONTENT_SELECT_LETTER = GOOSE_BASE_URL+"/letter/selectletter";
    /**
     * 私信回复接口
     */
    public static final String GOOSE_URL_CONTENT_LETTER_REPLAY = GOOSE_BASE_URL+"/letter/upletter";
    /**
     * 举报插入接口
     */
    public static final String GOOSE_URL_CONTENT_REPORT = GOOSE_BASE_URL+"/report/insertreport";
    /**
     * 编辑资料接口
     */
    public static final String GOOSE_URL_CONTENT_EDIT_DATA = GOOSE_BASE_URL+"/user/updateuser";
    /**
     * 反馈接口
     */
    public static final String GOOSE_URL_CONTENT_FEED_BACK = GOOSE_BASE_URL+"/feedback/insertfeedback";
    /**
     * 关注页签动态查询
     */
    public static final String GOOSE_URL_CONTENT_FOCUS_DYNAMIC = GOOSE_BASE_URL+"/follow/gzfollow";
    /**
     * 手机登录
     */
    public static final String GOOSE_URL_CONTENT_MOBILE_LOGIN = GOOSE_BASE_URL+"/user/login";
    /**
     * 支付宝授权
     */
    public static final String GOOSE_URL_ALIPAY_AUTH = GOOSE_BASE_URL+"/reward/zfb";
    /**
     * 用户信息查询
     */
    public static final String GOOSE_URL_SELECT_USER = GOOSE_BASE_URL+"/user/selectuser";
    /**
     * 查询用户 关注人数 跟 粉丝人数
     */
    public static final String GOOSE_URL_SELECT_FOCUS_FANS = GOOSE_BASE_URL+"/follow/countfollow";
}
