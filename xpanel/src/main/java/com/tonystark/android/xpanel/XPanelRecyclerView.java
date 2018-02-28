package com.tonystark.android.xpanel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Tony on 2018/2/15.
 */

public class XPanelRecyclerView extends RecyclerView implements IXPanelListScrollCtrl {

    private boolean isInBottom = false;

    private boolean isInTop = true;

    private boolean isMeasureAll = false;

    private boolean isScrollToEnd;

    public XPanelRecyclerView(Context context) {
        super(context);
        init();
    }

    public XPanelRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public XPanelRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    private void init() {
        //do nothing
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(new Runnable() {
            @Override
            public void run() {
                positionSolved();
            }
        });
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (dy > 0) {
            isScrollToEnd = true;
        } else {
            isScrollToEnd = false;
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == RecyclerView.SCROLL_STATE_IDLE || state == RecyclerView.SCROLL_STATE_SETTLING) {
            positionSolved();
        }
    }

    private void positionSolved() {
        LinearLayoutManager manager = (LinearLayoutManager) getLayoutManager();
        int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
        int totalItemCount = manager.getItemCount();

        if (lastVisibleItem == (totalItemCount - 1) && isScrollToEnd) {
            isInBottom = true;
        } else {
            isInBottom = false;
        }

        int firstVisibleItem = manager.findFirstCompletelyVisibleItemPosition();
        if (firstVisibleItem <= 0 && !isScrollToEnd) {
            isInTop = true;
        } else {
            isInTop = false;
        }
    }


    @Override
    public boolean isScrollInEnd() {
        return isInBottom;
    }

    @Override
    public boolean isScrollInBegin() {
        return isInTop;
    }

    @Override
    public boolean canScroll() {
        return getLayoutManager().canScrollVertically();
    }

    public XPanelRecyclerViewLayoutManager getLayoutManager() {
        return (XPanelRecyclerViewLayoutManager) super.getLayoutManager();
    }

    @Override
    public void setScrollLock(boolean isScroll) {
        getLayoutManager().setScrollLock(isScroll);
    }

    @Override
    public boolean isMeasureAll() {
        return getLayoutManager().isMeasureAll();
    }
}
