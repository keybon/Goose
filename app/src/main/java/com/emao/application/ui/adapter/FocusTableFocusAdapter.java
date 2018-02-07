package com.emao.application.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.ui.bean.FocusTableAttentionBean;
import com.emao.application.ui.callback.OnRecyclerViewClickListener;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 *
 * @author keybon
 */

public class FocusTableFocusAdapter extends RecyclerView.Adapter<FocusTableFocusAdapter.FocusTableFocusViewholder>
                implements View.OnClickListener{

    private List<FocusTableAttentionBean> list;
    private Context mContext;
    private OnRecyclerViewClickListener onRecyclerViewClickListener;

    public FocusTableFocusAdapter(Context context,List<FocusTableAttentionBean> list) {
        this.mContext = context;
        this.list = list;
    }

    public void setList(List<FocusTableAttentionBean> list) {
        this.list = list;
    }

    @Override
    public FocusTableFocusViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_table_focus_item,parent,false);
        FocusTableFocusViewholder viewholder = new FocusTableFocusViewholder(view);
        view.setOnClickListener(this);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(FocusTableFocusViewholder holder, int position) {
        FocusTableAttentionBean focusTableAttentionBean = list.get(position);
        holder.nickName.setText(focusTableAttentionBean.getUsername());
//        holder.isAttention.setSelected(focusTableAttentionBean.isAttention());
        holder.isAttention.setVisibility(View.VISIBLE);

        holder.portrait.setImageURI(focusTableAttentionBean.getHeadimgurl());

        holder.itemView.setTag(position);
        holder.portrait.setTag(position);
//        holder.nickName.setTag(position);
        holder.isAttention.setTag(position);
        holder.portrait.setOnClickListener(this);
//        holder.nickName.setOnClickListener(this);
        holder.isAttention.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnRecyclerViewClickListener(OnRecyclerViewClickListener onRecyclerViewClickListener){
        this.onRecyclerViewClickListener = onRecyclerViewClickListener;
    }

    @Override
    public void onClick(View v) {

        if(onRecyclerViewClickListener != null){
            switch (v.getId()){
                case R.id.focus_table_portrait:
                    onRecyclerViewClickListener.onClickItem1(v, (Integer) v.getTag());
                    break;
                case R.id.focus_table_nickname:
                    onRecyclerViewClickListener.onClickItem2(v, (Integer) v.getTag());
                    break;
                case R.id.focus_table_attention:
                    onRecyclerViewClickListener.onClickItem3(v, (Integer) v.getTag());
                    break;
                default:
                    onRecyclerViewClickListener.onClickItem1(v, (Integer) v.getTag());
                    break;
            }
        }
    }


    class FocusTableFocusViewholder extends RecyclerView.ViewHolder{

        private SimpleDraweeView portrait;
        private TextView nickName;
        private Button isAttention;

        public FocusTableFocusViewholder(View itemView) {
            super(itemView);
            portrait = itemView.findViewById(R.id.focus_table_portrait);
            nickName = itemView.findViewById(R.id.focus_table_nickname);
            isAttention = itemView.findViewById(R.id.focus_table_attention);
        }
    }
}
