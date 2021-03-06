package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 28/10/16.
 */

public class Lessons {

    @SerializedName("id")
    @Expose
    private Integer id;
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
    @SerializedName("owner")
    @Expose
    private Users owner;
    @SerializedName("chapter")
    @Expose
    private Chapters chapter;
    @SerializedName("length")
    @Expose
    private Length length;
    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("views")
    @Expose
    private Integer views;
    @SerializedName("is_spam")
    @Expose
    private Boolean isSpam;
    @SerializedName("offlineable")
    @Expose
    private Boolean offlineable;
    @SerializedName("sharable")
    @Expose
    private Boolean sharable;
    @SerializedName("likes_count")
    @Expose
    private Integer likesCount;
    @SerializedName("comments_count")
    @Expose
    private Integer commentsCount;
    @SerializedName("posted_to")
    @Expose
    private String postedTo;

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
     * The created
     */
    public String getCreated() {
        return created;
    }

    /**
     *
     * @param created
     * The created
     */
    public void setCreated(String created) {
        this.created = created;
    }

    /**
     *
     * @return
     * The modified
     */
    public String getModified() {
        return modified;
    }

    /**
     *
     * @param modified
     * The modified
     */
    public void setModified(String modified) {
        this.modified = modified;
    }

    /**
     *
     * @return
     * The owner
     */
    public Users getOwner() {
        return owner;
    }

    /**
     *
     * @param owner
     * The owner
     */
    public void setOwner(Users owner) {
        this.owner = owner;
    }

    /**
     *
     * @return
     * The chapter
     */
    public Chapters getChapter() {
        return chapter;
    }

    /**
     *
     * @param chapter
     * The chapter
     */
    public void setChapter(Chapters chapter) {
        this.chapter = chapter;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     *
     * @return
     * The length
     */
    public Length getLength() {
        return length;
    }

    /**
     *
     * @param length
     * The length
     */
    public void setLength(Length length) {
        this.length = length;
    }

    /**
     *
     * @return
     * The position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     *
     * @param position
     * The position
     */
    public void setPosition(Integer position) {
        this.position = position;
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

    public Boolean getIsSpam() {
        return isSpam;
    }

    public void setIsSpam(Boolean isSpam) {
        this.isSpam = isSpam;
    }

    public Boolean getOfflineable() {
        return offlineable;
    }

    public void setOfflineable(Boolean offlineable) {
        this.offlineable = offlineable;
    }

    public Boolean getSharable() {
        return sharable;
    }

    public void setSharable(Boolean sharable) {
        this.sharable = sharable;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getPostedTo() {
        return postedTo;
    }

    public void setPostedTo(String postedTo) {
        this.postedTo = postedTo;
    }
}