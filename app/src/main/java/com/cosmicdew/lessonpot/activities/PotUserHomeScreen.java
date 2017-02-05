package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.adapters.PagerAdapterForPotHome;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.customviews.CustomTabLayout;
import com.cosmicdew.lessonpot.customviews.UserCircularImageView;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Attachments;
import com.cosmicdew.lessonpot.models.AttachmentsAll;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.LessonsTable;
import com.cosmicdew.lessonpot.models.Sessions;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.models.UsersAll;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.cosmicdew.lessonpot.macros.PotMacros.getNotifyCount;

/**
 * Created by S.K. Pissay on 17/10/16.
 */

public class PotUserHomeScreen extends PotBaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Nullable
    @BindView(R.id.drawer_layout)
    DrawerLayout m_cDrawerLayout;

    @Nullable
    @BindView(R.id.nav_view)
    NavigationView m_cNavigationView;

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

    private View m_cView;
    private Users m_cUser;
    private BoardChoices m_cBoardChoicesGen;
    private PagerAdapterForPotHome m_cPagerAdapter;

    private String m_cNotification;
    private boolean m_cFromNetworkScreen;

    private String m_cGoOffline;
    private LessonsTable m_cLessonsTable;
    private HashMap<String, Attachments> m_cAttachList;
    private int count;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.pot_user_home_screen);
        ButterKnife.bind(this);

        m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);
        m_cNotification = getIntent().getStringExtra(PotMacros.NOTIFICATION);
        m_cFromNetworkScreen = getIntent().getBooleanExtra(PotMacros.FROM_NETWORK_SCREEN, false);
        m_cGoOffline = getIntent().getStringExtra(PotMacros.GO_OFFLINE);

        m_cView = m_cNavigationView.inflateHeaderView(R.layout.pot_nav_header_main);
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
        TextView lFullName = (TextView) m_cView.findViewById(R.id.USER_FULL_NAME_TXT);
        lFullName.setText(m_cUser.getFirstName() + " " + m_cUser.getLastName());
        TextView lRole = (TextView) m_cView.findViewById(R.id.USER_ROLL_TXT);
        lRole.setText(m_cUser.getRoleTitle());

        TextView lLessons = (TextView) m_cView.findViewById(R.id.NAV_LESSON_HOME);
        lLessons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != m_cDrawerLayout) {
                    m_cDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
        TextView lNetwork = (TextView) m_cView.findViewById(R.id.NAV_NETWORK);
        lNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lObjIntent;
                if (null != m_cDrawerLayout) {
                    m_cDrawerLayout.closeDrawer(GravityCompat.START);
                    lObjIntent = new Intent(PotUserHomeScreen.this, PotUserNetworkScreen.class);
                    lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    lObjIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(lObjIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        });
        TextView lGoOffline = (TextView) m_cView.findViewById(R.id.NAV_GO_OFFLINE);
        lGoOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lObjIntent;
                if (null != m_cDrawerLayout) {
                    m_cDrawerLayout.closeDrawer(GravityCompat.START);
                    lObjIntent = new Intent(PotUserHomeScreen.this, PotLandingScreen.class);
                    lObjIntent.putExtra(PotMacros.GO_OFFLINE, PotMacros.GO_OFFLINE);
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    lObjIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(lObjIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        });
        TextView lSwitchUser = (TextView) m_cView.findViewById(R.id.NAV_SIGN_OUT);
        lSwitchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lObjIntent;
                if (null != m_cDrawerLayout) {
                    m_cDrawerLayout.closeDrawer(GravityCompat.START);
                    lObjIntent = new Intent(PotUserHomeScreen.this, PotLandingScreen.class);
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    lObjIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(lObjIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        });
        TextView lEditProfile = (TextView) m_cView.findViewById(R.id.NAV_EDIT_PROFILE);
        lEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lObjIntent;
                if (null != m_cDrawerLayout) {
                    m_cDrawerLayout.closeDrawer(GravityCompat.START);
                    lObjIntent = new Intent(PotUserHomeScreen.this, EditProfileScreen.class);
                    lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                    startActivityForResult(lObjIntent, EDIT_PROFILE);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
        TextView lSuppTxt = (TextView) m_cView.findViewById(R.id.NAV_SUPPORT);
        lSuppTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != m_cDrawerLayout) {
                    m_cDrawerLayout.closeDrawer(GravityCompat.START);
                    displaySupportDialog(PotMacros.SUPPORT_OPTION, getResources().getString(R.string.support_txt), null, null, false);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
        TextView lFeedback = (TextView) m_cView.findViewById(R.id.NAV_SEND_FEEDBACK);
        lFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != m_cDrawerLayout) {
                    m_cDrawerLayout.closeDrawer(GravityCompat.START);
                    displaySupportDialog(PotMacros.SUPPORT_OPTION, getResources().getString(R.string.send_feedback_txt), null, null, false);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
        TextView lHelp = (TextView) m_cView.findViewById(R.id.NAV_HELP);
        lHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != m_cDrawerLayout) {
                    m_cDrawerLayout.closeDrawer(GravityCompat.START);
                    Intent lObjIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PotMacros.OVERVIEW));
                    startActivity(lObjIntent);
                }
            }
        });

        if (m_cToolBar != null) {
            setSupportActionBar(m_cToolBar);
            /*getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setTitle(getResources().getString(R.string.library_txt));
            m_cToolBar.setSubtitle(lsub);*/
            String lsub = String.format("%s %s (%s)",
                    m_cUser.getFirstName(),
                    m_cUser.getLastName(),
                    m_cUser.getRole());
            m_cHeaderTitle.setText(getResources().getString(R.string.lesson_home_txt));
            m_cHeaderSubTitle.setText(lsub);

        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, m_cDrawerLayout, m_cToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        m_cDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        m_cNavigationView.setNavigationItemSelectedListener(this);

        init();
    }

    private void init() {
        m_cTabLayout.addTab(m_cTabLayout.newTab().setText("Classes"));
        if (null == m_cGoOffline)
            m_cTabLayout.addTab(m_cTabLayout.newTab().setText("Viewed"));
        m_cTabLayout.addTab(m_cTabLayout.newTab().setText("Received"));
        m_cTabLayout.addTab(m_cTabLayout.newTab().setText("Mine"));
//        m_cTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        m_cPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(m_cTabLayout));

        if (null != m_cNotification) {
            m_cNotification.equalsIgnoreCase(Constants.LESSON_SHARE);
            updateSessionToken();
        } else {
            m_cPagerAdapter = new PagerAdapterForPotHome(getSupportFragmentManager(),
                    m_cObjFragmentBase,
                    m_cTabLayout.getTabCount(),
                    "",
                    m_cUser,
                    m_cGoOffline);
//        m_cPager.setOffscreenPageLimit(2);
            m_cPager.setAdapter(m_cPagerAdapter);
        }
        if (m_cFromNetworkScreen)
            m_cPager.setCurrentItem(2);

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
                        PotMacros.clearNotifyCount(PotUserHomeScreen.this, Constants.LESSON_SHARE, m_cUser);
                        invalidateOptionsMenu();
                        ShortcutBadger.applyCount(PotUserHomeScreen.this, PotMacros.getNotifyAll(PotUserHomeScreen.this)[0]);
                        // NOTHING TO DO HERE
                        break;
                    case 3:
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

    private void updateSessionToken() {
        if (null == m_cGoOffline && null != m_cUser) {
            int sessionId = PotMacros.getSessionId(this) > -1 ? PotMacros.getSessionId(this) : PotMacros.getGreenSessionId(this);
            displayProgressBar(-1, "Loading...");
            RequestManager.getInstance(this).placeRequest(Constants.SESSIONS +
                    sessionId +
                    "/" +
                    Constants.USERS +
                    m_cUser.getId() +
                    "/", Sessions.class, this, null, null, null, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (m_cDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            m_cDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            case R.id.action_add:
                lObjIntent = new Intent(this, LessonsScreen.class);
                lObjIntent.putExtra(PotMacros.OBJ_LESSON_TYPE, PotMacros.OBJ_LESSON_NEW);
                lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_BOARDCHOICES);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(getBoardChoicesGen()));
                startActivity(lObjIntent);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_library:
                // Handle the camera action
                break;
            case R.id.nav_message:
                break;
            case R.id.nav_view:
                break;
            case R.id.nav_network:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_logout:
                break;
            default:
                break;
        }

        m_cDrawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_PROFILE:
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

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        Intent lObjIntent;
        switch (pObjMessage.what) {
            case R.id.action_notify:
                if (pObjMessage.arg1 == 0) {
                    m_cPager.setCurrentItem(2);
                    PotMacros.clearNotifyCount(this, Constants.LESSON_SHARE, (Users) pObjMessage.obj);
                    invalidateOptionsMenu();
                } else {
                    lObjIntent = new Intent(PotUserHomeScreen.this, PotUserNetworkScreen.class);
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
            case PotMacros.SUPPORT_OPTION:
                String[] lTitCom = ((String[]) pObjMessage.obj);
                if (null != lTitCom) {
                    displayProgressBar(-1, "");
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.NAME, lTitCom[0]);
                        lJO.put(Constants.COMMENTS, lTitCom[1]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    displayProgressBar(-1, "Loading...");
                    RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS,
                            Lessons.class, this, null, null, lJO.toString(), true);
                }
                break;
        }
    }

    @Optional
    @OnClick({R.id.NAV_NETWORK})
    public void onClick(View v) {
        Intent lObjIntent;
        switch (v.getId()) {
            case R.id.NAV_NETWORK:
                if (null != m_cDrawerLayout) {
                    m_cDrawerLayout.closeDrawer(GravityCompat.START);
                    lObjIntent = new Intent(this, PotUserNetworkScreen.class);
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    lObjIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(lObjIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
                break;
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
        }
    }

    public void checkAndDownloadAttachments(Lessons pLessons, int pSourceId) {
        m_cAttachList = new HashMap<>();
        displayProgressBar(-1, "Loading...");
        RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                        pLessons.getId() +
                        "/" +
                        Constants.ATTACHMENTS, AttachmentsAll.class, this,
                new Object[]{pLessons, pSourceId},
                null, null, false);
    }

    public boolean checkAndUpdateLessons(Lessons pLessons, int pSourceId) {
        boolean lRetVal = false;
        List<LessonsTable> lessonsTableListAll = LessonsTable.listAll(LessonsTable.class);
        List<LessonsTable> lessonsTableList = LessonsTable.find(LessonsTable.class, "lesson_id = ? and user_id = ?", String.valueOf(pLessons.getId()), String.valueOf(m_cUser.getId()));
        if (lessonsTableList.size() == 0) {
            m_cLessonsTable = new LessonsTable(pLessons.getId(), pLessons.getName(), pLessons.getComments(),
                    pLessons.getCreated(), pLessons.getModified(), m_cUser.getId(), pLessons.getOwner().getId(), pSourceId != m_cUser.getId() ? pSourceId : -1,
                    pLessons.getChapter().getName(),
                    pLessons.getChapter().getSyllabus().getSubjectName(),
                    pLessons.getChapter().getSyllabus().getBoardclass().getName() + " " +
                            pLessons.getChapter().getSyllabus().getBoardclass().getBoard().getName(),
                    null,
                    null,
                    null,
                    null,
                    pLessons.getLength().getLengthSum(),
                    pLessons.getPosition(),
                    pLessons.getViews());
            m_cLessonsTable.save();
            lRetVal = true;
        } else {
            List<LessonsTable> lessonsTableAll = LessonsTable.find(LessonsTable.class, "lesson_id = ?", String.valueOf(pLessons.getId()));
            m_cLessonsTable = LessonsTable.find(LessonsTable.class, "lesson_id = ?", String.valueOf(pLessons.getId())).get(0);
            m_cLessonsTable.setLessonId(pLessons.getId());
            m_cLessonsTable.setName(pLessons.getName());
            m_cLessonsTable.setComments(pLessons.getComments());
            m_cLessonsTable.setCreated(pLessons.getCreated());
            m_cLessonsTable.setModified(pLessons.getModified());
            m_cLessonsTable.setUserId(m_cUser.getId());
            m_cLessonsTable.setOwnerId(pLessons.getOwner().getId());
            m_cLessonsTable.setSharerId(pSourceId != m_cUser.getId() ? pSourceId : -1);
            m_cLessonsTable.setChapterName(pLessons.getChapter().getName());
            m_cLessonsTable.setSyllabiName(pLessons.getChapter().getSyllabus().getSubjectName());
            m_cLessonsTable.setBoardClass(pLessons.getChapter().getSyllabus().getBoardclass().getName() + " " +
                    pLessons.getChapter().getSyllabus().getBoardclass().getBoard().getName());
            m_cLessonsTable.setLengthSum(pLessons.getLength().getLengthSum());
            m_cLessonsTable.setPosition(pLessons.getPosition());
            m_cLessonsTable.setViews(pLessons.getViews());
            m_cLessonsTable.save();
            lRetVal = false;
        }
        return lRetVal;
    }

    private Attachments addAttachment(Integer pId, String pLessonType, String pAttach) {
        Attachments lAttachments = new Attachments();
        lAttachments.setId(pId);
        lAttachments.setAttachmentType(pLessonType);
        lAttachments.setAttachment(pAttach);
        return lAttachments;
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.ATTACHMENTS)) {
                    AttachmentsAll lAttachmentsAll = (AttachmentsAll) response;
                    for (Attachments lAttachments : lAttachmentsAll.getAttachments()) {
                        if (lAttachments.getAttachmentType().equalsIgnoreCase(Constants.IMAGE)) {
                            switch (lAttachments.getSlot()) {
                                case 1:
                                    m_cAttachList.put(PotMacros.LESSON_IMG_1, addAttachment(lAttachments.getId(),
                                            PotMacros.LESSON_IMG_1,
                                            lAttachments.getAttachment()));
                                    break;
                                case 2:
                                    m_cAttachList.put(PotMacros.LESSON_IMG_2, addAttachment(lAttachments.getId(),
                                            PotMacros.LESSON_IMG_2,
                                            lAttachments.getAttachment()));
                                    break;
                                case 3:
                                    m_cAttachList.put(PotMacros.LESSON_IMG_3, addAttachment(lAttachments.getId(),
                                            PotMacros.LESSON_IMG_3,
                                            lAttachments.getAttachment()));
                                    break;
                            }
                        } else if (lAttachments.getAttachmentType().equalsIgnoreCase(Constants.AUDIO)) {
                            m_cAttachList.put(PotMacros.LESSON_AUDIO, addAttachment(lAttachments.getId(),
                                    PotMacros.LESSON_AUDIO,
                                    lAttachments.getAttachment()));
                        }
                    }
                    Object[] lObjects = (Object[]) refObj;
                    if (checkAndUpdateLessons((Lessons) lObjects[0], (int) lObjects[1])) {
                        LinkedHashMap<String, Attachments> linkedHashMap = new LinkedHashMap<>(m_cAttachList);
                        if (null != m_cAttachList && m_cAttachList.size() > 0) {
                            for (int i = 0; i < m_cAttachList.size(); i++) {
                                RequestManager.getInstance(this).placeStreamRequest((new ArrayList<Attachments>(linkedHashMap.values())).get(i).getAttachment(),
                                        Constants.OFFLINE_META,
                                        Attachments.class, this,
                                        (new ArrayList<Attachments>(linkedHashMap.values())).get(i).getAttachmentType().equals(PotMacros.LESSON_AUDIO) ?
                                                new Object[]{(new ArrayList<Attachments>(linkedHashMap.values())).get(i).getAttachmentType(), PotMacros.getOfflineAudioFilePath(this) + "/" + PotMacros.getGUID() + ".aac",
                                                        i == (m_cAttachList.size() - 1) ? (m_cAttachList.size() - 1) : -1} :
                                                new Object[]{(new ArrayList<Attachments>(linkedHashMap.values())).get(i).getAttachmentType(), PotMacros.getOfflineImageFilePath(this) + "/" + PotMacros.getGUID() + ".jpg",
                                                        i == (m_cAttachList.size() - 1) ? (m_cAttachList.size() - 1) : -1},
                                        null, null, false);
                            }
                        } else
                            hideDialog();
                    } else
                        hideDialog();
                } else if (apiMethod.contains(Constants.OFFLINE_META)) {
                    Object[] lObjects = (Object[]) refObj;
                    try {
                        byte[] lByte = (byte[]) response;
                        long lenghtOfFile = lByte.length;

                        //coverting reponse to input stream
                        InputStream input = new ByteArrayInputStream(lByte);
                        File file = new File((String) lObjects[1]);
                        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
                        byte data[] = new byte[1024];

                        long total = 0;

                        while ((count = input.read(data)) != -1) {
                            total += count;
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    switch ((String) lObjects[0]) {
                        case PotMacros.LESSON_AUDIO:
                            m_cLessonsTable.setAudio((String) lObjects[1]);
                            break;
                        case PotMacros.LESSON_IMG_1:
                            m_cLessonsTable.setImg1((String) lObjects[1]);
                            break;
                        case PotMacros.LESSON_IMG_2:
                            m_cLessonsTable.setImg2((String) lObjects[1]);
                            break;
                        case PotMacros.LESSON_IMG_3:
                            m_cLessonsTable.setImg3((String) lObjects[1]);
                            break;
                    }
                    m_cLessonsTable.save();
                    if ((int) lObjects[2] > -1) {
                        displayToast(getResources().getString(R.string.lesson_saved_successfully_txt));
                        hideDialog();
                    }
                } else if (apiMethod.contains(Constants.SHARE)) {
                    LessonShares lessonShares = (LessonShares) response;
                    if (null != lessonShares) {
                        hideDialog();
                    }
                } else if (apiMethod.contains(Constants.USERS) && !apiMethod.contains(Constants.SESSIONS)) {
                    UsersAll lUsers = (UsersAll) response;
                    Lessons lessons = (Lessons) refObj;
                    if (lessons != null) {
                        JSONObject lJO = new JSONObject();
                        try {
                            JSONArray lArray = new JSONArray();
                            lArray.put(lUsers.getUsers().get(0).getId());
                            lJO.put(Constants.USERS_TXT, lArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        displayProgressBar(-1, "");
                        RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                                        lessons.getId() +
                                        "/" +
                                        Constants.SHARE,
                                LessonShares.class, this, null, null, lJO.toString(), true);
                    }
                } else if (apiMethod.contains(Constants.LESSONS)) {
                    Lessons lessons = (Lessons) response;
                    HashMap<String, String> lParams = new HashMap<>();
                    lParams.put(Constants.USERNAME, PotMacros.SUPPORTWIZARD_USERNAME);
                    displayProgressBar(-1, "");
                    RequestManager.getInstance(this).placeRequest(Constants.USERS,
                            UsersAll.class, this, lessons, lParams, null, false);
                } else if (apiMethod.contains(Constants.SESSIONS)) {
                    Sessions lSessions = (Sessions) response;
                    PotMacros.saveUserToken(this, lSessions.getUserToken());
                    m_cPager.setAdapter(new PagerAdapterForPotHome(getSupportFragmentManager(),
                            m_cObjFragmentBase,
                            m_cTabLayout.getTabCount(),
                            "",
                            m_cUser,
                            m_cGoOffline));
                    m_cPager.invalidate();
//                    m_cPagerAdapter.notifyDataSetChanged();
                    hideDialog();
                } else {
                    super.onAPIResponse(response, apiMethod, refObj);
                    hideDialog();
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.ATTACHMENTS)||
                        apiMethod.contains(Constants.OFFLINE_META)||
                        apiMethod.contains(Constants.USERS) ||
                        apiMethod.contains(Constants.SHARE) ||
                        apiMethod.contains(Constants.LESSONS) ||
                        apiMethod.contains(Constants.SESSIONS)) {
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

    public BoardChoices getBoardChoicesGen() {
        return m_cBoardChoicesGen;
    }

    public void setBoardChoicesGen(BoardChoices pBoardChoicesGen) {
        this.m_cBoardChoicesGen = pBoardChoicesGen;
    }
}
