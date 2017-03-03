package com.cosmicdew.lessonpot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.adapters.CustomRecyclerAdapterForLikesComments;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.interfaces.RecyclerLikeCommentListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Comments;
import com.cosmicdew.lessonpot.models.CommentsAll;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.Likes;
import com.cosmicdew.lessonpot.models.LikesAll;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 17/2/17.
 */

public class PotLessonsCommentsFragment extends PotFragmentBaseClass implements RecyclerLikeCommentListener {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    RelativeLayout m_cRlMain;

    @Nullable
    @BindView(R.id.RECYC_HOME_CLASS_BOARDS)
    RecyclerView m_cRecycClasses;

    @Nullable
    @BindView(R.id.COMMENTS_EDIT)
    EditText m_cBoxEdit;

    @Nullable
    @BindView(R.id.DONE_DIALOG_TXT)
    TextView m_cDone;

    @Nullable
    @BindView(R.id.COMMENTS_AD_EDIT_LL)
    LinearLayout m_cBoxLL;

    private int m_cPos;
    private String m_cKey;
    private Users m_cUser;
    private String mComment;
    private Comments m_cCommt;

    private String m_cUserMode;

    private Lessons m_cLessons;
    private ArrayList<Comments> m_cComments;

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForLikesComments m_cRecycClassesAdapt;

    private static RecyclerLikeCommentListener m_cRefreshListener;

    public PotLessonsCommentsFragment() {
        super();
    }

    public static PotLessonsCommentsFragment newInstance(int pPosition, String pKey, Users pUser,
                                                         Lessons pLessons,
                                                         CommentsAll pCommentsAll,
                                                         String pUserMode,
                                                         RecyclerLikeCommentListener pRecyclerLikeCommentListener) {
        PotLessonsCommentsFragment lPotLessonsCommentsFragment = new PotLessonsCommentsFragment();
        m_cRefreshListener = pRecyclerLikeCommentListener;

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("KEY", pKey);
        args.putString(PotMacros.OBJ_USER, (new Gson()).toJson(pUser));
        args.putString(PotMacros.OBJ_LESSON, (new Gson()).toJson(pLessons));
        args.putString(PotMacros.OBJ_LESSON_COMMENTS, (new Gson()).toJson(pCommentsAll));
        args.putString(PotMacros.USER_LIKES_COMMENTS, pUserMode);
        lPotLessonsCommentsFragment.setArguments(args);

        return lPotLessonsCommentsFragment;
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
        m_cObjMainView = inflater.inflate(R.layout.fragment_comments, container, false);
        ButterKnife.bind(this, m_cObjMainView);

        m_cObjMainActivity.m_cObjFragmentBase = PotLessonsCommentsFragment.this;

        return m_cObjMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        m_cComments = new ArrayList<>();
        m_cPos = getArguments().getInt("Position", 0);
        m_cKey = getArguments().getString("KEY");
        m_cUser = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_USER), Users.class);
        m_cLessons = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_LESSON), Lessons.class);
        m_cUserMode = getArguments().getString(PotMacros.USER_LIKES_COMMENTS);
        if (null != m_cUserMode) {
            m_cBoxLL.setVisibility(View.GONE);
        }else {
            for (Comments lComments : ((new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_LESSON_COMMENTS), CommentsAll.class).getComments()))
                m_cComments.add(lComments);
        }
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

        //init Comments here
        if (null != m_cUserMode)
            callComments(m_cUserMode);
        else
            initComments();
    }

    private void initComments() {
        if (null != m_cComments && m_cComments.size() > 0) {
            m_cRecycClassesAdapt = new CustomRecyclerAdapterForLikesComments(m_cObjMainActivity, m_cUser, null, m_cComments, false, m_cUserMode, this);
            m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
        } else {
            if (m_cRecycClassesAdapt != null) {
                m_cComments.clear();
                m_cRecycClasses.setAdapter(new CustomRecyclerAdapterForLikesComments(m_cObjMainActivity, m_cUser, null, m_cComments, false, m_cUserMode, this));
                m_cRecycClasses.invalidate();
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void callComments(String pUserMode) {
        if (null != pUserMode)
            placeUserRequest(Constants.LESSON_COMMENTS, CommentsAll.class, pUserMode, null, null, false);
        else
            placeUserRequest(Constants.LESSONS +
                    m_cLessons.getId() +
                    "/" +
                    Constants.LESSON_COMMENTS, CommentsAll.class, pUserMode, null, null, false);
    }

    private void callLikes(String objLessonLikes) {
        placeUserRequest(Constants.LESSONS +
                m_cLessons.getId() +
                "/" +
                Constants.LESSON_LIKES, LikesAll.class, objLessonLikes, null, null, false);
    }

    @Optional
    @OnClick({R.id.DONE_DIALOG_TXT})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.DONE_DIALOG_TXT:
                if (validate()) {
                    if (m_cCommt != null) {
                        Message lMsg = new Message();
                        lMsg.what = PotMacros.OBJ_LESSON_COMMENTS_EDIT;
                        lMsg.obj = new Object[]{m_cBoxEdit.getText().toString().trim(),
                                m_cCommt};
                        m_cObjUIHandler.sendMessage(lMsg);
                    } else {
                        Message lMsg = new Message();
                        lMsg.what = PotMacros.OBJ_LESSON_COMMENTS_ADD;
                        lMsg.obj = m_cBoxEdit.getText().toString().trim();
                        m_cObjUIHandler.sendMessage(lMsg);
                    }
                    m_cBoxEdit.setText(null);
                    m_cCommt = null;
                }
                break;
        }
    }

    public boolean validate() {
        boolean lRetVal = false;
        String letComm = m_cBoxEdit.getText().toString().trim();
        if (letComm.isEmpty()) {
            m_cObjMainActivity.displaySnack(m_cRlMain, "Enter Comment");
            return false;
        } else {
            lRetVal = true;
        }
        return lRetVal;
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what) {
            case PotMacros.OBJ_LESSON_COMMENTS_ADD:
                mComment = (String) pObjMessage.obj;
                if (mComment != null && !mComment.isEmpty()) {
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.COMMENT, mComment.toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    placeUserRequest(Constants.LESSONS +
                            m_cLessons.getId() +
                            "/" +
                            Constants.LESSON_COMMENTS, Comments.class, null, null, lJO.toString(), true);
                }
                break;
            case PotMacros.OBJ_LESSON_COMMENTS_SHOW:
                CommentsAll lCommentsAll = (CommentsAll) pObjMessage.obj;
                Comments lComments = lCommentsAll.getComments().get(pObjMessage.arg1);
                if (lComments.getUser().getId() == m_cUser.getId())
                    break;
            case PotMacros.OBJ_LESSON_COMMENTS_EDIT:
                Object[] lObjects = (Object[]) pObjMessage.obj;
                mComment = (String) lObjects[0];
                Comments lComment = (Comments) lObjects[1];
                if (null != mComment) {
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.COMMENT, mComment.toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    placeUnivUserRequest(Constants.LESSONS +
                            m_cLessons.getId() +
                            "/" +
                            Constants.LESSON_COMMENTS +
                            lComment.getId() +
                            "/", Comments.class, null, null, lJO.toString(), Request.Method.PATCH);
                } else {
                    placeUnivUserRequest(Constants.LESSONS +
                            m_cLessons.getId() +
                            "/" +
                            Constants.LESSON_COMMENTS +
                            lComment.getId() +
                            "/", Comments.class, null, null, null, Request.Method.DELETE);
                }
                break;
            case PotMacros.OBJ_LESSON_COMMENTS_SPAM:
                Object[] lObjectSpam = (Object[]) pObjMessage.obj;
                mComment = (String) lObjectSpam[0];
                Comments lCommentSpam = (Comments) lObjectSpam[1];
                if (null != mComment) {
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.IS_SPAM, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    placeUnivUserRequest(Constants.LESSONS +
                            m_cLessons.getId() +
                            "/" +
                            Constants.LESSON_COMMENTS +
                            lCommentSpam.getId() +
                            "/", Comments.class, null, null, lJO.toString(), Request.Method.PATCH);
                }
                break;
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.LESSON_COMMENTS)) {
                    if (response instanceof Comments) {
                        Comments lComments = (Comments) response;
                        callComments(m_cUserMode);
                    } else if (response == null) {
                        callComments(m_cUserMode);
                    } else if (response instanceof CommentsAll) {
                        CommentsAll lCommentsAll = (CommentsAll) response;
                        m_cComments.clear();
                        m_cRecycClasses.setAdapter(new CustomRecyclerAdapterForLikesComments(m_cObjMainActivity, m_cUser, null, m_cComments, false, m_cUserMode, this));
                        m_cRecycClasses.invalidate();
                        for (Comments lComments : lCommentsAll.getComments())
                            m_cComments.add(lComments);
                        initComments();
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
                if (apiMethod.contains(Constants.LESSON_COMMENTS)) {
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
    public void onInfoClick(int pPostion, Likes pLike, Comments pComments, boolean pMode, View pView) {

    }

    @Override
    public void onInfoLongClick(int pPostion, Likes pLike, Comments pComments, boolean pMode, int pState, View pView) {
        if (null == m_cUserMode) {
            if (pMode) {
            } else {
                switch (pState) {
                    case 0:
                        m_cBoxEdit.setText(pComments.getComment());
                        m_cCommt = pComments;
                        break;
                    case 1:
                        m_cCommt = pComments;
                        Message llMsg = new Message();
                        llMsg.what = PotMacros.OBJ_LESSON_COMMENTS_EDIT;
                        llMsg.obj = new Object[]{null,
                                pComments};
                        m_cObjUIHandler.sendMessage(llMsg);
                        break;
                    case 2:
                        m_cCommt = pComments;
                        Message lllMsg = new Message();
                        lllMsg.what = PotMacros.OBJ_LESSON_COMMENTS_SPAM;
                        lllMsg.obj = new Object[]{m_cCommt.getComment().toString().trim(),
                                pComments};
                        m_cObjUIHandler.sendMessage(lllMsg);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void resetFragment(boolean pState) {

    }

    public void onBackPressed() {
        m_cRefreshListener.resetFragment(false);
        m_cObjMainActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
