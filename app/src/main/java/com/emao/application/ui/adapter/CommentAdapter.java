package com.emao.application.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.ui.bean.OpinionBean;

import java.util.List;

/**
 *
 * @author keybon
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<OpinionBean> list;

    public CommentAdapter(Context context,List<OpinionBean> list){
        this.mContext = context;
        this.list = list;
    }

    public void setList(List<OpinionBean> list) {
        this.list = list;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_comment_item,parent,false);
        CommentViewHolder viewHolder = new CommentViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        OpinionBean opinionBean = list.get(position);
        holder.comment_nick.setText(Html.fromHtml("我评论<font color=\'#3366cc\'>"+opinionBean.getUsername()+"</font>："));
        holder.comment_nick.setVisibility(View.GONE);
        holder.comment_content.setText(opinionBean.getComment());
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{

        private TextView comment_nick,comment_content;

        public CommentViewHolder(View itemView) {
            super(itemView);
            comment_nick = itemView.findViewById(R.id.adapter_comment_nick);
            comment_content = itemView.findViewById(R.id.adapter_comment_content);
        }
    }
}
