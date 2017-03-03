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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by S.K. Pissay on 16/2/17.
 */

public class PotLessonsLikesFragment extends PotFragmentBaseClass implements RecyclerLikeCommentListener {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    RelativeLayout m_cRlMain;

    @Nullable
    @BindView(R.id.RECYC_HOME_CLASS_BOARDS)
    RecyclerView m_cRecycClasses;

    private int m_cPos;
    private String m_cKey;
    private Users m_cUser;
    private String m_cUserMode;

    private Lessons m_cLessons;
    private ArrayList<Likes> m_cLikes;

    private boolean m_cLoading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForLikesComments m_cRecycClassesAdapt;

    private static RecyclerLikeCommentListener m_cRefreshListener;

    public PotLessonsLikesFragment() {
        super();
    }

    public static PotLessonsLikesFragment newInstance(int pPosition, String pKey, Users pUser,
                                                      Lessons pLessons,
                                                      LikesAll pLikesAll,
                                                      String pUserMode,
                                                      RecyclerLikeCommentListener pRecyclerLikeCommentListener) {
        PotLessonsLikesFragment lPotLessonsLikesFragment = new PotLessonsLikesFragment();
        m_cRefreshListener = pRecyclerLikeCommentListener;

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("KEY", pKey);
        args.putString(PotMacros.OBJ_USER, (new Gson()).toJson(pUser));
        args.putString(PotMacros.OBJ_LESSON, (new Gson()).toJson(pLessons));
        args.putString(PotMacros.OBJ_LESSON_LIKES, (new Gson()).toJson(pLikesAll));
        args.putString(PotMacros.USER_LIKES_COMMENTS, pUserMode);
        lPotLessonsLikesFragment.setArguments(args);

        return lPotLessonsLikesFragment;
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

        m_cObjMainActivity.m_cObjFragmentBase = PotLessonsLikesFragment.this;

        return m_cObjMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        m_cLikes = new ArrayList<>();
        m_cPos = getArguments().getInt("Position", 0);
        m_cKey = getArguments().getString("KEY");
        m_cUser = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_USER), Users.class);
        m_cLessons = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_LESSON), Lessons.class);
        m_cUserMode = getArguments().getString(PotMacros.USER_LIKES_COMMENTS);
        if (null == m_cUserMode)
            for (Likes likes : ((new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_LESSON_LIKES), LikesAll.class).getLikes()))
                m_cLikes.add(likes);
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

        //init likes here
        if (null != m_cUserMode)
            callLikes(m_cUserMode);
        else
            initLikes();
    }

    private void initLikes() {
        if (null != m_cLikes && m_cLikes.size() > 0) {
            m_cRecycClassesAdapt = new CustomRecyclerAdapterForLikesComments(m_cObjMainActivity, m_cUser, m_cLikes, null, true, m_cUserMode, this);
            m_cRecycClasses.setAdapter(m_cRecycClassesAdapt);
        } else {
            if (m_cRecycClassesAdapt != null) {
                m_cLikes.clear();
                m_cRecycClasses.setAdapter(new CustomRecyclerAdapterForLikesComments(m_cObjMainActivity, m_cUser, m_cLikes, null, true, m_cUserMode, this));
                m_cRecycClasses.invalidate();
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void callComments(String objLessonComments) {
        placeUserRequest(Constants.LESSONS +
                m_cLessons.getId() +
                "/" +
                Constants.LESSON_COMMENTS, CommentsAll.class, objLessonComments, null, null, false);
    }

    private void callLikes(String pUserMode) {
        if (null != pUserMode)
            placeUserRequest(Constants.LESSON_LIKES, LikesAll.class, pUserMode, null, null, false);
        else
            placeUserRequest(Constants.LESSONS +
                    m_cLessons.getId() +
                    "/" +
                    Constants.LESSON_LIKES, LikesAll.class, pUserMode, null, null, false);
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {

    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.LESSON_LIKES)) {
                    if (response instanceof Likes) {
                        Likes likes = (Likes) response;
                        callLikes(m_cUserMode);
                    } else if (response instanceof LikesAll) {
                        LikesAll likesAll = (LikesAll) response;
                        for (Likes likes : likesAll.getLikes())
                            m_cLikes.add(likes);
                        initLikes();
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
                if (apiMethod.contains(Constants.LESSON_LIKES)) {
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

    }

    @Override
    public void resetFragment(boolean pState) {

    }

    public void onBackPressed() {
        m_cRefreshListener.resetFragment(false);
        m_cObjMainActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
