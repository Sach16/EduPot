package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 28/10/16.
 */

public class Chapters {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("lesson_count")
    @Expose
    private Integer lessonCount;
    /*@SerializedName("lesson_length")
    @Expose
    private LessonLength lessonLength;*/
    @SerializedName("lesson_length")
    @Expose
    private Integer lessonLength;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("is_generic")
    @Expose
    private Boolean isGeneric;
    @SerializedName("syllabus")
    @Expose
    private Syllabi syllabus;

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
     * The lessonCount
     */
    public Integer getLessonCount() {
        return lessonCount;
    }

    /**
     *
     * @param lessonCount
     * The lesson_count
     */
    public void setLessonCount(Integer lessonCount) {
        this.lessonCount = lessonCount;
    }

    /**
     *
     * @return
     * The lessonLength
     */
    public Integer getLessonLength() {
        return lessonLength;
    }

    /**
     *
     * @param lessonLength
     * The lesson_length
     */
    public void setLessonLength(Integer lessonLength) {
        this.lessonLength = lessonLength;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The isGeneric
     */
    public Boolean getIsGeneric() {
        return isGeneric;
    }

    /**
     *
     * @param isGeneric
     * The is_generic
     */
    public void setIsGeneric(Boolean isGeneric) {
        this.isGeneric = isGeneric;
    }

    /**
     *
     * @return
     * The syllabus
     */
    public Syllabi getSyllabus() {
        return syllabus;
    }

    /**
     *
     * @param syllabus
     * The syllabus
     */
    public void setSyllabus(Syllabi syllabus) {
        this.syllabus = syllabus;
    }

}
