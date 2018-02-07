package com.emao.application.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emao.application.R;

import java.util.List;

/**
 *
 * @author keybon
 */

public class TemplateItemPopAdapter extends BaseAdapter {

    private List<Drawable> mList ;
    private Context mContext;

    public TemplateItemPopAdapter(Context context,List<Drawable> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
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
        TemplateItemPopViewHolder viewHolder ;
        if (convertView == null) {
            viewHolder = new TemplateItemPopViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.template_pop_item,parent,false);
            viewHolder.template_item_img = convertView.findViewById(R.id.template_item_img);
            viewHolder.template_item_v = convertView.findViewById(R.id.template_item_v);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TemplateItemPopViewHolder) convertView.getTag();
        }
        viewHolder.template_item_img.setScaleType(ImageView.ScaleType.CENTER);
        viewHolder.template_item_img.setImageDrawable(mList.get(position));
        return convertView;
    }

    class TemplateItemPopViewHolder {
        private ImageView template_item_img;
        private TextView template_item_v;
    }
}
