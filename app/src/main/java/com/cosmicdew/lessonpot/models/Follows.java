package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 22/2/17.
 */

public class Follows {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("from_user")
    @Expose
    private Users fromUser;
    @SerializedName("to_user")
    @Expose
    private Users toUser;
    @SerializedName("time")
    @Expose
    private String time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Users getFromUser() {
        return fromUser;
    }

    public void setFromUser(Users fromUser) {
        this.fromUser = fromUser;
    }

    public Users getToUser() {
        return toUser;
    }

    public void setToUser(Users toUser) {
        this.toUser = toUser;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
