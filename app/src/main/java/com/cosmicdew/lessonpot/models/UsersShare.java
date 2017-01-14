package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 6/12/16.
 */

public class UsersShare {

    @SerializedName("user")
    @Expose
    private Users users;
    @SerializedName("shares")
    @Expose
    private Integer shares;

    private boolean isSelected;

    /**
     *
     * @return
     * The users
     */
    public Users getUsers() {
        return users;
    }

    /**
     *
     * @param users
     * The users
     */
    public void setUsers(Users users) {
        this.users = users;
    }

    /**
     *
     * @return
     * The shares
     */
    public Integer getShares() {
        return shares;
    }

    /**
     *
     * @param shares
     * The shares
     */
    public void setShares(Integer shares) {
        this.shares = shares;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
