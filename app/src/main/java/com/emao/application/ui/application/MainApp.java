package com.emao.application.ui.application;

import android.app.Application;
import android.content.Context;

import com.emao.application.R;
import com.emao.application.ui.bean.ClassifyBean;
import com.emao.application.ui.utils.CommonUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.mabeijianxi.smallvideorecord2.JianXiCamera;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author keybon
 */

public class MainApp extends Application {

    public static Application IMApp;
    public static Context context;
    public static final String WECHAT_APP_ID = "wx3d61664e1db530ea";
    public static final String WECHAT_APP_SECRET = "de325dca47e10f5a9f075ed80fa4eec2";

    public static final String QQ_APP_ID = "1106530044";
    public static final String QQ_APP_KEY = "jkuaUsRtmEiJR5bW";

    public static final String WEIBO_APP_ID = "1724220894";
    public static final String WEIBO_APP_SECRET = "18fe3daa3effeadd8f2f9f097e3171ea";
    public static List<ClassifyBean> titlesList = new ArrayList<>();

    /**
     *
     * PID：
     支付宝支付业务参数，这个东西不好找，具体为：“首页”-》“右上角个人中心”-》“左边mapi网关产品秘钥”可查看。

     APPID：
     应用的appid。

     TARGET_ID：
     不重复的一个数即可，可用时间戳。

     rsa2：
     推荐用RSA加密参数，此时rsa2=true；

     RSA_PRIVATE 支付宝公钥

     RSA2_PRIVATE：
     商户私钥。私钥需要通过官方提供的工具生成。
     */
    public static final String ALIPAY_APP_ID = "2017102109431433";
    public static final String PID = "2088102123816631";
    public static final String TARGET_ID = String.valueOf(System.currentTimeMillis());


    /**
     * 商户私钥
     */
    public static final String RSA_PRIVATE = "MIIEpAIBAAKCAQEAtS4+gQOZmNMfnxIXm3NyQD6dfhmyB7QK4VmsW743j5EkS8b3y3r8Xz58qsL9kXEfWjl+VYJwoh/ezkmFin0oacv1W/xLH07hJyGB96f2ffxU4o2es+lWnyjxZNv3Zh+I5J3Q0sbHjn28iMibL1WJYYBPhsvIpQT7OlG0p/7mDME/0IQM8XAnmUADbLqBxDag3vZClXIc59mrxBhQngSwXdCJcc8N8A+UE/AkZrOnAkafk/a3uvBV//HSP8Nxd0ovD6uDMLxpaQPow5DTz//wSHTfQ6P9Gm8ce9kb56Xa+ZBgfx7fbnryDcjrgXFCZXdb3wVwLJQFS1457Nzy0SgUAQIDAQABAoIBADUpkpGMmt8q49ZrGp40a8vKE7BSndiAzch95YMt92Oa9Ql/ImqEddUEotQATyMEDG0O/bDdi2sm6tug2xxdySPyAtaw2uIe5RX0UQx4Lm2dQtMPNwgXaaFeBCbSHG/MxQBABmwyg4+d+Voxcsea06rWWX92ADPbmFPucZHxy1LmSwvXewExWlmhDYfZBW3DLuh2FG/34FfRGZeH+9L6Q2LtDdHbUZJXVXPD6e4nZh9tMgOK770gZitbu9H2kBitAw9wn/6e/+jmRYmCLEZv8wkk5LPPWgy8WpT9Yn78P5xFEklJxjtPr97G/xVuBWZj5petrNZknAhD0DFtqCgf7dkCgYEA2dzB0XWXjT9m2GZzeI184PQEzl0yOIukkutAd/+WD4VOGly0IS/ABiv3BDvgFI8i8O45y2QlAkqeeYuNABe7ZH/rB627ifIog3jG48el6cVjHGTBBMlUFFjtScBXVHB0VZWFdz/Ow/Xpu9iHE0y5wBfKXsJA5le/l0ddPSIqqgsCgYEA1OWj8snTfzvL64MCMXXfQWZxlqUx9oXGzBrupVOCD0wcE8XhZUmT2bFtRKtaxT1JNFJxLzhpyDkKguHQ+VS7thSx7RPiPzxpnvcHvL6ftdLP+QREeQw/b/TOl+tqxEyAdwg3npbpKY+RCx/jMnj5E2kHFsrtg5UuwC8Lw1hZzaMCgYEArymC27nEA9i6A7s7k+2OPf9z7ceYnUYJJ+blipWAPTJcvW1Tlmsh5NRsYocRfvYmjbe71//IkbMU/3xn/W8XR5JhkWbppduBH1N39aOLjg0ZxrZrSkHkzAdDoUx36ngP0M/K4zn2MDADoPhSgf3qSrvWBaIBNDHQ5PyHxqM10kkCgYBgzID81TNquF+Bl7GZl4SJEfE3k6jh8/VtvBkh5pROLEbf3qsbCJdG9ds+y8Kbx7/PIGUCGV5PgU/8Lnx7wUEQCtzTdKT8J5XGh39nHC012MJi8MPJXf5CA0cj4BBNbJNEi+HuI6OkF/jU3AzLWCG355sni+DuYRg+naV1jmhWjQKBgQCLmyFG2E0PM79NOYEXjmkge7FkxXulTkIFxhirStlX7lXvfpXdOuSLfiKW1FjrnRxwDqOwi4G+jF4VGO2GwI9ZTB/ADN9xPCkDC6oC5PxCVRuoZQMNg2lMW13hNmSnXZ3MrvkTv3kI/hHFApNFma4guzV6AbryWRW1YS8T69oukw==";
    public static final String RSA2_PRIVATE = "";

    public static final int SDK_PAY_FLAG = 1;
    public static final int SDK_AUTH_FLAG = 2;
    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     *
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String WEIBO_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    /**
     * 微博SDK的接口使用权限, 有一些高级接口是需要进行申请的.
     */
    public static final String WEIBO_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";



    @Override
    public void onCreate() {
        super.onCreate();

        IMApp = this;
        context = this;

        initRefreshConfig();
        initFrescoConfig();
        initSmallVideo();
    }

    private void initRefreshConfig(){
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                //全局设置主题颜色
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
                //指定为经典Header，默认是 贝塞尔雷达Header
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }
    public void initFrescoConfig(){
        ImagePipelineConfig frescoConfig = ImagePipelineConfig.newBuilder(getApplicationContext()) .setDownsampleEnabled(true).build();
        Fresco.initialize(this,frescoConfig);
    }


    public void initSmallVideo() {
        // 设置拍摄视频缓存路径
        JianXiCamera.setVideoCachePath(CommonUtils.getVideoPath());
        // 初始化拍摄SDK，必须
        JianXiCamera.initialize(false,null);
    }

}
