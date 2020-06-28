package com.connect.infixd.mobile.Introduction;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.connect.infixd.mobile.R;
import com.infixd.client.model.ShortestPathResponse;

import java.util.List;

public class HorizontalPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<ShortestPathResponse> shortestPaths;

    public HorizontalPagerAdapter(final Context context, List<ShortestPathResponse> shortestPaths) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.shortestPaths = shortestPaths;
    }

    @Override
    public int getCount() {
        return shortestPaths.size();
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view;
        view = mLayoutInflater.inflate(R.layout.shortest_path_view, container, false);
        setupItem(view, position);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }

    public void setupItem(View view, int position){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.shortestPathRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(new IntroductionPathRVAdapter(mContext,shortestPaths.get(position)));
    }
}
