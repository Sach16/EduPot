package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 18/10/16.
 */

//TODO : Note : later change this model to general response
public class BoardChoices {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("syllabus_count")
    @Expose
    private Integer syllabusCount;
    @SerializedName("chapter_count")
    @Expose
    private Integer chapterCount;
    @SerializedName("message_count")
    @Expose
    private Integer messageCount;
    @SerializedName("school_name")
    @Expose
    private String schoolName;
    @SerializedName("school_location")
    @Expose
    private String schoolLocation;
    @SerializedName("user")
    @Expose
    private Users user;
    @SerializedName("boardclass")
    @Expose
    private BoardClass boardclass;


    //old usage retained
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("is_generic")
    @Expose
    private Boolean isGeneric;
    @SerializedName("state")
    @Expose
    private String state;

    private Integer board;

    private String boardName;


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
     * The state
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    public void setState(String state) {
        this.state = state;
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

    public Integer getBoard() {
        return board;
    }

    public void setBoard(Integer board) {
        this.board = board;
    }


    /**
     *
     * @return
     * The syllabusCount
     */
    public Integer getSyllabusCount() {
        return syllabusCount;
    }

    /**
     *
     * @param syllabusCount
     * The syllabus_count
     */
    public void setSyllabusCount(Integer syllabusCount) {
        this.syllabusCount = syllabusCount;
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
     * The schoolName
     */
    public String getSchoolName() {
        return schoolName;
    }

    /**
     *
     * @param schoolName
     * The school_name
     */
    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    /**
     *
     * @return
     * The schoolLocation
     */
    public String getSchoolLocation() {
        return schoolLocation;
    }

    /**
     *
     * @param schoolLocation
     * The school_location
     */
    public void setSchoolLocation(String schoolLocation) {
        this.schoolLocation = schoolLocation;
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

    public Boolean getGeneric() {
        return isGeneric;
    }

    public void setGeneric(Boolean generic) {
        isGeneric = generic;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }
}

