package com.tonystark.android.xpanel;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tony on 2018/2/15.
 */

public class XPanelDragMotionDetection extends ViewDragHelper.Callback {

    private ViewGroup mDragView;

    private ViewGroup mDragContainer;

    private ViewDragHelper mDragHelper;

    private IXPanelListScrollCtrl mScrollCtrl;

    private Context mContext;

    private boolean isChuttyMode;

    private boolean isCeiling;

    private boolean isOriginState;

    private int mOriginTop;

    private float mKickBackPercent;

    private int mOffsetPixel;

    private int mBaseLinePixel;

    private boolean isCanFling;

    private boolean isDragUp;

    private boolean isInFling;

    private boolean isInBaseLine;

    private OnXPanelMotionListener mOnXPanelMotionListener;

    public XPanelDragMotionDetection(ViewGroup dragView, ViewGroup dragContainer, IXPanelListScrollCtrl scrollCtrl) {
        mDragView = dragView;
        mDragContainer = dragContainer;
        mContext = mDragContainer.getContext();
        mScrollCtrl = scrollCtrl;
        mDragHelper = ViewDragHelper.create(mDragContainer, 1.0f, this);
        isOriginState = true;
        mKickBackPercent = 0.5f;
        mBaseLinePixel = 0;
    }

    @Override
    public boolean tryCaptureView(View child, int pointerId) {
        return child == mDragView;
    }

    @Override
    public int clampViewPositionVertical(View child, int top, int dy) {
        int containerHeight = mDragContainer.getMeasuredHeight();

        //resolve base line
        if (dy > 0) {
            //move down
            int currentHeight = containerHeight - top;
            int exposedHeight = containerHeight - mOriginTop;
            if (mBaseLinePixel > exposedHeight) {
                mBaseLinePixel = exposedHeight;
            }
            if (currentHeight <= mBaseLinePixel) {
                setScrollLock(false);
                isInBaseLine = true;
                return containerHeight - mBaseLinePixel;
            } else {
                isInBaseLine = false;
            }
        } else {
            setScrollLock(true);
            isInBaseLine = false;
        }

        //resolve list can not scroll,fixed height
        if (!mScrollCtrl.canScroll()) {
            int offset = -mDragView.getMeasuredHeight() + containerHeight;
            if (top <= offset) {
                return offset;
            } else {
                return top;
            }
        }

        //resolve list is not fill parent
        if (mDragView.getMeasuredHeight() < containerHeight) {
            int smallestTop = containerHeight - mDragView.getMeasuredHeight();
            if (smallestTop >= 0 && top < smallestTop) {
                return smallestTop;
            }
        } else if (top - dy == 0) {//resolve list drag to container top
            //resolve list could scroll or not
            if (dy > 0) {
                //move down
                if (!mScrollCtrl.isScrollInBegin()) {
                    Log.i("DragVer", "move down");
                    return 0;
                }
            } else {
                //move up
                if (!mScrollCtrl.isScrollInEnd()) {
                    Log.i("DragVer", "move up");
                    return 0;
                }
            }
        }

        //resolve drag not out to screen top.
        if (top < 0) {
            return 0;
        }

        return top;
    }

    @Override
    public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
        mOffsetPixel = mDragContainer.getMeasuredHeight() - top;
        isDragUp = dy < 0;

        if (top == mOriginTop) {
            isOriginState = true;
        } else {
            isOriginState = false;
        }

        if (mOnXPanelMotionListener != null) {
            int offset = getOffsetPixel();

            if (isInFling) {
                mOnXPanelMotionListener.OnDrag(DragMotion.DRAG_FLING, offset);
            } else {
                if (isDragUp) {
                    mOnXPanelMotionListener.OnDrag(DragMotion.DRAG_UP, offset);
                } else {
                    if (!isInBaseLine) {
                        mOnXPanelMotionListener.OnDrag(DragMotion.DRAG_DOWN, offset);
                    }
                }
            }

            if (top <= 0 != isCeiling) {//call once
                mOnXPanelMotionListener.OnCeiling(top <= 0);
            }
        }

        isCeiling = top <= 0;
        Log.i("Position", "mOffsetPixel:" + mOffsetPixel +
                " mOriginTop:" + mOriginTop +
                " isCeiling:" + isCeiling +
                " isOriginState:" + isOriginState +
                " isDragUp" + isDragUp);

    }

    @Override
    public void onViewCaptured(View capturedChild, int activePointerId) {

    }

    @Override
    public void onViewReleased(View releasedChild, float xvel, float yvel) {
        if (isChuttyMode) {
            float threshold = mDragContainer.getMeasuredHeight() * (mKickBackPercent);

            if (mOffsetPixel >= threshold) {//before touch the captured view ,view state is origin state.
                int top = Math.max(mDragContainer.getMeasuredHeight() - mDragView.getMeasuredHeight(), 0);
                mDragHelper.settleCapturedViewAt(0, top);
            } else {
                mDragHelper.settleCapturedViewAt(0, mOriginTop);
            }
        }
        fling();
        ViewCompat.postInvalidateOnAnimation(mDragContainer);
    }

    private void fling() {
        if (!isCanFling || isChuttyMode) {
            return;
        }
        isInFling = true;
        mDragHelper.flingCapturedView(0, mDragContainer.getMeasuredHeight() - mDragView.getMeasuredHeight(), 0, mOriginTop);
    }

    @Override
    public void onViewDragStateChanged(int state) {
        if (state == ViewDragHelper.STATE_IDLE) {
            mDragHelper.abort();
            isInFling = false;
            if (mOnXPanelMotionListener != null) {
                int offset = getOffsetPixel();
                mOnXPanelMotionListener.OnDrag(DragMotion.DRAG_STOP, offset);
            }
        }
    }

    private void setScrollLock(boolean isScroll) {
        if (!mScrollCtrl.isMeasureAll()) {
            mScrollCtrl.setScrollLock(isScroll);
        }
    }

    @Override
    public int getViewVerticalDragRange(View child) {
        return child.getMeasuredHeight();
    }

    public int getOffsetPixel() {
        return mOffsetPixel;
    }

    public boolean isOriginState() {
        return isOriginState;
    }

    /**
     * set the kick back percent when the chutty mode is true.
     *
     * @param kickBackPercent range in 0 ~ 1.
     */
    public void setKickBackPercent(float kickBackPercent) {
        if (kickBackPercent < 0) {
            kickBackPercent = 0.01f;
        }
        if (kickBackPercent > 1) {
            kickBackPercent = 1;
        }
        mKickBackPercent = kickBackPercent;
    }

    public void setOriginTop(int originTop) {
        mOriginTop = originTop;
        mOffsetPixel = mOriginTop;
    }

    public int getOriginTop() {
        return mOriginTop;
    }

    public void setBaseLinePixel(int baseLinePixel) {
        mBaseLinePixel = baseLinePixel;
    }

    public void setChuttyMode(boolean chuttyMode) {
        isChuttyMode = chuttyMode;
    }

    public ViewDragHelper getDragHelper() {
        return mDragHelper;
    }

    public void setCanFling(boolean canFling) {
        isCanFling = canFling;
    }

    public boolean isCeiling() {
        return isCeiling;
    }

    public boolean isInBaseLine() {
        return isInBaseLine;
    }

    public void setOnXPanelMotionListener(OnXPanelMotionListener onXPanelMotionListener) {
        mOnXPanelMotionListener = onXPanelMotionListener;
    }

    public interface DragMotion {
        /**
         * Drag down
         */
        int DRAG_DOWN = 1;
        /**
         * Drag up
         */
        int DRAG_UP = 2;
        /**
         * Drag stop
         */
        int DRAG_STOP = 3;
        /**
         * When not touch but in scrolling.
         */
        int DRAG_FLING = 4;
    }

    public interface OnXPanelMotionListener {
        /**
         * When user drag the view or fling.
         *
         * @param dragMotion drag motion.
         * @param offset     offset from original point.
         */
        void OnDrag(int dragMotion, int offset);

        /**
         * When the drag view touch ceil,top may be small than or equal 0.
         *
         * @param isCeiling true is in ceiling.
         */
        void OnCeiling(boolean isCeiling);
    }
}
