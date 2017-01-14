package com.cosmicdew.lessonpot.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.fragments.PotUserHomeChapterFragment;
import com.cosmicdew.lessonpot.fragments.PotUserHomeChapterMineFragment;
import com.cosmicdew.lessonpot.fragments.PotUserHomeChapterReceivedFragment;
import com.cosmicdew.lessonpot.fragments.PotUserHomeChapterViewedFragment;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;

/**
 * Created by S.K. Pissay on 27/10/16.
 */

public class PagerAdapterForPotChapter extends FragmentStatePagerAdapter {

    int m_cNumOfTabs;
    String m_cId;
    private Users m_cUser;
    private BoardChoices m_cBoardChoices;
    private Syllabi m_cSyllabi;

    //write inner fragment items below

    public PotFragmentBaseClass m_cObjFragmentBase;

    public PagerAdapterForPotChapter(FragmentManager pFragment, PotFragmentBaseClass pObjFragmentBase,
                                     int pNumOfTabs, String pId, Users pUser, BoardChoices pBoardChoices, Syllabi pSyllabi) {
        super(pFragment);
        this.m_cNumOfTabs = pNumOfTabs;
        this.m_cObjFragmentBase = pObjFragmentBase;
        this.m_cId = pId;
        this.m_cUser = pUser;
        this.m_cBoardChoices = pBoardChoices;
        this.m_cSyllabi = pSyllabi;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                m_cObjFragmentBase = PotUserHomeChapterFragment.newInstance(position, m_cId, m_cUser, m_cBoardChoices, m_cSyllabi, null, null);
                return m_cObjFragmentBase;
            case 1:
                m_cObjFragmentBase = PotUserHomeChapterViewedFragment.newInstance(position, m_cId, m_cUser, m_cBoardChoices, m_cSyllabi);
                return m_cObjFragmentBase;
            case 2:
                m_cObjFragmentBase = PotUserHomeChapterReceivedFragment.newInstance(position, m_cId, m_cUser, m_cBoardChoices, m_cSyllabi);
                return m_cObjFragmentBase;
            case 3:
                m_cObjFragmentBase = PotUserHomeChapterMineFragment.newInstance(position, m_cId, m_cUser, m_cBoardChoices, m_cSyllabi);
                return m_cObjFragmentBase;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return m_cNumOfTabs;
    }
}
