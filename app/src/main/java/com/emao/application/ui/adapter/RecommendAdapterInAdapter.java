package com.emao.application.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emao.application.R;
import com.emao.application.ui.bean.RecommendImageBean;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.Constants;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 *
 * @author keybon
 */

public class RecommendAdapterInAdapter extends RecyclerView.Adapter<RecommendAdapterInAdapter.RecommendAdapterViewHolder> {

    private List<RecommendImageBean> list;
    private Context mContext;

    public RecommendAdapterInAdapter(Context mContext,List<RecommendImageBean> list) {
        this.list = list;
        this.mContext = mContext;
    }

    public void setList(List<RecommendImageBean> list) {
        this.list = list;
    }

    @Override
    public RecommendAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_picture_item,parent,false);
        RecommendAdapterViewHolder viewHolder = new RecommendAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecommendAdapterViewHolder holder, int position) {
        String picture = Constants.GOOSE_IMAGE_URL + list.get(position).getPicture();

        holder.adapter_picture_img.setController(CommonUtils.getDraweeController(mContext,holder.adapter_picture_img,picture));

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class RecommendAdapterViewHolder extends RecyclerView.ViewHolder{

        private SimpleDraweeView adapter_picture_img;

        public RecommendAdapterViewHolder(View itemView) {
            super(itemView);
            adapter_picture_img = itemView.findViewById(R.id.adapter_picture_img);
        }
    }

}
