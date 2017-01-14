package com.cosmicdew.lessonpot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
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
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 30/12/16.
 */

public class PotRegisterPhoneNumbers extends PotFragmentBaseClass {

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
    private Users m_cUsers;

    private static RefreshEditProfileListener m_cRefreshListener;

    public PotRegisterPhoneNumbers() {
        super();
    }

    public PotRegisterPhoneNumbers newInstance(int pPosition, Users pUsers, RefreshEditProfileListener pRefreshEditProfileListener) {
        PotRegisterPhoneNumbers lPotAdBoardScreen = new PotRegisterPhoneNumbers();

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
        m_cObjMainView = inflater.inflate(R.layout.fragment_reg_phone_numbers, container, false);
        ButterKnife.bind(this, m_cObjMainView);

        m_cObjMainActivity.m_cObjFragmentBase = PotRegisterPhoneNumbers.this;

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
        m_cUsers = (new Gson()).fromJson(getArguments().getString(PotMacros.OBJ_USER), Users.class);
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
                if (validate()) {
                    m_cObjMainActivity.hideSoftKeyboard();
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.USERNAME, etUserId.getText().toString().trim());
                        lJO.put(Constants.PASSWORD, etPass.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    m_cObjMainActivity.displayProgressBar(-1, "");
                    placeRequest(Constants.CREDENTIALS, Credentials.class, null, null, lJO.toString(), true);
                }
                break;
            case R.id.CANCEL_TXT:
                onBackPressed();
                break;
        }
    }

    private boolean validate() {
        boolean lRetVal = false;
        String lUserId = etUserId.getText().toString().trim();
        String lPass = etPass.getText().toString().trim();
        if (lUserId.isEmpty()) {
            m_cObjMainActivity.displaySnack(m_cRelL, "Enter UserId");
            return false;
        }  else if (!m_cObjMainActivity.isPassword(lUserId)) {
            m_cObjMainActivity.displaySnack(m_cRelL, "Minimum 6 characters at least 1 Alphabet and 1 Number");
            return false;
        } else if (lPass.isEmpty()) {
            m_cObjMainActivity.displaySnack(m_cRelL, "Enter Password");
            return false;
        }else if (!m_cObjMainActivity.isPassword(lPass)) {
            m_cObjMainActivity.displaySnack(m_cRelL, "Minimum 8 characters at least 1 Alphabet and 1 Number");
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
                Credentials lCredentials = (Credentials) response;
                if (null != lCredentials){
                    m_cRefreshListener.onUpdate(m_cPos, m_cUsers, null);
                    onBackPressed();
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
}
