package com.cosmicdew.lessonpot.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.adapters.CustomExpandableListAdapterForLessonsFilter;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.interfaces.RecyclerFilterListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.Group;
import com.cosmicdew.lessonpot.models.Groups;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.Role;
import com.cosmicdew.lessonpot.models.RoleFilter;
import com.cosmicdew.lessonpot.models.RoleFilters;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.models.UsersShare;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 8/12/16.
 */

public class ReceivedLessonsFilterScreen extends PotBaseActivity implements RecyclerFilterListener, ExpandableListView.OnGroupExpandListener {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    RelativeLayout m_cRlMain;

    @Nullable
    @BindView(R.id.EXPAN_LIST_VIEW)
    ExpandableListView m_cExpandView;

    @Nullable
    @BindView(R.id.SAVE_LESSON_TXT)
    TextView applyFilterTxt;

    private int previousItem = 0;

    private int m_cPos;
    private String m_cKey;
    private Users m_cUser;
    private BoardChoices m_cBoardChoice;
    private Syllabi m_cSyllabi;
    private Chapters m_cChapters;
    private Lessons m_cLessons;
    private String m_cSelectionType;

    private String mLessFromWhere;

    private boolean m_cIsFlatView = true;

    List<String> m_clistDataHeader;
    HashMap<String, Group> m_clistDataChild;
    HashMap<String, RoleFilter> m_clistDataChildFlat;
    HashMap<Integer, Object> m_cSelectionItems;
    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomExpandableListAdapterForLessonsFilter m_cExpandListAdapt;
    ArrayList<BoardChoices> m_cBoardClassList;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.fragment_expandable_share_list);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(getResources().getString(R.string.filter_lessons_txt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        applyFilterTxt.setText(getResources().getString(R.string.apply_txt));

        addHeader();

    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void addHeader() {
        LayoutInflater inflater = LayoutInflater.from(this);
        m_cExpandView.setDivider(null);
        m_cExpandView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    //do your per-item callback here
                    return true; //true if we consumed the click, false if not

                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    //check for id for safer calls
                    if (view.getId() == R.id.LIST_HEADER) {
                        int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                        ImageView lblImageViewTick = (ImageView) view
                                .findViewById(R.id.iv_tick);
                        if (checkAndUpdate(m_cSelectionItems.get(groupPosition))) {
                            lblImageViewTick.setImageDrawable(getResources().getDrawable(R.drawable.tick_blue));
                            m_cExpandView.collapseGroup(groupPosition);
                        } else {
                            lblImageViewTick.setImageDrawable(null);
                        }
                    }
                    //do your per-group callback here
                    return true; //true if we consumed the click, false if not
                } else {
                    // null item; we don't consume the click
                    return false;
                }
            }
        });
        m_cExpandView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                                        long id) {
                if (m_cIsFlatView) {
                    if (((RoleFilter)m_cSelectionItems.get(groupPosition)).isSelected()) {
                        return false;
                    } else {
                        return false;
                    }
                }else {
                    if (((Group)m_cSelectionItems.get(groupPosition)).isSelected()) {
                        return false;
                    } else {
                        return false;
                    }
                }
            }
        });
    }

    private boolean checkAndUpdate(Object o) {
        boolean lRetVal = false;
        if (o instanceof RoleFilter) {
            RoleFilter lRoleFilter = (RoleFilter) o;
            lRetVal = !lRoleFilter.isSelected();
            lRoleFilter.setSelected(lRetVal);
            for (Users lUsers : lRoleFilter.getUsers())
                lUsers.setSelected(lRetVal);
        }
        return lRetVal;
    }

    private void init() {
        m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);
        m_cBoardChoice = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_BOARDCHOICES), BoardChoices.class);
        m_cSyllabi = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_SYLLABI), Syllabi.class);
        m_cChapters = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_CHAPTERS), Chapters.class);
        m_cLessons = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_LESSON), Lessons.class);
        mLessFromWhere = getIntent().getStringExtra(PotMacros.OBJ_LESSONFROM);

        m_cSelectionType = getIntent().getStringExtra(PotMacros.OBJ_SELECTIONTYPE);

        m_cBoardClassList = new ArrayList<>();
        m_cSelectionItems = new HashMap<>();
        m_cLayoutManager = new LinearLayoutManager(this);
        m_cLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //Call api here
        displayProgressBar(-1, "");
        HashMap<String, String> lParams = new HashMap<>();
        Class lClass = null;
        if (m_cIsFlatView) {
            lParams.put(Constants.FLAT, String.valueOf(m_cIsFlatView));
            lClass = RoleFilters.class;
        } else {
            lParams = null;
            lClass = Groups.class;
        }
        switch (mLessFromWhere) {
            case PotMacros.OBJ_BOARDCHOICES:
                if (m_cBoardChoice == null)
                    RequestManager.getInstance(this).placeUserRequest(Constants.SHARED +
                                    Constants.USERFILTER,
                            lClass, this, null, lParams, null, false);
                else {
                    RequestManager.getInstance(this).placeUserRequest(Constants.SHARED +
                                    Constants.GMR +
                                    Constants.USERFILTER,
                            lClass, this, null, lParams, null, false);
                }
                break;
            case PotMacros.OBJ_SYLLABI:
                if (m_cSyllabi == null)
                    RequestManager.getInstance(this).placeUserRequest(Constants.SHARED +
                                    Constants.BOARDCLASSES +
                                    m_cBoardChoice.getBoardclass().getId() +
                                    "/" +
                                    Constants.USERFILTER,
                            lClass, this, null, lParams, null, false);
                else {
                    RequestManager.getInstance(this).placeUserRequest(Constants.SHARED +
                                    Constants.BOARDCLASSES +
                                    m_cBoardChoice.getBoardclass().getId() +
                                    "/" +
                                    Constants.GS +
                                    Constants.USERFILTER,
                            lClass, this, null, lParams, null, false);
                }
                break;
            case PotMacros.OBJ_CHAPTERS:
                if (null == m_cChapters)
                    RequestManager.getInstance(this).placeUserRequest(Constants.SHARED +
                                    Constants.SYLLABI +
                                    m_cSyllabi.getId() +
                                    "/" +
                                    Constants.USERFILTER,
                            lClass, this, null, lParams, null, false);
                else
                    RequestManager.getInstance(this).placeUserRequest(Constants.SHARED +
                                    Constants.CHAPTERS +
                                    m_cChapters.getId() +
                                    "/" +
                                    Constants.USERFILTER,
                            lClass, this, null, lParams, null, false);

                break;
        }
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
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what) {
            case R.id.CHILD_LL:
                loop:
                if (m_cIsFlatView) {
                } else {
                    int groupPosition = (int) pObjMessage.obj;
                    boolean lbreakFlag = false;
                    for (Role lRole : ((Group) (m_cSelectionItems.get(groupPosition))).getRoles()) {
                        lRole.setSelected(false);
                        boolean lContFlag = false;
                        for (UsersShare lUsersShare : lRole.getUsersShares()) {
                            if (!lUsersShare.isSelected()) {
                                lbreakFlag = true;
                                lContFlag = true;
                            }
                        }
                        if (!lContFlag)
                            lRole.setSelected(true);
                    }
                    if (lbreakFlag) {
                        m_cExpandListAdapt.notifyDataSetChanged();
                        break loop;
                    }

                    for (Role lRole : ((Group) (m_cSelectionItems.get(groupPosition))).getRoles()) {
                        lRole.setSelected(true);
                    }
                    ((Group) (m_cSelectionItems.get(groupPosition))).setSelected(true);
                    m_cExpandView.collapseGroup(groupPosition);
                }
                break;
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.SHARED)) {
                    if (m_cIsFlatView) {
                        RoleFilters lRoleFilters = (RoleFilters) response;
                        if (null != lRoleFilters && lRoleFilters.getRoleFilters().size() > 0) {
                            m_clistDataHeader = new ArrayList<String>();
                            m_clistDataChildFlat = new HashMap<>();
                            int i = 0;
                            for (RoleFilter lRoleFilter : lRoleFilters.getRoleFilters()) {
                                m_clistDataHeader.add(lRoleFilter.getName());
                                m_clistDataChildFlat.put(lRoleFilter.getName(),
                                        lRoleFilter);
                                m_cSelectionItems.put(i, lRoleFilter);
                                i++;
                            }
                        } else {
                            if (m_cExpandListAdapt != null) {
                                m_clistDataHeader.clear();
                                m_clistDataChildFlat.clear();
                                m_cSelectionItems.clear();
                                m_cExpandView.setAdapter(new CustomExpandableListAdapterForLessonsFilter(this, m_clistDataHeader,
                                        m_clistDataChild, m_clistDataChildFlat, m_cSelectionItems, m_cIsFlatView, this));
                                m_cExpandView.invalidate();
                            }
                            hideDialog();
                            break;
                        }
                    } else {
                        Groups lGroups = (Groups) response;
                        if (null != lGroups && lGroups.getGroups().size() > 0) {
                            m_clistDataHeader = new ArrayList<String>();
                            m_clistDataChild = new HashMap<>();
                            int i = 0;
                            for (Group lGroup : lGroups.getGroups()) {
                                m_clistDataHeader.add(lGroup.getName() + "#" + lGroup.getComment());
                                m_clistDataChild.put(lGroup.getName() + "#" + lGroup.getComment(),
                                        lGroup);
                                m_cSelectionItems.put(i, lGroup);
                                i++;
                            }
                        } else {
                            if (m_cExpandListAdapt != null) {
                                m_clistDataHeader.clear();
                                m_clistDataChild.clear();
                                m_cSelectionItems.clear();
                                m_cExpandView.setAdapter(new CustomExpandableListAdapterForLessonsFilter(this, m_clistDataHeader,
                                        m_clistDataChild, m_clistDataChildFlat, m_cSelectionItems, m_cIsFlatView, this));
                                m_cExpandView.invalidate();
                            }
                            hideDialog();
                            break;
                        }
                    }
                    m_cExpandListAdapt = new CustomExpandableListAdapterForLessonsFilter(this, m_clistDataHeader,
                            m_clistDataChild, m_clistDataChildFlat, m_cSelectionItems, m_cIsFlatView, this);
                    m_cExpandView.setAdapter(m_cExpandListAdapt);

                    if (m_cIsFlatView) {
                        for (int i = 0; i < m_cExpandListAdapt.getGroupCount(); i++)
                            m_cExpandView.expandGroup(i);
                    } else {
                        m_cExpandView.expandGroup(0);
                    }
                    m_cExpandView.setOnGroupExpandListener(this);
                    hideDialog();
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.SHARE) ||
                        apiMethod.contains(Constants.LESSONS) ||
                        apiMethod.contains(Constants.CONNECTIONS)) {
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

    @Optional
    @OnClick({R.id.CANCEL_LESSON_TXT, R.id.SAVE_LESSON_TXT})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.CANCEL_LESSON_TXT:
                onBackPressed();
                break;
            case R.id.SAVE_LESSON_TXT:
                Intent lReturnIntent;
                if (m_cSelectionItems != null && m_cSelectionItems.size() > 0) {
                    StringBuffer lUserIds = new StringBuffer();
                    if (m_cIsFlatView) {
                        for (int i = 0; i < m_cSelectionItems.size(); i++) {
                            RoleFilter lRoleFilter = ((RoleFilter) m_cSelectionItems.get(i));
                            for (int j = 0 ; j < lRoleFilter.getUsers().size() ; j++) {
                                Users lUsers = lRoleFilter.getUsers().get(j);
                                if (lUsers.isSelected()) {
                                    lUserIds.append(lUsers.getId()).append(" ");
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i < m_cSelectionItems.size(); i++) {
                            Group lGroup = (Group) m_cSelectionItems.get(i);
                            for (Role lRole : lGroup.getRoles()) {
                                for (UsersShare lUsersShare : lRole.getUsersShares()) {
                                    if (lUsersShare.isSelected()) {
//                                        lUserIds.add(lUsersShare.getUsers().getId());
                                    }
                                }
                            }
                        }
                    }
                    lReturnIntent = new Intent();
                    lReturnIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                    lReturnIntent.putExtra(PotMacros.OBJ_LESSON_FILTER, lUserIds.toString().trim());
                    setResult(Activity.RESULT_OK, lReturnIntent);
                    onBackPressed();
                }
                break;
        }
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        if (!m_cIsFlatView) {
            if (groupPosition != previousItem)
                m_cExpandView.collapseGroup(previousItem);
            previousItem = groupPosition;
        }
    }

    @Override
    public void onInfoClick(int groupPosition, int pPostion, Role pRole,  Users pUsers, boolean pMode, View pView) {
        loop:
        if (pMode) {
            RoleFilter lRoleFilter = ((RoleFilter) m_cSelectionItems.get(groupPosition));
            for (Users lUsers : lRoleFilter.getUsers()) {
                if (!lUsers.isSelected()) {
                    ((RoleFilter) m_cSelectionItems.get(groupPosition)).setSelected(false);
                    m_cExpandListAdapt.notifyDataSetChanged();
                    break loop;
                }
            }
            ((RoleFilter) m_cSelectionItems.get(groupPosition)).setSelected(true);
            m_cExpandView.collapseGroup(groupPosition);
        } else {
            ArrayList<String> list = new ArrayList<>();
            if (pRole.getUsersShares().size() > 0) {
                for (UsersShare lUsersShare : pRole.getUsersShares())
                    list.add(lUsersShare.getUsers().getFirstName() + " " +
                            lUsersShare.getUsers().getLastName());
                displayMultiSpinnerDialog(pView.getId(), pRole.getName(),
                        list, groupPosition, pPostion);
            }
        }
    }

    @Override
    public void onInfoLongClick(int groupPosition, int pPostion, Role pRole,  Users pUsers, boolean pMode, View pView) {
        loop:
        if (pMode) {
        } else {
            for (Role lRole : ((Group) (m_cSelectionItems.get(groupPosition))).getRoles()) {
                for (UsersShare lUsersShare : lRole.getUsersShares()) {
                    if (!lUsersShare.isSelected()) {
                        break loop;
                    }
                }
            }
            for (Role lRole : ((Group) (m_cSelectionItems.get(groupPosition))).getRoles()) {
                lRole.setSelected(true);
            }
            ((Group) (m_cSelectionItems.get(groupPosition))).setSelected(true);
            m_cExpandView.collapseGroup(groupPosition);
        }
    }

    @Override
    public void onOptionClick(int pPostion,  Users pUsers, int pOptionId, View pView) {

    }

    public void displayMultiSpinnerDialog(final int pId, String pTitle, final List<String> pList,
                                          final int groupPosition, final int pPostion) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        final ArrayList<Integer> selectedPos = new ArrayList<>();
        BaseAdapter lAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return pList.size();
            }

            @Override
            public Object getItem(int i) {
                return pList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(final int i, View pview, ViewGroup viewGroup) {
                View lView = pview;
                final SpinnerHolder holder;

                if (lView == null) {
                    lView = LayoutInflater.from(ReceivedLessonsFilterScreen.this).inflate(R.layout.spinner_multi_dialog_item, null);
                    holder = new SpinnerHolder();
                    holder.textview = (TextView) lView.findViewById(R.id.text1);
                    holder.imageview = (ImageView) lView.findViewById(R.id.iv_tick);
                    lView.setTag(holder);
                } else {
                    holder = (SpinnerHolder) lView.getTag();
                }
                holder.textview.setText(pList.get(i));
                if (((Group) (m_cSelectionItems.get(groupPosition))).getRoles().get(pPostion).getUsersShares().get(i).isSelected()) {
                    holder.imageview.setImageResource(R.drawable.tick_blue);
                } else {
                    holder.imageview.setImageResource(R.drawable.tick_grey);
                }
                /*if (selectedPos.indexOf(i) > -1)
                    holder.imageview.setImageResource(R.drawable.tick_blue);
                else
                    holder.imageview.setImageResource(R.drawable.tick_grey);*/
                lView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*if (selectedPos.indexOf(i) > -1) {
                            holder.imageview.setImageResource(R.drawable.tick_grey);
                            selectedPos.remove(selectedPos.indexOf(i));
                        } else {
                            holder.imageview.setImageResource(R.drawable.tick_blue);
                            selectedPos.add(i);
                        }*/
                        if(((Group) (m_cSelectionItems.get(groupPosition))).getRoles().get(pPostion).getUsersShares().get(i).isSelected()){
                            ((Group) (m_cSelectionItems.get(groupPosition))).getRoles().get(pPostion).getUsersShares().get(i).setSelected(false);
                            holder.imageview.setImageResource(R.drawable.tick_grey);
                        }else {
                            ((Group) (m_cSelectionItems.get(groupPosition))).getRoles().get(pPostion).getUsersShares().get(i).setSelected(true);
                            holder.imageview.setImageResource(R.drawable.tick_blue);
                        }
                    }
                });
                return lView;
            }
        };

        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        lObjBuilder.setAdapter(lAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedPos.indexOf(which) > -1)
                    selectedPos.remove(selectedPos.indexOf(which));
                else
                    selectedPos.add(which);
            }
        });
        lObjBuilder.setNeutralButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Message lMsg = new Message();
                        lMsg.what = pId;
                        lMsg.obj = groupPosition;
                        m_cObjUIHandler.sendMessage(lMsg);
                        dialog.dismiss();
                    }
                });
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(false);
        m_cObjDialog.show();
    }

    public class SpinnerHolder {
        ImageView imageview;
        TextView textview;
    }
}
