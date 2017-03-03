package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.adapters.PagerAdapterForPotUserLikesComments;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.customviews.CustomTabLayout;
import com.cosmicdew.lessonpot.customviews.UserCircularImageView;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by S.K. Pissay on 17/2/17.
 */

public class PotUserLikesCommentsScreen extends PotBaseActivity {

    @Nullable
    @BindView(R.id.toolbar_home)
    Toolbar m_cToolBar;

    @Nullable
    @BindView(R.id.header_view_title)
    TextView m_cHeaderTitle;

    @Nullable
    @BindView(R.id.header_view_sub_title)
    TextView m_cHeaderSubTitle;

    @Nullable
    @BindView(R.id.TAB_LAYOUT)
    CustomTabLayout m_cTabLayout;

    @Nullable
    @BindView(R.id.PAGER)
    ViewPager m_cPager;

    public static final int EDIT_PROFILE = 4320;
    private String m_cNotification;

    private View m_cView;
    private Users m_cUser;
    private BoardChoices m_cBoardChoicesGen;
    private PagerAdapterForPotUserLikesComments m_cPagerAdapter;

    public static final String REFRESH_CONSTANT = "REFRESH_CONSTANT";

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.pot_app_bar_main);
        ButterKnife.bind(this);

        m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);

        if (m_cToolBar != null) {
            setSupportActionBar(m_cToolBar);
            /*getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setTitle(getResources().getString(R.string.library_txt));
            m_cToolBar.setSubtitle(lsub);*/
            String lsub = String.format("%s %s (%s)",
                    m_cUser.getFirstName(),
                    m_cUser.getLastName(),
                    m_cUser.getRole());
            m_cHeaderTitle.setText(getResources().getString(R.string.my_likes_comments_txt));
            m_cHeaderSubTitle.setText(lsub);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    private void init() {
        m_cTabLayout.addTab(m_cTabLayout.newTab().setText(getResources().getString(R.string.lesson_likes_txt)));
        m_cTabLayout.addTab(m_cTabLayout.newTab().setText(getResources().getString(R.string.lesson_comments_txt)));
//        m_cTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        m_cPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(m_cTabLayout));
        m_cPagerAdapter = new PagerAdapterForPotUserLikesComments(getSupportFragmentManager(),
                m_cObjFragmentBase,
                m_cTabLayout.getTabCount(),
                "",
                m_cUser,
                PotMacros.USER_LIKES_COMMENTS);
        m_cPager.setAdapter(m_cPagerAdapter);
//        m_cPager.setOffscreenPageLimit(0);

        m_cTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                m_cPager.setCurrentItem(tab.getPosition());
//                swipeView.setEnabled(false);
                switch (tab.getPosition()) {
                    case 0:
                        // NOTHING TO DO HERE
                        break;
                    case 1:
//                        setTitle("Lead Name", false, false, true, false);
//                        swipeView.setEnabled(true);
//                        displayProgressBar(-1, "Loading Packages,..");
//                        m_cObjTransportMgr.getPackages("", EURemediesSpecialityScreen.this);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Nothing to do here
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Nothing to do here
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_PROFILE:
                if (resultCode == RESULT_OK) {
                    m_cUser = (new Gson()).fromJson(data.getStringExtra(PotMacros.OBJ_USER), Users.class);
                    if (resultCode == RESULT_OK) {
                        m_cUser = (new Gson()).fromJson(data.getStringExtra(PotMacros.OBJ_USER), Users.class);
                        String lsub = String.format("%s %s (%s)",
                                m_cUser.getFirstName(),
                                m_cUser.getLastName(),
                                m_cUser.getRole());
                        m_cHeaderTitle.setText(getResources().getString(R.string.lesson_home_txt));
                        m_cHeaderSubTitle.setText(lsub);
                        UserCircularImageView lUserPic = (UserCircularImageView) m_cView.findViewById(R.id.USER_CIV_CELL);
                        try {
                            Picasso.with(this)
                                    .load(m_cUser.getProfilePic())
                                    .error(R.drawable.profile_placeholder)
                                    .placeholder(R.drawable.profile_placeholder)
                                    .fit()
                                    .into(lUserPic);
                        } catch (Exception e) {
                            lUserPic.setImageResource(R.drawable.profile_placeholder);
                            e.printStackTrace();
                        }
                    }
                }
        }
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        Intent lObjIntent;
        switch (pObjMessage.what) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.SHARE)) {
                } else {
                    super.onAPIResponse(response, apiMethod, refObj);
                }
                hideDialog();
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.SHARE) ||
                        apiMethod.contains(Constants.LESSONS)) {
                    hideDialog();
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
                    } else {
                        String lMsg = new String(error.networkResponse.data);
                        showErrorMsg(lMsg);
                    }
                } else {
                    super.onErrorResponse(error, apiMethod, refObj);
                    hideDialog();
                }
                ;
                break;
        }
    }
}
