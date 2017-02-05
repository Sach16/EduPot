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
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.activities.LessonsScreen;
import com.cosmicdew.lessonpot.activities.PotUserLessonScreen;
import com.cosmicdew.lessonpot.activities.ReceivedLessonsFilterScreen;
import com.cosmicdew.lessonpot.activities.ShareScreen;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForLessonsReceived;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.customviews.RecyclerViewHeader;
import com.cosmicdew.lessonpot.interfaces.RecyclerHomeListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Attachments;
import com.cosmicdew.lessonpot.models.Board;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.BoardClass;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.Length;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.LessonSharesAll;
import com.cosmicdew.lessonpot.models.LessonViews;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.LessonsTable;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static android.app.Activity.RESULT_OK;

/**
 * Created by S.K. Pissay on 17/10/16.
 */

public class PotUserHomeReceivedFragment extends PotFragmentBaseClass implements RecyclerHomeListener {

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
    private String[] m_cUserIds;

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForLessonsReceived m_cRecycClassesAdapt;
    ArrayList<Lessons> m_cLessonsList;

    public PotUserHomeReceivedFragment() {
        super();
    }

    public static PotUserHomeReceivedFragment newInstance(int pPosition, String pKey, Users pUser, BoardChoices pBoardChoices,
                                                        Syllabi pSyllabi, Chapters pChapters, String pLessFromWhere, String pGoOffline) {
        PotUserHomeReceivedFragment lPotUserHomeReceivedFragment = new PotUserHomeReceivedFragment();

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("KEY", pKey);
        args.putString(PotMacros.OBJ_USER, (new Gson()).toJson(pUser));
        args.putString(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(pBoardChoices));
        args.putString(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(pSyllabi));
        args.putString(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(pChapters));
        args.putString(PotMacros.OBJ_LESSONFROM, pLessFromWhere);
        args.putString(PotMacros.GO_OFFLINE, pGoOffline);
        lPotUserHomeReceivedFragment.setArguments(args);

        return lPotUserHomeReceivedFragment;
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
        m_cObjMainView = inflater.inflate(R.layout.fragment_cust_recyclerview, container, false);
        ButterKnife.bind(this, m_cObjMainView);

        m_cObjMainActivity.m_cObjFragmentBase = PotUserHomeReceivedFragment.this;

        mRemvReceiver = new RemoveLessonReceiver();
        LocalBroadcastManager.getInstance(m_cObjMainActivity).registerReceiver(mRemvReceiver,
                new IntentFilter(PotMacros.REMOVELESSON_REFRESH_CONSTANT_RECEIVED));

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

        if (null != m_cGoOffline){
            initOffline();
        }else {
            //Calling board class apiR
            m_cObjMainActivity.displayProgressBar(-1, "");
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

            switch (mLessFromWhere) {
                case PotMacros.OBJ_BOARDCHOICES:
                    placeUserRequest(Constants.SHARED + Constants.GMR + Constants.LESSONS, LessonSharesAll.class, null, lParams, null, false);

                    break;
                case PotMacros.OBJ_SYLLABI:
                    placeUserRequest(Constants.SHARED + Constants.BOARDCLASSES +
                            m_cBoardChoice.getBoardclass().getId() +
                            "/" +
                            Constants.GS +
                            Constants.LESSONS, LessonSharesAll.class, null, lParams, null, false);

                    break;
                case PotMacros.OBJ_CHAPTERS:
                    if (null != m_cUserIds && m_cUserIds.length == 1)
                        lParams.put(Constants.ORDERING, Constants.LESSON__POSITION);
                    placeUserRequest(Constants.SHARED + Constants.CHAPTERS +
                            m_cChapters.getId() +
                            "/" +
                            Constants.LESSONS, LessonSharesAll.class, null, lParams, null, false);
                    break;
            }
        }
    }

    private void initOffline() {
        List<LessonsTable> lessonsTableAll = LessonsTable.listAll(LessonsTable.class);
        /*"sharer_id != ? and sharer_id != -1 and user_id = ? and board_class = ? and syllabi_name = ? and chapter_name = ?"*/
        List<LessonsTable> lessonsTableList = LessonsTable.findWithQuery(LessonsTable.class,
                "select * from lessons_table where sharer_id != ? and sharer_id != -1 and user_id = ? and board_class like '%'||?||'%' and syllabi_name = ? and chapter_name = ?",
                String.valueOf(m_cUser.getId()),
                String.valueOf(m_cUser.getId()),
                m_cBoardChoice.getBoardclass().getName() + " " + m_cBoardChoice.getBoardclass().getBoard().getName(),
                m_cSyllabi.getSubjectName(),
                m_cChapters.getName());
        ArrayList<LessonShares> m_cLessonSharesList = new ArrayList<>();
        if (null != lessonsTableList && lessonsTableList.size() > 0) {
            for (LessonsTable lessonsTable : lessonsTableList) {
                Lessons lessons = new Lessons();
                lessons.setId(lessonsTable.getLessonId());
                lessons.setName(lessonsTable.getName());
                lessons.setComments(lessonsTable.getComments());
                lessons.setCreated(lessonsTable.getCreated());
                lessons.setModified(lessonsTable.getModified());

                String[] lStrArr = lessonsTable.getBoardClass().split(" ");
                BoardClass lBoardClass = new BoardClass();
                lBoardClass.setName(lStrArr[0]);
                Board lBoard = new Board();
                lBoard.setName(lStrArr[1]);
                lBoardClass.setBoard(lBoard);
                lBoardClass.setIsGeneric(false);

                Chapters lChapters = new Chapters();
                Syllabi lSyllabi = new Syllabi();
                lSyllabi.setBoardclass(lBoardClass);
                lSyllabi.setName(lessonsTable.getSyllabiName());
                lChapters.setSyllabus(lSyllabi);
                lChapters.setName(lessonsTable.getChapterName());


                lessons.setChapter(lChapters);
                Users lUsers = new Users();
                lUsers.setId(lessonsTable.getOwnerId());
                lessons.setOwner(lUsers);
                lessons.setPosition(lessonsTable.getPosition());
                Length length = new Length();
                length.setLengthSum(lessonsTable.getLengthSum());
                lessons.setLength(length);
                lessons.setViews(lessonsTable.getViews());
                m_cLessonsList.add(lessons);

                LessonShares lessonShares = new LessonShares();
                Users lUserShare = new Users();
                lUserShare.setId(lessonsTable.getSharerId());
                lessonShares.setFromUser(lUserShare);
                lessonShares.setLesson(lessons);
                m_cLessonSharesList.add(lessonShares);
            }
            m_cRecycClassesAdapt = new CustomRecyclerAdapterForLessonsReceived(m_cObjMainActivity, m_cUser, m_cBoardChoice,
                    m_cSyllabi, m_cChapters, m_cLessonsList, m_cLessonSharesList, null, this);
            m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
        } else {
            if (null != m_cRecycClassesAdapt) {
                m_cLessonsList.clear();
                m_cRecycClassesAdapt.notifyDataSetChanged();
            }
        }
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
                                    lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, mLessFromWhere);
                                    lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_MINE_TAB);
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
                    lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, mLessFromWhere);
                    lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_MINE_TAB);
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
        switch (pObjMessage.what){
            case PotMacros.ON_INFO_LONG_CLICK_SHARED:
                switch (pObjMessage.arg1){
                    case R.id.action_share:
                        lObjIntent = new Intent(m_cObjMainActivity, ShareScreen.class);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_LESSON);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_RECEIVED_TAB);
                        lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(((LessonShares)pObjMessage.obj).getLesson()));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONSHARES, (new Gson()).toJson((LessonShares) pObjMessage.obj));
                        startActivity(lObjIntent);
                        break;
                    case R.id.action_edit:
                        callLessonView((LessonShares) pObjMessage.obj, PotMacros.OBJ_LESSON_EDIT);
                        break;
                    case R.id.action_delete:
                        callDeleteLessonApi((LessonShares)pObjMessage.obj);
                        break;
                    case R.id.action_remove:
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        placeDeleteRequest(Constants.LESSONS +
                                        ((LessonShares) pObjMessage.obj).getLesson().getId() +
                                        "/" +
                                        Constants.SOURCES +
                                        ((LessonShares) pObjMessage.obj).getFromUser().getId() +
                                        "/",
                                Attachments.class, null, null, null, true);
                        break;
                    case R.id.action_add_syllabus:
                        Lessons lessons = ((LessonShares)pObjMessage.obj).getLesson();
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        placeUserRequest(Constants.BOARDCLASSES +
                                        lessons.getChapter().getSyllabus().getBoardclass().getId() +
                                        "/" +
                                        Constants.SYLLABI +
                                        lessons.getChapter().getSyllabus().getId() +
                                        "/"
                                , Syllabi.class, null, null, null, true);
                        break;
                    case R.id.action_post_to_students:
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        placeUserRequest(Constants.LESSONS +
                                        ((LessonShares)pObjMessage.obj).getLesson().getId() +
                                        "/" +
                                        Constants.POST,
                                Lessons.class, null, null, null, true);
                        break;
                    case R.id.action_save:
                        ((PotUserLessonScreen) m_cObjMainActivity).checkAndDownloadAttachments(((LessonShares) pObjMessage.obj).getLesson(), ((LessonShares) pObjMessage.obj).getFromUser().getId());
                        break;
                }
                break;
            default:
                break;
        }

    }

    private void callDeleteLessonApi(LessonShares pLessonShares) {
        m_cObjMainActivity.displayProgressBar(-1, "");
        placeDeleteRequest(Constants.CHAPTERS +
                        pLessonShares.getLesson().getChapter().getId() +
                        "/" +
                        Constants.LESSONS +
                        pLessonShares.getLesson().getId() +
                        "/",
                Attachments.class, null, null, null, true);
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.SOURCES)){
                    if (response == null) {
                        m_cObjMainActivity.hideDialog();
                        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(m_cObjMainActivity);
                        Intent i = new Intent(PotMacros.REMOVELESSON_REFRESH_CONSTANT_VIEWED);
                        lbm.sendBroadcast(i);
                        m_cLessonsList.clear();
                        m_cRecycClassesAdapt.notifyDataSetChanged();
                        init();
                    }
                } else if (response instanceof Syllabi){
                    Syllabi lSyllabi = (Syllabi) response;
                    if (lSyllabi != null) {
                        m_cObjMainActivity.hideDialog();
                        m_cObjMainActivity.displayToast(getResources().getString(R.string.syllabus_added_successfully_txt));
                    }
                } else if (apiMethod.contains(Constants.POST)){
                    if (response == null) {
                        m_cObjMainActivity.hideDialog();
                        m_cObjMainActivity.displayToast(getResources().getString(R.string.lesson_posted_successfully_txt));
                    }
                }else if (apiMethod.contains(Constants.VIEWS)){
                    LessonViews lessonViews = (LessonViews) response;
                    if (lessonViews != null) {
                        Intent lObjIntent = new Intent(m_cObjMainActivity, LessonsScreen.class);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON_TYPE, (Integer) ((Object[])refObj)[1]);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, mLessFromWhere);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_RECEIVED_TAB);
                        lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                        lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoice));
                        lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(m_cSyllabi));
                        lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(m_cChapters));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(lessonViews.getLesson()));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONSHARES, (new Gson()).toJson((LessonShares) ((Object[]) refObj)[0]));
                        startActivity(lObjIntent);
                        m_cObjMainActivity.hideDialog();
                    }
                }else if (apiMethod.contains(Constants.LESSONS)) {
                    if (response == null) {
                        m_cObjMainActivity.hideDialog();
                        init();
                    } else {
                        LessonSharesAll lLessonSharesAll = (LessonSharesAll) response;
                        if (lLessonSharesAll != null && lLessonSharesAll.getLessonShares().size() > 0) {
                            for (LessonShares lLessonShares : lLessonSharesAll.getLessonShares()) {
                                if (lLessonShares.getLesson().getChapter().getIsGeneric())
                                    ((PotUserLessonScreen) m_cObjMainActivity).setLessonsGen(lLessonShares.getLesson());
                                m_cLessonsList.add(lLessonShares.getLesson());
                            }
                            if (null != m_cLessonsList && m_cLessonsList.size() > 0) {
                                m_cRecycClassesAdapt = new CustomRecyclerAdapterForLessonsReceived(m_cObjMainActivity, m_cUser, m_cBoardChoice,
                                        m_cSyllabi, m_cChapters, m_cLessonsList, lLessonSharesAll.getLessonShares(), null, this);
                                m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
                            }
                        } else {
                            if (m_cLessonsList.size() >= 0) {
                                if (null != m_cRecycClassesAdapt) {
                                    m_cLessonsList.clear();
                                    m_cRecycClasses.setAdapter(new CustomRecyclerAdapterForLessonsReceived(m_cObjMainActivity, m_cUser, m_cBoardChoice,
                                            m_cSyllabi, m_cChapters, m_cLessonsList, lLessonSharesAll.getLessonShares(), null, this));
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
                if (apiMethod.contains(Constants.SOURCES) ||
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
        callLessonView(pLessonShares, PotMacros.OBJ_LESSON_VIEW);
    }

    private void callLessonView(LessonShares pLessonShares, int pLessonType) {
        m_cObjMainActivity.displayProgressBar(-1, "");
        JSONObject lJO = new JSONObject();
        try {
            lJO.put(Constants.SOURCE, pLessonShares.getFromUser().getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        placeUserRequest(Constants.LESSONS +
                        pLessonShares.getLesson().getId() +
                        "/" +
                        Constants.VIEWS,
                LessonViews.class, new Object[]{pLessonShares, pLessonType}, null, lJO.toString(), true);
    }

    @Override
    public void onInfoLongClick(int pPostion, BoardChoices pBoardChoices, Syllabi pSyllabi, Chapters pChapters, Lessons pLessons, LessonShares pLessonShares, LessonViews pLessonViews, View pView) {
        if (m_cUser.getId().equals(pLessons.getOwner().getId()))
            displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_SHARED, pLessons.getName(),
                    Arrays.asList(getResources().getString(R.string.action_post_to_students),
                            getResources().getString(R.string.action_share),
                            getResources().getString(R.string.action_edit),
                            getResources().getString(R.string.action_delete),
                            getResources().getString(R.string.action_add_syllabus),
                            getResources().getString(R.string.action_save)),
                    Arrays.asList(R.id.action_post_to_students,
                            R.id.action_share,
                            R.id.action_edit,
                            R.id.action_delete,
                            R.id.action_add_syllabus,
                            R.id.action_save),
                    pLessonShares);
        else
            displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_SHARED, pLessons.getName(),
                    m_cUser.getRole().equalsIgnoreCase(PotMacros.ROLE_STUDENT) ?
                            Arrays.asList(getResources().getString(R.string.action_remove),
                                    getResources().getString(R.string.action_add_syllabus),
                                    getResources().getString(R.string.action_save))
                            :
                            Arrays.asList(getResources().getString(R.string.action_post_to_students),
                                    getResources().getString(R.string.action_share),
                                    getResources().getString(R.string.action_remove),
                                    getResources().getString(R.string.action_add_syllabus),
                                    getResources().getString(R.string.action_save)),
                    m_cUser.getRole().equalsIgnoreCase(PotMacros.ROLE_STUDENT) ?
                            Arrays.asList(R.id.action_remove,
                                    R.id.action_add_syllabus,
                                    R.id.action_save)
                            :
                            Arrays.asList(R.id.action_post_to_students,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_add_syllabus,
                                    R.id.action_save),
                    pLessonShares);
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
}
