package com.cosmicdew.lessonpot.interfaces;

import android.view.View;

import com.cosmicdew.lessonpot.models.Role;
import com.cosmicdew.lessonpot.models.Users;

/**
 * Created by S.K. Pissay on 11/1/17.
 */

public interface RecyclerFilterListener {

    public void onInfoClick(int groupPosition, int pPostion, Role pRole, Users pUsers, boolean pMode, View pView);

    public void onInfoLongClick(int groupPosition, int pPostion, Role pRole, Users pUsers, boolean pMode, View pView);

    public void onOptionClick(int pPostion, Users pUsers, int pOptionId, View pView);
}

