package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by S.K. Pissay on 29/1/17.
 */

public class UsersTable extends SugarRecord{

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("middle_name")
    @Expose
    private String middleName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("terms_accepted")
    @Expose
    private Boolean termsAccepted;
    @SerializedName("is_admin")
    @Expose
    private Boolean isAdmin;

    public UsersTable(){}

    public UsersTable(Integer userId, String firstName, String middleName, String lastName, String username,
                      String role, Boolean termsAccepted, Boolean isAdmin) {
        this.userId = userId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.username = username;
        this.role = role;
        this.termsAccepted = termsAccepted;
        this.isAdmin = isAdmin;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     *
     * @return
     * The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     * The first_name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     * The middleName
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     *
     * @param middleName
     * The middle_name
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     *
     * @return
     * The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     * The last_name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The role
     */
    public String getRole() {
        return role;
    }

    /**
     *
     * @param role
     * The role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     *
     * @return
     * The isAdmin
     */
    public Boolean getIsAdmin() {
        return isAdmin;
    }

    /**
     *
     * @param isAdmin
     * The is_admin
     */
    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     *
     * @return
     * The termsAccepted
     */
    public Boolean getTermsAccepted() {
        return termsAccepted;
    }

    /**
     *
     * @param termsAccepted
     * The terms_accepted
     */
    public void setTermsAccepted(Boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }

}