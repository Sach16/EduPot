package com.cosmicdew.lessonpot.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.fragments.PotLessonsCommentsFragment;
import com.cosmicdew.lessonpot.fragments.PotLessonsLikesFragment;
import com.cosmicdew.lessonpot.models.Users;

/**
 * Created by S.K. Pissay on 17/2/17.
 */

public class PagerAdapterForPotUserLikesComments extends FragmentStatePagerAdapter {

    int m_cNumOfTabs;
    String m_cId;
    private Users m_cUser;
    private String m_cMode;

    //write inner fragment items below

    public PotFragmentBaseClass m_cObjFragmentBase;

    public PagerAdapterForPotUserLikesComments(FragmentManager pFragment, PotFragmentBaseClass pObjFragmentBase,
                                               int pNumOfTabs, String pId, Users pUser, String pMode) {
        super(pFragment);
        this.m_cNumOfTabs = pNumOfTabs;
        this.m_cObjFragmentBase = pObjFragmentBase;
        this.m_cId = pId;
        this.m_cUser = pUser;
        this.m_cMode = pMode;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                m_cObjFragmentBase = PotLessonsLikesFragment.newInstance(position, null, m_cUser, null, null, m_cMode, null);
                return m_cObjFragmentBase;
            case 1:
                m_cObjFragmentBase = PotLessonsCommentsFragment.newInstance(position, null, m_cUser, null, null, m_cMode, null);
                return m_cObjFragmentBase;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return m_cNumOfTabs;
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }
}
