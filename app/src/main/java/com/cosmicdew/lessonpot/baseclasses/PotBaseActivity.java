package com.cosmicdew.lessonpot.baseclasses;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.activities.OtpScreen;
import com.cosmicdew.lessonpot.interfaces.ServerCallback;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Devices;
import com.cosmicdew.lessonpot.models.Sessions;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.cosmicdew.lessonpot.utils.CustomMovementMethod;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by S.K. Pissay on 20/3/16.
 */
public abstract class PotBaseActivity extends AppCompatActivity implements View.OnClickListener, ServerCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int DISPLAY_ERROR_ALERT_ID = 1;
    public static final int DISPLAY_PROGRESS_BAR_ID = 2;
    public static final int DISPLAY_ANIM_DIAL_ID = 3;
    public static final int PERMISSION_DENIED = -1;
    public static final int PERMISSION_GRANTED = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CALL = 42;
    private static final int REQUEST_RECORD_AUDIO = 29;
    private static final int REQUEST_ALL = 50;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String[] PERMISSIONS_CALL = {
            Manifest.permission.CALL_PHONE,
    };
    private static String[] PERMISSIONS_AUDIO = {
            Manifest.permission.RECORD_AUDIO,
    };
    private static String[] PERMISSIONS_ALL = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

//    public static final String GCM_PROJECT_NUMBER = "159301724926";

    /*TODO enter gcm project no. down*/
    public static final String GCM_PROJECT_NUMBER = "";

    public boolean pTakePicture;
    public boolean pSavePicture;

    public String m_cImageGUID;
    public String m_cAudioGUID;

    protected int m_cDialogID;
    protected AlertDialog m_cObjDialog;
    protected ProgressDialog m_cObjProgressBar;
    protected Snackbar m_cSnackBar;
    protected Dialog m_cObjAnimDialog;
    public UIHandler m_cObjUIHandler;
    public SwipeRefreshLayout swipeView;

    public HashMap<Integer, String> mFragmentTags = new HashMap<Integer, String>();

    public String m_cJsonSpecialityObject;
    public HashMap<String, HashMap<String, Object>> m_cJsonDoctorProfileObject;
    public HashMap<String, ArrayList<String>> m_cJsonAppSlot;

    // Drawer layout and its propeties
    public DrawerLayout m_cObjSliderMenu;
    public FrameLayout m_cContainFragment;
    public FragmentManager m_cObjFragmentManager;

    public PotFragmentBaseClass m_cObjFragmentBase;

    public NotificationReceiver m_cNotificationReceiver;

    public boolean ISSWIPE;

    protected abstract void handleUIMessage(Message pObjMessage);

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(pSavedInstance);
        m_cObjUIHandler = new UIHandler();

        // Set the Status Bar Color
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }*/

        // REGISTERING & GETTING PUSH NOTIFICATION
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    //ui controls

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_cNotificationReceiver = new NotificationReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(m_cNotificationReceiver,
                new IntentFilter(PotMacros.REFRESH_NOTIFY_CONSTANT));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //api controls

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

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        if (apiMethod == Constants.SESSIONS) {
            hideDialog();
            Sessions lSessions = (Sessions) response;
            PotMacros.saveLoginAuth(this, lSessions.getDeviceToken());
            PotMacros.saveSessionId(this, lSessions.getId());
            PotMacros.setSessionEndTime(this, lSessions.getEnd());
        } else if (apiMethod.contains(Constants.DEVICES)) {
            hideDialog();
            Devices lDevices = (Devices) response;
            if (null != lDevices)
                Log.d(TAG, "Fcm Token: " + "Updated to server");
            else
                Log.d(TAG, "Fcm Token: " + "Updated to server error");
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        if (apiMethod == Constants.SESSIONS) {
            hideDialog();
        }else if (apiMethod.contains(Constants.DEVICES)) {
            Log.d(TAG, "Fcm Token: " + "Updated to server error");
            hideDialog();
        }
        if (error.networkResponse == null) {
            Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
            hideDialog();
        }
    }

    public final class UIHandler extends Handler {
        @Override
        public void handleMessage(Message pObjMessage) {
            if (null != m_cObjFragmentBase && m_cObjFragmentBase.m_cIsActivityAttached) {
                m_cObjFragmentBase.handleUIhandler(pObjMessage);
            }
            handleUIMessage(pObjMessage);
        }
    }

    @Override
    protected Dialog onCreateDialog(int pID) {
        Dialog lRetVal = null;
        if (pID == DISPLAY_PROGRESS_BAR_ID) {
            lRetVal = m_cObjProgressBar;
        } else if (pID == DISPLAY_ANIM_DIAL_ID) {
            lRetVal = m_cObjAnimDialog;
        } else {
            lRetVal = m_cObjDialog;
        }
        m_cDialogID = pID;
        return lRetVal;
    }

    @SuppressWarnings("deprecation")
    public void hideDialog() {
        try {
            if (null != m_cObjDialog) {
                m_cObjDialog.dismiss();
                removeDialog(m_cDialogID);
            }
            if (null != m_cObjProgressBar) {
                m_cObjProgressBar.dismiss();
                removeDialog(m_cDialogID);
            }
            if (null != m_cObjAnimDialog) {
                m_cObjAnimDialog.dismiss();
                removeDialog(m_cDialogID);
            }
            m_cObjProgressBar = null;
            m_cObjDialog = null;
            m_cObjAnimDialog = null;
            m_cDialogID = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayToast(String message){
        Toast toast= Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void displayToastLong(String message){
        Toast toast= Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_LONG);
        toast.show();
    }

    public void displaySnack(View pView, String pSnackText) {
        hideDialog();
        m_cSnackBar = Snackbar.make(pView, pSnackText, Snackbar.LENGTH_LONG);
        m_cSnackBar.show();
    }

    public void displaySnackRetry(View pView, String pSnackText) {
        hideDialog();
        m_cSnackBar = Snackbar.make(pView, pSnackText, Snackbar.LENGTH_INDEFINITE)
                .setAction("Go Offline", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_cObjUIHandler.sendEmptyMessage(PotMacros.NOTIFICATION_NO_NETWORK_CONNECTION_RETRY);
                    }
                });
        m_cSnackBar.show();
    }

    public void createProgressDialog() {
        hideDialog();
        m_cObjProgressBar = new ProgressDialog(this);
        try {
            m_cObjProgressBar.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }try {
            m_cObjProgressBar.setCancelable(false);
            m_cObjProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            m_cObjProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_cObjProgressBar.setContentView(R.layout.progressdialog);
            m_cDialogID = DISPLAY_PROGRESS_BAR_ID;
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            m_cObjProgressBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayPagingProgressDialog() {
        hideDialog();
        m_cObjProgressBar = new ProgressDialog(this);
        try {
            m_cObjProgressBar.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
        m_cObjProgressBar.setCancelable(false);
        m_cObjProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        m_cObjProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_cObjProgressBar.setContentView(R.layout.progressdialog_paging);
        m_cDialogID = DISPLAY_PROGRESS_BAR_ID;
        try {
            m_cObjProgressBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayProgressBar(int pStrResID, String pErrorString) {
        createProgressDialog();
    }

    public void displaySpinnerProgressBar() {
        hideDialog();
        try {
            m_cObjProgressBar = new ProgressDialog(this/*, R.style.CustomDialogTheme*/);
            //m_cObjProgressBar.addContentView(new ProgressBar(this), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            m_cObjProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_cObjProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            m_cObjProgressBar.setCancelable(false);
            m_cDialogID = DISPLAY_PROGRESS_BAR_ID;
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            m_cObjProgressBar.show();
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("deprecation")
    public void displayErrorAlert(int pStrResID, String pErrorString) {
        displayErrorAlert(pStrResID, pErrorString, false);
    }

    public void displayErrorAlert(int pStrResID, String pErrorString, final boolean pSendMsg) {
        hideDialog();
        String lMessage = (null != pErrorString) ? pErrorString : this.getResources().getString(pStrResID);
        try {
            AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
            lObjBuilder.setCancelable(false);
            lObjBuilder.setTitle(getString(R.string.app_name));
            lObjBuilder.setMessage(lMessage);
            lObjBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface pObjDialog, int id) {
                    hideDialog();
                    if (pSendMsg) {
                        m_cObjUIHandler.sendEmptyMessage(1);
                    }
                }
            });
            m_cObjDialog = lObjBuilder.create();
            m_cObjDialog.show();
            showDialog(DISPLAY_ERROR_ALERT_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayAlert(int pStrResID, String pTitle, String pMessage) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        final View lMainView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null);
        ((TextView) lMainView.findViewById(R.id.ALLERT_TXT)).setText(pMessage);
        lObjBuilder.setView(lMainView);
        ((TextView) lMainView.findViewById(R.id.OK_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDialog();
            }
        });
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(true);
        m_cObjDialog.show();
    }

    public void displayYesOrNoAlert(final int pId, String pTitle, String pMessage) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        final View lMainView = LayoutInflater.from(this).inflate(R.layout.lesson_yes_no_dialog, null);
        ((TextView) lMainView.findViewById(R.id.ALLERT_TXT)).setText(pMessage);
        lObjBuilder.setView(lMainView);
        ((TextView) lMainView.findViewById(R.id.NO_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDialog();
            }
        });
        ((TextView) lMainView.findViewById(R.id.YES_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message lMsg = new Message();
                lMsg.what = pId;
                lMsg.obj = true;
                m_cObjUIHandler.sendMessage(lMsg);
                hideDialog();
            }
        });
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(false);
        m_cObjDialog.show();
    }

    public void displayYesOrNoCustAlert(final int pId, String pTitle, String pMessage, final Object pObj) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        final View lMainView = LayoutInflater.from(this).inflate(R.layout.lesson_yes_no_dialog, null);
        ((TextView) lMainView.findViewById(R.id.ALLERT_TXT)).setText(pMessage);
        lObjBuilder.setView(lMainView);
        ((TextView) lMainView.findViewById(R.id.NO_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDialog();
            }
        });
        ((TextView) lMainView.findViewById(R.id.YES_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message lMsg = new Message();
                lMsg.what = pId;
                lMsg.obj = new Object[]{true, pObj};
                m_cObjUIHandler.sendMessage(lMsg);
                hideDialog();
            }
        });
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(false);
        m_cObjDialog.show();
    }

    public void displayCommentDialog(final int pId, String pTitle, final String pComment, final boolean pIsViewMode) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header_2, null);
        if (pComment != null && pComment.trim().length() > 0){
            ((ImageView) lView.findViewById(R.id.DELETE_COMMENT_IMG)).setVisibility(View.VISIBLE);
            ((ImageView) lView.findViewById(R.id.DELETE_COMMENT_IMG)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message lMsg = new Message();
                    lMsg.what = pId;
                    lMsg.obj =  null;
                    m_cObjUIHandler.sendMessage(lMsg);
                    hideDialog();
                }
            });
        }
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        final View lMainView = LayoutInflater.from(this).inflate(R.layout.lesson_comment_dialog, null);
        EditText lEditText = (EditText) lMainView.findViewById(R.id.COMMENTS_EDIT);
        lEditText.setText("");
        lEditText.append(pComment != null ? pComment + " " : "");
        lEditText.setLinksClickable(true);
        lEditText.setAutoLinkMask(Linkify.WEB_URLS);
        lEditText.setMovementMethod(CustomMovementMethod.getInstance());

        lEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (pIsViewMode) {
                    Linkify.addLinks(s, Linkify.WEB_URLS);
                }
            }
        });
        if (pIsViewMode) {
            lEditText.setFocusable(false);
            lEditText.setClickable(false);
            //NOTE: Hyper links only on view mode
            Linkify.addLinks(lEditText, Linkify.WEB_URLS);
//            ((EditText) lMainView.findViewById(R.id.COMMENTS_EDIT)).setEnabled(false);
            ((TextView) lMainView.findViewById(R.id.DONE_DIALOG_TXT)).setVisibility(View.GONE);
            ((ImageView) lView.findViewById(R.id.DELETE_COMMENT_IMG)).setVisibility(View.GONE);
            ((TextView) lMainView.findViewById(R.id.CANCEL_DIALOG_TXT)).setVisibility(View.GONE);
        }
        lObjBuilder.setView(lMainView);

        ((TextView) lMainView.findViewById(R.id.CANCEL_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDialog();
            }
        });
        ((TextView) lMainView.findViewById(R.id.DONE_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message lMsg = new Message();
                lMsg.what = pId;
                lMsg.obj =  ((EditText) lMainView.findViewById(R.id.COMMENTS_EDIT)).getText().toString();
                m_cObjUIHandler.sendMessage(lMsg);
                hideDialog();
            }
        });
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(true);
        m_cObjDialog.show();
    }

    public void displayCommentDialog(final int pId, String pTitle, final String pComment, final boolean pIsViewMode, final Object pObj) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header_2, null);
        if (pComment != null && pComment.trim().length() > 0) {
            ((ImageView) lView.findViewById(R.id.DELETE_COMMENT_IMG)).setVisibility(View.VISIBLE);
            ((ImageView) lView.findViewById(R.id.DELETE_COMMENT_IMG)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message lMsg = new Message();
                    lMsg.what = pId;
                    lMsg.obj = new Object[]{null, pObj};
                    m_cObjUIHandler.sendMessage(lMsg);
                    hideDialog();
                }
            });
        }
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        final View lMainView = LayoutInflater.from(this).inflate(R.layout.lesson_comment_dialog, null);
        EditText lEditText = (EditText) lMainView.findViewById(R.id.COMMENTS_EDIT);
        lEditText.setText("");
        lEditText.append(pComment != null ? pComment + " " : "");
        lEditText.setLinksClickable(true);
        lEditText.setAutoLinkMask(Linkify.WEB_URLS);
        lEditText.setMovementMethod(CustomMovementMethod.getInstance());

        lEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pIsViewMode) {
                    Linkify.addLinks(s, Linkify.WEB_URLS);
                }
            }
        });
        if (pIsViewMode) {
            lEditText.setFocusable(false);
            lEditText.setClickable(false);
            //NOTE: Hyper links only on view mode
            Linkify.addLinks(lEditText, Linkify.WEB_URLS);
//            ((EditText) lMainView.findViewById(R.id.COMMENTS_EDIT)).setEnabled(false);
            ((TextView) lMainView.findViewById(R.id.DONE_DIALOG_TXT)).setVisibility(View.GONE);
            ((ImageView) lView.findViewById(R.id.DELETE_COMMENT_IMG)).setVisibility(View.GONE);
            ((TextView) lMainView.findViewById(R.id.CANCEL_DIALOG_TXT)).setVisibility(View.GONE);
        }
        lObjBuilder.setView(lMainView);

        ((TextView) lMainView.findViewById(R.id.CANCEL_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDialog();
            }
        });
        ((TextView) lMainView.findViewById(R.id.DONE_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message lMsg = new Message();
                lMsg.what = pId;
                lMsg.obj = new Object[]{((EditText) lMainView.findViewById(R.id.COMMENTS_EDIT)).getText().toString(),
                        pObj};
                m_cObjUIHandler.sendMessage(lMsg);
                hideDialog();
            }
        });
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(true);
        m_cObjDialog.show();
    }

    public void displaySupportDialog(final int pId, String pTitle, final String pSupportTitle, final String pComment, final boolean pIsViewMode) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header_2, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        final View lMainView = LayoutInflater.from(this).inflate(R.layout.lesson_support_dialog, null);
        EditText lSuppTitle = (EditText) lMainView.findViewById(R.id.SUPPORT_TITLE_TXT);
        EditText lEditText = (EditText) lMainView.findViewById(R.id.COMMENTS_EDIT);
        lEditText.setText("");
        lEditText.append(pSupportTitle != null ? pSupportTitle + " " : "");
        lEditText.setText("");
        lEditText.append(pComment != null ? pComment + " " : "");
        lEditText.setLinksClickable(true);
        lEditText.setAutoLinkMask(Linkify.WEB_URLS);
        lEditText.setMovementMethod(CustomMovementMethod.getInstance());

        lEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (pIsViewMode) {
                    Linkify.addLinks(s, Linkify.WEB_URLS);
                }
            }
        });
        if (pIsViewMode) {
            lEditText.setFocusable(false);
            lEditText.setClickable(false);
            //NOTE: Hyper links only on view mode
            Linkify.addLinks(lEditText, Linkify.WEB_URLS);
//            ((EditText) lMainView.findViewById(R.id.COMMENTS_EDIT)).setEnabled(false);
            ((TextView) lMainView.findViewById(R.id.DONE_DIALOG_TXT)).setVisibility(View.GONE);
            ((ImageView) lView.findViewById(R.id.DELETE_COMMENT_IMG)).setVisibility(View.GONE);
            ((TextView) lMainView.findViewById(R.id.CANCEL_DIALOG_TXT)).setVisibility(View.GONE);
        }
        lObjBuilder.setView(lMainView);

        ((TextView) lMainView.findViewById(R.id.CANCEL_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDialog();
            }
        });
        ((TextView) lMainView.findViewById(R.id.DONE_DIALOG_TXT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message lMsg = new Message();
                String[] lStrArr = new String[]{((EditText) lMainView.findViewById(R.id.SUPPORT_TITLE_TXT)).getText().toString(),
                        ((EditText) lMainView.findViewById(R.id.COMMENTS_EDIT)).getText().toString()};
                lMsg.what = pId;
                lMsg.obj = lStrArr;
                m_cObjUIHandler.sendMessage(lMsg);
                hideDialog();
            }
        });
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(true);
        m_cObjDialog.show();
    }

    public void displaySpinnerDialog(final int pId, String pTitle, final List<String> pList,
                                     final List<Integer> pListIds, final Object pObj) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        BaseAdapter lAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return pList.size();
            }

            @Override
            public Object getItem(int i) {
                return pList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View lView = LayoutInflater.from(PotBaseActivity.this).inflate(R.layout.spinner_dialog_item, null);
                TextView ltextview = (TextView) lView.findViewById(R.id.text1);
                ltextview.setText(pList.get(i));
                return lView;
            }
        };

        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        lObjBuilder.setAdapter(lAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message lMsg = new Message();
                lMsg.what = pId;
                lMsg.arg1 = pListIds.get(which);
                lMsg.obj = pObj;
                m_cObjUIHandler.sendMessage(lMsg);
                hideDialog();
            }
        });
        /*lObjBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(true);
        m_cObjDialog.show();
    }

    public void displaySpinnerDialog(final int pId, String pTitle, final List<String> pList, final Object pObj) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        BaseAdapter lAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return pList.size();
            }

            @Override
            public Object getItem(int i) {
                return pList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View lView = LayoutInflater.from(PotBaseActivity.this).inflate(R.layout.spinner_dialog_item, null);
                TextView ltextview = (TextView) lView.findViewById(R.id.text1);
                ltextview.setText(pList.get(i));
                return lView;
            }
        };

        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        lObjBuilder.setAdapter(lAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message lMsg = new Message();
                lMsg.what = pId;
                lMsg.arg1 = which;
                lMsg.obj = pObj;
                m_cObjUIHandler.sendMessage(lMsg);
                hideDialog();
            }
        });
        /*lObjBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(true);
        m_cObjDialog.show();
    }

    public void displaySpinnerNotifyDialog(final int pId,
                                           String pTitle,
                                           final List<String> pList,
                                           final int[] pIntArry,
                                           final Object pObj) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        BaseAdapter lAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return pList.size();
            }

            @Override
            public Object getItem(int i) {
                return pList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View lView = LayoutInflater.from(PotBaseActivity.this).inflate(R.layout.spinner_dialog_notify_item, null);
                TextView ltextview = (TextView) lView.findViewById(R.id.text1);
                ltextview.setText(pList.get(i));
                TextView lNotifyCount = (TextView) lView.findViewById(R.id.notify_count);
                if (pIntArry[i] > 0)
                    lNotifyCount.setText(String.valueOf(pIntArry[i]));
                else
                    lNotifyCount.setVisibility(View.GONE);
                return lView;
            }
        };

        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        lObjBuilder.setAdapter(lAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message lMsg = new Message();
                lMsg.what = pId;
                lMsg.arg1 = which;
                lMsg.obj = pObj;
                m_cObjUIHandler.sendMessage(lMsg);
                hideDialog();
            }
        });
        /*lObjBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(true);
        m_cObjDialog.show();
    }

    public void displayOptionsSpinnerDialog(final int pId, String pTitle, final List<String> pList, final Object pObj) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        BaseAdapter lAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return pList.size();
            }

            @Override
            public Object getItem(int i) {
                return pList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(final int i, View view, ViewGroup viewGroup) {
                View lView = view;
                final SpinnerHolder holder;

                if (lView == null) {
                    lView = LayoutInflater.from(PotBaseActivity.this).inflate(R.layout.spinner_settings_dialog_item, null);
                    holder = new SpinnerHolder();
                    holder.textview = (TextView) lView.findViewById(R.id.text1);
                    holder.imageview = (ImageView) lView.findViewById(R.id.iv_dots);
                    lView.setTag(holder);
                } else {
                    holder = (SpinnerHolder) lView.getTag();
                }

                holder.textview.setText(pList.get(i));
                holder.imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu pum = new PopupMenu(PotBaseActivity.this, view);
                        pum.inflate(R.menu.spinner_settings_menu);
                        pum.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.action_add_syllabus:
                                        Message lMsg = new Message();
                                        lMsg.what = pId;
                                        lMsg.arg1 = i;
                                        lMsg.arg2 = item.getItemId();
                                        lMsg.obj = pObj;
                                        m_cObjUIHandler.sendMessage(lMsg);
                                        hideDialog();
                                        break;
                                    case R.id.action_delete:
                                        Message llMsg = new Message();
                                        llMsg.what = pId;
                                        llMsg.arg1 = i;
                                        llMsg.arg2 = item.getItemId();
                                        llMsg.obj = pObj;
                                        m_cObjUIHandler.sendMessage(llMsg);
                                        hideDialog();
                                        break;
                                }
                                return true;
                            }
                        });
                        pum.show();
                    }
                });
                return lView;

                /*View lView = LayoutInflater.from(PotBaseActivity.this).inflate(R.layout.spinner_settings_dialog_item, null);
                TextView ltextview = (TextView) lView.findViewById(R.id.text1);
                ltextview.setText(pList.get(i));
                return lView;*/
            }
        };

        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        lObjBuilder.setAdapter(lAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.w("OnClick", "Triggered on flat listing");
            }
        });
        m_cObjDialog = lObjBuilder.create();
        /*m_cObjDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                ListView lv = m_cObjDialog.getListView(); //this is a ListView with your "buds" in it
                lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                        PopupMenu pum = new PopupMenu(PotBaseActivity.this, view);
                        pum.inflate(R.menu.spinner_settings_menu);
                        pum.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.action_add_syllabus:
                                        break;
                                    case R.id.action_delete:
                                        Message lMsg = new Message();
                                        lMsg.what = pId;
                                        lMsg.arg1 = position;
                                        lMsg.obj = pObj;
                                        m_cObjUIHandler.sendMessage(lMsg);
                                        hideDialog();
                                        break;
                                }
                                return true;
                            }
                        });
                        pum.show();

                        return true;
                    }
                });
            }
        });*/
        m_cObjDialog.setCanceledOnTouchOutside(true);
        m_cObjDialog.show();
    }

    public void displayMultiSpinnerDialog(final int pId, String pTitle, final List<String> pList, final ArrayList<Integer> pClassPosList) {
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        final ArrayList<Integer> selectedPos;
        if (pClassPosList.size() > 0)
            selectedPos = pClassPosList;
        else
            selectedPos = new ArrayList<>();
        BaseAdapter lAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return pList.size();
            }

            @Override
            public Object getItem(int i) {
                return pList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(final int i, View pview, ViewGroup viewGroup) {
                View lView = pview;
                final SpinnerHolder holder;

                if (lView == null) {
                    lView = LayoutInflater.from(PotBaseActivity.this).inflate(R.layout.spinner_multi_dialog_item, null);
                    holder = new SpinnerHolder();
                    holder.textview = (TextView) lView.findViewById(R.id.text1);
                    holder.imageview = (ImageView) lView.findViewById(R.id.iv_tick);
                   lView.setTag(holder);
                } else {
                    holder = (SpinnerHolder) lView.getTag();
                }
                holder.textview.setText(pList.get(i));
                if (selectedPos.indexOf(i) > -1)
                    holder.imageview.setImageResource(R.drawable.tick_blue);
                else
                    holder.imageview.setImageResource(R.drawable.tick_grey);
                lView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedPos.indexOf(i) > -1) {
                            holder.imageview.setImageResource(R.drawable.tick_grey);
                            selectedPos.remove(selectedPos.indexOf(i));
                        } else {
                            holder.imageview.setImageResource(R.drawable.tick_blue);
                            selectedPos.add(i);
                        }
                    }
                });
                return lView;
            }
        };

        View lView = LayoutInflater.from(this).inflate(R.layout.spinner_header, null);
        ((TextView) lView.findViewById(R.id.TEXT_HEAD)).setText(pTitle);
        lObjBuilder.setCustomTitle(lView);
        lObjBuilder.setAdapter(lAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedPos.indexOf(which) > -1)
                    selectedPos.remove(selectedPos.indexOf(which));
                else
                    selectedPos.add(which);
            }
        });
        lObjBuilder.setNeutralButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Message lMsg = new Message();
                        lMsg.what = pId;
                        lMsg.obj = selectedPos;
                        m_cObjUIHandler.sendMessage(lMsg);
                        dialog.dismiss();
                    }
                });
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.setCanceledOnTouchOutside(false);
        m_cObjDialog.show();
    }

    public class SpinnerHolder {
        ImageView imageview;
        TextView textview;
    }

    public void showSettingsAlert() {
        hideDialog();
        String lMessage = getResources().getString(R.string.alert_gps_not_enabled);
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        lObjBuilder.setCancelable(false);
        lObjBuilder.setTitle("GPS is settings");
        lObjBuilder.setMessage(lMessage);

        // On pressing Settings button
        lObjBuilder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1);
            }
        });

        // on pressing cancel button
        lObjBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                hideDialog();
                m_cObjUIHandler.sendEmptyMessage(1);
            }
        });

        // Showing Alert Message
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.show();
        showDialog(DISPLAY_ERROR_ALERT_ID);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public void deleteImage() {
        File lFile = new File(PotMacros.getImageFilePath(this), m_cImageGUID + ".jpg");
        if (lFile.exists()) {
            lFile.delete();
        }
    }

    public void deleteAudio() {
        File lFile = new File(PotMacros.getAudioFilePath(this), m_cAudioGUID + ".mp3");
        if (lFile.exists()) {
            lFile.delete();
        }
    }

    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public boolean isEmailValid(String pEmail) {
        boolean lRetVal = true;
        if (pEmail.trim().length() > 0) {
            CharSequence inputStr = pEmail;
            Pattern pattern = Pattern.compile(PotMacros.EMAIL_PATTERN_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches())
                lRetVal = true;
            else
                lRetVal = false;
        }
        return lRetVal;
    }

    public boolean isPhoneNoValid(String pPhone) {
        boolean lRetVal = true;
        if (pPhone.trim().length() > 0) {
            CharSequence inputStr = pPhone;
            Pattern pattern = Pattern.compile(PotMacros.PHONE_PATTERN_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches())
                lRetVal = true;
            else
                lRetVal = false;
        }
        return lRetVal;
    }

    public boolean isAlphaNumeric(String pAlphaNum) {
        boolean lRetVal = true;
        if (pAlphaNum.trim().length() > 0) {
            CharSequence inputStr = pAlphaNum;
            Pattern pattern = Pattern.compile(PotMacros.ALPHA_NUMERIC_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches())
                lRetVal = true;
            else
                lRetVal = false;
        }
        return lRetVal;
    }

    public boolean isAlphaNumSearch(String pAlphaNum) {
        boolean lRetVal = true;
        if (pAlphaNum.trim().length() > 0) {
            CharSequence inputStr = pAlphaNum;
            Pattern pattern = Pattern.compile(PotMacros.ALPHA_NUMERIC_SEARCH_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches())
                lRetVal = true;
            else
                lRetVal = false;
        }
        return lRetVal;
    }

    public boolean isAlphabetic(String pAlpha) {
        boolean lRetVal = true;
        if (pAlpha.trim().length() > 0) {
            CharSequence inputStr = pAlpha;
            Pattern pattern = Pattern.compile(PotMacros.ALPHABETIC_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches())
                lRetVal = true;
            else
                lRetVal = false;
        }
        return lRetVal;
    }

    public boolean isPassword(String pAlpha) {
        boolean lRetVal = true;
        if (pAlpha.trim().length() > 0) {
            CharSequence inputStr = pAlpha;
            Pattern pattern = Pattern.compile(PotMacros.PASSWORD_MIN_8_ALPHA_1_NUM_1, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches())
                lRetVal = true;
            else
                lRetVal = false;
        }
        return lRetVal;
    }

    public boolean isUsername(String pAlpha) {
        boolean lRetVal = true;
        if (pAlpha.trim().length() > 0) {
            CharSequence inputStr = pAlpha;
            Pattern pattern = Pattern.compile(PotMacros.USEERNAME_MIN_6, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches())
                lRetVal = true;
            else
                lRetVal = false;
        }
        return lRetVal;
    }

    public boolean copyFile(String from, String to) {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File source = new File(from);
                File destination = new File(to);
                verifyStoragePermissions(this);
                if (source.exists()) {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(destination).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }

    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void verifyCallPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_CALL,
                    REQUEST_CALL
            );
        }
    }

    public static void verifyAudioPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_AUDIO,
                    REQUEST_RECORD_AUDIO
            );
        }
    }

    public void verifyAllPermissions(Activity activity) {
        // Check if we have all permission
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permission1 != PackageManager.PERMISSION_GRANTED)
                requestPermissions(PERMISSIONS_ALL, REQUEST_ALL);
            else if (permission2 != PackageManager.PERMISSION_GRANTED)
                requestPermissions(PERMISSIONS_ALL, REQUEST_ALL);
            else
                    m_cObjUIHandler.sendEmptyMessage(PERMISSION_GRANTED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_ALL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    m_cObjUIHandler.sendEmptyMessage(PERMISSION_GRANTED);
                else
                    m_cObjUIHandler.sendEmptyMessage(PERMISSION_DENIED);
                break;
        }
    }

    public class CheckIsNetWorkAvailable extends AsyncTask<String, Void, String> {
        private boolean isSucesses = false;
        private boolean m_cIsDialogReq = false;
        private Object m_cObj;

        public CheckIsNetWorkAvailable(boolean pIsDialogReq, Object pObj){
            this.m_cIsDialogReq = pIsDialogReq;
            this.m_cObj = pObj;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!m_cIsDialogReq) {
                displayProgressBar(-1, "Loading...");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            isSucesses =  activeNetworkInfo != null && activeNetworkInfo.isConnected();
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!m_cIsDialogReq) {
                hideDialog();
            }
            if(isSucesses) {
                Message lMessage = new Message();
                lMessage.what = PotMacros.NOTIFICATION_FOR_NETWORK_CONNECTION_AVAILABLE;
                lMessage.obj = m_cObj;
                m_cObjUIHandler.sendMessage(lMessage);
            } else {
                displayToast("Please check network connection");
            }
        }
    }

    public void showErrorMsg(String lMsg) {
        try {
            JSONObject lJsonObj = new JSONObject(lMsg);
            Toast.makeText(this, lJsonObj.getString("detail"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isSessionValid(){
        boolean lRet = false;
        if (null != PotMacros.getSessionEndTime(this)){
            Date lEndDate = PotMacros.convertStringToDate(PotMacros.getSessionEndTime(this), PotMacros.DATE_FORMAT_UNDERSC_YYYY_MM_DD_HHMMSS_SSSS);
            if (lEndDate.after(new Date()))
                lRet = true;
            else
                lRet = false;
        }
        return lRet;
    }

    public void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public int getDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public void updateAuth(){
        JSONObject lJO = new JSONObject();
        try {
            lJO.put(Constants.LOGIN_PHONE_NUMBER, PotMacros.getOTPPhoneNo(this));
            lJO.put(Constants.PIN, PotMacros.getOTPpin(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestManager.getInstance(this).placeRequest(Constants.SESSIONS, Sessions.class, this, null, null, lJO.toString(), true);
    }

    public void refreshToken() {
        JSONObject lJO = new JSONObject();
        try {
            lJO.put(Constants.LOGIN_PHONE_NUMBER, PotMacros.getOTPPhoneNo(this));
            lJO.put(Constants.NOTIFICATION_TOKEN, PotMacros.getFCMToken(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestManager.getInstance(this).placeUnivRequest(Constants.DEVICES +
                PotMacros.getDeviceID(this) +
                "/", Devices.class, this, null, null, lJO.toString(), Request.Method.PATCH);
    }

    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            m_cObjUIHandler.sendEmptyMessage(PotMacros.REFRESH_NOTIFY_CONSTANT_KEY);
        }
    }

    public void checkAndDelete(String pFilePath) {
        if (null != pFilePath)
            if (new File(pFilePath).exists())
                new File(pFilePath).delete();
    }

    @Override
    public void onClick(View v) {
        Intent lObjIntent;
        switch (v.getId()) {
        /*TODO below code =for slider id's*/
        }
    }

    public boolean logout(){
        Intent lObjIntent;
        if ( PotMacros.removeLoginAuth(this)) {
            lObjIntent = new Intent(this, OtpScreen.class);
            lObjIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            lObjIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(lObjIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            return true;
        }
        return false;
    }

}
