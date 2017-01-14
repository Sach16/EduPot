package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Connections;
import com.cosmicdew.lessonpot.models.KeyValue;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.models.UsersAll;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.google.gson.Gson;

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
 * Created by S.K. Pissay on 23/11/16.
 */

public class ConnectionScreen extends PotBaseActivity {

    @Nullable
    @BindView(R.id.RL_MAIN)
    RelativeLayout m_cRelLay;

    @Nullable
    @BindView(R.id.USER_ID_EDIT)
    EditText etUserId;

    @Nullable
    @BindView(R.id.TERMS_AND_CONDITION_NUMB)
    TextView m_cCountryCodetxt;

    @Nullable
    @BindView(R.id.PHONE_NUMBER_EDIT)
    EditText etPhoneNum;

    @Nullable
    @BindView(R.id.FIRST_NAME_EDIT)
    EditText etFirstName;

    @Nullable
    @BindView(R.id.LAST_NAME_EDIT)
    EditText etLastName;

    @Nullable
    @BindView(R.id.ROLE_TXT)
    TextView m_cRoleTxt;

    @Nullable
    @BindView(R.id.ADDNAL_INFO_LL)
    LinearLayout m_cAddnalLL;

    ArrayList<BoardChoices> m_cBoardChoiceList;

    private Users m_cUser;
    private int m_cRolePos = -1;
    ArrayList<String> m_cRolesList;
    HashMap<String, String> m_cRolesDic;

    private static final int MESSAGE_INFO = 9090;
    private boolean m_cIsAddnalOpen = false;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.add_connection);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(getResources().getString(R.string.add_connection_txt));
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
        }
        etPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etPhoneNum.getText().length() > 0)
                    etUserId.setEnabled(false);
                else
                    etUserId.setEnabled(true);

            }
        });
        etUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etUserId.getText().length() > 0)
                    etPhoneNum.setEnabled(false);
                else
                    etPhoneNum.setEnabled(true);
            }
        });


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
            case MESSAGE_INFO:
                if ((boolean)pObjMessage.obj) {
                    Uri uri = Uri.parse("sms:"/* + "8892348234"*/);
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    it.putExtra("sms_body", "The sms text");
                    startActivity(it);
                }
                break;
        }
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

    @Optional
    @OnClick({R.id.ROLE_TXT, R.id.ADD_CONNECTION_TXT,
            R.id.CANCEL_TXT})
    public void onClick(View v) {
        Intent lObjIntent;
        switch (v.getId()) {
            case R.id.ROLE_TXT:
                if (null != m_cRolesList && m_cRolesList.size() > 0)
                    displaySpinnerDialog(v.getId(), getResources().getString(R.string.select_role_txt), m_cRolesList, null);
                break;
            case R.id.ADD_CONNECTION_TXT:
                if (validate()) {
                    if (m_cIsAddnalOpen) {
                        //check for user existance api
                        displayProgressBar(-1, "");
                        HashMap<String, String> lParams = new HashMap<>();
                        if (!etPhoneNum.getText().toString().trim().isEmpty())
                            lParams.put(Constants.PHONE, m_cCountryCodetxt.getText().toString().trim() + etPhoneNum.getText().toString().trim());
                        if (!etUserId.getText().toString().trim().isEmpty())
                            lParams.put(Constants.USERNAME, etUserId.getText().toString().trim());
                        if (!etFirstName.getText().toString().trim().isEmpty())
                            lParams.put(Constants.FIRST_NAME, etFirstName.getText().toString().trim());
                        if (!etLastName.getText().toString().trim().isEmpty())
                            lParams.put(Constants.LAST_NAME, etLastName.getText().toString().trim());
                        if (m_cRolePos > -1)
                            lParams.put(Constants.ROLE, m_cRolesDic.get(m_cRolesList.get(m_cRolePos)));
                        RequestManager.getInstance(this).placeRequest(Constants.USERS, UsersAll.class, this, null, lParams, null, false);
                    } else {
                        //check for user existance api
                        displayProgressBar(-1, "");
                        HashMap<String, String> lParams = new HashMap<>();
                        if (!etPhoneNum.getText().toString().trim().isEmpty())
                            lParams.put(Constants.PHONE, m_cCountryCodetxt.getText().toString().trim() + etPhoneNum.getText().toString().trim());
                        if (!etUserId.getText().toString().trim().isEmpty())
                            lParams.put(Constants.USERNAME, etUserId.getText().toString().trim());
                        RequestManager.getInstance(this).placeRequest(Constants.USERS, UsersAll.class, this, null, lParams, null, false);
                    }
                }
                break;
            case R.id.CANCEL_TXT:
                onBackPressed();
                break;
        }
    }

    public boolean validate() {
        boolean lRetVal = false;
        String letUserId = etUserId.getText().toString().trim();
        String letPhoneNum = etPhoneNum.getText().toString().trim();
        if (letUserId.isEmpty() && letPhoneNum.isEmpty()) {
            displaySnack(m_cRelLay, "Please Provide UserID or Phone Number");
            return false;
        } else if (!isPhoneNoValid(letPhoneNum)) {
            displaySnack(m_cRelLay, "Please Enter Valid Phone Number");
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
            case Constants.USERS:
                UsersAll lUsersAll = (UsersAll) response;
                hideDialog();
                if (m_cIsAddnalOpen){
                    if (null != lUsersAll && lUsersAll.getUsers().size() == 0) {
                        displayAlert(-1, getResources().getString(R.string.alert_txt),
                                getResources().getString(R.string.invalid_user_txt));
                    } else if (null != lUsersAll && lUsersAll.getUsers().size() > 1) {
                        displayAlert(-1, getResources().getString(R.string.alert_txt),
                                getResources().getString(R.string.insufficient_info_txt));
                    } else if (null != lUsersAll && lUsersAll.getUsers().size() == 1){
                        displayProgressBar(-1, "");
                        JSONObject lJO = new JSONObject();
                        try {
                            lJO.put(Constants.CONNECTION, lUsersAll.getUsers().get(0).getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RequestManager.getInstance(this).placeUserRequest(Constants.CONNECTIONS, Connections.class, this, null, null, lJO.toString(), true);
                    }
                }else {
                    if (null != lUsersAll && lUsersAll.getUsers().size() == 0) {
                        displayYesOrNoAlert(MESSAGE_INFO, getResources().getString(R.string.alert_txt),
                                getResources().getString(R.string.sms_info_txt));
                    } else if (null != lUsersAll && lUsersAll.getUsers().size() > 1) {
                        displayAlert(-1, getResources().getString(R.string.alert_txt),
                                getResources().getString(R.string.insufficient_info_txt));
                        m_cAddnalLL.setVisibility(View.VISIBLE);
                        m_cIsAddnalOpen = true;
                        etPhoneNum.setEnabled(false);
                        etUserId.setEnabled(false);
                    } else if (null != lUsersAll && lUsersAll.getUsers().size() == 1){
                        displayProgressBar(-1, "");
                        JSONObject lJO = new JSONObject();
                        try {
                            lJO.put(Constants.CONNECTION, lUsersAll.getUsers().get(0).getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RequestManager.getInstance(this).placeUserRequest(Constants.CONNECTIONS, Connections.class, this, null, null, lJO.toString(), true);
                    }
                }
                break;
            case Constants.CONNECTIONS:
                Connections lConnections = (Connections) response;
                if (null != lConnections)
                    onBackPressed();
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
            case Constants.ROLES:
            case Constants.USERS:
            case Constants.CONNECTIONS:
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
