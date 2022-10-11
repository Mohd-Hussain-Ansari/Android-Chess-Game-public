package com.hussain.chess.Model;

public class User {

    private String id;


    private String name;

    private int lostCount;

    private int winCount;

    private int MatchCount;

    private int DrawCount;

    public User() {

    }

    public User(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLostCount() {
        return lostCount;
    }

    public void setLostCount(int lostCount) {
        this.lostCount = lostCount;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getMatchCount() {
        return MatchCount;
    }

    public void setMatchCount(int matchCount) {
        MatchCount = matchCount;
    }

    public int getDrawCount() {
        return DrawCount;
    }

    public void setDrawCount(int drawCount) {
        DrawCount = drawCount;
    }
}
