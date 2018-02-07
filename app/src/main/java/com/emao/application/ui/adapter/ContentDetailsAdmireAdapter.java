package com.emao.application.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.emao.application.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 *
 * @author keybon
 */

public class ContentDetailsAdmireAdapter extends BaseAdapter {



    private ArrayList<String> mList ;
    private Context mContext;

    public ContentDetailsAdmireAdapter(Context context,ArrayList<String> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setmList(ArrayList<String> mList) {
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContentDetailsAdmireAdapter.ContentDetailsAdmireViewHolder viewHolder ;
        if (convertView == null) {
            viewHolder = new ContentDetailsAdmireAdapter.ContentDetailsAdmireViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_details_admire_item,parent,false);
            viewHolder.portrait = convertView.findViewById(R.id.adapter_details_admire_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ContentDetailsAdmireAdapter.ContentDetailsAdmireViewHolder) convertView.getTag();
        }

        try {
            viewHolder.portrait.setImageURI(mList.get(position));
        } catch (IllegalStateException illegalstateException){

        }
        return convertView;
    }

    class ContentDetailsAdmireViewHolder {
        private SimpleDraweeView portrait;
    }

}
