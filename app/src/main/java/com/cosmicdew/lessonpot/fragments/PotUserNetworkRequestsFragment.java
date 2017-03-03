package com.cosmicdew.lessonpot.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForNetworkRequests;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.interfaces.RecyclerNetworkListener;
import com.cosmicdew.lessonpot.interfaces.RecyclerSelectionListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Connections;
import com.cosmicdew.lessonpot.models.ConnectionsAll;
import com.cosmicdew.lessonpot.models.Role;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cosmicdew.lessonpot.activities.PotUserNetworkScreen.REFRESH_CONSTANT;

/**
 * Created by S.K. Pissay on 22/11/16.
 */

public class PotUserNetworkRequestsFragment extends PotFragmentBaseClass implements RecyclerNetworkListener {

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

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForNetworkRequests m_cRecycClassesAdapt;
    ArrayList<Connections> m_cConnectionsList;

    public PotUserNetworkRequestsFragment() {
        super();
    }

    public static PotUserNetworkRequestsFragment newInstance(int pPosition, String pKey, Users pUser, String pSellectionType,
                                                             RecyclerSelectionListener pRecyclerSelectionListener) {
        PotUserNetworkRequestsFragment lPotUserNetworkRequestsFragment = new PotUserNetworkRequestsFragment();

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("KEY", pKey);
        args.putString("OBJECT", (new Gson()).toJson(pUser));
        args.putString(PotMacros.OBJ_SELECTIONTYPE, pSellectionType);
        lPotUserNetworkRequestsFragment.setArguments(args);

        return lPotUserNetworkRequestsFragment;
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

        m_cObjMainActivity.m_cObjFragmentBase = PotUserNetworkRequestsFragment.this;

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
        m_cConnectionsList = new ArrayList<>();
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
        placeUserRequest(Constants.CONNECTION_REQUESTS, ConnectionsAll.class, null, null, null, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {

    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            case Constants.CONNECTION_REQUESTS:
                ConnectionsAll lConnectionsAll = (ConnectionsAll) response;
                if (lConnectionsAll != null && lConnectionsAll.getConnections().size() > 0) {
                    for (Connections lConnections : lConnectionsAll.getConnections()) {
                        m_cConnectionsList.add(lConnections);
                    }
                    if (null != m_cConnectionsList && m_cConnectionsList.size() > 0) {
                        m_cRecycClassesAdapt = new CustomRecyclerAdapterForNetworkRequests(m_cObjMainActivity, m_cConnectionsList, m_cSelectionType, this);
                        m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
                    }
                } else {
                    if (m_cRecycClassesAdapt != null) {
                        m_cConnectionsList.clear();
                        m_cRecycClasses.setAdapter(new CustomRecyclerAdapterForNetworkRequests(m_cObjMainActivity, m_cConnectionsList, m_cSelectionType, this));
                        m_cRecycClasses.invalidate();
                    }
                }
                m_cObjMainActivity.hideDialog();
                break;
            default:
                if (apiMethod.contains(Constants.CONNECTIONS)) {
                    if (response == null) {
                        m_cObjMainActivity.displayToast(getResources().getString(R.string.connection_request_deleted_txt));
                    } else if (response instanceof Connections) {
                        Connections lConnections = (Connections) response;
                        if (lConnections.getIsSpam()) {
                            m_cObjMainActivity.displayToast(getResources().getString(R.string.connection_marked_as_spam_txt));
                        } else {
                            m_cObjMainActivity.displayToast(getResources().getString(R.string.connection_accepted_successfully_txt));
                        }
                        m_cObjMainActivity.hideDialog();
                    }
                    init();
                    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(m_cObjMainActivity);
                    Intent i = new Intent(REFRESH_CONSTANT);
                    lbm.sendBroadcast(i);
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
                if (apiMethod.contains(Constants.CONNECTIONS)) {
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
    public void onInfoClick(int pPostion, Role pRole, Connections pConnections, boolean pMode, View pView) {

    }

    @Override
    public void onInfoLongClick(int pid, int pPostion, Role pRole, Connections pConnections, boolean pMode, View pView) {

    }

    @Override
    public void onOptionClick(int pPostion, Connections pConnections, int pOptionId, View pView) {
        switch (pOptionId) {
            case R.id.ACCEPT_NET_TXT:
                m_cObjMainActivity.displayProgressBar(-1, "");
                JSONObject lJO = new JSONObject();
                try {
                    lJO.put(Constants.IS_APPROVED, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                placePutRequest(Constants.CONNECTIONS +
                        pConnections.getId() +
                        "/", Connections.class, null, null, lJO.toString(), true);
                break;
            case R.id.DELETE_NET_TXT:
                m_cObjMainActivity.displayProgressBar(-1, "");
                placeDeleteRequest(Constants.CONNECTIONS +
                        pConnections.getId() +
                        "/", Connections.class, null, null, null, true);
                break;
            case R.id.SPAM_SETTINGS_IMG:
                JSONObject llJO = new JSONObject();
                try {
                    llJO.put(Constants.IS_SPAM, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                placePutRequest(Constants.CONNECTIONS +
                        pConnections.getId() +
                        "/", Connections.class, null, null, llJO.toString(), true);
                break;
        }
    }
}
