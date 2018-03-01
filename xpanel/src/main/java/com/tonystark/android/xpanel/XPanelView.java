package com.tonystark.android.xpanel;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


/**
 * XPanelView
 * <p>
 * Created by Tony on 2018/2/15.
 */
public class XPanelView extends FrameLayout {
    /**
     * The view that will be drag
     * If you doesn't set list view
     * XPanel will select the default list view {@link android.support.v7.widget.RecyclerView}
     */
    protected XPanelRecyclerView mListView;
    /**
     * The list data adapter will be used, when the drag view is default one.
     */
    protected AbsXPanelAdapter mAdapter;
    /**
     * The layout will be placed before list view.
     */
    protected View mHeaderLayout;
    /**
     * The view which will be drag.
     */
    protected LinearLayout mDragViewGroup;
    /**
     * Callback when the user touch or drag the view.
     */
    protected XPanelDragMotionDetection mDetection;
    /**
     * Expose whole panel in parent layout percent.
     * values range in {0 - 1} can not be zero.
     */
    protected float mExposedPercent;

    public XPanelView(Context context) {
        super(context);
        init(context);
    }

    public XPanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public XPanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public XPanelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        FrameLayout.LayoutParams groupParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        mDragViewGroup = new LinearLayout(context);
        mDragViewGroup.setOrientation(LinearLayout.VERTICAL);

        addView(mDragViewGroup, groupParams);
        initListView(context);
        initDragHelper();
        mExposedPercent = 0.3f;//the default value is ten percent.
    }

    private void initListView(Context context) {
        if (mListView == null) {
            mListView = initRecyclerView(context);
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        if (!(params instanceof LinearLayout.LayoutParams)) {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mDragViewGroup.addView(mListView, params);
    }

    private void initDragHelper() {
        mDetection = new XPanelDragMotionDetection(mDragViewGroup, this, mListView);
    }

    @Override
    public void computeScroll() {
        if (mDetection.getDragHelper().continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDetection.getDragHelper().shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetection.getDragHelper().processTouchEvent(event);
        return true;
    }

    protected XPanelRecyclerView initRecyclerView(Context context) {
        XPanelRecyclerView recyclerView = new XPanelRecyclerView(context);
        XPanelRecyclerViewLayoutManager manager = new XPanelRecyclerViewLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        return recyclerView;
    }

    /**
     * Set a new adapter to provide child views on demand.
     * <p>
     * When adapter is changed, all existing views are recycled back to the pool. If the pool has
     * only one adapter, it will be cleared.
     *
     * @param adapter The new adapter to set, or null to set no adapter.
     */
    public void setAdapter(AbsXPanelAdapter adapter) {
        mListView.setAdapter(adapter);
        mAdapter = adapter;
    }

    /**
     * Retrieves the previously set adapter or null if no adapter is set.
     *
     * @return The previously set adapter
     * @see #setAdapter(AbsXPanelAdapter)
     */
    public AbsXPanelAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Set a new header to provide a layout ahead of list.
     *
     * @param headerLayout The new layout to set.
     */
    public void setHeaderLayout(View headerLayout) {
        mDragViewGroup.removeView(mHeaderLayout);
        if (headerLayout == null) {
            return;
        }
        mHeaderLayout = headerLayout;
        mDragViewGroup.addView(mHeaderLayout, 0);
    }

    /**
     * Make the list can not scroll and fixed the height of list.
     *
     * @param isMeasureAll true is recycler view can't slip,it will measure all item.
     */
    public void setMeasureAll(boolean isMeasureAll) {
        mListView.getLayoutManager().setMeasureAll(isMeasureAll);
    }

    /**
     * Returns an {@link RecyclerView.ItemDecoration} previously added to this RecyclerView.
     *
     * @param index The index position of the desired ItemDecoration.
     * @return the ItemDecoration at index position, or null if invalid index.
     */
    public RecyclerView.ItemDecoration getItemDecorationAt(int index) {
        return mListView.getItemDecorationAt(index);
    }

    /**
     * Add an {@link RecyclerView.ItemDecoration} to this RecyclerView. Item decorations can
     * affect both measurement and drawing of individual item views.
     * <p>
     * <p>Item decorations are ordered. Decorations placed earlier in the list will
     * be run/queried/drawn first for their effects on item views. Padding added to views
     * will be nested; a padding added by an earlier decoration will mean further
     * item decorations in the list will be asked to draw/pad within the previous decoration's
     * given area.</p>
     *
     * @param decor Decoration to add
     */
    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mListView.addItemDecoration(decor);
    }

    /**
     * Add an {@link RecyclerView.ItemDecoration} to this RecyclerView. Item decorations can
     * affect both measurement and drawing of individual item views.
     * <p>
     * <p>Item decorations are ordered. Decorations placed earlier in the list will
     * be run/queried/drawn first for their effects on item views. Padding added to views
     * will be nested; a padding added by an earlier decoration will mean further
     * item decorations in the list will be asked to draw/pad within the previous decoration's
     * given area.</p>
     *
     * @param decor Decoration to add
     * @param index Position in the decoration chain to insert this decoration at. If this value
     *              is negative the decoration will be added at the end.
     */
    public void addItemDecoration(RecyclerView.ItemDecoration decor, int index) {
        mListView.addItemDecoration(decor, index);
    }

    /**
     * Sets the {@link RecyclerView.ItemAnimator} that will handle animations involving changes
     * to the items in this RecyclerView. By default, RecyclerView instantiates and
     * uses an instance of {@link DefaultItemAnimator}. Whether item animations are
     * enabled for the RecyclerView depends on the ItemAnimator and whether
     * the LayoutManager {@link RecyclerView.LayoutManager#supportsPredictiveItemAnimations()
     * supports item animations}.
     *
     * @param animator The ItemAnimator being set. If null, no animations will occur
     *                 when changes occur to the items in this RecyclerView.
     */
    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mListView.setItemAnimator(animator);
    }

    /**
     * Control the whole panel exposed height in parent layout.
     * Avoid modify it in animate.because it very unfriendly with performance.
     * Default value is 30 percent.
     *
     * @param exposedPercent the range in 0 ~ 1. 1 is mean the panel is exposed 100 percent
     */
    public void setExposedPercent(float exposedPercent) {
        exposedPercent = exposedPercent < 0 ? 0.01f : exposedPercent > 1 ? 1 : exposedPercent;
        mExposedPercent = exposedPercent;
        requestLayout();
    }

    /**
     * When the view is in chutty mode,this value is valuable.
     * Drag view and release ,the view will be kick back if release position is not bigger than this percent of parent viewgroup.
     * Default value is 50 percent.you should be bigger than {@link #mExposedPercent}
     *
     * @param percent the range in 0 ~ 1.
     */
    public void setKickBackPercent(float percent) {
        mDetection.setKickBackPercent(percent);
    }

    /**
     * Make the drag view has some stick feeling. something like chutty.
     *
     * @param chuttyMode true is has chutty mode.
     */
    public void setChuttyMode(boolean chuttyMode) {
        mDetection.setChuttyMode(chuttyMode);
    }

    /**
     * Set the base line in parent height in pixel,it should smaller than exposed height.
     * the view will be blocked When drag position smaller than base line.
     *
     * @param pixel the pixel you want to exposed it.
     */
    public void setDragBaseLine(int pixel) {
        mDetection.setBaseLinePixel(pixel);
    }

    /**
     * Set the XPanel can fling.When your set the chutty mode is true,than this flag is invalid.
     *
     * @param isCanFling true is can fling.
     */
    public void setCanFling(boolean isCanFling) {
        mDetection.setCanFling(isCanFling);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!mListView.canScroll()) {
            int headerHeightPixel = 0;
            if (mHeaderLayout != null) {
                headerHeightPixel = mHeaderLayout.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mDragViewGroup.getLayoutParams();
            params.height = mListView.getMeasuredHeight() + headerHeightPixel;
            mDragViewGroup.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = mDragViewGroup.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mDragViewGroup.setLayoutParams(params);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View view = getChildAt(0);
        if (view instanceof LinearLayout) {
            if (!mDetection.isOriginState()) {//if is not origin state,the window height definitely changed, so need to restore the height
                int childLeft = getPaddingLeft();
                int topShouldBe = getMeasuredHeight() - mDetection.getOffsetPixel();
                view.layout(childLeft,
                        topShouldBe,
                        childLeft + view.getMeasuredWidth(),
                        topShouldBe + view.getMeasuredHeight());
            } else {
                int childLeft = getPaddingLeft();
                int childTop = getPaddingTop();
                int topOffset = 0;
                if (getMeasuredHeight() * mExposedPercent > view.getMeasuredHeight()) {
                    topOffset = getMeasuredHeight() - view.getMeasuredHeight();
                } else {
                    topOffset = (int) (getMeasuredHeight() - getMeasuredHeight() * (mExposedPercent));
                }
                view.layout(childLeft,
                        childTop + topOffset,
                        childLeft + view.getMeasuredWidth(),
                        childTop + topOffset + view.getMeasuredHeight());
                mDetection.setOriginTop(childTop + topOffset);
            }
        }
    }

    public void setOnXPanelMotionListener(XPanelDragMotionDetection.OnXPanelMotionListener listener) {
        if (mDetection != null) {
            mDetection.setOnXPanelMotionListener(listener);
        }
    }
}
