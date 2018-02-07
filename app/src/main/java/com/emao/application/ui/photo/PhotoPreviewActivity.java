package com.emao.application.ui.photo;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.emao.application.R;
import com.emao.application.ui.activity.BaseActivity;
import com.emao.application.ui.photo.drawview.PhotoDraweeView;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author keybon
 */

public class PhotoPreviewActivity extends BaseActivity {

    protected static final String EXTRA_BUCKET_ID = "EXTRA_BUCKET_ID";
    protected static final String EXTRA_SELECTED = "EXTRA_SELECTED";
    protected static final String EXTRA_START_POSITION = "EXTRA_START_POSITION";
    private static final String ARG_PATH = "path";

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CheckBox checkBox;

    private ArrayList<ImageItem> mSelectedImages;
//    private List<ImageItem> mDisplayImages;
    private int mStartPosition = 0;
    private int mMaxSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_photo_preview);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();
        mViewPager = findViewById(R.id.preview_viewpager);
        checkBox = findViewById(R.id.preview_checkbox);
    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        ImageHelper imageHelper = ImageHelper.getInstance(this);
        mSelectedImages = new ArrayList<ImageItem>();
        mStartPosition = intent.getIntExtra(EXTRA_START_POSITION, 0);
//        long bucketId = intent.getLongExtra(EXTRA_BUCKET_ID, 0);
//        mDisplayImages = imageHelper.getImagesByBucketId(bucketId);

        if (intent.hasExtra(EXTRA_SELECTED)) {
            mSelectedImages = intent.getParcelableArrayListExtra(EXTRA_SELECTED);
//            if (mDisplayImages == null) {
//                mDisplayImages = new ArrayList<ImageItem>();
//            }
//            if (mDisplayImages.size() == 0) {
//                mDisplayImages.addAll(mSelectedImages);
//            }

//            if (mSelectedImages.contains(mDisplayImages.get(mStartPosition))) {
//                checkBox.setChecked(true);
//            } else {
//                checkBox.setChecked(false);
//            }
        }

        mMaxSize = intent.getIntExtra(PhotoBrowserActivity.PHOTO_MAX_SIZE, 0);



        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    public static class PhotoViewFragment extends Fragment {

        public static PhotoViewFragment newInstance(String path) {
            PhotoViewFragment fragment = new PhotoViewFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PATH, path);
            fragment.setArguments(args);
            return fragment;
        }

        public PhotoViewFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_photo_preview_item, container, false);
            String path = getArguments().getString(ARG_PATH);
            final PhotoDraweeView photoDraweeView = rootView.findViewById(R.id.photo_progressive);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            photoDraweeView.setVisibility(View.VISIBLE);
            Uri uri = Uri.fromFile(new File(path));
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setUri(uri);
            controller.setAutoPlayAnimations(true);
            controller.setOldController(photoDraweeView.getController());
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null || photoDraweeView == null) {
                        return;
                    }
                    photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            });
            photoDraweeView.setController(controller.build());
            return rootView;
        }
    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PhotoViewFragment.newInstance(mSelectedImages.get(position).getPath());
        }

        @Override
        public int getCount() {
            return (mSelectedImages == null ? 0 : mSelectedImages.size());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mSelectedImages.get(position).getPath();
        }
    }

}
