package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 28/10/16.
 */

public class Syllabi {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("distinguisher")
    @Expose
    private String distinguisher;
    @SerializedName("is_generic")
    @Expose
    private Boolean isGeneric;
    @SerializedName("chapter_count")
    @Expose
    private Integer chapterCount;
    @SerializedName("message_count")
    @Expose
    private Integer messageCount;
    @SerializedName("lesson_count")
    @Expose
    private Integer lessonCount;
    @SerializedName("boardclass")
    @Expose
    private BoardClass boardclass;


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
     * The name
     */
    public String getName() {
        return name + " " + getDistinguisher();
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
     * The boardclass
     */
    public BoardClass getBoardclass() {
        return boardclass;
    }

    /**
     *
     * @param boardclass
     * The boardclass
     */
    public void setBoardclass(BoardClass boardclass) {
        this.boardclass = boardclass;
    }

    /**
     *
     * @return
     * The chapterCount
     */
    public Integer getChapterCount() {
        return chapterCount;
    }

    /**
     *
     * @param chapterCount
     * The chapter_count
     */
    public void setChapterCount(Integer chapterCount) {
        this.chapterCount = chapterCount;
    }

    /**
     *
     * @return
     * The messageCount
     */
    public Integer getMessageCount() {
        return messageCount;
    }

    /**
     *
     * @param messageCount
     * The message_count
     */
    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
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

    public String getDistinguisher() {
        return distinguisher != null ? distinguisher : "";
    }

    public void setDistinguisher(String distinguisher) {
        this.distinguisher = distinguisher;
    }

    public String getSubjectName() {
        return name;
    }

}