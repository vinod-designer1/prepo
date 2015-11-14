package com.prepo.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.prepo.R;
import com.prepo.SplashScreenActivity;

/**
 * Created by vinody on 11/1/15.
 */
public class SplashPagerAdapter extends PagerAdapter {

    private Context _context;
    private int[] _screenIds;

    public SplashPagerAdapter(Context context, int[] screenIds) {
        _screenIds = screenIds;
        _context = context;
    }

    @Override
    public int getCount() {
        return _screenIds.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        int screenid = _screenIds[position];

        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = null;

        if (screenid == 0) {
            view = inflater.inflate(R.layout.splash_page_screen_1, container, false);
        } else if (screenid == 1) {
            view = inflater.inflate(R.layout.splash_page_screen_2, container, false);
        } else if (screenid == 2) {
            view = inflater.inflate(R.layout.splash_page_screen_1, container, false);
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}