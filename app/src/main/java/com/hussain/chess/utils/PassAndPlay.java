package com.hussain.chess.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hussain.chess.Model.GameStatus;
import com.hussain.chess.Model.Piece;
import com.hussain.chess.database.AppDatabase;
import com.hussain.chess.database.SavedGame;
import com.hussain.chess.database.SavedGameDao;
import com.hussain.chess.database.TempSavedOfflineGame;
import com.hussain.chess.database.TempSavedOfflineGameDao;

public class PassAndPlay extends Game {
    //id if game is saved game
    private int saveGameId;

    long timerLimit;
    long blackTimer;
    long whiteTimer;
    boolean isShowAvailableMove;
    boolean isShowLastMove;
    boolean isSound;
    boolean isUndoRedo;

    boolean isChangeSettings;


    public boolean isChangeSettings() {
        return isChangeSettings;
    }

    public long getTimerLimit() {
        return timerLimit;
    }

    public int getSaveGameId() {
        return saveGameId;
    }

    public long getBlackTimer() {
        return blackTimer;
    }


    public long getWhiteTimer() {
        return whiteTimer;
    }

    public boolean isShowAvailableMove() {
        return isShowAvailableMove;
    }

    public boolean isShowLastMove() {
        return isShowLastMove;
    }

    public boolean isSound() {
        return isSound;
    }

    public boolean isUndoRedo() {
        return isUndoRedo;
    }


    public boolean isUserCanSaveGame() {
        // if user made minimum 3 move
        return gameStatus == GameStatus.ACTIVE && movesPlayed.size() > 2;
    }


    public void setSaveGameId(int saveGameId) {
        this.saveGameId = saveGameId;
    }


    public void saveGame(Context context, long timerLimit, long blackTimer, long whiteTimer,
                         boolean isShowAvailableMove, boolean isShowLastMove, boolean isSound, boolean isUndoRedo, boolean isChangeSettings) {

        this.timerLimit = timerLimit;
        this.blackTimer = blackTimer;
        this.whiteTimer = whiteTimer;
        this.isShowAvailableMove = isShowAvailableMove;
        this.isShowLastMove = isShowLastMove;
        this.isSound = isSound;
        this.isUndoRedo = isUndoRedo;
        this.isChangeSettings = isChangeSettings;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Piece.class, new InterfaceAdapter<Piece>());
        Gson gson = gsonBuilder.create();


        AppDatabase db = AppDatabase.getDbInstance(context);

        SavedGameDao savedGameDao = db.savedGameDao();
        SavedGame savedGame = new SavedGame();
        // removing back (undo) move of a game
        //movesPlayed.clear();
        // removing forward (redo) move of a game
        forwardMovesPlayed.clear();
        savedGame.gameObject = gson.toJson(this);
        if (saveGameId == 0) {

            savedGameDao.insertAll(savedGame);
        } else {
            savedGameDao.updateGameObject(saveGameId, savedGame.gameObject);
        }


    }

    public void tempSaveOfflineGame(Context context, long timerLimit, long blackTimer, long whiteTimer,
                                    boolean isShowAvailableMove, boolean isShowLastMove, boolean isSound, boolean isUndoRedo, boolean isChangeSettings) {

        this.timerLimit = timerLimit;
        this.blackTimer = blackTimer;
        this.whiteTimer = whiteTimer;
        this.isShowAvailableMove = isShowAvailableMove;
        this.isShowLastMove = isShowLastMove;
        this.isSound = isSound;
        this.isUndoRedo = isUndoRedo;
        this.isChangeSettings = isChangeSettings;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Piece.class, new InterfaceAdapter<Piece>());
        Gson gson = gsonBuilder.create();


        AppDatabase db = AppDatabase.getDbInstance(context);

        TempSavedOfflineGameDao tempSavedOfflineGameDao = db.tempSavedGameDao();
        TempSavedOfflineGame tempSavedOfflineGame = new TempSavedOfflineGame();
        // removing back (undo) move of a game
        //movesPlayed.clear();
        // removing forward (redo) move of a game
        forwardMovesPlayed.clear();
        tempSavedOfflineGame.id = 1;
        tempSavedOfflineGame.gameObject = gson.toJson(this);

        tempSavedOfflineGameDao.insertAll(tempSavedOfflineGame);

    }

    public void deleteSavedGame(Context context) {
        if (saveGameId != 0) {
            AppDatabase db = AppDatabase.getDbInstance(context);
            SavedGameDao savedGameDao = db.savedGameDao();
            savedGameDao.deleteById(saveGameId);
        }
    }
}
