package com.group.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.websocket.Session;
import java.util.ArrayList;

public class Player {
    private String username;
    private Session session;
    private Boolean isOpen;
    private Long grid;

    public Long getGrid() {
        return grid;
    }

    public void setGrid(Long grid) {
        this.grid = grid;
    }

    private ArrayList<Integer> hand = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Boolean isOpen() {
        return session.isOpen();
    }

    public ArrayList<Integer> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Integer> hand) {
        this.hand = hand;
    }
}
