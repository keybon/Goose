package com.emao.application.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.ui.bean.FocusTableLetterBean;
import com.emao.application.ui.callback.OnRecyclerViewItemClickListener;

import java.util.List;

/**
 *
 * @author keybon
 */

public class FocusTableLetterAdapter extends RecyclerView.Adapter<FocusTableLetterAdapter.FocusTableLetterViewholder>
                    implements View.OnClickListener{

    private List<FocusTableLetterBean> list;
    private Context mContext;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public FocusTableLetterAdapter(Context context,List<FocusTableLetterBean> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public FocusTableLetterAdapter.FocusTableLetterViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_table_letter_item,parent,false);
        FocusTableLetterViewholder viewholder = new FocusTableLetterViewholder(view);
        view.setOnClickListener(this);
        return viewholder;
    }

    public void setList(List<FocusTableLetterBean> list) {
        this.list = list;
    }

    @Override
    public void onBindViewHolder(FocusTableLetterAdapter.FocusTableLetterViewholder holder, int position) {
        FocusTableLetterBean focusTableLetterBean = list.get(position);
        holder.itemView.setTag(position);
        holder.nickName.setText(focusTableLetterBean.getUsername()+"："+focusTableLetterBean.getFtext());
        if(focusTableLetterBean.getCount().equals("0")){
            holder.newsCount.setVisibility(View.GONE);
        } else {
            holder.newsCount.setVisibility(View.VISIBLE);
            holder.newsCount.setText(focusTableLetterBean.getCount());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
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

    class FocusTableLetterViewholder extends RecyclerView.ViewHolder{

        private TextView nickName;
        private TextView newsCount;

        public FocusTableLetterViewholder(View itemView) {
            super(itemView);
            nickName = itemView.findViewById(R.id.letter_table_nickname);
            newsCount = itemView.findViewById(R.id.letter_table_count);
        }
    }
}
