package com.emao.application.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.ui.bean.RewardBean;
import com.emao.application.ui.utils.CommonUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 *
 * @author keybon
 */

public class ContentDetailsRewardAdapter extends RecyclerView.Adapter<ContentDetailsRewardAdapter.ContentDetailsRewardViewHolder> {


    private Context mContext;
    private List<RewardBean> dataList;

    public ContentDetailsRewardAdapter(Context mContext, List<RewardBean> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    public void setDataList(List<RewardBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ContentDetailsRewardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_details_opinion_item,parent,false);
        ContentDetailsRewardViewHolder viewHolder = new ContentDetailsRewardViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContentDetailsRewardViewHolder holder, int position) {
        RewardBean bean = dataList.get(position);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.opinionPortrait.getLayoutParams();
        layoutParams.height = CommonUtils.dip2px(mContext,30);
        layoutParams.width = CommonUtils.dip2px(mContext,30) ;
        holder.opinionPortrait.setLayoutParams(layoutParams);

        holder.opinionContent.setText(Html.fromHtml(bean.getUsername()+"打赏:&nbsp&nbsp&nbsp<font color=\"#FC5459\">"+bean.getReward()+"金币</font>"));
        holder.opinionNickname.setText(bean.getUsername());
        holder.opinionPortrait.setImageURI(bean.getHeadimgurl());

        long dateTime = CommonUtils.getLongTime(bean.getCtime());
        holder.opinionTime.setText(CommonUtils.getTimeFormatText(dateTime));

        if(dataList.size() - 1 == position) {
            holder.cutline.setVisibility(View.GONE);
        } else {
            holder.cutline.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ContentDetailsRewardViewHolder extends RecyclerView.ViewHolder{

        private TextView opinionContent,opinionNickname,opinionTime;
        private SimpleDraweeView opinionPortrait;
        private TextView cutline;

        public ContentDetailsRewardViewHolder(View itemView) {
            super(itemView);
            opinionContent = itemView.findViewById(R.id.adapter_details_opinion_content);
            opinionPortrait = itemView.findViewById(R.id.message_friend_portrait);
            opinionNickname = itemView.findViewById(R.id.message_friend_nickname);
            opinionTime = itemView.findViewById(R.id.message_friend_time);
            cutline = itemView.findViewById(R.id.message_details_cutline);
        }
    }
}
