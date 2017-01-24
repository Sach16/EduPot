package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by S.K. Pissay on 17/1/17.
 */

public class AppConfig {

    @SerializedName("highlights")
    @Expose
    private List<String> highlights = null;
    @SerializedName("last_supported_version")
    @Expose
    private String lastSupportedVersion;
    @SerializedName("latest_version")
    @Expose
    private String latestVersion;

    public List<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<String> highlights) {
        this.highlights = highlights;
    }

    public String getLastSupportedVersion() {
        return lastSupportedVersion;
    }

    public void setLastSupportedVersion(String lastSupportedVersion) {
        this.lastSupportedVersion = lastSupportedVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

}
