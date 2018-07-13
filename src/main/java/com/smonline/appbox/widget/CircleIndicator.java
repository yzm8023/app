package com.smonline.appbox.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class CircleIndicator extends View implements ViewPager.OnPageChangeListener {

    private static final float DEF_LAYOUT_HEIGHT = 10.0f;
    private static final float DEF_CIRCLE_RADIUS = 5.0f;
    private static final float DEF_CIRCLE_PADDING = 15.0f;
    private static final float DEF_ROUND_RADIUS = 5.0f;

    private float mWidth;
    private float mHeight;
    private float mCircleRadius;
    private float mCirclePadding;
    private float mLineRectWidth;
    private float mRoundRadius;

    private int mCurSelectedPage;
    private int mIndicatorCircleCount;

    private Context mContext;
    private Paint mCirclePaint;
    private Paint mRectLinePaint;
    private RectF mLineRect = new RectF();

    private ViewPager mViewPager;

    public CircleIndicator(Context context) {
        this(context, null);
    }

    public CircleIndicator(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public CircleIndicator(Context context, AttributeSet attr, int defStyleAttr){
        super(context, attr, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint.setColor(mContext.getResources().getColor(android.R.color.darker_gray));
        mCirclePaint.setAntiAlias(true);

        mRectLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectLinePaint.setStyle(Paint.Style.FILL);
        mRectLinePaint.setColor(mContext.getResources().getColor(android.R.color.holo_blue_light));
        mRectLinePaint.setAntiAlias(true);

        mHeight = dip2px(mContext, DEF_LAYOUT_HEIGHT);
        mCircleRadius = dip2px(mContext, DEF_CIRCLE_RADIUS);
        mCirclePadding = dip2px(mContext, DEF_CIRCLE_PADDING);
        mRoundRadius = dip2px(mContext, DEF_ROUND_RADIUS);
        mLineRectWidth = mCircleRadius * 4;
    }

    public void setViewPager(ViewPager viewPager){
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(this);
        mIndicatorCircleCount = mViewPager.getAdapter().getCount();
        mWidth = mCircleRadius * 2 * mIndicatorCircleCount + mCirclePadding * (mIndicatorCircleCount - 1) + (mLineRectWidth / 2 - mCircleRadius) * 2;
        requestLayout();
        mViewPager.setCurrentItem(0, false);
    }

    private int dip2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int)mWidth, (int)mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mIndicatorCircleCount; i++) {
            float cx = mLineRectWidth / 2 - mCircleRadius + mCircleRadius + (mCircleRadius * 2 + mCirclePadding) * i;
            float cy = mHeight / 2;
            canvas.drawCircle(cx, cy, mCircleRadius, mCirclePaint);
        }

        float curCircleX = mLineRectWidth / 2 - mCircleRadius + mCircleRadius + (mCircleRadius * 2 + mCirclePadding) * mCurSelectedPage;
        float lineRectLeft = curCircleX - mLineRectWidth / 2;
        float lineRectRight = lineRectLeft + mLineRectWidth;
        float lineRectTop = 0;
        float lineRectBottom = mHeight;
        mLineRect.set(lineRectLeft, lineRectTop, lineRectRight, lineRectBottom);
        canvas.drawRoundRect(mLineRect, mRoundRadius, mRoundRadius, mRectLinePaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurSelectedPage = position;
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
