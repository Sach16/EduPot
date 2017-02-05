package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S.K. Pissay on 5/10/16.
 */

public class Users{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("created")
    @Expose
    private String created;
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
    @SerializedName("role_title")
    @Expose
    private String roleTitle;
    @SerializedName("terms_accepted")
    @Expose
    private Boolean termsAccepted;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("is_admin")
    @Expose
    private Boolean isAdmin;
    @SerializedName("connection_type")
    @Expose
    private Integer connectionType;
    @SerializedName("list_in_connections")
    @Expose
    private Boolean listInConnections;
    @SerializedName("list_in_shares")
    @Expose
    private Boolean listInShares;

    //TODO : NOTE : uncomment when required
    @SerializedName("devices")
    @Expose
    private List<Devices> devices = new ArrayList<Devices>();
    @SerializedName("boardclasses")
    @Expose
    private List<BoardClass> boardclasses = new ArrayList<BoardClass>();
    @SerializedName("syllabi")
    @Expose
    private List<Syllabi> syllabi = new ArrayList<Syllabi>();

    private boolean isSelected;

    public Users(){}

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
     * The roleTitle
     */
    public String getRoleTitle() {
        return roleTitle;
    }

    /**
     *
     * @param roleTitle
     * The role_title
     */
    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    /**
     *
     * @return
     * The profilePic
     */
    public String getProfilePic() {
        return profilePic;
    }

    /**
     *
     * @param profilePic
     * The profile_pic
     */
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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

    /**
     *
     * @return
     * The connectionType
     */
    public Integer getConnectionType() {
        return connectionType;
    }

    /**
     *
     * @param connectionType
     * The connection_type
     */
    public void setConnectionType(Integer connectionType) {
        this.connectionType = connectionType;
    }

    /**
     *
     * @return
     * The listInConnections
     */
    public Boolean getListInConnections() {
        return listInConnections;
    }

    /**
     *
     * @param listInConnections
     * The list_in_connections
     */
    public void setListInConnections(Boolean listInConnections) {
        this.listInConnections = listInConnections;
    }

    /**
     *
     * @return
     * The listInShares
     */
    public Boolean getListInShares() {
        return listInShares;
    }

    /**
     *
     * @param listInShares
     * The list_in_shares
     */
    public void setListInShares(Boolean listInShares) {
        this.listInShares = listInShares;
    }

    public List<Devices> getDevices() {
        return devices;
    }

    public void setDevices(List<Devices> devices) {
        this.devices = devices;
    }

    public List<BoardClass> getBoardclasses() {
        return boardclasses;
    }

    public void setBoardclasses(List<BoardClass> boardclasses) {
        this.boardclasses = boardclasses;
    }

    public List<Syllabi> getSyllabi() {
        return syllabi;
    }

    public void setSyllabi(List<Syllabi> syllabi) {
        this.syllabi = syllabi;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

