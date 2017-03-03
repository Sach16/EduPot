package com.cosmicdew.lessonpot.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.activities.AddSyllabusScreen;
import com.cosmicdew.lessonpot.adapters.CustomExpandableListAdapterForNetworkConnections;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.interfaces.RecyclerNetworkListener;
import com.cosmicdew.lessonpot.interfaces.RecyclerSelectionListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Connections;
import com.cosmicdew.lessonpot.models.Group;
import com.cosmicdew.lessonpot.models.Groups;
import com.cosmicdew.lessonpot.models.Role;
import com.cosmicdew.lessonpot.models.Roles;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.models.UsersAll;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static com.cosmicdew.lessonpot.activities.PotUserNetworkScreen.REFRESH_CONSTANT;

/**
 * Created by S.K. Pissay on 22/11/16.
 */

public class PotUserNetworkConnectionsFragment extends PotFragmentBaseClass implements RecyclerNetworkListener, ExpandableListView.OnGroupExpandListener {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    RelativeLayout m_cRlMain;

    @Nullable
    @BindView(R.id.EXPAN_LIST_VIEW)
    ExpandableListView m_cExpandView;

    private int previousItem = 0;

    public static final int ADD_SYLLABUS = 11321;

    private int m_cPos;
    private String m_cKey;
    private Users m_cUser;
    private String m_cSelectionType;
    protected AlertDialog m_cObjDialog;

    private View mHeaderView;

    MyReceiver r;

    private boolean m_cIsFlatView = true;

    List<String> m_clistDataHeader;
    HashMap<String, Group> m_clistDataChild;
    HashMap<String, Role> m_clistDataChildFlat;
    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomExpandableListAdapterForNetworkConnections m_cExpandListAdapt;
    ArrayList<BoardChoices> m_cBoardClassList;

    public PotUserNetworkConnectionsFragment() {
        super();
    }

    public static PotUserNetworkConnectionsFragment newInstance(int pPosition, String pKey, Users pUser, String pSellectionType,
                                                                RecyclerSelectionListener pRecyclerSelectionListener) {
        PotUserNetworkConnectionsFragment lPotUserNetworkConnectionsFragment = new PotUserNetworkConnectionsFragment();

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("KEY", pKey);
        args.putString("OBJECT", (new Gson()).toJson(pUser));
        args.putString(PotMacros.OBJ_SELECTIONTYPE, pSellectionType);
        lPotUserNetworkConnectionsFragment.setArguments(args);

        return lPotUserNetworkConnectionsFragment;
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
        m_cObjMainView = inflater.inflate(R.layout.fragment_expandable_list, container, false);
        ButterKnife.bind(this, m_cObjMainView);

        m_cObjMainActivity.m_cObjFragmentBase = PotUserNetworkConnectionsFragment.this;
        addHeader();

        return m_cObjMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(m_cObjMainActivity).registerReceiver(r,
                new IntentFilter(REFRESH_CONSTANT));
        init();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            init();
        }
    }

    private void addHeader() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mHeaderView = inflater.inflate(R.layout.header_filter_layout, null);
        final TextView lHeaderText = (TextView) mHeaderView.findViewById(R.id.FILTER_LIST_TXT);
        lHeaderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_cIsFlatView)
                    lHeaderText.setText(getResources().getString(R.string.show_flat_txt));
                else
                    lHeaderText.setText(getResources().getString(R.string.show_groups_txt));
                m_cIsFlatView = !m_cIsFlatView;
                init();
            }
        });
        m_cExpandView.setDivider(null);
        m_cExpandView.addHeaderView(mHeaderView);
    }

    private void init() {
        m_cPos = getArguments().getInt("Position", 0);
        m_cKey = getArguments().getString("KEY");
        m_cUser = (new Gson()).fromJson(getArguments().getString("OBJECT"), Users.class);
        m_cSelectionType = getArguments().getString(PotMacros.OBJ_SELECTIONTYPE);
        m_cBoardClassList = new ArrayList<>();
        m_cLayoutManager = new LinearLayoutManager(m_cObjMainActivity);
        m_cLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //Call api here
        m_cObjMainActivity.displayProgressBar(-1, "");
        if (m_cIsFlatView) {
            HashMap<String, String> lParams = new HashMap<>();
            lParams.put(Constants.FLAT, String.valueOf(m_cIsFlatView));
            placeUserRequest(Constants.CONNECTIONS, Roles.class, null, lParams, null, false);
        } else {
            placeUserRequest(Constants.CONNECTIONS, Groups.class, null, null, null, false);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what) {
            case R.id.CHILD_LL:
                if (m_cIsFlatView) {
                    switch (pObjMessage.arg2) {
                        case R.id.action_add_syllabus:
                            HashMap<String, String> lParams = new HashMap<>();
                            lParams.put(Constants.USERNAME, PotMacros.SYLLABUSWIZARD_USERNAME);
                            m_cObjMainActivity.displayProgressBar(-1, "");
                            placeRequest(Constants.USERS,
                                    UsersAll.class, ((Connections) pObjMessage.obj).getConnectionTo(), lParams, null, false);
                            break;
                        case R.id.action_delete:
                            Connections pConnections = (Connections) pObjMessage.obj;
                            displayYesOrNoCustAlert(R.id.action_delete,
                                    getResources().getString(R.string.delete_connection_txt),
                                    String.format("%s \n\n%s",
                                            getResources().getString(R.string.all_lessons_shared_txt),
                                            getResources().getString(R.string.do_you_want_to_proceed_txt)),
                                    pConnections);

                            break;
                    }
                } else {
                    switch (pObjMessage.arg2) {
                        case R.id.action_add_syllabus:
                            HashMap<String, String> lParams = new HashMap<>();
                            lParams.put(Constants.USERNAME, PotMacros.SYLLABUSWIZARD_USERNAME);
                            m_cObjMainActivity.displayProgressBar(-1, "");
                            placeRequest(Constants.USERS,
                                    UsersAll.class, ((Role) pObjMessage.obj).getConnections().get(pObjMessage.arg1).getConnectionTo(), lParams, null, false);
                            break;
                        case R.id.action_delete:
                            Role pRole = (Role) pObjMessage.obj;
                            displayYesOrNoCustAlert(R.id.action_delete,
                                    getResources().getString(R.string.delete_connection_txt),
                                    String.format("%s \n\n%s",
                                            getResources().getString(R.string.all_lessons_shared_txt),
                                            getResources().getString(R.string.do_you_want_to_proceed_txt)),
                                    pRole);
                            break;
                    }
                }
                break;
            case R.id.action_delete:
                if (m_cIsFlatView) {
                    Object[] lObjects = (Object[]) pObjMessage.obj;
                    if ((boolean) lObjects[0]) {
                        Connections pConnections = (Connections) lObjects[1];
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        placeDeleteRequest(Constants.CONNECTIONS +
                                pConnections.getId() +
                                "/", Connections.class, null, null, null, true);
                    }
                }else {
                    Object[] lObjects = (Object[]) pObjMessage.obj;
                    if ((boolean) lObjects[0]) {
                        Role pRole = (Role) lObjects[1];
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        placeDeleteRequest(Constants.CONNECTIONS +
                                pRole.getConnections().get(pObjMessage.arg1).getId() +
                                "/", Connections.class, null, null, null, true);
                    }
                }
                break;
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        Intent lObjIntent;
        switch (apiMethod) {
            case Constants.USERS:
                UsersAll lUsers = (UsersAll) response;
                Users lUserConnected = (Users) refObj;
                lObjIntent = new Intent(m_cObjMainActivity, AddSyllabusScreen.class);
                if (lUserConnected.getId() == lUsers.getUsers().get(0).getId())
                    lObjIntent.putExtra(PotMacros.OBJ_USER_MAIN, (new Gson()).toJson(m_cUser));
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(lUserConnected));
                startActivityForResult(lObjIntent, ADD_SYLLABUS);
                break;
            case Constants.CONNECTIONS:
                if (m_cIsFlatView) {
                    Roles lRoles = (Roles) response;
                    if (null != lRoles && lRoles.getRoles().size() > 0) {
                        m_clistDataHeader = new ArrayList<String>();
                        m_clistDataChildFlat = new HashMap<>();
                        for (Role lRole : lRoles.getRoles()) {
                            m_clistDataHeader.add(lRole.getName());
                            m_clistDataChildFlat.put(lRole.getName(),
                                    lRole);
                        }
                    } else {
                        if (m_cExpandListAdapt != null) {
                            m_clistDataHeader.clear();
                            m_clistDataChildFlat.clear();
                            m_cExpandView.setAdapter(new CustomExpandableListAdapterForNetworkConnections(m_cObjMainActivity, m_clistDataHeader,
                                    m_clistDataChild, m_clistDataChildFlat, m_cIsFlatView, this));
                            m_cExpandView.invalidate();
                            m_cExpandView.removeHeaderView(mHeaderView);
                        }
                        m_cObjMainActivity.hideDialog();
                        break;
                    }
                } else {
                    Groups lGroups = (Groups) response;
                    if (null != lGroups && lGroups.getGroups().size() > 0) {
                        m_clistDataHeader = new ArrayList<String>();
                        m_clistDataChild = new HashMap<>();
                        for (Group lGroup : lGroups.getGroups()) {
                            m_clistDataHeader.add(lGroup.getName() + "#" + lGroup.getComment());
                            m_clistDataChild.put(lGroup.getName() + "#" + lGroup.getComment(),
                                    lGroup);
                        }
                    } else {
                        if (m_cExpandListAdapt != null) {
                            m_clistDataHeader.clear();
                            m_clistDataChild.clear();
                            m_cExpandView.setAdapter(new CustomExpandableListAdapterForNetworkConnections(m_cObjMainActivity, m_clistDataHeader,
                                    m_clistDataChild, m_clistDataChildFlat, m_cIsFlatView, this));
                            m_cExpandView.invalidate();
                            m_cExpandView.removeHeaderView(mHeaderView);
                        }
                        m_cObjMainActivity.hideDialog();
                        break;
                    }
                }
                m_cExpandListAdapt = new CustomExpandableListAdapterForNetworkConnections(m_cObjMainActivity, m_clistDataHeader,
                        m_clistDataChild, m_clistDataChildFlat, m_cIsFlatView, this);
                m_cExpandView.setAdapter(m_cExpandListAdapt);

                if (m_cIsFlatView) {
                    for (int i = 0; i < m_cExpandListAdapt.getGroupCount(); i++)
                        m_cExpandView.expandGroup(i);
                } else {
                    m_cExpandView.expandGroup(0);
                }
                m_cExpandView.setOnGroupExpandListener(this);
                m_cObjMainActivity.hideDialog();
                break;
            default:
                if (apiMethod.contains(Constants.CONNECTIONS)) {
                    if (response == null) {
                        m_cObjMainActivity.displayToast(getResources().getString(R.string.connection_deleted_txt));
                    }
                    init();
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
            case Constants.USERS:
            case Constants.CONNECTIONS:
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

    @Optional
    @OnClick({})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        }
    }

    @Override
    public void onInfoClick(int pPostion,  Role pRole, Connections pConnections, boolean pMode, View pView) {
        if (pMode){
        }else {
            ArrayList<String> list = new ArrayList<>();
            if (pRole.getConnections().size() > 0) {
                for (Connections lConnections : pRole.getConnections())
                    list.add(lConnections.getConnectionTo().getFirstName() + " " +
                            lConnections.getConnectionTo().getLastName());
                m_cObjMainActivity.displayOptionsSpinnerDialog(pView.getId(), pRole.getName(),
                        list, pRole);
            }
        }
    }

    @Override
    public void onInfoLongClick(int pid, int pPostion, Role pRole, Connections pConnections, boolean pMode, View pView) {
        if (pMode){
            switch (pid){
                case R.id.action_add_syllabus:
                    Message lMsg = new Message();
                    lMsg.what = R.id.CHILD_LL;
                    lMsg.arg1 = pPostion;
                    lMsg.arg2 = pid;
                    lMsg.obj = pConnections;
                    m_cObjUIHandler.sendMessage(lMsg);
                    break;
                case R.id.action_delete:
                    Message llMsg = new Message();
                    llMsg.what = R.id.CHILD_LL;
                    llMsg.arg1 = pPostion;
                    llMsg.arg2 = pid;
                    llMsg.obj = pConnections;
                    m_cObjUIHandler.sendMessage(llMsg);
                    break;
            }
        }else {
        }
    }

    @Override
    public void onOptionClick(int pPostion, Connections pConnections, int pOptionId, View pView) {

    }

    @Override
    public void onGroupExpand(int groupPosition) {
        if (!m_cIsFlatView) {
            if (groupPosition != previousItem)
                m_cExpandView.collapseGroup(previousItem);
            previousItem = groupPosition;
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