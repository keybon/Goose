package com.emao.application.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.emao.application.R;
import com.emao.application.ui.fragment.BaseFragment;
import com.emao.application.ui.fragment.MenuFocusFragment;
import com.emao.application.ui.fragment.MenuMessageFragment;
import com.emao.application.ui.fragment.MenuMineFragment;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.emao.application.ui.view.ShowPopWinFactor;
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity;
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;



/**
 * @author keybon
 */
public class MainActivity extends BaseActivity implements OnLoadmoreListener,OnRefreshListener,View.OnClickListener{


    private static final int BOTTOM_NAVIGATION = 1;
    private static final int INTENT_TYPE1 = 1;
    private static final int INTENT_TYPE2 = 2;
    private static final String TAG_MESSAGE = "tag_message";
    private static final String TAG_MENU_FOCUS = "tag_menu_focus";
    private static final String TAG_MINE = "tag_mine";


    private FragmentManager mFragmentManager;
    private BottomNavigationView bottomNavigationView;
    private BaseFragment mCurrentFragment;
    private Activity baseActivity;

    private SmartRefreshLayout refreshLayout;
    /** pop */
    private ShowPopWinFactor popupWindow;


    private int tabIdx;

    public static Bitmap contentBitmap = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BOTTOM_NAVIGATION:
                    showFragment(msg.arg1);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化view
     */
    @Override
    public void initView(){

//        SharedPreUtils.put(SharedPreUtils.SP_USERID,"17");
        baseActivity = this;

        mFragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.navigation_view);
        refreshLayout = findViewById(R.id.main_smart_refresh);

        new Thread(new Runnable() {
            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inPreferredConfig = Bitmap.Config.RGB_565;

                contentBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.copylink_big);

            }
        }).start();
    }

    @Override
    public void initData(){

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setEnableAutoLoadmore(false);
        refreshLayout.setFooterTriggerRate(1.3f);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Message msg = mHandler.obtainMessage(BOTTOM_NAVIGATION);
                msg.arg1 = item.getItemId();
                mHandler.removeMessages(BOTTOM_NAVIGATION);
                mHandler.sendMessageDelayed(msg, 150L);
                return true;
            }
        });
        // 长按
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(MenuItem item) {

            }
        });

        // 根据tabIdx判断显示哪个页签
        tabIdx = getIntent().getIntExtra("tabIdx", 0);
        int checkId;
        switch (tabIdx) {
            case 1:
                checkId = R.id.navigation_focus;
                break;
            case 2:
                checkId = R.id.navigation_mine;
                break;
            case 0:
            default:
                checkId = R.id.navigation_message;
                break;
        }
        showFragment(checkId);
        setBackBtnLeft(false);
        setBackBtnRight(true);


//        if(!Constants.COLLECTION_ALI_TYPE.equals(SharedPreUtils.get(SharedPreUtils.SP_COLLECTION,""))){
//            DialogFactory.OKButCancelDialog(this, "请在设置里设置您的收款方式，避免打赏后不能转到贵账户。", "确定", "取消", new OnDialogCallBackListener() {
//                @Override
//                public void OnOkClick() {
//                    showFragment(R.id.navigation_mine);
//                    SharedPreUtils.put(SharedPreUtils.SP_COLLECTION, Constants.COLLECTION_ALI_TYPE);
//                    bottomNavigationView.setSelectedItemId(R.id.navigation_mine);
//                }
//            });
//        }
    }

    private void showFragment(int id) {
        String tag = null;
        switch (id) {
            case R.id.navigation_message:
                tag = TAG_MESSAGE;
                break;
            case R.id.navigation_focus:
                tag = TAG_MENU_FOCUS;
                break;
            case R.id.navigation_mine:
                tag = TAG_MINE;
                break;
            default:
                    break;
        }
        if(TextUtils.isEmpty(tag)) {return;}

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        BaseFragment fragment = (BaseFragment) mFragmentManager.findFragmentByTag(tag);
        if (mCurrentFragment != null && mCurrentFragment.isVisible()) {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.hide(mCurrentFragment);
        }
        if(fragment == null) {
            if (tag.equals(TAG_MESSAGE)) {
                fragment = MenuMessageFragment.newInstance();
            }
            else if (tag.equals(TAG_MENU_FOCUS)) {
                fragment = MenuFocusFragment.newInstance();
            }
            else if (tag.equals(TAG_MINE)) {
                fragment = new MenuMineFragment();
            }
            transaction.add(R.id.content, fragment, tag);
        }
        else {
            transaction.show(fragment);
        }
        mCurrentFragment = fragment;
        initShowTitle(tag);
        transaction.commit();
    }

    private void initShowTitle(String Tag){
        switch (Tag) {
            case TAG_MESSAGE:
                setActionBarTitle(baseActivity.getResources().getString(R.string.actionbar_message));
                setBackBtnRight(true);
                actionBar.setElevation(15);
                refreshLayout.setEnableLoadmore(true);
                refreshLayout.setEnableRefresh(true);
                break;
            case TAG_MENU_FOCUS:
                setActionBarTitle(baseActivity.getResources().getString(R.string.actionbar_focus));
                setBackBtnRight(false);
                actionBar.setElevation(15);
                refreshLayout.setEnableLoadmore(true);
                refreshLayout.setEnableRefresh(true);
                break;
            case TAG_MINE:
                setActionBarTitle(baseActivity.getResources().getString(R.string.actionbar_mine));
                setBackBtnRight(false);
                actionBar.setElevation(0);
                refreshLayout.setEnableLoadmore(false);
                refreshLayout.setEnableRefresh(false);
                break;
            default:
                break;
        }
        setBackBtnLeft(false);
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        if(CommonUtils.isNetworkConnected(this)){
            if(mCurrentFragment instanceof MenuMessageFragment){
                mCurrentFragment.onMessageLoadData(refreshlayout);
            } else if(mCurrentFragment instanceof MenuFocusFragment){
                mCurrentFragment.onFocusLoadData(refreshlayout);
            }
        } else {
            ToastUtils.showShortToast(CommonUtils.getResourceString(R.string.network_hint));
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if(CommonUtils.isNetworkConnected(this)){
            if(mCurrentFragment instanceof MenuMessageFragment){
                mCurrentFragment.onMessageRefreshData(refreshlayout);
            } else if(mCurrentFragment instanceof MenuFocusFragment){
                mCurrentFragment.onFocusRefreshData(refreshlayout);
            }
        } else {
            refreshlayout.finishRefresh(1000);
        }
    }

    @Override
    public void compilePopWindow(View view) {
        showBottomPop(view);
//        showTemplatePop(view,ReleaseContentActivity.PICTURE_TYPE);
    }

    /**
     * 全屏 从底部弹起
     */
    public void showBottomPop(View view) {
        if(popupWindow == null){
            popupWindow = new ShowPopWinFactor(this);
        }
        popupWindow.showBottomPop(this);
    }

    /**
     * 选择模版
     */
    public void showTemplatePop(View view,final int activity_flag){
        if(popupWindow == null){
            popupWindow = new ShowPopWinFactor(this);
        }
        popupWindow.showTemplatePop(activity_flag, this, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                thisIntent(INTENT_TYPE1,activity_flag);
                dimissPop();
            }
        });
        popupWindow.showAtLocation(findViewById(R.id.main_activity), Gravity.BOTTOM,0,0);
    }

    public void dimissPop(){
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_compile_picture:
                dimissPop();
                showTemplatePop(v,ReleaseContentActivity.PICTURE_TYPE);
                break;
            case R.id.menu_compile_video:
                dimissPop();
                showTemplatePop(v,ReleaseContentActivity.VIDEO_TYPE);
                break;
            case R.id.menu_compile_close:
                dimissPop();
                break;
            case R.id.actionbar_back_left:
                dimissPop();
                break;
            default:
                break;
        }
    }

    private void thisIntent(int type,int activity_flag){
        switch (type){
            case INTENT_TYPE1:
                if(activity_flag == ReleaseContentActivity.PICTURE_TYPE){
                    jumpToPhoto();
                } else if (activity_flag == ReleaseContentActivity.VIDEO_TYPE){
                    jumpToVideo();
                }
                break;
            case INTENT_TYPE2:
                break;
            default:
                break;
        }
    }


    private void jumpToVideo(){

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                ) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

//                startActivity(new Intent(MainActivity.this,VideoRecordActivity.class));
                startVideoConfig();

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, ReleaseContentActivity.REQUEST_CODE_RECORD_AUDIO);
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA}, ReleaseContentActivity.REQUEST_CODE_RECORD_AUDIO);
        }

    }


    /**
     * 录制完成后需要跳转的activity
     */
    public final static String OVER_ACTIVITY_NAME = "over_activity_name";
    /**
     * 录制配置key
     */
    public final static String MEDIA_RECORDER_CONFIG_KEY = "media_recorder_config_key";

    private void startVideoConfig(){

        // 录制
        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()

                .fullScreen(false)
                .smallVideoWidth(360)
                .smallVideoHeight(480)
                .recordTimeMax(10*1000)
                .recordTimeMin(1000)
                .maxFrameRate(20)
                .videoBitrate(600000)
                .captureThumbnailsTime(1)
                .build();

        Intent intent = new Intent(this, MediaRecorderActivity.class);
        intent.putExtra(OVER_ACTIVITY_NAME, SendSmallVideoActivity.class.getName());
        intent.putExtra(MEDIA_RECORDER_CONFIG_KEY, config);
        startActivityForResult(intent,VideoRecordActivity.REQUEST_VIDEO);
    }

    private void jumpToPhoto(){
//        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//
//            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//
//                Intent intent = new Intent(MainActivity.this, PhotoBrowserActivity.class);
//                intent.putExtra(PhotoBrowserActivity.PHOTO_MAX_SIZE, 9);
//                startActivity(intent);
//
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, ReleaseContentActivity.REQUEST_CODE_CAMERA);
//            }
//
//        } else {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, ReleaseContentActivity.REQUEST_CODE_CAMERA);
//        }
        Intent intent = new Intent(MainActivity.this, ReleaseContentActivity.class);
        intent.putExtra(ReleaseContentActivity.RELEASE_MAXNUM, 9);
        intent.putExtra(ReleaseContentActivity.RELEASE_FLAG, ReleaseContentActivity.PICTURE_TYPE);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int results : grantResults){
            if(requestCode == ReleaseContentActivity.REQUEST_CODE_CAMERA && results != PackageManager.PERMISSION_GRANTED) {
                ToastUtils.showShortToast("请开启相机权限");
            }
            if(requestCode == ReleaseContentActivity.REQUEST_CODE_RECORD_AUDIO && results != PackageManager.PERMISSION_GRANTED) {
                ToastUtils.showShortToast("请开启相机、麦克风权限");
            }
        }
        if(requestCode == ReleaseContentActivity.REQUEST_CODE_CAMERA){
            jumpToPhoto();
        } else if(requestCode == ReleaseContentActivity.REQUEST_CODE_RECORD_AUDIO){
            jumpToVideo();
        }
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                if (!this.isFinishing()) {
                    ToastUtils.showShortToast("再按一次退出程序");
                }
                exitTime = System.currentTimeMillis();
            } else {
                backKeyPress();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == VideoRecordActivity.REQUEST_VIDEO) {
                if(data != null){
                    Intent intent = data;
                    intent.setClass(this,ReleaseContentActivity.class);
                    startActivity(data);
                } else if (requestCode == VideoRecordActivity.REQUEST_VIDEO_CANCEL) {

                } else {
//                    ToastUtils.showShortToast("系统异常，请重新录制～");
                }
            }
        } catch (Exception e){
            LogUtils.e("录制视频有问题 "+e.toString());
        }
    }
}
