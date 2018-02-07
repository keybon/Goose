package com.emao.application.ui.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.emao.application.ui.application.MainApp;
import com.emao.application.ui.bean.OpinionBean;

import java.util.List;

/**
 * @author keybon
 */

public class KeybonItemDecoration extends RecyclerView.ItemDecoration {


    /**
     * 用于存放测量文字Rect
     */
    private Rect mBounds;

    private int titleHeight;
    private Paint mPaint;
    private List<OpinionBean> datas;

    /**
     * title背景色
     */
    private final int COLOR_TITLE_BG = Color.parseColor("#e5e8ea");
    /**
     * title颜色
     */
    private final int COLOR_TITLE_FONT = Color.parseColor("#31363B");

    public KeybonItemDecoration(List<OpinionBean> datas) {
        this.datas = datas;


        mPaint = new Paint();
        mBounds = new Rect();
        this.titleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, MainApp.IMApp.getResources().getDisplayMetrics());
        int mTitleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 17, MainApp.IMApp.getResources().getDisplayMetrics());
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int position = params.getViewLayoutPosition();
            if (datas == null || datas.isEmpty() || position > datas.size() - 1 || position < 0) {
                continue;//越界
            }
            //我记得Rv的item position在重置时可能为-1.保险点判断一下吧
            if (position > -1) {
                //等于0肯定要有title的
                if (position == 0) {
                    drawTitleArea(c, left, right, child, params, position);
                } else {
                    //其他的通过判断
                    String ctime = datas.get(position).getCtime();
                    String nextTime = datas.get(position - 1).getCtime();

                    if (null != ctime && !ctime.substring(0, 10).equals(nextTime.substring(0, 10))) {
                        drawTitleArea(c, left, right, child, params, position);
                    } else {

                    }

                }
            }
        }
    }

    /**
     * 绘制Title区域背景和文字的方法
     */
    private void drawTitleArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {//最先调用，绘制在最下层
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(left, child.getTop() - params.topMargin - titleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);

        mPaint.getTextBounds(datas.get(position).getCtime(), 0, datas.get(position).getCtime().length(), mBounds);
        c.drawText(datas.get(position).getCtime().substring(0,10), child.getPaddingLeft() + 25, child.getTop() - params.topMargin - (titleHeight / 2 - mBounds.height() / 2), mPaint);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        // 参数说明：
        // 1. outRect：全为 0 的 Rect（包括着Item）
        // 2. view：RecyclerView 中的 视图Item
        // 3. parent：RecyclerView 本身
        // 4. state：状态    px
        int itemPosition = parent.getChildAdapterPosition(view);
        // 获得每个Item的位置

        if (datas == null || datas.isEmpty() || itemPosition > datas.size() - 1 || itemPosition < 0) {
            return;//越界
        }

        String ctime = datas.get(itemPosition).getCtime();


        //只有一条数据 画
        if (itemPosition == 0) {
            outRect.set(0, titleHeight, 0, 0);
        } else {

            if (datas.size() != (itemPosition - 1)) {

                String nextTime = datas.get(itemPosition - 1).getCtime();

                if (null != ctime && !ctime.substring(0, 10).equals(nextTime.substring(0, 10))) {
                    outRect.set(0, titleHeight, 0, 0);
                } else {
                    outRect.set(0, 0, 0, 0);
                }

            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
//        int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
//        if (datas == null || datas.isEmpty() || position > datas.size() - 1 || position < 0) {
//            return;//越界
//        }
//        //获取要悬停的title
//        String tag = datas.get(position).getCtime();
//        if (tag != null) {
//            //出现一个奇怪的bug，有时候child为空，所以将 child = parent.getChildAt(i)。-> parent.findViewHolderForLayoutPosition(pos).itemView
//            View child = parent.findViewHolderForLayoutPosition(position).itemView;
//            //定义一个flag，Canvas是否位移过的标志
//            boolean flag = false;
//            //防止数组越界（一般情况不会出现）
//            if (position > 0) {
//                //当前第一个可见的Item的tag，不等于其后一个item的tag，说明悬浮的View要切换了
//                String nextCtime = datas.get(position - 1).getCtime().substring(0, 10);
//                if (!tag.substring(0,10).equals(nextCtime)) {
//                    //当第一个可见的item在屏幕中还剩的高度小于title区域的高度时，我们也该开始做悬浮Title的“交换动画”
////                    if (child.getHeight() + child.getTop() < titleHeight) {
//                    if (child.getTop() < titleHeight) {
//                        //每次绘制前 保存当前Canvas状态，
//                        c.save();
//                        flag = true;
//                        //上滑时，将canvas上移 （y为负数） ,所以后面canvas 画出来的Rect和Text都上移了，有种切换的“动画”感觉
//                        c.translate(0, child.getHeight() + child.getTop() - titleHeight);
//                    }
//                }
//            }
//            mPaint.setColor(COLOR_TITLE_BG);
//            c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + titleHeight, mPaint);
//            mPaint.setColor(COLOR_TITLE_FONT);
//
//            mPaint.getTextBounds(tag, 0, tag.length(), mBounds);
//            c.drawText(tag, child.getPaddingLeft() + 25, parent.getPaddingTop() + titleHeight - (titleHeight / 2 - mBounds.height() / 2), mPaint);
//            if (flag) {
//                c.restore();//恢复画布到之前保存的状态
//            }
//        }
    }
}
