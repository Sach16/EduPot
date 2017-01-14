package com.cosmicdew.lessonpot.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.activities.PotUserHomeScreen;
import com.cosmicdew.lessonpot.activities.SplashScreen;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Connections;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by S.K. Pissay on 12/12/16.
 */

public class PotFirebaseMessagingService extends FirebaseMessagingService{

    private Connections m_cConnections;
    private LessonShares m_cLessonShares;
    private Users m_cUsers;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        /*Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Title: " + remoteMessage.getNotification().getTitle());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());*/
        Log.d(TAG, "Notification Message Data: " + remoteMessage.getData().toString());

        //Calling method to generate notification
        sendNotification(/*remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody(),*/
                remoteMessage.getData());
    }

    private void sendNotification(/*String title, String body, */Map<String, String> pMap) {
        Intent lObjIntent = null;
        String lTag = null;
        String lStrObj = null;
            lStrObj = pMap.get(Constants.OBJECT);
            lTag = pMap.get(Constants.TAG);
        int notificationId = (int) (System.currentTimeMillis() % 1000);
        switch (lTag){
            case Constants.CONNECTION_REQUEST:
                lObjIntent = new Intent(this, PotUserHomeScreen.class);
                m_cConnections = (new Gson()).fromJson(lStrObj, Connections.class);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cConnections.getConnectionTo()));
                lObjIntent.putExtra(PotMacros.NOTIFICATION, Constants.CONNECTION_REQUEST);
                PotMacros.setNotifyCount(this, Constants.CONNECTION_REQUEST, notificationId, m_cConnections.getConnectionTo());
                break;
            case Constants.CONNECTION_APPROVED:
                lObjIntent = new Intent(this, PotUserHomeScreen.class);
                m_cConnections = (new Gson()).fromJson(lStrObj, Connections.class);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cConnections.getConnectionFrom()));
                lObjIntent.putExtra(PotMacros.NOTIFICATION, Constants.CONNECTION_APPROVED);
//                PotMacros.setNotifyCount(this, Constants.CONNECTION_APPROVED, m_cConnections.getConnectionTo());
                break;
            case Constants.LESSON_SHARE:
                lObjIntent = new Intent(this, PotUserHomeScreen.class);
                m_cLessonShares = (new Gson()).fromJson(lStrObj, LessonShares.class);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cLessonShares.getToUser()));
                lObjIntent.putExtra(PotMacros.NOTIFICATION, Constants.LESSON_SHARE);
                PotMacros.setNotifyCount(this, Constants.LESSON_SHARE, notificationId, m_cLessonShares.getToUser());
                break;
            case Constants.LESSON_SHARES_FROM_CONNECTION:
            case Constants.LESSON_SHARES_FROM_SYLLABUS:
                lObjIntent = new Intent(this, PotUserHomeScreen.class);
                m_cUsers = (new Gson()).fromJson(lStrObj, Users.class);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUsers));
                lObjIntent.putExtra(PotMacros.NOTIFICATION, lTag.equals(Constants.LESSON_SHARES_FROM_CONNECTION) ?
                        Constants.LESSON_SHARES_FROM_CONNECTION : Constants.LESSON_SHARES_FROM_SYLLABUS);
                break;
            default:
                lObjIntent = new Intent(this, SplashScreen.class);
                break;
        }
//        lObjIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        lObjIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        int REQUEST_CODE_BASE = 1000;
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, lObjIntent,
//                PendingIntent.FLAG_ONE_SHOT);
        lObjIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 1000 + (int) (System.currentTimeMillis()%1000);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, lObjIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_notify)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(pMap.get(Constants.TITLE))
                .setContentText(pMap.get(Constants.BODY))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(pMap.get(Constants.BODY)))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notificationBuilder.build());
        ShortcutBadger.applyCount(this, PotMacros.getNotifyAll(this)[0]);
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        Intent i = new Intent(PotMacros.REFRESH_NOTIFY_CONSTANT);
        lbm.sendBroadcast(i);
    }
}
