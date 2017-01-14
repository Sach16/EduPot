package com.cosmicdew.lessonpot.interfaces;

import android.view.View;

import com.cosmicdew.lessonpot.models.BoardChoices;


/**
 * Created by S.K. Pissay on 18/10/16.
 */

public interface RecyclerClassBoardsListener {
    public void onInfoClick(int pPostion, BoardChoices pBoardChoices, View pView);

    public void onInfoLongClick(int pPostion, BoardChoices pBoardChoices, View pView);

    public void onSelectionClicked(int pPostion, BoardChoices pBoardChoices, View pView);
}
