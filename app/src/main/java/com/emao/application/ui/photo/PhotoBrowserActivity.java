package com.emao.application.ui.photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;

import com.emao.application.ui.activity.ReleaseContentActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.emao.application.R;
import com.emao.application.ui.activity.BaseActivity;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author keybon
 */

public class PhotoBrowserActivity extends BaseActivity implements AbsListView.OnScrollListener, ImageGridView.OnImageSelectedListener {

    public static final String PHOTO_MAX_SIZE = "PHOTO_MAX_SIZE";
    public static final String SELECTED_DATA = "SELECTED_DATA";
    private static final int DEFAULT_MAX_SIZE = 9;
    public static final int REQUEST_BROWSER = 0x00001;

    private ImageGridView gridView;
    // 日期
    private TextView mLabelDate;
    private Animation mAlphaAnimShow;
    private Animation mAlphaAnimHide;
    private boolean enableHideLabelDate = true;

    /**
     * 选中的照片
     */
    private ArrayList<ImageItem> mSelectedImages;
    /**
     * 相册
     */
    private List<ImageItem> images;
    private ImageHelper mImageHelper;

    private int mMaxSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_photo_browser);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();

        gridView = findViewById(R.id.photo_browser_grid);
        mLabelDate = findViewById(R.id.label_date);

        mMaxSize = getIntent().getIntExtra(PHOTO_MAX_SIZE, DEFAULT_MAX_SIZE);

        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        gridView.setOnScrollListener(this);
        gridView.setOnImageSelectedListener(this);

    }

    @Override
    public void initData() {
        super.initData();

        setRightTextColor(getResources().getColor(R.color.common_white));

        mImageHelper = ImageHelper.getInstance(this);
        mSelectedImages = new ArrayList<ImageItem>();

        mAlphaAnimShow = new AlphaAnimation(0f, 1f);
        mAlphaAnimHide = new AlphaAnimation(1f, 0f);
        mAlphaAnimShow.setDuration(300);
        mAlphaAnimHide.setDuration(300);

        images = mImageHelper.getLocalPhotosByBucketId(0);
        gridView.initImages(mSelectedImages, images);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTitleRightTv();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_IDLE:
                Fresco.getImagePipeline().resume();
                enableHideLabelDate = true;
                postHideLabelDate();
                return;
            case SCROLL_STATE_FLING:
                Fresco.getImagePipeline().pause();
            case SCROLL_STATE_TOUCH_SCROLL:
                enableHideLabelDate = false;
                if (mLabelDate.getVisibility() != View.VISIBLE) {
                    mLabelDate.clearAnimation();
                    mAlphaAnimShow.reset();
                    mLabelDate.startAnimation(mAlphaAnimShow);
                    mLabelDate.setVisibility(View.VISIBLE);
                }
                return;
            default:
                break;
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem < view.getCount()) {
            ImageItem item = (ImageItem) view.getItemAtPosition(firstVisibleItem);
            mLabelDate.setText(CommonUtils.getRelativeDateString(item.getDateAdded()));
        }
    }

    @Override
    public void onImageSelected(GridView parent, int position, ImageItem image, boolean isSelected) {
        if (isSelected && mSelectedImages.size() >= mMaxSize) {
            parent.setItemChecked(position, false);
            ToastUtils.showShortToast(getString(R.string.photo_max_limit, 9));
            return;
        }
        if (isSelected) {
            mSelectedImages.add(image);
        } else if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
        }
        supportInvalidateOptionsMenu();
        initTitleRightTv();
    }

    public void initTitleRightTv() {
        int size = mSelectedImages == null ? 0 : mSelectedImages.size();
        setTextViewRight(true);
        setTextRight("" + getString(R.string.photo_send_num, size, mMaxSize));
    }

    @Override
    public void onImageItemClicked(ImageItem image, int position) {

    }

    private void postHideLabelDate() {
        gridView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (enableHideLabelDate) {
                    enableHideLabelDate = false;
                    mLabelDate.clearAnimation();
                    mAlphaAnimHide.reset();
                    mLabelDate.startAnimation(mAlphaAnimHide);
                    mLabelDate.setVisibility(View.GONE);
                }
            }
        }, 300);
    }

    @Override
    public void commitPress() {
        super.commitPress();
        Intent data = new Intent();
        ArrayList<String> paths = new ArrayList<String>();
        for (ImageItem item : mSelectedImages) {
            File f = new File(item.getPath());
            if (f.exists()) {
                if (f.length() > 1024 * 1024 * 20) {
                    ToastUtils.showShortToast(CommonUtils.getResourceString(R.string.default_pic_over_20MB));
                    return;
                }
            }
            paths.add(item.getPath());
        }
        data.putExtra(ReleaseContentActivity.RELEASE_FLAG, ReleaseContentActivity.PICTURE_TYPE);
        data.putExtra(ReleaseContentActivity.RELEASE_MAXNUM, 9);
        data.setClass(this, ReleaseContentActivity.class);
        data.putStringArrayListExtra(SELECTED_DATA, paths);
//        startActivity(data);
        setResult(PhotoBrowserActivity.REQUEST_BROWSER, data);
        this.finish();
    }
}
