package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
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
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.Lessons;
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

import java.util.Arrays;
import java.util.HashMap;

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

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.pot_user_home_screen);
        ButterKnife.bind(this);

        m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);
        m_cNotification = getIntent().getStringExtra(PotMacros.NOTIFICATION);
        m_cFromNetworkScreen = getIntent().getBooleanExtra(PotMacros.FROM_NETWORK_SCREEN, false);

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
                    m_cUser);
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
        if (null != m_cUser) {
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

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.SHARE)) {
                    LessonShares lessonShares = (LessonShares) response;
                    if (null != lessonShares) {
                        hideDialog();
                    }
                } else if (apiMethod.contains(Constants.USERS)) {
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
                            m_cUser));
                    m_cPager.invalidate();
//                    m_cPagerAdapter.notifyDataSetChanged();
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
                if (apiMethod.contains(Constants.USERS) ||
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
