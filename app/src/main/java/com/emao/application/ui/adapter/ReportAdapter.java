package com.emao.application.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.ui.bean.ReportBean;
import com.emao.application.ui.callback.OnRecyclerViewItemClickListener;

import java.util.List;

/**
 *
 * @author keybon
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private Context mContext;
    private List<ReportBean> list;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public ReportAdapter(Context context, List<ReportBean> list){
        this.mContext = context;
        this.list = list;
    }

    public void setList(List<ReportBean> list) {
        this.list = list;
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_report_item,parent,false);
        ReportViewHolder viewHolder = new ReportViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onRecyclerViewItemClickListener != null){
                    onRecyclerViewItemClickListener.onItemClick(v, (Integer) v.getTag());
                    notifyDataSetChanged();
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReportViewHolder holder, int position) {
        ReportBean reportBean = list.get(position);
        holder.itemname.setText(reportBean.getItemname());
        holder.checkBox.setChecked(reportBean.getSelect());
        holder.itemView.setTag(position);
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ReportViewHolder extends RecyclerView.ViewHolder{

        private TextView itemname;
        private CheckBox checkBox;

        public ReportViewHolder(View itemView) {
            super(itemView);
            itemname = itemView.findViewById(R.id.report_item_name);
            checkBox = itemView.findViewById(R.id.report_item_checkbox);
        }
    }
}
