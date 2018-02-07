package com.emao.application.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.http.OkHttpManager;
import com.emao.application.ui.adapter.PictureReleaseAdapter;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.bean.ClassifyBean;
import com.emao.application.ui.callback.OnDialogCallBackListener;
import com.emao.application.ui.callback.OnPayCallBackListener;
import com.emao.application.ui.callback.OnRecyclerViewItemClickListener;
import com.emao.application.ui.photo.PhotoBrowserActivity;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.GsonUtils;
import com.emao.application.ui.utils.LogUtils;
import com.emao.application.ui.utils.SharedPreUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.emao.application.ui.view.DialogFactory;
import com.emao.application.ui.view.LabelsView;
import com.emao.application.ui.view.ShowPopWinFactor;
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity;
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Request;

/**
 *
 * @author keybon
 */

public class ReleaseContentActivity extends BaseActivity {

    public static final int PICTURE_TYPE = 1;
    public static final int VIDEO_TYPE = 2;
    public static final int REQUEST_CODE_CAMERA = 4;
    public static final int REQUEST_CODE_RECORD_AUDIO = 5;
    public static final String RELEASE_FLAG = "RELEASE_FLAG";
    public static final String RELEASE_MAXNUM = "RELEASE_MAXNUM";

    private ArrayList<String> drawables;
    private TextView numText;
    private EditText picture_wish_text;
    private int maxNum = 0 ;
    private int style;

    private LabelsView labelsView;
    private String checkid = "1";

    private RelativeLayout video_rl;
    private JZVideoPlayerStandard videoplayer;
    private ImageView videoPlay;
    private String videoPath;
    private TextView replayTv;


    private RecyclerView recyclerView;
    private PictureReleaseAdapter recycAdapter;
    private GridLayoutManager mLayoutManager;
    private ItemTouchHelper itemTouchHelper;

    private ShowPopWinFactor popupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_release_content);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();
        setActionBarTitle(CommonUtils.getResourceString(R.string.picture_release_title));
        setTextViewRight(true);

        style = getIntent().getIntExtra(RELEASE_FLAG,0);

        numText = findViewById(R.id.picture_num_text);
        picture_wish_text = findViewById(R.id.picture_wish_text);

        video_rl = findViewById(R.id.release_video_rl);
        videoplayer = findViewById(R.id.release_videoplayer);
        videoPlay = findViewById(R.id.release_video_play);
        recyclerView = findViewById(R.id.picture_recycleview);
        replayTv = findViewById(R.id.release_video_play_tv);
        labelsView = findViewById(R.id.release_labels);

        initLabeLsView();

        if( PICTURE_TYPE == style ){
            recyclerView.setVisibility(View.VISIBLE);
            video_rl.setVisibility(View.GONE);
            maxNum = getIntent().getIntExtra(RELEASE_MAXNUM,0);
            drawables= getIntent().getStringArrayListExtra(PhotoBrowserActivity.SELECTED_DATA);
            if(drawables == null){
                drawables = new ArrayList<>();
            }
            initPhotoView();
        } else {
            videoPath = getIntent().getStringExtra(VideoRecordActivity.REQUEST_VIDEO_PATH);
            recyclerView.setVisibility(View.GONE);
            video_rl.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(videoPath)){
                videoplayer.setUp(videoPath,JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
            } else {
                replayTv.setText("点击录制视频");
            }
            replayTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startVideoConfig();
                }
            });
        }


    }

    @Override
    public void initData() {
        super.initData();

        numText.setText(getString(R.string.picture_init_text,0));

        if( PICTURE_TYPE == style ){
            initPhotoData();
        } else {
            initVideoData();
        }
        picture_wish_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() <= 500){
                    numText.setText(getString(R.string.picture_init_text,s.length()));
                } else {
                    ToastUtils.showShortToast(R.string.text_num_above);
                }
                if(s.length() > 0){
                    setRightTextColor(getResources().getColor(R.color.common_white));
                    setRightClickEnable(true);
                } else {
                    setRightTextColor(getResources().getColor(R.color.actionbar_text_right));
                    setRightClickEnable(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 不支持连续换行5次
                int start = picture_wish_text.getSelectionStart();
                int end = picture_wish_text.getSelectionEnd();
                int lineCount = countStrNum(s.toString(), "\n");
                if (lineCount == 15) {
                    s.delete(start - 1, end);
                }
            }
        });
    }

    private void initLabeLsView(){

        if( MainApp.titlesList.size() > 0 ){
            checkid = MainApp.titlesList.get(0).getId();
            labelsView.setLabels(MainApp.titlesList, new LabelsView.LabelTextProvider<ClassifyBean>() {
                @Override
                public CharSequence getLabelText(TextView label, int position, ClassifyBean data) {
                    return data.getName();
                }
            });
            labelsView.setSelectType(LabelsView.SelectType.SINGLE);
            labelsView.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
                @Override
                public void onLabelClick(TextView label, Object data, int position) {
                    LogUtils.e("position = "+position );
                    checkid = ((ClassifyBean)data).getId();
                }
            });
        }
    }

    /**
     * 拖拽效果
     */
    private void initPhotoData(){
        recycAdapter.setOnItemClick(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(drawables != null && drawables.size() > 0){
                    drawables.remove(position);
                }
                recycAdapter.setDrawableList(drawables);
                recycAdapter.notifyDataSetChanged();

            }

            @Override
            public void onItemLongClick(View view, int position) {
                if(position != drawables.size()){
                    itemTouchHelper.startDrag(recyclerView.getChildViewHolder(view));
                }
            }

            @Override
            public void onPhotoClick(View view, int position) {
                jumpToPhoto();
            }
        });

        itemTouchHelper=new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags=0;
                if(recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager ||recyclerView.getLayoutManager() instanceof GridLayoutManager){
                    dragFlags=ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
                }
                return makeMovementFlags(dragFlags,0);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from=viewHolder.getAdapterPosition();
                int to=target.getAdapterPosition();
                String path = drawables.get(from);
                drawables.remove(from);
                drawables.add(to,path);
                recycAdapter.notifyItemMoved(from,to);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void initVideoData(){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int results : grantResults){
            if(requestCode == REQUEST_CODE_CAMERA && results != PackageManager.PERMISSION_GRANTED) {
                ToastUtils.showShortToast("请开启相机权限");
            }
            if(requestCode == REQUEST_CODE_RECORD_AUDIO && results != PackageManager.PERMISSION_GRANTED) {
                ToastUtils.showShortToast("请开启相机、麦克风权限");
            }
        }
        if(requestCode == REQUEST_CODE_CAMERA){
            jumpToPhoto();
        } else if(requestCode == REQUEST_CODE_RECORD_AUDIO){
            jumpToVideo();
        }
    }

    private void jumpToPhoto(){
        if(ContextCompat.checkSelfPermission(ReleaseContentActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(ReleaseContentActivity.this, PhotoBrowserActivity.class);
                int count = 9 - drawables.size();
                intent.putExtra(PhotoBrowserActivity.PHOTO_MAX_SIZE, count);
                startActivityForResult(intent,PhotoBrowserActivity.REQUEST_BROWSER);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_CAMERA);
            }

        } else {
            ActivityCompat.requestPermissions(ReleaseContentActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        }
    }

    private void jumpToVideo(){

        if(ContextCompat.checkSelfPermission(ReleaseContentActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ReleaseContentActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                ) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

//                startActivityForResult(new Intent(ReleaseContentActivity.this,VideoRecordActivity.class),VideoRecordActivity.REQUEST_VIDEO);
                startVideoConfig();

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_RECORD_AUDIO);
            }
        } else {
            ActivityCompat.requestPermissions(ReleaseContentActivity.this, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA}, REQUEST_CODE_RECORD_AUDIO);
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

    /**
     * 指定字符包含个数
     *
     * @param str1
     * @param str2
     * @return
     */
    private int countStrNum(String str1, String str2) {
        int countStr = 0;
        if (str1.indexOf(str2) == -1) {
            return 0;
        } else {
            while (str1.indexOf(str2) != -1) {
                countStr++;
                str1 = str1.substring(str1.indexOf(str2) + str2.length(), str1.length());
            }
        }
        return countStr;
    }

    /**
     * 初始化 照片
     */
    public void initPhotoView(){
        mLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                outRect.bottom = CommonUtils.dip2px(ReleaseContentActivity.this, 4.5f);
                outRect.right = CommonUtils.dip2px(ReleaseContentActivity.this, 4.5f);
            }
        });
        recycAdapter = new PictureReleaseAdapter(this, drawables, maxNum);
        recyclerView.setAdapter(recycAdapter);
    }

    /**
     * 初始化 视频
     */
    public void initVideoView(){
        setVisibleVideoPlay();
//        videoPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                jumpToVideo();
//            }
//        });
    }

    /**
     * 无地址 不显示
     */
    public void setVisibleVideoPlay(){
        if(TextUtils.isEmpty(videoPath)){
            // 小图标
            videoPlay.setVisibility(View.VISIBLE);
            // 播放器
            videoplayer.setVisibility(View.GONE);

        } else {
            // 小图标
            videoPlay.setVisibility(View.GONE);
            // 播放器
            videoplayer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void commitPress() {

        if(TextUtils.isEmpty(picture_wish_text.getText().toString().trim())){
            ToastUtils.showShortToast("请输入您要发布的内容～");
            return;
        }

//        DialogFactory.showLoading(this);
        DialogFactory.showProgressDialog(this);

        if(PICTURE_TYPE == style){
            pictureCommit(checkid);
        } else {
            videoCommit(checkid);
        }

//        showSelectorClassify();

    }

    public void showSelectorClassify(){
        if (popupWindow == null) {
            popupWindow = new ShowPopWinFactor(this);
        }
        popupWindow.showSelectorClassify(new OnPayCallBackListener() {
            @Override
            public void onClickPay(View v, String str) {
                LogUtils.e("发布    点击了  "+str);
                if(PICTURE_TYPE == style){
                    pictureCommit(str);
                } else {
                    videoCommit(str);
                }

                disPopWindow();

            }

            @Override
            public void onClickClose(View v, String str) {
                disPopWindow();
            }
        });
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    public void disPopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * 发布图片  cid 分类
     */
    public void pictureCommit(String cid){

        HashMap<String,String> params = new HashMap<>();
        params.put("uid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        params.put("cid",cid);
        params.put("title",picture_wish_text.getText().toString().trim());
        params.put("type","1");
        params.put("template","1");
        params.put("source","1");
        OkHttpManager.getInstance().applyUploadAsync(Constants.GOOSE_URL_CONTENT_PUBLISH, params, drawables, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("发表图片 "+result);
                DialogFactory.hideLoading(ReleaseContentActivity.this);
                startActivity(new Intent(ReleaseContentActivity.this,MainActivity.class));
                finish();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("发表图片失败 "+request.toString());
                DialogFactory.hideLoading(ReleaseContentActivity.this);
                ToastUtils.showShortToast("发表图片失败");
            }
        });
    }

    /**
     * 发布视频
     */
    public void videoCommit(String cid){

//        HashMap<String,String> params = new HashMap<>();
        HashMap<String,Object> params = new HashMap<>();
        params.put("uid", (String) SharedPreUtils.get(SharedPreUtils.SP_USERID,""));
        params.put("cid",cid);
        params.put("title",picture_wish_text.getText().toString().trim());
        params.put("type","2");
        params.put("template","1");
        params.put("source","1");
        params.put("video",new File(videoPath));

//        String url = "http://82.201.205.117/shiming/fileupload.ashx";
        OkHttpManager.getInstance().uploadAsync1(Constants.GOOSE_URL_CONTENT_PUBLISH, params, new OkHttpManager.DataCallBack() {
//        OkHttpManager.getInstance().uploadAsync(Constants.GOOSE_URL_CONTENT_PUBLISH, new File(videoPath), params, new OkHttpManager.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("发表视频 "+result);
                HashMap<String,String> map = GsonUtils.getMapParams(result);
//                DialogFactory.hideLoading(ReleaseContentActivity.this);
                DialogFactory.hideProgressDialog(ReleaseContentActivity.this);
                if(Constants.ERROR_CODE_200.equals(map.get("error"))){
                    startActivity(new Intent(ReleaseContentActivity.this,MainActivity.class));
                    if(!ReleaseContentActivity.this.isFinishing()){
                        finish();
                    }
                } else {
                    ToastUtils.showShortToast(map.get("msg"));
                }

            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("发表视频失败 "+request.toString());
                DialogFactory.hideLoading(ReleaseContentActivity.this);
                ToastUtils.showShortToast("发表视频失败");
            }
        });
    }


    @Override
    public void backKeyPress() {
        DialogFactory.OKAndCancelDialog(this,
                CommonUtils.getResourceString(R.string.dialog_init_content),
                CommonUtils.getResourceString(R.string.dialog_init_ok),
                CommonUtils.getResourceString(R.string.dialog_init_cancel),
                new OnDialogCallBackListener() {
                    @Override
                    public void OnOkClick() {
                        finish();
                    }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PhotoBrowserActivity.REQUEST_BROWSER) {
            if (data != null && data.hasExtra(PhotoBrowserActivity.SELECTED_DATA)) {
                List<String> listExtra = data.getStringArrayListExtra(PhotoBrowserActivity.SELECTED_DATA);
                drawables.addAll(listExtra);
                if(recycAdapter != null){
                    recycAdapter.setDrawableList(drawables);
                    recycAdapter.notifyDataSetChanged();
                }
            }

        } else if (requestCode == VideoRecordActivity.REQUEST_VIDEO) {
            if(data != null && data.hasExtra(VideoRecordActivity.REQUEST_VIDEO_PATH)){
                videoPath = data.getStringExtra(VideoRecordActivity.REQUEST_VIDEO_PATH);
                setVisibleVideoPlay();
                if(!TextUtils.isEmpty(videoPath)){
                    videoplayer.setUp(videoPath,JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
                }
            }
        } else if (requestCode == VideoRecordActivity.REQUEST_VIDEO_CANCEL) {

        }
    }
}
