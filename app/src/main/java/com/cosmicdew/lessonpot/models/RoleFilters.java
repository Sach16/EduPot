package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S.K. Pissay on 11/1/17.
 */

public class RoleFilters {

    @SerializedName("roles")
    @Expose
    private List<RoleFilter> roleFilters = new ArrayList<RoleFilter>();

    /**
     *
     * @return
     * The roles
     */
    public List<RoleFilter> getRoleFilters() {
        return roleFilters;
    }

    /**
     *
     * @param roleFilters
     * The roles
     */
    public void setRoleFilters(List<RoleFilter> roleFilters) {
        this.roleFilters = roleFilters;
    }

}
