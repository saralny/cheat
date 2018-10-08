package com.group.model;

import java.util.ArrayList;

public class Operate {
    // 0出牌, 1揭牌, 2过
    private Integer type;
    private String username;
    private ArrayList<Integer> play = new ArrayList<>();
    private Integer state;
    private Long grid;

    public Long getGrid() {
        return grid;
    }

    public void setGrid(Long grid) {
        this.grid = grid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Integer> getPlay() {
        return play;
    }

    public void setPlay(ArrayList<Integer> play) {
        this.play = play;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
