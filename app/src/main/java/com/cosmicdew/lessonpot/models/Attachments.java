package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 6/11/16.
 */

public class Attachments {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("attachment_type")
    @Expose
    private String attachmentType;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    @SerializedName("slot")
    @Expose
    private Integer slot;
    @SerializedName("content_url")
    @Expose
    private Object contentUrl;
    @SerializedName("length")
    @Expose
    private Integer length;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("lesson")
    @Expose
    private Integer lesson;

    //
    private boolean isDeleted;

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
     * The attachmentType
     */
    public String getAttachmentType() {
        return attachmentType;
    }

    /**
     *
     * @param attachmentType
     * The attachment_type
     */
    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    /**
     *
     * @return
     * The attachment
     */
    public String getAttachment() {
        return attachment;
    }

    /**
     *
     * @param attachment
     * The attachment
     */
    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    /**
     *
     * @return
     * The slot
     */
    public Integer getSlot() {
        return slot;
    }

    /**
     *
     * @param slot
     * The slot
     */
    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    /**
     *
     * @return
     * The contentUrl
     */
    public Object getContentUrl() {
        return contentUrl;
    }

    /**
     *
     * @param contentUrl
     * The content_url
     */
    public void setContentUrl(Object contentUrl) {
        this.contentUrl = contentUrl;
    }

    /**
     *
     * @return
     * The length
     */
    public Integer getLength() {
        return length;
    }

    /**
     *
     * @param length
     * The length
     */
    public void setLength(Integer length) {
        this.length = length;
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
     * The lesson
     */
    public Integer getLesson() {
        return lesson;
    }

    /**
     *
     * @param lesson
     * The lesson
     */
    public void setLesson(Integer lesson) {
        this.lesson = lesson;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
