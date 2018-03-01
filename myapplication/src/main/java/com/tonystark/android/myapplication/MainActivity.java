package com.tonystark.android.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tonystark.android.xpanel.AbsXPanelAdapter;
import com.tonystark.android.xpanel.XPanelDefaultHeaderView;
import com.tonystark.android.xpanel.XPanelDragMotionDetection;
import com.tonystark.android.xpanel.XPanelView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> mDataList = new ArrayList<>();

    private XPanelView xPanelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        xPanelView = findViewById(R.id.xpanelview);
        for (int i = 0; i < 15; i++) {
            mDataList.add("item name : " + i);
        }

        AbsXPanelAdapter absXPanelAdapter = new XPanelAdapter();
        xPanelView.setAdapter(absXPanelAdapter);
        absXPanelAdapter.notifyDataSetChanged();

//        XPanelDefaultHeaderView headerView = new XPanelDefaultHeaderView(this);
//        headerView.setCanDrag(true);

//        xPanelView.setHeaderLayout(headerView);
        ViewGroup vp = new FrameLayout(this);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        vp.setLayoutParams(params);
        vp.setBackgroundColor(Color.BLUE);
        xPanelView.setHeaderLayout(vp);
        xPanelView.setMeasureAll(false);
        xPanelView.setChuttyMode(false);
        xPanelView.setCanFling(true);
        xPanelView.setExposedPercent(0.25f);
        xPanelView.setKickBackPercent(0.65f);
        xPanelView.setOnXPanelMotionListener(new XPanelDragMotionDetection.OnXPanelMotionListener() {
            @Override
            public void OnDrag(int dragMotion, int offset) {
                String motion = "";
                switch (dragMotion) {
                    case XPanelDragMotionDetection.DragMotion.DRAG_DOWN: {
                        motion = "DRAG_DOWN";
                        break;
                    }
                    case XPanelDragMotionDetection.DragMotion.DRAG_UP: {
                        motion = "DRAG_UP";
                        break;
                    }
                    case XPanelDragMotionDetection.DragMotion.DRAG_FLING: {
                        motion = "DRAG_FLING";
                        break;
                    }
                    case XPanelDragMotionDetection.DragMotion.DRAG_STOP: {
                        motion = "DRAG_STOP";
                        break;
                    }
                }
                Log.i("OnXPanelMotionListener", "OnDrag dragMotion:" + motion + " offset:" + offset);
            }

            @Override
            public void OnCeiling(boolean isCeiling) {
                Log.i("OnXPanelMotionListener", "OnCeiling isCeiling:" + isCeiling);
            }

        });
    }

    private class XPanelAdapter extends AbsXPanelAdapter<XPanelAdapter.MyViewHolder> {
        private Toast mToast;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.mItem.setText(mDataList.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    if (position == 0) {
                        xPanelView.setHeaderLayout(null);
                    } else {
                        XPanelDefaultHeaderView headerView = new XPanelDefaultHeaderView(MainActivity.this);
                        headerView.setCanDrag(true);

                        xPanelView.setHeaderLayout(headerView);
                    }
//                    if (position == 0 || position == 24) {
//                        Intent intent = new Intent(MainActivity.this, InputActivity.class);
//                        startActivity(intent);
//                    }
                    mToast = Toast.makeText(holder.mItem.getContext(), "点击了:" + position + "个", Toast.LENGTH_SHORT);
                    mToast.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView mItem;

            public MyViewHolder(View itemView) {
                super(itemView);
                mItem = itemView.findViewById(R.id.item);
            }
        }
    }
}
