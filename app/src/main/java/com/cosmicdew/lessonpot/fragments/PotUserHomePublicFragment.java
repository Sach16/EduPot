package com.cosmicdew.lessonpot.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.activities.LessonsScreen;
import com.cosmicdew.lessonpot.activities.PotUserLessonScreen;
import com.cosmicdew.lessonpot.activities.PotUserProfileScreen;
import com.cosmicdew.lessonpot.activities.ReceivedLessonsFilterScreen;
import com.cosmicdew.lessonpot.activities.ShareScreen;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForLessons;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.customviews.RecyclerViewHeader;
import com.cosmicdew.lessonpot.interfaces.RecyclerHomeListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Attachments;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.Follows;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.LessonViews;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.LessonsAll;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.models.UsersAll;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static android.app.Activity.RESULT_OK;

/**
 * Created by S.K. Pissay on 24/1/17.
 */

public class PotUserHomePublicFragment extends PotFragmentBaseClass implements RecyclerHomeListener {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    FrameLayout m_cRlMain;

    @Nullable
    @BindView(R.id.RECYC_HOME_CLASS_BOARDS)
    RecyclerView m_cRecycClasses;

    @Nullable
    @BindView(R.id.RECYCLERVIEW_HEADER)
    RecyclerViewHeader m_cHeaderView;

    @Nullable
    @BindView(R.id.FILTER_LIST_IMG)
    ImageView m_cFilterHeadImg;

    private int m_cPos;
    private String m_cKey;
    private Users m_cUser;
    private BoardChoices m_cBoardChoice;
    private Syllabi m_cSyllabi;
    private Chapters m_cChapters;

    private RemoveLessonReceiver mRemvReceiver;

    private String m_cGoOffline;

    private Dialog m_cObjDialog;
    private String mLessFromWhere;

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForLessons m_cRecycClassesAdapt;
    ArrayList<Lessons> m_cLessonsList;

    private String[] m_cUserIds;

    public PotUserHomePublicFragment() {
        super();
    }

    public static PotUserHomePublicFragment newInstance(int pPosition, String pKey, Users pUser, BoardChoices pBoardChoices,
                                                        Syllabi pSyllabi, Chapters pChapters, String pLessFromWhere, String pGoOffline) {
        PotUserHomePublicFragment lPotUserHomePublicFragment = new PotUserHomePublicFragment();

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("KEY", pKey);
        args.putString(PotMacros.OBJ_USER, (new Gson()).toJson(pUser));
        args.putString(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(pBoardChoices));
        args.putString(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(pSyllabi));
        args.putString(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(pChapters));
        args.putString(PotMacros.OBJ_LESSONFROM, pLessFromWhere);
        args.putString(PotMacros.GO_OFFLINE, pGoOffline);
        lPotUserHomePublicFragment.setArguments(args);

        return lPotUserHomePublicFragment;
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
        m_cObjMainView = inflater.inflate(R.layout.fragment_public_recyclerview, container, false);
        ButterKnife.bind(this, m_cObjMainView);

        m_cObjMainActivity.m_cObjFragmentBase = PotUserHomePublicFragment.this;

        mRemvReceiver = new RemoveLessonReceiver();
        LocalBroadcastManager.getInstance(m_cObjMainActivity).registerReceiver(mRemvReceiver,
                new IntentFilter(PotMacros.REMOVELESSON_REFRESH_CONSTANT_VIEWED));

        return m_cObjMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private class RemoveLessonReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != m_cRecycClassesAdapt) {
                m_cLessonsList.clear();
                m_cRecycClassesAdapt.notifyDataSetChanged();
                init();
            }
        }
    }


    private void init() {
        m_cPos = getArguments().getInt("Position", 0);
        m_cKey = getArguments().getString("KEY");
        m_cUser = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_USER), Users.class);
        m_cBoardChoice = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_BOARDCHOICES), BoardChoices.class);
        m_cSyllabi = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_SYLLABI), Syllabi.class);
        m_cChapters = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_CHAPTERS), Chapters.class);
        mLessFromWhere = getArguments().getString(PotMacros.OBJ_LESSONFROM);
        m_cGoOffline = getArguments().getString(PotMacros.GO_OFFLINE);
        m_cLessonsList = new ArrayList<>();
        m_cLayoutManager = new LinearLayoutManager(m_cObjMainActivity);
        m_cLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        m_cRecycClasses.setLayoutManager(m_cLayoutManager);
        m_cHeaderView.attachTo(m_cRecycClasses);
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

        //Calling board class api
        m_cObjMainActivity.displayProgressBar(-1, "");

        //TODO : check i think both if else in not required
        HashMap<String, String> lParams = new HashMap<>();
        StringBuffer lBuffer = new StringBuffer();
        if (null != m_cUserIds) {
            for (int i = 0; i < m_cUserIds.length; i++) {
                if (i == 0)
                    lBuffer.append(m_cUserIds[i]);
                else
                    lBuffer.append(",").append(m_cUserIds[i]);
            }
            lParams.put(Constants.USERS_TXT, lBuffer.toString());
        }
        placeUserRequest(Constants.PUBLIC + Constants.CHAPTERS +
                m_cChapters.getId() +
                "/" +
                Constants.LESSONS, LessonsAll.class, null, lParams, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Optional
    @OnClick({R.id.FILTER_LIST_IMG})
    public void onClick(View v) {
        final Intent lObjIntent;
        lObjIntent = new Intent(m_cObjMainActivity, ReceivedLessonsFilterScreen.class);
        switch (v.getId()) {
            case R.id.FILTER_LIST_IMG:
                if (null != m_cUserIds && m_cUserIds.length > 0) {
                    final PopupMenu pum = new PopupMenu(m_cObjMainActivity, v);
                    pum.inflate(R.menu.spinner_filter_menu);
                    pum.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_remove_filter:
                                    m_cUserIds = null;
                                    m_cLessonsList.clear();
                                    m_cRecycClassesAdapt.notifyDataSetChanged();
                                    m_cFilterHeadImg.setImageResource(R.drawable.filericon);
                                    init();
                                    pum.dismiss();
                                    break;
                                case R.id.action_edit_filter:
                                    lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_LESSON_PUBLIC_TAB);
                                    lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                                    lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoice));
                                    lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(m_cSyllabi));
                                    lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(m_cChapters));
                                    startActivityForResult(lObjIntent, PotMacros.LESSON_FILTER);
                                    pum.dismiss();
                                    break;
                            }
                            return true;
                        }
                    });
                    pum.show();
                } else {
                    lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_LESSON_PUBLIC_TAB);
                    lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                    lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoice));
                    lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(m_cSyllabi));
                    lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(m_cChapters));
                    startActivityForResult(lObjIntent, PotMacros.LESSON_FILTER);
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PotMacros.LESSON_FILTER:
                if (resultCode == RESULT_OK) {
                    m_cUserIds = data.getStringExtra(PotMacros.OBJ_LESSON_FILTER).split(" ");
                    if (null != m_cUserIds && m_cUserIds.length > 0)
                        m_cFilterHeadImg.setImageResource(R.drawable.filericon_blue);
                    else
                        m_cFilterHeadImg.setImageResource(R.drawable.filericon);
                    m_cLessonsList.clear();
                    m_cRecycClassesAdapt.notifyDataSetChanged();
                }
        }
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        Intent lObjIntent;
        switch (pObjMessage.what) {
            case PotMacros.ON_INFO_LONG_CLICK_VIEWED_LESSON:
                switch (pObjMessage.arg1) {
                    case R.id.action_share:
                        lObjIntent = new Intent(m_cObjMainActivity, ShareScreen.class);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, mLessFromWhere);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_MINE_TAB);
                        lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(((Lessons) pObjMessage.obj)));
                        startActivity(lObjIntent);
                        break;
                    case R.id.action_edit:
                        callLessonView((Lessons) pObjMessage.obj, PotMacros.OBJ_LESSON_EDIT);
                        break;
                    case R.id.action_add_syllabus:
                        Lessons lessons = ((Lessons) pObjMessage.obj);
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        placeUserRequest(Constants.BOARDCLASSES +
                                        lessons.getChapter().getSyllabus().getBoardclass().getId() +
                                        "/" +
                                        Constants.SYLLABI +
                                        lessons.getChapter().getSyllabus().getId() +
                                        "/"
                                , Syllabi.class, null, null, null, true);
                        break;
                    case R.id.action_save:
                        HashMap<String, String> lParams = new HashMap<>();
                        lParams.put(Constants.USERNAME, PotMacros.PUBLIC_USERNAME);
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        placeRequest(Constants.USERS,
                                UsersAll.class, new Object[]{R.id.action_save,
                                        (Lessons) pObjMessage.obj,
                                        ((Lessons) pObjMessage.obj).getOwner().getId()}, lParams, null, false);
                        break;
                    case R.id.action_post_to_public:
                    case R.id.action_extend_post_to_public:
                        checkSharesAndPosttoPublic(true, 2, (Lessons) pObjMessage.obj);
                        break;
                    case R.id.action_unpost_from_public:
                        checkSharesAndPosttoPublic(false, 2, ((Lessons) pObjMessage.obj));
                        break;
                    case R.id.action_post_to_connections:
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        placeUserRequest(Constants.LESSONS +
                                        ((Lessons) pObjMessage.obj).getId() +
                                        "/" +
                                        Constants.POST,
                                Lessons.class, getResources().getString(R.string.lesson_posted_successfully_txt), null, null, true);
                        break;
                    case R.id.action_extend_post_to_followers:
                    case R.id.action_post_to_followers_and_connections:
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        JSONObject lllJO = new JSONObject();
                        try {
                            lllJO.put(Constants.POST_TO_FOLLOWERS, true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        placeUserRequest(Constants.LESSONS +
                                        ((Lessons) pObjMessage.obj).getId() +
                                        "/" +
                                        Constants.POST,
                                Lessons.class, getResources().getString(R.string.lesson_posted_successfully_txt), null, lllJO.toString(), true);
                        break;
                    case R.id.action_unpost_from_connections:
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        placeUnivUserRequest(Constants.LESSONS +
                                        ((Lessons) pObjMessage.obj).getId() +
                                        "/" +
                                        Constants.POST,
                                Lessons.class, getResources().getString(R.string.lesson_unposted_successfully_txt), null, null, Request.Method.DELETE);
                        break;
                    case R.id.action_unpost_from_followers:
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        JSONObject llJO = new JSONObject();
                        try {
                            llJO.put(Constants.POST_TO_FOLLOWERS, false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        placeUserRequest(Constants.LESSONS +
                                        ((Lessons) pObjMessage.obj).getId() +
                                        "/" +
                                        Constants.POST,
                                Lessons.class, getResources().getString(R.string.lesson_unposted_successfully_txt), null, llJO.toString(), true);
                        break;
                    case R.id.action_report_spam:
                        checkSharesAndPosttoPublic(true, 3, ((Lessons) pObjMessage.obj));
                        break;
                    case R.id.action_view_creator_profile:
                        lObjIntent = new Intent(m_cObjMainActivity, PotUserProfileScreen.class);
                        lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(((Lessons) pObjMessage.obj).getOwner()));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(((Lessons) pObjMessage.obj)));
                        startActivity(lObjIntent);
                        break;
                    case R.id.action_include_in_online_received_tab:
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        HashMap<String, String> llParams = new HashMap<>();
                        llParams.put(Constants.USERNAME, PotMacros.PUBLIC_USERNAME);
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        placeRequest(Constants.USERS,
                                UsersAll.class, new Object[]{R.id.action_include_in_online_received_tab,
                                        (Lessons) pObjMessage.obj,
                                        ((Lessons) pObjMessage.obj).getOwner().getId()}, llParams, null, false);
                        break;
                }
                break;
            case PotMacros.ACTION_POST_TO_PUBLIC:
                Object[] lObjectPub = (Object[]) pObjMessage.obj;
                Lessons llessons = (Lessons) lObjectPub[1];
                if ((boolean) lObjectPub[0]) {
                    callLessonFlags(true, 0, llessons);
                    callLessonFlags(true, 2, llessons);
                }
                break;
            default:
                break;
        }
    }

    private void checkSharesAndPosttoPublic(boolean isTrue, int pCase, Lessons pLessons) {
        if (pLessons.getSharable()) {
            callLessonFlags(isTrue, pCase, pLessons);
        } else {
            displayYesOrNoCustAlert(PotMacros.ACTION_POST_TO_PUBLIC,
                    getResources().getString(R.string.action_post_to_public),
                    getResources().getString(R.string.lesson_sharable_permission_txt),
                    pLessons);
        }
    }

    private void callLessonFlags(boolean isTrue, int pCase, Lessons pLessons) {
        String lStrObj = null;
        JSONObject lJO = new JSONObject();
        try {
            switch (pCase) {
                case 0:
                    lJO.put(Constants.SHARABLE, isTrue);
                    lJO.put(Constants.NAME, pLessons.getName());
                    lStrObj = Constants.SHARABLE;
                    break;
                case 1:
                    lJO.put(Constants.OFFLINEABLE, isTrue);
                    lJO.put(Constants.NAME, pLessons.getName());
                    lStrObj = Constants.OFFLINEABLE;
                    break;
                case 2:
                    lJO.put(Constants.PUBLICABLE, isTrue);
                    lJO.put(Constants.NAME, pLessons.getName());
                    lStrObj = Constants.PUBLICABLE;
                    break;
                case 3:
                    lJO.put(Constants.IS_SPAM, isTrue);
                    lJO.put(Constants.NAME, pLessons.getName());
                    lStrObj = Constants.IS_SPAM;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        placeUnivUserRequest(Constants.CHAPTERS +
                pLessons.getChapter().getId() +
                "/" +
                Constants.LESSONS +
                pLessons.getId() +
                "/", Lessons.class, new Object[]{lStrObj, isTrue}, null, lJO.toString(), Request.Method.PATCH);
    }

    private void callDeleteLessonApi(Lessons pLessons) {
        m_cObjMainActivity.displayProgressBar(-1, "");
        placeDeleteRequest(Constants.CHAPTERS +
                        pLessons.getChapter().getId() +
                        "/" +
                        Constants.LESSONS +
                        pLessons.getId() +
                        "/",
                Attachments.class, null, null, null, true);
    }

    private void refreshList() {
        m_cLessonsList.clear();
        m_cRecycClassesAdapt.notifyDataSetChanged();
        init();
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.PUBLICSHARE)) {
                    if (response == null) {
                        m_cObjMainActivity.displayToast(getResources().getString(R.string.lesson_successfully_included_txt));
                        m_cObjMainActivity.hideDialog();
                    }
                } else if (apiMethod.contains(Constants.USERS)) {
                    Object[] lObjects = (Object[]) refObj;
                    Lessons lessons = (Lessons) lObjects[1];
                    int lId = (int) lObjects[2];
                    switch ((int) lObjects[0]) {
                        case R.id.action_save:
                            m_cObjMainActivity.hideDialog();
                            UsersAll lUsers = (UsersAll) response;
                            ((PotUserLessonScreen) m_cObjMainActivity).checkAndDownloadAttachments(lessons, lId,
                                    lUsers.getUsers().get(0).getFirstName() + " " + lUsers.getUsers().get(0).getLastName());
                            break;
                        case R.id.action_include_in_online_received_tab:
                            m_cObjMainActivity.hideDialog();
                            m_cObjMainActivity.displayProgressBar(-1, "");
                            UsersAll llUsers = (UsersAll) response;
                            JSONObject lllJO = new JSONObject();
                            try {
                                lllJO.put(Constants.PUBLIC_USER, String.valueOf(llUsers.getUsers().get(0).getId()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            placeUserRequest(Constants.LESSONS +
                                            lessons.getId() +
                                            "/" +
                                            Constants.PUBLICSHARE,
                                    Lessons.class, null, null, lllJO.toString(), true);
                            break;
                    }
                } else if (apiMethod.contains(Constants.FOLLOWERS)) {
                    Follows lFollows = (Follows) response;
                    m_cObjMainActivity.displayToast(String.format("%s %s", getResources().getString(R.string.following_success_txt),
                            lFollows.getToUser().getFirstName() + " " + lFollows.getToUser().getLastName()));
                    m_cObjMainActivity.hideDialog();
                } else if (apiMethod.contains(Constants.SOURCES)) {
                    if (response == null) {
                        m_cObjMainActivity.hideDialog();
                        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(m_cObjMainActivity);
                        Intent i = new Intent(PotMacros.REMOVELESSON_REFRESH_CONSTANT_RECEIVED);
                        lbm.sendBroadcast(i);
                        m_cLessonsList.clear();
                        m_cRecycClassesAdapt.notifyDataSetChanged();
                        init();
                    }
                } else if (response instanceof Syllabi) {
                    Syllabi lSyllabi = (Syllabi) response;
                    if (lSyllabi != null) {
                        m_cObjMainActivity.hideDialog();
                        m_cObjMainActivity.displayToast(getResources().getString(R.string.syllabus_added_successfully_txt));
                    }
                } else if (apiMethod.contains(Constants.POST)) {
                    if (response == null) {
                        m_cObjMainActivity.hideDialog();
                        m_cObjMainActivity.displayToast((String) refObj);
                        refreshList();
                    }
                } else if (apiMethod.contains(Constants.VIEWS)) {
                    LessonViews lessonViews = (LessonViews) response;
                    if (lessonViews != null) {
                        Intent lObjIntent = new Intent(m_cObjMainActivity, LessonsScreen.class);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON_TYPE, (Integer) refObj);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, mLessFromWhere);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_PUBLIC_TAB);
                        lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                        lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoice));
                        lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(m_cSyllabi));
                        lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(m_cChapters));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(lessonViews.getLesson()));
                        startActivity(lObjIntent);
                        m_cObjMainActivity.hideDialog();
                    }
                } else if (apiMethod.contains(Constants.LESSONS) && refObj != null) {
                    Lessons lLessons = (Lessons) response;
                    Object[] lObjects = (Object[]) refObj;
                    if (lLessons != null) {
                        switch ((String) lObjects[0]) {
                            case Constants.SHARABLE:
                                if ((boolean) lObjects[1])
                                    m_cObjMainActivity.displayToast(getResources().getString(R.string.shares_enabled_txt));
                                else
                                    m_cObjMainActivity.displayToast(getResources().getString(R.string.shares_disabled_txt));
                                break;
                            case Constants.OFFLINEABLE:
                                if ((boolean) lObjects[1])
                                    m_cObjMainActivity.displayToast(getResources().getString(R.string.offline_save_enabled_txt));
                                else
                                    m_cObjMainActivity.displayToast(getResources().getString(R.string.offline_save_disabled_txt));
                                break;
                            case Constants.PUBLICABLE:
                                if ((boolean) lObjects[1])
                                    m_cObjMainActivity.displayToast(getResources().getString(R.string.posted_to_public_txt));
                                else
                                    m_cObjMainActivity.displayToast(getResources().getString(R.string.unpost_from_public_txt));
                                break;
                            case Constants.IS_SPAM:
                                if ((boolean) lObjects[1])
                                    m_cObjMainActivity.displayToast(getResources().getString(R.string.marked_as_spam_txt));
                                else
                                    m_cObjMainActivity.displayToast(getResources().getString(R.string.removed_spam_txt));
                                break;
                        }
                    }
                    m_cObjMainActivity.hideDialog();
                    m_cLessonsList.clear();
                    m_cRecycClassesAdapt.notifyDataSetChanged();
                    init();
                } else if (apiMethod.contains(Constants.LESSONS)) {
                    if (response == null) {
                        m_cObjMainActivity.hideDialog();
                        init();
                    } else {
                        LessonsAll lLessonsAll = (LessonsAll) response;
                        if (lLessonsAll != null && lLessonsAll.getLessons().size() > 0) {
                            for (Lessons lessons : lLessonsAll.getLessons()) {
                                if (lessons.getChapter().getIsGeneric())
                                    ((PotUserLessonScreen) m_cObjMainActivity).setLessonsGen(lessons);
                                m_cLessonsList.add(lessons);
                            }
                            if (null != m_cLessonsList && m_cLessonsList.size() > 0) {
                                m_cRecycClassesAdapt = new CustomRecyclerAdapterForLessons(m_cObjMainActivity, m_cUser, m_cBoardChoice,
                                        m_cSyllabi, m_cChapters, m_cLessonsList, null, null, this);
                                m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
                            }
                        } else {
                            if (m_cLessonsList.size() >= 0) {
                                if (null != m_cRecycClassesAdapt) {
                                    m_cLessonsList.clear();
                                    m_cRecycClasses.setAdapter(new CustomRecyclerAdapterForLessons(m_cObjMainActivity, m_cUser, m_cBoardChoice,
                                            m_cSyllabi, m_cChapters, m_cLessonsList, null, null, this));
                                    m_cRecycClasses.invalidate();
                                }
                            }
                        }
                        m_cObjMainActivity.hideDialog();
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
            default:
                if (apiMethod.contains(Constants.PUBLICSHARE) ||
                        apiMethod.contains(Constants.USERS) ||
                        apiMethod.contains(Constants.FOLLOWERS) ||
                        apiMethod.contains(Constants.SOURCES) ||
                        apiMethod.contains(Constants.BOARDCLASSES) ||
                        apiMethod.contains(Constants.POST) ||
                        apiMethod.contains(Constants.VIEWS) ||
                        apiMethod.contains(Constants.CHAPTERS) ||
                        apiMethod.contains(Constants.LESSONS)) {
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
    public void onInfoClick(int pPostion, BoardChoices pBoardChoices, Syllabi pSyllabi, Chapters pChapters, Lessons pLessons, LessonShares pLessonShares, LessonViews pLessonViews, View pView) {
        callLessonView(pLessons, PotMacros.OBJ_LESSON_VIEW);
    }

    private void callLessonView(Lessons pLessons, int pLessonType) {
        m_cObjMainActivity.displayProgressBar(-1, "");
        JSONObject lJO = new JSONObject();
        try {
            lJO.put(Constants.SOURCE, pLessons.getOwner().getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        placeUserRequest(Constants.LESSONS +
                        pLessons.getId() +
                        "/" +
                        Constants.VIEWS,
                LessonViews.class, pLessonType, null, lJO.toString(), true);
    }

    @Override
    public void onInfoLongClick(int pPostion, BoardChoices pBoardChoices, Syllabi pSyllabi, Chapters pChapters, Lessons pLessons, LessonShares pLessonShares, LessonViews pLessonViews, View pView) {
        Object[] lObjects =
                PotMacros.getLessonSettingsOptionsList(m_cObjMainActivity, pLessons, pLessonShares, pLessonViews, PotMacros.OBJ_LESSON_PUBLIC_TAB, m_cUser);
        displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_VIEWED_LESSON, pLessons.getName(),
                (List<String>) lObjects[0],
                (List<Integer>) lObjects[1],
                pLessons);

        /*if (m_cUser.getId().equals(pLessons.getOwner().getId()))
            displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_VIEWED, pLessons.getName(),
                    Arrays.asList(getResources().getString(R.string.action_post_to_connections),
                            getResources().getString(R.string.action_share),
                            getResources().getString(R.string.action_edit),
                            getResources().getString(R.string.action_delete_lesson),
                            getResources().getString(R.string.action_add_syllabus),
                            getResources().getString(R.string.action_save)),
                    Arrays.asList(R.id.action_post_to_connections,
                            R.id.action_share,
                            R.id.action_edit,
                            R.id.action_delete_lesson,
                            R.id.action_add_syllabus,
                            R.id.action_save),
                    pLessons);
        else
            displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_VIEWED_LESSON, pLessons.getName(),
                    m_cUser.getRole().equalsIgnoreCase(PotMacros.ROLE_STUDENT) ?
                            Arrays.asList(getResources().getString(R.string.action_remove),
                                    getResources().getString(R.string.action_add_syllabus),
                                    getResources().getString(R.string.action_save),
                                    getResources().getString(R.string.action_follow))
                            :
                            Arrays.asList(getResources().getString(R.string.action_post_to_connections),
                                    getResources().getString(R.string.action_share),
                                    getResources().getString(R.string.action_remove),
                                    getResources().getString(R.string.action_add_syllabus),
                                    getResources().getString(R.string.action_save),
                                    getResources().getString(R.string.action_follow)),
                    m_cUser.getRole().equalsIgnoreCase(PotMacros.ROLE_STUDENT) ?
                            Arrays.asList(R.id.action_remove,
                                    R.id.action_add_syllabus,
                                    R.id.action_save,
                                    R.id.action_view_creator_profile)
                            :
                            Arrays.asList(R.id.action_post_to_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_add_syllabus,
                                    R.id.action_save,
                                    R.id.action_view_creator_profile),
                    pLessons);*/
    }

    @Override
    public void onSelectionClicked(int pPostion, BoardChoices pBoardChoices, Syllabi pSyllabi, Chapters pChapters, Lessons pLessons, LessonShares pLessonShares, LessonViews pLessonViews, View pView) {

    }

    public void displaySpinnerDialog(final int pId, String pTitle, final List<String> pList,
                                     final List<Integer> pListIds, final Object pObj) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(m_cObjMainActivity);
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
            public View getView(int i, View view, ViewGroup viewGroup) {
                View lView = LayoutInflater.from(m_cObjMainActivity).inflate(R.layout.spinner_dialog_item, null);
                TextView ltextview = (TextView) lView.findViewById(R.id.text1);
                ltextview.setText(pList.get(i));
                return lView;
            }
        };

        View lView = LayoutInflater.from(m_cObjMainActivity).inflate(R.layout.spinner_header, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        lObjBuilder.setAdapter(lAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message lMsg = new Message();
                lMsg.what = pId;
                lMsg.arg1 = pListIds.get(which);
                lMsg.obj = pObj;
                m_cObjUIHandler.sendMessage(lMsg);
                m_cObjDialog.dismiss();
            }
        });
        /*lObjBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(true);
        m_cObjDialog.show();
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
