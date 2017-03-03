package com.cosmicdew.lessonpot.baseclasses;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.interfaces.ServerCallback;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.util.HashMap;

/**
 * Created by S.K. Pissay on 20/3/16.
 */
public abstract class PotFragmentBaseClass extends Fragment implements View.OnClickListener, ServerCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    protected PotBaseActivity m_cObjMainActivity;
    protected UIHandler m_cObjUIHandler;
    protected View m_cObjMainView;
    protected boolean m_cIsActivityAttached;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity pObjActivity) {
        super.onAttach(pObjActivity);
        m_cObjMainActivity = (PotBaseActivity) getActivity();
        m_cObjUIHandler = new UIHandler();
        m_cIsActivityAttached = true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        m_cIsActivityAttached = false;
    }

    @Override
    public void onClick(View v) {
    }

    public final class UIHandler extends Handler {
        public void handleMessage(Message pObjMessage) {
            handleUIMessage(pObjMessage);
        }
    }

    public void handleUIhandler(Message pObjMessage) {
        Message lObJMsg = new Message();
        lObJMsg.what = pObjMessage.what;
        lObJMsg.arg1 = pObjMessage.arg1;
        lObJMsg.arg2 = pObjMessage.arg2;
        lObJMsg.obj = pObjMessage.obj;
        m_cObjUIHandler.sendMessage(lObJMsg);
    }

    protected abstract void handleUIMessage(Message pObjMessage);

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void complete(int code) {

    }

    public void placeRequest(String methodName, Class clazz, Object refObj, HashMap<String, String> params, String body, boolean isPOST) {
        RequestManager.getInstance(getActivity()).placeRequest(methodName, clazz, this, refObj, params, body, isPOST);
    }

    public void placeUserRequest(String methodName, Class clazz, Object refObj, HashMap<String, String> params, String body, boolean isPOST) {
        RequestManager.getInstance(getActivity()).placeUserRequest(methodName, clazz, this, refObj, params, body, isPOST);
    }

    public void placeUnivRequest(String methodName, Class clazz, Object refObj, HashMap<String, String> feedParams, String body, Integer requestMethod) {
        RequestManager.getInstance(getActivity()).placeUnivRequest(methodName, clazz, this, refObj, feedParams, body, requestMethod);
    }

    public void placeUnivUserRequest(String methodName, Class clazz, Object refObj, HashMap<String, String> feedParams, String body, Integer requestMethod) {
        RequestManager.getInstance(getActivity()).placeUnivUserRequest(methodName, clazz, this, refObj, feedParams, body, requestMethod);
    }

    public void placeDeleteRequest(String methodName, Class clazz, Object refObj, HashMap<String, String> params, String body, boolean isDELETE) {
        RequestManager.getInstance(getActivity()).placeDeleteRequest(methodName, clazz, this, refObj, params, body, isDELETE);
    }

    public void placePutRequest(String methodName, Class clazz, Object refObj, HashMap<String, String> params, String body, boolean isPuT) {
        RequestManager.getInstance(getActivity()).placePutRequest(methodName, clazz, this, refObj, params, body, isPuT);
    }

    public void placeMultiPartRequest(String methodName, Class clazz, Object refObj, HashMap<String, String> feedParams, File file, String fileKey) {
        RequestManager.getInstance(getActivity()).placeMultiPartRequest(methodName, clazz, this, refObj, feedParams, file, fileKey);
    }

    public void placeUnivMultiPartRequest(String methodName, Class clazz, Object refObj, HashMap<String, String> feedParams, File file, String fileKey, Integer requestMethod) {
        RequestManager.getInstance(getActivity()).placeUnivMultiPartRequest(methodName, clazz, this, refObj, feedParams, file, fileKey, requestMethod);
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {

    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        if(error instanceof NoConnectionError){
            Toast.makeText(m_cObjMainActivity, "Please check Network connection", Toast.LENGTH_SHORT).show();
            m_cObjMainActivity.hideDialog();
        }
        if(error.networkResponse.statusCode == 401){
        }
    }

}
