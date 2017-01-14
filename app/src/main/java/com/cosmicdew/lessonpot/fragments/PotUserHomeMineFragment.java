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
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.cosmicdew.lessonpot.activities.PotUserLessonScreen;
import com.cosmicdew.lessonpot.activities.ShareScreen;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForLessonsMine;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.interfaces.OnStartDragListener;
import com.cosmicdew.lessonpot.interfaces.RecyclerHomeListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Attachments;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.LessonViews;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.LessonsAll;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.utils.SimpleItemTouchHelperCallback;
import com.google.gson.Gson;

import org.json.JSONArray;
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

/**
 * Created by S.K. Pissay on 17/10/16.
 */

public class PotUserHomeMineFragment extends PotFragmentBaseClass implements RecyclerHomeListener, OnStartDragListener {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    RelativeLayout m_cRlMain;

    @Nullable
    @BindView(R.id.RECYC_HOME_CLASS_BOARDS)
    RecyclerView m_cRecycClasses;

    @Nullable
    @BindView(R.id.RECYCLERVIEW_HEADER)
    RelativeLayout m_cHeaderView;

    @Nullable
    @BindView(R.id.HEADER_DET_TXT)
    TextView m_cHeaderDetTxt;

    @Nullable
    @BindView(R.id.FILTER_LIST_TXT)
    TextView m_cHeaderListTxt;

    private ItemTouchHelper m_cItemTouchHelper;

    private boolean m_cIsInReorder = false;
    private boolean m_cReorderAttached = false;

    private int m_cPos;
    private String m_cKey;
    private Users m_cUser;
    private BoardChoices m_cBoardChoice;
    private Syllabi m_cSyllabi;
    private Chapters m_cChapters;

    private Dialog m_cObjDialog;
    private String mLessFromWhere;

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForLessonsMine m_cRecycClassesAdapt;
    ArrayList<Lessons> m_cLessonsList;

    public PotUserHomeMineFragment() {
        super();
    }

    public static PotUserHomeMineFragment newInstance(int pPosition, String pKey, Users pUser, BoardChoices pBoardChoices,
                                                        Syllabi pSyllabi, Chapters pChapters, String pLessFromWhere) {
        PotUserHomeMineFragment lPotUserHomeMineFragment = new PotUserHomeMineFragment();

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("KEY", pKey);
        args.putString(PotMacros.OBJ_USER, (new Gson()).toJson(pUser));
        args.putString(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(pBoardChoices));
        args.putString(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(pSyllabi));
        args.putString(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(pChapters));
        args.putString(PotMacros.OBJ_LESSONFROM, pLessFromWhere);
        lPotUserHomeMineFragment.setArguments(args);

        return lPotUserHomeMineFragment;
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
        m_cObjMainView = inflater.inflate(R.layout.fragment_chapter_mine_reorder, container, false);
        ButterKnife.bind(this, m_cObjMainView);

        m_cObjMainActivity.m_cObjFragmentBase = PotUserHomeMineFragment.this;

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
        m_cBoardChoice = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_BOARDCHOICES), BoardChoices.class);
        m_cSyllabi = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_SYLLABI), Syllabi.class);
        m_cChapters = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_CHAPTERS), Chapters.class);
        mLessFromWhere = getArguments().getString(PotMacros.OBJ_LESSONFROM);
        m_cLessonsList = new ArrayList<>();
        m_cLayoutManager = new LinearLayoutManager(m_cObjMainActivity);
        m_cLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        m_cRecycClasses.setLayoutManager(m_cLayoutManager);
        if (null != mLessFromWhere &&
                mLessFromWhere.equalsIgnoreCase(PotMacros.OBJ_CHAPTERS)) {
            m_cHeaderView.setVisibility(View.VISIBLE);
            m_cHeaderListTxt.setText(getResources().getString(R.string.reorder_txt));
//            m_cHeaderView.attachTo(m_cRecycClasses);
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

        //Calling board class api
        m_cObjMainActivity.displayProgressBar(-1, "");

        switch (mLessFromWhere) {
            case PotMacros.OBJ_BOARDCHOICES:
                placeUserRequest(Constants.CREATED +
                        Constants.GMR +
                        Constants.LESSONS, LessonsAll.class, null, null, null, false);

                break;
            case PotMacros.OBJ_SYLLABI:
                placeUserRequest(Constants.CREATED + Constants.BOARDCLASSES +
                        m_cBoardChoice.getBoardclass().getId() +
                        "/" +
                        Constants.GS +
                        Constants.LESSONS, LessonsAll.class, null, null, null, false);

                break;
            case PotMacros.OBJ_CHAPTERS:
                HashMap<String, String> lParams = new HashMap<>();
                lParams.put(Constants.ORDERING, Constants.POSITION);
                placeUserRequest(Constants.CREATED + Constants.CHAPTERS +
                        m_cChapters.getId() +
                        "/" +
                        Constants.LESSONS, LessonsAll.class, null, lParams, null, false);
                break;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Optional
    @OnClick({R.id.FILTER_LIST_TXT})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FILTER_LIST_TXT:
                if (m_cIsInReorder) {
                    m_cHeaderListTxt.setText(getResources().getString(R.string.reorder_txt));
                    m_cHeaderDetTxt.setText("");
                    /*ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(null);
                    m_cItemTouchHelper = new ItemTouchHelper(null);
                    m_cItemTouchHelper.attachToRecyclerView(null);*/
                    //Call bulk reorder api
                    m_cObjMainActivity.displayProgressBar(-1, "");
                    JSONObject lJO = new JSONObject();
                    try {
                        JSONArray lArray = new JSONArray();
                        for (int i = 0; i < m_cLessonsList.size(); ++i) {
                            lArray.put(m_cLessonsList.get(i).getId());
                        }
                        lJO.put(Constants.LESSON_ORDER, lArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    placePutRequest(Constants.CHAPTERS +
                            m_cChapters.getId() +
                            "/" +
                            Constants.LESSONS, LessonsAll.class, PotMacros.LESSONPOSITION, null, lJO.toString(), true);

                } else {
                    m_cHeaderDetTxt.setText(getResources().getString(R.string.hold_and_drag_to_change_order_txt));
                    m_cHeaderListTxt.setText(getResources().getString(R.string.done_txt));
                    //SWIPE ONTOUCH CODE BELOW
                    if (!m_cReorderAttached) {
                        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(m_cRecycClassesAdapt);
                        m_cItemTouchHelper = new ItemTouchHelper(callback);
                        m_cItemTouchHelper.attachToRecyclerView(m_cRecycClasses);
                        m_cReorderAttached = !m_cReorderAttached;
                    }
                }
                m_cIsInReorder = !m_cIsInReorder;
                CustomRecyclerAdapterForLessonsMine.setIsInReorder(m_cIsInReorder);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        Intent lObjIntent;
        switch (pObjMessage.what){
            case PotMacros.ON_INFO_LONG_CLICK_MINE_LESSON:
                switch (pObjMessage.arg1){
                    case R.id.action_share:
                        lObjIntent = new Intent(m_cObjMainActivity, ShareScreen.class);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, mLessFromWhere);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_MINE_TAB);
                        lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson((Lessons)pObjMessage.obj));
                        startActivity(lObjIntent);
                        break;
                    case R.id.action_edit:
                        callLessonView((Lessons)pObjMessage.obj, PotMacros.OBJ_LESSON_EDIT);
                        break;
                    case R.id.action_delete:
                        callDeleteLessonApi((Lessons)pObjMessage.obj);
                        break;
                    case R.id.action_add_syllabus:
                        Lessons lessons = (Lessons) pObjMessage.obj;
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
                                        ((Lessons)pObjMessage.obj).getId() +
                                        "/" +
                                        Constants.POST,
                                Lessons.class, null, null, null, true);
                        break;

                }
                break;
            default:
                break;
        }
    }

    private void callDeleteLessonApi(Lessons pLesson) {
        m_cObjMainActivity.displayProgressBar(-1, "");
        placeDeleteRequest(Constants.CHAPTERS +
                        pLesson.getChapter().getId() +
                        "/" +
                        Constants.LESSONS +
                        pLesson.getId() +
                        "/",
                Attachments.class, null, null, null, true);
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (refObj != null && ((Integer) refObj) == PotMacros.LESSONPOSITION) {
                    m_cLessonsList.clear();
                    m_cRecycClassesAdapt.notifyDataSetChanged();
                    init();
                } else if (response instanceof Syllabi) {
                    Syllabi lSyllabi = (Syllabi) response;
                    if (lSyllabi != null) {
                        m_cObjMainActivity.hideDialog();
                        m_cObjMainActivity.displayToast(getResources().getString(R.string.syllabus_added_successfully_txt));
                    }
                } else if (apiMethod.contains(Constants.POST)) {
                    if (response == null) {
                        m_cObjMainActivity.hideDialog();
                        m_cObjMainActivity.displayToast(getResources().getString(R.string.lesson_posted_successfully_txt));
                    }
                } else if (apiMethod.contains(Constants.VIEWS)) {
                    LessonViews lessonViews = (LessonViews) response;
                    if (lessonViews != null) {
                        Intent lObjIntent = new Intent(m_cObjMainActivity, LessonsScreen.class);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON_TYPE, (Integer) refObj);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, mLessFromWhere);
                        lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_MINE_TAB);
                        lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                        lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoice));
                        lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(m_cSyllabi));
                        lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(m_cChapters));
                        lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(lessonViews.getLesson()));
                        startActivity(lObjIntent);
                        m_cObjMainActivity.hideDialog();
                    }
                } else if (apiMethod.contains(Constants.LESSONS)) {
                    if (response == null) {
                        m_cObjMainActivity.hideDialog();
                        init();
                    } else {
                        LessonsAll lLessonsAll = (LessonsAll) response;
                        if (lLessonsAll != null && lLessonsAll.getLessons().size() > 0) {
                            for (Lessons lLessons : lLessonsAll.getLessons()) {
                                if (lLessons.getChapter().getIsGeneric())
                                    ((PotUserLessonScreen) m_cObjMainActivity).setLessonsGen(lLessons);
                                m_cLessonsList.add(lLessons);
                            }
                            if (null != m_cLessonsList && m_cLessonsList.size() > 0) {
                                m_cRecycClassesAdapt = new CustomRecyclerAdapterForLessonsMine(m_cObjMainActivity, m_cUser, m_cBoardChoice,
                                        m_cSyllabi, m_cChapters, m_cLessonsList, null, null, this, this, mLessFromWhere, m_cIsInReorder);
                                m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
                            }
                        } else {
                            if (m_cLessonsList.size() > 0) {
                                m_cLessonsList.clear();
                                m_cRecycClassesAdapt.notifyDataSetChanged();
                            }
                            m_cHeaderView.setVisibility(View.GONE);
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
                if (apiMethod.contains(Constants.BOARDCLASSES) ||
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
        if (m_cUser.getId().equals(pLessons.getOwner().getId()))
            displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_MINE_LESSON, pLessons.getName(),
                    Arrays.asList(getResources().getString(R.string.action_post_to_students),
                            getResources().getString(R.string.action_share),
                            getResources().getString(R.string.action_edit),
                            getResources().getString(R.string.action_delete),
                            getResources().getString(R.string.action_add_syllabus)),
                    Arrays.asList(R.id.action_post_to_students,
                            R.id.action_share,
                            R.id.action_edit,
                            R.id.action_delete,
                            R.id.action_add_syllabus),
                    pLessons);
        else
            displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_MINE_LESSON, pLessons.getName(),
                    m_cUser.getRole().equalsIgnoreCase(PotMacros.ROLE_STUDENT) ?
                            Arrays.asList(getResources().getString(R.string.action_remove),
                                    getResources().getString(R.string.action_add_syllabus))
                            :
                            Arrays.asList(getResources().getString(R.string.action_post_to_students),
                                    getResources().getString(R.string.action_share),
                                    getResources().getString(R.string.action_remove),
                                    getResources().getString(R.string.action_add_syllabus)),
                    m_cUser.getRole().equalsIgnoreCase(PotMacros.ROLE_STUDENT) ?
                            Arrays.asList(R.id.action_remove,
                                    R.id.action_add_syllabus)
                            :
                            Arrays.asList(R.id.action_post_to_students,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_add_syllabus),
                    pLessons);
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder, List<Lessons> pObjLessons) {
        if (m_cIsInReorder) {
            m_cItemTouchHelper.startDrag(viewHolder);
            m_cLessonsList = (ArrayList<Lessons>) pObjLessons;
        }
    }
}
