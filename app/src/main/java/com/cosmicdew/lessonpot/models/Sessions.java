package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 29/9/16.
 */

public class Sessions {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("user_token")
    @Expose
    private String userToken;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("device")
    @Expose
    private Integer device;

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
     * The deviceToken
     */
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     *
     * @param deviceToken
     * The device_token
     */
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    /**
     *
     * @return
     * The userToken
     */
    public String getUserToken() {
        return userToken;
    }

    /**
     *
     * @param userToken
     * The user_token
     */
    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    /**
     *
     * @return
     * The start
     */
    public String getStart() {
        return start;
    }

    /**
     *
     * @param start
     * The start
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     *
     * @return
     * The end
     */
    public String getEnd() {
        return end;
    }

    /**
     *
     * @param end
     * The end
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     *
     * @return
     * The device
     */
    public Integer getDevice() {
        return device;
    }

    /**
     *
     * @param device
     * The device
     */
    public void setDevice(Integer device) {
        this.device = device;
    }

}
