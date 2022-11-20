package com.hussain.chess.Model;

public class Player {
    private final boolean whiteSide;

    private final boolean computer;
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Player(boolean whiteSide) {
        this.whiteSide = whiteSide;
        this.computer = false;
    }

    public Player(boolean whiteSide, boolean computer) {
        this.whiteSide = whiteSide;
        this.computer = computer;
    }

    public boolean isWhiteSide() {
        return this.whiteSide;
    }

    public boolean isComputer() {
        return computer;
    }


}
