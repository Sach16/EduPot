package com.cosmicdew.lessonpot.interfaces;

import android.view.View;

import com.cosmicdew.lessonpot.models.Role;
import com.cosmicdew.lessonpot.models.UsersShare;

/**
 * Created by S.K. Pissay on 6/12/16.
 */

public interface RecyclerShareListener {

    public void onInfoClick(int groupPosition, int pPostion, Role pRole, UsersShare pUsersShare, boolean pMode, View pView);

    public void onInfoLongClick(int groupPosition, int pPostion, Role pRole, UsersShare pUsersShare, boolean pMode, View pView);

    public void onOptionClick(int pPostion, UsersShare pUsersShare, int pOptionId, View pView);
}
