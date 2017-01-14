package com.cosmicdew.lessonpot.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cosmicdew.lessonpot.fragments.ScreenSlidePageFragment;

import java.util.ArrayList;

/**
 * Created by S.K. Pissay on 14/11/16.
 */

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> mList;
    private int mPos;
    private Context mCtx;

    public ScreenSlidePagerAdapter(FragmentManager fm, Context pCtx, int pPos, ArrayList<String> pList) {
        super(fm);
        this.mList = pList;
        this.mPos = pPos;
        this.mCtx = pCtx;
    }

    @Override
    public Fragment getItem(int position) {
        return ScreenSlidePageFragment.newInstance(mPos, mList.get(position));
    }

    @Override
    public int getCount() {
        return mList.size();
    }

}
