package com.cosmicdew.lessonpot.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.adapters.ScreenSlidePagerAdapter;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.customviews.RoundedCornersTransformation;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Attachments;
import com.cosmicdew.lessonpot.models.AttachmentsAll;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.Comments;
import com.cosmicdew.lessonpot.models.CommentsAll;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.LessonViews;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.LessonsTable;
import com.cosmicdew.lessonpot.models.Likes;
import com.cosmicdew.lessonpot.models.LikesAll;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.models.UsersAll;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.cosmicdew.lessonpot.utils.Utilities;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTouch;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 17/10/16.
 */

public class LessonsScreen extends PotBaseActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener,
        CompoundButton.OnCheckedChangeListener, View.OnTouchListener{

    private static final long MAX_TIME_MILLIS = 480000;
    private static final int RECORD_SEEK = 99;
    private static final int PLAY_SEEK = 89;
    private static final String MEDIA_TAG = "MEDIA_TAG:";

    public static final int TAKE_PICTURE = 101;
    public static final int LESSON_MAPPING = 110;
    public static final int PROCESS_PICTURE = 111;
    public static final int LESSON_LIKES_COMMENT = 121;

    @Nullable
    @BindView(R.id.toolbar_home)
    Toolbar m_cToolBar;

    @Nullable
    @BindView(R.id.TITLE_TXT)
    TextView m_cTitleTxt;

    @Nullable
    @BindView(R.id.MAIN_RL)
    CoordinatorLayout m_cRlMain;

    @Nullable
    @BindView(R.id.LESSON_TITLE_TXT)
    EditText mLessonTitleEdit;

    @Nullable
    @BindView(R.id.REC_PAUSE_RES_IMG)
    ImageView mRecPauseResImg;

    @Nullable
    @BindView(R.id.PLAY_PAUSE_RESUM_IMG)
    ImageView mPlayPauseResImg;

    @Nullable
    @BindView(R.id.SEEKBAR)
    SeekBar mSeekBar;

    @Nullable
    @BindView(R.id.IMAGES_TITLE_TXT)
    TextView commentBox;

    @Nullable
    @BindView(R.id.LESSON_IMG_1)
    ImageView mLessonImg1;

    @Nullable
    @BindView(R.id.LESSON_IMG_2)
    ImageView mLessonImg2;

    @Nullable
    @BindView(R.id.LESSON_IMG_3)
    ImageView mLessonImg3;

    @Nullable
    @BindView(R.id.PROGRESS_TIMER_TXT)
    TextView progressTimerTxt;

    @Nullable
    @BindView(R.id.FIXED_TIMER_TXT)
    TextView fixedTimerTxt;

    @Nullable
    @BindView(R.id.LESSON_LOCATION_TXT)
    TextView lessonLocationTxt;

    @Nullable
    @BindView(R.id.TITLE_LL)
    LinearLayout lessonTitleLL;

    @Nullable
    @BindView(R.id.DELETE_MEDIA_IMG)
    ImageView deleteMediaImg;

    @Nullable
    @BindView(R.id.MEDIA_PLAYER_LL)
    LinearLayout mediaPlayerLL;

    @Nullable
    @BindView(R.id.LESSON_LOCATION_RL)
    RelativeLayout lessonLocRL;

    @Nullable
    @BindView(R.id.OWNER_TXT)
    TextView ownerTxt;

    @Nullable
    @BindView(R.id.SHARED_BY_TXT)
    TextView sharedByTxt;

    @Nullable
    @BindView(R.id.NUM_OF_LISTENS_TXT)
    TextView numbOfListensTxt;

    @Nullable
    @BindView(R.id.LAST_LISTENED_TXT)
    TextView lastListensTxt;

    @Nullable
    @BindView(R.id.SAVE_CANCEL_LL)
    LinearLayout saveCancelLL;

    @Nullable
    @BindView(R.id.PLAY_PROGRESS_BAR)
    ProgressBar playProgressBar;

    @Nullable
    @BindView(R.id.SAVE_LESSON_TXT)
    TextView saveTxt;

    @Nullable
    @BindView(R.id.CANCEL_LESSON_TXT)
    TextView cancelTxt;

    @Nullable
    @BindView(R.id.BOTTOM_VIEW)
    FrameLayout viewStub;

    @Nullable
    @BindView(R.id.LIKE_COMMENT_LL)
    LinearLayout likeCommentLL;

    @Nullable
    @BindView(R.id.LIKE_TXT)
    TextView likeTxt;

    @Nullable
    @BindView(R.id.COMMENT_TXT)
    TextView commentTxt;

    @Nullable
    @BindView(R.id.LIKE_LIST_TXT)
    TextView likeListTxt;

    @Nullable
    @BindView(R.id.COMMENT_LIST_TXT)
    TextView commentListTxt;

    @Nullable
    @BindView(R.id.LESSON_SWITCH_LL)
    LinearLayout lessonSwitchLl;

    @Nullable
    @BindView(R.id.SHARES_SWITCH)
    Switch m_cSharesSwch;

    @Nullable
    @BindView(R.id.OFFLINE_SWITCH)
    Switch m_cOfflineSwch;

    //inner views of bottom_view

    @Nullable
    @BindView(R.id.IMG_PAGER)
    ViewPager imgPager;

    @Nullable
    @BindView(R.id.PAGER_DELETE_IMG)
    ImageView deletePagerImg;

    BottomSheetBehavior<FrameLayout> m_cSlider;
    ScreenSlidePagerAdapter m_cScreenSlidePagerAdapter;

    private Users m_cUser;
    private BoardChoices m_cBoardChoice;
    private Syllabi m_cSyllabi;
    private Chapters m_cChapters;
    private Lessons m_cLessons;
    private LessonShares m_cLessonShares;
    private LessonViews m_cLessonViews;
    private int mLessonID;

    private String mNotes;
    private String mComment;

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private int length;
    private int count;

    private long m_cStartRecTime;
    boolean m_cImageProcessing;
    private Bitmap m_cObjSelectedBitMap;
    private int m_cCurrentImgId;

    private boolean mIsRecording = false;
    private boolean mIsPlaying = false;
    private int m_cLessType;
    private String mLessFromWhere;
    private String mLessFromWhereWchTab;
    private int mRequestResultIndex;
    private boolean mIsLessonMapped = false;
    private String m_cAudioUrl;
    private HashMap<String, Attachments> m_cAttachList;

    private LessonsTable m_cLessonsTable;

    private boolean m_cIsShareTouched;
    private boolean m_cIsOfflineTouched;
    private boolean m_cIsTitleTouched;

    private boolean m_cIsSharable = true;
    private boolean m_cIsOfflinable = true;

    private boolean m_cIsEdited;
    private boolean m_cIsPrepared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_screen_main);
        ButterKnife.bind(this);


        if (m_cToolBar != null){
            setSupportActionBar(m_cToolBar);
            m_cTitleTxt.setText(getResources().getString(R.string.add_lesson_txt));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        m_cAttachList = new HashMap<>();
        m_cLessType = getIntent().getIntExtra(PotMacros.OBJ_LESSON_TYPE, -1);
        mLessFromWhere = getIntent().getStringExtra(PotMacros.OBJ_LESSONFROM);
        mLessFromWhereWchTab = getIntent().getStringExtra(PotMacros.OBJ_LESSONFROMWCHTAB);
        mRequestResultIndex = getIntent().getIntExtra(PotMacros.LESSON_INDEX_PAGE, 0);
        m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);
        m_cBoardChoice = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_BOARDCHOICES), BoardChoices.class);
        m_cSyllabi = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_SYLLABI), Syllabi.class);
        m_cChapters = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_CHAPTERS), Chapters.class);
        m_cLessons = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_LESSON), Lessons.class);
        m_cLessonShares = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_LESSONSHARES), LessonShares.class);
        m_cLessonViews = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_LESSONVIEWS), LessonViews.class);
        mSeekBar.setOnSeekBarChangeListener(this);
//        mSeekBar.setPadding(3, 3, 3, 3);
        m_cAudioGUID = PotMacros.getGUID();

        init();
    }

    private void init() {
        switch (m_cLessType) {
            case PotMacros.OBJ_LESSON_NEW:
                ButterKnife.apply(mPlayPauseResImg, PotMacros.SETENABLED, false);
                m_cSharesSwch.setChecked(m_cUser.getSharable());
                m_cIsSharable = m_cUser.getSharable();
                m_cOfflineSwch.setChecked(m_cUser.getOfflineable());
                m_cIsOfflinable = m_cUser.getOfflineable();
                setViewMode();
                switch (mLessFromWhere) {
                    case PotMacros.OBJ_BOARDCHOICES:
                        lessonLocationTxt.setText(String.format("%s, %s", m_cBoardChoice.getBoardclass().getBoard().getName(),
                                m_cBoardChoice.getBoardclass().getName()));
                        break;
                    case PotMacros.OBJ_SYLLABI:
                        lessonLocationTxt.setText(String.format("%s, %s, %s", m_cBoardChoice.getBoardclass().getBoard().getName(),
                                m_cBoardChoice.getBoardclass().getName(),
                                m_cSyllabi.getName()));
                        break;
                    case PotMacros.OBJ_CHAPTERS:
                        lessonLocationTxt.setText(String.format("%s, %s, %s, \n%s", m_cBoardChoice.getBoardclass().getBoard().getName(),
                                m_cBoardChoice.getBoardclass().getName(),
                                m_cSyllabi.getName(),
                                m_cChapters.getName()));
                        break;
                    case PotMacros.OBJ_LESSON:
                        lessonLocationTxt.setText(String.format("%s, %s, %s, \n%s", m_cBoardChoice.getBoardclass().getBoard().getName(),
                                m_cBoardChoice.getBoardclass().getName(),
                                m_cSyllabi.getName(),
                                m_cChapters.getName()));
                        break;
                }
                break;
            case PotMacros.OBJ_LESSON_VIEW:
            case PotMacros.OBJ_LESSON_EDIT:
                mLessonID = m_cLessons.getId();
                m_cTitleTxt.setText(m_cLessons.getName());
                m_cSharesSwch.setChecked(m_cLessons.getSharable());
                m_cIsSharable = m_cLessons.getSharable();
                m_cOfflineSwch.setChecked(m_cLessons.getOfflineable());
                m_cIsOfflinable = m_cLessons.getOfflineable();
                ButterKnife.apply(mPlayPauseResImg, PotMacros.SETENABLED, true);
                //uncomment if required
//                ButterKnife.apply(mRecPauseResImg, PotMacros.SETENABLED, false);
                setViewMode();
                if (!mIsLessonMapped) {
                    mLessonTitleEdit.setText(m_cLessons.getName());
                    mLessonTitleEdit.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            Log.v("Text", "b4");
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            Log.v("Text", "on");
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            Log.v("Text", "af");
                            if (m_cIsTitleTouched) {
                                m_cIsEdited = true;
                                m_cIsTitleTouched = false;
                            }
                        }
                    });
                    mNotes = m_cLessons.getComments();
                    if (mNotes.length() > 0) {
                        commentBox.setVisibility(View.VISIBLE);
                    }
                }
                String llessonSource = "";
                switch (mLessFromWhereWchTab){
                    case PotMacros.OBJ_LESSON_MINE_TAB:
                    case PotMacros.OBJ_LESSON_PUBLIC_TAB:
                        llessonSource = m_cLessons.getOwner().getFirstName() + " " + m_cLessons.getOwner().getLastName();
                        break;
                    case PotMacros.OBJ_LESSON_RECEIVED_TAB:
                        llessonSource = m_cLessonShares.getFromUser().getFirstName() + " " + m_cLessonShares.getFromUser().getLastName();
                        break;
                    case PotMacros.OBJ_LESSON_VIEWED_TAB:
                        llessonSource = m_cLessonViews.getSource().getFirstName() + " " + m_cLessonViews.getSource().getLastName();
                        break;
                }
                ownerTxt.setText(String.format("Creator : %s", m_cLessons.getOwner().getFirstName() + " " +
                        m_cLessons.getOwner().getLastName()));
                sharedByTxt.setText(String.format("Shared By : %s", llessonSource));
                numbOfListensTxt.setText(String.format("Total No. of Views : %d", m_cLessons.getViews()));
                lastListensTxt.setText(String.format("Created/Modified Date : %s", PotMacros.getDateFormat(null, m_cLessons.getModified(),
                        PotMacros.DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS_SSSSSS, PotMacros.DATE_FORMAT_MMMDDYYYY)));
                lessonLocationTxt.setText(String.format("%s, %s, %s, \n%s",
                        m_cLessons.getChapter().getSyllabus().getBoardclass().getBoard().getName(),
                        m_cLessons.getChapter().getSyllabus().getBoardclass().getName(),
                        m_cLessons.getChapter().getSyllabus().getName(),
                        m_cLessons.getChapter().getName()));

                //called when mapping is done
                if (mIsLessonMapped) {
                    switch (mLessFromWhere) {
                        case PotMacros.OBJ_BOARDCHOICES:
                            lessonLocationTxt.setText(String.format("%s, %s", m_cBoardChoice.getBoardclass().getBoard().getName(),
                                    m_cBoardChoice.getBoardclass().getName()));
                            break;
                        case PotMacros.OBJ_SYLLABI:
                            lessonLocationTxt.setText(String.format("%s, %s, %s", m_cBoardChoice.getBoardclass().getBoard().getName(),
                                    m_cBoardChoice.getBoardclass().getName(),
                                    m_cSyllabi.getName()));
                            break;
                        case PotMacros.OBJ_CHAPTERS:
                            lessonLocationTxt.setText(String.format("%s, %s, %s, \n%s", m_cBoardChoice.getBoardclass().getBoard().getName(),
                                    m_cBoardChoice.getBoardclass().getName(),
                                    m_cSyllabi.getName(),
                                    m_cChapters.getName()));
                            break;
                        case PotMacros.OBJ_LESSON:
                            lessonLocationTxt.setText(String.format("%s, %s, %s, \n%s", m_cBoardChoice.getBoardclass().getBoard().getName(),
                                    m_cBoardChoice.getBoardclass().getName(),
                                    m_cSyllabi.getName(),
                                    m_cChapters.getName()));
                            break;
                    }
                    //check for quit
                    break;
                }

                //calling attachments api
                if (!mIsLessonMapped) {
                    displayProgressBar(-1, "Loading...");
                    RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                            m_cLessons.getId() +
                            "/" +
                            Constants.ATTACHMENTS, AttachmentsAll.class, this, null, null, null, false);
                }
                break;
        }
    }

    private void setViewMode() {
        switch (m_cLessType) {
            case PotMacros.OBJ_LESSON_VIEW:
                mRecPauseResImg.setVisibility(View.GONE);
                lessonTitleLL.setVisibility(View.GONE);
                deleteMediaImg.setVisibility(View.GONE);
                lessonLocRL.setVisibility(View.GONE);
                saveCancelLL.setVisibility(View.GONE);
                lessonSwitchLl.setVisibility(View.GONE);
                likeCommentLL.setVisibility(View.VISIBLE);
                callLikes(null);
                callComments(null);
                commentBox.setVisibility(View.GONE);
                mLessonImg1.setVisibility(View.GONE);
                mLessonImg2.setVisibility(View.GONE);
                mLessonImg3.setVisibility(View.GONE);
                mediaPlayerLL.setVisibility(View.GONE);
                /*LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ownerTxt.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                ownerTxt.setLayoutParams(params);*/
                if (!mIsLessonMapped)
                    checkWhereToMap();
                break;
            case PotMacros.OBJ_LESSON_NEW:
                ownerTxt.setVisibility(View.GONE);
                sharedByTxt.setVisibility(View.GONE);
                numbOfListensTxt.setVisibility(View.GONE);
                lastListensTxt.setVisibility(View.GONE);
                likeCommentLL.setVisibility(View.GONE);
                break;
            case PotMacros.OBJ_LESSON_EDIT:
                ownerTxt.setVisibility(View.GONE);
                sharedByTxt.setVisibility(View.GONE);
                numbOfListensTxt.setVisibility(View.GONE);
                lastListensTxt.setVisibility(View.GONE);
                likeCommentLL.setVisibility(View.GONE);
                mRecPauseResImg.setVisibility(View.VISIBLE);
                lessonTitleLL.setVisibility(View.VISIBLE);
                deleteMediaImg.setVisibility(View.VISIBLE);
                lessonLocRL.setVisibility(View.VISIBLE);
                saveCancelLL.setVisibility(View.VISIBLE);
                commentBox.setVisibility(View.VISIBLE);
                mLessonImg1.setVisibility(View.VISIBLE);
                mLessonImg2.setVisibility(View.VISIBLE);
                mLessonImg3.setVisibility(View.VISIBLE);
                mediaPlayerLL.setVisibility(View.VISIBLE);
                lessonSwitchLl.setVisibility(View.VISIBLE);
                m_cTitleTxt.setText(getResources().getString(R.string.edit_lesson_txt));
                if (!mIsLessonMapped)
                    checkWhereToMap();
                    //refreshing the flags
                m_cSharesSwch.setChecked(m_cLessons.getSharable());
                m_cIsSharable = m_cLessons.getSharable();
                m_cOfflineSwch.setChecked(m_cLessons.getOfflineable());
                m_cIsOfflinable = m_cLessons.getOfflineable();
                break;
        }
    }

    private void checkWhereToMap() {
        if (m_cLessons.getChapter().getSyllabus().getBoardclass().getIsGeneric()) {
            mLessFromWhere = PotMacros.OBJ_BOARDCHOICES;
        } else if (m_cLessons.getChapter().getSyllabus().getIsGeneric()) {
            mLessFromWhere = PotMacros.OBJ_SYLLABI;
        } else if (m_cLessons.getChapter().getIsGeneric()) {
            mLessFromWhere = PotMacros.OBJ_CHAPTERS;
        } else {
            mLessFromWhere = PotMacros.OBJ_LESSON;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent lObjIntent;
        switch (item.getItemId()) {
            case R.id.action_share:
                lObjIntent = new Intent(this, ShareScreen.class);
                lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, mLessFromWhere);
                lObjIntent.putExtra(PotMacros.OBJ_LESSONFROMWCHTAB, mLessFromWhereWchTab.equals(PotMacros.OBJ_LESSON_PUBLIC_TAB) ?
                        PotMacros.OBJ_LESSON_MINE_TAB : mLessFromWhereWchTab);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(m_cLessons));
                lObjIntent.putExtra(PotMacros.OBJ_LESSONSHARES, (new Gson()).toJson(m_cLessonShares));
                lObjIntent.putExtra(PotMacros.OBJ_LESSONVIEWS, (new Gson()).toJson(m_cLessonViews));
                startActivity(lObjIntent);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit:
                m_cLessType = PotMacros.OBJ_LESSON_EDIT;
                setViewMode();
                invalidateOptionsMenu();
                return true;
            case R.id.action_delete_lesson:
                displayYesOrNoCustAlert(PotMacros.ACTION_DELETE_LESSON,
                        getResources().getString(R.string.action_delete_lesson),
                        getResources().getString(R.string.delete_online_lesson_desc_txt),
                        m_cLessons);
                return true;
            case R.id.action_remove:
                displayProgressBar(-1, "");
                int lSourceId = -1;
                switch (mLessFromWhereWchTab) {
                    case PotMacros.OBJ_LESSON_RECEIVED_TAB:
                        lSourceId = m_cLessonShares.getFromUser().getId();
                        break;
                    case PotMacros.OBJ_LESSON_VIEWED_TAB:
                        lSourceId = m_cLessonViews.getSource().getId();
                        break;
                }
                RequestManager.getInstance(this).placeDeleteRequest(Constants.LESSONS +
                                m_cLessons.getId() +
                                "/" +
                                Constants.SOURCES +
                                lSourceId +
                                "/",
                        Attachments.class, this, null, null, null, true);
                return true;
            case R.id.action_add_syllabus:
                displayProgressBar(-1, "");
                RequestManager.getInstance(this).placeUserRequest(Constants.BOARDCLASSES +
                                m_cLessons.getChapter().getSyllabus().getBoardclass().getId() +
                                "/" +
                                Constants.SYLLABI +
                                m_cLessons.getChapter().getSyllabus().getId() +
                                "/"
                        , Syllabi.class, this, null, null, null, true);
                return true;
            case R.id.action_save:
                if (checkAndUpdateLessons()) {
                    if (m_cAttachList.size() > 0)
                        displayProgressBar(-1, "");
                    else
                        displayToast(getResources().getString(R.string.lesson_saved_successfully_txt));
                    LinkedHashMap<String, Attachments> linkedHashMap = new LinkedHashMap<>(m_cAttachList);
                    for (int i = 0; i < m_cAttachList.size(); i++) {
                        RequestManager.getInstance(this).placeStreamRequest((new ArrayList<Attachments>(linkedHashMap.values())).get(i).getAttachment(),
                                Constants.OFFLINE_META,
                                Attachments.class, this,
                                (new ArrayList<Attachments>(linkedHashMap.values())).get(i).getAttachmentType().equals(PotMacros.LESSON_AUDIO) ?
                                        new Object[]{(new ArrayList<Attachments>(linkedHashMap.values())).get(i).getAttachmentType(), PotMacros.getOfflineAudioFilePath(this) + "/" + PotMacros.getGUID() + ".aac",
                                                i == (m_cAttachList.size() - 1) ? (m_cAttachList.size() - 1) : -1} :
                                        new Object[]{(new ArrayList<Attachments>(linkedHashMap.values())).get(i).getAttachmentType(), PotMacros.getOfflineImageFilePath(this) + "/" + PotMacros.getGUID() + ".jpg",
                                                i == (m_cAttachList.size() - 1) ? (m_cAttachList.size() - 1) : -1},
                                null, null, false);
                    }
                }
                return true;
            case R.id.action_post_to_public:
            case R.id.action_extend_post_to_public:
                if (m_cLessons.getSharable()){
                    callLessonFlags(true, 2);
                }else {
                    displayYesOrNoCustAlert(R.id.action_post_to_public,
                            getResources().getString(R.string.permission_txt),
                            getResources().getString(R.string.lesson_sharable_permission_txt),
                            null);
                }
                return true;
            case R.id.action_unpost_from_public:
                if (m_cLessons.getSharable()){
                    callLessonFlags(false, 2);
                }else {
                    displayYesOrNoCustAlert(R.id.action_post_to_public,
                            getResources().getString(R.string.permission_txt),
                            getResources().getString(R.string.lesson_sharable_permission_txt),
                            null);
                }
                return true;
            case R.id.action_post_to_connections:
                displayProgressBar(-1, "");
                RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                                m_cLessons.getId() +
                                "/" +
                                Constants.POST,
                        Lessons.class, this, getResources().getString(R.string.lesson_posted_successfully_txt), null, null, true);

                return true;
            case R.id.action_extend_post_to_followers:
            case R.id.action_post_to_followers_and_connections:
                displayProgressBar(-1, "");
                JSONObject lJO = new JSONObject();
                try {
                    lJO.put(Constants.POST_TO_FOLLOWERS, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                                m_cLessons.getId() +
                                "/" +
                                Constants.POST,
                        Lessons.class, this, getResources().getString(R.string.lesson_posted_successfully_txt), null, lJO.toString(), true);
                return true;
            case R.id.action_unpost_from_connections:
                displayProgressBar(-1, "");
                RequestManager.getInstance(this).placeUnivUserRequest(Constants.LESSONS +
                                m_cLessons.getId() +
                                "/" +
                                Constants.POST,
                        Lessons.class, this, getResources().getString(R.string.lesson_unposted_successfully_txt), null, null, Request.Method.DELETE);

                return true;
            case R.id.action_unpost_from_followers:
                displayProgressBar(-1, "");
                JSONObject llJO = new JSONObject();
                try {
                    llJO.put(Constants.POST_TO_FOLLOWERS, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                                m_cLessons.getId() +
                                "/" +
                                Constants.POST,
                        Lessons.class, this, getResources().getString(R.string.lesson_unposted_successfully_txt), null, llJO.toString(), true);
                return true;
            case R.id.action_report_spam:
                callLessonFlags(true, 3);
                return true;
            case R.id.action_delete_all_shares:
                displayYesOrNoCustAlert(R.id.action_delete_all_shares,
                        getResources().getString(R.string.action_delete_all_shares),
                        getResources().getString(R.string.delete_all_shares_desc_txt),
                        m_cLessons);
                return true;
            case R.id.action_delete_my_shares:
                displayYesOrNoCustAlert(R.id.action_delete_my_shares,
                        getResources().getString(R.string.action_delete_my_shares),
                        getResources().getString(R.string.delete_my_shares_desc_txt),
                        m_cLessons);
                return true;
            case R.id.action_view_creator_profile:
                lObjIntent = new Intent(this, PotUserProfileScreen.class);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cLessons.getOwner()));
                lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(m_cLessons));
                startActivity(lObjIntent);
                return true;
            case R.id.action_view_sharer_profile:
                Users llessonSource;
                switch (mLessFromWhereWchTab){
                    case PotMacros.OBJ_LESSON_MINE_TAB:
                    case PotMacros.OBJ_LESSON_PUBLIC_TAB:
                        llessonSource = m_cLessons.getOwner();
                        break;
                    case PotMacros.OBJ_LESSON_RECEIVED_TAB:
                        llessonSource = m_cLessonShares.getFromUser();
                        break;
                    case PotMacros.OBJ_LESSON_VIEWED_TAB:
                        llessonSource = m_cLessonViews.getSource();
                        break;
                    default:
                        llessonSource = m_cLessons.getOwner();
                        break;
                }
                lObjIntent = new Intent(this, PotUserProfileScreen.class);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(llessonSource));
                lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(m_cLessons));
                startActivity(lObjIntent);
                return true;
            case R.id.action_include_in_online_received_tab:
                displayProgressBar(-1, "");
                HashMap<String, String> llParams = new HashMap<>();
                llParams.put(Constants.USERNAME, PotMacros.PUBLIC_USERNAME);
                RequestManager.getInstance(this).placeRequest(Constants.USERS,
                        UsersAll.class, this, null, llParams, null, false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (m_cLessType == PotMacros.OBJ_LESSON_NEW || m_cLessType == PotMacros.OBJ_LESSON_EDIT) {
            menu.findItem(R.id.action_post_to_public).setVisible(false);
            menu.findItem(R.id.action_post_to_connections).setVisible(false);
            menu.findItem(R.id.action_share).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_delete_lesson).setVisible(false);
            menu.findItem(R.id.action_delete_all_shares).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_post_to_followers_and_connections).setVisible(false);
            menu.findItem(R.id.action_extend_post_to_public).setVisible(false);
            menu.findItem(R.id.action_extend_post_to_followers).setVisible(false);
            menu.findItem(R.id.action_unpost_from_public).setVisible(false);
            menu.findItem(R.id.action_unpost_from_connections).setVisible(false);
            menu.findItem(R.id.action_unpost_from_followers).setVisible(false);
            menu.findItem(R.id.action_add_syllabus).setVisible(false);
            menu.findItem(R.id.action_remove).setVisible(false);
            menu.findItem(R.id.action_delete_my_shares).setVisible(false);
            menu.findItem(R.id.action_include_in_online_received_tab).setVisible(false);
            menu.findItem(R.id.action_view_creator_profile).setVisible(false);
            menu.findItem(R.id.action_view_sharer_profile).setVisible(false);
            menu.findItem(R.id.action_report_spam).setVisible(false);
        } else if (m_cLessType == PotMacros.OBJ_LESSON_VIEW) {
            menu = PotMacros.getLessonSettingsOptionsList(this, m_cLessons, m_cLessonShares, m_cLessonViews, mLessFromWhereWchTab, m_cUser, menu);

            /*if (m_cUser.getId().equals(m_cLessons.getOwner().getId())) {
                menu.findItem(R.id.action_remove).setVisible(false);
                menu.findItem(R.id.action_view_creator_profile).setVisible(false);
                if (!mLessFromWhere.equals(PotMacros.OBJ_LESSON))
                    menu.findItem(R.id.action_post_to_public).setVisible(false);
            } else {
                if (m_cUser.getRole().equalsIgnoreCase(PotMacros.ROLE_STUDENT)) {
                    menu.findItem(R.id.action_share).setVisible(false);
                    menu.findItem(R.id.action_post_to_connections).setVisible(false);
                }
                menu.findItem(R.id.action_edit).setVisible(false);
                menu.findItem(R.id.action_delete).setVisible(false);
                menu.findItem(R.id.action_post_to_public).setVisible(false);
                menu.findItem(R.id.action_view_creator_profile).setVisible(false);
            }*/
        }
        return super.onPrepareOptionsMenu(menu);

        //uncomment when required
//        MenuItem register = menu.findItem(R.id.action_delete);
//        register.setVisible(false);  //userRegistered is boolean, pointing if the user has registered or not.
//        return true;
    }

    private boolean checkAndUpdateLessons() {
        boolean lRetVal = false;
        List<LessonsTable> lessonsTableListAll = LessonsTable.listAll(LessonsTable.class);
        List<LessonsTable> lessonsTableList = LessonsTable.find(LessonsTable.class, "lesson_id = ? and user_id = ?", String.valueOf(m_cLessons.getId()), String.valueOf(m_cUser.getId()));
        int lSourceId = -1;
        String lSource = null;
        switch (mLessFromWhereWchTab) {
            case PotMacros.OBJ_LESSON_RECEIVED_TAB:
                lSourceId = m_cLessonShares.getFromUser().getId();
                lSource = m_cLessonShares.getFromUser().getFirstName() + " " + m_cLessonShares.getFromUser().getLastName();
                break;
            case PotMacros.OBJ_LESSON_VIEWED_TAB:
                lSourceId = m_cLessonViews.getSource().getId();
                lSource = m_cLessonViews.getSource().getFirstName() + " " + m_cLessonViews.getSource().getLastName();
                break;
            case PotMacros.OBJ_LESSON_MINE_TAB:
            case PotMacros.OBJ_LESSON_PUBLIC_TAB:
                lSourceId = m_cLessons.getOwner().getId();
                lSource = m_cLessons.getOwner().getFirstName() + " " + m_cLessons.getOwner().getLastName();
                break;
        }
        if (lessonsTableList.size() == 0) {
            m_cLessonsTable = new LessonsTable(m_cLessons.getId(), m_cLessons.getName(), m_cLessons.getComments(),
                    m_cLessons.getCreated(), m_cLessons.getModified(), m_cUser.getId(), m_cLessons.getOwner().getId(), lSourceId != m_cUser.getId() ? lSourceId : -1,
                    m_cLessons.getChapter().getName(),
                    m_cLessons.getChapter().getSyllabus().getSubjectName(),
                    m_cLessons.getChapter().getSyllabus().getBoardclass().getName() + "," +
                    m_cLessons.getChapter().getSyllabus().getBoardclass().getBoard().getName(),
                    null,
                    null,
                    null,
                    null,
                    m_cLessons.getLength().getLengthSum(),
                    m_cLessons.getPosition(),
                    m_cLessons.getViews(),
                    false,
                    m_cLessons.getOwner().getFirstName() + " " + m_cLessons.getOwner().getLastName(),
                    lSource);
            m_cLessonsTable.save();
            lRetVal = true;
        } else {
            List<LessonsTable> lessonsTableAll = LessonsTable.find(LessonsTable.class, "lesson_id = ?", String.valueOf(m_cLessons.getId()));
            m_cLessonsTable = LessonsTable.find(LessonsTable.class, "lesson_id = ? and user_id = ?", String.valueOf(m_cLessons.getId()), String.valueOf(m_cUser.getId())).get(0);
            m_cLessonsTable.setLessonId(m_cLessons.getId());
            m_cLessonsTable.setName(m_cLessons.getName());
            m_cLessonsTable.setComments(m_cLessons.getComments());
            m_cLessonsTable.setCreated(m_cLessons.getCreated());
            m_cLessonsTable.setModified(m_cLessons.getModified());
            m_cLessonsTable.setUserId(m_cUser.getId());
            m_cLessonsTable.setOwnerId(m_cLessons.getOwner().getId());
            m_cLessonsTable.setSharerId(lSourceId != m_cUser.getId() ? lSourceId : -1);
            m_cLessonsTable.setChapterName(m_cLessons.getChapter().getName());
            m_cLessonsTable.setSyllabiName(m_cLessons.getChapter().getSyllabus().getSubjectName());
            m_cLessonsTable.setBoardClass(m_cLessons.getChapter().getSyllabus().getBoardclass().getName() + "," +
                    m_cLessons.getChapter().getSyllabus().getBoardclass().getBoard().getName());
            m_cLessonsTable.setLengthSum(m_cLessons.getLength().getLengthSum());
            m_cLessonsTable.setPosition(m_cLessons.getPosition());
            m_cLessonsTable.setViews(m_cLessons.getViews());
            m_cLessonsTable.setEdited(false);
            if (null != m_cLessonsTable.getAudio())
            checkAndDelete(m_cLessonsTable.getAudio());
            if (null != m_cLessonsTable.getImg1())
            checkAndDelete(m_cLessonsTable.getImg1());
            if (null != m_cLessonsTable.getImg2())
            checkAndDelete(m_cLessonsTable.getImg2());
            if (null != m_cLessonsTable.getImg3())
            checkAndDelete(m_cLessonsTable.getImg3());
            m_cLessonsTable.setAudio(null);
            m_cLessonsTable.setImg1(null);
            m_cLessonsTable.setImg2(null);
            m_cLessonsTable.setImg3(null);
            m_cLessonsTable.setOwner(m_cLessons.getOwner().getFirstName() + " " + m_cLessons.getOwner().getLastName());
            m_cLessonsTable.setSource(lSource);
            m_cLessonsTable.save();
            lRetVal = true;
        }
        return lRetVal;
    }

    private Attachments addAttachment(Integer pId, String pLessonType, String pAttach) {
        Attachments lAttachments = new Attachments();
        lAttachments.setId(pId);
        lAttachments.setAttachmentType(pLessonType);
        lAttachments.setAttachment(pAttach);
        return lAttachments;
    }

    @Override
    public void onBackPressed() {
        if (viewStub.getVisibility() == View.VISIBLE)
            viewStub.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    try {
                        displayProgressBar(-1, "Loading...");
                        long ldate = 0;
                        Uri lUriwodata = null;
                        try {
                            lUriwodata = data.getData();
                        } catch (Exception e) {
                            lUriwodata = null;
                        }
                        if (data != null && lUriwodata != null) {
                            File lfile = new File(PotMacros.getImageFilePath(this), m_cImageGUID + ".jpg");

                            Uri m_cSelectedImageUri = data == null ? null : data.getData();
                            String lImageName = m_cImageGUID + ".jpg";

                            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                            Cursor cursor = getContentResolver().query(m_cSelectedImageUri,
                                    filePathColumn, null, null, null);
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            cursor.close();

                            copyFile(picturePath, lfile.getAbsolutePath());

                            m_cObjUIHandler.sendEmptyMessageDelayed(PROCESS_PICTURE, 500);
                        } else {
                            m_cObjUIHandler.sendEmptyMessageDelayed(PROCESS_PICTURE, 500);

                            File lFile = new File(PotMacros.getImageFilePath(this), m_cImageGUID + ".jpg");
                            double kilobytes = (lFile.length() / 1024);
                            if (kilobytes > 2000) {
                                displaySnack(m_cRlMain, "Selected file exceeds upload file limit");
                            }
                        }
//						deletePicFromDCIM();
                    } catch (Exception e) {
                        displaySnack(m_cRlMain, "Unable to retrieve Image");
                        e.printStackTrace();
                        Log.w("IMAGE_NAME  : ", m_cImageGUID);
                    }
                }
                break;
            case LESSON_MAPPING:
                if (resultCode == RESULT_OK) {
                    Syllabi lSyllabi = (new Gson()).fromJson(data.getStringExtra(PotMacros.OBJ_SYLLABI), Syllabi.class);
                    BoardChoices lBoardChoices = (new Gson()).fromJson(data.getStringExtra(PotMacros.OBJ_BOARDCHOICES), BoardChoices.class);
                    if (null != m_cLessons) {
                        if (null != lSyllabi) {
                            if (!m_cLessons.getChapter().getSyllabus().getId().equals(lSyllabi.getId())) {
                                displayYesOrNoCustAlert(LESSON_MAPPING, getResources().getString(R.string.edit_location_txt),
                                        getResources().getString(R.string.syllabus_change_will_txt),
                                        data);
                            } else {
                                //call mapping
                                callMappingLocally(data);
                            }
                        } else {
                            if (!m_cLessons.getChapter().getSyllabus().getBoardclass().getId().equals(lBoardChoices.getBoardclass().getId())) {
                                displayYesOrNoCustAlert(LESSON_MAPPING, getResources().getString(R.string.edit_location_txt),
                                        getResources().getString(R.string.syllabus_change_will_txt),
                                        data);
                            } else {
                                //call mapping
                                callMappingLocally(data);
                            }
                        }
                    } else {
                        callMappingLocally(data);
                    }
                }
                break;
            case LESSON_LIKES_COMMENT:
                if (resultCode == RESULT_OK){
                    callLikes(null);
                    callComments(null);
                }
                break;
        }
    }

    private void callMappingLocally(Intent data) {
        mLessFromWhere = data.getStringExtra(PotMacros.OBJ_LESSONFROM);
        m_cUser = (new Gson()).fromJson(data.getStringExtra(PotMacros.OBJ_USER), Users.class);
        m_cBoardChoice = (new Gson()).fromJson(data.getStringExtra(PotMacros.OBJ_BOARDCHOICES), BoardChoices.class);
        m_cSyllabi = (new Gson()).fromJson(data.getStringExtra(PotMacros.OBJ_SYLLABI), Syllabi.class);
        m_cChapters = (new Gson()).fromJson(data.getStringExtra(PotMacros.OBJ_CHAPTERS), Chapters.class);
        mIsLessonMapped = true;
        m_cIsEdited = true;
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            stopRecording();
        }
        if (mPlayer != null) {
            mPlayer.stop();
            stopPlaying();
        }
        //TODO : delete complete folder not only single image and image
        deleteRecursive(PotMacros.getImageFilePath(this));
        deleteRecursive(PotMacros.getAudioFilePath(this));
    }

    @Optional
    @OnClick({R.id.REC_PAUSE_RES_IMG,
            R.id.PLAY_PAUSE_RESUM_IMG, R.id.IMAGES_TITLE_TXT,
            R.id.CANCEL_LESSON_TXT, R.id.SAVE_LESSON_TXT,
            R.id.LESSON_IMG_1, R.id.LESSON_IMG_2, R.id.LESSON_IMG_3,
            R.id.DELETE_MEDIA_IMG, R.id.TITLE_TXT,
            R.id.PAGER_DELETE_IMG, R.id.LESSON_LOCATION_RL,
            R.id.LIKE_TXT, R.id.COMMENT_TXT,
            R.id.LIKE_LIST_TXT, R.id.COMMENT_LIST_TXT})
    public void onClick(View v) {
        Intent lObjIntent;
        switch (v.getId()) {
            case R.id.REC_PAUSE_RES_IMG:
                if (mIsRecording) {
                    /*mIsRecording = !mIsRecording;
                    mRecPauseResImg.setImageResource(R.drawable.ellipse_3);
                    stopRecording();
                    ButterKnife.apply(mPlayPauseResImg, PotMacros.SETENABLED, true);*/

                    //the below code will tc of the image also
                    manualStopRec();
                } else {
                    if ((new File(PotMacros.getAudioFilePath(this), m_cAudioGUID + ".aac").exists()) || m_cAudioUrl != null)
                        displayYesOrNoAlert(v.getId(), getResources().getString(R.string.rewrite_record_txt),
                                getResources().getString(R.string.rewrite_record_detail_txt));
                    else {
                        mIsRecording = !mIsRecording;
                        mRecPauseResImg.setImageResource(R.drawable.rectangle_1_2);
                        startRecording();
                        ButterKnife.apply(mPlayPauseResImg, PotMacros.SETENABLED, false);
                    }
                }
                break;
            case R.id.PLAY_PAUSE_RESUM_IMG:
                if ((new File(PotMacros.getAudioFilePath(this), m_cAudioGUID + ".aac").exists())) { //stored file
                    if (mIsPlaying) {
                        mIsPlaying = !mIsPlaying;
                        mPlayPauseResImg.setImageResource(R.drawable.playicon);
                        pausePlaying();
                        ButterKnife.apply(mRecPauseResImg, PotMacros.SETENABLED, true);
                    } else {
                        mIsPlaying = !mIsPlaying;
                        mPlayPauseResImg.setImageResource(R.drawable.pauseicon);
                        if (mPlayer != null)
                            resumePlaying();
                        else
                            startPlaying();
                        ButterKnife.apply(mRecPauseResImg, PotMacros.SETENABLED, false);
                    }
                }else if (null != m_cAudioUrl) { //live streaming
                    if (m_cIsPrepared) //allow only if prepared
                        if (mIsPlaying) {
                            mIsPlaying = !mIsPlaying;
                            mPlayPauseResImg.setImageResource(R.drawable.playicon);
                            pausePlaying();
                            ButterKnife.apply(mRecPauseResImg, PotMacros.SETENABLED, true);
                        } else {
                            mIsPlaying = !mIsPlaying;
                            mPlayPauseResImg.setImageResource(R.drawable.pauseicon);
                            if (mPlayer != null && mPlayer.isPlaying())
                                resumePlaying();
                            else
                                startPlaying(m_cAudioUrl);
                            ButterKnife.apply(mRecPauseResImg, PotMacros.SETENABLED, false);
                        }
                }
                break;
            case R.id.IMAGES_TITLE_TXT:
                if (m_cLessType == PotMacros.OBJ_LESSON_VIEW)
                    displayCommentDialog(v.getId(), getResources().getString(R.string.notes_txt), mNotes, true);
                else
                    displayCommentDialog(v.getId(), getResources().getString(R.string.notes_txt), mNotes, false);
                break;
            case R.id.CANCEL_LESSON_TXT:
                onBackPressed();
                break;
            case R.id.SAVE_LESSON_TXT:
                if (m_cObjUIHandler.hasMessages(RECORD_SEEK)) {
                    manualStopRec();
                }
                if (mLessonID != 0) {
                    displayProgressBar(-1, "");
                    HashMap<String, String> lParams = new HashMap<>();
                    if (m_cIsEdited)
                        lParams.put(Constants.CONTENT_EDITED, String.valueOf(m_cIsEdited));
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.NAME, mLessonTitleEdit.getText().toString().trim().isEmpty() ? m_cLessons.getName() :
                                mLessonTitleEdit.getText().toString().trim());
                        lJO.put(Constants.COMMENTS, mNotes != null ? mNotes : "");
                        lJO.put(Constants.SHARABLE, m_cIsSharable);
                        lJO.put(Constants.OFFLINEABLE, m_cIsOfflinable);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    switch (mLessFromWhere) {
                        case PotMacros.OBJ_BOARDCHOICES:
                            RequestManager.getInstance(this).placeUnivUserRequest(Constants.LESSONS
                                            +
                                            mLessonID +
                                            "/",
                                    Lessons.class, this, null, lParams, lJO.toString(), Request.Method.PUT);

                            break;
                        case PotMacros.OBJ_SYLLABI:
                            RequestManager.getInstance(this).placeUnivUserRequest(Constants.BOARDCLASSES +
                                            m_cBoardChoice.getBoardclass().getId() +
                                            "/" +
                                            Constants.LESSONS +
                                            mLessonID +
                                            "/",
                                    Lessons.class, this, null, lParams, lJO.toString(), Request.Method.PUT);

                            break;
                        case PotMacros.OBJ_CHAPTERS:
                            RequestManager.getInstance(this).placeUnivUserRequest(Constants.SYLLABI +
                                            m_cSyllabi.getId() +
                                            "/" +
                                            Constants.LESSONS +
                                            mLessonID +
                                            "/",
                                    Lessons.class, this, null, lParams, lJO.toString(), Request.Method.PUT);
                            break;
                        case PotMacros.OBJ_LESSON:
                            RequestManager.getInstance(this).placeUnivUserRequest(Constants.CHAPTERS +
                                            m_cChapters.getId() +
                                            "/" +
                                            Constants.LESSONS +
                                            mLessonID +
                                            "/",
                                    Lessons.class, this, null, lParams, lJO.toString(), Request.Method.PUT);
                            break;
                    }
                } else {
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.NAME, mLessonTitleEdit.getText().toString().trim().isEmpty() ? "" :
                                mLessonTitleEdit.getText().toString().trim());
                        lJO.put(Constants.COMMENTS, mNotes != null ? mNotes : "");
                        lJO.put(Constants.SHARABLE, m_cIsSharable);
                        lJO.put(Constants.OFFLINEABLE, m_cIsOfflinable);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ButterKnife.apply(saveTxt, PotMacros.SETENABLED, false);
                    saveTxt.setTextColor(getResources().getColor(R.color.warm_grey));
                    displayProgressBar(-1, "Loading...");
                    switch (mLessFromWhere) {
                        case PotMacros.OBJ_BOARDCHOICES:
                            RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS,
                                    Lessons.class, this, null, null, lJO.toString(), true);

                            break;
                        case PotMacros.OBJ_SYLLABI:
                            RequestManager.getInstance(this).placeUserRequest(Constants.BOARDCLASSES +
                                    m_cBoardChoice.getBoardclass().getId() +
                                    "/" +
                                    Constants.LESSONS, Lessons.class, this, null, null, lJO.toString(), true);

                            break;
                        case PotMacros.OBJ_CHAPTERS:
                            RequestManager.getInstance(this).placeUserRequest(Constants.SYLLABI +
                                    m_cSyllabi.getId() +
                                    "/" +
                                    Constants.LESSONS, Lessons.class, this, null, null, lJO.toString(), true);
                            break;
                        case PotMacros.OBJ_LESSON:
                            RequestManager.getInstance(this).placeUserRequest(Constants.CHAPTERS +
                                    m_cChapters.getId() +
                                    "/" +
                                    Constants.LESSONS, Lessons.class, this, null, null, lJO.toString(), true);
                            break;
                    }
                }
                break;
            case R.id.LESSON_IMG_1:
                if (m_cLessType == PotMacros.OBJ_LESSON_VIEW) {
                    if (m_cAttachList.containsKey(PotMacros.LESSON_IMG_1)){
                        showBottomSheet(PotMacros.LESSON_IMG_1);
                    }
                }else {
                    if (m_cAttachList.containsKey(PotMacros.LESSON_IMG_1))
                        showPhotoOption(v.getId(), true);
                    else
                        showPhotoOption(v.getId(), false);
                }
                break;
            case R.id.LESSON_IMG_2:
                if (m_cLessType == PotMacros.OBJ_LESSON_VIEW) {
                    if (m_cAttachList.containsKey(PotMacros.LESSON_IMG_2)){
                        showBottomSheet(PotMacros.LESSON_IMG_2);
                    }
                }else {
                    if (m_cAttachList.containsKey(PotMacros.LESSON_IMG_2))
                        showPhotoOption(v.getId(), true);
                    else
                        showPhotoOption(v.getId(), false);
                }
                break;
            case R.id.LESSON_IMG_3:
                if (m_cLessType == PotMacros.OBJ_LESSON_VIEW) {
                    if (m_cAttachList.containsKey(PotMacros.LESSON_IMG_3)){
                        showBottomSheet(PotMacros.LESSON_IMG_3);
                    }
                }else {
                    if (m_cAttachList.containsKey(PotMacros.LESSON_IMG_3))
                        showPhotoOption(v.getId(), true);
                    else
                        showPhotoOption(v.getId(), false);
                }
                break;
            case R.id.DELETE_MEDIA_IMG:
                if ((new File(PotMacros.getAudioFilePath(this), m_cAudioGUID + ".aac").exists()) || m_cAudioUrl != null)
                    displayYesOrNoAlert(v.getId(), getResources().getString(R.string.delete_record_txt),
                            getResources().getString(R.string.delete_record_detail_txt));
                break;
            case R.id.TITLE_TXT:
                break;
            case R.id.PAGER_DELETE_IMG:
                viewStub.setVisibility(View.GONE);
                break;
            case R.id.LESSON_LOCATION_RL:
                lObjIntent = new Intent(this, LessonMappingScreen.class);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                startActivityForResult(lObjIntent, LESSON_MAPPING);
                break;
            case R.id.LIKE_TXT:
                RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                        m_cLessons.getId() +
                        "/" +
                        Constants.LESSON_LIKES, Likes.class, this, null, null, null, true);
                break;
            case R.id.COMMENT_TXT:
                if (m_cLessType == PotMacros.OBJ_LESSON_VIEW)
                    callComments(PotMacros.OBJ_LESSON_COMMENTS);
                break;
            case R.id.LIKE_LIST_TXT:
                callLikes(PotMacros.OBJ_LESSON_LIKES);
                break;
            case R.id.COMMENT_LIST_TXT:
                callComments(PotMacros.OBJ_LESSON_COMMENTS);
                break;
        }
    }

    @Optional
    @OnTouch({R.id.SHARES_SWITCH, R.id.OFFLINE_SWITCH,
            R.id.LESSON_TITLE_TXT})
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.SHARES_SWITCH:
                m_cIsShareTouched = true;
                break;
            case R.id.OFFLINE_SWITCH:
                m_cIsOfflineTouched = true;
                break;
            case R.id.LESSON_TITLE_TXT:
                m_cIsTitleTouched = true;
                break;
        }
        return false;
    }

    @Optional
    @OnCheckedChanged({R.id.SHARES_SWITCH, R.id.OFFLINE_SWITCH})
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.SHARES_SWITCH:
                if (m_cIsShareTouched) {
                    m_cIsShareTouched = false;
                    m_cIsSharable = !m_cIsSharable;
                    if(!m_cIsSharable){
                        if (m_cLessType == PotMacros.OBJ_LESSON_EDIT) {
                            if (m_cLessons.getPostedTo().equals(Constants.POSTED_FOLLOWERS)){
                                displayToastLong(getResources().getString(R.string.lesson_cannot_be_visible_txt));
                            }else {
                                displayToast(getResources().getString(R.string.lesson_cannot_be_shared_txt));
                            }
                        }else {
                            displayToast(getResources().getString(R.string.lesson_cannot_be_shared_txt));
                        }
                    }
//                    if (m_cLessType != PotMacros.OBJ_LESSON_NEW)
//                        callLessonFlags(isChecked, 0);
                }
                break;
            case R.id.OFFLINE_SWITCH:
                if (m_cIsOfflineTouched) {
                    m_cIsOfflineTouched = false;
                    m_cIsOfflinable = !m_cIsOfflinable;
                    if(!m_cIsOfflinable){
                        displayToast(getResources().getString(R.string.lesson_cannot_be_offlinable_txt));
                    }

//                    if (m_cLessType != PotMacros.OBJ_LESSON_NEW)
//                        callLessonFlags(isChecked, 1);
                }
                break;
        }
    }

    private void callComments(String objLessonComments) {
        RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                m_cLessons.getId() +
                "/" +
                Constants.LESSON_COMMENTS, CommentsAll.class, this, objLessonComments, null, null, false);
    }

    private void callLikes(String objLessonLikes) {
        RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                m_cLessons.getId() +
                "/" +
                Constants.LESSON_LIKES, LikesAll.class, this, objLessonLikes, null, null, false);
    }

    private void callLessonFlags(boolean isTrue, int pCase) {
        String lStrObj = null;
        JSONObject lJO = new JSONObject();
        try {
            switch (pCase) {
                case 0:
                    lJO.put(Constants.SHARABLE, isTrue);
                    lJO.put(Constants.NAME, m_cLessons.getName());
                    lStrObj = Constants.SHARABLE;
                    break;
                case 1:
                    lJO.put(Constants.OFFLINEABLE, isTrue);
                    lJO.put(Constants.NAME, m_cLessons.getName());
                    lStrObj = Constants.OFFLINEABLE;
                    break;
                case 2:
                    lJO.put(Constants.PUBLICABLE, isTrue);
                    lJO.put(Constants.NAME, m_cLessons.getName());
                    lStrObj = Constants.PUBLICABLE;
                    break;
                case 3:
                    lJO.put(Constants.IS_SPAM, isTrue);
                    lJO.put(Constants.NAME, m_cLessons.getName());
                    lStrObj = Constants.IS_SPAM;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestManager.getInstance(this).placeUnivUserRequest(Constants.CHAPTERS +
                m_cLessons.getChapter().getId() +
                "/" +
                Constants.LESSONS +
                m_cLessons.getId() +
                "/", Lessons.class, this, new Object[]{lStrObj, isTrue}, null, lJO.toString(), Request.Method.PATCH);
    }

    private void manualStopRec() {
        if (mIsRecording) {
            mIsRecording = !mIsRecording;
            mRecPauseResImg.setImageResource(R.drawable.ellipse_3);
            ButterKnife.apply(mPlayPauseResImg, PotMacros.SETENABLED, true);
        }
        stopRecording();
        if (mPlayer != null)
            stopPlaying();
        mSeekBar.setProgress(0);
        progressTimerTxt.setText(getResources().getString(R.string.timer_nutral));
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, Uri.parse(PotMacros.getAudioFilePath(this) + "/" + m_cAudioGUID + ".aac"));
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long millSecond = Long.parseLong(durationStr);
            fixedTimerTxt.setText(PotMacros.getFormatedTimer(millSecond));
        }catch (Exception e){
            e.printStackTrace();
            fixedTimerTxt.setText(getResources().getString(R.string.timer_nutral));
        }
        m_cObjUIHandler.removeMessages(RECORD_SEEK);
        m_cObjUIHandler.removeMessages(PLAY_SEEK);
    }

    private void showPhotoOption(int pId, boolean pShow) {
        if (pShow)
        displaySpinnerDialog(pId, getResources().getString(R.string.add_photo_txt),
                Arrays.asList(getResources().getString(R.string.go_to_camera), getResources().getString(R.string.go_to_gallery),
                        getResources().getString(R.string.delete_photo), getResources().getString(R.string.view_photo)), null);
        else
            displaySpinnerDialog(pId, getResources().getString(R.string.add_photo_txt),
                    Arrays.asList(getResources().getString(R.string.go_to_camera), getResources().getString(R.string.go_to_gallery)), null);

    }

    private void performPhotoOptions(int pId, String pLessonImgTag, ImageView mLessonImg) {
        switch (pId){
            case 0:
                takePhoto();
                break;
            case 1:
                showGallery();
                break;
            case 2:
                if (((Attachments)m_cAttachList.get(pLessonImgTag)).getId() == -1) {
                    m_cAttachList.remove(pLessonImgTag);
                }else {
                    ((Attachments)m_cAttachList.get(pLessonImgTag)).setIsDeleted(true);
                }
                mLessonImg.setImageResource(R.mipmap.cameraicon_pad2);
                m_cIsEdited = true;
                break;
            case 3:
                showBottomSheet(pLessonImgTag);
                break;
        }
    }

    private void showBottomSheet(String pLessonImgTag) {
        ArrayList<String> list = new ArrayList<>();
        if (m_cAttachList.containsKey(PotMacros.LESSON_IMG_1))
            list.add(((Attachments)m_cAttachList.get(PotMacros.LESSON_IMG_1)).getAttachment());
        if (m_cAttachList.containsKey(PotMacros.LESSON_IMG_2))
            list.add(((Attachments)m_cAttachList.get(PotMacros.LESSON_IMG_2)).getAttachment());
        if (m_cAttachList.containsKey(PotMacros.LESSON_IMG_3))
            list.add(((Attachments)m_cAttachList.get(PotMacros.LESSON_IMG_3)).getAttachment());

        m_cSlider = BottomSheetBehavior.from(viewStub);
        m_cSlider.setState(BottomSheetBehavior.STATE_EXPANDED);

        HashMap<String, Attachments> lAttachList = new HashMap<>(m_cAttachList);
        if (lAttachList.containsKey(PotMacros.LESSON_AUDIO))
            lAttachList.remove(PotMacros.LESSON_AUDIO);
        int lIndex = 0;
        switch (lAttachList.size()){
            case 3:
            case 1:
                switch (pLessonImgTag){
                    case PotMacros.LESSON_IMG_1:
                        lIndex = 0;
                        break;
                    case PotMacros.LESSON_IMG_2:
                        lIndex = 1;
                        break;
                    case PotMacros.LESSON_IMG_3:
                        lIndex = 2;
                        break;
                }
                break;
            case 2:
                switch (pLessonImgTag){
                    case PotMacros.LESSON_IMG_1:
                        lIndex = 0;
                        break;
                    case PotMacros.LESSON_IMG_2:
                        if (lAttachList.containsKey(PotMacros.LESSON_IMG_1))
                            lIndex = 1;
                        else
                            lIndex = 0;
                        break;
                    case PotMacros.LESSON_IMG_3:
                        lIndex = 1;
                        break;
                }
                break;
        }
        m_cScreenSlidePagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this, 0, list, null);
        imgPager.setAdapter(m_cScreenSlidePagerAdapter);
        imgPager.setCurrentItem(lIndex);

        m_cSlider.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING)
                    m_cSlider.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        viewStub.setVisibility(View.VISIBLE);
    }

    private void startRecording() {
        m_cObjUIHandler.removeMessages(RECORD_SEEK);
        m_cObjUIHandler.removeMessages(PLAY_SEEK);
        mRecorder = new MediaRecorder();
        deleteRecursive(PotMacros.getAudioFilePath(this));
        try {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioEncodingBitRate(32000);
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setOutputFile(PotMacros.getAudioFilePath(this) + "/" + m_cAudioGUID + ".aac");
            mRecorder.prepare();
            mRecorder.start();
            mSeekBar.setProgress(0);
            int max = (int) MAX_TIME_MILLIS / 1000;
            mSeekBar.setMax(max);
            m_cStartRecTime = System.currentTimeMillis();
            m_cObjUIHandler.sendEmptyMessage(RECORD_SEEK);
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        try {
            m_cAttachList.put(PotMacros.LESSON_AUDIO, addAttachment(-1, PotMacros.LESSON_AUDIO, m_cAudioGUID + ".aac"));
            m_cIsEdited = true;
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        try {
            mPlayer.setDataSource(PotMacros.getAudioFilePath(this) + "/" + m_cAudioGUID + ".aac");
            mPlayer.prepare();
            if (mSeekBar.getProgress() > 0) {
                mSeekBar.setProgress(mSeekBar.getProgress());
                mPlayer.seekTo(mSeekBar.getProgress() * 1000);
            }else {
                mSeekBar.setProgress(0);
            }
            mPlayer.start();
            int max = mPlayer.getDuration() / 1000;
            mSeekBar.setMax(max);
            m_cObjUIHandler.sendEmptyMessage(PLAY_SEEK);
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    private void startPlaying(String pUrlPath) {
        //below code put back to handler
        if (mPlayer != null) {
            try {
                mPlayer.start();
                mSeekBar.setProgress(0);
                int max = mPlayer.getDuration() / 1000;
                mSeekBar.setMax(max);
                m_cObjUIHandler.sendEmptyMessage(PLAY_SEEK);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //player prepared
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        m_cIsPrepared = true;
        playProgressBar.setVisibility(View.GONE);
        fixedTimerTxt.setText(PotMacros.getFormatedTimer(mPlayer.getDuration()));
    }

    //player buffering info
    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        switch (i) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                playProgressBar.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                playProgressBar.setVisibility(View.GONE);
                break;
        }
        return false;
    }

    private class StartPlaying extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(LessonsScreen.this);
            try {
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(m_cAudioUrl);
                mPlayer.setOnPreparedListener(LessonsScreen.this);
                mPlayer.setOnInfoListener(LessonsScreen.this);
                mPlayer.prepareAsync();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
            }
            return null;
        }
    }

    private void pausePlaying() {
        try {
            mPlayer.pause();
            length = mPlayer.getCurrentPosition();
        }catch (Exception e){
            Log.v(MEDIA_TAG, "media player pause error");
        }
    }

    private void resumePlaying() {
        mPlayer.seekTo(length);
        mPlayer.start();
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        length = 0;
        mSeekBar.setProgress(0);
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what) {
            case RECORD_SEEK:
                if (mRecorder != null) {
                    long currTime = System.currentTimeMillis() - m_cStartRecTime;
                    long mCurrentPosition = currTime;
                    if (mCurrentPosition <= MAX_TIME_MILLIS) {
                        mSeekBar.setProgress((int) (mCurrentPosition / 1000));
                        progressTimerTxt.setText(PotMacros.getFormatedTimer(mCurrentPosition));
                        fixedTimerTxt.setText(PotMacros.getFormatedTimer(MAX_TIME_MILLIS));
                        if (mCurrentPosition != MAX_TIME_MILLIS) {
                            m_cObjUIHandler.sendEmptyMessageDelayed(RECORD_SEEK, 1000);
                        }
                    } else {
                        manualStopRec();
                    }
                }
                break;
            case PLAY_SEEK:
                if (mPlayer != null) {
                    int lmax = mPlayer.getDuration() / 1000;
                    int mCurrentPosition = mPlayer.getCurrentPosition() / 1000;
                    if (mCurrentPosition <= lmax) {
                        mSeekBar.setProgress(mCurrentPosition);
                        progressTimerTxt.setText(PotMacros.getFormatedTimer(mPlayer.getCurrentPosition()));
                        fixedTimerTxt.setText(PotMacros.getFormatedTimer(mPlayer.getDuration()));
                        if (mCurrentPosition != lmax)
                            m_cObjUIHandler.sendEmptyMessageDelayed(PLAY_SEEK, 1000);
                    }
                }
                break;
            case R.id.IMAGES_TITLE_TXT:
                mNotes = (String) pObjMessage.obj;
                m_cIsEdited = true;
                break;
            case R.id.LESSON_IMG_1:
                m_cCurrentImgId = pObjMessage.what;
                performPhotoOptions(pObjMessage.arg1, PotMacros.LESSON_IMG_1, mLessonImg1);
                break;
            case R.id.LESSON_IMG_2:
                m_cCurrentImgId = pObjMessage.what;
                performPhotoOptions(pObjMessage.arg1, PotMacros.LESSON_IMG_2, mLessonImg2);
                break;
            case R.id.LESSON_IMG_3:
                m_cCurrentImgId = pObjMessage.what;
                performPhotoOptions(pObjMessage.arg1, PotMacros.LESSON_IMG_3, mLessonImg3);
                break;
            case PROCESS_PICTURE:
                new captureImage().execute("");
                break;
            case R.id.DELETE_MEDIA_IMG:
                if ((boolean) pObjMessage.obj) {
                    if (mPlayer != null)
                        stopPlaying();
                    if (mRecorder != null)
                        stopRecording();
                    if (m_cAudioUrl != null) {
                        ((Attachments) m_cAttachList.get(PotMacros.LESSON_AUDIO)).setIsDeleted(true);
                        m_cAudioUrl = null;
                    } else {
                        m_cAttachList.remove(PotMacros.LESSON_AUDIO);
                    }
                    mIsPlaying = false;
                    mIsRecording = false;
                    mPlayPauseResImg.setImageResource(R.drawable.playicon);
                    mRecPauseResImg.setImageResource(R.drawable.ellipse_3);
                    ButterKnife.apply(mRecPauseResImg, PotMacros.SETENABLED, true);
                    ButterKnife.apply(mPlayPauseResImg, PotMacros.SETENABLED, false);
                    deleteRecursive(PotMacros.getAudioFilePath(this));
                    progressTimerTxt.setText(getResources().getString(R.string.timer_nutral));
                    fixedTimerTxt.setText(getResources().getString(R.string.timer_nutral));
                    m_cIsEdited = true;
                }
                break;
            case R.id.REC_PAUSE_RES_IMG:
                if ((boolean) pObjMessage.obj) {
                    m_cAudioUrl = null;
                    mIsRecording = !mIsRecording;
                    mRecPauseResImg.setImageResource(R.drawable.rectangle_1_2);
                    startRecording();
                    ButterKnife.apply(mPlayPauseResImg, PotMacros.SETENABLED, false);
                }
                break;
            case PotMacros.LESSON_ACTION_DOWNLOADED:
                //start playing
                new StartPlaying().execute();
                break;
            case R.id.COMMENT_TXT:
                mComment = (String) pObjMessage.obj;
                if (mComment != null && !mComment.isEmpty()) {
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.COMMENT, mComment.toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                            m_cLessons.getId() +
                            "/" +
                            Constants.LESSON_COMMENTS, Comments.class, this, null, null, lJO.toString(), true);
                }
                break;
            case PotMacros.OBJ_LESSON_COMMENTS_SHOW:
                CommentsAll lCommentsAll = (CommentsAll) pObjMessage.obj;
                Comments lComments = lCommentsAll.getComments().get(pObjMessage.arg1);
                if (lComments.getUser().getId() == m_cUser.getId())
                    displayCommentDialog(PotMacros.OBJ_LESSON_COMMENTS_EDIT,
                            getResources().getString(R.string.lesson_comment_txt),
                            lComments.getComment(),
                            false,
                            lComments);
                else
                    displayCommentDialog(PotMacros.OBJ_LESSON_COMMENTS_EDIT,
                            getResources().getString(R.string.lesson_comment_txt),
                            lComments.getComment(),
                            true,
                            lComments);
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
                    RequestManager.getInstance(this).placeUnivUserRequest(Constants.LESSONS +
                            m_cLessons.getId() +
                            "/" +
                            Constants.LESSON_COMMENTS +
                            lComment.getId() +
                            "/", Comments.class, this, null, null, lJO.toString(), Request.Method.PATCH);
                } else {
                    RequestManager.getInstance(this).placeUnivUserRequest(Constants.LESSONS +
                            m_cLessons.getId() +
                            "/" +
                            Constants.LESSON_COMMENTS +
                            lComment.getId() +
                            "/", Comments.class, this, null, null, null, Request.Method.DELETE);
                }
                break;
            case R.id.action_post_to_public:
                Object[] lObjectPub = (Object[]) pObjMessage.obj;
                if ((boolean) lObjectPub[0]) {
                    callLessonFlags(true, 0);
                    callLessonFlags(true, 2);
                }
                break;
            case LESSON_MAPPING:
                Object[] lObjectLoc = (Object[]) pObjMessage.obj;
                Intent lIntent = (Intent) lObjectLoc[1];
                if ((boolean) lObjectLoc[0]) {
                    callMappingLocally(lIntent);
                }
                break;
            case PotMacros.ACTION_DELETE_LESSON:
                Object[] lObjectDel = (Object[]) pObjMessage.obj;
                Lessons pLesson = (Lessons) lObjectDel[1];
                if ((boolean) lObjectDel[0]) {
                    callDeleteLessonApi();
                }
                break;
            case R.id.action_delete_all_shares:
                Object[] lObjectDelAll = (Object[]) pObjMessage.obj;
                Lessons lLessons = (Lessons) lObjectDelAll[1];
                if ((boolean) lObjectDelAll[0]) {
                    HashMap<String, String> lParams = new HashMap<>();
                    lParams.put(Constants.SCOPE, Constants.SCOPE_ALL);
                    displayProgressBar(-1, "");
                    RequestManager.getInstance(this).placeUnivUserRequest(Constants.LESSONS +
                                    m_cLessons.getId() +
                                    "/" +
                                    Constants.POST,
                            Lessons.class, this, getResources().getString(R.string.deleted_all_shares_txt), lParams, null, Request.Method.DELETE);
                }
                break;
            case R.id.action_delete_my_shares:
                Object[] lObjectDelMy = (Object[]) pObjMessage.obj;
                Lessons llLessons = (Lessons) lObjectDelMy[1];
                if ((boolean) lObjectDelMy[0]) {
                    HashMap<String, String> llParams = new HashMap<>();
                    llParams.put(Constants.SCOPE, Constants.SCOPE_MINE);
                    displayProgressBar(-1, "");
                    RequestManager.getInstance(this).placeUnivUserRequest(Constants.LESSONS +
                                    m_cLessons.getId() +
                                    "/" +
                                    Constants.POST,
                            Lessons.class, this, getResources().getString(R.string.deleted_my_shares_txt), llParams, null, Request.Method.DELETE);
                }
                break;
        }
    }

    //Media Player on complete triggers when record is completed
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (null == m_cAudioUrl) {
            // stored file
            mIsPlaying = !mIsPlaying;
            mPlayPauseResImg.setImageResource(R.drawable.playicon);
            ButterKnife.apply(mRecPauseResImg, PotMacros.SETENABLED, true);
            progressTimerTxt.setText(getResources().getString(R.string.timer_nutral));
            stopPlaying();
        } else {
            // live streaming
            mIsPlaying = !mIsPlaying;
            mPlayPauseResImg.setImageResource(R.drawable.playicon);
            ButterKnife.apply(mRecPauseResImg, PotMacros.SETENABLED, true);
            progressTimerTxt.setText(getResources().getString(R.string.timer_nutral));
            mSeekBar.setProgress(0);
            pausePlaying();
        }
    }

    //SeekBar on progress triggers for every progress change
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mPlayer != null && fromUser) {
            if (m_cIsPrepared || !(m_cLessType == PotMacros.OBJ_LESSON_VIEW)) {
                mPlayer.seekTo(progress * 1000);
                if (mIsPlaying) {
                    mPlayer.start();
                    m_cObjUIHandler.sendEmptyMessage(PLAY_SEEK);
                }
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.PUBLICSHARE)) {
                    if (response == null) {
                        displayToast(getResources().getString(R.string.lesson_successfully_included_txt));
                        hideDialog();
                    }
                } else if (apiMethod.contains(Constants.USERS)) {
                    UsersAll llUsers = (UsersAll) response;
                    JSONObject lllJO = new JSONObject();
                    try {
                        lllJO.put(Constants.PUBLIC_USER, String.valueOf(llUsers.getUsers().get(0).getId()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                                    m_cLessons.getId() +
                                    "/" +
                                    Constants.PUBLICSHARE,
                            Lessons.class, this, null, null, lllJO.toString(), true);
                } else if (apiMethod.contains(Constants.LESSON_COMMENTS)) {
                    if (response instanceof Comments) {
                        Comments lComments = (Comments) response;
                        callComments(null);
                    } else if (response == null) {
                        callComments(null);
                    } else if (response instanceof CommentsAll) {
                        CommentsAll lCommentsAll = (CommentsAll) response;
                        commentListTxt.setText(String.valueOf(lCommentsAll.getCount()));
                        if (null != lCommentsAll) {
                            if (null != refObj) {
                                /*ArrayList<String> lComments = new ArrayList<>();
                                ArrayList<Integer> lIds = new ArrayList<>();
                                if (null != (String) refObj) {
                                    for (Comments comments : lCommentsAll.getComments()) {
                                        lComments.add(String.format("%s : %s", comments.getUser().getFirstName() + " " + comments.getUser().getLastName(),
                                                comments.getComment()));
                                        lIds.add(comments.getId());
                                    }
                                    displaySpinnerDialog(PotMacros.OBJ_LESSON_COMMENTS_SHOW, getResources().getString(R.string.lesson_comments_txt),
                                            lComments, lCommentsAll);
                                }*/
                                if (null != (String) refObj) {
                                    Intent lObjIntent = new Intent(this, LessonLikesCommentsScreen.class);
                                    lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                                    lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(m_cLessons));
                                    lObjIntent.putExtra(PotMacros.OBJ_LESSON_COMMENTS, (new Gson()).toJson(lCommentsAll));
                                    lObjIntent.putExtra(PotMacros.OBJ_SELECTIONTYPE, false);
                                    startActivityForResult(lObjIntent, LESSON_LIKES_COMMENT);
                                }
                            }
                        }
                    }
                } else if (apiMethod.contains(Constants.LESSON_LIKES)) {
                    if (response instanceof Likes) {
                        Likes likes = (Likes) response;
                        callLikes(null);
                    } else if (response instanceof LikesAll) {
                        LikesAll likesAll = (LikesAll) response;
                        likeListTxt.setText(String.valueOf(likesAll.getCount()));
                        if (null != likesAll && likesAll.getLikes().size() > 0) {
                            /*ArrayList<String> lNames = new ArrayList<>();
                            ArrayList<Integer> lIds = new ArrayList<>();
                            if (null != (String) refObj) {
                                for (Likes likes : likesAll.getLikes()) {
                                    lNames.add(likes.getUser().getFirstName() + " " + likes.getUser().getLastName());
                                    lIds.add(likes.getUser().getId());
                                }
                                displaySpinnerDialog(PotMacros.OBJ_LESSON_LIKES_SHOW, getResources().getString(R.string.lesson_likes_txt),
                                        lNames, lIds, likesAll);
                            }*/
                            if (null != (String) refObj) {
                                Intent lObjIntent = new Intent(this, LessonLikesCommentsScreen.class);
                                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                                lObjIntent.putExtra(PotMacros.OBJ_LESSON, (new Gson()).toJson(m_cLessons));
                                lObjIntent.putExtra(PotMacros.OBJ_LESSON_LIKES, (new Gson()).toJson(likesAll));
                                lObjIntent.putExtra(PotMacros.OBJ_SELECTIONTYPE, true);
                                startActivityForResult(lObjIntent, LESSON_LIKES_COMMENT);
                            }
                        }
                    }
                } else if (apiMethod.contains(Constants.SOURCES)) {
                    if (response == null) {
                        hideDialog();
                        onBackPressed();
                    }
                } else if (response instanceof Syllabi) {
                    Syllabi lSyllabi = (Syllabi) response;
                    if (lSyllabi != null) {
                        hideDialog();
                        displayToast(getResources().getString(R.string.syllabus_added_successfully_txt));
                    }
                } else if (apiMethod.contains(Constants.POST)) {
                    if (response == null) {
                        displayToast((String) refObj);
                        displayProgressBar(-1, "");
                        RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                                m_cLessons.getId() +
                                "/", Lessons.class, this, new Object[]{PotMacros.OBJ_LESSON, false}, null, null, false);
                    }
                } else if (apiMethod.contains(Constants.CHAPTERS) && (refObj == null)) {
                    if (response == null) {
                        hideDialog();
                        onBackPressed();
                    } else if (response instanceof Lessons) {
                        Lessons lLessons = (Lessons) response;
                        if (lLessons != null && m_cAttachList.size() > 0) {
                            mLessonID = lLessons.getId();
                            callAttachmentsApi();
                        } else {
                            hideDialog();
                            navigate();
                        }
                    }
                } else if (apiMethod.contains(Constants.ATTACHMENTS)) {
                    if (response == null || response instanceof Attachments) {
//                        Attachments lAttachments = (Attachments) response;
                        //check only size, coz lAttachments is null when deleted
                        m_cAttachList.remove((String) refObj);
                        if (m_cAttachList.size() > 0) {
                            callAttachmentsApi();
                        } else {
                            hideDialog();
                            navigate();
                        }
                    } else {
                        AttachmentsAll lAttachmentsAll = (AttachmentsAll) response;
                        for (Attachments lAttachments : lAttachmentsAll.getAttachments()) {
                            if (lAttachments.getAttachmentType().equalsIgnoreCase(Constants.IMAGE)) {
                                switch (lAttachments.getSlot()) {
                                    case 1:
                                        m_cAttachList.put(PotMacros.LESSON_IMG_1, addAttachment(lAttachments.getId(),
                                                PotMacros.LESSON_IMG_1,
                                                lAttachments.getAttachment()));
                                        mLessonImg1.setVisibility(View.VISIBLE);
                                        Picasso.with(LessonsScreen.this)
                                                .load(lAttachments.getAttachment())
                                                .error(R.drawable.profile_placeholder)
                                                .placeholder(R.drawable.profile_placeholder)
                                                .transform(new RoundedCornersTransformation(0, 0))
                                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                .config(Bitmap.Config.RGB_565)
//                                                .centerCrop()
//                                                .resize(mLessonImg2.getMeasuredWidth(), mLessonImg1.getMeasuredHeight())
//                                                .fit()
                                                .into(mLessonImg1);
                                        break;
                                    case 2:
                                        m_cAttachList.put(PotMacros.LESSON_IMG_2, addAttachment(lAttachments.getId(),
                                                PotMacros.LESSON_IMG_2,
                                                lAttachments.getAttachment()));
                                        mLessonImg2.setVisibility(View.VISIBLE);
                                        Picasso.with(LessonsScreen.this)
                                                .load(lAttachments.getAttachment())
                                                .error(R.drawable.profile_placeholder)
                                                .placeholder(R.drawable.profile_placeholder)
                                                .transform(new RoundedCornersTransformation(0, 0))
                                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                .config(Bitmap.Config.RGB_565)
//                                                .centerCrop()
//                                                .resize(mLessonImg2.getMeasuredWidth(), mLessonImg1.getMeasuredHeight())
//                                                .fit()
                                                .into(mLessonImg2);
                                        break;
                                    case 3:
                                        m_cAttachList.put(PotMacros.LESSON_IMG_3, addAttachment(lAttachments.getId(),
                                                PotMacros.LESSON_IMG_3,
                                                lAttachments.getAttachment()));
                                        mLessonImg3.setVisibility(View.VISIBLE);
                                        Picasso.with(LessonsScreen.this)
                                                .load(lAttachments.getAttachment())
                                                .error(R.drawable.profile_placeholder)
                                                .placeholder(R.drawable.profile_placeholder)
                                                .transform(new RoundedCornersTransformation(0, 0))
                                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                .config(Bitmap.Config.RGB_565)
//                                                .centerCrop()
//                                                .resize(mLessonImg3.getMeasuredWidth(), mLessonImg1.getMeasuredHeight())
//                                                .fit()
                                                .into(mLessonImg3);
                                        break;
                                }
                            } else if (lAttachments.getAttachmentType().equalsIgnoreCase(Constants.AUDIO)) {
                                //start playing
                                playProgressBar.setVisibility(View.VISIBLE);
                                m_cAudioUrl = lAttachments.getAttachment();
                                m_cObjUIHandler.sendEmptyMessage(PotMacros.LESSON_ACTION_DOWNLOADED);
                                m_cAttachList.put(PotMacros.LESSON_AUDIO, addAttachment(lAttachments.getId(),
                                        PotMacros.LESSON_AUDIO,
                                        lAttachments.getAttachment()));
                                mediaPlayerLL.setVisibility(View.VISIBLE);

                                /*LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ownerTxt.getLayoutParams();
                                params.setMargins(0, 0, 19, 0);
                                ownerTxt.setLayoutParams(params);*/

                                //downloading file options
//                                new DownloadFile().execute((String) lAttachments.getAttachment(), null);
//                                downloadFileVolley(lAttachments.getAttachment());
                            }
                        }
                        hideDialog();
                    }
                } else if (apiMethod.contains(Constants.LESSONS) && refObj != null) {
                    Lessons lLessons = (Lessons) response;
                    Object[] lObjects = (Object[]) refObj;
                    if (lLessons != null) {
                        m_cLessons = lLessons;
                        switch ((String) lObjects[0]) {
                            case Constants.SHARABLE:
                                if ((boolean) lObjects[1])
                                    displaySnack(m_cRlMain, getResources().getString(R.string.shares_enabled_txt));
                                else
                                    displaySnack(m_cRlMain, getResources().getString(R.string.shares_disabled_txt));
                                break;
                            case Constants.OFFLINEABLE:
                                if ((boolean) lObjects[1])
                                    displaySnack(m_cRlMain, getResources().getString(R.string.offline_save_enabled_txt));
                                else
                                    displaySnack(m_cRlMain, getResources().getString(R.string.offline_save_disabled_txt));
                                break;
                            case Constants.PUBLICABLE:
                                if ((boolean) lObjects[1])
                                    displaySnack(m_cRlMain, getResources().getString(R.string.posted_to_public_txt));
                                else
                                    displaySnack(m_cRlMain, getResources().getString(R.string.unpost_from_public_txt));
                                break;
                            case Constants.IS_SPAM:
                                if ((boolean) lObjects[1])
                                    displaySnack(m_cRlMain, getResources().getString(R.string.marked_as_spam_txt));
                                else
                                    displaySnack(m_cRlMain, getResources().getString(R.string.removed_spam_txt));
                                break;
                        }
                    }
                    hideDialog();
                } else if (apiMethod.contains(Constants.LESSONS)) {
                    Lessons lLessons = (Lessons) response;
                    if (lLessons != null && m_cAttachList.size() > 0) {
                        mLessonID = lLessons.getId();
                        callAttachmentsApi();
                    } else {
                        hideDialog();
                        navigate();
                    }
                } else if (apiMethod.contains(Constants.OFFLINE_META)) {
                    Object[] lObjects = (Object[]) refObj;
                    try {
                        byte[] lByte = (byte[]) response;
                        long lenghtOfFile = lByte.length;

                        //coverting reponse to input stream
                        InputStream input = new ByteArrayInputStream(lByte);
                        File file = new File((String) lObjects[1]);
                        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
                        byte data[] = new byte[1024];

                        long total = 0;

                        while ((count = input.read(data)) != -1) {
                            total += count;
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    switch ((String) lObjects[0]) {
                        case PotMacros.LESSON_AUDIO:
                            if (null != m_cLessonsTable.getAudio())
                                checkAndDelete(m_cLessonsTable.getAudio());
                            m_cLessonsTable.setAudio((String) lObjects[1]);
                            break;
                        case PotMacros.LESSON_IMG_1:
                            if (null != m_cLessonsTable.getImg1())
                                checkAndDelete(m_cLessonsTable.getImg1());
                            m_cLessonsTable.setImg1((String) lObjects[1]);
                            break;
                        case PotMacros.LESSON_IMG_2:
                            if (null != m_cLessonsTable.getImg2())
                                checkAndDelete(m_cLessonsTable.getImg2());
                            m_cLessonsTable.setImg2((String) lObjects[1]);
                            break;
                        case PotMacros.LESSON_IMG_3:
                            if (null != m_cLessonsTable.getImg3())
                                checkAndDelete(m_cLessonsTable.getImg3());
                            m_cLessonsTable.setImg3((String) lObjects[1]);
                            break;
                    }
                    m_cLessonsTable.save();
                    if ((int) lObjects[2] > -1) {
                        displayToast(getResources().getString(R.string.lesson_saved_successfully_txt));
                        hideDialog();
                    }
                } else {
                    super.onAPIResponse(response, apiMethod, refObj);
                    hideDialog();
                }
                break;
        }
    }

    private void navigate() {
        if (mIsLessonMapped){
            Intent lObjIntent = new Intent(this, PotUserLessonScreen.class);
            switch (mLessFromWhere) {
                case PotMacros.OBJ_BOARDCHOICES:
                    lObjIntent.putExtra(PotMacros.LESSON_INDEX_PAGE, 2);
                    lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, mLessFromWhere);
                    lObjIntent.putExtra(PotMacros.LESSON_HEADER, m_cBoardChoice.getBoardclass().getName());
                    break;
                case PotMacros.OBJ_SYLLABI:
                    lObjIntent.putExtra(PotMacros.LESSON_INDEX_PAGE, 2);
                    lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, mLessFromWhere);
                    lObjIntent.putExtra(PotMacros.LESSON_HEADER, m_cSyllabi.getName());
                    break;
                case PotMacros.OBJ_CHAPTERS:
                    lObjIntent.putExtra(PotMacros.LESSON_INDEX_PAGE, 3);
                    lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, mLessFromWhere);
                    lObjIntent.putExtra(PotMacros.LESSON_HEADER, m_cChapters.getName());
                    break;
                case PotMacros.OBJ_LESSON:
                    lObjIntent.putExtra(PotMacros.LESSON_INDEX_PAGE, 3);
                    lObjIntent.putExtra(PotMacros.OBJ_LESSONFROM, PotMacros.OBJ_CHAPTERS);
                    lObjIntent.putExtra(PotMacros.LESSON_HEADER, m_cChapters.getName());
                    break;
            }
            lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
            lObjIntent.putExtra(PotMacros.OBJ_BOARDCHOICES, (new Gson()).toJson(m_cBoardChoice));
            lObjIntent.putExtra(PotMacros.OBJ_SYLLABI, (new Gson()).toJson(m_cSyllabi));
            lObjIntent.putExtra(PotMacros.OBJ_CHAPTERS, (new Gson()).toJson(m_cChapters));
            lObjIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(lObjIntent);
            finish();
        }else {
            if (mRequestResultIndex == 3){
                Intent lReturnIntent = new Intent();
                lReturnIntent.putExtra(PotMacros.LESSON_INDEX_PAGE, mRequestResultIndex);
                setResult(Activity.RESULT_OK, lReturnIntent);
                finish();
            }
            else {
                /*if (!mLessFromWhere.equalsIgnoreCase(PotMacros.OBJ_LESSON)) {*/
                    //this is to navigate to mine tab of general screen
                    mIsLessonMapped = true;
                    navigate();
                /*} else
                    onBackPressed();*/
            }
        }
    }

    //TODO: Call PUT apis and DELETE apis as req, which is not avail now
    public void callAttachmentsApi(){
        if (m_cAttachList.size() > 0) {
            if (m_cAttachList.containsKey(PotMacros.LESSON_AUDIO)) {
                if (((Attachments) m_cAttachList.get(PotMacros.LESSON_AUDIO)).getIsDeleted()) {
                    callDeleteAttachmentApi(((Attachments) m_cAttachList.get(PotMacros.LESSON_AUDIO)).getId(), PotMacros.LESSON_AUDIO);
                } else {
                    if (m_cAudioUrl == null) {
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(this, Uri.parse(PotMacros.getAudioFilePath(this) + "/" + m_cAudioGUID + ".aac"));
                        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        long millSecond = Long.parseLong(durationStr);
                        HashMap<String, String> lParams = new HashMap<>();
                        lParams.put(Constants.ATTACHMENT_TYPE, Constants.AUDIO);
                        lParams.put(Constants.SLOT, Integer.toString(1));
                        lParams.put(Constants.LENGTH, Long.toString(millSecond));
                        RequestManager.getInstance(this).placeMultiPartRequest(Constants.LESSONS +
                                        mLessonID +
                                        "/" +
                                        Constants.ATTACHMENTS, Attachments.class, this, PotMacros.LESSON_AUDIO, lParams,
                                new File(PotMacros.getAudioFilePath(this), ((Attachments) m_cAttachList.get(PotMacros.LESSON_AUDIO)).getAttachment()),
                                Constants.ATTACHMENT);
                    } else {
                        m_cAttachList.remove(PotMacros.LESSON_AUDIO);
                        callAttachmentsApi();
                    }
                }
//                m_cAttachList.remove(PotMacros.LESSON_AUDIO);
            } else if (m_cAttachList.containsKey(PotMacros.LESSON_IMG_1)) {
                if (((Attachments) m_cAttachList.get(PotMacros.LESSON_IMG_1)).getIsDeleted()) {
                    callDeleteAttachmentApi(((Attachments) m_cAttachList.get(PotMacros.LESSON_IMG_1)).getId(), PotMacros.LESSON_IMG_1);
                } else {
                    if ((new File(PotMacros.getImageFilePath(this), ((Attachments) m_cAttachList.get(PotMacros.LESSON_IMG_1)).getAttachment()).exists())) {
                        HashMap<String, String> lParams = new HashMap<>();
                        lParams.put(Constants.ATTACHMENT_TYPE, Constants.IMAGE);
                        lParams.put(Constants.SLOT, Integer.toString(1));
                        RequestManager.getInstance(this).placeMultiPartRequest(Constants.LESSONS +
                                        mLessonID +
                                        "/" +
                                        Constants.ATTACHMENTS, Attachments.class, this, PotMacros.LESSON_IMG_1, lParams,
                                new File(PotMacros.getImageFilePath(this), ((Attachments) m_cAttachList.get(PotMacros.LESSON_IMG_1)).getAttachment()),
                                Constants.ATTACHMENT);
                    } else {
                        m_cAttachList.remove(PotMacros.LESSON_IMG_1);
                        callAttachmentsApi();
                    }
                }
//                m_cAttachList.remove(PotMacros.LESSON_IMG_1);
            } else if (m_cAttachList.containsKey(PotMacros.LESSON_IMG_2)) {
                if (((Attachments) m_cAttachList.get(PotMacros.LESSON_IMG_2)).getIsDeleted()) {
                    callDeleteAttachmentApi(((Attachments) m_cAttachList.get(PotMacros.LESSON_IMG_2)).getId(), PotMacros.LESSON_IMG_2);
                } else {
                    if ((new File(PotMacros.getImageFilePath(this), ((Attachments) m_cAttachList.get(PotMacros.LESSON_IMG_2)).getAttachment()).exists())) {
                        HashMap<String, String> lParams = new HashMap<>();
                        lParams.put(Constants.ATTACHMENT_TYPE, Constants.IMAGE);
                        lParams.put(Constants.SLOT, Integer.toString(2));
                        RequestManager.getInstance(this).placeMultiPartRequest(Constants.LESSONS +
                                        mLessonID +
                                        "/" +
                                        Constants.ATTACHMENTS, Attachments.class, this, PotMacros.LESSON_IMG_2, lParams,
                                new File(PotMacros.getImageFilePath(this), ((Attachments) m_cAttachList.get(PotMacros.LESSON_IMG_2)).getAttachment()),
                                Constants.ATTACHMENT);
                    } else {
                        m_cAttachList.remove(PotMacros.LESSON_IMG_2);
                        callAttachmentsApi();
                    }
                }
//                m_cAttachList.remove(PotMacros.LESSON_IMG_2);
            } else if (m_cAttachList.containsKey(PotMacros.LESSON_IMG_3)) {
                if (((Attachments) m_cAttachList.get(PotMacros.LESSON_IMG_3)).getIsDeleted()) {
                    callDeleteAttachmentApi(((Attachments) m_cAttachList.get(PotMacros.LESSON_IMG_3)).getId(), PotMacros.LESSON_IMG_3);
                } else {
                    if ((new File(PotMacros.getImageFilePath(this), ((Attachments) m_cAttachList.get(PotMacros.LESSON_IMG_3)).getAttachment()).exists())) {
                        HashMap<String, String> lParams = new HashMap<>();
                        lParams.put(Constants.ATTACHMENT_TYPE, Constants.IMAGE);
                        lParams.put(Constants.SLOT, Integer.toString(3));
                        RequestManager.getInstance(this).placeMultiPartRequest(Constants.LESSONS +
                                        mLessonID +
                                        "/" +
                                        Constants.ATTACHMENTS, Attachments.class, this, PotMacros.LESSON_IMG_3, lParams,
                                new File(PotMacros.getImageFilePath(this), ((Attachments) m_cAttachList.get(PotMacros.LESSON_IMG_3)).getAttachment()),
                                Constants.ATTACHMENT);
                    } else {
                        m_cAttachList.remove(PotMacros.LESSON_IMG_3);
                        callAttachmentsApi();
                    }
                }
//                m_cAttachList.remove(PotMacros.LESSON_IMG_3);
            }
        }else {
            navigate();
        }
    }

    private void callDeleteAttachmentApi(Integer pAttachmentId, String pAttachmentType) {
        displayProgressBar(-1, "");
        RequestManager.getInstance(this).placeDeleteRequest(Constants.LESSONS +
                        mLessonID +
                        "/" +
                        Constants.ATTACHMENTS +
                        pAttachmentId +
                        "/",
                Attachments.class, this, pAttachmentType, null, null, true);
    }

    private void callDeleteLessonApi() {
        displayProgressBar(-1, "");
        RequestManager.getInstance(this).placeDeleteRequest(Constants.CHAPTERS +
                        m_cLessons.getChapter().getId() +
                        "/" +
                        Constants.LESSONS +
                        m_cLessons.getId() +
                        "/",
                Attachments.class, this, null, null, null, true);
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        if (apiMethod.contains(Constants.PUBLICSHARE) ||
                apiMethod.contains(Constants.USERS) ||
                apiMethod.contains(Constants.LESSON_LIKES) ||
                apiMethod.contains(Constants.LESSON_COMMENTS) ||
                apiMethod.contains(Constants.SOURCES) ||
                apiMethod.contains(Constants.BOARDCLASSES) ||
                apiMethod.contains(Constants.POST) ||
                apiMethod.contains(Constants.CHAPTERS) ||
                apiMethod.contains(Constants.ATTACHMENTS) ||
                apiMethod.contains(Constants.LESSONS)) {
            hideDialog();
            if (error instanceof NoConnectionError) {
                Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
            } else {
                String lMsg = new String(error.networkResponse.data);
                showErrorMsg(lMsg);
            }
            ButterKnife.apply(saveTxt, PotMacros.SETENABLED, true);
            saveTxt.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            super.onErrorResponse(error, apiMethod, refObj);
            hideDialog();
        }
    }

    public void showGallery() {
        verifyStoragePermissions(this);
//        m_cObjUIHandler.sendEmptyMessage(DELETE_CACHES);
        m_cImageGUID = PotMacros.getGUID();
        File lFile = new File(PotMacros.getImageFilePath(this), m_cImageGUID + ".jpg");
        Uri imageUri = Uri.fromFile(lFile);

        // Gallery.
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(galleryIntent, TAKE_PICTURE);
    }

    public void takePhoto() {
//            m_cObjUIHandler.sendEmptyMessage(DELETE_CACHES);
        m_cImageGUID = PotMacros.getGUID();
        File lFile = new File(PotMacros.getImageFilePath(this), m_cImageGUID + ".jpg");
        Uri imageUri = Uri.fromFile(lFile);

        // Camera.
        //		final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(captureIntent, TAKE_PICTURE);
    }

    class captureImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            m_cImageProcessing = true;
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                File lFile = new File(PotMacros.getImageFilePath(LessonsScreen.this), m_cImageGUID + ".jpg");
                if (lFile.exists()) {
                    //This inSampleSize option reduces memory consumption
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    m_cObjSelectedBitMap = BitmapFactory.decodeFile(lFile.getAbsolutePath(), options);

                    ExifInterface lObjExIf = new ExifInterface(lFile.getAbsolutePath());
                    //TODO addded for double cross check
                    int orientation = lObjExIf.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    Matrix matrix = new Matrix();
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                    }

                    //TODO : compress only camera images not the gallery
                    m_cObjSelectedBitMap = Bitmap.createBitmap(m_cObjSelectedBitMap, 0, 0,
                            m_cObjSelectedBitMap.getWidth(), m_cObjSelectedBitMap.getHeight(), matrix, true);

                    m_cObjSelectedBitMap = Utilities.scaleCameraImage(LessonsScreen.this, m_cObjSelectedBitMap);

                    pTakePicture = true;
                    pSavePicture = false;

                    if (null != m_cObjSelectedBitMap) {
                        if (lFile.exists()) {
                            lFile.delete();
                        }
                        FileOutputStream outStream = new FileOutputStream(lFile.getAbsoluteFile());
                        m_cObjSelectedBitMap.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
                        outStream.flush();
                        outStream.close();
                        outStream = null;
                    }
                    //todo uncomment to save picture
//                    savePicture(lDate);

                    m_cObjSelectedBitMap = Utilities.scaleCameraImage(LessonsScreen.this, m_cObjSelectedBitMap);

                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.print(e.getCause() + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideDialog();
            m_cImageProcessing = false;
            hideDialog();
            ImageView lview = null;
            try {
                switch (m_cCurrentImgId) {
                    case R.id.LESSON_IMG_1:
                        lview = mLessonImg1;
                        m_cAttachList.put(PotMacros.LESSON_IMG_1, addAttachment(-1, PotMacros.LESSON_IMG_1, m_cImageGUID + ".jpg"));
                        mLessonImg1.setTag(R.id.IMG_PATH, PotMacros.getImageFilePath(LessonsScreen.this) + "/" + m_cImageGUID + ".jpg");
                        break;
                    case R.id.LESSON_IMG_2:
                        lview = mLessonImg2;
                        m_cAttachList.put(PotMacros.LESSON_IMG_2, addAttachment(-1, PotMacros.LESSON_IMG_2, m_cImageGUID + ".jpg"));
                        mLessonImg2.setTag(R.id.IMG_PATH, PotMacros.getImageFilePath(LessonsScreen.this) + "/" + m_cImageGUID + ".jpg");
                        break;
                    case R.id.LESSON_IMG_3:
                        lview = mLessonImg3;
                        m_cAttachList.put(PotMacros.LESSON_IMG_3, addAttachment(-1, PotMacros.LESSON_IMG_3, m_cImageGUID + ".jpg"));
                        mLessonImg3.setTag(R.id.IMG_PATH, PotMacros.getImageFilePath(LessonsScreen.this) + "/" + m_cImageGUID + ".jpg");
                        break;
                    default:
                        lview = mLessonImg1;
                        break;
                }
                m_cIsEdited = true;
                Picasso.with(LessonsScreen.this)
                        .load(new File(PotMacros.getImageFilePath(LessonsScreen.this), m_cImageGUID + ".jpg"))
                        .error(R.drawable.profile_placeholder)
                        .placeholder(R.drawable.profile_placeholder)
                        .config(Bitmap.Config.RGB_565)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .fit()
                        .into(lview);
            } catch (Exception e) {
                lview.setBackgroundResource(R.drawable.profile_placeholder);
                e.printStackTrace();
            }
        }
    }
}
