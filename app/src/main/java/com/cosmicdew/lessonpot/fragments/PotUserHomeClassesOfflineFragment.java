package com.cosmicdew.lessonpot.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.activities.LessonsOfflineScreen;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForLessonsMine;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.interfaces.OnStartDragListener;
import com.cosmicdew.lessonpot.interfaces.RecyclerHomeListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Attachments;
import com.cosmicdew.lessonpot.models.Board;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.BoardClass;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.Length;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.LessonViews;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.LessonsAll;
import com.cosmicdew.lessonpot.models.LessonsTable;
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
 * Created by S.K. Pissay on 7/2/17.
 */

public class PotUserHomeClassesOfflineFragment extends PotFragmentBaseClass implements RecyclerHomeListener, OnStartDragListener {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    RelativeLayout m_cRlMain;

    @Nullable
    @BindView(R.id.RECYC_HOME_CLASS_BOARDS)
    RecyclerView m_cRecycClasses;

    public static final int NOTIFICATION_FOR_NETWORK_CONNECTION_AVAILABLE_LOCALLY = 1016;

    private int m_cPos;
    private String m_cKey;
    private Users m_cUser;

    private Dialog m_cObjDialog;

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForLessonsMine m_cRecycClassesAdapt;
    ArrayList<Lessons> m_cLessonsList;

    private String m_cGoOffline;
    private List<LessonsTable> m_cLessonsTableList;

    public PotUserHomeClassesOfflineFragment() {
        super();
    }

    public static PotUserHomeClassesOfflineFragment newInstance(int pPosition, String pKey, Users pUser, String pGoOffline) {
        PotUserHomeClassesOfflineFragment lPotUserHomeClassesOfflineFragment = new PotUserHomeClassesOfflineFragment();

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("KEY", pKey);
        args.putString(PotMacros.OBJ_USER, (new Gson()).toJson(pUser));
        args.putString(PotMacros.GO_OFFLINE, pGoOffline);
        lPotUserHomeClassesOfflineFragment.setArguments(args);

        return lPotUserHomeClassesOfflineFragment;
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

        m_cObjMainActivity.m_cObjFragmentBase = PotUserHomeClassesOfflineFragment.this;

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

        if (null != m_cGoOffline){
            initOffline();
        }else {
            //Calling board class api
            m_cObjMainActivity.displayProgressBar(-1, "");

            placeUserRequest(Constants.CREATED + Constants.LESSONS, LessonsAll.class, null, null, null, false);
        }

    }

    private void initOffline() {
        List<LessonsTable> lessonsTableAll = LessonsTable.listAll(LessonsTable.class);
        m_cLessonsTableList = LessonsTable.find(LessonsTable.class, "user_id = ? and owner_id = ? and (lesson_id = -1 or is_edited)",String.valueOf(m_cUser.getId()), String.valueOf(m_cUser.getId()));
        if (null != m_cLessonsTableList && m_cLessonsTableList.size() > 0) {
            for (LessonsTable lessonsTable : m_cLessonsTableList) {
                Lessons lessons = new Lessons();
                lessons.setId(lessonsTable.getLessonId());
                lessons.setName(lessonsTable.getName());
                lessons.setComments(lessonsTable.getComments());
                lessons.setCreated(lessonsTable.getCreated());
                lessons.setModified(lessonsTable.getModified());

                String[] lStrArr = lessonsTable.getBoardClass().split(",");
                BoardClass lBoardClass = new BoardClass();
                lBoardClass.setName(lStrArr[0]);
                Board lBoard = new Board();
                if (lStrArr.length > 1)
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
                String[] lName = lessonsTable.getOwner().split(" ");
                lUsers.setFirstName(lName[0]);
                lUsers.setLastName(lName[1]);
                lessons.setOwner(lUsers);
                lessons.setPosition(lessonsTable.getPosition());
                Length length = new Length();
                length.setLengthSum(lessonsTable.getLengthSum());
                lessons.setLength(length);
                lessons.setViews(lessonsTable.getViews());
                m_cLessonsList.add(lessons);
            }
            m_cRecycClassesAdapt = new CustomRecyclerAdapterForLessonsMine(m_cObjMainActivity, m_cUser, null,
                    null, null, m_cLessonsList, null, null, this, this, null, m_cGoOffline, false);
            m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
        } else {
            if (null != m_cRecycClassesAdapt) {
                m_cLessonsList.clear();
                m_cRecycClasses.setAdapter(new CustomRecyclerAdapterForLessonsMine(m_cObjMainActivity, m_cUser, null,
                        null, null, m_cLessonsList, null, null, this, this, null, m_cGoOffline, false));
                m_cRecycClasses.invalidate();
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
            case PotMacros.ON_INFO_LONG_CLICK_MINE:
                Object[] lObjects = (Object[]) pObjMessage.obj;
                switch (pObjMessage.arg1) {
                    case R.id.action_edit:
                        Lessons pLessons = (Lessons) lObjects[0];
                        if (null != m_cGoOffline) {
                            lObjIntent = new Intent(m_cObjMainActivity, LessonsOfflineScreen.class);
                            lObjIntent.putExtra(PotMacros.OBJ_LESSON_TYPE, PotMacros.OBJ_LESSON_EDIT);
                            //Changed PotMacros.OBJ_LESSON to PotMacros.OBJ_BOARDCHOICES
                            lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_BOARDCHOICES);
                            lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_MINE_TAB);
                            lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                            lObjIntent.putExtra(PotMacros.GO_OFFLINE, m_cGoOffline);
                            lObjIntent.putExtra(PotMacros.GO_OFFLINE_LESSON_POSITION, (int) lObjects[1]);
                            BoardChoices lBoardChoices = new BoardChoices();
                            lBoardChoices.setBoardclass(pLessons.getChapter().getSyllabus().getBoardclass());
                            lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(lBoardChoices));
                            lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(pLessons.getChapter().getSyllabus()));
                            lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(pLessons.getChapter()));
                            lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(pLessons));
                            startActivity(lObjIntent);
                        } else
                            callLessonView((Lessons) lObjects[0], PotMacros.OBJ_LESSON_EDIT);
                        break;
                    case R.id.action_delete_lesson:
                        displayYesOrNoCustAlert(PotMacros.ACTION_DELETE_LESSON,
                                getResources().getString(R.string.action_delete_lesson),
                                getResources().getString(R.string.delete_offline_lesson_desc_txt),
                                (int) lObjects[1]);
                        break;
                    case R.id.action_upload:
//                        m_cObjMainActivity.new CheckIsNetWorkAvailable(true, lObjects).execute();
                        new CheckIsNetWorkAvailable(true, lObjects).execute();
                        break;
                }
                break;
            case NOTIFICATION_FOR_NETWORK_CONNECTION_AVAILABLE_LOCALLY:
                Object[] lObjectArr = (Object[]) pObjMessage.obj;
                Lessons lLessons = (Lessons) lObjectArr[0];
                lObjIntent = new Intent(m_cObjMainActivity, LessonsOfflineScreen.class);
                lObjIntent.putExtra(PotMacros.OBJ_LESSON_TYPE, PotMacros.OBJ_LESSON_UPLOAD);
                //Changed PotMacros.OBJ_LESSON to PotMacros.OBJ_BOARDCHOICES
                lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_BOARDCHOICES);
                lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_MINE_TAB);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                lObjIntent.putExtra(PotMacros.GO_OFFLINE, m_cGoOffline);
                lObjIntent.putExtra(PotMacros.GO_OFFLINE_LESSON_POSITION, (int) lObjectArr[1]);
                BoardChoices lBoardChoices = new BoardChoices();
                lBoardChoices.setBoardclass(lLessons.getChapter().getSyllabus().getBoardclass());
                lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(lBoardChoices));
                lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(lLessons.getChapter().getSyllabus()));
                lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(lLessons.getChapter()));
                lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(lLessons));
                startActivity(lObjIntent);
                break;
            case PotMacros.ACTION_DELETE_LESSON:
                Object[] lObjectDel = (Object[]) pObjMessage.obj;
                int pId = (int) lObjectDel[1];
                if ((boolean) lObjectDel[0]) {
                    if (null != m_cLessonsTableList && m_cLessonsTableList.size() > 0) {
                        try {
                            LessonsTable lessonsTable = m_cLessonsTableList.get(pId);
                            m_cObjMainActivity.checkAndDelete(lessonsTable.getImg1());
                            m_cObjMainActivity.checkAndDelete(lessonsTable.getImg2());
                            m_cObjMainActivity.checkAndDelete(lessonsTable.getImg3());
                            m_cObjMainActivity.checkAndDelete(lessonsTable.getAudio());
                            LessonsTable.delete(lessonsTable);
                            if (null != m_cRecycClassesAdapt) {
                                m_cLessonsList.clear();
                                m_cRecycClassesAdapt.notifyDataSetChanged();
                            }
                            initOffline();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
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
                if (apiMethod.contains(Constants.ATTACHMENTS)
                        || apiMethod.contains(Constants.LESSONS)) {
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
                if (apiMethod.contains(Constants.ATTACHMENTS) ||
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
        if (null != m_cGoOffline) {
            Intent lObjIntent = new Intent(m_cObjMainActivity, LessonsOfflineScreen.class);
            lObjIntent.putExtra(PotMacros.OBJ_LESSON_TYPE, PotMacros.OBJ_LESSON_VIEW);
            //Changed PotMacros.OBJ_LESSON to PotMacros.OBJ_BOARDCHOICES
            lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_BOARDCHOICES);
            lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, PotMacros.OBJ_LESSON_MINE_TAB);
            lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
            lObjIntent.putExtra(PotMacros.GO_OFFLINE, m_cGoOffline);
            lObjIntent.putExtra(PotMacros.GO_OFFLINE_LESSON_POSITION, pPostion);
            BoardChoices lBoardChoices = new BoardChoices();
            lBoardChoices.setBoardclass(pLessons.getChapter().getSyllabus().getBoardclass());
            lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(lBoardChoices));
            lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(pLessons.getChapter().getSyllabus()));
            lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(pLessons.getChapter()));
            lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(pLessons));
            startActivity(lObjIntent);
        } else
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
            displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_MINE, pLessons.getName(),
                    Arrays.asList(getResources().getString(R.string.action_edit),
                            getResources().getString(R.string.action_delete_lesson),
                            getResources().getString(R.string.action_upload)),
                    Arrays.asList(R.id.action_edit,
                            R.id.action_delete_lesson,
                            R.id.action_upload),
                    new Object[]{pLessons, pPostion});
        else
            displaySpinnerDialog(PotMacros.ON_INFO_LONG_CLICK_MINE, pLessons.getName(),
                    m_cUser.getRole().equalsIgnoreCase(PotMacros.ROLE_STUDENT) ?
                            Arrays.asList(getResources().getString(R.string.action_remove),
                                    getResources().getString(R.string.action_delete_lesson))
                            :
                            Arrays.asList(getResources().getString(R.string.action_remove),
                                    getResources().getString(R.string.action_delete_lesson)),
                    m_cUser.getRole().equalsIgnoreCase(PotMacros.ROLE_STUDENT) ?
                            Arrays.asList(R.id.action_remove,
                                    R.id.action_delete_lesson)
                            :
                            Arrays.asList(R.id.action_remove,
                                    R.id.action_delete_lesson),
                    new Object[]{pLessons, pPostion});
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

    }

    public class CheckIsNetWorkAvailable extends AsyncTask<String, Void, String> {
        private boolean isSucesses = false;
        private boolean m_cIsDialogReq = false;
        private Object m_cObj;

        public CheckIsNetWorkAvailable(boolean pIsDialogReq, Object pObj){
            this.m_cIsDialogReq = pIsDialogReq;
            this.m_cObj = pObj;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!m_cIsDialogReq) {
                m_cObjMainActivity.displayProgressBar(-1, "Loading...");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) m_cObjMainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            isSucesses =  activeNetworkInfo != null && activeNetworkInfo.isConnected();
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!m_cIsDialogReq) {
                m_cObjMainActivity.hideDialog();
            }
            if(isSucesses) {
                Message lMessage = new Message();
                lMessage.what = NOTIFICATION_FOR_NETWORK_CONNECTION_AVAILABLE_LOCALLY;
                lMessage.obj = m_cObj;
                m_cObjUIHandler.sendMessage(lMessage);
            } else {
                m_cObjMainActivity.displayToast("Please check network connection");
            }
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