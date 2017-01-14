package com.cosmicdew.lessonpot.network;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cosmicdew.lessonpot.interfaces.ServerCallback;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by S.K. Pissay on 10/11/16.
 */

public class InputStreamRequest<T> extends Request<byte[]> {

    private static final int EA_SOCKET_TIMEOUT_MS = 1000 * 10;
    private final Class<T> clazz;
    private final RequestManager.IQListener<T> listener;
    private ServerCallback<T> sourceListener;
    private Map<String, String> mParams;
    private String apiMethodName;
    private int isPost;
    private Object refObj;

    //create a static map for directly accessing headers
    public Map<String, String> responseHeaders ;

    public InputStreamRequest(int method, String mUrl,
                              String apiMethodName,
                              Class<T> clazz,
                              HashMap<String, String> params,
                              ServerCallback<T> sourceListener,
                              Object refObj,
                              RequestManager.IQListener<T> listener,
                              Response.ErrorListener errorListener) {
        super(method, mUrl, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(
                EA_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.setSourceListener(sourceListener);
        GsonBuilder gsonBuilder = new GsonBuilder();

        // this request would never use cache.
        setShouldCache(false);
        this.setApiMethodName(apiMethodName);
        this.isPost = method;
        this.clazz = clazz;
        this.listener = listener;
        this.setRefObj(refObj);
        mParams=params;
    }

    @Override
    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return mParams;
    };


    @Override
    protected void deliverResponse(byte[] response) {
        listener.onResponse(response, this);
    }

    @Override
    public void deliverError(VolleyError error) {
        listener.onErrorResponse(error, this);

    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {

        //Initialise local responseHeaders map with response headers received
        responseHeaders = response.headers;

        //Pass the response data here
        return Response.success( response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    /**
     * @return the sourceListener
     */
    public ServerCallback getSourceListener() {
        return sourceListener;
    }

    /**
     * @param sourceListener the sourceListener to set
     */
    public void setSourceListener(ServerCallback<T> sourceListener) {
        this.sourceListener = sourceListener;
    }

    /**
     * @return the apiMethodName
     */
    public String getApiMethodName() {
        return apiMethodName;
    }

    /**
     * @param apiMethodName the apiMethodName to set
     */
    public void setApiMethodName(String apiMethodName) {
        this.apiMethodName = apiMethodName;
    }


    public int getIsPost() {
        return isPost;
    }

    /**
     * @return the clazz
     */
    public Class getClazz() {
        return clazz;
    }

    public Object getRefObj() {
        return refObj;
    }

    public void setRefObj(Object refObj) {
        this.refObj = refObj;
    }
}