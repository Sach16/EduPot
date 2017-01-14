package com.cosmicdew.lessonpot.interfaces;

import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.Syllabi;

/**
 * Created by S.K. Pissay on 14/11/16.
 */

public interface RecyclerSelectionListener {
    public void onInfoClicked(int pPostion, BoardChoices pBoardChoices, Syllabi pSyllabi, Chapters pChapters, Lessons pLessons, String pFragID);
}
