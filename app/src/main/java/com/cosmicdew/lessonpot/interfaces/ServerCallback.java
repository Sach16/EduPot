package com.cosmicdew.lessonpot.interfaces;

import com.android.volley.VolleyError;


/**
 * @author S.K. Pissay
 * Callback Interface used for Server API Response
 */
public interface ServerCallback<T> {
    public abstract void complete(int code);

    /**
     * @param response
     * @param apiMethod
     * @param refObj
     */
    public abstract void onAPIResponse(Object response, String apiMethod, Object refObj);

    /**
     * @param error
     * @param apiMethod
     * @param refObj
     */
    public abstract void onErrorResponse(VolleyError error, String apiMethod, Object refObj);
};
