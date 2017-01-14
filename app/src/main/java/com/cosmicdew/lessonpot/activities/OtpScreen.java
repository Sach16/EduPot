package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.cosmicdew.lessonpot.models.Devices;
import com.cosmicdew.lessonpot.models.Sessions;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 28/9/16.
 */

public class OtpScreen extends PotBaseActivity {

    @Nullable
    @BindView(R.id.REL_LAY)
    RelativeLayout m_cRelLay;

    @Nullable
    @BindView(R.id.OTP_LAY)
    LinearLayout m_cOtpLay;

    @Nullable
    @BindView(R.id.TERMS_AND_CONDITION_NUMB)
    TextView m_cCountryCodetxt;

    @Nullable
    @BindView(R.id.PHONE_NUMBER)
    EditText m_cPhoneEdit;

    @Nullable
    @BindView(R.id.OTP1)
    EditText m_cOtpEdit1;

    @Nullable
    @BindView(R.id.OTP2)
    EditText m_cOtpEdit2;

    @Nullable
    @BindView(R.id.OTP3)
    EditText m_cOtpEdit3;

    @Nullable
    @BindView(R.id.OTP4)
    EditText m_cOtpEdit4;

    @Nullable
    @BindView(R.id.OTP5)
    EditText m_cOtpEdit5;

    @Nullable
    @BindView(R.id.OTP6)
    EditText m_cOtpEdit6;

    @Nullable
    @BindView(R.id.OTP_TXT)
    TextView m_cOtpTextHeader;

    @Nullable
    @BindView(R.id.SUBMIT_OTP_TXT)
    TextView m_cSubmitOtpTxt;

    @Nullable
    @BindView(R.id.RESEND_OTP_TXT)
    TextView m_cResendOtpTxt;

    @Nullable
    @BindView(R.id.SUBMIT_PH_TXT)
    TextView m_cPhoneSubmitTxt;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.otp_screen);
        ButterKnife.bind(this);

//        getSupportActionBar().setTitle(getResources().getString(R.string.phone_number_verification));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_center);

        init();

    }

    private void init() {
        if (PotMacros.getOTPGen(this)) {
            m_cPhoneEdit.setText(PotMacros.getOTPPhoneNo(this).contains(getResources().getString(R.string.prefix_numb)) ?
                    PotMacros.getOTPPhoneNo(this).replace(getResources().getString(R.string.prefix_numb), "") : PotMacros.getOTPPhoneNo(this));
            m_cPhoneSubmitTxt.setTextColor(getResources().getColor(R.color.colorAccent));
            m_cOtpLay.setVisibility(View.VISIBLE);
        }
        checkTextWatch();
    }

    private void checkTextWatch() {
        m_cPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (m_cPhoneEdit.getText().length() >= 10){
                    m_cPhoneSubmitTxt.setTextColor(getResources().getColor(R.color.colorAccent));
                }else {
                    m_cPhoneSubmitTxt.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ArrayList<EditText> editTextList = new ArrayList<>();
        editTextList.add(m_cOtpEdit1);
        editTextList.add(m_cOtpEdit2);
        editTextList.add(m_cOtpEdit3);
        editTextList.add(m_cOtpEdit4);
        editTextList.add(m_cOtpEdit5);
        editTextList.add(m_cOtpEdit6);

        for (final EditText editText : editTextList){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    checkNowWithIds(editText);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    private void checkNowWithIds(EditText editText) {
        switch (editText.getId()){
            case R.id.OTP1:
                if (editText.getText().length() >= 1) {
                    m_cOtpEdit2.requestFocus();
                }
                break;
            case R.id.OTP2:
                if (editText.getText().length() >= 1) {
                    m_cOtpEdit3.requestFocus();
                }
                break;
            case R.id.OTP3:
                if (editText.getText().length() >= 1) {
                    m_cOtpEdit4.requestFocus();
                }
                break;
            case R.id.OTP4:
                if (editText.getText().length() >= 1) {
                    m_cOtpEdit5.requestFocus();
                }
                break;
            case R.id.OTP5:
                if (editText.getText().length() >= 1) {
                    m_cOtpEdit6.requestFocus();
                }
                break;
            case R.id.OTP6:
                if (editText.getText().length() >= 1) {
                    m_cSubmitOtpTxt.requestFocus();
                    m_cSubmitOtpTxt.setTextColor(getResources().getColor(R.color.colorAccent));
                }else {
                    m_cSubmitOtpTxt.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
                break;
        }
    }

    public boolean validatePhone() {
        boolean lRetVal = false;
        String lPhone = m_cPhoneEdit.getText().toString().trim();

        if (lPhone.isEmpty()) {
            displaySnack(m_cRelLay, "Please Enter Phone Number");
            return false;
        } else if (!isPhoneNoValid(lPhone)) {
            displaySnack(m_cRelLay, "Please Enter Valid Phone Number");
            return false;
        } else {
            lRetVal = true;
        }
        return lRetVal;
    }

    public boolean validateCred() {
        boolean lRetVal = false;
        String lPhone = m_cPhoneEdit.getText().toString().trim();
        String m_cOtp1 = m_cOtpEdit1.getText().toString().trim();
        String m_cOtp2 = m_cOtpEdit2.getText().toString().trim();
        String m_cOtp3 = m_cOtpEdit3.getText().toString().trim();
        String m_cOtp4 = m_cOtpEdit4.getText().toString().trim();
        String m_cOtp5 = m_cOtpEdit5.getText().toString().trim();
        String m_cOtp6 = m_cOtpEdit6.getText().toString().trim();

        if (lPhone.isEmpty()){
            displaySnack(m_cRelLay, "Please Enter Phone Number");
            return false;
        }else if (!isPhoneNoValid(lPhone)){
            displaySnack(m_cRelLay, "Please Enter Valid Phone Number");
            return false;
        }else if (m_cOtp1.isEmpty()){
            displaySnack(m_cRelLay, "Enter first digit");
            return false;
        }else if (m_cOtp2.isEmpty()){
            displaySnack(m_cRelLay, "Enter second digit");
            return false;
        }else if (m_cOtp3.isEmpty()){
            displaySnack(m_cRelLay, "Enter third digit");
            return false;
        }else if (m_cOtp4.isEmpty()){
            displaySnack(m_cRelLay, "Enter fourth digit");
            return false;
        }else if (m_cOtp5.isEmpty()){
            displaySnack(m_cRelLay, "Enter fifth digit");
            return false;
        }else if (m_cOtp6.isEmpty()){
            displaySnack(m_cRelLay, "Enter sixth digit");
            return false;
        }else {
            lRetVal = true;
        }
        return lRetVal;
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {

    }

    @Optional
    @OnClick({R.id.SUBMIT_PH_TXT, R.id.SUBMIT_OTP_TXT, R.id.RESEND_OTP_TXT})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SUBMIT_PH_TXT:
//                if (!PotMacros.getOTPGen(this)) {
                    if (validatePhone()) {
                        hideSoftKeyboard();
                        JSONObject lJO = new JSONObject();
                        try {
                            lJO.put(Constants.LOGIN_PHONE_NUMBER, m_cCountryCodetxt.getText().toString().trim() + m_cPhoneEdit.getText().toString().trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        HashMap<String, String> lParams = new HashMap<>();
                        lParams.put(Constants.LOGIN_PHONE_NUMBER, m_cPhoneEdit.getText().toString().trim());
                        displayProgressBar(-1, "");
                        RequestManager.getInstance(this).placeRequest(Constants.DEVICES, Devices.class, this, null, null, lJO.toString(), true);
                    }
//                }
                break;
            case R.id.SUBMIT_OTP_TXT:
                if (validateCred()) {
                    hideSoftKeyboard();
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.LOGIN_PHONE_NUMBER, m_cCountryCodetxt.getText().toString().trim() + m_cPhoneEdit.getText().toString().trim());
                        String lOtp = m_cOtpEdit1.getText().toString().trim() +
                                m_cOtpEdit2.getText().toString().trim() +
                                m_cOtpEdit3.getText().toString().trim() +
                                m_cOtpEdit4.getText().toString().trim() +
                                m_cOtpEdit5.getText().toString().trim() +
                                m_cOtpEdit6.getText().toString().trim();
                        lJO.put(Constants.PIN, lOtp);
                        PotMacros.setOTPpin(this, lOtp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    displayProgressBar(-1, "");
                    RequestManager.getInstance(this).placeRequest(Constants.SESSIONS, Sessions.class, this, null, null, lJO.toString(), true);
                }
                break;
            case R.id.RESEND_OTP_TXT:
                if (PotMacros.getResendOtpCount(this) <= 5) {
                    if (validatePhone()) {
                        PotMacros.setResendOtpCount(this, (PotMacros.getResendOtpCount(this) + 1));
                        JSONObject lJO = new JSONObject();
                        try {
                            lJO.put(Constants.LOGIN_PHONE_NUMBER, m_cCountryCodetxt.getText().toString().trim() + m_cPhoneEdit.getText().toString().trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        HashMap<String, String> lParams = new HashMap<>();
                        lParams.put(Constants.LOGIN_PHONE_NUMBER, m_cPhoneEdit.getText().toString().trim());
                        displayProgressBar(-1, "");
                        RequestManager.getInstance(this).placeRequest(Constants.DEVICES, Devices.class, this, null, null, lJO.toString(), true);
                    }
                }else {
                    displayErrorAlert(-1, getResources().getString(R.string.sms_limit_exceeded_prompt));
                }
            break;
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            case Constants.DEVICES:
                hideDialog();
                m_cOtpEdit1.requestFocus();
                Devices lDevices = (Devices) response;
                PotMacros.setOTPGen(this, true);
                PotMacros.setDeviceID(this, lDevices.getId());
//                PotMacros.setOTPpin(this, lDevices.getPin());
                PotMacros.setOTPPhoneNo(this, lDevices.getPhone());
                if (PotMacros.getOTPGen(this)) {
                    m_cPhoneEdit.setText(PotMacros.getOTPPhoneNo(this).contains(getResources().getString(R.string.prefix_numb)) ?
                            PotMacros.getOTPPhoneNo(this).replace(getResources().getString(R.string.prefix_numb), "") : PotMacros.getOTPPhoneNo(this));
                    m_cOtpLay.setVisibility(View.VISIBLE);
                }
                break;
            case Constants.SESSIONS:
                hideDialog();
                Sessions lSessions = (Sessions) response;
                Intent lObjIntent;
                PotMacros.saveLoginAuth(this, lSessions.getDeviceToken());
                PotMacros.saveGreenSessionId(this, lSessions.getId());
                PotMacros.setSessionEndTime(this, lSessions.getEnd());
                lObjIntent = new Intent(this, PotLandingScreen.class);
                lObjIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(lObjIntent);
                refreshToken();
                finish();
                break;
            default:
                super.onAPIResponse(response, apiMethod, refObj);
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod) {
            case Constants.DEVICES:
                hideDialog();
                if(error instanceof NoConnectionError){
                    Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
                    hideDialog();
                }else {
                    String lMsg = new String(error.networkResponse.data);
                    showErrorMsg(lMsg);
                }
                break;
            case Constants.SESSIONS:
                hideDialog();
                if(error instanceof NoConnectionError){
                    Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
                    hideDialog();
                }else {
                    String lMsg = new String(error.networkResponse.data);
                    showErrorMsg(lMsg);
                }
                break;
            default:
                super.onErrorResponse(error, apiMethod, refObj);
                break;
        }

    }

}
