package com.cosmicdew.lessonpot.interfaces;

import android.view.View;

import com.cosmicdew.lessonpot.models.Connections;
import com.cosmicdew.lessonpot.models.Role;

/**
 * Created by S.K. Pissay on 23/11/16.
 */

public interface RecyclerNetworkListener {

    public void onInfoClick(int pPostion, Role pRole, Connections pConnections, boolean pMode, View pView);

    public void onInfoLongClick(int pid, int pPostion, Role pRole, Connections pConnections, boolean pMode, View pView);

    public void onOptionClick(int pPostion, Connections pConnections, int pOptionId, View pView);
}
