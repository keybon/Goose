package com.emao.application.ui.adapter;

import android.content.Context;
import android.view.View;

import com.bigkoo.convenientbanner.holder.Holder;
import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.bean.BannerBean;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * @author keybon
 */

public class LocalImageHolderView implements Holder<BannerBean> {

    private SimpleDraweeView imageView;

    @Override
    public View createView(Context context) {

        imageView = new SimpleDraweeView(context);

        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(MainApp.IMApp.getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build();

        imageView.setHierarchy(hierarchy);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, BannerBean data) {
//        imageView.setImageURI(Constants.GOOSE_IMAGE_URL+data.getPicture());
        imageView.setImageURI(data.getPicture());
    }

}
