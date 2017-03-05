package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForClassesAndBoards;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.fragments.PotAdEdBoardFragment;
import com.cosmicdew.lessonpot.interfaces.RecyclerClassBoardsListener;
import com.cosmicdew.lessonpot.interfaces.RefreshClassBoardsListener;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.BoardChoicesAll;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 30/12/16.
 */

public class PotEditBoardClassScreen extends PotBaseActivity implements RefreshClassBoardsListener, RecyclerClassBoardsListener {

    @Nullable
    @BindView(R.id.ADD_BOARDS_MAIN_LAYOUT)
    RelativeLayout m_cllMain;

    @Nullable
    @BindView(R.id.RECYC_CLASS_BOARDS)
    RecyclerView m_cRecycBoards;

    private Menu mMenu;
    private String m_cRole;
    public static final String FRAG_TAG = "PotAdEdBoardFragment";

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForClassesAndBoards m_cRecycBoardAdapt;
    ArrayList<BoardChoices> m_cBoardClassList;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.edit_board_class_screen);
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

        //calling Board class api
        RequestManager.getInstance(this).placeUserRequest(Constants.BOARDCLASSES, BoardChoicesAll.class, this, null, null, null, false);

//        refreshBoardsList();
    }

    private void refreshBoardsList() {
        /*if (null != m_cBoardClassList && m_cBoardClassList.size() > 0) {
            if (m_cRecycBoardAdapt == null) {
                m_cRecycBoardAdapt = new CustomRecyclerAdapterForClassesAndBoards(this, m_cBoardClassList, true, this);
                m_cRecycBoards.setAdapter(m_cRecycBoardAdapt);
            }else {
                m_cRecycBoardAdapt.notifyDataSetChanged();
            }
        }else {
            if (null != m_cBoardClassList && m_cRecycBoardAdapt != null) {
                m_cBoardClassList.clear();
                m_cRecycBoardAdapt.notifyDataSetChanged();
            }
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
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
                        .add(R.id.ADD_BOARDS_MAIN_LAYOUT, new PotAdEdBoardFragment().newInstance(-1, null, this), FRAG_TAG)
                        .commit();
                switchModeFragment(true, -1);
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
//            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            if (pPos > -1)
                getSupportActionBar().setTitle(getResources().getString(R.string.edit_boards_or_classes));
            else
                getSupportActionBar().setTitle(getResources().getString(R.string.add_boards_or_classes));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            MenuItem register = mMenu.findItem(R.id.action_add);
            register.setVisible(false);
        } else {
            getSupportActionBar().setTitle(getResources().getString(R.string.add_boards_and_classes));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            MenuItem register = mMenu.findItem(R.id.action_add);
            register.setVisible(true);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //userRegistered is boolean, pointing if the user has registered or not.
        return true;

    }

    @Optional
    @OnClick({})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                super.onClick(v);
                break;
        }

    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        Intent lObjIntent;
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.BOARDCLASSES)) {
                    BoardChoicesAll lBoardChoicesAll = (BoardChoicesAll) response;
                    if (lBoardChoicesAll != null && lBoardChoicesAll.getBoardChoices().size() > 0) {
                        for (BoardChoices lBoardChoices : lBoardChoicesAll.getBoardChoices()) {
                            if (!lBoardChoices.getBoardclass().getIsGeneric())
                                m_cBoardClassList.add(lBoardChoices);
                        }
                        if (null != m_cBoardClassList && m_cBoardClassList.size() > 0) {
                            m_cRecycBoardAdapt = new CustomRecyclerAdapterForClassesAndBoards(this, m_cBoardClassList, true, this);
                            m_cRecycBoards.setAdapter(m_cRecycBoardAdapt);
                        }
                    } else {
                        if (m_cBoardClassList.size() > 0) {
                            m_cBoardClassList.clear();
                            m_cRecycBoardAdapt.notifyDataSetChanged();
                        }
                    }
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
                if (apiMethod.contains(Constants.BOARDCLASSES)) {
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
            ((PotAdEdBoardFragment) getSupportFragmentManager().findFragmentByTag(FRAG_TAG)).onBackPressed();
            switchModeFragment(false, -1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onUpdate(int pPostion, BoardChoices pBoardChoices, View pView) {
        /*if (pPostion < 0) {
            m_cBoardClassList.add(pBoardChoices);
            refreshBoardsList();
            switchModeFragment(false, -1);
        }else {
            m_cBoardClassList.set(pPostion, pBoardChoices);
            refreshBoardsList();
            switchModeFragment(false, -1);
        }*/
    }

    @Override
    public void resetFragment(boolean pState) {
        //below 1st line added new
        if (null != m_cRecycBoardAdapt) {
            m_cBoardClassList.clear();
            m_cRecycBoardAdapt.notifyDataSetChanged();
        }
        init();
        switchModeFragment(pState, -1);
    }

    @Override
    public void onInfoClick(int pPostion, BoardChoices pBoardChoices, View pView) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.ADD_BOARDS_MAIN_LAYOUT, new PotAdEdBoardFragment().newInstance(pPostion, pBoardChoices, this), FRAG_TAG)
                .commit();
        switchModeFragment(true, pPostion);
    }

    @Override
    public void onInfoLongClick(int pPostion, BoardChoices pBoardChoices, View pView) {
    }

    @Override
    public void onSelectionClicked(int pPostion, BoardChoices pBoardChoices, View pView) {

    }
}
