package com.tonystark.android.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tonystark.android.xpanel.AbsXPanelAdapter;
import com.tonystark.android.xpanel.XPanelDefaultHeaderView;
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
        for (int i = 0; i < 50; i++) {
            mDataList.add("item name : " + i);
        }

        AbsXPanelAdapter absXPanelAdapter = new XPanelAdapter();
        xPanelView.setAdapter(absXPanelAdapter);
        absXPanelAdapter.notifyDataSetChanged();

        XPanelDefaultHeaderView headerView = new XPanelDefaultHeaderView(this);
        headerView.setCanDrag(true);

        xPanelView.setHeaderLayout(headerView);
        xPanelView.setMeasureAll(false);
        xPanelView.setChuttyMode(true);
        xPanelView.setCanFling(true);
        xPanelView.setExposedPercent(0.25f);
        xPanelView.setKickBackPercent(0.65f);
        xPanelView.setDragBaseLine((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 500, getResources().getDisplayMetrics()));
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
