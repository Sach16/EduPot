package com.cosmicdew.lessonpot.interfaces;

import android.view.View;

import com.cosmicdew.lessonpot.models.Users;

/**
 * Created by S.K. Pissay on 30/12/16.
 */

public interface RefreshEditProfileListener {
    public void onUpdate(int pPostion, Users pUsers, View pView);
}
