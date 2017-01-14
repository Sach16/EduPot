package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 28/10/16.
 */

public class LessonLength {

    @SerializedName("length__sum")
    @Expose
    private Object lengthSum;

    /**
     *
     * @return
     * The lengthSum
     */
    public Object getLengthSum() {
        return lengthSum;
    }

    /**
     *
     * @param lengthSum
     * The length__sum
     */
    public void setLengthSum(Object lengthSum) {
        this.lengthSum = lengthSum;
    }

}
