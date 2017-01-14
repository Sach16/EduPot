package com.cosmicdew.lessonpot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by S.K. Pissay on 28/10/16.
 */

public class Board {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("is_generic")
    @Expose
    private Boolean isGeneric;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("board")
    @Expose
    private Integer board;


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

    /**
     *
     * @return
     * The isGeneric
     */
    public Boolean getIsGeneric() {
        return isGeneric;
    }

    /**
     *
     * @param isGeneric
     * The is_generic
     */
    public void setIsGeneric(Boolean isGeneric) {
        this.isGeneric = isGeneric;
    }

    /**
     *
     * @return
     * The state
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return
     * The board
     */
    public Integer getBoard() {
        return board;
    }

    /**
     *
     * @param board
     * The board
     */
    public void setBoard(Integer board) {
        this.board = board;
    }

}
