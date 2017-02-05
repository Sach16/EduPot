package com.cosmicdew.lessonpot.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.fragments.PotUserHomeChapterFragment;
import com.cosmicdew.lessonpot.fragments.PotUserHomeClassesFragment;
import com.cosmicdew.lessonpot.fragments.PotUserHomeSubjectFragment;
import com.cosmicdew.lessonpot.interfaces.RecyclerSelectionListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cosmicdew.lessonpot.macros.PotMacros.FRAG_CHAPTER;
import static com.cosmicdew.lessonpot.macros.PotMacros.FRAG_CLASSES;
import static com.cosmicdew.lessonpot.macros.PotMacros.FRAG_SUBJECTS;

/**
 * Created by S.K. Pissay on 26/12/16.
 */

public class AddSyllabusScreen extends PotBaseActivity implements RecyclerSelectionListener {

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
    private Users m_cLoggedInUser;
    private BoardChoices m_cBoardChoices;
    private Syllabi m_cSyllabi;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.lesson_mapping_screen);
        ButterKnife.bind(this);

        m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);
        m_cLoggedInUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER_MAIN), Users.class);

        if (m_cToolBar != null){
            setSupportActionBar(m_cToolBar);
            /*getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setTitle(getResources().getString(R.string.library_txt));
            m_cToolBar.setSubtitle(lsub);*/
            m_cHeaderTitle.setText(getResources().getString(R.string.select_board_class));
            m_cHeaderSubTitle.setText(m_cUser.getFirstName() + " " + m_cUser.getLastName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    private void init() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.CONTAINER_FL, new PotUserHomeClassesFragment().newInstance(-1, null, m_cUser, PotMacros.OBJ_SELECTIONTYPE_ADDSYLLABUS, this, null), FRAG_CLASSES)
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
        if (getSupportFragmentManager().findFragmentByTag(FRAG_CHAPTER) != null) {
            ((PotUserHomeChapterFragment) getSupportFragmentManager().findFragmentByTag(FRAG_CHAPTER)).onBackPressed();
            switchModeFragment(false, 1);
        } else if (getSupportFragmentManager().findFragmentByTag(FRAG_SUBJECTS) != null) {
            ((PotUserHomeSubjectFragment) getSupportFragmentManager().findFragmentByTag(FRAG_SUBJECTS)).onBackPressed();
            switchModeFragment(false, 0);
        } else if (getSupportFragmentManager().findFragmentByTag(FRAG_CLASSES) != null) {
            ((PotUserHomeClassesFragment) getSupportFragmentManager().findFragmentByTag(FRAG_CLASSES)).onBackPressed();
            super.onBackPressed();
        }
    }

    private void switchModeFragment(boolean b, int i) {
        switch (i){
            case 0:
                m_cHeaderTitle.setText(getResources().getString(R.string.select_board_class));
                m_cHeaderSubTitle.setText(m_cUser.getFirstName() + " " + m_cUser.getLastName());
                break;
            case 1:
                String lsub = String.format("%s %s",
                        m_cBoardChoices.getBoardclass().getName(),
                        m_cBoardChoices.getBoardclass().getBoard().getName());
                m_cHeaderTitle.setText(getResources().getString(R.string.select_syllabus));
                m_cHeaderSubTitle.setText(lsub);
                break;
            case 2:
                m_cHeaderTitle.setText(getResources().getString(R.string.select_chapter));
                m_cHeaderSubTitle.setText(m_cSyllabi.getName());
                break;
        }
    }

    @Override
    public void onInfoClicked(int pPostion, BoardChoices pBoardChoices, Syllabi pSyllabi, Chapters pChapters, Lessons pLessons, String pFragID) {
        Intent lReturnIntent;
        switch (pFragID) {
            case FRAG_CLASSES:
                m_cBoardChoices = pBoardChoices;
                if (!pBoardChoices.getBoardclass().getIsGeneric()) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.CONTAINER_FL, new PotUserHomeSubjectFragment().newInstance(-1, null, m_cUser, pBoardChoices, PotMacros.OBJ_SELECTIONTYPE_ADDSYLLABUS, this, null), PotMacros.FRAG_SUBJECTS)
                            .commit();
                    switchModeFragment(false, 1);
                } else {
                    lReturnIntent = new Intent();
                    lReturnIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_BOARDCHOICES);
                    lReturnIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                    lReturnIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(pBoardChoices));
                    setResult(Activity.RESULT_OK, lReturnIntent);
                    ((PotUserHomeClassesFragment) getSupportFragmentManager().findFragmentByTag(FRAG_CLASSES)).onBackPressed();
                    finish();
                }
                break;
            case PotMacros.FRAG_SUBJECTS:
                m_cSyllabi = pSyllabi;
                if (!pSyllabi.getIsGeneric()) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.CONTAINER_FL, new PotUserHomeChapterFragment().newInstance(-1, null, m_cUser, pBoardChoices, pSyllabi, PotMacros.OBJ_SELECTIONTYPE_ADDSYLLABUS, this, null), PotMacros.FRAG_CHAPTER)
                            .commit();
                    switchModeFragment(false, 2);
                } else {
                    lReturnIntent = new Intent();
                    lReturnIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_SYLLABI);
                    lReturnIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                    lReturnIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(pBoardChoices));
                    lReturnIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(pSyllabi));
                    setResult(Activity.RESULT_OK, lReturnIntent);
                    ((PotUserHomeSubjectFragment) getSupportFragmentManager().findFragmentByTag(FRAG_SUBJECTS)).onBackPressed();
                    ((PotUserHomeClassesFragment) getSupportFragmentManager().findFragmentByTag(FRAG_CLASSES)).onBackPressed();
                    finish();
                }
                break;
            case PotMacros.FRAG_CHAPTER:
                lReturnIntent = new Intent();
                setResult(Activity.RESULT_OK, lReturnIntent);
                switchModeFragment(false, 1);
                ((PotUserHomeChapterFragment) getSupportFragmentManager().findFragmentByTag(FRAG_CHAPTER)).onBackPressed();
//                ((PotUserHomeSubjectFragment) getSupportFragmentManager().findFragmentByTag(FRAG_SUBJECTS)).onBackPressed();
//                ((PotUserHomeClassesFragment) getSupportFragmentManager().findFragmentByTag(FRAG_CLASSES)).onBackPressed();
//                finish();
                break;
        }
    }
}
