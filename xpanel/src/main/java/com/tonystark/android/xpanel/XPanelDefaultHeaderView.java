package com.tonystark.android.xpanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tony on 2018/1/9.
 */
public class XPanelDefaultHeaderView extends View {

    private static final int DRAG_BAR_WIDTH_DP = 40;

    private static final int DRAG_BAR_HEIGHT_DP = 5;

    private Paint mRectPaint = null;

    private Paint mShadowPaint = null;

    private Paint mDragBarPaint = null;

    private RectF mBGRect = new RectF();

    private RectF mDragRect = new RectF();

    private float mRoundRadius;
    /**
     * 变化范围从0-1,圆角率
     */
    private float mRoundRadiusRate = 1;
    /**
     * 手柄弯曲度数,变化范围从30-180度,角为一三象限角
     */
    private float mDragBarAngle = 180;

    private float mShadowHeight;

    private int mShadowColor = 0x1941414c;

    private int mDragBarColor = 0x1A000000;

    private int mForegroundColor = Color.WHITE;

    private float mShadowRadius = 0;

    private boolean isCanDrag = false;

    private float mDragBarWidthPx = 0;

    private float mDragBarHeightPx = 0;

    private float mDragBarRadius = 0;


    public XPanelDefaultHeaderView(Context context) {
        this(context, null);
    }

    public XPanelDefaultHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XPanelDefaultHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public XPanelDefaultHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Paint.Style.FILL);
        mRectPaint.setColor(Color.BLACK);
        mRectPaint.setDither(true);

        mShadowRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, getResources().getDisplayMetrics());
        mRoundRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 17, getResources().getDisplayMetrics());
        mDragBarRadius = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        mShadowHeight = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        mDragBarWidthPx = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, DRAG_BAR_WIDTH_DP, getResources().getDisplayMetrics());
        mDragBarHeightPx = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, DRAG_BAR_HEIGHT_DP, getResources().getDisplayMetrics());
        mShadowPaint = new Paint(mRectPaint);
        mDragBarPaint = new Paint(mRectPaint);
        mDragBarPaint.setColor(mDragBarColor);
        int height = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (height + getShadowHeight()));
        setLayoutParams(params);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mBGRect.left = getPaddingLeft();
        mBGRect.top = getPaddingTop() + getShadowHeight();
        mBGRect.right = mBGRect.left + getMeasuredWidth() - getPaddingRight();
        mBGRect.bottom = mBGRect.top + getMeasuredHeight() - getPaddingBottom();

        mDragRect.left = mBGRect.left + (getMeasuredWidth() / 2f - mDragBarWidthPx / 2f);
        mDragRect.top = mBGRect.top / 2f + ((mBGRect.bottom - mBGRect.top) - mDragBarHeightPx) / 2f;
        mDragRect.right = mDragRect.left + mDragBarWidthPx;
        mDragRect.bottom = mDragRect.top + mDragBarHeightPx;
    }

    public float getShadowHeight() {
        return mShadowHeight + mShadowRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mShadowPaint.setShadowLayer(mShadowRadius, 0, -mShadowHeight, mShadowColor);
        mShadowPaint.setColor(mForegroundColor);
        mRectPaint.setColor(mForegroundColor);
        drawForeground(canvas);
        drawDragBar(canvas);
    }

    private void drawForeground(Canvas canvas) {
        float radius = mRoundRadius * mRoundRadiusRate;
        //绘制圆角
        canvas.drawRoundRect(mBGRect, radius, radius, mShadowPaint);
        //处理不需要的圆角部分(左下角)
        canvas.drawRect(0, mBGRect.bottom - radius, radius, mBGRect.bottom, mRectPaint);
        //处理不需要的圆角部分(右下角)
        canvas.drawRect(mBGRect.right - radius, mBGRect.bottom - radius, mBGRect.right, mBGRect.bottom,
                mRectPaint);
    }

    private void drawDragBar(Canvas canvas) {
        if (isCanDrag) {
            if (mDragBarAngle == 180) {
                //绘制拖把
                canvas.drawRoundRect(mDragRect, mDragBarRadius, mDragBarRadius, mDragBarPaint);
            } else {
                float angle = mDragBarAngle;
                //准备左边部分
                Path pathLeft = new Path();
                pathLeft.setFillType(Path.FillType.WINDING);
                RectF rectLeft = new RectF(mDragRect);
                rectLeft.right = (rectLeft.right - rectLeft.left) / 2 + rectLeft.left;
                pathLeft.addRoundRect(rectLeft,
                        new float[]{mDragBarRadius, mDragBarRadius, 0, 0, 0, 0, mDragBarRadius, mDragBarRadius},
                        Path.Direction.CCW);
                //准备右边部分
                Path pathRight = new Path();
                pathRight.setFillType(Path.FillType.WINDING);
                RectF rectRight = new RectF(mDragRect);
                rectRight.left = rectRight.left + (rectRight.right - rectRight.left) / 2;
                pathRight.addRoundRect(rectRight,
                        new float[]{0, 0, mDragBarRadius, mDragBarRadius, mDragBarRadius, mDragBarRadius, 0, 0},
                        Path.Direction.CCW);
                //准备中间的扇形
                RectF arcRectF = new RectF();
                float line = rectLeft.bottom - rectLeft.top;
                arcRectF.left = rectLeft.right - line;
                arcRectF.top = rectLeft.top - line;
                arcRectF.right = rectLeft.right + line;
                arcRectF.bottom = rectLeft.top + line;

                //计算偏移量
                float height = rectLeft.right - rectLeft.left;
                float translateY = (float) (Math.sin(Math.toRadians((180 - angle) / 2)) * height / 2);

                canvas.translate(0, translateY);
                //绘制扇形
//        canvas.drawRect(arcRectF, mDragBarPaint);
                canvas.drawArc(arcRectF, 90 - ((180 - angle) / 2), 180 - angle, true, mDragBarPaint);
                //旋转至左边
                canvas.save();
                canvas.rotate((180 - angle) / 2, rectLeft.right, rectLeft.top);
                //绘制左边
                canvas.drawPath(pathLeft, mDragBarPaint);
                canvas.restore();
                //旋转到右边
                canvas.save();
                canvas.rotate(-((180 - angle) / 2), rectRight.left, rectRight.top);
                //绘制右边
                canvas.drawPath(pathRight, mDragBarPaint);
                canvas.restore();
                canvas.translate(0, -translateY);
            }
        }
    }

    public void setRoundRadiusRate(float rate) {
        //边界检查
        rate = rate > 1 ? 1 : (rate < 0 ? 0 : rate);
        mRoundRadiusRate = rate;
        invalidate();
    }

    public void setDragBarAngle(float dragBarAngle) {
        //边界检查
        dragBarAngle = dragBarAngle > 180 ? 180 : (dragBarAngle < 90 ? 90 : dragBarAngle);
        mDragBarAngle = dragBarAngle;
        invalidate();
    }

    public float getDragBarAngle() {
        return mDragBarAngle;
    }

    public float getRoundRadiusRate() {
        return mRoundRadiusRate;
    }

    public void setShadowHeight(int shadowHeightDp) {
        mShadowHeight = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, shadowHeightDp, getResources().getDisplayMetrics());
        requestLayout();
    }

    public void setShadowColor(int color) {
        mShadowColor = color;
    }

    public void setForegroundColor(int color) {
        if (mForegroundColor != color) {
            mForegroundColor = color;
            invalidate();
        }
    }

    public void setShadowRadius(int radiusDp) {
        mShadowRadius = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusDp, getResources().getDisplayMetrics());
        invalidate();
    }

    public void setCanDrag(boolean canDrag) {
        isCanDrag = canDrag;
        invalidate();
    }

    public void setDragBarColor(int dragBarColor) {
        mDragBarColor = dragBarColor;
        invalidate();
    }
}
