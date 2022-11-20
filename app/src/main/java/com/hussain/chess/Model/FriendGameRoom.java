package com.hussain.chess.Model;

import java.util.Arrays;
import java.util.List;

public class FriendGameRoom extends GameRoom {

    private String name;
    private String password;


    public FriendGameRoom copy() {

        FriendGameRoom gameRoom = new FriendGameRoom();
        gameRoom.id = this.id;
        gameRoom.player1 = this.player1;
        gameRoom.player1Name = this.player1Name;
        gameRoom.player2 = this.player2;
        gameRoom.player2Name = this.player2Name;

        return gameRoom;


    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
