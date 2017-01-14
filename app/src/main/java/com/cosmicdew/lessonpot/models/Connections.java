package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 23/11/16.
 */

public class Connections {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("connection_from")
    @Expose
    private Users connectionFrom;
    @SerializedName("connection_to")
    @Expose
    private Users connectionTo;
    @SerializedName("is_approved")
    @Expose
    private Boolean isApproved;
    @SerializedName("is_spam")
    @Expose
    private Boolean isSpam;
    @SerializedName("blocking_user")
    @Expose
    private Users blockingUser;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;

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
     * The connectionFrom
     */
    public Users getConnectionFrom() {
        return connectionFrom;
    }

    /**
     *
     * @param connectionFrom
     * The connection_from
     */
    public void setConnectionFrom(Users connectionFrom) {
        this.connectionFrom = connectionFrom;
    }

    /**
     *
     * @return
     * The connectionTo
     */
    public Users getConnectionTo() {
        return connectionTo;
    }

    /**
     *
     * @param connectionTo
     * The connection_to
     */
    public void setConnectionTo(Users connectionTo) {
        this.connectionTo = connectionTo;
    }

    /**
     *
     * @return
     * The isApproved
     */
    public Boolean getIsApproved() {
        return isApproved;
    }

    /**
     *
     * @param isApproved
     * The is_approved
     */
    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    /**
     *
     * @return
     * The isSpam
     */
    public Boolean getIsSpam() {
        return isSpam;
    }

    /**
     *
     * @param isSpam
     * The is_spam
     */
    public void setIsSpam(Boolean isSpam) {
        this.isSpam = isSpam;
    }

    /**
     *
     * @return
     * The blockingUser
     */
    public Users getBlockingUser() {
        return blockingUser;
    }

    /**
     *
     * @param blockingUser
     * The blocking_user
     */
    public void setBlockingUser(Users blockingUser) {
        this.blockingUser = blockingUser;
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

}