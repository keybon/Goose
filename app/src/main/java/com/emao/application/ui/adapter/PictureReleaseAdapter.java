package com.emao.application.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.emao.application.R;
import com.emao.application.ui.callback.OnRecyclerViewItemClickListener;
import com.emao.application.ui.utils.fresco.FrescoImageUtils;

import java.util.ArrayList;

/**
 *
 * @author keybon
 */

public class PictureReleaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener,View.OnLongClickListener{


    private final int VIEWPHOTOTYPE = 1;
    private final int VIEWADDTYPE = 2;

    private ArrayList<String> drawableList ;

    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    private Context mContext;

    private int maxNum;
    private int position;

    public PictureReleaseAdapter(Context mContext,ArrayList<String> drawableList, int maxNum){
        this.drawableList = drawableList;
        this.maxNum = maxNum;
        this.mContext = mContext;
    }

    public void setDrawableList(ArrayList<String> drawableList) {
        this.drawableList = drawableList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEWPHOTOTYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_picture_item,parent,false);
            PictureViewHolder pictureViewHolder = new PictureViewHolder(view);
            view.setOnLongClickListener(this);
            view.setOnClickListener(this);
            return pictureViewHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_picture_item,parent,false);
            PictureViewAddHolder pictureViewAddHolder = new PictureViewAddHolder(view);
            return pictureViewAddHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof PictureViewHolder){

            FrescoImageUtils.getInstance()
                    .createSdcardBuilder(drawableList.get(position))
                    .setResizeOptions(200,200)
//                    .setFailDrawable(R.drawable.image_demaged)
                    .build()
                    .showSdcardImage(((PictureViewHolder) holder).adapter_picture_img);
            holder.itemView.setTag(position);
        } else if(holder instanceof PictureViewAddHolder){

            ((PictureViewAddHolder) holder).adapter_picture_add_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.picture_add));
            ((PictureViewAddHolder) holder).adapter_picture_add_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewItemClickListener.onPhotoClick(v,position);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if(drawableList.size() >= maxNum){
            return maxNum;
        }
        return drawableList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == drawableList.size()){
            return VIEWADDTYPE;
        }
        return VIEWPHOTOTYPE;
    }

    public void setOnItemClick(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if(onRecyclerViewItemClickListener != null){
            onRecyclerViewItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(onRecyclerViewItemClickListener != null){
            onRecyclerViewItemClickListener.onItemLongClick(v,(Integer) v.getTag());
        }
        return false;
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder{

        private SimpleDraweeView adapter_picture_img;

        public PictureViewHolder(View view){
            super(view);
            adapter_picture_img = view.findViewById(R.id.adapter_picture_img);
        }
    }

    public class PictureViewAddHolder extends RecyclerView.ViewHolder{

        private SimpleDraweeView adapter_picture_add_img;

        public PictureViewAddHolder(View view){
            super(view);
            adapter_picture_add_img = view.findViewById(R.id.adapter_picture_img);
        }
    }
}
