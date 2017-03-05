package com.cosmicdew.lessonpot.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.activities.PotUserChapterScreen;
import com.cosmicdew.lessonpot.activities.PotUserLessonScreen;
import com.cosmicdew.lessonpot.activities.PotUserSubjectScreen;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForSubjects;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.interfaces.RecyclerHomeListener;
import com.cosmicdew.lessonpot.interfaces.RecyclerSelectionListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.LessonViews;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.LessonsTable;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.SyllabiAll;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by S.K. Pissay on 27/10/16.
 */

public class PotUserHomeSubjectFragment extends PotFragmentBaseClass implements RecyclerHomeListener {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    RelativeLayout m_cRlMain;

    @Nullable
    @BindView(R.id.RECYC_HOME_CLASS_BOARDS)
    RecyclerView m_cRecycClasses;

    private Dialog m_cObjDialog;

    private int m_cPos;
    private String m_cKey;
    private Users m_cUser;
    private BoardChoices m_cBoardChoices;
    private String m_cSelectionType;

    private String m_cGoOffline;

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForSubjects m_cRecycClassesAdapt;
    ArrayList<Syllabi> m_cSyllabiList;

    private static RecyclerSelectionListener m_cRecyclerSelectionListener;

    public PotUserHomeSubjectFragment() {
        super();
    }

    public static PotUserHomeSubjectFragment newInstance(int pPosition, String pKey, Users pUser, BoardChoices pBoardChoices, String pSellectionType,
                                                         RecyclerSelectionListener pRecyclerSelectionListener, String pGoOffline) {
        PotUserHomeSubjectFragment lPotUserHomeSubjectFragment = new PotUserHomeSubjectFragment();

        m_cRecyclerSelectionListener = pRecyclerSelectionListener;

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("KEY", pKey);
        args.putString(PotMacros.OBJ_USER, (new Gson()).toJson(pUser));
        args.putString(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(pBoardChoices));
        args.putString(PotMacros.OBJ_SELECTIONTYPE, pSellectionType);
        args.putString(PotMacros.GO_OFFLINE, pGoOffline);
        lPotUserHomeSubjectFragment.setArguments(args);

        return lPotUserHomeSubjectFragment;
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

        m_cObjMainActivity.m_cObjFragmentBase = PotUserHomeSubjectFragment.this;

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
        m_cSelectionType = getArguments().getString(PotMacros.OBJ_SELECTIONTYPE);
        m_cGoOffline = getArguments().getString(PotMacros.GO_OFFLINE);
        m_cSyllabiList = new ArrayList<>();
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

        if (null != m_cGoOffline){
            initOffline();
        }else {
            //Calling board class api
            m_cObjMainActivity.displayProgressBar(-1, "");
            if (null != m_cSelectionType &&
                    m_cSelectionType.equalsIgnoreCase(PotMacros.OBJ_SELECTIONTYPE_ADDSYLLABUS))
                placeUserRequest(Constants.USER +
                                m_cUser.getId() +
                                "/" +
                                Constants.BOARDCLASSES +
                                m_cBoardChoices.getBoardclass().getId() +
                                "/" +
                                Constants.SYLLABI
                        , SyllabiAll.class, null, null, null, false);
            else
                placeUserRequest(Constants.BOARDCLASSES +
                                m_cBoardChoices.getBoardclass().getId() +
                                "/" +
                                Constants.SYLLABI
                        , SyllabiAll.class, null, null, null, false);
        }

    }

    private void initOffline() {
        List<LessonsTable> lessonsTableAll = LessonsTable.listAll(LessonsTable.class);
        List<LessonsTable> lessonsTableList = LessonsTable.findWithQuery(LessonsTable.class, "select * from lessons_table where user_id = ? and board_class like '%'||?||'%' and lesson_id != -1",
                String.valueOf(m_cUser.getId()),
                m_cBoardChoices.getBoardclass().getName() + "," + m_cBoardChoices.getBoardclass().getBoard().getName());
        Set<String> lSetSyllabi = new HashSet<>();
        for (LessonsTable lessonsTable : lessonsTableList)
            lSetSyllabi.add(lessonsTable.getSyllabiName());
        if (null != lSetSyllabi && lSetSyllabi.size() > 0) {
            for (String lSylName : lSetSyllabi) {
                Syllabi lSyllabi = new Syllabi();
                lSyllabi.setIsGeneric(false);
                lSyllabi.setName(lSylName);
                lSyllabi.setChapterCount(0);
                lSyllabi.setLessonCount(0);
                m_cSyllabiList.add(lSyllabi);
            }
            m_cRecycClassesAdapt = new CustomRecyclerAdapterForSubjects(m_cObjMainActivity, m_cSyllabiList, m_cSelectionType,
                    m_cGoOffline, this);
            m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
        } else {
            if (null != m_cRecycClassesAdapt) {
                m_cSyllabiList.clear();
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
        Intent lObjIntent;
        switch (pObjMessage.what) {
            case PotMacros.ON_INFO_LONG_CLICK_SYLLABI:
                switch (pObjMessage.arg1) {
                    case R.id.action_remove:
                        Syllabi lSyllabi = (Syllabi) pObjMessage.obj;
                        displayYesOrNoCustAlert(R.id.action_remove,
                                getResources().getString(R.string.delete_subject_txt),
                                String.format("%s \n%s",
                                        getResources().getString(R.string.once_you_delete_this_subject_txt),
                                        getResources().getString(R.string.do_you_want_to_proceed_txt)),
                                lSyllabi);
                        break;
                }
                break;
            case R.id.action_remove:
                Object[] lObjects = (Object[]) pObjMessage.obj;
                Syllabi lSyllabi = (Syllabi) lObjects[1];
                if (null != m_cGoOffline) {
                    List<LessonsTable> lessonsTableList = LessonsTable.findWithQuery(LessonsTable.class,
                            "select * from lessons_table where user_id = ? and board_class like '%'||?||'%' and syllabi_name = ?",
                            String.valueOf(m_cUser.getId()),
                            m_cBoardChoices.getBoardclass().getName() + "," + m_cBoardChoices.getBoardclass().getBoard().getName(),
                            lSyllabi.getName().trim());
                    if (null != lessonsTableList && lessonsTableList.size() > 0)
                        for (LessonsTable lessonsTable : lessonsTableList) {
                            m_cObjMainActivity.checkAndDelete(lessonsTable.getImg1());
                            m_cObjMainActivity.checkAndDelete(lessonsTable.getImg2());
                            m_cObjMainActivity.checkAndDelete(lessonsTable.getImg3());
                            m_cObjMainActivity.checkAndDelete(lessonsTable.getAudio());
                            LessonsTable.delete(lessonsTable);
                        }
                    if (null != m_cRecycClassesAdapt) {
                        m_cSyllabiList.clear();
                        m_cRecycClassesAdapt.notifyDataSetChanged();
                    }
                    initOffline();
                } else {
                    if ((boolean) lObjects[0]) {
                        m_cObjMainActivity.displayProgressBar(-1, "Loading...");
                        RequestManager.getInstance(m_cObjMainActivity).placeUnivUserRequest(Constants.BOARDCLASSES +
                                        m_cBoardChoices.getBoardclass().getId() +
                                        "/" +
                                        Constants.SYLLABI +
                                        lSyllabi.getId() +
                                        "/"
                                , Users.class, this, PotMacros.ON_INFO_LONG_CLICK_SYLLABI, null, null, Request.Method.DELETE);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (refObj != null && (Integer) refObj == PotMacros.ON_INFO_LONG_CLICK_SYLLABI) {
                    m_cSyllabiList.clear();
                    m_cRecycClassesAdapt.notifyDataSetChanged();
                    init();
                } else if (apiMethod.contains(Constants.SYLLABI)){
                    SyllabiAll lSyllabiAll = (SyllabiAll) response;
                    if (lSyllabiAll != null && lSyllabiAll.getSyllabi().size() > 0) {
                        for (Syllabi lSyllabi : lSyllabiAll.getSyllabi()) {
                            if (null == m_cSelectionType)
                                if (lSyllabi.getIsGeneric())
                                    ((PotUserSubjectScreen) m_cObjMainActivity).setSyllabiGen(lSyllabi);
                            if (null != m_cSelectionType &&
                                    m_cSelectionType.equalsIgnoreCase(PotMacros.OBJ_SELECTIONTYPE_ADDSYLLABUS) &&
                                    lSyllabi.getIsGeneric()) {
                            } else
                                m_cSyllabiList.add(lSyllabi);
                        }
                        if (null != m_cSyllabiList && m_cSyllabiList.size() > 0) {
                            m_cRecycClassesAdapt = new CustomRecyclerAdapterForSubjects(m_cObjMainActivity, m_cSyllabiList, m_cSelectionType,
                                    m_cGoOffline, this);
                            m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
                        }
                    } else {
                        if (m_cSyllabiList.size() > 0) {
                            m_cSyllabiList.clear();
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
                if ((refObj != null && (Integer) refObj == PotMacros.ON_INFO_LONG_CLICK_BOARD_CLASS) ||
                        apiMethod.contains(Constants.SYLLABI)) {
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
            lObjIntent = new Intent(m_cObjMainActivity, PotUserChapterScreen.class);
            lObjIntent.putExtra(PotMacros.GO_OFFLINE, m_cGoOffline);
            lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
            lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoices));
            lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(pSyllabi));
            startActivity(lObjIntent);
        }else {
            if (pSyllabi.getIsGeneric()) {
                lObjIntent = new Intent(m_cObjMainActivity, PotUserLessonScreen.class);
                lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_SYLLABI);
                lObjIntent.putExtra(PotMacros.LESSON_HEADER, pSyllabi.getName());
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoices));
                lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(pSyllabi));
                startActivity(lObjIntent);
            } else {
                lObjIntent = new Intent(m_cObjMainActivity, PotUserChapterScreen.class);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoices));
                lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(pSyllabi));
                startActivity(lObjIntent);
            }
        }
    }

    @Override
    public void onInfoLongClick(int pPostion, BoardChoices pBoardChoices, Syllabi pSyllabi, Chapters pChapters, Lessons pLessons, LessonShares pLessonShares, LessonViews pLessonViews, View pView) {
        displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_SYLLABI,
                pSyllabi.getName(),
                Arrays.asList(getResources().getString(R.string.delete_subject_txt)),
                Arrays.asList(R.id.action_remove),
                pSyllabi);
    }

    @Override
    public void onSelectionClicked(int pPostion, BoardChoices pBoardChoices, Syllabi pSyllabi, Chapters pChapters, Lessons pLessons, LessonShares pLessonShares, LessonViews pLessonViews, View pView) {
        m_cRecyclerSelectionListener.onInfoClicked(pPostion, m_cBoardChoices, pSyllabi, null, null, PotMacros.FRAG_SUBJECTS);
    }

    public void onBackPressed() {
        m_cObjMainActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
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
