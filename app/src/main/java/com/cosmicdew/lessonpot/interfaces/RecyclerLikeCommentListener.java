package com.cosmicdew.lessonpot.interfaces;

import android.view.View;

import com.cosmicdew.lessonpot.models.Comments;
import com.cosmicdew.lessonpot.models.Likes;

/**
 * Created by S.K. Pissay on 16/2/17.
 */

public interface RecyclerLikeCommentListener {

    public void onInfoClick(int pPostion, Likes pLike, Comments pComments, boolean pMode, View pView);

    public void onInfoLongClick(int pPostion, Likes pLike, Comments pComments, boolean pMode, int pState, View pView);

    public void resetFragment(boolean pState);

}
