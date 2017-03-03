package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForClassesAndBoards;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.fragments.PotAdBoardFragment;
import com.cosmicdew.lessonpot.interfaces.RecyclerClassBoardsListener;
import com.cosmicdew.lessonpot.interfaces.RefreshClassBoardsListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Sessions;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 20/10/16.
 */

public class PotBoardClassScreen extends PotBaseActivity implements RefreshClassBoardsListener, RecyclerClassBoardsListener {

    @Nullable
    @BindView(R.id.ADD_BOARDS_MAIN_LAYOUT)
    RelativeLayout m_cllMain;

    @Nullable
    @BindView(R.id.RECYC_CLASS_BOARDS)
    RecyclerView m_cRecycBoards;

    @Nullable
    @BindView(R.id.NO_DATA_RL)
    RelativeLayout m_cNoDataLay;

    @Nullable
    @BindView(R.id.REGISTER_TXT_LAY)
    RelativeLayout m_cRegRl;

    @Nullable
    @BindView(R.id.REGISTER_TXT)
    TextView m_cRegTxt;

    private Menu mMenu;
    private String m_cFirstName;
    private String m_cLastName;
    private String m_cRole;
    public static final String FRAG_TAG = "PotAdBoardFragment";

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForClassesAndBoards m_cRecycBoardAdapt;
    ArrayList<BoardChoices> m_cBoardClassList;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.add_boards_class_screen);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(getResources().getString(R.string.add_boards_and_classes));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        m_cFirstName = getIntent().getStringExtra(Constants.FIRST_NAME);
        m_cLastName = getIntent().getStringExtra(Constants.LAST_NAME);
        m_cRole = getIntent().getStringExtra(Constants.ROLE);
        m_cBoardClassList = new ArrayList<>();
        m_cLayoutManager = new LinearLayoutManager(this);
        m_cLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        m_cRecycBoards.setLayoutManager(m_cLayoutManager);
        m_cRecycBoards.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        refreshBoardsList();
    }

    private void refreshBoardsList() {
        if (null != m_cBoardClassList && m_cBoardClassList.size() > 0) {
            if (m_cRecycBoardAdapt == null) {
                m_cRecycBoardAdapt = new CustomRecyclerAdapterForClassesAndBoards(this, m_cBoardClassList, false, this);
                m_cRecycBoards.setAdapter(m_cRecycBoardAdapt);
            }else {
                m_cRecycBoardAdapt.notifyDataSetChanged();
            }
            m_cNoDataLay.setVisibility(View.GONE);
            m_cRegRl.setVisibility(View.VISIBLE);
        }else {
            if (null != m_cBoardClassList && m_cRecycBoardAdapt != null) {
                m_cBoardClassList.clear();
                m_cRecycBoardAdapt.notifyDataSetChanged();
            }
            m_cNoDataLay.setVisibility(View.VISIBLE);
            m_cRegRl.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pot_class_board, menu);
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent lObjIntent;
        switch (item.getItemId()) {
            case R.id.action_add:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.ADD_BOARDS_MAIN_LAYOUT, new PotAdBoardFragment().newInstance(-1, null, this), FRAG_TAG)
                        .commit();
                switchModeFragment(true, -1);
                return true;
            case R.id.action_delete:
                //TODO : remove the selected board and refresh the list
                if (((PotAdBoardFragment)getSupportFragmentManager().findFragmentByTag(FRAG_TAG)).getPos() > -1) {
                    m_cBoardClassList.remove(((PotAdBoardFragment) getSupportFragmentManager().findFragmentByTag(FRAG_TAG)).getPos());
                    m_cRecycBoardAdapt.notifyDataSetChanged();
                    ((PotAdBoardFragment) getSupportFragmentManager().findFragmentByTag(FRAG_TAG)).onBackPressed();
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void switchModeFragment(boolean pIsFragment, Integer pPos) {
        if (pIsFragment) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.titleboards_center);
            MenuItem register = mMenu.findItem(R.id.action_add);
            register.setVisible(false);
            if (pPos > -1) {
                MenuItem delete = mMenu.findItem(R.id.action_delete);
                delete.setVisible(true);
            }
        }else {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.titleclass_center);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            MenuItem register = mMenu.findItem(R.id.action_add);
            register.setVisible(true);
            MenuItem delete = mMenu.findItem(R.id.action_delete);
            delete.setVisible(false);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem register = menu.findItem(R.id.action_delete);
        register.setVisible(false);  //userRegistered is boolean, pointing if the user has registered or not.
        return true;

    }

    @Optional
    @OnClick({R.id.REGISTER_TXT, R.id.CANCEL_TXT})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.REGISTER_TXT:
                if (null != m_cBoardClassList && m_cBoardClassList.size() > 0) {
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.FIRST_NAME, m_cFirstName);
                        lJO.put(Constants.LAST_NAME, m_cLastName);
                        lJO.put(Constants.ROLE, m_cRole);
                        lJO.put(Constants.TERMS_ACCEPTED, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    displayProgressBar(-1, "");
                    RequestManager.getInstance(this).placeRequest(Constants.USERS, Users.class, this, null, null, lJO.toString(), true);
                }
                break;
            case R.id.CANCEL_TXT:
                onBackPressed();
                break;
            default:
                super.onClick(v);
                break;
        }

    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        Intent lObjIntent;
        switch (apiMethod){
            case Constants.USERS:
                displayProgressBar(-1, "Loading");
                Users lUsers = (Users) response;
                if (null != lUsers.getCreated()) {
                    int sessionId = PotMacros.getSessionId(this) > -1 ? PotMacros.getSessionId(this) : PotMacros.getGreenSessionId(this);
                    RequestManager.getInstance(this).placeRequest(Constants.SESSIONS +
                            sessionId +
                            "/" +
                            Constants.USERS +
                            lUsers.getId() +
                            "/", Sessions.class, this, null, null, null, true);
                }
                break;
            default:
                if (apiMethod.contains(Constants.SESSIONS)) {
                    Sessions lSessions = (Sessions) response;
                    PotMacros.saveUserToken(this, lSessions.getUserToken());
                    if (null != m_cBoardClassList && m_cBoardClassList.size() > 0) {
                        displayProgressBar(-1, "Loading");
                        for (BoardChoices lBoard : m_cBoardClassList) {
                            RequestManager.getInstance(this).placeUserRequest(Constants.BOARDCLASSES +
                                    lBoard.getBoard() +
                                    "/", Sessions.class, this, null, null, null, true);
                        }
                    }
                } else if (apiMethod.contains(Constants.BOARDCLASSES)) {
                    Log.v("TAG", "Added boardClass");
                    lObjIntent = new Intent(this, PotLandingScreen.class);
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(lObjIntent);
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
            case Constants.USERS:
                hideDialog();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
                } else {
                    String lMsg = new String(error.networkResponse.data);
                    showErrorMsg(lMsg);
                }
                break;
            default:
                if (apiMethod.contains(Constants.BOARDCLASSES) || apiMethod.contains(Constants.SESSIONS)){
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
                }
                break;
        }
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(FRAG_TAG) != null) {
            getSupportFragmentManager().findFragmentByTag(FRAG_TAG);
            ((PotAdBoardFragment)getSupportFragmentManager().findFragmentByTag(FRAG_TAG)).onBackPressed();
            switchModeFragment(false, -1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onUpdate(int pPostion, BoardChoices pBoardChoices, View pView) {
        if (pPostion < 0) {
            m_cBoardClassList.add(pBoardChoices);
            refreshBoardsList();
            switchModeFragment(false, -1);
        }else {
            m_cBoardClassList.set(pPostion, pBoardChoices);
            refreshBoardsList();
            switchModeFragment(false, -1);
        }
    }

    @Override
    public void resetFragment(boolean pState) {
        //below 1st line added new
        refreshBoardsList();
        switchModeFragment(pState, -1);
    }

    @Override
    public void onInfoClick(int pPostion, BoardChoices pBoardChoices, View pView) {
        /*getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.ADD_BOARDS_MAIN_LAYOUT, new PotAdBoardFragment().newInstance(pPostion, pBoardChoices, this), FRAG_TAG)
                .commit();
        switchModeFragment(true, pPostion);*/
    }

    @Override
    public void onInfoLongClick(int pPostion, BoardChoices pBoardChoices, View pView) {
    }

    @Override
    public void onSelectionClicked(int pPostion, BoardChoices pBoardChoices, View pView) {

    }
}
