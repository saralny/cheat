package com.group.dto;

import com.group.model.GameRoom;
import com.group.model.Player;

import java.util.ArrayList;

public class FrontMessage {
    // -1匹配 0出牌, 1揭牌, 2过 3会话 4匹配
    private Integer type;
    // 玩家姓名
    private String username;
    // 玩家
    private Player player;
    // 对话
    private String talk;
    // 出牌
    private ArrayList<Integer> play = new ArrayList<>();
    // 声明
    private Integer state;
    // 开始人数
    private Integer startNumber;
    // 房间id
    private Long grid;
    // 房间
    private GameRoom gr;

    public Long getGrid() {
        return grid;
    }

    public void setGrid(Long grid) {
        this.grid = grid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(Integer startNumber) {
        this.startNumber = startNumber;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public GameRoom getGr() {
        return gr;
    }

    public void setGr(GameRoom gr) {
        this.gr = gr;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }



    public String getTalk() {
        return talk;
    }

    public void setTalk(String talk) {
        this.talk = talk;
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
