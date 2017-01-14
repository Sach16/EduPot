package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 1/12/16.
 */

public class LessonShares {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("lesson")
    @Expose
    private Lessons lesson;
    @SerializedName("from_user")
    @Expose
    private Users fromUser;
    @SerializedName("to_user")
    @Expose
    private Users toUser;
    @SerializedName("is_new")
    @Expose
    private Boolean isNew;
    @SerializedName("time")
    @Expose
    private String time;

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }
    /**
     *
     * @return
     * The lesson
     */
    public Lessons getLesson() {
        return lesson;
    }

    /**
     *
     * @param lesson
     * The lesson
     */
    public void setLesson(Lessons lesson) {
        this.lesson = lesson;
    }

    /**
     *
     * @return
     * The fromUser
     */
    public Users getFromUser() {
        return fromUser;
    }

    /**
     *
     * @param fromUser
     * The from_user
     */
    public void setFromUser(Users fromUser) {
        this.fromUser = fromUser;
    }

    /**
     *
     * @return
     * The toUser
     */
    public Users getToUser() {
        return toUser;
    }

    /**
     *
     * @param toUser
     * The to_user
     */
    public void setToUser(Users toUser) {
        this.toUser = toUser;
    }

    /**
     *
     * @return
     * The isNew
     */
    public Boolean getIsNew() {
        return isNew;
    }

    /**
     *
     * @param isNew
     * The is_new
     */
    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    /**
     *
     * @return
     * The time
     */
    public String getTime() {
        return time;
    }

    /**
     *
     * @param time
     * The time
     */
    public void setTime(String time) {
        this.time = time;
    }

}
