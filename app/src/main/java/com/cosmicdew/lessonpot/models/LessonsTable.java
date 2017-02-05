package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by S.K. Pissay on 30/1/17.
 */

public class LessonsTable extends SugarRecord{

    @SerializedName("lesson_id")
    @Expose
    private Integer lessonId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("owner_id")
    @Expose
    private Integer ownerId;
    @SerializedName("sharer_id")
    @Expose
    private Integer sharerId;
    @SerializedName("chapter_name")
    @Expose
    private String chapterName;
    @SerializedName("syllabi_name")
    @Expose
    private String syllabiName;
    @SerializedName("board_class")
    @Expose
    private String boardClass;
    @SerializedName("img_1")
    @Expose
    private String img1;
    @SerializedName("img_2")
    @Expose
    private String img2;
    @SerializedName("img_3")
    @Expose
    private String img3;
    @SerializedName("audio")
    @Expose
    private String audio;
    @SerializedName("length_sum")
    @Expose
    private Integer lengthSum;
    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("views")
    @Expose
    private Integer views;

    public LessonsTable(){}

    public LessonsTable(Integer lessonId, String name, String comments, String created, String modified, Integer userId,
                        Integer ownerId, Integer sharerId, String chapterName, String syllabiName, String boardClass, String img1,
                        String img2, String img3, String audio, Integer lengthSum, Integer position, Integer views){
        this.lessonId = lessonId;
        this.name = name;
        this.comments = comments;
        this.created = created;
        this.modified = modified;
        this.userId = userId;
        this.ownerId = ownerId;
        this.sharerId = sharerId;
        this.chapterName = chapterName;
        this.syllabiName = syllabiName;
        this.boardClass = boardClass;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.audio = audio;
        this.lengthSum = lengthSum;
        this.position = position;
        this.views = views;
    }

    public Integer getLessonId() {
        return lessonId;
    }

    public void setLessonId(Integer lessonId) {
        this.lessonId = lessonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getSharerId() {
        return sharerId;
    }

    public void setSharerId(Integer sharerId) {
        this.sharerId = sharerId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getSyllabiName() {
        return syllabiName;
    }

    public void setSyllabiName(String syllabiName) {
        this.syllabiName = syllabiName;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Integer getLengthSum() {
        return lengthSum;
    }

    public void setLengthSum(Integer lengthSum) {
        this.lengthSum = lengthSum;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public String getBoardClass() {
        return boardClass;
    }

    public void setBoardClass(String boardClass) {
        this.boardClass = boardClass;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
