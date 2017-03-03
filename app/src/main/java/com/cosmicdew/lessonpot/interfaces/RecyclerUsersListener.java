package com.cosmicdew.lessonpot.interfaces;

import android.view.View;

import com.cosmicdew.lessonpot.models.Follows;
import com.cosmicdew.lessonpot.models.Users;


/**
 * Created by S.K. Pissay on 5/10/16.
 */

public interface RecyclerUsersListener {
    public void onInfoClick(int pPostion, Users pUsers, View pView);
    public void onInfoLongClick(int pPostion, Users pUsers, View pView);
    public void onOptionsClick(int pPostion, Follows pFollows, View pView, int pOpt);
}
