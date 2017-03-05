package com.cosmicdew.lessonpot.activities;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 19/1/17.
 */

public class PotAddExistingUserScreen extends PotBaseActivity {

    @Nullable
    @BindView(R.id.MAIN_RL)
    RelativeLayout m_cRelL;

    @Nullable
    @BindView(R.id.PASS_EDIT)
    EditText etPass;

    @Nullable
    @BindView(R.id.USER_ID_EDIT)
    EditText etUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alternative_user_screen);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(getResources().getString(R.string.register_existing_account_txt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what) {
        }
    }

    @Optional
    @OnClick({R.id.DONE_DIALOG_TXT, R.id.CANCEL_DIALOG_TXT})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.DONE_DIALOG_TXT:
                if (validate()) {
                    hideSoftKeyboard();
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.USERNAME, etUserId.getText().toString().trim());
                        lJO.put(Constants.PASSWORD, etPass.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    displayProgressBar(-1, "");
                    RequestManager.getInstance(this).placeRequest(Constants.USERDEVICES, Users.class, this, null, null, lJO.toString(), true);
                }
                break;
            case R.id.CANCEL_DIALOG_TXT:
                onBackPressed();
                break;
        }
    }

    private boolean validate() {
        boolean lRetVal = false;
        String lUserId = etUserId.getText().toString().trim();
        String lPass = etPass.getText().toString().trim();
        if (lUserId.isEmpty()) {
            displaySnack(m_cRelL, "Enter Account Id");
            return false;
        } else if (lPass.isEmpty()) {
            displaySnack(m_cRelL, "Enter Password");
            return false;
        } else {
            lRetVal = true;
        }
        return lRetVal;
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.USERDEVICES)) {
                    hideDialog();
                    Users lUsers = (Users) response;
                    if (null != lUsers) {
                        displayToast(getResources().getString(R.string.account_added_successfully_txt));
                        onBackPressed();
                    }
                } else {
                    super.onAPIResponse(response, apiMethod, refObj);
                    hideDialog();
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.USERDEVICES)) {
                    hideDialog();
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
                    } else if (error.networkResponse.statusCode == 404) {
                        displayToast(getResources().getString(R.string.invalid_credentials_txt));
                    } else {
                        String lMsg = new String(error.networkResponse.data);
                        showErrorMsg(lMsg);
                    }
                } else {
                    super.onErrorResponse(error, apiMethod, refObj);
                    hideDialog();
                }
                break;
        }
    }

}
