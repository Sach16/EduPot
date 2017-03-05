package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForUsers;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.interfaces.RecyclerUsersListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Follows;
import com.cosmicdew.lessonpot.models.Sessions;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.models.UsersAll;
import com.cosmicdew.lessonpot.models.UsersTable;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.cosmicdew.lessonpot.network.RequestManager.LIVE_BASEFEED_URL;
import static com.cosmicdew.lessonpot.network.RequestManager.TEST_BASEFEED_URL;

/**
 * Created by S.K. Pissay on 17/10/16.
 */

public class PotLandingScreen extends PotBaseActivity implements RecyclerUsersListener {

    @Nullable
    @BindView(R.id.CV_USER_CELL)
    CardView mcvBanqCell;

    @Nullable
    @BindView(R.id.RV_LIST_STU)
    RecyclerView m_cRecycUsers;

    @Nullable
    @BindView(R.id.NO_DATA_RL)
    RelativeLayout rlNoData;

    @Nullable
    @BindView(R.id.REGISTER_NEW_TXT)
    TextView regNewTxt;

    @Nullable
    @BindView(R.id.ADD_EXISTING_TXT)
    TextView addExTxt;

    public static final int RE_FRESH = 7771;

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForUsers m_cRecycAdUsers;
    ArrayList<Users> m_cUsersList;

    private Users m_cUser;

    private String m_cGoOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pot_landing_screen);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_center);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        m_cGoOffline = getIntent().getStringExtra(PotMacros.GO_OFFLINE);
        m_cUsersList = new ArrayList<>();
        m_cLayoutManager = new LinearLayoutManager(this);
        m_cLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        m_cRecycUsers.setLayoutManager(m_cLayoutManager);
        m_cRecycUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = m_cLayoutManager.getChildCount();
                    totalItemCount = m_cLayoutManager.getItemCount();
                    pastVisiblesItems = m_cLayoutManager.findFirstVisibleItemPosition();

//                    int page = totalItemCount / 15;
                    if (m_cLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            m_cLoading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
//                            int lpage = page + 1;
//                            page = lpage;
//                            doPagination(lpage);
                        }
                    }
                }
            }
        });

        if (null != m_cGoOffline){
            initOffline();
            regNewTxt.setVisibility(View.GONE);
            addExTxt.setVisibility(View.GONE);
        }else {
            displayProgressBar(-1, "Loading...");
            RequestManager.getInstance(this).placeRequest(Constants.USERS, UsersAll.class, this, null, null, null, false);
        }

    }

    private void initOffline() {
        List<UsersTable> lUsersAll = UsersTable.listAll(UsersTable.class);
        if (null != lUsersAll && lUsersAll.size() > 0) {
            for (UsersTable lUsersTable : lUsersAll) {
                Users lUsers = new Users();
                lUsers.setId(lUsersTable.getUserId());
                lUsers.setFirstName(lUsersTable.getFirstName());
                lUsers.setMiddleName(lUsersTable.getMiddleName());
                lUsers.setLastName(lUsersTable.getLastName());
                lUsers.setUsername(lUsersTable.getUsername());
                lUsers.setRole(lUsersTable.getRole());
                lUsers.setRoleTitle(lUsersTable.getRole());
                lUsers.setTermsAccepted(lUsersTable.getTermsAccepted());
                lUsers.setIsAdmin(lUsersTable.getIsAdmin());
                m_cUsersList.add(lUsers);
            }
            m_cRecycAdUsers = new CustomRecyclerAdapterForUsers(this, m_cUsersList, m_cGoOffline, this);
            m_cRecycUsers.setAdapter(m_cRecycAdUsers);
            rlNoData.setVisibility(View.GONE);
        }else {
            if (null != m_cRecycAdUsers) {
                m_cUsersList.clear();
                m_cRecycAdUsers.notifyDataSetChanged();
            }
            rlNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        Intent lObjIntent;
        switch (pObjMessage.what){
            case PotMacros.ON_INFO_LONG_CLICK_USERS:
                switch (pObjMessage.arg1){
                    case R.id.action_remove:
                        Object[] lObjects = (Object[]) pObjMessage.obj;
                        Users lUsers = (Users) lObjects[0];
                        displayYesOrNoCustAlert(R.id.action_remove,
                                getResources().getString(R.string.remove_user_txt),
                                String.format("%s %s \n%s",
                                        lUsers.getFirstName() + " " +  lUsers.getLastName(),
                                        getResources().getString(R.string.will_be_removed_from_this_phone_txt),
                                        getResources().getString(R.string.do_you_want_to_proceed_txt)),
                                lObjects);
                        break;
                }
                break;
            case R.id.action_remove:
                Object[] lObjectsAll = (Object[]) pObjMessage.obj;
                if ((boolean) lObjectsAll[0]){
                    Object[] lObjects = (Object[]) lObjectsAll[1];
                    Users lUsers = (Users) lObjects[0];
                    displayProgressBar(-1, "Loading...");
                    new DeleteUser().execute(Constants.USERS +
                            lUsers.getId() +
                            "/" +
                            Constants.DEVICE +
                            PotMacros.getDeviceID(this) +
                            "/", lObjects);
                }
                break;
            case RE_FRESH:
                init();
                break;
            default:
                break;
        }
    }

    public class DeleteUser extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
                try {
                    URL url = new URL(RequestManager.IS_LIVE ? LIVE_BASEFEED_URL : TEST_BASEFEED_URL + objects[0]);
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setDoOutput(true);
                    httpCon.setRequestProperty("Content-Type", "application/json");
                    httpCon.setRequestProperty(Constants.AUTHORIZATION, PotMacros.getLoginAuth(PotLandingScreen.this));
                    httpCon.setRequestMethod("DELETE");
                    httpCon.connect();
                    httpCon.getInputStream();
                    int i = httpCon.getResponseCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            hideDialog();
            init();
        }
    }

    @Optional
    @OnClick({R.id.REGISTER_NEW_TXT, R.id.ADD_EXISTING_TXT, R.id.OVERVIEW_TXT})
    public void onClick(View v) {
        Intent lObjIntent;
        switch (v.getId()){
            case R.id.REGISTER_NEW_TXT:
                lObjIntent = new Intent(this, TermsAndCondition.class);
                lObjIntent.putExtra(PotMacros.OBJ_REGISTERATION, true);
                startActivity(lObjIntent);
                break;
            case R.id.ADD_EXISTING_TXT:
                lObjIntent = new Intent(this, PotAddExistingUserScreen.class);
                startActivity(lObjIntent);
                /*deleteRecursive(PotMacros.getOfflineAudioFilePath(this));
                deleteRecursive(PotMacros.getOfflineImageFilePath(this));
                LessonsTable.deleteAll(LessonsTable.class);*/
                break;
            case R.id.OVERVIEW_TXT:
                lObjIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PotMacros.OVERVIEW));
                startActivity(lObjIntent);
                break;
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        Intent lObjIntent;
        switch (apiMethod) {
            case Constants.USERS:
                UsersAll lUsersAll = (UsersAll) response;
                if (null != lUsersAll && lUsersAll.getUsers().size() > 0) {
                    checkAndUpdateUsers(lUsersAll);
                    for (Users lUsers : lUsersAll.getUsers()) {
                        m_cUsersList.add(lUsers);
                    }
                    m_cRecycAdUsers = new CustomRecyclerAdapterForUsers(this, m_cUsersList, m_cGoOffline, this);
                    m_cRecycUsers.setAdapter(m_cRecycAdUsers);
                    rlNoData.setVisibility(View.GONE);
                }else {
                    if (null != m_cRecycAdUsers) {
                        m_cUsersList.clear();
                        m_cRecycAdUsers.notifyDataSetChanged();
                    }
                    rlNoData.setVisibility(View.VISIBLE);
                }
                hideDialog();
                break;
            default:
                if (apiMethod.contains(Constants.SESSIONS)) {
                    Sessions lSessions = (Sessions) response;
                    PotMacros.saveUserToken(this, lSessions.getUserToken());
                    String lObjUser = (new Gson()).toJson(getUser());
                    lObjIntent = new Intent(this, PotUserHomeScreen.class);
                    lObjIntent.putExtra(PotMacros.OBJ_USER, lObjUser);
                    startActivity(lObjIntent);
                    finish();
                }else if (apiMethod.contains(Constants.DEVICE)) {
                    Object[] lObjects = (Object[]) refObj;
                    Users lUsers = (Users) lObjects[0];
                    PotMacros.clearNotifyCount(this, Constants.LESSON_SHARE, lUsers);
                    PotMacros.clearNotifyCount(this, Constants.CONNECTION_REQUEST, lUsers);
                    ShortcutBadger.applyCount(this, PotMacros.getNotifyAll(this)[0]);
                    if (null != m_cRecycAdUsers) {
                        m_cUsersList.remove((int) lObjects[1]);
                        m_cRecycUsers.setAdapter(new CustomRecyclerAdapterForUsers(this, m_cUsersList, m_cGoOffline, this));
                        m_cRecycUsers.invalidate();
//                        m_cObjUIHandler.sendEmptyMessage(RE_FRESH);
//                        break;
                    }
                } else {
                    super.onAPIResponse(response, apiMethod, refObj);
                }
                hideDialog();
                break;
        }
    }

    private void checkAndUpdateUsers(UsersAll pUsersAll) {
        for (Users lUsers : pUsersAll.getUsers()) {
//            List<UsersTable> lUsersTableListAll = UsersTable.listAll(UsersTable.class);
            List<UsersTable> lUsersTableList = UsersTable.find(UsersTable.class, "user_id = ?", String.valueOf(lUsers.getId()));
            if (lUsersTableList.size() == 0) {
                UsersTable lUsersTable = new UsersTable(lUsers.getId(), lUsers.getFirstName(), lUsers.getMiddleName(),
                        lUsers.getLastName(), lUsers.getUsername(), lUsers.getRole(), lUsers.getTermsAccepted(), lUsers.getIsAdmin());
                lUsersTable.save();
            } else {
//                List<UsersTable> lUsersTableAll = UsersTable.find(UsersTable.class, "user_id = ?", String.valueOf(lUsers.getId()));
                UsersTable lUsersTable = UsersTable.find(UsersTable.class, "user_id = ?", String.valueOf(lUsers.getId())).get(0);
                lUsersTable.setUserId(lUsers.getId());
                lUsersTable.setFirstName(lUsers.getFirstName());
                lUsersTable.setMiddleName(lUsers.getMiddleName());
                lUsersTable.setLastName(lUsers.getLastName());
                lUsersTable.setUsername(lUsers.getUsername());
                lUsersTable.setRole(lUsers.getRole());
                lUsersTable.setTermsAccepted(lUsers.getTermsAccepted());
                lUsersTable.setIsAdmin(lUsers.getIsAdmin());
                lUsersTable.save();
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod){
            case Constants.USERS:
                hideDialog();
                if(error instanceof NoConnectionError){
                    Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
                }else {
                    String lMsg = new String(error.networkResponse.data);
                    showErrorMsg(lMsg);
                }
                break;
            default:
                if (apiMethod.contains(Constants.SESSIONS) ||
                        apiMethod.contains(Constants.DEVICE)){
                    hideDialog();
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
                    } else {
                        String lMsg = new String(error.networkResponse.data);
                        showErrorMsg(lMsg);
                    }
                }else {
                    super.onErrorResponse(error, apiMethod, refObj);
                    hideDialog();
                };
                break;
        }
    }

    @Override
    public void onInfoClick(int pPostion, Users pUsers, View pView) {
        Intent lObjIntent;
        if (null != m_cGoOffline){
            lObjIntent = new Intent(this, PotUserHomeScreen.class);
            lObjIntent.putExtra(PotMacros.GO_OFFLINE, PotMacros.GO_OFFLINE);
            lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(pUsers));
            startActivity(lObjIntent);
            finish();
        }else {
            int sessionId = PotMacros.getSessionId(this) > -1 ? PotMacros.getSessionId(this) : PotMacros.getGreenSessionId(this);
            if (null != pUsers) {
                displayProgressBar(-1, "Loading...");
                RequestManager.getInstance(this).placeRequest(Constants.SESSIONS +
                        sessionId +
                        "/" +
                        Constants.USERS +
                        pUsers.getId() +
                        "/", Sessions.class, this, null, null, null, true);
                setUser(pUsers);
            }
        }
    }

    @Override
    public void onInfoLongClick(int pPostion, Users pUsers, View pView) {
        /*if (null == m_cGoOffline) {
            displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_USERS,
                    pUsers.getFirstName() + " " + pUsers.getLastName(),
                    Arrays.asList(getResources().getString(R.string.remove_user_txt)),
                    Arrays.asList(R.id.action_remove),
                    new Object[]{pUsers, pPostion});
        }*/
    }

    @Override
    public void onOptionsClick(int pPostion, Follows pFollows, View pView, int pOpt) {

    }

    public Users getUser() {
        return m_cUser;
    }

    public void setUser(Users pUser) {
        this.m_cUser = pUser;
    }
}
