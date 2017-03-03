package com.cosmicdew.lessonpot.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.fragments.PotLessonsCommentsFragment;
import com.cosmicdew.lessonpot.fragments.PotLessonsLikesFragment;
import com.cosmicdew.lessonpot.interfaces.RecyclerLikeCommentListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Comments;
import com.cosmicdew.lessonpot.models.CommentsAll;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.Likes;
import com.cosmicdew.lessonpot.models.LikesAll;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by S.K. Pissay on 17/2/17.
 */

public class LessonLikesCommentsScreen extends PotBaseActivity implements RecyclerLikeCommentListener {

    @Nullable
    @BindView(R.id.toolbar_home)
    Toolbar m_cToolBar;

    @Nullable
    @BindView(R.id.header_view_title)
    TextView m_cHeaderTitle;

    @Nullable
    @BindView(R.id.header_view_sub_title)
    TextView m_cHeaderSubTitle;

    @Nullable
    @BindView(R.id.CONTAINER_FL)
    FrameLayout m_cMainRL;

    private Users m_cUser;
    private BoardChoices m_cBoardChoices;
    private Syllabi m_cSyllabi;

    private Lessons m_cLessons;
    private LikesAll m_cLikeAll;
    private CommentsAll m_cCommentsAll;
    private boolean m_cMode;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.lesson_mapping_screen);
        ButterKnife.bind(this);

        m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);
        m_cLessons = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_LESSON), Lessons.class);
        m_cLikeAll =  (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_LESSON_LIKES), LikesAll.class);
        m_cCommentsAll = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_LESSON_COMMENTS), CommentsAll.class);
        m_cMode = getIntent().getBooleanExtra(PotMacros.OBJ_SELECTIONTYPE, false);

        if (m_cToolBar != null) {
            setSupportActionBar(m_cToolBar);
            /*getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setTitle(getResources().getString(R.string.library_txt));
            m_cToolBar.setSubtitle(lsub);*/
            if (m_cMode)
                m_cHeaderTitle.setText(getResources().getString(R.string.lesson_likes_txt));
            else
                m_cHeaderTitle.setText(getResources().getString(R.string.lesson_comments_txt));
            m_cHeaderSubTitle.setText(m_cLessons.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    private void init() {
        if (m_cMode)
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.CONTAINER_FL, new PotLessonsLikesFragment().newInstance(-1, null, m_cUser, m_cLessons, m_cLikeAll, null, this), PotMacros.FRAG_LIKES)
                    .commit();
        else
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.CONTAINER_FL, new PotLessonsCommentsFragment().newInstance(-1, null, m_cUser, m_cLessons, m_cCommentsAll, null, this), PotMacros.FRAG_COMMENTS)
                    .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent lObjIntent;
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

    }

    @Override
    public void onBackPressed() {
        Intent lReturnIntent;
        if (getSupportFragmentManager().findFragmentByTag(PotMacros.FRAG_LIKES) != null) {
            ((PotLessonsLikesFragment) getSupportFragmentManager().findFragmentByTag(PotMacros.FRAG_LIKES)).onBackPressed();
        } else if (getSupportFragmentManager().findFragmentByTag(PotMacros.FRAG_COMMENTS) != null) {
            ((PotLessonsCommentsFragment) getSupportFragmentManager().findFragmentByTag(PotMacros.FRAG_COMMENTS)).onBackPressed();
        }
        lReturnIntent = new Intent();
        setResult(Activity.RESULT_OK, lReturnIntent);
        finish();
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
}
