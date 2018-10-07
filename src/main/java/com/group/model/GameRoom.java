package com.group.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

public class GameRoom {
    // 主键
    private Long id;
    // 信息类型 0:匹配中 1:对局 2新消息
    private Integer status;
    // 开局人数
    private Integer startNumber;
    // 玩家
    private ArrayList<Player> players = new ArrayList<>();
    // 桌牌
    private ArrayList<Integer> desk = new ArrayList<>();
    // 声明
    private Integer state;
    // 排池
    private ArrayList<Integer> pool = new ArrayList<>();
    // 对话
    private ArrayList<String> talk = new ArrayList<>();
    // 操作人
    private String current;
    // 操作记录
    private ArrayList<Operate> operates = new ArrayList<>();

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @JsonIgnore
    public ArrayList<Operate> getOperates() {
        return operates;
    }

    public void setOperates(ArrayList<Operate> operates) {
        this.operates = operates;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public ArrayList<String> getTalk() {
        return talk;
    }

    public void setTalk(ArrayList<String> talk) {
        this.talk = talk;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(Integer startNumber) {
        this.startNumber = startNumber;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Integer> getDesk() {
        return desk;
    }

    public void setDesk(ArrayList<Integer> desk) {
        this.desk = desk;
    }

    public ArrayList<Integer> getPool() {
        return pool;
    }

    public void setPool(ArrayList<Integer> pool) {
        this.pool = pool;
    }
}