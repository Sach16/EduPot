package com.cosmicdew.lessonpot.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S.K. Pissay on 21/12/16.
 */

public class NotifyDS {

    private Integer connectionRequests;

    private Integer connectionApproved;

    private Integer lessonShare;

    private List<Integer> connectionRequestsIds = new ArrayList<>();

    private List<Integer> lessonShareIds = new ArrayList<>();

    private Users users;

    public Integer getConnectionRequests() {
        return connectionRequests;
    }

    public void setConnectionRequests(Integer connectionRequests) {
        this.connectionRequests = connectionRequests;
    }

    public Integer getConnectionApproved() {
        return connectionApproved;
    }

    public void setConnectionApproved(Integer connectionApproved) {
        this.connectionApproved = connectionApproved;
    }

    public Integer getLessonShare() {
        return lessonShare;
    }

    public void setLessonShare(Integer lessonShare) {
        this.lessonShare = lessonShare;
    }

    public List<Integer> getConnectionRequestsIds() {
        return connectionRequestsIds;
    }

    public void setConnectionRequestsIds(List<Integer> connectionRequestsIds) {
        this.connectionRequestsIds = connectionRequestsIds;
    }

    public List<Integer> getLessonShareIds() {
        return lessonShareIds;
    }

    public void setLessonShareIds(List<Integer> lessonShareIds) {
        this.lessonShareIds = lessonShareIds;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}
