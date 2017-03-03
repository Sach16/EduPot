package com.cosmicdew.lessonpot.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.fragments.PotUserHomeClassesFragment;
import com.cosmicdew.lessonpot.fragments.PotUserHomeClassesMineFragment;
import com.cosmicdew.lessonpot.fragments.PotUserHomeClassesOfflineFragment;
import com.cosmicdew.lessonpot.fragments.PotUserHomeClassesReceivedFragment;
import com.cosmicdew.lessonpot.fragments.PotUserHomeClassesViewedFragment;
import com.cosmicdew.lessonpot.models.Users;


/**
 * Created by S.K. Pissay on 17/10/16.
 */

public class PagerAdapterForPotHome extends FragmentStatePagerAdapter{

    int m_cNumOfTabs;
    String m_cId;
    private Users m_cUser;
    private String m_cGoOffline;

    //write inner fragment items below

    public PotFragmentBaseClass m_cObjFragmentBase;

    public PagerAdapterForPotHome(FragmentManager pFragment, PotFragmentBaseClass pObjFragmentBase,
                                  int pNumOfTabs, String pId, Users pUser, String pGoOffline) {
        super(pFragment);
        this.m_cNumOfTabs = pNumOfTabs;
        this.m_cObjFragmentBase = pObjFragmentBase;
        this.m_cId = pId;
        this.m_cUser = pUser;
        this.m_cGoOffline = pGoOffline;

    }

    @Override
    public Fragment getItem(int position) {
        if (null == m_cGoOffline)
            switch (position) {
                case 0:
                    m_cObjFragmentBase = PotUserHomeClassesFragment.newInstance(position, m_cId, m_cUser, null, null, m_cGoOffline);
                    return m_cObjFragmentBase;
                case 1:
                    m_cObjFragmentBase = PotUserHomeClassesViewedFragment.newInstance(position, m_cId, m_cUser, m_cGoOffline);
                    return m_cObjFragmentBase;
                case 2:
                    m_cObjFragmentBase = PotUserHomeClassesReceivedFragment.newInstance(position, m_cId, m_cUser, m_cGoOffline);
                    return m_cObjFragmentBase;
                case 3:
                    m_cObjFragmentBase = PotUserHomeClassesMineFragment.newInstance(position, m_cId, m_cUser, m_cGoOffline);
                    return m_cObjFragmentBase;
                default:
                    return null;
            }
        else
            switch (position) {
                case 0:
                    m_cObjFragmentBase = PotUserHomeClassesFragment.newInstance(position, m_cId, m_cUser, null, null, m_cGoOffline);
                    return m_cObjFragmentBase;
                case 1:
                    m_cObjFragmentBase = PotUserHomeClassesOfflineFragment.newInstance(position, m_cId, m_cUser, m_cGoOffline);
                    return m_cObjFragmentBase;
                case 2:
                    m_cObjFragmentBase = PotUserHomeClassesReceivedFragment.newInstance(position, m_cId, m_cUser, m_cGoOffline);
                    return m_cObjFragmentBase;
                case 3:
                    m_cObjFragmentBase = PotUserHomeClassesMineFragment.newInstance(position, m_cId, m_cUser, m_cGoOffline);
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
