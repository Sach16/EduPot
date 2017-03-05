package com.cosmicdew.lessonpot.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForNetworkFollowing;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.interfaces.RecyclerSelectionListener;
import com.cosmicdew.lessonpot.interfaces.RecyclerUsersListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Follows;
import com.cosmicdew.lessonpot.models.FollowsAll;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by S.K. Pissay on 22/2/17.
 */

public class PotUserNetworkFollowingFragment extends PotFragmentBaseClass implements RecyclerUsersListener {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    RelativeLayout m_cRlMain;

    @Nullable
    @BindView(R.id.RECYC_HOME_CLASS_BOARDS)
    RecyclerView m_cRecycClasses;

    private int m_cPos;
    private String m_cKey;
    private Users m_cUser;
    private String m_cSelectionType;

    private Dialog m_cObjDialog;

    public static final int ACTION_DELETE_CONNECTION = 9179;

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForNetworkFollowing m_cRecycClassesAdapt;
    ArrayList<Follows> m_cFollowsList;

    public PotUserNetworkFollowingFragment() {
        super();
    }

    public static PotUserNetworkFollowingFragment newInstance(int pPosition, String pKey, Users pUser, String pSellectionType,
                                                              RecyclerSelectionListener pRecyclerSelectionListener) {
        PotUserNetworkFollowingFragment lPotUserNetworkFollowingFragment = new PotUserNetworkFollowingFragment();

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("KEY", pKey);
        args.putString("OBJECT", (new Gson()).toJson(pUser));
        args.putString(PotMacros.OBJ_SELECTIONTYPE, pSellectionType);
        lPotUserNetworkFollowingFragment.setArguments(args);

        return lPotUserNetworkFollowingFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        m_cIsActivityAttached = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_cObjMainView = inflater.inflate(R.layout.fragment_homeclasses, container, false);
        ButterKnife.bind(this, m_cObjMainView);

        m_cObjMainActivity.m_cObjFragmentBase = PotUserNetworkFollowingFragment.this;

        return m_cObjMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        m_cPos = getArguments().getInt("Position", 0);
        m_cKey = getArguments().getString("KEY");
        m_cUser = (new Gson()).fromJson(getArguments().getString("OBJECT"), Users.class);
        m_cSelectionType = getArguments().getString(PotMacros.OBJ_SELECTIONTYPE);
        m_cFollowsList = new ArrayList<>();
        m_cLayoutManager = new LinearLayoutManager(m_cObjMainActivity);
        m_cLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        m_cRecycClasses.setLayoutManager(m_cLayoutManager);
        m_cRecycClasses.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        //Call api here
        m_cObjMainActivity.displayProgressBar(-1, "");
        placeUserRequest(Constants.FOLLOWING, FollowsAll.class, null, null, null, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what) {
            case ACTION_DELETE_CONNECTION:
                Object[] lObjectDelMy = (Object[]) pObjMessage.obj;
                Follows lFollows = (Follows) lObjectDelMy[1];
                if ((boolean) lObjectDelMy[0]) {
                    m_cObjMainActivity.displayProgressBar(-1, "");
                    placeUnivUserRequest(Constants.FOLLOWERS +
                            lFollows.getId() +
                            "/", Follows.class, null, null, null, Request.Method.DELETE);
                }
                break;
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            case Constants.FOLLOWING:
                FollowsAll lFollowsAll = (FollowsAll) response;
                if (lFollowsAll != null && lFollowsAll.getFollows().size() > 0) {
                    for (Follows lFollows : lFollowsAll.getFollows()) {
                        m_cFollowsList.add(lFollows);
                    }
                    if (null != m_cFollowsList && m_cFollowsList.size() > 0) {
                        m_cRecycClassesAdapt = new CustomRecyclerAdapterForNetworkFollowing(m_cObjMainActivity, m_cFollowsList, m_cSelectionType, this);
                        m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
                    }
                } else {
                    if (m_cRecycClassesAdapt != null) {
                        m_cFollowsList.clear();
                        m_cRecycClasses.setAdapter(new CustomRecyclerAdapterForNetworkFollowing(m_cObjMainActivity, m_cFollowsList, m_cSelectionType, this));
                        m_cRecycClasses.invalidate();
                    }
                }
                m_cObjMainActivity.hideDialog();
                break;
            default:
                if (apiMethod.contains(Constants.FOLLOWERS)) {
                    if (response == null) {
                        m_cObjMainActivity.hideDialog();
                        m_cObjMainActivity.displayToast(getResources().getString(R.string.unfollowed_txt));
                        m_cFollowsList.clear();
                        m_cRecycClasses.setAdapter(new CustomRecyclerAdapterForNetworkFollowing(m_cObjMainActivity, m_cFollowsList, m_cSelectionType, this));
                        m_cRecycClasses.invalidate();
                        init();
                    }
                } else {
                    super.onAPIResponse(response, apiMethod, refObj);
                    m_cObjMainActivity.hideDialog();
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod) {
            case Constants.CONNECTION_REQUESTS:
                m_cObjMainActivity.hideDialog();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(m_cObjMainActivity, "Please check Network connection", Toast.LENGTH_SHORT).show();
                } else {
                    String lMsg = new String(error.networkResponse.data);
                    m_cObjMainActivity.showErrorMsg(lMsg);
                }
                break;
            default:
                if (apiMethod.contains(Constants.FOLLOWERS)) {
                    m_cObjMainActivity.hideDialog();
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(m_cObjMainActivity, "Please check Network connection", Toast.LENGTH_SHORT).show();
                    } else {
                        String lMsg = new String(error.networkResponse.data);
                        m_cObjMainActivity.showErrorMsg(lMsg);
                    }
                } else {
                    super.onErrorResponse(error, apiMethod, refObj);
                    m_cObjMainActivity.hideDialog();
                }
                break;
        }
    }

    @Override
    public void onInfoClick(int pPostion, Users pUsers, View pView) {

    }

    @Override
    public void onInfoLongClick(int pPostion, Users pUsers, View pView) {

    }

    @Override
    public void onOptionsClick(int pPostion, Follows pFollows, View pView, int pOpt) {
        switch (pOpt){
            case 0:
                break;
            case 1:
                displayYesOrNoCustAlert(ACTION_DELETE_CONNECTION,
                        getResources().getString(R.string.unfollow_txt),
                        getResources().getString(R.string.all_lessons_shared_by_txt),
                        pFollows);
                break;
        }
    }

    public void displayYesOrNoCustAlert(final int pId, String pTitle, String pMessage, final Object pObj) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(m_cObjMainActivity);
        View lView = LayoutInflater.from(m_cObjMainActivity).inflate(R.layout.spinner_header, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        final View lMainView = LayoutInflater.from(m_cObjMainActivity).inflate(R.layout.lesson_yes_no_dialog, null);
        ((TextView) lMainView.findViewById(R.id.ALLERT_TXT)).setText(pMessage);
        lObjBuilder.setView(lMainView);
        ((TextView) lMainView.findViewById(R.id.NO_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_cObjDialog.dismiss();
            }
        });
        ((TextView) lMainView.findViewById(R.id.YES_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message lMsg = new Message();
                lMsg.what = pId;
                lMsg.obj = new Object[]{true, pObj};
                m_cObjUIHandler.sendMessage(lMsg);
                m_cObjDialog.dismiss();
            }
        });
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(false);
        m_cObjDialog.show();
    }
}
