package com.emao.application.ui.photo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.emao.application.R;
import com.emao.application.ui.utils.fresco.FrescoImageUtils;

import java.util.List;

public class ImageGridView extends GridView {

    private GradientDrawable mCoverNormal;
    private Drawable mCoverChecked;

	private ImageAdapter adapter;

    private OnImageSelectedListener mOnImageSelectedListener;

	public ImageGridView(Context context) {
		super(context);
		init();
	}

	public ImageGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ImageGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
        mCoverNormal = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{Color.parseColor("#02000000"), Color.parseColor("#40000000")});
        mCoverChecked = new ColorDrawable(Color.parseColor("#77000000"));

        setOverScrollMode(OVER_SCROLL_NEVER);
		adapter = new ImageAdapter(getContext(), R.layout.photo_grid_item_image);
		setAdapter(adapter);
	}

    public interface OnImageSelectedListener {
        public void onImageSelected(GridView parent, int position, ImageItem image, boolean isSelected);
        public void onImageItemClicked(ImageItem image, int position);
    }

    public void setOnImageSelectedListener(OnImageSelectedListener onImageSelectedListener) {
        this.mOnImageSelectedListener = onImageSelectedListener;
    }

    public void initSelectImages(List<ImageItem> selectedImages) {
        if(selectedImages != null) {
            int s = adapter.getCount();
            for(int i = 0; i < s; i++) {
                ImageItem item = adapter.getItem(i);
                if(selectedImages.contains(item)) {
                    setItemChecked(i, true);
                }
                else {
                    setItemChecked(i, false);
                }
            }
        }
    }

    public void initImages(List<ImageItem> images) {
        setAdapter(null);
        adapter.clear();
        adapter.addAll(images);
        setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void initImages(List<ImageItem> selectedImages, List<ImageItem> images) {
		initImages(images);
        initSelectImages(selectedImages);
	}

    public void notifyDataSetChanged() {
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
	
	class ImageAdapter extends ArrayAdapter<ImageItem> {
		
		private LayoutInflater inflater;

		public ImageAdapter(Context context, int resource) {
			super(context, resource);
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(null == convertView) {
				convertView = inflater.inflate(R.layout.photo_grid_item_image, parent, false);
				holder = new ViewHolder();
				holder.image = (SimpleDraweeView) convertView.findViewById(R.id.image_image);
                holder.checkCover = (ImageView) convertView.findViewById(R.id.check_cover);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			final ImageItem imageItem = getItem(position);
            FrescoImageUtils.getInstance()
                    .createSdcardBuilder(imageItem.getPath())
//                    .setResizeOptions(200,200)
//                    .setFailDrawable(R.drawable.image_demaged)
                    .build()
                    .showSdcardImage(holder.image);
//            Glide.with(getContext()).load(imageItem.getPath()).asBitmap().error(R.drawable.image_demaged).into(holder.image);
            if(getChoiceMode() == CHOICE_MODE_MULTIPLE) {
                boolean checked = isItemChecked(position);
                holder.checkCover.setImageDrawable(checked?mCoverChecked:mCoverNormal);
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(checked);
                holder.checkBox.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isChecked = ((CheckBox)v).isChecked();
                        setItemChecked(position, isChecked);
                        if(mOnImageSelectedListener != null) {
                            mOnImageSelectedListener.onImageSelected(ImageGridView.this, position, imageItem, isChecked);
                        }
                    }
                });
            }
            else {
                holder.checkCover.setImageDrawable(mCoverNormal);
                holder.checkBox.setVisibility(View.GONE);
            }
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnImageSelectedListener != null) {
                        mOnImageSelectedListener.onImageItemClicked(imageItem, position);
                    }
                }
            });
			return convertView;
		}
		
	}
	
	static class ViewHolder {
		SimpleDraweeView image;
        ImageView checkCover;
        CheckBox checkBox;
	}
	
}
