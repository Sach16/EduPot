package com.cosmicdew.lessonpot.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.activities.PotUserChapterScreen;
import com.cosmicdew.lessonpot.activities.PotUserLessonScreen;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForChapters;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.customviews.RecyclerViewHeader;
import com.cosmicdew.lessonpot.interfaces.RecyclerHomeListener;
import com.cosmicdew.lessonpot.interfaces.RecyclerSelectionListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.ChaptersAll;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.LessonViews;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.LessonsTable;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 27/10/16.
 */

public class PotUserHomeChapterFragment extends PotFragmentBaseClass implements RecyclerHomeListener {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    FrameLayout m_cRlMain;

    @Nullable
    @BindView(R.id.RECYC_HOME_CLASS_BOARDS)
    RecyclerView m_cRecycClasses;

    @Nullable
    @BindView(R.id.RECYCLERVIEW_HEADER)
    RecyclerViewHeader m_cHeaderView;

    private int m_cPos;
    private String m_cKey;
    private Users m_cUser;
    private BoardChoices m_cBoardChoices;
    private Syllabi m_cSyllabi;
    private String m_cSelectionType;

    private String m_cGoOffline;

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForChapters m_cRecycClassesAdapt;
    ArrayList<Chapters> m_cChaptersList;

    private static RecyclerSelectionListener m_cRecyclerSelectionListener;

    public PotUserHomeChapterFragment() {
        super();
    }

    public static PotUserHomeChapterFragment newInstance(int pPosition, String pKey, Users pUser, BoardChoices pBoardChoices, Syllabi pSyllabi, String pSellectionType,
                                                         RecyclerSelectionListener pRecyclerSelectionListener, String pGoOffline) {
        PotUserHomeChapterFragment lPotUserHomeChapterFragment = new PotUserHomeChapterFragment();

        m_cRecyclerSelectionListener = pRecyclerSelectionListener;

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("KEY", pKey);
        args.putString(PotMacros.OBJ_USER, (new Gson()).toJson(pUser));
        args.putString(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(pBoardChoices));
        args.putString(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(pSyllabi));
        args.putString(PotMacros.OBJ_SELECTIONTYPE, pSellectionType);
        args.putString(PotMacros.GO_OFFLINE, pGoOffline);
        lPotUserHomeChapterFragment.setArguments(args);

        return lPotUserHomeChapterFragment;
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
        m_cObjMainView = inflater.inflate(R.layout.fragment_chapters_mapping_addsyllabus, container, false);
        ButterKnife.bind(this, m_cObjMainView);

        m_cObjMainActivity.m_cObjFragmentBase = PotUserHomeChapterFragment.this;

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
        m_cUser = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_USER), Users.class);
        m_cBoardChoices = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_BOARDCHOICES), BoardChoices.class);
        m_cSyllabi = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_SYLLABI), Syllabi.class);
        m_cSelectionType = getArguments().getString(PotMacros.OBJ_SELECTIONTYPE);
        m_cGoOffline = getArguments().getString(PotMacros.GO_OFFLINE);
        m_cChaptersList = new ArrayList<>();
        m_cLayoutManager = new LinearLayoutManager(m_cObjMainActivity);
        m_cLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        m_cRecycClasses.setLayoutManager(m_cLayoutManager);
        if (null != m_cSelectionType &&
                m_cSelectionType.equalsIgnoreCase(PotMacros.OBJ_SELECTIONTYPE_ADDSYLLABUS)) {
            m_cHeaderView.setVisibility(View.VISIBLE);
            m_cHeaderView.attachTo(m_cRecycClasses);
        }
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
            //Calling board class api
            m_cObjMainActivity.displayProgressBar(-1, "");
            placeUserRequest(Constants.SYLLABI +
                            m_cSyllabi.getId() +
                            "/" +
                            Constants.CHAPTERS
                    , ChaptersAll.class, null, null, null, false);
        }
    }

    private void initOffline() {
        List<LessonsTable> lessonsTableAll = LessonsTable.listAll(LessonsTable.class);
        List<LessonsTable> lessonsTableList = LessonsTable.findWithQuery(LessonsTable.class, "select * from lessons_table where user_id = ? and board_class like '%'||?||'%' and syllabi_name = ? and lesson_id != -1",
                String.valueOf(m_cUser.getId()),
                m_cBoardChoices.getBoardclass().getName() + "," + m_cBoardChoices.getBoardclass().getBoard().getName(),
                m_cSyllabi.getSubjectName());
        Set<String> lSetChapters = new HashSet<>();
        for (LessonsTable lessonsTable : lessonsTableList)
            lSetChapters.add(lessonsTable.getChapterName());
        if (null != lSetChapters && lSetChapters.size() > 0) {
            for (String lChpName : lSetChapters) {
                Chapters lChapters = new Chapters();
                lChapters.setIsGeneric(false);
                lChapters.setName(lChpName);
                lChapters.setLessonCount(0);
                m_cChaptersList.add(lChapters);
            }
            m_cRecycClassesAdapt = new CustomRecyclerAdapterForChapters(m_cObjMainActivity, m_cChaptersList, m_cSelectionType,
                    m_cGoOffline, this);
            m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
        } else {
            if (null != m_cRecycClassesAdapt) {
                m_cChaptersList.clear();
                m_cRecycClassesAdapt.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {

    }

    @Optional
    @OnClick({R.id.FILTER_LIST_TXT})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FILTER_LIST_TXT:
                //calling add to syllabus api
                m_cObjMainActivity.displayProgressBar(-1, "");
                placeUserRequest(Constants.BOARDCLASSES +
                                m_cBoardChoices.getBoardclass().getId() +
                                "/" +
                                Constants.SYLLABI +
                                m_cSyllabi.getId() +
                                "/"
                        , Syllabi.class, null, null, null, true);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.BOARDCLASSES)) {
                    Syllabi lSyllabi = (Syllabi) response;
                    if (null != lSyllabi) {
                        m_cObjMainActivity.hideDialog();
                        m_cObjMainActivity.displayToast(getResources().getString(R.string.syllabus_added_successfully_txt));
                        m_cRecyclerSelectionListener.onInfoClicked(-1, m_cBoardChoices, m_cSyllabi, null, null, PotMacros.FRAG_CHAPTER);
                    }
                } else if (apiMethod.contains(Constants.CHAPTERS)){
                    ChaptersAll lChaptersAll = (ChaptersAll) response;
                    if (lChaptersAll != null && lChaptersAll.getChapters().size() > 0) {
                        for (Chapters lChapters : lChaptersAll.getChapters()) {
                            if (null == m_cSelectionType)
                                if (lChapters.getIsGeneric())
                                    ((PotUserChapterScreen) m_cObjMainActivity).setChaptersGen(lChapters);
                            if (null != m_cSelectionType &&
                                    m_cSelectionType.equalsIgnoreCase(PotMacros.OBJ_SELECTIONTYPE_ADDSYLLABUS) &&
                                    lChapters.getIsGeneric()) {
                            } else
                                m_cChaptersList.add(lChapters);
                        }
                        if (null != m_cChaptersList && m_cChaptersList.size() > 0) {
                            m_cRecycClassesAdapt = new CustomRecyclerAdapterForChapters(m_cObjMainActivity, m_cChaptersList, m_cSelectionType,
                                    m_cGoOffline, this);
                            m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
                        }
                    } else {
                        if (m_cChaptersList.size() > 0) {
                            m_cChaptersList.clear();
                            m_cRecycClassesAdapt.notifyDataSetChanged();
                        }
                    }
                    m_cObjMainActivity.hideDialog();
                }else {
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
                if (apiMethod.contains(Constants.BOARDCLASSES)||
                        apiMethod.contains(Constants.CHAPTERS)) {
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
        Intent lObjIntent;
        if (null != m_cGoOffline){
            lObjIntent = new Intent(m_cObjMainActivity, PotUserLessonScreen.class);
            lObjIntent.putExtra(PotMacros.GO_OFFLINE, m_cGoOffline);
            lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_CHAPTERS);
            lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
            lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoices));
            lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(m_cSyllabi));
            lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(pChapters));
            startActivity(lObjIntent);
        }else {
            if (pChapters.getIsGeneric()) {
                lObjIntent = new Intent(m_cObjMainActivity, PotUserLessonScreen.class);
                lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_CHAPTERS);
                lObjIntent.putExtra(PotMacros.LESSON_HEADER, pChapters.getName());
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoices));
                lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(m_cSyllabi));
                lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(pChapters));
                startActivity(lObjIntent);
            } else {
                lObjIntent = new Intent(m_cObjMainActivity, PotUserLessonScreen.class);
                lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_CHAPTERS);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoices));
                lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(m_cSyllabi));
                lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(pChapters));
                startActivity(lObjIntent);
            }
        }
    }

    @Override
    public void onInfoLongClick(int pPostion, BoardChoices pBoardChoices, Syllabi pSyllabi, Chapters pChapters, Lessons pLessons, LessonShares pLessonShares, LessonViews pLessonViews, View pView) {

    }

    @Override
    public void onSelectionClicked(int pPostion, BoardChoices pBoardChoices, Syllabi pSyllabi, Chapters pChapters, Lessons pLessons, LessonShares pLessonShares, LessonViews pLessonViews, View pView) {
        if (null != m_cSelectionType &&
                m_cSelectionType.equalsIgnoreCase(PotMacros.OBJ_SELECTIONTYPE))
            m_cRecyclerSelectionListener.onInfoClicked(pPostion, m_cBoardChoices, m_cSyllabi, pChapters, null, PotMacros.FRAG_CHAPTER);
    }

    public void onBackPressed() {
        m_cObjMainActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
