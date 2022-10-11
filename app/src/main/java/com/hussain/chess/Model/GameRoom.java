package com.hussain.chess.Model;

import java.util.Arrays;
import java.util.List;

public class GameRoom {

    String id;
    String player1;
    String player2 = "null";
    String player1Name;


    private String priorityMove = "null";

    String player2Name;
    // set 10 min timer
    private long player1Timer = 600000;
    private long player2Timer = 600000;
    private long timerStartTime;
    private GameStatus gameStatus;
    private String turn;

    private String game;

    private boolean isPlayer1White;
    private boolean isPlayer2White;

    public List<Boolean> isAskDraw = Arrays.asList(false, false);

    public List<Boolean> playerDraw = Arrays.asList(false, false);


    private String rematchBy;
    private boolean isRematchAccepted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getRematchBy() {
        return rematchBy;
    }

    public void setRematchBy(String rematchBy) {
        this.rematchBy = rematchBy;
    }


    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public boolean isPlayer1White() {
        return isPlayer1White;
    }

    public void setPlayer1White(boolean player1White) {
        isPlayer1White = player1White;
    }

    public boolean isPlayer2White() {
        return isPlayer2White;
    }

    public void setPlayer2White(boolean player2White) {
        isPlayer2White = player2White;
    }


    public long getPlayer1Timer() {
        return player1Timer;
    }

    public void setPlayer1Timer(long player1Timer) {
        this.player1Timer = player1Timer;
    }

    public long getPlayer2Timer() {
        return player2Timer;
    }

    public void setPlayer2Timer(long player2Timer) {
        this.player2Timer = player2Timer;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }


    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }


    public long getTimerStartTime() {
        return timerStartTime;
    }

    public void setTimerStartTime(long timerStartTime) {
        this.timerStartTime = timerStartTime;
    }


    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getPriorityMove() {
        return priorityMove;
    }

    public void setPriorityMove(String priorityMove) {
        this.priorityMove = priorityMove;
    }


    public GameRoom copy() {

        GameRoom gameRoom = new GameRoom();
        gameRoom.id = this.id;
        gameRoom.player1 = this.player1;
        gameRoom.player1Name = this.player1Name;
        gameRoom.player2 = this.player2;
        gameRoom.player2Name = this.player2Name;

        return gameRoom;


    }

    public boolean isRematchAccepted() {
        return isRematchAccepted;
    }

    public void setRematchAccepted(boolean rematchAccepted) {
        isRematchAccepted = rematchAccepted;
    }
}
