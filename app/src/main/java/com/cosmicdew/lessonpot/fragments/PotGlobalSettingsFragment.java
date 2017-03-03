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
 * Created by S.K. Pissay on 25/1/17.
 */

public class PotGlobalSettingsFragment extends PotFragmentBaseClass implements CompoundButton.OnCheckedChangeListener,
        View.OnTouchListener {

    @Nullable
    @BindView(R.id.MAIN_RL)
    RelativeLayout m_cRelL;

    @Nullable
    @BindView(R.id.SHARES_SWITCH)
    Switch m_cSharesSwch;

    @Nullable
    @BindView(R.id.OFFLINE_SWITCH)
    Switch m_cOfflineSwch;

    private int m_cPos = -1;
    private Users m_cUsers;

    private boolean m_cIsShareTouched;
    private boolean m_cIsOfflineTouched;

    private static RefreshEditProfileListener m_cRefreshListener;

    public PotGlobalSettingsFragment() {
        super();
    }

    public PotGlobalSettingsFragment newInstance(int pPosition, Users pUsers, RefreshEditProfileListener pRefreshEditProfileListener) {
        PotGlobalSettingsFragment lPotAdBoardScreen = new PotGlobalSettingsFragment();

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
        m_cObjMainView = inflater.inflate(R.layout.fragment_lessons_global_settings, container, false);
        ButterKnife.bind(this, m_cObjMainView);

        m_cObjMainActivity.m_cObjFragmentBase = PotGlobalSettingsFragment.this;

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

        m_cSharesSwch.setChecked(m_cUsers.getSharable());
        m_cOfflineSwch.setChecked(m_cUsers.getOfflineable());
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what) {
        }
    }

    @Optional
    @OnClick({})
    public void onClick(View v) {
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
                            case Constants.SHARABLE:
                                if ((boolean) lObjects[1])
                                    m_cObjMainActivity.displaySnack(m_cRelL, "Shares Enabled");
                                else
                                    m_cObjMainActivity.displaySnack(m_cRelL, "Shares Disabled");
                                break;
                            case Constants.OFFLINEABLE:
                                if ((boolean) lObjects[1])
                                    m_cObjMainActivity.displaySnack(m_cRelL, "Offline Save Enabled");
                                else
                                    m_cObjMainActivity.displaySnack(m_cRelL, "Offline Save Disabled");
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
    @OnCheckedChanged({R.id.SHARES_SWITCH, R.id.OFFLINE_SWITCH})
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.SHARES_SWITCH:
                if (m_cIsShareTouched) {
                    m_cIsShareTouched = false;
                    callSwitchApi(isChecked, 0);
                }
                break;
            case R.id.OFFLINE_SWITCH:
                if (m_cIsOfflineTouched) {
                    m_cIsOfflineTouched = false;
                    callSwitchApi(isChecked, 1);
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
                    lJO.put(Constants.SHARABLE, isChecked);
                    lStrObj = Constants.SHARABLE;
                    break;
                case 1:
                    lJO.put(Constants.OFFLINEABLE, isChecked);
                    lStrObj = Constants.OFFLINEABLE;
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
    @OnTouch({R.id.SHARES_SWITCH, R.id.OFFLINE_SWITCH})
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.SHARES_SWITCH:
                m_cIsShareTouched = true;
                break;
            case R.id.OFFLINE_SWITCH:
                m_cIsOfflineTouched = true;
                break;
        }
        return false;
    }
}
