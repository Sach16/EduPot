package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 1/12/16.
 */

public class Length {


    @SerializedName("length__sum")
    @Expose
    private Integer lengthSum;

    /**
     *
     * @return
     * The lengthSum
     */
    public Integer getLengthSum() {
        return lengthSum;
    }

    /**
     *
     * @param lengthSum
     * The length__sum
     */
    public void setLengthSum(Integer lengthSum) {
        this.lengthSum = lengthSum;
    }

}
