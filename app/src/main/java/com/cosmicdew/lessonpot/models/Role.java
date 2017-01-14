package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S.K. Pissay on 29/11/16.
 */

public class Role {

    @SerializedName("users")
    @Expose
    private List<UsersShare> usersShares = new ArrayList<UsersShare>();
    @SerializedName("connections")
    @Expose
    private List<Connections> connections = new ArrayList<Connections>();
    @SerializedName("name")
    @Expose
    private String name;

    private boolean isSelected;

    private Integer shares;

    /**
     *
     * @return
     * The connections
     */
    public List<Connections> getConnections() {
        return connections;
    }

    /**
     *
     * @param connections
     * The connections
     */
    public void setConnections(List<Connections> connections) {
        this.connections = connections;
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

    public List<UsersShare> getUsersShares() {
        return usersShares;
    }

    public void setUsersShares(List<UsersShare> usersShares) {
        this.usersShares = usersShares;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Integer getShares() {
        return shares;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }
}
