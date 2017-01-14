package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.macros.PotMacros;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 13/10/16.
 */

public class TermsAndCondition extends PotBaseActivity {

    @Nullable
    @BindView(R.id.TERMS_AND_CONDITION_TXT)
    TextView m_ctermsConditiontxt;

    @Nullable
    @BindView(R.id.PRIVACY_POLICY_TXT)
    TextView m_cPrivacyPolicy;

    @Nullable
    @BindView(R.id.AGREE_TERMS_TXT)
    TextView m_cAgreeTerms;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.terms_and_conditions);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyAllPermissions(this);
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what){
            case PERMISSION_GRANTED:
                break;
            case PERMISSION_DENIED:
                break;
        }

    }

    @Optional
    @OnClick({R.id.TERMS_AND_CONDITION_TXT, R.id.PRIVACY_POLICY_TXT,
            R.id.AGREE_TERMS_TXT})
    public void onClick(View v) {
        Intent lObjInt;
        switch (v.getId()) {
            case R.id.TERMS_AND_CONDITION_TXT:
                lObjInt = new Intent(Intent.ACTION_VIEW, Uri.parse(PotMacros.TERMS_AND_CONDITION_URL));
                startActivity(lObjInt);
                break;
            case R.id.PRIVACY_POLICY_TXT:
                lObjInt = new Intent(Intent.ACTION_VIEW, Uri.parse(PotMacros.PRIVACY_POLICY_URL));
                startActivity(lObjInt);
                break;
            case R.id.AGREE_TERMS_TXT:
                lObjInt = new Intent(this, OtpScreen.class);
                PotMacros.setAgreeTerms(this, true);
                startActivity(lObjInt);
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }

    }
}
