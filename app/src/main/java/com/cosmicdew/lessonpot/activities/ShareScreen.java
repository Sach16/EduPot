package com.cosmicdew.lessonpot.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.cosmicdew.lessonpot.adapters.CustomExpandableListAdapterForLessonsShare;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.interfaces.RecyclerShareListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Group;
import com.cosmicdew.lessonpot.models.Groups;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.LessonViews;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.Role;
import com.cosmicdew.lessonpot.models.Roles;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.models.UsersShare;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 6/12/16.
 */

public class ShareScreen extends PotBaseActivity implements RecyclerShareListener, ExpandableListView.OnGroupExpandListener {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    RelativeLayout m_cRlMain;

    @Nullable
    @BindView(R.id.EXPAN_LIST_VIEW)
    ExpandableListView m_cExpandView;

    private int previousItem = 0;

    private int m_cPos;
    private String m_cKey;
    private Users m_cUser;
    private Lessons m_cLessons;
    private LessonShares m_cLessonShares;
    private LessonViews m_cLessonViews;
    private String m_cSelectionType;

    private String mLessFromWhere;
    private String mLessFromWhereWchTab;

    private View mHeaderView;

    private boolean m_cIsFlatView = true;

    List<String> m_clistDataHeader;
    HashMap<String, Group> m_clistDataChild;
    HashMap<String, Role> m_clistDataChildFlat;
    HashMap<Integer, Object> m_cSelectionItems;
    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomExpandableListAdapterForLessonsShare m_cExpandListAdapt;
    ArrayList<BoardChoices> m_cBoardClassList;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.fragment_expandable_share_list);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(getResources().getString(R.string.share_lesson_txt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addHeader();

    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void addHeader() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mHeaderView = inflater.inflate(R.layout.header_filter_layout, null);
        TextView lHeaderText = (TextView) mHeaderView.findViewById(R.id.HEADER_DET_TXT);
        lHeaderText.setText(getResources().getString(R.string.tap_and_hold_txt_det));
        final TextView lHeadClickTxt = (TextView) mHeaderView.findViewById(R.id.FILTER_LIST_TXT);
        lHeadClickTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_cIsFlatView)
                    lHeadClickTxt.setText(getResources().getString(R.string.show_flat_txt));
                else
                    lHeadClickTxt.setText(getResources().getString(R.string.show_groups_txt));
                m_cIsFlatView = !m_cIsFlatView;
                init();
            }
        });
        m_cExpandView.setDivider(null);
        m_cExpandView.addHeaderView(mHeaderView);
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
                            if (m_cIsFlatView){
                                if (((Role)m_cSelectionItems.get(groupPosition)).getShares().equals(1))
                                    lblImageViewTick.setImageDrawable(getResources().getDrawable(R.drawable.tick_shared));
                                else
                                    lblImageViewTick.setImageDrawable(null);
                            }else {
                                if (((Group)m_cSelectionItems.get(groupPosition)).getShares().equals(1))
                                    lblImageViewTick.setImageDrawable(getResources().getDrawable(R.drawable.tick_shared));
                                else
                                    lblImageViewTick.setImageDrawable(null);
                            }
                            m_cExpandListAdapt.notifyDataSetChanged();
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
                    if (((Role)m_cSelectionItems.get(groupPosition)).isSelected()) {
//                        parent.collapseGroup(groupPosition);
                        return false;
                    } else {
                        return false;
                    }
                }else {
                    if (((Group)m_cSelectionItems.get(groupPosition)).isSelected()) {
//                        parent.collapseGroup(groupPosition);
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
        if (o instanceof Group) {
            Group lGroup = (Group) o;
            if (null != lGroup) {
                lRetVal = !lGroup.isSelected();
                lGroup.setSelected(lRetVal);
                for (Role lRole : lGroup.getRoles()) {
                    lRole.setSelected(lRetVal);
                    for (UsersShare lUsersShare : lRole.getUsersShares())
                        lUsersShare.setSelected(lRetVal);
                }
            }
        } else {
            Role lRole = (Role) o;
            if (null != lRole) {
                lRetVal = !lRole.isSelected();
                lRole.setSelected(lRetVal);
                for (UsersShare lUsersShare : lRole.getUsersShares())
                    lUsersShare.setSelected(lRetVal);
            }
        }
        return lRetVal;
    }

    private Object checkAndUpdateReShares(Object o) {
        Object lRetObj = null;
        if (o instanceof Groups) {
            Groups lGroups = (Groups) o;
            if (null != lGroups) {
                for (Group lGroup : lGroups.getGroups()) {
                    lGroup.setShares(0);
                    boolean lBreakFlag = false;
                    for (Role lRole : lGroup.getRoles()) {
                        lRole.setShares(0);
                        boolean lContFlag = false;
                        for (UsersShare lUsersShare : lRole.getUsersShares()) {
                            if (lUsersShare.getShares().equals(0)) {
                                lBreakFlag = true;
                                lContFlag = true;
                            }
                        }
                        if (!lContFlag)
                            lRole.setShares(1);
                    }
                    if (!lBreakFlag)
                        lGroup.setShares(1);
                }
            }
            lRetObj = lGroups;
        } else {
            Roles lRoles = (Roles) o;
            for (Role lRole : lRoles.getRoles()) {
                lRole.setShares(0);
                boolean lContFlag = false;
                for (UsersShare lUsersShare : lRole.getUsersShares()) {
                    if (lUsersShare.getShares().equals(0)) {
                        lContFlag = true;
                    }
                }
                if (!lContFlag)
                    lRole.setShares(1);
            }
            lRetObj = lRoles;
        }
        return lRetObj;
    }

    private void init() {
        m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);
        m_cLessons = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_LESSON), Lessons.class);
        m_cLessonShares = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_LESSONSHARES), LessonShares.class);
        m_cLessonViews = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_LESSONVIEWS), LessonViews.class);
        mLessFromWhere = getIntent().getStringExtra(PotMacros.OBJ_LESSONFROM);
        mLessFromWhereWchTab = getIntent().getStringExtra(PotMacros.OBJ_LESSONFROMWCHTAB);


        m_cSelectionType = getIntent().getStringExtra(PotMacros.OBJ_SELECTIONTYPE);

        m_cBoardClassList = new ArrayList<>();
        m_cSelectionItems = new HashMap<>();
        m_cLayoutManager = new LinearLayoutManager(this);
        m_cLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        checkWhereToMap();

        //Call api here
        displayProgressBar(-1, "");
        HashMap<String, String> lParams = new HashMap<>();
        Class lClass = null;
        if (m_cIsFlatView) {
            lParams.put(Constants.FLAT, String.valueOf(m_cIsFlatView));
            lClass = Roles.class;
        } else {
            lParams = null;
            lClass = Groups.class;
        }
        int lSourceId = -1;
        switch (mLessFromWhereWchTab){
            case PotMacros.OBJ_LESSON_VIEWED_TAB:
                lSourceId = m_cLessonViews.getSource().getId();
                break;
            case PotMacros.OBJ_LESSON_RECEIVED_TAB:
                lSourceId = m_cLessonShares.getFromUser().getId();
                break;
            case PotMacros.OBJ_LESSON_MINE_TAB:
                lSourceId = m_cLessons.getOwner().getId();
                break;
        }
        switch (mLessFromWhere) {
            case PotMacros.OBJ_BOARDCHOICES:
                RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                                m_cLessons.getId() +
                                "/" +
                                Constants.SOURCES +
                                lSourceId +
                                "/" +
                                Constants.AUDIENCE +
                                Constants.ALL,
                        lClass, this, null, lParams, null, false);
                break;
            case PotMacros.OBJ_SYLLABI:
                RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                                m_cLessons.getId() +
                                "/" +
                                Constants.SOURCES +
                                lSourceId +
                                "/" +
                                Constants.AUDIENCE +
                                Constants.BOARDCLASS,
                        lClass, this, null, lParams, null, false);
                break;
            default:
                RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                                m_cLessons.getId() +
                                "/" +
                                Constants.SOURCES +
                                lSourceId +
                                "/" +
                                Constants.AUDIENCE +
                                Constants.SYLLABUS,
                        lClass, this, null, lParams, null, false);
                break;
        }

    }

    private void checkWhereToMap() {
        if (m_cLessons.getChapter().getSyllabus().getBoardclass().getIsGeneric()) {
            mLessFromWhere = PotMacros.OBJ_BOARDCHOICES;
        } else if (m_cLessons.getChapter().getSyllabus().getIsGeneric()) {
            mLessFromWhere = PotMacros.OBJ_SYLLABI;
        } else if (m_cLessons.getChapter().getIsGeneric()) {
            mLessFromWhere = PotMacros.OBJ_CHAPTERS;
        } else {
            mLessFromWhere = PotMacros.OBJ_LESSON;
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
                        ((Group) (m_cSelectionItems.get(groupPosition))).setSelected(false);
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
                if (apiMethod.contains(Constants.SHARE)) {
                    LessonShares lessonShares = (LessonShares) response;
                    if (null != lessonShares) {
                        hideDialog();
                        displayToast(getResources().getString(R.string.lesson_shared_successfully_txt));
                        onBackPressed();
                    }
                } else if (apiMethod.contains(Constants.LESSONS)) {
                    if (m_cIsFlatView) {
                        Roles lRoles = (Roles) response;
                        if (null != lRoles && lRoles.getRoles().size() > 0) {
                            lRoles = (Roles) checkAndUpdateReShares(lRoles);
                            m_clistDataHeader = new ArrayList<String>();
                            m_clistDataChildFlat = new HashMap<>();
                            int i = 0;
                            for (Role lRole : lRoles.getRoles()) {
                                m_clistDataHeader.add(lRole.getName());
                                m_clistDataChildFlat.put(lRole.getName(),
                                        lRole);
                                m_cSelectionItems.put(i, lRole);
                                i++;
                            }
                        } else {
                            if (m_cExpandListAdapt != null) {
                                m_clistDataHeader.clear();
                                m_clistDataChildFlat.clear();
                                m_cSelectionItems.clear();
                                m_cExpandView.setAdapter(new CustomExpandableListAdapterForLessonsShare(this, m_clistDataHeader,
                                        m_clistDataChild, m_clistDataChildFlat, m_cSelectionItems, m_cIsFlatView, this));
                                m_cExpandView.invalidate();
                                m_cExpandView.removeHeaderView(mHeaderView);
                            }
                            hideDialog();
                            break;
                        }
                    } else {
                        Groups lGroups = (Groups) response;
                        if (null != lGroups && lGroups.getGroups().size() > 0) {
                            lGroups = (Groups) checkAndUpdateReShares(lGroups);
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
                                m_cExpandView.setAdapter(new CustomExpandableListAdapterForLessonsShare(this, m_clistDataHeader,
                                        m_clistDataChild, m_clistDataChildFlat, m_cSelectionItems, m_cIsFlatView, this));
                                m_cExpandView.invalidate();
                                m_cExpandView.removeHeaderView(mHeaderView);
                            }
                            hideDialog();
                            break;
                        }
                    }
                    m_cExpandListAdapt = new CustomExpandableListAdapterForLessonsShare(this, m_clistDataHeader,
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
                } else if (apiMethod.contains(Constants.CONNECTIONS)) {
                    if (response == null) {
                        displayToast(getResources().getString(R.string.connection_deleted_txt));
                    }
                    init();
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
                //TODO : call lesson share here
                if (m_cSelectionItems != null && m_cSelectionItems.size() > 0) {
                    List<Integer> lUserIds = new ArrayList();
                    if (m_cIsFlatView) {
                        for (int i = 0; i < m_cSelectionItems.size(); i++) {
                            Role lRole = ((Role) m_cSelectionItems.get(i));
                            for (UsersShare lUsersShare : lRole.getUsersShares()) {
                                if (lUsersShare.isSelected()) {
                                    lUserIds.add(lUsersShare.getUsers().getId());
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i < m_cSelectionItems.size(); i++) {
                            Group lGroup = (Group) m_cSelectionItems.get(i);
                            for (Role lRole : lGroup.getRoles()) {
                                for (UsersShare lUsersShare : lRole.getUsersShares()) {
                                    if (lUsersShare.isSelected()) {
                                        lUserIds.add(lUsersShare.getUsers().getId());
                                    }
                                }
                            }
                        }
                    }
                    if (lUserIds != null && lUserIds.size() > 0) {
                        JSONObject lJO = new JSONObject();
                        try {
                            JSONArray lArray = new JSONArray();
                            for (int i = 0; i < lUserIds.size(); ++i) {
                                lArray.put(lUserIds.get(i));
                            }
                            lJO.put(Constants.USERS_TXT, lArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        displayProgressBar(-1, "");
                        RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                                        m_cLessons.getId() +
                                        "/" +
                                        Constants.SHARE,
                                LessonShares.class, this, null, null, lJO.toString(), true);
                    }
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
    public void onInfoClick(int groupPosition, int pPostion, Role pRole, UsersShare pUsersShare, boolean pMode, View pView) {
        loop:
        if (pMode) {
            Role lRole = ((Role) m_cSelectionItems.get(groupPosition));
            for (UsersShare lUsersShare : lRole.getUsersShares()) {
                if (!lUsersShare.isSelected()) {
                    ((Role) m_cSelectionItems.get(groupPosition)).setSelected(false);
                    m_cExpandListAdapt.notifyDataSetChanged();
                    break loop;
                }
            }
            ((Role) m_cSelectionItems.get(groupPosition)).setSelected(true);
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
    public void onInfoLongClick(int groupPosition, int pPostion, Role pRole, UsersShare pUsersShare, boolean pMode, View pView) {
        loop:
        if (pMode) {
        } else {
            for (Role lRole : ((Group) (m_cSelectionItems.get(groupPosition))).getRoles()) {
                for (UsersShare lUsersShare : lRole.getUsersShares()) {
                    if (!lUsersShare.isSelected()) {
                        ((Group) (m_cSelectionItems.get(groupPosition))).setSelected(false);
                        m_cExpandListAdapt.notifyDataSetChanged();
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
    public void onOptionClick(int pPostion, UsersShare pUsersShare, int pOptionId, View pView) {

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
                    lView = LayoutInflater.from(ShareScreen.this).inflate(R.layout.spinner_multi_dialog_item, null);
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
                    if (((Group) (m_cSelectionItems.get(groupPosition))).getRoles().get(pPostion).getUsersShares().get(i).getShares().equals(1))
                        holder.imageview.setImageResource(R.drawable.tick_shared);
                    else
                        holder.imageview.setImageResource(R.drawable.tick_grey);
                }
                lView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(((Group) (m_cSelectionItems.get(groupPosition))).getRoles().get(pPostion).getUsersShares().get(i).isSelected()) {
                            ((Group) (m_cSelectionItems.get(groupPosition))).getRoles().get(pPostion).getUsersShares().get(i).setSelected(false);
                            if (((Group) (m_cSelectionItems.get(groupPosition))).getRoles().get(pPostion).getUsersShares().get(i).getShares().equals(1))
                                holder.imageview.setImageResource(R.drawable.tick_shared);
                            else
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