package com.cosmicdew.lessonpot.interfaces;

import android.support.v7.widget.RecyclerView;

import com.cosmicdew.lessonpot.models.Lessons;

import java.util.List;

/**
 * Created by S.K. Pissay on 18/10/16.
 */

/**
 * Listener for manual initiation of a drag.
 */
public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder, List<Lessons> pObjLessons);

}
