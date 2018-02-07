package com.emao.application.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emao.application.R;
import com.emao.application.ui.bean.SingleChatBean;
import com.emao.application.ui.callback.OnRecyclerViewItemClickListener;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.Constants;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * @author keybon
 */

public class SingleChatAdapter extends RecyclerView.Adapter<SingleChatAdapter.ChatViewHolder> implements View.OnClickListener{

    private final int SINGLE_CHAT_LEFT = 1;
    private final int SINGLE_CHAT_RIGHT = 2;

    private List<SingleChatBean> list;
    private Context mContext;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    private String id;

    public SingleChatAdapter(Context context, List<SingleChatBean> list,String id) {
        this.mContext = context;
        this.list = list;
        this.id = id;
    }

    public void setList(List<SingleChatBean> list) {
        this.list = list;
    }

    @Override
    public SingleChatAdapter.ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        ChatViewHolder viewHolder = null;
        if( viewType == SINGLE_CHAT_LEFT ){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_single_chat_left,parent,false);
            viewHolder = new SingChatViewLeftHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_single_chat_right,parent,false);
            viewHolder = new SingChatViewRightHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SingleChatAdapter.ChatViewHolder holder, int position) {
        SingleChatBean singleChatBean = list.get(position);
        holder.onBinderView(singleChatBean,position);

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClick(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if(id.equals(list.get(position).getFid())){
            return SINGLE_CHAT_RIGHT;
        }
        return SINGLE_CHAT_LEFT;
    }

    /**
     * 新消息刷新列表方法
     */
    public void notifyAddMessage(Activity activity,final RecyclerView recyclerView) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyItemInserted(list.size());
                recyclerView.smoothScrollToPosition(list.size());
                notifyItemRangeChanged(0, list.size());
            }
        });
    }

    /**
     * 删除消息刷新列表方法
     */
    public void notifyDeleteMessage(Activity activity,final int position) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(position);
                if (position != list.size()) {
                    notifyItemRangeChanged(position, list.size() - position);
                }
            }
        });
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        private TextView sendTime;

        public ChatViewHolder(View itemView) {
            super(itemView);
            sendTime = itemView.findViewById(R.id.tv_sendtime);
        }

        public void onBinderView(SingleChatBean singleChatBean,int position){
            setTime(position);
        }

        public void setTime(int position){
            SingleChatBean f1 = list.get(position);
            long f1Time = CommonUtils.getLongTime(f1.getCtime());
            String talkTime = CommonUtils.getNaviItemDate(f1Time);
            boolean isShow = false;
            if (position != 0) {
                SingleChatBean f2 = list.get(position - 1);
                long f2Time = CommonUtils.getLongTime(f2.getCtime());
                long diff = f1Time - f2Time;
                diff = Math.abs(diff);
                // 小于间隔
                if (diff < Constants.INTERVAL) {
                    isShow = showTimeByCount(position);
                }else{
                    isShow=true;
                }
            }
            showTimeHint(talkTime,isShow);
        }



        private void showTimeHint(String talkTime,boolean isShow){
            if(sendTime != null){
                sendTime.setText(talkTime);
                if(isShow){
                    sendTime.setVisibility(View.VISIBLE);
                } else {
                    sendTime.setVisibility(View.GONE);
                }
            }
        }

        /**
         * 方法简介：如果连续N条消息都没有显示消息时间，第N+1条显示 输入项说明： 返回项说明：
         *
         * @param pos index of current message
         * @return
         */
        private boolean showTimeByCount(int pos) {
            int COUNT = Constants.TIMECOUNT;
            if (pos < COUNT) {
                return false;
            }
            return true;
        }

    }
    class SingChatViewLeftHolder extends ChatViewHolder{

        private TextView content;
        private SimpleDraweeView portrait;

        public SingChatViewLeftHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.single_chat_left_content);
            portrait = itemView.findViewById(R.id.single_chat_left_portrait);
        }

        @Override
        public void onBinderView(SingleChatBean singleChatBean, int position) {
            super.onBinderView(singleChatBean, position);
            content.setText(singleChatBean.getContent());
            portrait.setImageURI(singleChatBean.getHeadimgurl());
            portrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mContext.startActivity(new Intent(mContext, UserInfoActivity.class));
                }
            });
        }
    }
    class SingChatViewRightHolder extends ChatViewHolder{
        private TextView content;
        private SimpleDraweeView portrait;

        public SingChatViewRightHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.single_chat_right_content);
            portrait = itemView.findViewById(R.id.single_chat_right_portrait);
        }

        @Override
        public void onBinderView(SingleChatBean singleChatBean, int position) {
            super.onBinderView(singleChatBean, position);
            content.setText(singleChatBean.getContent());
            portrait.setImageURI(singleChatBean.getHeadimgurl());
            portrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mContext.startActivity(new Intent(mContext, UserInfoActivity.class));
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        if(onRecyclerViewItemClickListener != null){
            onRecyclerViewItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }
}
