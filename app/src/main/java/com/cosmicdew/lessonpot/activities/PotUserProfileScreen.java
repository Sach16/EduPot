package com.cosmicdew.lessonpot.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.customviews.TouchImageView;
import com.cosmicdew.lessonpot.customviews.UserCircularImageView;
import com.cosmicdew.lessonpot.fragments.PotUserNetworkFollowersFragment;
import com.cosmicdew.lessonpot.interfaces.RecyclerSelectionListener;
import com.cosmicdew.lessonpot.interfaces.RefreshEditProfileListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.Follows;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 27/2/17.
 */

public class PotUserProfileScreen extends PotBaseActivity implements RefreshEditProfileListener, RecyclerSelectionListener {

    @Nullable
    @BindView(R.id.MAIN_RL)
    CoordinatorLayout m_cMainCL;

    @Nullable
    @BindView(R.id.EDIT_PRO_MAIN_RL)
    RelativeLayout m_cMainReLay;
    
    @Nullable
    @BindView(R.id.USER_CIV_CELL)
    UserCircularImageView m_cUserCivCell;

    @Nullable
    @BindView(R.id.USER_FULL_NAME_TXT)
    TextView m_cNameTxt;

    @Nullable
    @BindView(R.id.USER_ROLL_TXT)
    TextView m_cRoleTxt;

    @Nullable
    @BindView(R.id.FOLLOW_STATUS_TXT)
    TextView m_cFollowStatusTxt;

    @Nullable
    @BindView(R.id.FOLLOWERS_COUNT_TXT)
    TextView m_cFollowersCountTxt;

    @Nullable
    @BindView(R.id.FOLLOWERS_COUNT_DESC_TXT)
    TextView m_cFollowersCountDescTxt;

    @Nullable
    @BindView(R.id.ACC_ID_RL)
    RelativeLayout m_cAccIdRl;

    @Nullable
    @BindView(R.id.ACCOUNT_ID_DESC_TXT)
    TextView m_cAccountIdDescTxt;

    @Nullable
    @BindView(R.id.BIO_EDIT)
    TextView m_cBioEdittxt;

    @Nullable
    @BindView(R.id.FOLLOW_TXT)
    TextView m_cFollowTxt;

    @Nullable
    @BindView(R.id.BOTTOM_VIEW)
    FrameLayout viewStub;

    @Nullable
    @BindView(R.id.USER_IMG)
    TouchImageView bottomImg;

    BottomSheetBehavior<FrameLayout> m_cSlider;

    public static final int TAKE_PICTURE = 101;
    public static final int PROCESS_PICTURE = 111;

    public static final int CALL_PROCESS_PICTURE = 171;
    public static final int CALL_FIRST_NAME = 172;
    public static final int CALL_LAST_NAME = 173;

    private int m_cCurrentImgId;

    boolean m_cImageProcessing;
    private Bitmap m_cObjSelectedBitMap;

    private Users m_cUser;
    private Lessons m_cLessons;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.user_profile_screen);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(getResources().getString(R.string.user_profile_txt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);
        m_cLessons = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_LESSON), Lessons.class);
        if (null != m_cUser) {
            try {
                Picasso.with(PotUserProfileScreen.this)
                        .load(m_cUser.getProfilePic())
                        .error(R.drawable.profile_placeholder)
                        .placeholder(R.drawable.profile_placeholder)
                        .config(Bitmap.Config.RGB_565)
                        .fit()
                        .into(m_cUserCivCell);
            } catch (Exception e) {
                e.printStackTrace();
            }
            m_cNameTxt.setText(m_cUser.getFirstName() + " " + m_cUser.getLastName());
            m_cRoleTxt.setText(m_cUser.getRoleTitle());
            m_cFollowersCountTxt.setText(String.valueOf(m_cUser.getFollowers()));
            if (m_cUser.getFollowers() > 0) {
                m_cFollowersCountTxt.setTextColor(getResources().getColor(R.color.colorAccent));
                m_cFollowersCountDescTxt.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            if (m_cUser.getRelationship().equalsIgnoreCase(getResources().getString(R.string.none_txt)))
                m_cFollowTxt.setVisibility(View.VISIBLE);
            m_cFollowStatusTxt.setText(PotMacros.s2l(m_cUser.getRelationship()));
            m_cBioEdittxt.setText(m_cUser.getBio());
            if (null != m_cUser.getUsername()) {
                m_cAccountIdDescTxt.setText(m_cUser.getUsername());
            } else {
                m_cAccIdRl.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onUpdate(int pPostion, Users pUsers, View pView) {
        switch (pPostion){
            case 0:
                break;
            case 1:
                break;
            case 2:
                m_cUser = pUsers;
                break;
        }
    }

    @Override
    public void onInfoClicked(int pPostion, BoardChoices pBoardChoices, Syllabi pSyllabi, Chapters pChapters, Lessons pLessons, String pFragID) {

    }

    @Optional
    @OnClick({R.id.PROFILE_PIC_LL, R.id.NO_FOLLOWERS_LL,
            R.id.PAGER_DELETE_IMG, R.id.FOLLOW_TXT})
    public void onClick(View v) {
        Intent lObjIntent;
        switch (v.getId()) {
            case R.id.PROFILE_PIC_LL:
                if (m_cUser.getProfilePic() != null)
                    showBottomSheet(m_cUserCivCell);
                break;
            case R.id.NO_FOLLOWERS_LL:
                if (m_cUser.getFollowers() > 0) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.EDIT_PRO_MAIN_RL, new PotUserNetworkFollowersFragment().newInstance(0, null, m_cUser, PotMacros.FRAG_FOLLOWERS, this), PotMacros.FRAG_FOLLOWERS)
                            .commit();
                    switchModeFragment(true, PotMacros.FRAG_FOLLOWERS);
                }
                break;
            case R.id.PAGER_DELETE_IMG:
                viewStub.setVisibility(View.GONE);
                break;
            case R.id.FOLLOW_TXT:
                hideDialog();
                JSONObject lJO = new JSONObject();
                try {
                    lJO.put(Constants.TO_USER, m_cUser.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestManager.getInstance(this).placeUserRequest(Constants.FOLLOWERS, Follows.class, this, null, null, lJO.toString(), true);
                break;
        }
    }

    private void switchModeFragment(boolean pIsFragment, String pFragTag) {
        switch (pFragTag){
            case PotMacros.FRAG_FOLLOWERS:
                getSupportActionBar().setTitle(getResources().getString(R.string.followers_txt));
                break;
            default:
                getSupportActionBar().setTitle(getResources().getString(R.string.edit_profile_txt));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (viewStub.getVisibility() == View.VISIBLE)
            viewStub.setVisibility(View.GONE);
        else {
            if (getSupportFragmentManager().findFragmentByTag(PotMacros.FRAG_FOLLOWERS) != null) {
                ((PotUserNetworkFollowersFragment) getSupportFragmentManager().findFragmentByTag(PotMacros.FRAG_FOLLOWERS)).onBackPressed();
                switchModeFragment(false, "");
            } else {
                Intent lReturnIntent = new Intent();
                lReturnIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                setResult(Activity.RESULT_OK, lReturnIntent);
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        Intent lObjIntent;
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.FOLLOWERS)){
                    Follows lFollows = (Follows) response;
                    if (null != lFollows){
                    }
                    hideDialog();
                }else {
                    super.onAPIResponse(response, apiMethod, refObj);
                    hideDialog();
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.CREDENTIALS)){
                    hideDialog();
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
                    } else {
                        String lMsg = new String(error.networkResponse.data);
                        showErrorMsg(lMsg);
                    }
                }else {
                    super.onErrorResponse(error, apiMethod, refObj);
                    hideDialog();
                }
                break;
        }
    }

    private void showBottomSheet(ImageView pLessonImgTag) {
        m_cSlider = BottomSheetBehavior.from(viewStub);
        m_cSlider.setState(BottomSheetBehavior.STATE_EXPANDED);

        try {
            Picasso.with(PotUserProfileScreen.this)
                    .load(m_cUser.getProfilePic())
                    .error(R.drawable.profile_placeholder)
                    .placeholder(R.drawable.profile_placeholder)
                    .config(Bitmap.Config.RGB_565)
                    .fit()
                    .into(bottomImg);
        }catch (Exception e){
            e.printStackTrace();
        }

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
}
