package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.interfaces.RecyclerClassBoardsListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.KeyValue;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.models.UsersAll;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 28/9/16.
 */

public class RegisterationScreen extends PotBaseActivity implements RecyclerClassBoardsListener{

    @Nullable
    @BindView(R.id.REL_LAY)
    RelativeLayout m_cRelLay;

    @Nullable
    @BindView(R.id.FIRST_NAME)
    EditText etFirstName;

    @Nullable
    @BindView(R.id.LAST_NAME)
    EditText etLastName;

    @Nullable
    @BindView(R.id.ROLE_TXT)
    TextView m_cRoleTxt;

    ArrayList<BoardChoices> m_cBoardChoiceList;

    private Users m_cUser;
    private int m_cRolePos = -1;
    ArrayList<String> m_cRolesList;
    HashMap<String, String> m_cRolesDic;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.reg_screen);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(getResources().getString(R.string.register_new_user));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        if (null != getIntent().getStringExtra(PotMacros.OBJ_USER)) {
            m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);
            if (null != m_cUser) {
                etFirstName.setText(m_cUser.getFirstName());
                etLastName.setText(m_cUser.getLastName());
//                etRoleName.setText(m_cUser.getRole());
            }
        }


        //Calling team api
        displayProgressBar(-1, "Loading");
        RequestManager.getInstance(this).placeRequest(Constants.ROLES, KeyValue[].class, this, null, null, null, false);
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what){
            case R.id.ROLE_TXT:
                m_cRolePos = pObjMessage.arg1;
                m_cRoleTxt.setText(m_cRolesList.get(pObjMessage.arg1));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (null != getIntent().getStringExtra(PotMacros.OBJ_USER))
            getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                deleteUser();
//                Use the below raw code if required
//                new DeleteUser().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteUser() {
        displayProgressBar(-1, "");
        RequestManager.getInstance(this).placeDeleteRequest(Constants.USERS + m_cUser.getId(), Users.class, this, null, null, null, true);
    }

    @Override
    public void onInfoClick(int pPostion, BoardChoices pBoardChoices, View pView) {
//        displayDocDialog(m_cRolePos, false);
    }

    @Override
    public void onInfoLongClick(int pPostion, BoardChoices pBoardChoices, View pView) {
    }

    @Override
    public void onSelectionClicked(int pPostion, BoardChoices pBoardChoices, View pView) {

    }

    public class DeleteUser extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            if (null != m_cUser) {
                try {
                    URL url = new URL("http://52.66.119.49/api/v1/" + Constants.USERS + m_cUser.getId());
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
//                    httpCon.setDoOutput(true);
                    httpCon.setRequestProperty(
                            "Content-Type", "application/x-www-form-urlencoded");
                    httpCon.setRequestMethod("DELETE");
                    httpCon.setRequestProperty(Constants.AUTHORIZATION, PotMacros.getLoginAuth(RegisterationScreen.this));
                    httpCon.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Optional
    @OnClick({R.id.ROLE_TXT, R.id.NEXT_TXT, R.id.CANCEL_TXT})
    public void onClick(View v) {
        Intent lObjIntent;
        switch (v.getId()) {
            case R.id.ROLE_TXT:
                if (null != m_cRolesList && m_cRolesList.size() > 0)
                    displaySpinnerDialog(v.getId(), getResources().getString(R.string.select_role_txt), m_cRolesList, null);
                break;
            case R.id.NEXT_TXT:
                if (validateCred()) {
                    //check for user existance api
                    displayProgressBar(-1, "");
                    HashMap<String, String> lParams = new HashMap<>();
                    lParams.put(Constants.FIRST_NAME, etFirstName.getText().toString().trim());
                    lParams.put(Constants.LAST_NAME, etLastName.getText().toString().trim());
                    lParams.put(Constants.ROLE, m_cRolesDic.get(m_cRolesList.get(m_cRolePos)));
                    RequestManager.getInstance(this).placeRequest(Constants.USERS, UsersAll.class, this, null, lParams, null, false);
                }
                break;
            case R.id.CANCEL_TXT:
                onBackPressed();
                break;
        }
    }

    public boolean validateCred() {
        boolean lRetVal = false;
        String letFirstName = etFirstName.getText().toString().trim();
        String letLastName = etLastName.getText().toString().trim();
        if (letFirstName.isEmpty()) {
            displaySnack(m_cRelLay, "Please Enter First Name");
            return false;
        } else if (letLastName.isEmpty()) {
            displaySnack(m_cRelLay, "Please Enter Last Name");
            return false;
        } else if (m_cRolePos < 0) {
            displaySnack(m_cRelLay, "Select Role");
            return false;
        } else {
            lRetVal = true;
        }
        return lRetVal;
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        Intent lObjIntent;
        switch (apiMethod) {
            case Constants.USERS:
                UsersAll lUsersAll = (UsersAll) response;
                if (null != lUsersAll && lUsersAll.getUsers().size() > 0) {
                    displayToast(getResources().getString(R.string.user_already_exists));
                }else {
                    lObjIntent = new Intent(this, PotBoardClassScreen.class);
                    lObjIntent.putExtra(Constants.FIRST_NAME, etFirstName.getText().toString().trim());
                    lObjIntent.putExtra(Constants.LAST_NAME, etLastName.getText().toString().trim());
                    lObjIntent.putExtra(Constants.ROLE, m_cRolesDic.get(m_cRolesList.get(m_cRolePos)));
                    startActivity(lObjIntent);
                }
                hideDialog();
                break;
            case Constants.ROLES:
                List<KeyValue> lRolesList = Arrays.asList((KeyValue[]) response);
                if (lRolesList != null && lRolesList.size() > 0) {
                    m_cRolesList = new ArrayList<>();
                    m_cRolesDic = new HashMap<>();
                    for (KeyValue lrole : lRolesList) {
                        m_cRolesList.add(lrole.getValue());
                        m_cRolesDic.put(lrole.getValue(), lrole.getKey());
                    }
//                    m_cRoleTxt.setText(m_cRolesList.get(0));
//                    m_cRolePos = 0;
                } else {
                    m_cRolesList = new ArrayList<>();
                    m_cRolesDic = new HashMap<>();
                    m_cRolesList.clear();
                    m_cRolesDic.clear();
                    m_cRoleTxt.setText(null);
                    m_cRolePos = -1;
                }
                hideDialog();
                break;
            default:
                super.onAPIResponse(response, apiMethod, refObj);
                hideDialog();
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod) {
            case Constants.USERS:
            case Constants.ROLES:
                hideDialog();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
                } else {
                    String lMsg = new String(error.networkResponse.data);
                    showErrorMsg(lMsg);
                }
                break;
            default:
                super.onErrorResponse(error, apiMethod, refObj);
                hideDialog();
                break;
        }
    }
}
