package com.cosmicdew.lessonpot.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.BuildConfig;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.AppConfig;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;


/**
 * Created by S.K. Pissay on 5/10/16.
 */

public class SplashScreen extends PotBaseActivity {

    public static final int STARTAPPLICATION = 1000;

    private Dialog m_cObjDialog;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
        updateAuth();
        refreshToken();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        RequestManager.getInstance(this).placeRequest(Constants.APPCONFIG,
                AppConfig.class, this, null, null, null, false);
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        Intent lObjIntent;
        switch (pObjMessage.what) {
            case STARTAPPLICATION:
                if (null != PotMacros.getLoginAuth(this) && -1 != PotMacros.getGreenSessionId(this)) {
                    lObjIntent = new Intent(this, PotLandingScreen.class);
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(lObjIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else if (true == PotMacros.getAgreeTerms(this)) {
                    lObjIntent = new Intent(this, OtpScreen.class);
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(lObjIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    lObjIntent = new Intent(this, TermsAndCondition.class);
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    lObjIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(lObjIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
                break;
            case PotMacros.APPCONFIG:
                Intent lObjInt;
                if ((boolean) pObjMessage.obj) {
                    Uri lUri = Uri.parse(PotMacros.GOOGLE_PLAY_URL);
                    lObjInt = new Intent(Intent.ACTION_VIEW, lUri);
                    startActivity(lObjInt);
                } else {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            case Constants.APPCONFIG:
                int versionCode = BuildConfig.VERSION_CODE;
                String versionName = BuildConfig.VERSION_NAME;
                AppConfig lAppConfig = (AppConfig) response;
                StringBuffer lBuff = new StringBuffer();
                for (int i = 0; i < lAppConfig.getHighlights().size(); i++) {
                    if (i == 0)
                        lBuff.append(lAppConfig.getHighlights().get(i));
                    else
                        lBuff.append("\n").append(lAppConfig.getHighlights().get(i));
                }
                if (versionName.equalsIgnoreCase(lAppConfig.getLastSupportedVersion()))
                    m_cObjUIHandler.sendEmptyMessageDelayed(STARTAPPLICATION, 1000);
                else {
                    displayYesOrNoAppConfigAlert(PotMacros.APPCONFIG, getResources().getString(R.string.app_name),
                            String.format("Update %s %s", lAppConfig.getLastSupportedVersion(), getResources().getString(R.string.update_is_available_txt)),
                            lBuff.toString());
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
            case Constants.APPCONFIG:
                hideDialog();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
                    hideDialog();
                } else {
                    String lMsg = new String(error.networkResponse.data);
                    showErrorMsg(lMsg);
                }
                break;
            default:
                super.onErrorResponse(error, apiMethod, refObj);
                break;
        }
    }

    public void displayYesOrNoAppConfigAlert(final int pId, String pTitle, String pMessage, String pMessage2) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        final View lMainView = LayoutInflater.from(this).inflate(R.layout.pot_config_dialog, null);
        ((TextView) lMainView.findViewById(R.id.ALLERT_TXT)).setText(pMessage);
        ((TextView) lMainView.findViewById(R.id.ALLERT_TXT_2)).setText(pMessage2);
        lObjBuilder.setView(lMainView);
        ((TextView) lMainView.findViewById(R.id.NO_DIALOG_TXT)).setText(getResources().getString(R.string.cancel_txt));
        ((TextView) lMainView.findViewById(R.id.NO_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message lMsg = new Message();
                lMsg.what = pId;
                lMsg.obj = false;
                m_cObjUIHandler.sendMessage(lMsg);
                m_cObjDialog.dismiss();
            }
        });
        ((TextView) lMainView.findViewById(R.id.YES_DIALOG_TXT)).setText(getResources().getString(R.string.ok_txt));
        ((TextView) lMainView.findViewById(R.id.YES_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message lMsg = new Message();
                lMsg.what = pId;
                lMsg.obj = true;
                m_cObjUIHandler.sendMessage(lMsg);
                m_cObjDialog.dismiss();
            }
        });
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(false);
        m_cObjDialog.show();
    }
}
