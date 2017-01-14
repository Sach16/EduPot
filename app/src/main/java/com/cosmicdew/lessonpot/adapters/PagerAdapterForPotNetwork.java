package com.cosmicdew.lessonpot.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.fragments.PotUserNetworkConnectionsFragment;
import com.cosmicdew.lessonpot.fragments.PotUserNetworkRequestsFragment;
import com.cosmicdew.lessonpot.models.Users;

/**
 * Created by S.K. Pissay on 22/11/16.
 */

public class PagerAdapterForPotNetwork extends FragmentStatePagerAdapter {

    int m_cNumOfTabs;
    String m_cId;
    private Users m_cUser;

    //write inner fragment items below

    public PotFragmentBaseClass m_cObjFragmentBase;

    public PagerAdapterForPotNetwork(FragmentManager pFragment, PotFragmentBaseClass pObjFragmentBase,
                                  int pNumOfTabs, String pId, Users pUser) {
        super(pFragment);
        this.m_cNumOfTabs = pNumOfTabs;
        this.m_cObjFragmentBase = pObjFragmentBase;
        this.m_cId = pId;
        this.m_cUser = pUser;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                m_cObjFragmentBase = PotUserNetworkRequestsFragment.newInstance(position, m_cId, m_cUser, null, null);
                return m_cObjFragmentBase;
            case 1:
                m_cObjFragmentBase = PotUserNetworkConnectionsFragment.newInstance(position, m_cId, m_cUser, null, null);
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
