package com.cosmicdew.lessonpot.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.cosmicdew.lessonpot.interfaces.ServerCallback;
import com.cosmicdew.lessonpot.macros.PotMacros;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by S.K. Pissay on 5/3/16.
 */

public class RequestManager<T> {

    public static final boolean IS_LIVE = false;

    public static final String LIVE_BASEFEED_URL = "https://api.lessonpot.com/api/v1/";
    public static final String TEST_BASEFEED_URL = "http://52.66.119.49/api/v1/";

    public static RequestManager instance;
    protected String LOG_TAG = "RequestManager";
    private Context mCtx;
    private RequestQueue mRequestQueue;
    private ArrayList<GsonRequest<T>> requestArr = new ArrayList<GsonRequest<T>>();
    private Context mContext;
    private String baseFeedURL;

    /*
     * Constructor
     */
    public RequestManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
        baseFeedURL = "";
        if (RequestManager.IS_LIVE)
            baseFeedURL = LIVE_BASEFEED_URL;
        else
            baseFeedURL = TEST_BASEFEED_URL;
//            baseFeedURL = LIVE_BASEFEED_URL;

    }

    /*
     * Singleton Initialization
     */
    public static RequestManager getInstance(Context context) {
        if (instance == null) {
            instance = new RequestManager(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            DefaultHttpClient mDefaultHttpClient = new DefaultHttpClient();

            final ClientConnectionManager mClientConnectionManager = mDefaultHttpClient.getConnectionManager();
            final HttpParams mHttpParams = mDefaultHttpClient.getParams();
            final ThreadSafeClientConnManager mThreadSafeClientConnManager = new ThreadSafeClientConnManager(mHttpParams, mClientConnectionManager.getSchemeRegistry());
            mDefaultHttpClient = new DefaultHttpClient(mThreadSafeClientConnManager, mHttpParams);
            final HttpStack httpStack = new HttpClientStack(mDefaultHttpClient);


            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext(), httpStack);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


    /*
     * Method to create the request from the passed parameters and place a Volley request
     */
    public void placeRequest(String methodName, Class<T> clazz, ServerCallback<T> listener, Object refObj, HashMap<String, String> feedParams, String body, boolean isPOST) {

        String feedurl = baseFeedURL.concat(methodName);

        if (!isPOST && feedParams != null) {
            feedurl = createGetWithParams(feedurl, feedParams);
        }


        Log.d(LOG_TAG, "SearchURL::" + feedurl);
        GsonRequest<T> jsObjRequest = new GsonRequest<T>(isPOST ? Request.Method.POST : Request.Method.GET,
                feedurl,
                methodName,
                clazz,
                feedParams,
                body,
                listener,
                refObj,
                createSuccessListener(),
                createErrorListener());


//        if (feedParams != null && !feedParams.containsKey(Constants.AUTHKEY)) {
//            HashMap<String, String> headers = new HashMap<String, String>();
//            headers.put(Constants.AUTHKEY,  new Constants().getApiKey());
//            jsObjRequest.setHeaders(headers);
//        }

        if (null != PotMacros.getLoginAuth(mCtx)) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.AUTHORIZATION, PotMacros.getLoginAuth(mCtx));
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);
        }else {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);

        }

//        jsObjRequest.setTag("ZM");
        int socketTimeout = 30000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);


        addToRequestQueue(jsObjRequest);

    }

    public void placeUnivRequest(String methodName, Class<T> clazz, ServerCallback<T> listener, Object refObj, HashMap<String, String> feedParams, String body, Integer requestMethod) {

        String feedurl = baseFeedURL.concat(methodName);

        if (!requestMethod.equals(Request.Method.POST) && feedParams != null) {
            feedurl = createGetWithParams(feedurl, feedParams);
        }


        Log.d(LOG_TAG, "SearchURL::" + feedurl);
        GsonRequest<T> jsObjRequest = new GsonRequest<T>(requestMethod,
                feedurl,
                methodName,
                clazz,
                feedParams,
                body,
                listener,
                refObj,
                createSuccessListener(),
                createErrorListener());


//        if (feedParams != null && !feedParams.containsKey(Constants.AUTHKEY)) {
//            HashMap<String, String> headers = new HashMap<String, String>();
//            headers.put(Constants.AUTHKEY,  new Constants().getApiKey());
//            jsObjRequest.setHeaders(headers);
//        }

        if (null != PotMacros.getLoginAuth(mCtx)) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.AUTHORIZATION, PotMacros.getLoginAuth(mCtx));
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);
        }else {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);

        }

//        jsObjRequest.setTag("ZM");
        int socketTimeout = 30000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);


        addToRequestQueue(jsObjRequest);

    }

    public void placeUnivUserRequest(String methodName, Class<T> clazz, ServerCallback<T> listener, Object refObj, HashMap<String, String> feedParams, String body, Integer requestMethod) {

        String feedurl = baseFeedURL.concat(methodName);

        if (!requestMethod.equals(Request.Method.POST) && feedParams != null) {
            feedurl = createGetWithParams(feedurl, feedParams);
        }


        Log.d(LOG_TAG, "SearchURL::" + feedurl);
        GsonRequest<T> jsObjRequest = new GsonRequest<T>(requestMethod,
                feedurl,
                methodName,
                clazz,
                feedParams,
                body,
                listener,
                refObj,
                createSuccessListener(),
                createErrorListener());


//        if (feedParams != null && !feedParams.containsKey(Constants.AUTHKEY)) {
//            HashMap<String, String> headers = new HashMap<String, String>();
//            headers.put(Constants.AUTHKEY,  new Constants().getApiKey());
//            jsObjRequest.setHeaders(headers);
//        }

        if (null != PotMacros.getUserToken(mCtx)) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.AUTHORIZATION, PotMacros.getUserToken(mCtx));
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);
        }else {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);

        }

//        jsObjRequest.setTag("ZM");
        int socketTimeout = 30000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);


        addToRequestQueue(jsObjRequest);

    }

    public void placeUserRequest(String methodName, Class<T> clazz, ServerCallback<T> listener, Object refObj, HashMap<String, String> feedParams, String body, boolean isPOST) {

        String feedurl = baseFeedURL.concat(methodName);

        if (!isPOST && feedParams != null) {
            feedurl = createGetWithParams(feedurl, feedParams);
        }


        Log.d(LOG_TAG, "SearchURL::" + feedurl);
        GsonRequest<T> jsObjRequest = new GsonRequest<T>(isPOST ? Request.Method.POST : Request.Method.GET,
                feedurl,
                methodName,
                clazz,
                feedParams,
                body,
                listener,
                refObj,
                createSuccessListener(),
                createErrorListener());


//        if (feedParams != null && !feedParams.containsKey(Constants.AUTHKEY)) {
//            HashMap<String, String> headers = new HashMap<String, String>();
//            headers.put(Constants.AUTHKEY,  new Constants().getApiKey());
//            jsObjRequest.setHeaders(headers);
//        }

        if (null != PotMacros.getUserToken(mCtx)) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.AUTHORIZATION, PotMacros.getUserToken(mCtx));
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);
        }else {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);

        }

//        jsObjRequest.setTag("ZM");
        int socketTimeout = 30000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);


        addToRequestQueue(jsObjRequest);

    }

    public void placeDeleteRequest(String methodName, Class<T> clazz, ServerCallback<T> listener, Object refObj, HashMap<String, String> feedParams, String body, boolean isDelete) {

        String feedurl = baseFeedURL.concat(methodName);

        if (!isDelete && feedParams != null) {
            feedurl = createGetWithParams(feedurl, feedParams);
        }


        Log.d(LOG_TAG, "SearchURL::" + feedurl);
        GsonRequest<T> jsObjRequest = new GsonRequest<T>(isDelete ? Request.Method.DELETE : Request.Method.GET,
                feedurl,
                methodName,
                clazz,
                feedParams,
                body,
                listener,
                refObj,
                createSuccessListener(),
                createErrorListener());


//        if (feedParams != null && !feedParams.containsKey(Constants.AUTHKEY)) {
//            HashMap<String, String> headers = new HashMap<String, String>();
//            headers.put(Constants.AUTHKEY,  new Constants().getApiKey());
//            jsObjRequest.setHeaders(headers);
//        }

        if (null != PotMacros.getUserToken(mCtx)) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.AUTHORIZATION, PotMacros.getUserToken(mCtx));
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);
        }else {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);
        }

//        jsObjRequest.setTag("ZM");
        int socketTimeout = 30000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);


        addToRequestQueue(jsObjRequest);

    }

    public void placePutRequest(String methodName, Class<T> clazz, ServerCallback<T> listener, Object refObj, HashMap<String, String> feedParams, String body, boolean isPUT) {

        String feedurl = baseFeedURL.concat(methodName);

        if (!isPUT && feedParams != null) {
            feedurl = createGetWithParams(feedurl, feedParams);
        }


        Log.d(LOG_TAG, "SearchURL::" + feedurl);
        GsonRequest<T> jsObjRequest = new GsonRequest<T>(isPUT ? Request.Method.PUT : Request.Method.GET,
                feedurl,
                methodName,
                clazz,
                feedParams,
                body,
                listener,
                refObj,
                createSuccessListener(),
                createErrorListener());


//        if (feedParams != null && !feedParams.containsKey(Constants.AUTHKEY)) {
//            HashMap<String, String> headers = new HashMap<String, String>();
//            headers.put(Constants.AUTHKEY,  new Constants().getApiKey());
//            jsObjRequest.setHeaders(headers);
//        }

        if (null != PotMacros.getUserToken(mCtx)) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.AUTHORIZATION, PotMacros.getUserToken(mCtx));
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);
        }else {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);
        }

//        jsObjRequest.setTag("ZM");
        int socketTimeout = 30000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);


        addToRequestQueue(jsObjRequest);

    }

    /*
     * Method to create the request from the passed parameters and place a Volley request
     */
    public void placeMultiPartRequest(String methodName, Class<T> clazz, ServerCallback<T> listener, Object refObj, HashMap<String, String> feedParams, File file, String fileKey) {

        String feedurl = baseFeedURL.concat(methodName);

        // Check if the feedParams has Authkey if so then get it again from
        // the SharedPreferences
        /*if (feedParams != null && feedParams.containsKey(FeedParams.AUTHKEY)) {
            feedParams.put(FeedParams.AUTHKEY,
                    SessionManager.getSessionId(mCtx));
        }*/

        // Before making the call.. Check if the Network is available or not
//		if (!isNetworkAvailable(mCtx)) {
//			result.setStatus(ERR);
//			result.setStatusCode(ErrorCodeConsts.NETOWORK_UNAVAILABLE);
//			result.setErrorMessage(Consts.NO_NETWORK);
//			return result;
//		}

        MultiPartRequest jsObjRequest = new MultiPartRequest<T>(Request.Method.POST,
                feedurl,
                methodName,
                clazz,
                feedParams,
                listener,
                refObj,
                createSuccessListener(),
                createErrorListener(), fileKey, file);

        if (null != PotMacros.getUserToken(mCtx)) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.AUTHORIZATION, PotMacros.getUserToken(mCtx));
//            headers.put(Constants.CONTENT_TYPE, Constants.MULTIPART_FORM_DATA);
            jsObjRequest.setHeaders(headers);
        }else {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);
        }


        jsObjRequest.setTag("GN");
        int socketTimeout = 30000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);


        addToRequestQueue(jsObjRequest);

    }

    public void placeUnivMultiPartRequest(String methodName, Class<T> clazz, ServerCallback<T> listener, Object refObj, HashMap<String, String> feedParams, File file, String fileKey, Integer requestMethod) {

        String feedurl = baseFeedURL.concat(methodName);

        // Check if the feedParams has Authkey if so then get it again from
        // the SharedPreferences
        /*if (feedParams != null && feedParams.containsKey(FeedParams.AUTHKEY)) {
            feedParams.put(FeedParams.AUTHKEY,
                    SessionManager.getSessionId(mCtx));
        }*/

        // Before making the call.. Check if the Network is available or not
//		if (!isNetworkAvailable(mCtx)) {
//			result.setStatus(ERR);
//			result.setStatusCode(ErrorCodeConsts.NETOWORK_UNAVAILABLE);
//			result.setErrorMessage(Consts.NO_NETWORK);
//			return result;
//		}

        MultiPartRequest jsObjRequest = new MultiPartRequest<T>(requestMethod,
                feedurl,
                methodName,
                clazz,
                feedParams,
                listener,
                refObj,
                createSuccessListener(),
                createErrorListener(), fileKey, file);

        if (null != PotMacros.getLoginAuth(mCtx)) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.AUTHORIZATION, PotMacros.getLoginAuth(mCtx));
//            headers.put(Constants.CONTENT_TYPE, Constants.MULTIPART_FORM_DATA);
            jsObjRequest.setHeaders(headers);
        }else {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            jsObjRequest.setHeaders(headers);
        }


        jsObjRequest.setTag("GN");
        int socketTimeout = 30000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);


        addToRequestQueue(jsObjRequest);

    }

    public void placeStreamRequest(String feedurl, String apiMethodName, Class<T> clazz, ServerCallback<T> listener, Object refObj, HashMap<String, String> feedParams, String body, boolean isPOST) {


        Log.d(LOG_TAG, "SearchURL::" + feedurl);
        InputStreamRequest<T> jsObjRequest = new InputStreamRequest<T>(isPOST ? Request.Method.POST : Request.Method.GET,
                feedurl,
                apiMethodName,
                clazz,
                feedParams,
                listener,
                refObj,
                createSuccessListener(),
                createErrorListener());


//        jsObjRequest.setTag("ZM");
        int socketTimeout = 30000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);


        addToRequestQueue(jsObjRequest);

    }

    private String createGetWithParams(String url, HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value != null) {
                try {
                    value = URLEncoder.encode(String.valueOf(value), HTTP.UTF_8);
                    if (builder.length() > 0)
                        builder.append("&");
                    builder.append(key).append("=").append(value);
                } catch (UnsupportedEncodingException e) {
                    Log.d("RequestManager", "UnsupportedEncodingException : "+e.toString());
                }
            }
        }

        return (url += "?" + builder.toString());
    }

    private IQListener<T> createSuccessListener() {
        return new IQListener<T>() {

            @Override
            public void onResponse(T response, GsonRequest<T> request) {
                try {
                    Log.i(LOG_TAG, request.getApiMethodName() + ": onResponse called");
                    request.getSourceListener().onAPIResponse(response, request.getApiMethodName(), request.getRefObj());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(T response, MultiPartRequest<T> request) {
                try {
                    Log.i(LOG_TAG+"Multipart", request.getApiMethodName() + ": onResponse called");
                    request.getSourceListener().onAPIResponse(response, request.getApiMethodName(), request.getRefObj());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(byte[] response, InputStreamRequest<T> request) {
                try {
                    Log.i(LOG_TAG+"Stream", request.getApiMethodName() + ": onResponse called");
                    request.getSourceListener().onAPIResponse(response, request.getApiMethodName(), request.getRefObj());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onErrorResponse(VolleyError error, GsonRequest<T> request) {
                try {
                    Log.i(LOG_TAG, request.getApiMethodName() + ": onErrorResponse called");
                    request.getSourceListener().onErrorResponse(error, request.getApiMethodName(), request.getRefObj());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error, MultiPartRequest<T> request) {
                try {
                    Log.i(LOG_TAG, request.getApiMethodName() + ": onErrorResponse called");
                    request.getSourceListener().onErrorResponse(error, request.getApiMethodName(), request.getRefObj());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error, InputStreamRequest<T> request) {
                try {
                    Log.i(LOG_TAG, request.getApiMethodName() + ": onErrorResponse called");
                    request.getSourceListener().onErrorResponse(error, request.getApiMethodName(), request.getRefObj());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(T response) {
                // TODO Auto-generated method stub

            }


        };
    }

    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOG_TAG, "RequestManager : VolleyError");
            }
        };
    }

    /*
     * Method to stop the requests currently in the queue
	 */
    public void stop() {
        mRequestQueue.stop();

    }

    /*
     * Method to start processing the requests in the queue
     */
    public void start() {
        mRequestQueue.start();

    }

    /*
     * Method to cancel all the requests in the queue
     */
    public void cancelAll() {
        requestArr.clear();
        mRequestQueue.cancelAll("ZM");
    }


    /**
     * Customized Callback interface for delivering parsed responses.
     */
    public interface IQListener<T> extends Response.Listener<T> {
        /**
         * Called when a response is received.
         */
        public void onResponse(T response, GsonRequest<T> request);

        public void onResponse(T response, MultiPartRequest<T> request);

        public void onResponse(byte[] response, InputStreamRequest<T> request);

        public void onErrorResponse(VolleyError error, GsonRequest<T> request);

        public void onErrorResponse(VolleyError error, MultiPartRequest<T> request);

        public void onErrorResponse(VolleyError error, InputStreamRequest<T> request);

    }
}
