package com.cosmicdew.lessonpot.models;

/**
 * Created by S.K. Pissay on 28/9/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Devices {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_admin")
    @Expose
    private Boolean isAdmin;
    @SerializedName("is_registered")
    @Expose
    private Boolean isRegistered;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("pin")
    @Expose
    private String pin;

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
     * The isRegistered
     */
    public Boolean getIsRegistered() {
        return isRegistered;
    }

    /**
     *
     * @param isRegistered
     * The is_registered
     */
    public void setIsRegistered(Boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    /**
     *
     * @return
     * The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @param phone
     * The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     *
     * @return
     * The pin
     */
    public String getPin() {
        return pin;
    }

    /**
     *
     * @param pin
     * The pin
     */
    public void setPin(String pin) {
        this.pin = pin;
    }

}
