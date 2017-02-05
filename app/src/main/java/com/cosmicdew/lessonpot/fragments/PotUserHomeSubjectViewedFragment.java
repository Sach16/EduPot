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
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.activities.LessonsScreen;
import com.cosmicdew.lessonpot.activities.PotUserSubjectScreen;
import com.cosmicdew.lessonpot.activities.ShareScreen;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForLessonsViewed;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.interfaces.RecyclerHomeListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Attachments;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.LessonViews;
import com.cosmicdew.lessonpot.models.LessonViewsAll;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by S.K. Pissay on 8/11/16.
 */

public class PotUserHomeSubjectViewedFragment extends PotFragmentBaseClass implements RecyclerHomeListener {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    RelativeLayout m_cRlMain;

    @Nullable
    @BindView(R.id.RECYC_HOME_CLASS_BOARDS)
    RecyclerView m_cRecycClasses;

    private int m_cPos;
    private String m_cKey;
    private Users m_cUser;
    private BoardChoices m_cBoardChoices;

    private RemoveLessonReceiver mRemvReceiver;

    private String m_cGoOffline;

    private Dialog m_cObjDialog;

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForLessonsViewed m_cRecycClassesAdapt;
    ArrayList<Lessons> m_cLessonsList;

    public PotUserHomeSubjectViewedFragment() {
        super();
    }

    public static PotUserHomeSubjectViewedFragment newInstance(int pPosition, String pKey, Users pUser, BoardChoices pBoardChoices, String pGoOffline) {
        PotUserHomeSubjectViewedFragment lPotUserHomeSubjectViewedFragment = new PotUserHomeSubjectViewedFragment();

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("KEY", pKey);
        args.putString(PotMacros.OBJ_USER, (new Gson()).toJson(pUser));
        args.putString(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(pBoardChoices));
        args.putString(PotMacros.GO_OFFLINE, pGoOffline);
        lPotUserHomeSubjectViewedFragment.setArguments(args);

        return lPotUserHomeSubjectViewedFragment;
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

        m_cObjMainActivity.m_cObjFragmentBase = PotUserHomeSubjectViewedFragment.this;

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
        m_cBoardChoices = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_BOARDCHOICES), BoardChoices.class);
        m_cGoOffline = getArguments().getString(PotMacros.GO_OFFLINE);
        m_cLessonsList = new ArrayList<>();
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

        //Calling board class api
        m_cObjMainActivity.displayProgressBar(-1, "");

        placeUserRequest(Constants.VIEWED + Constants.BOARDCLASSES +
                m_cBoardChoices.getBoardclass().getId() +
                "/" +
                Constants.LESSONS, LessonViewsAll.class, null, null, null, false);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        Intent lObjIntent;
        switch (pObjMessage.what){
            case PotMacros.ON_INFO_LONG_CLICK_VIEWED:
                switch (pObjMessage.arg1){
                    case R.id.action_share:
                        lObjIntent = new Intent(m_cObjMainActivity, ShareScreen.class);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_SYLLABI);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_VIEWED_TAB);
                        lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(((LessonViews)pObjMessage.obj).getLesson()));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONVIEWS, (new Gson()).toJson((LessonViews) pObjMessage.obj));
                        startActivity(lObjIntent);
                        break;
                    case R.id.action_edit:
                        callLessonView((LessonViews) pObjMessage.obj, PotMacros.OBJ_LESSON_EDIT);
                        break;
                    case R.id.action_delete:
                        callDeleteLessonApi((LessonViews)pObjMessage.obj);
                        break;
                    case R.id.action_remove:
                        m_cObjMainActivity.displayProgressBar(-1, "");
                        placeDeleteRequest(Constants.LESSONS +
                                        ((LessonViews) pObjMessage.obj).getLesson().getId() +
                                        "/" +
                                        Constants.SOURCES +
                                        ((LessonViews) pObjMessage.obj).getSource().getId() +
                                        "/",
                                Attachments.class, null, null, null, true);
                        break;
                    case R.id.action_add_syllabus:
                        Lessons lessons = ((LessonViews) pObjMessage.obj).getLesson();
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
                                        ((LessonViews)pObjMessage.obj).getLesson().getId() +
                                        "/" +
                                        Constants.POST,
                                Lessons.class, null, null, null, true);
                        break;
                    case R.id.action_save:
                        ((PotUserSubjectScreen) m_cObjMainActivity).checkAndDownloadAttachments(((LessonViews) pObjMessage.obj).getLesson(), ((LessonViews) pObjMessage.obj).getSource().getId());
                        break;
                }
                break;
            default:
                break;
        }

    }

    private void callDeleteLessonApi(LessonViews pLessonViews) {
        m_cObjMainActivity.displayProgressBar(-1, "");
        placeDeleteRequest(Constants.CHAPTERS +
                        pLessonViews.getLesson().getChapter().getId() +
                        "/" +
                        Constants.LESSONS +
                        pLessonViews.getLesson().getId() +
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
                        Intent i = new Intent(PotMacros.REMOVELESSON_REFRESH_CONSTANT_RECEIVED);
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
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON_TYPE, (Integer) refObj);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_SYLLABI);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_VIEWED_TAB);
                        lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                        lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoices));
                        lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(lessonViews.getLesson().getChapter().getSyllabus()));
                        lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(lessonViews.getLesson().getChapter()));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(lessonViews.getLesson()));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONVIEWS, (new Gson()).toJson(lessonViews));
                        startActivity(lObjIntent);
                        m_cObjMainActivity.hideDialog();
                    }
                }else if (apiMethod.contains(Constants.CHAPTERS)){
                    if (response == null){
                        m_cObjMainActivity.hideDialog();
                        init();
                    }
                } else if (apiMethod.contains(Constants.LESSONS)) {
                    LessonViewsAll lLessonViewsAll = (LessonViewsAll) response;
                    if (lLessonViewsAll != null && lLessonViewsAll.getLessonViews().size() > 0) {
                        for (LessonViews lessonViews : lLessonViewsAll.getLessonViews()) {
//                            if (lLessons.getChapter().getIsGeneric())
//                                ((PotUserLessonScreen) m_cObjMainActivity).setLessonsGen(lLessons);
                            m_cLessonsList.add(lessonViews.getLesson());
                        }
                        if (null != m_cLessonsList && m_cLessonsList.size() > 0) {
                            m_cRecycClassesAdapt = new CustomRecyclerAdapterForLessonsViewed(m_cObjMainActivity, m_cUser, m_cBoardChoices,
                                    null, null, m_cLessonsList, null, lLessonViewsAll.getLessonViews(), this);
                            m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
                        }
                    } else {
                        if (m_cLessonsList.size() >= 0) {
                            if (null != m_cRecycClassesAdapt) {
                                m_cLessonsList.clear();
                                m_cRecycClasses.setAdapter(new CustomRecyclerAdapterForLessonsViewed(m_cObjMainActivity, m_cUser, m_cBoardChoices,
                                        null, null, m_cLessonsList, null, lLessonViewsAll.getLessonViews(), this));
                                m_cRecycClasses.invalidate();
                            }
                        }
                    }
                    m_cObjMainActivity.hideDialog();
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
        callLessonView(pLessonViews, PotMacros.OBJ_LESSON_VIEW);
    }

    private void callLessonView(LessonViews pLessonViews, int pLessonType) {
        m_cObjMainActivity.displayProgressBar(-1, "");
        JSONObject lJO = new JSONObject();
        try {
            lJO.put(Constants.SOURCE, pLessonViews.getSource().getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        placeUserRequest(Constants.LESSONS +
                        pLessonViews.getLesson().getId() +
                        "/" +
                        Constants.VIEWS,
                LessonViews.class, pLessonType, null, lJO.toString(), true);
    }

    @Override
    public void onInfoLongClick(int pPostion, BoardChoices pBoardChoices, Syllabi pSyllabi, Chapters pChapters, Lessons pLessons, LessonShares pLessonShares, LessonViews pLessonViews, View pView) {
        if (m_cUser.getId().equals(pLessons.getOwner().getId()))
            displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_VIEWED, pLessons.getName(),
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
                    pLessonViews);
        else
            displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_VIEWED, pLessons.getName(),
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
                    pLessonViews);
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