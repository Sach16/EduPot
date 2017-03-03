package com.cosmicdew.lessonpot.macros;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.LessonViews;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.NotifyDS;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by S.K. Pissay on 20/3/16.
 */
public class PotMacros {

    public static boolean m_cGPSEnabled = false;
    public static Location m_cObjCurLocation = null;

    public static final int NOTIFICATION_FOR_NO_NETWORK_CONNECTION = -68653;
    public static final int NOTIFICATION_FOR_NETWORK_CONNECTION_AVAILABLE = 1001;
    public static final int NOTIFICATION_NO_NETWORK_CONNECTION_RETRY = 1002;

    public static final String SUPPORTWIZARD_USERNAME = "supportwizard";
    public static final String SYLLABUSWIZARD_USERNAME = "syllabuswizard";
    public static final String PUBLIC_USERNAME = "public";

    public static final String REMOVELESSON_REFRESH_CONSTANT_VIEWED = "REMOVELESSON_REFRESH_CONSTANT_VIEWED";
    public static final String REMOVELESSON_REFRESH_CONSTANT_RECEIVED = "REMOVELESSON_REFRESH_CONSTANT_RECEIVED";

    public static final String GO_OFFLINE = "GO_OFFLINE";
    public static final String GO_OFFLINE_LESSON_POSITION = "GO_OFFLINE_LESSON_POSITION";

    public static final int APPCONFIG = 1612;

    public static final int LESSONPOSITION = 1342;
    public static final int LESSON_FILTER = 1343;

    public static final String TERMS_AND_CONDITION_URL = "http://www.lessonpot.com/terms";
    public static final String OVERVIEW = "http://www.lessonpot.com/help";
    public static final String PRIVACY_POLICY_URL = "http://www.lessonpot.com/policies";
    public static final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=com.cosmicdew.lessonpot";
    public static final String HTTP_PREFIX = "http";

    public static final String REFRESH_NOTIFY_CONSTANT = "REFRESH_NOTIFY_CONSTANT";
    public static final int REFRESH_NOTIFY_CONSTANT_KEY = 924378;

    public static final int YES_MESSAGE = 1111;
    public static final int NO_MESSAGE = 1112;

    public static final String OBJ_LESSON_FILTER = "OBJ_LESSON_FILTER";
    public static final String IMG_VIEW_ID = "IMG_VIEW_ID";
    public static final int LESSON_ACTION_DOWNLOADED = 9856;
    public static final int SUPPORT_OPTION = 3700;

    public static final int ONLINE_LESSON_NOT_FOUND = 7013;
    public static final int ACTION_POST_TO_PUBLIC = 9174;
    public static final int ACTION_DELETE_LESSON = 9175;

    public static final String OBJ_REGISTERATION = "OBJ_REGISTERATION";
    public static final String OBJ_USER = "OBJ_USER";
    public static final String OBJ_BOARDCHOICES = "OBJ_BOARDCHOICES";
    public static final String OBJ_SYLLABI = "OBJ_SYLLABI";
    public static final String OBJ_CHAPTERS = "OBJ_CHAPTERS";
    public static final String OBJ_LESSON_TYPE = "OBJ_LESSON_TYPE";
    public static final String OBJ_LESSONFROM = "OBJ_LESSONFROM";
    public static final String OBJ_LESSONFROMWCHTAB = "OBJ_LESSONFROMWCHTAB";
    public static final String OBJ_LESSON = "OBJ_LESSON";
    public static final String OBJ_LESSONSHARES = "OBJ_LESSONSHARES";
    public static final String OBJ_LESSONVIEWS = "OBJ_LESSONVIEWS";
    public static final String LESSON_HEADER = "LESSON_HEADER";
    public static final String LESSON_INDEX_PAGE = "LESSON_INDEX_PAGE";

    public static final String OBJ_USER_MAIN = "OBJ_USER_MAIN";

    public static final String NOTIFICATION = "NOTIFICATION";

    public static final String FROM_NETWORK_SCREEN = "FROM_NETWORK_SCREEN";

    public static final String LESSON_IMG_1 = "LESSON_IMG_1";
    public static final String LESSON_IMG_2 = "LESSON_IMG_2";
    public static final String LESSON_IMG_3 = "LESSON_IMG_3";
    public static final String LESSON_AUDIO = "LESSON_AUDIO";

    public static final String ROLE_STUDENT = "student";

    public static final int OBJ_LESSON_NEW = 0;
    public static final int OBJ_LESSON_VIEW = 1;
    public static final int OBJ_LESSON_EDIT = 2;
    public static final int OBJ_LESSON_UPLOAD = 3;

    public static final int ON_INFO_LONG_CLICK_MINE = 3465;
    public static final int ON_INFO_LONG_CLICK_SHARED = 3466;
    public static final int ON_INFO_LONG_CLICK_VIEWED = 3467;

    public static final String OBJ_LESSON_VIEWED_TAB = "OBJ_LESSON_VIEWED_TAB";
    public static final String OBJ_LESSON_RECEIVED_TAB = "OBJ_LESSON_RECEIVED_TAB";
    public static final String OBJ_LESSON_MINE_TAB = "OBJ_LESSON_MINE_TAB";
    public static final String OBJ_LESSON_PUBLIC_TAB = "OBJ_LESSON_PUBLIC_TAB";

    public static final String OBJ_LESSON_LIKES = "OBJ_LESSON_LIKES";
    public static final String OBJ_LESSON_COMMENTS = "OBJ_LESSON_COMMENTS";

    public static final int OBJ_LESSON_LIKES_SHOW = 3711;
    public static final int OBJ_LESSON_COMMENTS_SHOW = 3712;
    public static final int OBJ_LESSON_LIKES_EDIT = 3713;
    public static final int OBJ_LESSON_COMMENTS_EDIT = 3714;
    public static final int OBJ_LESSON_COMMENTS_ADD = 3715;
    public static final int OBJ_LESSON_COMMENTS_SPAM = 3716;

    public static final int ON_INFO_LONG_CLICK_VIEWED_LESSON = 3490;
    public static final int ON_INFO_LONG_CLICK_MINE_LESSON = 3491;

    public static final int ON_INFO_LONG_CLICK_USERS = 3492;
    public static final int ON_INFO_LONG_CLICK_BOARD_CLASS = 3493;
    public static final int ON_INFO_LONG_CLICK_SYLLABI = 3494;

    public static final String OBJ_SELECTIONTYPE_ADDSYLLABUS = "OBJ_SELECTIONTYPE_ADDSYLLABUS";
    public static final String OBJ_SELECTIONTYPE = "OBJ_SELECTIONTYPE";
    public static final String FRAG_CLASSES = "FRAG_CLASSES";
    public static final String FRAG_SUBJECTS = "FRAG_SUBJECTS";
    public static final String FRAG_CHAPTER = "FRAG_CHAPTER";

    public static final String FRAG_LIKES = "FRAG_LIKES";
    public static final String FRAG_COMMENTS = "FRAG_COMMENTS";
    public static final String USER_LIKES_COMMENTS = "USER_LIKES_COMMENTS";

    public static final String FRAG_FOLLOWERS = "FRAG_FOLLOWERS";
    public static final String FRAG_FOLLOWING = "FRAG_FOLLOWING";

    public static final String DISPLAY_DATE_FORMAT = "dd MMM yyyy";
    public static final String DISPLAY_DATE_TIME_FORMAT = "dd MMM yyyy HH:mm";
    public static final String DISPLAY_DATE_TIME_FORMAT_DAY = "E, dd MMM yyyy";
    public static final String DEFAULT_DATEFORMAT_MMDDYYYY = "MM-dd-yyyy";
    public static final String DEFAULT_TIMEFORMAT_HHMMSS = "HH:mm:ss";
    public static final String DATE_FORMAT_EEEMMDD_HHMMSS_ZYYYY = "EEE MMM dd HH:mm:ss z yyyy";
    public static final String DATE_FORMAT_YYYY_MM_DD_SSZ = "yyyy-MM-dd'T'HH:mm:ssZ";//yyyy-MM-dd'T'HH:mm:ssZ
    public static final String DATE_FORMAT_YYYY_MM_DD_HH = "yyyy:MM:dd HH:mm:ss";//2015:08:28 08:56:15
    public static final String TIME_FORMAT_HHMM_AM_PM = "hh:mm aa";
    public static final String TIME_FORMAT_MM_SS = "mm:ss";
    public static final String TIME_FORMAT_HH_MM_SS = "HH:mm:ss";
    public static final String DATE_FORMAT_DDMMMYYYY = "dd MMM yyyy";
    public static final String DATE_FORMAT_MMMDDYYYY = "MMM-dd-yyyy";
    public static final String DATE_FORMAT_DDMMYYYY = "dd MMM";
    public static final String DATE_FORMAT_DOTTED_MMDDYY = "MM.dd.yy";
    public static final String DATE_FORMAT_UNDERSC_YYYY_MM_DD_HHMMSS_SSSS = "yyyy-MM-dd'T'HH:mm:ss.sssZ";
    public static final String DATE_FORMAT_UNDERSC_YYYY_MM_DD_HHMMSS_SSSSSS = "yyyy-MM-dd'T'HH:mm:ss.ssssssZ";
    public static final String DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_UNDERSC_DDMMYYYY_HHMMAA = "dd-MM-yyyy hh:mm aa";


    public static final String DEFAULT_DATEFORMAT_DDMMYYYY = "dd-MM-yyyy";
    public static final String DEFAULT_DATEFORMAT_DDMMYY = "dd-MM-yy";
    public static final String DEFAULT_DATEFORMAT_YYYYMMDD = "yyyy-MM-dd";
    public static final String TIME_FORMAT_HHMMAA = "hh:mmaa";

    public static final String DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS_SSSSSS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS_SSSS = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATE_FORMAT_UNDERSC_YYYY_MM_DD_HHMMSS_ssss = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATE_FORMAT_YYYYMMDD_HHMMSS_SSSS = "yyyy.MM.dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS_T = "yyyy-MM-dd'T'HH:mm:ss";

    //public static final String ANDROID_APP_ID = "ANDROID_PATIENT_V1.1";
    public static final String EMAIL_PATTERN_REGEX =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final String PHONE_PATTERN_REGEX = "^[2-9]{1}[0-9]{9}$";

    public static final String ALPHA_NUMERIC_SEARCH_REGEX = "^[a-zA-Z0-9 -]*$";

    public static final String ALPHA_NUMERIC_REGEX = "^[a-zA-Z0-9 ]*$";

    public static final String ALPHABETIC_REGEX = "^[a-zA ]*$";

    public static final String PASSWORD_MIN_8_ALPHA_1_NUM_1 = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

    public static final String USEERNAME_MIN_6 = "^[a-zA-Z0-9]{6,}$";

    public static final String INCLUDE = "include";
    public static final String PAGINATION_PAGE = "page";

    public static String BINARY_MEDIA_TYPE_JPEG = "image/jpeg";
    public static String BINARY_MEDIA_TYPE_JPG = "image/jpg";
    public static String BINARY_MEDIA_TYPE_WAV = "audio/wav";
    public static String BINARY_MEDIA_TYPE_AMR = "audio/amr";
    public static String IMAGE_FILE_EXTENSIONS = "jpeg jpg bmp gif";
    public static String AUDIO_FILE_EXTENSIONS = "wav amr mp3";
    public static String TXT_FILE_EXT = "txt";
    public static String SPACE = " ";

    public static String EPRESCRIPTION_PATH = Environment.getExternalStorageDirectory() + "/EURemedi/EPrescreption";

    public static void ShowFile(Context pObjContext, String pPath) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        File file = new File(pPath);

        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);

        intent.setDataAndType(Uri.fromFile(file), type);

        try {
            pObjContext.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(pObjContext, "Not able to open the file format,..", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static boolean isNetworkConnected(Context pObjContext) {
        ConnectivityManager cm = (ConnectivityManager) pObjContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public static String getRupees(String pAmount) {
        StringBuffer lRetVal = new StringBuffer();
        if (null != pAmount && pAmount.toString().trim().length() > 0) {
            if (pAmount.contains("₹"))
                return pAmount;
            else
                lRetVal.append("\u20B9 ").append(pAmount);
        } else {
            lRetVal.append("\u20B9 ").append("0");
        }
        return lRetVal.toString();
    }

    //Slug to label
    public static String s2l(String pSlug) {
        StringBuffer lBuff = new StringBuffer();
        String lSample = pSlug.toLowerCase().replace("_", " ");
        String[] titleArray = lSample.split(" ");
        for (String lparam : titleArray) {
            lBuff.append(Character.toUpperCase(lparam.charAt(0))).append(lparam.substring(1)).append(" ");
        }
        return lBuff.toString();
    }

    public static String getFormatedTimer(long pMilliseconds) {
//        long minutes = TimeUnit.MILLISECONDS.toMinutes(pMilliseconds);
//        long seconds = TimeUnit.MILLISECONDS.toSeconds(pMilliseconds) != 60 ?  TimeUnit.MILLISECONDS.toSeconds(pMilliseconds) : 00;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(pMilliseconds) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(pMilliseconds) % TimeUnit.MINUTES.toSeconds(1);

        return String.format("%02d:%02d", minutes, seconds);
//        return (new SimpleDateFormat(TIME_FORMAT_MM_SS)).format(new Date(pMilliseconds));
    }

    public static String getFormatedTimerHMS(long pMilliseconds) {
//        long minutes = TimeUnit.MILLISECONDS.toMinutes(pMilliseconds);
//        long seconds = TimeUnit.MILLISECONDS.toSeconds(pMilliseconds) != 60 ?  TimeUnit.MILLISECONDS.toSeconds(pMilliseconds) : 00;
        long hours = TimeUnit.MILLISECONDS.toHours(pMilliseconds);
        pMilliseconds -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(pMilliseconds);
        pMilliseconds -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(pMilliseconds);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
//        return (new SimpleDateFormat(TIME_FORMAT_MM_SS)).format(new Date(pMilliseconds));
    }

    public static final ButterKnife.Action<View> DISABLE = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, int index) {
            view.setEnabled(false);
        }
    };

    public static final ButterKnife.Setter<View, Boolean> SETENABLED = new ButterKnife.Setter<View, Boolean>() {
        @Override
        public void set(View view, Boolean value, int index) {
            view.setEnabled(value);
        }
    };

    public static int getRandomColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(150), rnd.nextInt(150), rnd.nextInt(150));
        return color;
    }

    public static String getMIMEtype(String pFileName) {
        String lRetVal = SPACE;
        if (null != pFileName) {
            int lIndex = pFileName.lastIndexOf('.');
            if (lIndex > 0) {
                String lExtention = pFileName.substring(lIndex + 1);
                if (null != lExtention) {
                    if (AUDIO_FILE_EXTENSIONS.indexOf(lExtention.toLowerCase()) != -1) {
                        lRetVal = BINARY_MEDIA_TYPE_AMR;
                    } else if (IMAGE_FILE_EXTENSIONS.indexOf(lExtention.toLowerCase()) != -1) {
                        lRetVal = BINARY_MEDIA_TYPE_JPG;
                    }
                }
            }
        }
        return lRetVal;
    }

    public static void showCustomAlert(Context pObjContext, int pObjX, int pObjY) {
        // Create layout inflator object to inflate toast.xml file
        LayoutInflater inflater = (LayoutInflater) pObjContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Call toast.xml file for toast layout
        View toastRoot = inflater.inflate(R.layout.toast, null);

        Toast toast = new Toast(pObjContext);

        // Set layout to toast
        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER, pObjX, pObjY);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public static int roundOffFloat(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#");
        return Integer.valueOf(twoDForm.format(d));
    }

    public static void exportDatabse(Context pObjContext, String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + pObjContext.getPackageName() + "//databases//" + databaseName + "";
                String backupDBPath = "backupname.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }

    public static String getDateFormatDDMMYYYY(Date pDate, String pDateTxt) {
        String lFormatedDate = null;
        SimpleDateFormat lObjFormat = new SimpleDateFormat(PotMacros.DEFAULT_DATEFORMAT_DDMMYYYY);
        Date lObjDate = null;
        try {
            if (null != pDateTxt) {
                SimpleDateFormat lObjFormat1 = new SimpleDateFormat(PotMacros.DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS);
                lObjDate = lObjFormat1.parse(pDateTxt);
            } else if (null != pDate) {
                lObjDate = pDate;
            }
            if (null != lObjDate) {
//					lObjFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.LONG, Locale.US);
                lFormatedDate = lObjFormat.format(lObjDate);
            }
        } catch (Exception ex) {
            lFormatedDate = "";
            ex.printStackTrace();
        }
        return lFormatedDate;
    }

    public static String getDateFormatYYYYMMDD(Date pDate, String pDateTxt) {
        String lFormatedDate = null;
        SimpleDateFormat lObjFormat = new SimpleDateFormat(PotMacros.DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS);
        Date lObjDate = null;
        try {
            if (null != pDateTxt) {
                SimpleDateFormat lObjFormat1 = new SimpleDateFormat(PotMacros.DEFAULT_DATEFORMAT_DDMMYYYY);
                lObjDate = lObjFormat1.parse(pDateTxt);
            } else if (null != pDate) {
                lObjDate = pDate;
            }
            if (null != lObjDate) {
//					lObjFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.LONG, Locale.US);
                lFormatedDate = lObjFormat.format(lObjDate);
            }
        } catch (Exception ex) {
            lFormatedDate = "";
            ex.printStackTrace();
        }
        return lFormatedDate;
    }

    public static String getDateFormat(Date pDate, String pDateTxt, String pInPut, String pOutPut) {
        String lFormatedDate = null;
        SimpleDateFormat lObjFormat = new SimpleDateFormat(pOutPut);
        Date lObjDate = null;
        try {
            if (null != pDateTxt) {
                SimpleDateFormat lObjFormat1 = new SimpleDateFormat(pInPut, Locale.US);
                lObjFormat1.setTimeZone(TimeZone.getTimeZone("gmt"));
                lObjDate = lObjFormat1.parse(pDateTxt);
            } else if (null != pDate) {
                lObjDate = pDate;
            }
            if (null != lObjDate) {
                lFormatedDate = lObjFormat.format(lObjDate);
            }
        } catch (Exception ex) {
            lFormatedDate = "";
            ex.printStackTrace();
        }
        return lFormatedDate;
    }

    public static Date convertStringToDate(String pDateString, String pFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pFormat);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(pDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        if (diff < 0) {
            diff = 1;
        }
        return diff;
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static boolean getIsTodaysDate(String pDateString, String pFormat) {
        Date lDate = convertStringToDate(pDateString, pFormat);
        return DateUtils.isToday(lDate.getTime());
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public static String getUtcDateFormat(Date pDate, String pDateTxt, String pInPut, String pOutPut) {
        String lFormatedDate = null;
        SimpleDateFormat lObjFormat = new SimpleDateFormat(pOutPut);
        lObjFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
        Date lObjDate = null;
        try {
            if (null != pDateTxt) {
                SimpleDateFormat lObjFormat1 = new SimpleDateFormat(pInPut);
                lObjDate = lObjFormat1.parse(pDateTxt);
            } else if (null != pDate) {
                lObjDate = pDate;
            }
            if (null != lObjDate) {
                lFormatedDate = lObjFormat.format(lObjDate);
            }
        } catch (Exception ex) {
            lFormatedDate = "";
        }
        return lFormatedDate;
    }

    public static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static void saveLoginAuth(Context pContext, String pValue) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LOGIN_AUTH_TOKEN", pValue);
        editor.commit();
    }

    public static void saveSessionId(Context pContext, Integer pValue) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("LOGIN_AUTH_TOKEN_SESSION_ID", pValue);
        editor.commit();
    }

    public static void saveGreenSessionId(Context pContext, Integer pValue) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("LOGIN_AUTH_GREEN_TOKEN_SESSION_ID", pValue);
        editor.commit();
    }

    public static void saveUserToken(Context pContext, String pValue) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LOGIN_AUTH_TOKEN_USER_TOKEN", pValue);
        editor.commit();
    }

    public static void setOTPGen(Context pContext, boolean pState) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("OTP_GEN", pState);
        editor.commit();
    }

    public static void setOTPpin(Context pContext, String pPin) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("OTP_PIN", pPin);
        editor.commit();
    }

    public static void setDeviceID(Context pContext, Integer pDeviceId) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("DEVICE_ID", pDeviceId);
        editor.commit();
    }

    public static void setFCMToken(Context pContext, String pToken) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("FCM_TOKEN", pToken);
        editor.commit();
    }

    public static void setOTPPhoneNo(Context pContext, String pOtpPhone) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("OTP_PHONE", pOtpPhone);
        editor.commit();
    }

    public static void setSessionEndTime(Context pContext, String pEndTime) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("SESSION_END_TIME", pEndTime);
        editor.commit();
    }

    public static void setAgreeTerms(Context pContext, boolean pAgree) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("TERMS_CONDITION", pAgree);
        editor.commit();
    }

    public static void setResendOtpCount(Context pContext, Integer pCount) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("RESEND_COUNT", pCount);
        editor.commit();
    }

    public static void setNotifyCount(Context pContext, String pTag, Integer pNotificationId, Users pUsers) {
        SharedPreferences prefs = pContext.getSharedPreferences("NOTIFICATION_DS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (null == pContext.getSharedPreferences("NOTIFICATION_DS", Context.MODE_PRIVATE).getString(pUsers.getId().toString(), null)) {
            NotifyDS lNotifyDS = new NotifyDS();
            lNotifyDS.setUsers(pUsers);
            switch (pTag) {
                case Constants.CONNECTION_REQUEST:
                    lNotifyDS.setConnectionRequests(1);
                    lNotifyDS.setConnectionApproved(0);
                    lNotifyDS.setLessonShare(0);
                    lNotifyDS.setConnectionRequestsIds(Collections.singletonList(pNotificationId));
                    break;
                case Constants.CONNECTION_APPROVED:
                    lNotifyDS.setConnectionRequests(0);
                    lNotifyDS.setConnectionApproved(1);
                    lNotifyDS.setLessonShare(0);
                    break;
                case Constants.LESSON_SHARE:
                case Constants.LESSON_EDIT:
                    lNotifyDS.setConnectionRequests(0);
                    lNotifyDS.setConnectionApproved(0);
                    lNotifyDS.setLessonShare(1);
                    lNotifyDS.setLessonShareIds(Collections.singletonList(pNotificationId));
                    break;
            }
            editor.putString(pUsers.getId().toString(), (new Gson()).toJson(lNotifyDS));
            editor.commit();
        } else {
            String lNotifyDSStr = pContext.getSharedPreferences("NOTIFICATION_DS", Context.MODE_PRIVATE).getString(pUsers.getId().toString(), null);
            NotifyDS lNotifyDS = (new Gson()).fromJson(lNotifyDSStr, NotifyDS.class);
            lNotifyDS.setUsers(pUsers);
            switch (pTag) {
                case Constants.CONNECTION_REQUEST:
                    lNotifyDS.setConnectionRequests(lNotifyDS.getConnectionRequests() + 1);
                    lNotifyDS.getConnectionRequestsIds().add(pNotificationId);
                    lNotifyDS.setConnectionRequestsIds(lNotifyDS.getConnectionRequestsIds());
                    break;
                case Constants.CONNECTION_APPROVED:
                    lNotifyDS.setConnectionApproved(lNotifyDS.getConnectionApproved() + 1);
                    break;
                case Constants.LESSON_SHARE:
                case Constants.LESSON_EDIT:
                    lNotifyDS.setLessonShare(lNotifyDS.getLessonShare() + 1);
                    lNotifyDS.getLessonShareIds().add(pNotificationId);
                    lNotifyDS.setLessonShareIds(lNotifyDS.getLessonShareIds());
                    break;
            }
            editor.putString(pUsers.getId().toString(), (new Gson()).toJson(lNotifyDS));
            editor.commit();
        }
    }

    public static void clearNotifyCount(Context pContext, String pTag, Users pUsers) {
        SharedPreferences prefs = pContext.getSharedPreferences("NOTIFICATION_DS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String lNotifyDSStr = pContext.getSharedPreferences("NOTIFICATION_DS", Context.MODE_PRIVATE).getString(pUsers.getId().toString(), null);
        if (null != lNotifyDSStr) {
            NotifyDS lNotifyDS = (new Gson()).fromJson(lNotifyDSStr, NotifyDS.class);
            lNotifyDS.setUsers(pUsers);
            switch (pTag) {
                case Constants.CONNECTION_REQUEST:
                case Constants.CONNECTION_APPROVED:
                    lNotifyDS.setConnectionRequests(0);
                    lNotifyDS.setConnectionApproved(0);
                    for (Integer lInteger : lNotifyDS.getConnectionRequestsIds()) {
                        NotificationManager notificationManager = (NotificationManager) pContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(lInteger);
                    }
                    lNotifyDS.getConnectionRequestsIds().clear();
                    break;
                case Constants.LESSON_SHARE:
                case Constants.LESSON_EDIT:
                    lNotifyDS.setLessonShare(0);
                    for (Integer lInteger : lNotifyDS.getLessonShareIds()) {
                        NotificationManager notificationManager = (NotificationManager) pContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(lInteger);
                    }
                    lNotifyDS.getLessonShareIds().clear();
                    break;
            }
            editor.putString(pUsers.getId().toString(), (new Gson()).toJson(lNotifyDS));
            editor.commit();
        }
    }

    public static int[] getNotifyCount(Context pContext, Users pUsers) {
        SharedPreferences prefs = pContext.getSharedPreferences("NOTIFICATION_DS", Context.MODE_PRIVATE);
        if (null == pContext.getSharedPreferences("NOTIFICATION_DS", Context.MODE_PRIVATE).getString(pUsers.getId().toString(), null)) {
            return new int[]{0, 0};
        } else {
            String lNotifyDSStr = pContext.getSharedPreferences("NOTIFICATION_DS", Context.MODE_PRIVATE).getString(pUsers.getId().toString(), null);
            NotifyDS lNotifyDS = (new Gson()).fromJson(lNotifyDSStr, NotifyDS.class);
            return new int[]{lNotifyDS.getConnectionApproved() +
                    lNotifyDS.getConnectionRequests(), lNotifyDS.getLessonShare()};
        }
    }

    public static int[] getNotifyAll(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("NOTIFICATION_DS", Context.MODE_PRIVATE);
        if (null == pContext.getSharedPreferences("NOTIFICATION_DS", Context.MODE_PRIVATE).getAll()) {
            return new int[]{0, 0};
        } else {
            Map<String, ?> lNotifyDSAll = pContext.getSharedPreferences("NOTIFICATION_DS", Context.MODE_PRIVATE).getAll();
            int lCount = 0;
            for (int i = 0; i < lNotifyDSAll.size(); i++) {
                NotifyDS lNotifyDS = (new Gson()).fromJson((String) lNotifyDSAll.get((lNotifyDSAll.keySet().toArray())[i]), NotifyDS.class);
                lCount = lCount + lNotifyDS.getLessonShare() + lNotifyDS.getConnectionRequests();
            }
            Log.w(TAG, "ALL COUNTS DS :" + lNotifyDSAll.toString());
            return new int[]{lCount, 0};
        }
    }

    public static String getLoginAuth(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        return prefs.getString("LOGIN_AUTH_TOKEN", null);
    }

    public static Integer getSessionId(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        return prefs.getInt("LOGIN_AUTH_TOKEN_SESSION_ID", -1);
    }

    public static Integer getGreenSessionId(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        return prefs.getInt("LOGIN_AUTH_GREEN_TOKEN_SESSION_ID", -1);
    }

    public static String getUserToken(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        return prefs.getString("LOGIN_AUTH_TOKEN_USER_TOKEN", null);
    }

    public static boolean getOTPGen(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        return prefs.getBoolean("OTP_GEN", false);
    }

    public static Integer getDeviceID(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        return prefs.getInt("DEVICE_ID", -1);
    }

    public static String getFCMToken(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        return prefs.getString("FCM_TOKEN", null);
    }

    public static String getOTPpin(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        return prefs.getString("OTP_PIN", null);
    }

    public static String getSessionEndTime(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        return prefs.getString("SESSION_END_TIME", null);
    }

    public static String getOTPPhoneNo(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        return prefs.getString("OTP_PHONE", null);
    }

    public static boolean getAgreeTerms(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        return prefs.getBoolean("TERMS_CONDITION", false);
    }

    public static int getResendOtpCount(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences("LOGIN_AUTH", Context.MODE_PRIVATE);
        return prefs.getInt("RESEND_COUNT", 0);
    }

    public static ArrayList<String> getStartDay(Context pContext) {
        ArrayList<String> lArry = new ArrayList<>();
        SharedPreferences prefs = pContext.getSharedPreferences("START_DAY", Context.MODE_PRIVATE);
        lArry.add(prefs.getString("START_DAY_DATE", null));
        lArry.add(prefs.getString("START_DAY_TIME", null));
        return lArry;
    }

    public static boolean removeLoginAuth(Context pContext) {
        SharedPreferences preferences = pContext.getSharedPreferences("LOGIN_AUTH", 0);
        preferences.edit().remove("LOGIN_AUTH_TOKEN").commit();
        preferences.edit().remove("OTP_GEN").commit();
        return preferences.edit().remove("OTP_PHONE").commit();
    }

    public static void setPushSetting(Context pContext, boolean pStart) {
        if (pStart) {
            FirebaseInstanceId.getInstance().getToken();
        } else {
            Thread lThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            lThread.start();
        }
    }

    public static Object[] getLessonSettingsOptionsList(Context pCtx, Lessons pLessons,
                                                        LessonShares pLessonShares,
                                                        LessonViews pLessonViews,
                                                        String pLessFromWhereWchTab, Users pUsers) {
        Object[] lRelObjArr = null;
        List<String> pList = null;
        List<Integer> pListIds = null;
        String T = "1";
        String F = "0";
        String NO = "0";
        String CO = "1";
        String FO = "2";
        String creator, sharable, offlineable, postedTo, isSpecificSyll, showSharer;
        creator = pLessons.getOwner().getId().equals(pUsers.getId()) ? T : F;
        sharable = pLessons.getSharable() ? T : F;
        offlineable = pLessons.getOfflineable() ? T : F;
        isSpecificSyll = !pLessons.getChapter().getSyllabus().getIsGeneric() ? T : F;
        switch (pLessons.getPostedTo()) {
            case Constants.POSTED_NONE:
                postedTo = NO;
                break;
            case Constants.POSTED_CONNECTIONS:
                postedTo = CO;
                break;
            case Constants.POSTED_FOLLOWERS:
                postedTo = FO;
                break;
            default:
                postedTo = NO;
                break;
        }

        int llessonSource;
        switch (pLessFromWhereWchTab) {
            case PotMacros.OBJ_LESSON_MINE_TAB:
                llessonSource = pLessons.getOwner().getId();
                break;
            case PotMacros.OBJ_LESSON_RECEIVED_TAB:
                llessonSource = pLessonShares.getFromUser().getId();
                break;
            case PotMacros.OBJ_LESSON_VIEWED_TAB:
                llessonSource = pLessonViews.getSource().getId();
                break;
            default:
                llessonSource = pLessons.getOwner().getId();
                break;
        }
        showSharer = !pLessons.getOwner().getId().equals(llessonSource) ? T : F;
        String lFormat;
        if (!pLessFromWhereWchTab.equals(PotMacros.OBJ_LESSON_PUBLIC_TAB)) {
            lFormat = String.format("%s%s%s%s", creator, sharable, offlineable, postedTo);
            switch (lFormat) {
                case "1110":
                case "1010":
                case "1100":
                case "1000":
                    if (isSpecificSyll.equals(T)) {
                        pList = Arrays.asList(pCtx.getResources().getString(R.string.action_post_to_public),
                                pCtx.getResources().getString(R.string.action_post_to_connections),
                                pCtx.getResources().getString(R.string.action_share),
                                pCtx.getResources().getString(R.string.action_edit),
                                pCtx.getResources().getString(R.string.action_delete_lesson),
                                pCtx.getResources().getString(R.string.action_delete_all_shares),
                                pCtx.getResources().getString(R.string.action_save),
                                pCtx.getResources().getString(R.string.action_view_creator_profile),
                                pCtx.getResources().getString(R.string.action_report_spam));
                        pListIds = Arrays.asList(R.id.action_post_to_public,
                                R.id.action_post_to_connections,
                                R.id.action_share,
                                R.id.action_edit,
                                R.id.action_delete_lesson,
                                R.id.action_delete_all_shares,
                                R.id.action_save,
                                R.id.action_view_creator_profile,
                                R.id.action_report_spam);
                    } else {
                        pList = Arrays.asList(pCtx.getResources().getString(R.string.action_post_to_connections),
                                pCtx.getResources().getString(R.string.action_share),
                                pCtx.getResources().getString(R.string.action_edit),
                                pCtx.getResources().getString(R.string.action_delete_lesson),
                                pCtx.getResources().getString(R.string.action_delete_all_shares),
                                pCtx.getResources().getString(R.string.action_save),
                                pCtx.getResources().getString(R.string.action_view_creator_profile),
                                pCtx.getResources().getString(R.string.action_report_spam));
                        pListIds = Arrays.asList(R.id.action_post_to_connections,
                                R.id.action_share,
                                R.id.action_edit,
                                R.id.action_delete_lesson,
                                R.id.action_delete_all_shares,
                                R.id.action_save,
                                R.id.action_view_creator_profile,
                                R.id.action_report_spam);
                    }
                    break;
                case "1111":
                case "1101":
                case "1011":
                case "1001":
                    if (isSpecificSyll.equals(T)) {
                        pList = Arrays.asList(pCtx.getResources().getString(R.string.action_extend_post_to_public),
                                pCtx.getResources().getString(R.string.action_unpost_from_connections),
                                pCtx.getResources().getString(R.string.action_share),
                                pCtx.getResources().getString(R.string.action_edit),
                                pCtx.getResources().getString(R.string.action_delete_lesson),
                                pCtx.getResources().getString(R.string.action_delete_all_shares),
                                pCtx.getResources().getString(R.string.action_save),
                                pCtx.getResources().getString(R.string.action_view_creator_profile),
                                pCtx.getResources().getString(R.string.action_report_spam));
                        pListIds = Arrays.asList(R.id.action_extend_post_to_public,
                                R.id.action_unpost_from_connections,
                                R.id.action_share,
                                R.id.action_edit,
                                R.id.action_delete_lesson,
                                R.id.action_delete_all_shares,
                                R.id.action_save,
                                R.id.action_view_creator_profile,
                                R.id.action_report_spam);
                    } else {
                        pList = Arrays.asList(pCtx.getResources().getString(R.string.action_unpost_from_connections),
                                pCtx.getResources().getString(R.string.action_share),
                                pCtx.getResources().getString(R.string.action_edit),
                                pCtx.getResources().getString(R.string.action_delete_lesson),
                                pCtx.getResources().getString(R.string.action_delete_all_shares),
                                pCtx.getResources().getString(R.string.action_save),
                                pCtx.getResources().getString(R.string.action_view_creator_profile),
                                pCtx.getResources().getString(R.string.action_report_spam));
                        pListIds = Arrays.asList(R.id.action_unpost_from_connections,
                                R.id.action_share,
                                R.id.action_edit,
                                R.id.action_delete_lesson,
                                R.id.action_delete_all_shares,
                                R.id.action_save,
                                R.id.action_view_creator_profile,
                                R.id.action_report_spam);
                    }
                    break;
                case "1112":
                case "1102":
                    /*Because creators public lesson must always be sharable*/
//                case "1012":
//                case "1002":
                    pList = Arrays.asList(pCtx.getResources().getString(R.string.action_unpost_from_public),
                            pCtx.getResources().getString(R.string.action_share),
                            pCtx.getResources().getString(R.string.action_edit),
                            pCtx.getResources().getString(R.string.action_delete_lesson),
                            pCtx.getResources().getString(R.string.action_delete_all_shares),
                            pCtx.getResources().getString(R.string.action_save),
                            pCtx.getResources().getString(R.string.action_view_creator_profile),
                            pCtx.getResources().getString(R.string.action_report_spam));
                    pListIds = Arrays.asList(R.id.action_unpost_from_public,
                            R.id.action_share,
                            R.id.action_edit,
                            R.id.action_delete_lesson,
                            R.id.action_delete_all_shares,
                            R.id.action_save,
                            R.id.action_view_creator_profile,
                            R.id.action_report_spam);
                    break;
                case "0110":
                    if (isSpecificSyll.equals(T)) {
                        if (showSharer.equals(T)) {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_post_to_followers_and_connections),
                                    pCtx.getResources().getString(R.string.action_post_to_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_save),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_view_sharer_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_post_to_followers_and_connections,
                                    R.id.action_post_to_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_save,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_view_sharer_profile,
                                    R.id.action_report_spam);
                        } else {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_post_to_followers_and_connections),
                                    pCtx.getResources().getString(R.string.action_post_to_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_save),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_post_to_followers_and_connections,
                                    R.id.action_post_to_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_save,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_report_spam);

                        }
                    } else {
                        if (showSharer.equals(T)) {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_post_to_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_view_sharer_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_post_to_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_view_sharer_profile,
                                    R.id.action_report_spam);
                        } else {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_post_to_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_post_to_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_report_spam);
                        }
                    }
                    break;
                case "0100":
                    if (isSpecificSyll.equals(T)) {
                        if (showSharer.equals(T)) {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_post_to_followers_and_connections),
                                    pCtx.getResources().getString(R.string.action_post_to_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_view_sharer_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_post_to_followers_and_connections,
                                    R.id.action_post_to_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_view_sharer_profile,
                                    R.id.action_report_spam);
                        } else {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_post_to_followers_and_connections),
                                    pCtx.getResources().getString(R.string.action_post_to_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_post_to_followers_and_connections,
                                    R.id.action_post_to_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_report_spam);

                        }
                    } else {
                        if (showSharer.equals(T)) {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_post_to_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_view_sharer_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_post_to_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_view_sharer_profile,
                                    R.id.action_report_spam);
                        } else {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_post_to_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_post_to_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_report_spam);
                        }
                    }
                    break;
                case "0010":
                    if (showSharer.equals(T)) {
                        pList = Arrays.asList(pCtx.getResources().getString(R.string.action_remove),
                                pCtx.getResources().getString(R.string.action_delete_my_shares),
                                pCtx.getResources().getString(R.string.action_save),
                                pCtx.getResources().getString(R.string.action_add_syllabus),
                                pCtx.getResources().getString(R.string.action_view_creator_profile),
                                pCtx.getResources().getString(R.string.action_view_sharer_profile),
                                pCtx.getResources().getString(R.string.action_report_spam));
                        pListIds = Arrays.asList(R.id.action_remove,
                                R.id.action_delete_my_shares,
                                R.id.action_save,
                                R.id.action_add_syllabus,
                                R.id.action_view_creator_profile,
                                R.id.action_view_sharer_profile,
                                R.id.action_report_spam);
                    } else {
                        pList = Arrays.asList(pCtx.getResources().getString(R.string.action_remove),
                                pCtx.getResources().getString(R.string.action_delete_my_shares),
                                pCtx.getResources().getString(R.string.action_save),
                                pCtx.getResources().getString(R.string.action_add_syllabus),
                                pCtx.getResources().getString(R.string.action_view_creator_profile),
                                pCtx.getResources().getString(R.string.action_report_spam));
                        pListIds = Arrays.asList(R.id.action_remove,
                                R.id.action_delete_my_shares,
                                R.id.action_save,
                                R.id.action_add_syllabus,
                                R.id.action_view_creator_profile,
                                R.id.action_report_spam);
                    }
                    break;
                case "0000":
                    if (showSharer.equals(T)) {
                        pList = Arrays.asList(pCtx.getResources().getString(R.string.action_remove),
                                pCtx.getResources().getString(R.string.action_delete_my_shares),
                                pCtx.getResources().getString(R.string.action_add_syllabus),
                                pCtx.getResources().getString(R.string.action_view_creator_profile),
                                pCtx.getResources().getString(R.string.action_view_sharer_profile),
                                pCtx.getResources().getString(R.string.action_report_spam));
                        pListIds = Arrays.asList(R.id.action_remove,
                                R.id.action_delete_my_shares,
                                R.id.action_add_syllabus,
                                R.id.action_view_creator_profile,
                                R.id.action_view_sharer_profile,
                                R.id.action_report_spam);
                    } else {
                        pList = Arrays.asList(pCtx.getResources().getString(R.string.action_remove),
                                pCtx.getResources().getString(R.string.action_delete_my_shares),
                                pCtx.getResources().getString(R.string.action_add_syllabus),
                                pCtx.getResources().getString(R.string.action_view_creator_profile),
                                pCtx.getResources().getString(R.string.action_report_spam));
                        pListIds = Arrays.asList(R.id.action_remove,
                                R.id.action_delete_my_shares,
                                R.id.action_add_syllabus,
                                R.id.action_view_creator_profile,
                                R.id.action_report_spam);
                    }
                    break;
                case "0111":
                    if (isSpecificSyll.equals(T)) {
                        if (showSharer.equals(T)) {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_extend_post_to_followers),
                                    pCtx.getResources().getString(R.string.action_unpost_from_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_save),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_view_sharer_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_extend_post_to_followers,
                                    R.id.action_unpost_from_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_save,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_view_sharer_profile,
                                    R.id.action_report_spam);
                        } else {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_extend_post_to_followers),
                                    pCtx.getResources().getString(R.string.action_unpost_from_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_save),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_extend_post_to_followers,
                                    R.id.action_unpost_from_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_save,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_report_spam);
                        }
                    } else {
                        if (showSharer.equals(T)) {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_unpost_from_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_save),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_view_sharer_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_unpost_from_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_save,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_view_sharer_profile,
                                    R.id.action_report_spam);
                        } else {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_unpost_from_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_save),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_unpost_from_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_save,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_report_spam);
                        }
                    }
                    break;
                case "0101":
                    if (isSpecificSyll.equals(T)) {
                        if (showSharer.equals(T)) {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_extend_post_to_followers),
                                    pCtx.getResources().getString(R.string.action_unpost_from_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_view_sharer_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_extend_post_to_followers,
                                    R.id.action_unpost_from_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_view_sharer_profile,
                                    R.id.action_report_spam);
                        } else {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_extend_post_to_followers),
                                    pCtx.getResources().getString(R.string.action_unpost_from_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_extend_post_to_followers,
                                    R.id.action_unpost_from_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_report_spam);
                        }
                    } else {
                        if (showSharer.equals(T)) {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_unpost_from_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_view_sharer_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_unpost_from_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_view_sharer_profile,
                                    R.id.action_report_spam);
                        } else {
                            pList = Arrays.asList(pCtx.getResources().getString(R.string.action_unpost_from_connections),
                                    pCtx.getResources().getString(R.string.action_share),
                                    pCtx.getResources().getString(R.string.action_remove),
                                    pCtx.getResources().getString(R.string.action_delete_my_shares),
                                    pCtx.getResources().getString(R.string.action_add_syllabus),
                                    pCtx.getResources().getString(R.string.action_view_creator_profile),
                                    pCtx.getResources().getString(R.string.action_report_spam));
                            pListIds = Arrays.asList(R.id.action_unpost_from_connections,
                                    R.id.action_share,
                                    R.id.action_remove,
                                    R.id.action_delete_my_shares,
                                    R.id.action_add_syllabus,
                                    R.id.action_view_creator_profile,
                                    R.id.action_report_spam);
                        }
                    }
                    break;
                /*Because if lesson is not sharable, non creator will not have post entries*/
//                case "0011":
//                case "0001":
//                case "0012":
//                case "0002":
                case "0112":
                    if (showSharer.equals(T)) {
                        pList = Arrays.asList(pCtx.getResources().getString(R.string.action_unpost_from_followers),
                                pCtx.getResources().getString(R.string.action_share),
                                pCtx.getResources().getString(R.string.action_remove),
                                pCtx.getResources().getString(R.string.action_delete_my_shares),
                                pCtx.getResources().getString(R.string.action_save),
                                pCtx.getResources().getString(R.string.action_add_syllabus),
                                pCtx.getResources().getString(R.string.action_view_creator_profile),
                                pCtx.getResources().getString(R.string.action_view_sharer_profile),
                                pCtx.getResources().getString(R.string.action_report_spam));
                        pListIds = Arrays.asList(R.id.action_unpost_from_followers,
                                R.id.action_share,
                                R.id.action_remove,
                                R.id.action_delete_my_shares,
                                R.id.action_save,
                                R.id.action_add_syllabus,
                                R.id.action_view_creator_profile,
                                R.id.action_view_sharer_profile,
                                R.id.action_report_spam);
                    }else {
                        pList = Arrays.asList(pCtx.getResources().getString(R.string.action_unpost_from_followers),
                                pCtx.getResources().getString(R.string.action_share),
                                pCtx.getResources().getString(R.string.action_remove),
                                pCtx.getResources().getString(R.string.action_delete_my_shares),
                                pCtx.getResources().getString(R.string.action_save),
                                pCtx.getResources().getString(R.string.action_add_syllabus),
                                pCtx.getResources().getString(R.string.action_view_creator_profile),
                                pCtx.getResources().getString(R.string.action_report_spam));
                        pListIds = Arrays.asList(R.id.action_unpost_from_followers,
                                R.id.action_share,
                                R.id.action_remove,
                                R.id.action_delete_my_shares,
                                R.id.action_save,
                                R.id.action_add_syllabus,
                                R.id.action_view_creator_profile,
                                R.id.action_report_spam);
                    }
                    break;
                case "0102":
                    if (showSharer.equals(T)) {
                        pList = Arrays.asList(pCtx.getResources().getString(R.string.action_unpost_from_followers),
                                pCtx.getResources().getString(R.string.action_share),
                                pCtx.getResources().getString(R.string.action_remove),
                                pCtx.getResources().getString(R.string.action_delete_my_shares),
                                pCtx.getResources().getString(R.string.action_add_syllabus),
                                pCtx.getResources().getString(R.string.action_view_creator_profile),
                                pCtx.getResources().getString(R.string.action_view_sharer_profile),
                                pCtx.getResources().getString(R.string.action_report_spam));
                        pListIds = Arrays.asList(R.id.action_unpost_from_followers,
                                R.id.action_share,
                                R.id.action_remove,
                                R.id.action_delete_my_shares,
                                R.id.action_add_syllabus,
                                R.id.action_view_creator_profile,
                                R.id.action_view_sharer_profile,
                                R.id.action_report_spam);
                    }else {
                        pList = Arrays.asList(pCtx.getResources().getString(R.string.action_unpost_from_followers),
                                pCtx.getResources().getString(R.string.action_share),
                                pCtx.getResources().getString(R.string.action_remove),
                                pCtx.getResources().getString(R.string.action_delete_my_shares),
                                pCtx.getResources().getString(R.string.action_add_syllabus),
                                pCtx.getResources().getString(R.string.action_view_creator_profile),
                                pCtx.getResources().getString(R.string.action_report_spam));
                        pListIds = Arrays.asList(R.id.action_unpost_from_followers,
                                R.id.action_share,
                                R.id.action_remove,
                                R.id.action_delete_my_shares,
                                R.id.action_add_syllabus,
                                R.id.action_view_creator_profile,
                                R.id.action_report_spam);
                    }
                    break;
            }
            lRelObjArr = new Object[]{pList, pListIds};
        } else {
            lFormat = String.format("%s%s%s", creator, offlineable, postedTo);
            switch (lFormat) {
                case "102":
                case "112":
                    /*Because, lessons listed in public tab are always posted to followers and conections*/
//                case "110":
//                case "111"
//                case "100":
//                case "101":
                    pList = Arrays.asList(pCtx.getResources().getString(R.string.action_unpost_from_public),
                            pCtx.getResources().getString(R.string.action_share),
                            pCtx.getResources().getString(R.string.action_save),
                            pCtx.getResources().getString(R.string.action_view_creator_profile),
                            pCtx.getResources().getString(R.string.action_report_spam));
                    pListIds = Arrays.asList(R.id.action_unpost_from_public,
                            R.id.action_share,
                            R.id.action_save,
                            R.id.action_view_creator_profile,
                            R.id.action_report_spam);
                    break;
                case "010":
                    pList = Arrays.asList(pCtx.getResources().getString(R.string.action_post_to_followers_and_connections),
                            pCtx.getResources().getString(R.string.action_post_to_connections),
                            pCtx.getResources().getString(R.string.action_share),
                            pCtx.getResources().getString(R.string.action_save),
                            pCtx.getResources().getString(R.string.action_include_in_online_received_tab),
                            pCtx.getResources().getString(R.string.action_view_creator_profile),
                            pCtx.getResources().getString(R.string.action_report_spam));
                    pListIds = Arrays.asList(R.id.action_post_to_followers_and_connections,
                            R.id.action_post_to_connections,
                            R.id.action_share,
                            R.id.action_save,
                            R.id.action_include_in_online_received_tab,
                            R.id.action_view_creator_profile,
                            R.id.action_report_spam);
                    break;
                case "000":
                    pList = Arrays.asList(pCtx.getResources().getString(R.string.action_post_to_followers_and_connections),
                            pCtx.getResources().getString(R.string.action_post_to_connections),
                            pCtx.getResources().getString(R.string.action_share),
                            pCtx.getResources().getString(R.string.action_include_in_online_received_tab),
                            pCtx.getResources().getString(R.string.action_view_creator_profile),
                            pCtx.getResources().getString(R.string.action_report_spam));
                    pListIds = Arrays.asList(R.id.action_post_to_followers_and_connections,
                            R.id.action_post_to_connections,
                            R.id.action_share,
                            R.id.action_include_in_online_received_tab,
                            R.id.action_view_creator_profile,
                            R.id.action_report_spam);
                    break;
                case "011":
                    pList = Arrays.asList(pCtx.getResources().getString(R.string.action_extend_post_to_followers),
                            pCtx.getResources().getString(R.string.action_share),
                            pCtx.getResources().getString(R.string.action_save),
                            pCtx.getResources().getString(R.string.action_include_in_online_received_tab),
                            pCtx.getResources().getString(R.string.action_view_creator_profile),
                            pCtx.getResources().getString(R.string.action_report_spam));
                    pListIds = Arrays.asList(R.id.action_extend_post_to_followers,
                            R.id.action_share,
                            R.id.action_save,
                            R.id.action_include_in_online_received_tab,
                            R.id.action_view_creator_profile,
                            R.id.action_report_spam);
                    break;
                case "001":
                    pList = Arrays.asList(pCtx.getResources().getString(R.string.action_extend_post_to_followers),
                            pCtx.getResources().getString(R.string.action_share),
                            pCtx.getResources().getString(R.string.action_include_in_online_received_tab),
                            pCtx.getResources().getString(R.string.action_view_creator_profile),
                            pCtx.getResources().getString(R.string.action_report_spam));
                    pListIds = Arrays.asList(R.id.action_extend_post_to_followers,
                            R.id.action_share,
                            R.id.action_include_in_online_received_tab,
                            R.id.action_view_creator_profile,
                            R.id.action_report_spam);
                    break;
                case "012":
                    pList = Arrays.asList(pCtx.getResources().getString(R.string.action_share),
                            pCtx.getResources().getString(R.string.action_save),
                            pCtx.getResources().getString(R.string.action_include_in_online_received_tab),
                            pCtx.getResources().getString(R.string.action_view_creator_profile),
                            pCtx.getResources().getString(R.string.action_report_spam));
                    pListIds = Arrays.asList(R.id.action_share,
                            R.id.action_save,
                            R.id.action_include_in_online_received_tab,
                            R.id.action_view_creator_profile,
                            R.id.action_report_spam);
                    break;
                case "002":
                    pList = Arrays.asList(pCtx.getResources().getString(R.string.action_share),
                            pCtx.getResources().getString(R.string.action_include_in_online_received_tab),
                            pCtx.getResources().getString(R.string.action_view_creator_profile),
                            pCtx.getResources().getString(R.string.action_report_spam));
                    pListIds = Arrays.asList(R.id.action_share,
                            R.id.action_include_in_online_received_tab,
                            R.id.action_view_creator_profile,
                            R.id.action_report_spam);
                    break;
            }
            lRelObjArr = new Object[]{pList, pListIds};
        }
        return lRelObjArr;
    }

    public static Menu getLessonSettingsOptionsList(Context pCtx, Lessons pLessons,
                                                    LessonShares pLessonShares,
                                                    LessonViews pLessonViews,
                                                    String pLessFromWhereWchTab, Users pUsers, Menu menu) {
        menu.findItem(R.id.action_post_to_public).setVisible(false);
        menu.findItem(R.id.action_post_to_connections).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.action_delete_lesson).setVisible(false);
        menu.findItem(R.id.action_delete_all_shares).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_post_to_followers_and_connections).setVisible(false);
        menu.findItem(R.id.action_extend_post_to_public).setVisible(false);
        menu.findItem(R.id.action_extend_post_to_followers).setVisible(false);
        menu.findItem(R.id.action_unpost_from_public).setVisible(false);
        menu.findItem(R.id.action_unpost_from_connections).setVisible(false);
        menu.findItem(R.id.action_unpost_from_followers).setVisible(false);
        menu.findItem(R.id.action_add_syllabus).setVisible(false);
        menu.findItem(R.id.action_remove).setVisible(false);
        menu.findItem(R.id.action_delete_my_shares).setVisible(false);
        menu.findItem(R.id.action_include_in_online_received_tab).setVisible(false);
        menu.findItem(R.id.action_view_creator_profile).setVisible(false);
        menu.findItem(R.id.action_view_sharer_profile).setVisible(false);
        menu.findItem(R.id.action_report_spam).setVisible(false);

        String T = "1";
        String F = "0";
        String NO = "0";
        String CO = "1";
        String FO = "2";
        String creator, sharable, offlineable, postedTo, isSpecificSyll, showSharer;
        creator = pLessons.getOwner().getId().equals(pUsers.getId()) ? T : F;
        sharable = pLessons.getSharable() ? T : F;
        offlineable = pLessons.getOfflineable() ? T : F;
        isSpecificSyll = !pLessons.getChapter().getSyllabus().getIsGeneric() ? T : F;
        switch (pLessons.getPostedTo()) {
            case Constants.POSTED_NONE:
                postedTo = NO;
                break;
            case Constants.POSTED_CONNECTIONS:
                postedTo = CO;
                break;
            case Constants.POSTED_FOLLOWERS:
                postedTo = FO;
                break;
            default:
                postedTo = NO;
                break;
        }

        int llessonSource;
        switch (pLessFromWhereWchTab) {
            case PotMacros.OBJ_LESSON_MINE_TAB:
                llessonSource = pLessons.getOwner().getId();
                break;
            case PotMacros.OBJ_LESSON_RECEIVED_TAB:
                llessonSource = pLessonShares.getFromUser().getId();
                break;
            case PotMacros.OBJ_LESSON_VIEWED_TAB:
                llessonSource = pLessonViews.getSource().getId();
                break;
            default:
                llessonSource = pLessons.getOwner().getId();
                break;
        }
        showSharer = !pLessons.getOwner().getId().equals(llessonSource) ? T : F;

        String lFormat;
        if (!pLessFromWhereWchTab.equals(PotMacros.OBJ_LESSON_PUBLIC_TAB)) {
            lFormat = String.format("%s%s%s%s", creator, sharable, offlineable, postedTo);
            switch (lFormat) {
                case "1110":
                case "1010":
                case "1100":
                case "1000":
                    if (isSpecificSyll.equals(T))
                        menu.findItem(R.id.action_post_to_public).setVisible(true);
                    menu.findItem(R.id.action_post_to_connections).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_edit).setVisible(true);
                    menu.findItem(R.id.action_delete_lesson).setVisible(true);
                    menu.findItem(R.id.action_delete_all_shares).setVisible(true);
                    menu.findItem(R.id.action_save).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "1111":
                case "1101":
                case "1011":
                case "1001":
                    if (isSpecificSyll.equals(T))
                        menu.findItem(R.id.action_extend_post_to_public).setVisible(true);
                    menu.findItem(R.id.action_unpost_from_connections).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_edit).setVisible(true);
                    menu.findItem(R.id.action_delete_lesson).setVisible(true);
                    menu.findItem(R.id.action_delete_all_shares).setVisible(true);
                    menu.findItem(R.id.action_save).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "1112":
                case "1102":
                     /*Because creators public lesson must always be sharable*/
//                case "1012":
//                case "1002":
                    menu.findItem(R.id.action_unpost_from_public).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_edit).setVisible(true);
                    menu.findItem(R.id.action_delete_lesson).setVisible(true);
                    menu.findItem(R.id.action_delete_all_shares).setVisible(true);
                    menu.findItem(R.id.action_save).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "0110":
                    if (isSpecificSyll.equals(T))
                        menu.findItem(R.id.action_post_to_followers_and_connections).setVisible(true);
                    menu.findItem(R.id.action_post_to_connections).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_delete_my_shares).setVisible(true);
                    menu.findItem(R.id.action_save).setVisible(true);
                    menu.findItem(R.id.action_add_syllabus).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    if (showSharer.equals(T))
                        menu.findItem(R.id.action_view_sharer_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "0100":
                    if (isSpecificSyll.equals(T))
                        menu.findItem(R.id.action_post_to_followers_and_connections).setVisible(true);
                    menu.findItem(R.id.action_post_to_connections).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_delete_my_shares).setVisible(true);
                    menu.findItem(R.id.action_add_syllabus).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    if (showSharer.equals(T))
                        menu.findItem(R.id.action_view_sharer_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "0010":
                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_delete_my_shares).setVisible(true);
                    menu.findItem(R.id.action_save).setVisible(true);
                    menu.findItem(R.id.action_add_syllabus).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    if (showSharer.equals(T))
                        menu.findItem(R.id.action_view_sharer_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "0000":
                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_delete_my_shares).setVisible(true);
                    menu.findItem(R.id.action_add_syllabus).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    if (showSharer.equals(T))
                        menu.findItem(R.id.action_view_sharer_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "0111":
                    if (isSpecificSyll.equals(T))
                        menu.findItem(R.id.action_extend_post_to_followers).setVisible(true);
                    menu.findItem(R.id.action_unpost_from_connections).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_delete_my_shares).setVisible(true);
                    menu.findItem(R.id.action_save).setVisible(true);
                    menu.findItem(R.id.action_add_syllabus).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    if (showSharer.equals(T))
                        menu.findItem(R.id.action_view_sharer_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "0101":
                    if (isSpecificSyll.equals(T))
                        menu.findItem(R.id.action_extend_post_to_followers).setVisible(true);
                    menu.findItem(R.id.action_unpost_from_connections).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_delete_my_shares).setVisible(true);
                    menu.findItem(R.id.action_add_syllabus).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    if (showSharer.equals(T))
                        menu.findItem(R.id.action_view_sharer_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "0112":
                    menu.findItem(R.id.action_unpost_from_followers).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_delete_my_shares).setVisible(true);
                    menu.findItem(R.id.action_save).setVisible(true);
                    menu.findItem(R.id.action_add_syllabus).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    if (showSharer.equals(T))
                        menu.findItem(R.id.action_view_sharer_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "0102":
                    menu.findItem(R.id.action_unpost_from_followers).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_delete_my_shares).setVisible(true);
                    menu.findItem(R.id.action_add_syllabus).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    if (showSharer.equals(T))
                        menu.findItem(R.id.action_view_sharer_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
            }
        } else {
            lFormat = String.format("%s%s%s", creator, offlineable, postedTo);
            switch (lFormat) {
                case "102":
                case "112":
                    /*Because, lessons listed in public tab are always posted to followers and conections*/
//                case "110":
//                case "111"
//                case "100":
//                case "101":
                    menu.findItem(R.id.action_unpost_from_public).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_save).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "010":
                    menu.findItem(R.id.action_post_to_followers_and_connections).setVisible(true);
                    menu.findItem(R.id.action_post_to_connections).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_save).setVisible(true);
                    menu.findItem(R.id.action_include_in_online_received_tab).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "000":
                    menu.findItem(R.id.action_post_to_followers_and_connections).setVisible(true);
                    menu.findItem(R.id.action_post_to_connections).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_include_in_online_received_tab).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "011":
                    menu.findItem(R.id.action_extend_post_to_followers).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_save).setVisible(true);
                    menu.findItem(R.id.action_include_in_online_received_tab).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "001":
                    menu.findItem(R.id.action_extend_post_to_followers).setVisible(true);
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_include_in_online_received_tab).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "012":
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_save).setVisible(true);
                    menu.findItem(R.id.action_include_in_online_received_tab).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
                case "002":
                    menu.findItem(R.id.action_share).setVisible(true);
                    menu.findItem(R.id.action_include_in_online_received_tab).setVisible(true);
                    menu.findItem(R.id.action_view_creator_profile).setVisible(true);
                    menu.findItem(R.id.action_report_spam).setVisible(true);
                    break;
            }
        }
        return menu;
    }

    public static final String EMAIL_PATTERNS1 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";
    public static final String EMAIL_PATTERNS2 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public static boolean isEmailValid(String pEmail) {

        boolean lRetVal = false;
        if (pEmail.matches(EMAIL_PATTERNS1) || pEmail.matches(EMAIL_PATTERNS2)) {
            lRetVal = true;
        } else {
            lRetVal = false;
        }
        return lRetVal;
    }

    public static String getGUID() {
        return UUID.randomUUID().toString();
    }

    public static JSONObject parseJson(String pString) {
        JSONObject lRetVal = null;
        try {
            lRetVal = (JSONObject) new JSONTokener(pString).nextValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lRetVal;
    }

    public static Bitmap cropImage(Bitmap pBitMap) {
        pBitMap.setDensity(Bitmap.DENSITY_NONE);
        if (pBitMap.getWidth() >= pBitMap.getHeight()) {
            pBitMap = Bitmap.createBitmap(
                    pBitMap,
                    pBitMap.getWidth() / 2 - pBitMap.getHeight() / 2,
                    0,
                    pBitMap.getHeight(),
                    pBitMap.getHeight()
            );
        } else {
            pBitMap = Bitmap.createBitmap(
                    pBitMap,
                    0,
                    pBitMap.getHeight() / 2 - pBitMap.getWidth() / 2,
                    pBitMap.getWidth(),
                    pBitMap.getWidth()
            );
        }

        return pBitMap;
    }

    public static File getImageFilePath(Context pObjScreen) {
        File lObjMediaStorageDir = new File(getRootMediaFilePath(pObjScreen), "Images");
        if (!lObjMediaStorageDir.exists()) {
            lObjMediaStorageDir.mkdirs();
        }
        return lObjMediaStorageDir;
    }

    public static File getPdfFilePath(Context pObjScreen) {
        File lObjMediaStorageDir = new File(getRootMediaFilePath(pObjScreen), "Pdfs");
        if (!lObjMediaStorageDir.exists()) {
            lObjMediaStorageDir.mkdirs();
        }
        return lObjMediaStorageDir;
    }

    public static File getAudioFilePath(Context pObjScreen) {
        File lObjMediaStorageDir = new File(getRootMediaFilePath(pObjScreen), "Audio");
        if (!lObjMediaStorageDir.exists()) {
            lObjMediaStorageDir.mkdirs();
        }
        return lObjMediaStorageDir;
    }

    public static File getOfflineImageFilePath(Context pObjScreen) {
        File lObjMediaStorageDir = new File(getRootMediaFilePath(pObjScreen), "offlineimages");
        if (!lObjMediaStorageDir.exists()) {
            lObjMediaStorageDir.mkdirs();
        }
        return lObjMediaStorageDir;
    }

    public static File getOfflineAudioFilePath(Context pObjScreen) {
        File lObjMediaStorageDir = new File(getRootMediaFilePath(pObjScreen), "offlineaudio");
        if (!lObjMediaStorageDir.exists()) {
            lObjMediaStorageDir.mkdirs();
        }
        return lObjMediaStorageDir;
    }

    public static File getTempImageFilePath(Context pObjScreen) {
        File lObjMediaStorageDir = new File(getRootMediaFilePath(pObjScreen), "tempimages");
        if (!lObjMediaStorageDir.exists()) {
            lObjMediaStorageDir.mkdirs();
        }
        return lObjMediaStorageDir;
    }

    public static File getTempAudioFilePath(Context pObjScreen) {
        File lObjMediaStorageDir = new File(getRootMediaFilePath(pObjScreen), "tempaudio");
        if (!lObjMediaStorageDir.exists()) {
            lObjMediaStorageDir.mkdirs();
        }
        return lObjMediaStorageDir;
    }

    public static Bitmap scaleImage(Context pObjContext, Bitmap pBitMap) {
        Bitmap bitmap = pBitMap;
        // Get current dimensions AND the desired bounding box
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        DisplayMetrics metrics = pObjContext.getResources().getDisplayMetrics();
        int displayWidth = metrics.widthPixels;
        int displayHeight = metrics.heightPixels;

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float scale = 1;

        if (scale == 0) //(float)displayWidth >= width && (float)displayHeight >= height)
        {// No scaling
            scale = 1;
        } else {
            float xScale = ((float) displayWidth) / width;
            float yScale = ((float) displayHeight) / height;
            scale = (xScale <= yScale) ? xScale : yScale;
        }
        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // Create a new bitmap and convert it to a format understood by the ImageView
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return bitmap;

    }

    public static File getRootMediaFilePath(Context pObjScreen) {
        File lObjMediaStorageDir = pObjScreen.getExternalFilesDir(null);
        if (null == lObjMediaStorageDir) {
            lObjMediaStorageDir = pObjScreen.getFilesDir();
        }
        lObjMediaStorageDir = new File(lObjMediaStorageDir, "lessonpot");
        if (!lObjMediaStorageDir.exists()) {
            lObjMediaStorageDir.mkdirs();
        }
        return lObjMediaStorageDir;
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static boolean copyFile(String from, String to) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                File source = new File(from);
                File destination = new File(to);
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

    public static void saveFile(Context context, Bitmap b, String picName) {
        FileOutputStream fos;
        Bitmap lBitmap = loadBitmap(context, picName);
        try {
            if (lBitmap == null) {
                fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
                b.compress(Bitmap.CompressFormat.PNG, 60, fos);
                fos.close();
            }
        } catch (FileNotFoundException e) {
            Log.d(PotMacros.class.getName(), "file not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(PotMacros.class.getName(), "io exception");
            e.printStackTrace();
        }
    }

    public static Bitmap loadBitmap(Context context, String picName) {
        Bitmap b = null;
        FileInputStream fis;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis, null, options);
            fis.close();
        } catch (FileNotFoundException e) {
            Log.d(PotMacros.class.getName(), "file not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(PotMacros.class.getName(), "io exception");
            e.printStackTrace();
        }
        return b;
    }

    // Add an event to the calendar of the user.
    public static Long addEvent(Context context, String date, String pTitle) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        Date lDate = new Date();
        Long lEventId = 0L;
        try {
            lDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calDate = Calendar.getInstance();
        calDate.set(Calendar.DATE, lDate.getDate());
//        Calendar tmp = (Calendar) now.clone();
//        Calendar calDate = now;

        try {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, calDate.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, calDate.getTimeInMillis() + 15 * 60 * 1000);
            values.put(CalendarContract.Events.TITLE, pTitle);
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                    .getTimeZone().getID());
            System.out.println(Calendar.getInstance().getTimeZone().getID());
            /*Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // Save the eventId into the Task object for possible future delete.
            lEventId = Long.parseLong(uri.getLastPathSegment());
//            Long lEventId = pEvnetId;
            // Add a 5 minute, 1 hour and 1 day reminders (3 reminders)
            setReminder(context, cr, lEventId, 5);
//            setReminder(context, cr, lEventId, 7);
//            setReminder(context, cr, lEventId, 10);
            setReminder(context, cr, lEventId, 60);
            setReminder(context, cr, lEventId, 1440);*/

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Reminder not able to add", Toast.LENGTH_SHORT).show();
        }
        return lEventId;
    }

    // routine to add reminders with the event
    private static void setReminder(Context context, ContentResolver cr, long eventID, int timeBefore) {
        try {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, timeBefore);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
//            Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
            Cursor c = CalendarContract.Reminders.query(cr, eventID,
                    new String[]{CalendarContract.Reminders.MINUTES});
            if (c.moveToFirst()) {
                System.out.println("calendar"
                        + c.getInt(c.getColumnIndex(CalendarContract.Reminders.MINUTES)));
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Reminder not able to set", Toast.LENGTH_SHORT).show();
        }
    }

    // function to remove an event from the calendar using the eventId stored within the Task object.
    public static void removeEvent(Context context, Long lEventId) {
        try {
            ContentResolver cr = context.getContentResolver();
            int iNumRowsDeleted = 0;
            Uri eventsUri = Uri.parse("content://com.android.calendar/events");
            Uri eventUri = ContentUris.withAppendedId(eventsUri, lEventId);
            iNumRowsDeleted = cr.delete(eventUri, null, null);
            Log.i("REMOVE EVENT", "Deleted " + iNumRowsDeleted + " calendar entry.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int updateEvent(Context context, String date, String title, Long eventId) {
        int iNumRowsUpdated = 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            Date lDate = new Date();
            try {
                lDate = format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            GregorianCalendar calDate = new GregorianCalendar(lDate.getYear(), lDate.getMonth(), lDate.getDay(),
                    lDate.getHours(), lDate.getMinutes());

            ContentValues event = new ContentValues();

            event.put(CalendarContract.Events.TITLE, title);
            event.put("hasAlarm", 1); // 0 for false, 1 for true
            event.put(CalendarContract.Events.DTSTART, calDate.getTimeInMillis());
            event.put(CalendarContract.Events.DTEND, calDate.getTimeInMillis() + 15 * 60 * 1000);

            Uri eventsUri = Uri.parse("content://com.android.calendar/events");
            Uri eventUri = ContentUris.withAppendedId(eventsUri, eventId);

            iNumRowsUpdated = context.getContentResolver().update(eventUri, event, null, null);

            Log.i("UPDATE EVENT", "Updated " + iNumRowsUpdated + " calendar entry.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iNumRowsUpdated;
    }

}
