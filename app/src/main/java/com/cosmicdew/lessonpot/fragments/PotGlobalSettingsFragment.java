package com.cosmicdew.lessonpot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 25/1/17.
 */

public class PotGlobalSettingsFragment extends PotFragmentBaseClass implements CompoundButton.OnCheckedChangeListener{

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
    @OnClick({})
    public void onClick(View v) {}

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

    @Optional
    @OnCheckedChanged({R.id.SHARES_SWITCH, R.id.OFFLINE_SWITCH})
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.SHARES_SWITCH:
                if (isChecked)
                    m_cObjMainActivity.displaySnack(m_cRelL, "Shares Enabled");
                else
                    m_cObjMainActivity.displaySnack(m_cRelL, "Shares Disabled");
                break;
            case R.id.OFFLINE_SWITCH:
                if (isChecked)
                    m_cObjMainActivity.displaySnack(m_cRelL, "Offline Save Enabled");
                else
                    m_cObjMainActivity.displaySnack(m_cRelL, "Offline Save Disabled");
                break;
        }
    }
}
