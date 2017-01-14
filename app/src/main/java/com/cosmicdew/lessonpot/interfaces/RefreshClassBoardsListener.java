package com.cosmicdew.lessonpot.interfaces;

import android.view.View;

import com.cosmicdew.lessonpot.models.BoardChoices;

/**
 * Created by S.K. Pissay on 21/10/16.
 */

public interface RefreshClassBoardsListener {
    public void onUpdate(int pPostion, BoardChoices pBoardChoices, View pView);
    public void resetFragment(boolean pState);
}
