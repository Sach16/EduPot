package com.cosmicdew.lessonpot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.interfaces.RefreshEditProfileListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTouch;
import butterknife.Optional;

/**
 * Created by chandrasekhar on 1/3/17.
 */

public class PotProfileInfoFragment extends PotFragmentBaseClass implements CompoundButton.OnCheckedChangeListener,
        View.OnTouchListener {

    @Nullable
    @BindView(R.id.MAIN_RL)
    RelativeLayout m_cRelL;

    @Nullable
    @BindView(R.id.ACC_ID_SWITCH)
    Switch m_cAccIdSwch;

    @Nullable
    @BindView(R.id.BIO_EDIT)
    EditText m_cBioEdit;

    private int m_cPos = -1;
    private Users m_cUsers;

    private boolean m_cIsBioTouched;

    private static RefreshEditProfileListener m_cRefreshListener;

    public PotProfileInfoFragment() {
        super();
    }

    public PotProfileInfoFragment newInstance(int pPosition, Users pUsers, RefreshEditProfileListener pRefreshEditProfileListener) {
        PotProfileInfoFragment lPotAdBoardScreen = new PotProfileInfoFragment();

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
        m_cObjMainView = inflater.inflate(R.layout.fragment_profile_info, container, false);
        ButterKnife.bind(this, m_cObjMainView);

        m_cObjMainActivity.m_cObjFragmentBase = PotProfileInfoFragment.this;

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

        m_cAccIdSwch.setChecked(m_cUsers.getShowAccountId());
        m_cBioEdit.setText(m_cUsers.getBio());
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what) {
        }
    }

    @Optional
    @OnClick({R.id.APPLY_TXT})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.APPLY_TXT:
                JSONObject lJO = new JSONObject();
                String lStrObj = null;
                try {
                    lJO.put(Constants.BIO, m_cBioEdit.getText().toString().trim());
                    lStrObj = Constants.BIO;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                placeUnivRequest(Constants.USERS +
                                m_cUsers.getId() +
                                "/",
                        Users.class, new Object[]{lStrObj, true}, null, lJO.toString(), Request.Method.PATCH);
                break;
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.USERS)) {
                    m_cObjMainActivity.hideDialog();
                    Users lUsers = (Users) response;
                    Object[] lObjects = (Object[]) refObj;
                    if (null != lUsers) {
                        m_cUsers = lUsers;
                        switch ((String) lObjects[0]) {
                            case Constants.SHOW_ACCOUNT_ID:
                                if ((boolean) lObjects[1])
                                    m_cObjMainActivity.displaySnack(m_cRelL, "Include Account Id Enabled");
                                else
                                    m_cObjMainActivity.displaySnack(m_cRelL, "Include Account Id Disabled");
                                break;
                            case Constants.BIO:
                                m_cObjMainActivity.displaySnack(m_cRelL, "Modification Saved");
                                break;
                        }
                        m_cRefreshListener.onUpdate(m_cPos, lUsers, null);
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
                if (apiMethod.contains(Constants.USERS)) {
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

    public void onBackPressed() {
        m_cObjMainActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public int getPos() {
        return m_cPos;
    }

    @Optional
    @OnCheckedChanged({R.id.ACC_ID_SWITCH})
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.ACC_ID_SWITCH:
                if (m_cIsBioTouched) {
                    m_cIsBioTouched = false;
                    callSwitchApi(isChecked, 0);
                }
                break;
        }
    }

    private void callSwitchApi(boolean isChecked, int pCase) {
        String lStrObj = null;
        JSONObject lJO = new JSONObject();
        try {
            switch (pCase) {
                case 0:
                    lJO.put(Constants.SHOW_ACCOUNT_ID, isChecked);
                    lStrObj = Constants.SHOW_ACCOUNT_ID;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        placeUnivRequest(Constants.USERS +
                        m_cUsers.getId() +
                        "/",
                Users.class, new Object[]{lStrObj, isChecked}, null, lJO.toString(), Request.Method.PATCH);
    }

    @Optional
    @OnTouch({R.id.ACC_ID_SWITCH})
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.ACC_ID_SWITCH:
                m_cIsBioTouched = true;
                break;
        }
        return false;
    }
}
