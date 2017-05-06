package com.xueba.quting.high;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by quting on 2015/7/1.
 */
public class myPagerAdapter extends PagerAdapter
{
    private List<View> mListViews;
    public myPagerAdapter( List<View>mListViews)
    {
        this.mListViews=mListViews;
    }
    @Override
    public  void destroyItem(ViewGroup container,int position,Object obkect)
    {
        container.removeView(mListViews.get(position));
    }
    @Override
    public Object instantiateItem(ViewGroup container,int position)
    {
        container.addView(mListViews.get(position),0);
        return mListViews.get(position);
    }
    @Override
    public int getCount()
    {
        return mListViews.size();
    }
    @Override
    public boolean isViewFromObject(View arg0,Object arg1)
    {
        return arg0==arg1;
    }

}
