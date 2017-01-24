package com.cosmicdew.lessonpot.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
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

    private boolean m_cIsReg;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        m_cIsReg = getIntent().getBooleanExtra(PotMacros.OBJ_REGISTERATION, false);
        if (!m_cIsReg) {
            setTheme(R.style.AppTheme_NoActionBar);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(pSavedInstance);
        setContentView(R.layout.terms_and_conditions);
        ButterKnife.bind(this);

        if (m_cIsReg) {
            getSupportActionBar().setTitle(getResources().getString(R.string.terms_conditions));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!m_cIsReg)
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Optional
    @OnClick({R.id.TERMS_AND_CONDITION_TXT, R.id.PRIVACY_POLICY_TXT,
            R.id.AGREE_TERMS_TXT})
    public void onClick(View v) {
        Intent lObjIntent;
        switch (v.getId()) {
            case R.id.TERMS_AND_CONDITION_TXT:
                lObjIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PotMacros.TERMS_AND_CONDITION_URL));
                startActivity(lObjIntent);
                break;
            case R.id.PRIVACY_POLICY_TXT:
                lObjIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PotMacros.PRIVACY_POLICY_URL));
                startActivity(lObjIntent);
                break;
            case R.id.AGREE_TERMS_TXT:
                if (!m_cIsReg) {
                    lObjIntent = new Intent(this, OtpScreen.class);
                    PotMacros.setAgreeTerms(this, true);
                    startActivity(lObjIntent);
                    finish();
                }else {
                    lObjIntent = new Intent(this, RegisterationScreen.class);
                    startActivity(lObjIntent);
                    finish();
                }
                break;
            default:
                super.onClick(v);
                break;
        }

    }
}
