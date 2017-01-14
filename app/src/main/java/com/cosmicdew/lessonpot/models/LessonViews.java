package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 30/11/16.
 */

public class LessonViews {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user")
    @Expose
    private Users user;
    @SerializedName("lesson")
    @Expose
    private Lessons lesson;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("views")
    @Expose
    private Integer views;
    @SerializedName("source")
    @Expose
    private Users source;

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
     * The user
     */
    public Users getUser() {
        return user;
    }

    /**
     *
     * @param user
     * The user
     */
    public void setUser(Users user) {
        this.user = user;
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

    /**
     *
     * @return
     * The views
     */
    public Integer getViews() {
        return views;
    }

    /**
     *
     * @param views
     * The views
     */
    public void setViews(Integer views) {
        this.views = views;
    }

    /**
     *
     * @return
     * The source
     */
    public Users getSource() {
        return source;
    }

    /**
     *
     * @param source
     * The source
     */
    public void setSource(Users source) {
        this.source = source;
    }

}

