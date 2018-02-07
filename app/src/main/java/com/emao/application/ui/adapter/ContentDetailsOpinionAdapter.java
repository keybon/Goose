package com.emao.application.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.ui.bean.OpinionBean;
import com.emao.application.ui.callback.OnItemClickListener;
import com.emao.application.ui.utils.CommonUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 *
 * @author keybon
 */

public class ContentDetailsOpinionAdapter extends RecyclerView.Adapter<ContentDetailsOpinionAdapter.ContentDetailsOpinionViewHolder> implements View.OnClickListener{


    private Context mContext;
    private List<OpinionBean> dataList;
    private OnItemClickListener onItemClickListener;

    public ContentDetailsOpinionAdapter(Context mContext, List<OpinionBean> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    public void setDataList(List<OpinionBean> dataList) {
        this.dataList = dataList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ContentDetailsOpinionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_details_opinion_item,parent,false);
        ContentDetailsOpinionViewHolder viewHolder = new ContentDetailsOpinionViewHolder(view);
//        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContentDetailsOpinionViewHolder holder, int position) {

        OpinionBean bean = dataList.get(position);
        holder.opinionContent.setText(bean.getComment());
        holder.opinionNickname.setText(bean.getUsername());

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.opinionPortrait.getLayoutParams();
        layoutParams.height = CommonUtils.dip2px(mContext,30);
        layoutParams.width = CommonUtils.dip2px(mContext,30) ;
        holder.opinionPortrait.setLayoutParams(layoutParams);

        holder.opinionPortrait.setImageURI(bean.getHeadimgurl());

        long dateTime = CommonUtils.getLongTime(bean.getCtime());
        holder.opinionTime.setText(CommonUtils.getTimeFormatText(dateTime));

        if(position == dataList.size() - 1){
            holder.cutLine.setVisibility(View.GONE);
        } else {
            holder.cutLine.setVisibility(View.VISIBLE);
        }
//        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }


    @Override
    public void onClick(View v) {
        if(onItemClickListener != null){
            onItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    class ContentDetailsOpinionViewHolder extends RecyclerView.ViewHolder{

        private TextView opinionContent,opinionNickname,opinionTime;
        private SimpleDraweeView opinionPortrait;
        private TextView cutLine;

        public ContentDetailsOpinionViewHolder(View itemView) {
            super(itemView);
            opinionContent = itemView.findViewById(R.id.adapter_details_opinion_content);
            opinionPortrait = itemView.findViewById(R.id.message_friend_portrait);
            opinionNickname = itemView.findViewById(R.id.message_friend_nickname);
            opinionTime = itemView.findViewById(R.id.message_friend_time);
            cutLine = itemView.findViewById(R.id.message_details_cutline);
        }
    }
}
