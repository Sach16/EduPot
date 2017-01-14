package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.adapters.PagerAdapterForPotLesson;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.customviews.CustomTabLayout;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.cosmicdew.lessonpot.macros.PotMacros.getNotifyCount;

/**
 * Created by S.K. Pissay on 27/10/16.
 */

public class PotUserLessonScreen extends PotBaseActivity {

    public static final int LESSON_INDEX_RESULT = 9090;

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

    private Users m_cUser;
    private BoardChoices m_cBoardChoice;
    private Syllabi m_cSyllabi;
    private Chapters m_cChapters;
    private Lessons m_cLessonsGen;
    private PagerAdapterForPotLesson m_cPagerAdapter;
    private String mLessonListHeader;
    private String mLessFromWhere;
    private int mIndexPage;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.pot_app_bar_main);
        ButterKnife.bind(this);

        mLessonListHeader = getIntent().getStringExtra(PotMacros.LESSON_HEADER);
        mIndexPage = getIntent().getIntExtra(PotMacros.LESSON_INDEX_PAGE, 0);
        mLessFromWhere = getIntent().getStringExtra(PotMacros.OBJ_LESSONFROM);
        m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);
        m_cBoardChoice = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_BOARDCHOICES), BoardChoices.class);
        m_cSyllabi = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_SYLLABI), Syllabi.class);
        m_cChapters = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_CHAPTERS), Chapters.class);

        if (m_cToolBar != null){
            setSupportActionBar(m_cToolBar);
            /*getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setTitle(getResources().getString(R.string.library_txt));
            m_cToolBar.setSubtitle(lsub);*/
            String lsub = String.format("%s %s (%s)",
                    m_cUser.getFirstName(),
                    m_cUser.getLastName(),
                    m_cUser.getRole());
            m_cHeaderTitle.setText(mLessonListHeader != null ? mLessonListHeader : m_cChapters.getName());
            m_cHeaderSubTitle.setText(lsub);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        init();
    }

    private void init() {
        m_cTabLayout.addTab(m_cTabLayout.newTab().setText("Viewed"));
        m_cTabLayout.addTab(m_cTabLayout.newTab().setText("Received"));
        m_cTabLayout.addTab(m_cTabLayout.newTab().setText("Mine"));
//        m_cTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        m_cPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(m_cTabLayout));
        m_cPagerAdapter = new PagerAdapterForPotLesson(getSupportFragmentManager(),
                m_cObjFragmentBase,
                m_cTabLayout.getTabCount(),
                "",
                m_cUser,
                m_cBoardChoice,
                m_cSyllabi,
                m_cChapters,
                mLessFromWhere);
        m_cPager.setOffscreenPageLimit(2);
        m_cPager.setAdapter(m_cPagerAdapter);
        m_cPager.setCurrentItem(mIndexPage);

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
                    case 2:
                        // NOTHING TO DO HERE
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LESSON_INDEX_RESULT:
                if (resultCode == RESULT_OK) {
                    mIndexPage = data.getIntExtra(PotMacros.LESSON_INDEX_PAGE, 0);
                    m_cPager.setCurrentItem(mIndexPage);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pot_home, menu);

        MenuItem item = menu.findItem(R.id.action_notify);
        MenuItemCompat.setActionView(item, R.layout.menu_action_badge_item);
        View view = MenuItemCompat.getActionView(item);
        TextView notifCount = (TextView) view.findViewById(R.id.menu_badge);
        int lNotifyCount = getNotifyCount(this, m_cUser)[0] + getNotifyCount(this, m_cUser)[1];
        if (lNotifyCount > 0)
            notifCount.setText(String.valueOf(lNotifyCount));
        else
            notifCount.setVisibility(View.GONE);
        ImageView icon = (ImageView) view.findViewById(R.id.menu_badge_icon);
        icon.setImageResource(R.mipmap.notify_bell);
        if (view != null) {
            view.setOnClickListener(this);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent lObjIntent;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add:
                lObjIntent = new Intent(this, LessonsScreen.class);
                if (m_cChapters != null && !m_cChapters.getIsGeneric()) {
                    lObjIntent.putExtra(PotMacros.OBJ_LESSON_TYPE, PotMacros.OBJ_LESSON_NEW);
                    lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_LESSON);
                    lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                    lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoice));
                    lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(m_cSyllabi));
                    lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(m_cChapters));
                    lObjIntent.putExtra(PotMacros.LESSON_INDEX_PAGE, 2);
                } else {
                    lObjIntent.putExtra(PotMacros.OBJ_LESSON_TYPE, PotMacros.OBJ_LESSON_NEW);
                    lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, mLessFromWhere);
                    lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                    lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoice));
                    lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(m_cSyllabi));
                    lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(m_cChapters));
                    lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(getLessonsGen()));
                    lObjIntent.putExtra(PotMacros.LESSON_INDEX_PAGE, 2);
                }
                startActivityForResult(lObjIntent, LESSON_INDEX_RESULT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_notify);
        MenuItemCompat.setActionView(item, R.layout.menu_action_badge_item);
        View view = MenuItemCompat.getActionView(item);
        TextView notifCount = (TextView) view.findViewById(R.id.menu_badge);
        int lNotifyCount = getNotifyCount(this, m_cUser)[0] + getNotifyCount(this, m_cUser)[1];
        if (lNotifyCount > 0)
            notifCount.setText(String.valueOf(lNotifyCount));
        else
            notifCount.setVisibility(View.GONE);
        ImageView icon = (ImageView) view.findViewById(R.id.menu_badge_icon);
        icon.setImageResource(R.mipmap.notify_bell);
        if (view != null) {
            view.setOnClickListener(this);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        Intent lObjIntent;
        switch (pObjMessage.what) {
            case R.id.action_notify:
                if (pObjMessage.arg1 == 0){
                    lObjIntent = new Intent(PotUserLessonScreen.this, PotUserHomeScreen.class);
                    lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson((Users) pObjMessage.obj));
                    lObjIntent.putExtra(PotMacros.FROM_NETWORK_SCREEN, true);
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    lObjIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(lObjIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    PotMacros.clearNotifyCount(this, Constants.LESSON_SHARE, (Users) pObjMessage.obj);
                    finish();
                }else {
                    lObjIntent = new Intent(PotUserLessonScreen.this, PotUserNetworkScreen.class);
                    lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson((Users) pObjMessage.obj));
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    lObjIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(lObjIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    PotMacros.clearNotifyCount(this, Constants.CONNECTION_APPROVED, (Users) pObjMessage.obj);
                    finish();
                }
                ShortcutBadger.applyCount(this, PotMacros.getNotifyAll(this)[0]);
                break;
            case PotMacros.REFRESH_NOTIFY_CONSTANT_KEY:
                invalidateOptionsMenu();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent lObjIntent;
        switch (v.getId()) {
            case R.id.action_notify:
                if ((getNotifyCount(this, m_cUser)[0] + getNotifyCount(this, m_cUser)[1]) > 0) {
                    displaySpinnerNotifyDialog(R.id.action_notify,
                            getResources().getString(R.string.notifications_txt),
                            Arrays.asList(getResources().getString(R.string.newly_received_lessons_txt),
                                    getResources().getString(R.string.newly_connection_requests_txt)),
                            new int[]{PotMacros.getNotifyCount(this, m_cUser)[1],
                                    PotMacros.getNotifyCount(this, m_cUser)[0]},
                            m_cUser);
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    public Lessons getLessonsGen() {
        return m_cLessonsGen;
    }

    public void setLessonsGen(Lessons pLessonsGen) {
        this.m_cLessonsGen = pLessonsGen;
    }
}