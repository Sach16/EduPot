package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S.K. Pissay on 29/11/16.
 */

public class Roles {

    @SerializedName("roles")
    @Expose
    private List<Role> roles = new ArrayList<Role>();

    /**
     *
     * @return
     * The roles
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     *
     * @param roles
     * The roles
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

}
