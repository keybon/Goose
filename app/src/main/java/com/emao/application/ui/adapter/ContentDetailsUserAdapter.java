package com.emao.application.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emao.application.R;
import com.emao.application.ui.bean.RecommendImageBean;
import com.emao.application.ui.callback.OnItemClickListener;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.Constants;
import com.emao.application.ui.utils.fresco.AsyncTaskVideoLoad;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.jzvd.JZVideoPlayerStandard;

/**
 *
 * @author keybon
 */

public class ContentDetailsUserAdapter extends RecyclerView.Adapter<ContentDetailsUserAdapter.ContentUserViewHolder> implements View.OnClickListener{

    private final int VIEW_TYPE_PICTURE = 1;
    private final int VIEW_TYPE_VIDEO = 2;

    private Context mContext;
    private OnItemClickListener onItemClickListener;
    private int viewType;
    private List<RecommendImageBean> pictureList;
    private String videoPath;

    public ContentDetailsUserAdapter(Context mContext,int type) {
        this.mContext = mContext;
        this.viewType = type;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setPictureList(List<RecommendImageBean> pictureList) {
        this.pictureList = pictureList;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    @Override
    public ContentUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        ContentDetailsUserAdapter.ContentUserViewHolder viewHolder = null;
        if(VIEW_TYPE_VIDEO == viewType){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_content_details_user_video,parent,false);
            viewHolder = new ContentUserVideoViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_content_details_user_picture,parent,false);
            viewHolder = new ContentUserPictureViewHolder(view);
        }

        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContentUserViewHolder holder, int position) {
        holder.onBindViewHolder(position);
    }

    @Override
    public int getItemCount() {
        if(VIEW_TYPE_VIDEO == viewType){
            return 1;
        }
        return pictureList == null ? 0 : pictureList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public void onClick(View v) {
        if(onItemClickListener != null){
            onItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    class ContentUserViewHolder extends RecyclerView.ViewHolder{


        public ContentUserViewHolder(View itemView) {
            super(itemView);
        }

        public void onBindViewHolder(int position){

        }
    }

    class ContentUserVideoViewHolder extends ContentUserViewHolder{

        private JZVideoPlayerStandard jzVideoPlayerStandard;

        public ContentUserVideoViewHolder(View itemView) {
            super(itemView);
            jzVideoPlayerStandard = itemView.findViewById(R.id.content_details_user_item_video);
        }

        @Override
        public void onBindViewHolder(int position) {
            super.onBindViewHolder(position);

            if(TextUtils.isEmpty(videoPath)){
                jzVideoPlayerStandard.setVisibility(View.GONE);
            } else {
                jzVideoPlayerStandard.setUp(Constants.GOOSE_VIDEO_URL + videoPath
                        , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,"");
                jzVideoPlayerStandard.setVisibility(View.VISIBLE);
                AsyncTaskVideoLoad asyncTaskVideoLoad = new AsyncTaskVideoLoad(jzVideoPlayerStandard);
                asyncTaskVideoLoad.execute(Constants.GOOSE_VIDEO_URL + videoPath);
            }

        }
    }

    class ContentUserPictureViewHolder extends ContentUserViewHolder{

//        private ImageView imageView;
        private SimpleDraweeView imageView;

        public ContentUserPictureViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.content_details_user_item_img);
        }

        @Override
        public void onBindViewHolder(int position) {
            super.onBindViewHolder(position);
            String picture = Constants.GOOSE_IMAGE_URL + pictureList.get(position).getPicture();

            imageView.setController(CommonUtils.getDraweeDetailsController(mContext,imageView,picture,150,150));

            itemView.setTag(position);
        }
    }


}
