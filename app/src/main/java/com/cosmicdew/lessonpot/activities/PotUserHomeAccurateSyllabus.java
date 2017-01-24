package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.models.UsersAll;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 12/1/17.
 */

public class PotUserHomeAccurateSyllabus extends PotBaseActivity {

    @Nullable
    @BindView(R.id.HOME_CLASS_MAIN_LAYOUT)
    RelativeLayout m_cRlMain;

    private Users m_cUser;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.pot_user_home_accurate_syllabus);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(getResources().getString(R.string.ways_to_get_syllabus_accurate_txt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);
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
    protected void handleUIMessage(Message pObjMessage) {
        Intent lObjIntent;
        switch (pObjMessage.what) {
            case PotMacros.SUPPORT_OPTION:
                String[] lTitCom = ((String[]) pObjMessage.obj);
                if (null != lTitCom) {
                    displayProgressBar(-1, "");
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.NAME, lTitCom[0]);
                        lJO.put(Constants.COMMENTS, lTitCom[1]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    displayProgressBar(-1, "Loading...");
                    RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS,
                            Lessons.class, this, null, null, lJO.toString(), true);
                }
                break;
        }
    }

    @Optional
    @OnClick({R.id.RIGHT_SYLLABUS_HELP_LL})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.RIGHT_SYLLABUS_HELP_LL:
                displaySupportDialog(PotMacros.SUPPORT_OPTION, getResources().getString(R.string.syllabus_help_txt), null, null, false);
                break;
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.SHARE)) {
                    LessonShares lessonShares = (LessonShares) response;
                    if (null != lessonShares) {
                        hideDialog();
                        onBackPressed();
                    }
                } else if (apiMethod.contains(Constants.USERS)) {
                    UsersAll lUsers = (UsersAll) response;
                    Lessons lessons = (Lessons) refObj;
                    if (lessons != null) {
                        JSONObject lJO = new JSONObject();
                        try {
                            JSONArray lArray = new JSONArray();
                            lArray.put(lUsers.getUsers().get(0).getId());
                            lJO.put(Constants.USERS_TXT, lArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        displayProgressBar(-1, "");
                        RequestManager.getInstance(this).placeUserRequest(Constants.LESSONS +
                                        lessons.getId() +
                                        "/" +
                                        Constants.SHARE,
                                LessonShares.class, this, null, null, lJO.toString(), true);
                    }
                } else if (apiMethod.contains(Constants.LESSONS)) {
                    Lessons lessons = (Lessons) response;
                    HashMap<String, String> lParams = new HashMap<>();
                    lParams.put(Constants.USERNAME, PotMacros.SUPPORTWIZARD_USERNAME);
                    displayProgressBar(-1, "");
                    RequestManager.getInstance(this).placeRequest(Constants.USERS,
                            UsersAll.class, this, lessons, lParams, null, false);
                } else {
                    super.onAPIResponse(response, apiMethod, refObj);
                }
                hideDialog();
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.USERS) ||
                        apiMethod.contains(Constants.SHARE) ||
                        apiMethod.contains(Constants.LESSONS) ||
                        apiMethod.contains(Constants.SESSIONS)) {
                    hideDialog();
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
                    } else {
                        String lMsg = new String(error.networkResponse.data);
                        showErrorMsg(lMsg);
                    }
                } else {
                    super.onErrorResponse(error, apiMethod, refObj);
                    hideDialog();
                }
                ;
                break;
        }
    }
}
