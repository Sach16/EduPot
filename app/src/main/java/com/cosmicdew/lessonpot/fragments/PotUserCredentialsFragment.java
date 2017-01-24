package com.cosmicdew.lessonpot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.interfaces.RefreshEditProfileListener;
import com.cosmicdew.lessonpot.macros.Credentials;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.models.UsersAll;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 29/12/16.
 */

public class PotUserCredentialsFragment extends PotFragmentBaseClass implements View.OnTouchListener{

    @Nullable
    @BindView(R.id.MAIN_RL)
    RelativeLayout m_cRelL;

    @Nullable
    @BindView(R.id.PASS_EDIT)
    EditText etPass;

    @Nullable
    @BindView(R.id.USER_ID_EDIT)
    EditText etUserId;

    private int m_cPos = -1;
    private Users m_cUser;

    private boolean m_cIsEditMode;
    private boolean m_cIsPassChanged;
    private boolean m_cIsAccIdChanged;
    private boolean m_cIsAccIdTouched;
    private boolean m_cIsPassTouched;

    private static final String RECEIVE_CREDENTIALS = "RECEIVE_CREDENTIALS";

    private static RefreshEditProfileListener m_cRefreshListener;

    public PotUserCredentialsFragment() {
        super();
    }

    public PotUserCredentialsFragment newInstance(int pPosition, Users pUsers, RefreshEditProfileListener pRefreshEditProfileListener) {
        PotUserCredentialsFragment lPotAdBoardScreen = new PotUserCredentialsFragment();
        this.m_cRefreshListener = pRefreshEditProfileListener;

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString(PotMacros.OBJ_USER, (new Gson()).toJson(pUsers));
        lPotAdBoardScreen.setArguments(args);

        return lPotAdBoardScreen;
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
        m_cObjMainView = inflater.inflate(R.layout.fragment_user_credentials, container, false);
        ButterKnife.bind(this, m_cObjMainView);

        m_cObjMainActivity.m_cObjFragmentBase = PotUserCredentialsFragment.this;

        init();

        return m_cObjMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        m_cPos = getArguments().getInt("Position");
        m_cUser = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_USER), Users.class);
        m_cIsAccIdChanged = false;
        m_cIsPassChanged = false;
        m_cIsAccIdTouched = false;
        m_cIsPassTouched = false;
        etUserId.addTextChangedListener(new TextWatcher() {
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
                if (m_cIsAccIdTouched)
                    if (m_cIsEditMode)
                        m_cIsAccIdChanged = true;
            }
        });
        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (m_cIsPassTouched)
                    if (m_cIsEditMode)
                        m_cIsPassChanged = true;
            }
        });


        //Call credentials api
        m_cObjMainActivity.displayProgressBar(-1, "Loading...");
        placeUserRequest(Constants.CREDENTIALS, UsersAll.class, RECEIVE_CREDENTIALS, null, null, false);
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what) {
        }
    }

    @Optional
    @OnClick({R.id.DONE_TXT, R.id.CANCEL_TXT})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.DONE_TXT:
                if (m_cIsEditMode) {
                    if (m_cIsAccIdChanged && m_cIsPassChanged) {
                        if (validateBoth()) {
                            callBoth();
                        }
                    } else if (m_cIsAccIdChanged) {
                        if (validateAccId()) {
                            m_cObjMainActivity.hideSoftKeyboard();
                            JSONObject lJO = new JSONObject();
                            try {
                                lJO.put(Constants.USERNAME, etUserId.getText().toString().trim());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            m_cObjMainActivity.displayProgressBar(-1, "");
                            placeUserRequest(Constants.CREDENTIALS, Credentials.class, null, null, lJO.toString(), true);
                        }
                    } else if (m_cIsPassChanged) {
                        if (validatePass()) {
                            m_cObjMainActivity.hideSoftKeyboard();
                            JSONObject lJO = new JSONObject();
                            try {
                                lJO.put(Constants.PASSWORD, etPass.getText().toString().trim());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            m_cObjMainActivity.displayProgressBar(-1, "");
                            placeUserRequest(Constants.CREDENTIALS, Credentials.class, null, null, lJO.toString(), true);
                        }
                    } else {
                        onBackPressed();
                    }
                } else {
                    if (validateBoth()) {
                        callBoth();
                    }
                }
                break;
            case R.id.CANCEL_TXT:
                onBackPressed();
                break;
        }
    }

    private void callBoth() {
        m_cObjMainActivity.hideSoftKeyboard();
        JSONObject lJO = new JSONObject();
        try {
            lJO.put(Constants.USERNAME, etUserId.getText().toString().trim());
            lJO.put(Constants.PASSWORD, etPass.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        m_cObjMainActivity.displayProgressBar(-1, "");
        placeUserRequest(Constants.CREDENTIALS, Credentials.class, null, null, lJO.toString(), true);
    }

    private boolean validateBoth() {
        boolean lRetVal = false;
        String lUserId = etUserId.getText().toString().trim();
        String lPass = etPass.getText().toString().trim();
        if (lUserId.isEmpty()) {
            m_cObjMainActivity.displaySnack(m_cRelL, "Enter Account Id");
            return false;
        } else if (!m_cObjMainActivity.isUsername(lUserId)) {
            m_cObjMainActivity.displaySnack(m_cRelL, "Account Id : minimum 6 characters");
            return false;
        } else if (lPass.isEmpty()) {
            m_cObjMainActivity.displaySnack(m_cRelL, "Enter Password");
            return false;
        } else if (!m_cObjMainActivity.isPassword(lPass)) {
            m_cObjMainActivity.displaySnack(m_cRelL, "Password : minimum 8 characters at least 1 Alphabet and 1 Number");
            return false;
        } else {
            lRetVal = true;
        }
        return lRetVal;
    }

    private boolean validateAccId() {
        boolean lRetVal = false;
        String lUserId = etUserId.getText().toString().trim();
        String lPass = etPass.getText().toString().trim();
        if (lUserId.isEmpty()) {
            m_cObjMainActivity.displaySnack(m_cRelL, "Enter Account Id");
            return false;
        } else if (!m_cObjMainActivity.isUsername(lUserId)) {
            m_cObjMainActivity.displaySnack(m_cRelL, "Account Id : minimum 6 characters");
            return false;
        } else {
            lRetVal = true;
        }
        return lRetVal;
    }

    private boolean validatePass() {
        boolean lRetVal = false;
        String lUserId = etUserId.getText().toString().trim();
        String lPass = etPass.getText().toString().trim();
        if (lPass.isEmpty()) {
            m_cObjMainActivity.displaySnack(m_cRelL, "Enter Password");
            return false;
        } else if (!m_cObjMainActivity.isPassword(lPass)) {
            m_cObjMainActivity.displaySnack(m_cRelL, "Password : minimum 8 characters at least 1 Alphabet and 1 Number");
            return false;
        } else {
            lRetVal = true;
        }
        return lRetVal;
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            case Constants.CREDENTIALS:
                m_cObjMainActivity.hideDialog();
                if (refObj == null) {
                    Credentials lCredentials = (Credentials) response;
                    if (null != lCredentials) {
                        m_cObjMainActivity.displayToast(getResources().getString(R.string.user_cred_updated_txt));
                        m_cRefreshListener.onUpdate(m_cPos, m_cUser, null);
                        onBackPressed();
                    }
                }else {
                    UsersAll lUsersAll = (UsersAll) response;
                    if (null != lUsersAll && lUsersAll.getUsers().size() > 0) {
                        etUserId.setText(lUsersAll.getUsers().get(0).getUsername());
                        if (lUsersAll.getUsers().get(0).getUsername().length() > 0) {
                            etPass.setHint("********");
                            m_cIsEditMode = true;
                        }
                    }
                }
                break;
            default:
                super.onAPIResponse(response, apiMethod, refObj);
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod) {
            case Constants.CREDENTIALS:
                m_cObjMainActivity.hideDialog();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(m_cObjMainActivity, "Please check Network connection", Toast.LENGTH_SHORT).show();
                } else {
                    String lMsg = new String(error.networkResponse.data);
                    m_cObjMainActivity.showErrorMsg(lMsg);
                }
                break;
            default:
                super.onErrorResponse(error, apiMethod, refObj);
                break;
        }
    }

    public void onBackPressed() {
        m_cObjMainActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public int getPos() {
        return m_cPos;
    }

    @Optional
    @OnTouch({R.id.PASS_EDIT, R.id.USER_ID_EDIT})
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()){
            case R.id.USER_ID_EDIT:
                m_cIsAccIdTouched = true;
                break;
            case R.id.PASS_EDIT:
                m_cIsPassTouched = true;
                break;
        }
        return false;
    }
}
