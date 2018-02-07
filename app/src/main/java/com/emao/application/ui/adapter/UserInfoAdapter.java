package com.emao.application.ui.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.ui.bean.RecommendFriendBean;
import com.emao.application.ui.bean.RecommendImageBean;
import com.emao.application.ui.callback.OnRecyclerViewClickListener;
import com.emao.application.ui.callback.OnRecyclerViewItemClickListener;
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

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.UserInfoViewHolder>{

    private final int TYPE_PHOTO = 1;
    private final int TYPE_VIDEO = 2;

    private Context mContext;
    private List<RecommendFriendBean> list;


    private List<RecommendImageBean> photoPath;
    private GridLayoutManager layoutManager;
    private RecommendAdapterInAdapter mAdapter;

    private OnRecyclerViewClickListener onRecyclerViewClickListener;

    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public UserInfoAdapter(Context mContext, List<RecommendFriendBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void setList(List<RecommendFriendBean> list) {
        this.list = list;
    }

    @Override
    public UserInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        UserInfoViewHolder viewHolder = null;
        if(TYPE_VIDEO == viewType){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_message_video_userinfo_item,parent,false);
            viewHolder = new UserInfoVideoViewHolder(view);
        } else
//            if (TYPE_PHOTO == viewType)
            {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_message_friend_item,parent,false);
            viewHolder = new UserInfoPhotoViewHolder(view);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onRecyclerViewItemClickListener != null){
                    onRecyclerViewItemClickListener.onItemLongClick(v, (Integer) v.getTag());
                }

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserInfoViewHolder holder, int position) {


        RecommendFriendBean bean = list.get(position);
        holder.onBinderView(bean,position);

    }

    public void setOnRecyclerViewClickListener(OnRecyclerViewClickListener onRecyclerViewClickListener){
        this.onRecyclerViewClickListener = onRecyclerViewClickListener;
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(list.get(position).getType());
    }

    public void initRecyclerView(UserInfoPhotoViewHolder holder){

        layoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
        holder.adapterRecycler.setLayoutManager(layoutManager);
        holder.adapterRecycler.setNestedScrollingEnabled(false);
//        holder.adapterRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
//                outRect.bottom = CommonUtils.dip2px(mContext, 4f);
//                outRect.right = CommonUtils.dip2px(mContext, 4f);
//            }
//        });

        mAdapter = new RecommendAdapterInAdapter(mContext, photoPath);
        holder.adapterRecycler.setAdapter(mAdapter);
    }


    class UserInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private SimpleDraweeView simpleDraweeView;
        private TextView nickname,time,content;
        private LinearLayout commentBt,opinionBt,admireBt;
        private RelativeLayout title;
        private TextView commentNum,opinionNum,bottom_cutarea;
        private LinearLayout item_onclick;


        public UserInfoViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = itemView.findViewById(R.id.message_friend_portrait);
            nickname = itemView.findViewById(R.id.message_friend_nickname);
            time = itemView.findViewById(R.id.message_friend_time);
            content = itemView.findViewById(R.id.message_friend_content);
            title = itemView.findViewById(R.id.message_friend_title);

            commentBt = itemView.findViewById(R.id.message_friend_comment_ll);
            opinionBt = itemView.findViewById(R.id.message_friend_opinion_ll);
            admireBt = itemView.findViewById(R.id.message_friend_admire_ll);
            commentNum = itemView.findViewById(R.id.message_friend_comment);
            opinionNum = itemView.findViewById(R.id.message_friend_opinion);
            bottom_cutarea = itemView.findViewById(R.id.bottom_cutarea);
            item_onclick = itemView.findViewById(R.id.item_onclick);
        }

        public void onBinderView(RecommendFriendBean recommendFriendBean,int position){

            simpleDraweeView.setImageURI(recommendFriendBean.getHeadimgurl());
            nickname.setText(recommendFriendBean.getUsername());
            content.setText(recommendFriendBean.getTitle());

            long dateTime = CommonUtils.getLongTime(recommendFriendBean.getCtime());
            time.setText(CommonUtils.getTimeFormatText(dateTime));
            commentNum.setText(recommendFriendBean.getGive());
            opinionNum.setText(recommendFriendBean.getComment());

            commentBt.setOnClickListener(this);
            opinionBt.setOnClickListener(this);
            admireBt.setOnClickListener(this);
            simpleDraweeView.setOnClickListener(this);
            item_onclick.setOnClickListener(this);

            itemView.setTag(position);

            commentBt.setTag(position);
            opinionBt.setTag(position);
            admireBt.setTag(position);
            simpleDraweeView.setTag(position);
            item_onclick.setTag(position);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.message_friend_comment_ll:
                    if(onRecyclerViewClickListener != null){
                        onRecyclerViewClickListener.onClickItem1(v, (Integer) v.getTag());
                    }
                    break;
                case R.id.message_friend_opinion_ll:
                case R.id.item_onclick:
                    if(onRecyclerViewClickListener != null){
                        onRecyclerViewClickListener.onClickItem2(v, (Integer) v.getTag());
                    }
                    break;
                case R.id.message_friend_admire_ll:
                    if(onRecyclerViewClickListener != null){
                        onRecyclerViewClickListener.onClickItem3(v, (Integer) v.getTag());
                    }
                    break;
                case R.id.message_friend_portrait:
                    // 个人详情
                    if(onRecyclerViewClickListener != null){
                        onRecyclerViewClickListener.onClickItem4(v, (Integer) v.getTag());
                    }
                    break;
                default:
                    break;
            }
        }
    }


    class UserInfoPhotoViewHolder extends UserInfoViewHolder{

        private RecyclerView adapterRecycler;


        public UserInfoPhotoViewHolder(View itemView) {
            super(itemView);
            adapterRecycler = itemView.findViewById(R.id.adapter_message_recyclerview);
        }

        @Override
        public void onBinderView(RecommendFriendBean recommendFriendBean, int position) {
            super.onBinderView(recommendFriendBean, position);

            photoPath = recommendFriendBean.getImage();
            if(photoPath != null && photoPath.size() > 0){
                adapterRecycler.setVisibility(View.VISIBLE);
                initRecyclerView(this);
            } else {
                adapterRecycler.setVisibility(View.GONE);
            }
        }
    }

    class UserInfoVideoViewHolder extends UserInfoViewHolder {

        private JZVideoPlayerStandard jzVideoPlayerStandard;

        public UserInfoVideoViewHolder(View itemView) {
            super(itemView);
            jzVideoPlayerStandard = itemView.findViewById(R.id.message_userinfo_videoplayer);
        }

        @Override
        public void onBinderView(RecommendFriendBean recommendFriendBean, int position) {
            super.onBinderView(recommendFriendBean, position);
//            jzVideoPlayerStandard.thumbImageView.setImageBitmap(CommonUtils.createVideoThumbnail(Constants.GOOSE_VIDEO_URL_TEST,640,480));

            String videoPath = recommendFriendBean.getVideo();
            if(TextUtils.isEmpty(videoPath)){
                jzVideoPlayerStandard.setVisibility(View.GONE);
            } else {

                jzVideoPlayerStandard.setUp(Constants.GOOSE_VIDEO_URL + videoPath, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,"");
                jzVideoPlayerStandard.setVisibility(View.VISIBLE);
                AsyncTaskVideoLoad asyncTaskVideoLoad = new AsyncTaskVideoLoad(jzVideoPlayerStandard);
                asyncTaskVideoLoad.execute(Constants.GOOSE_VIDEO_URL + videoPath);

            }

        }
    }

}
