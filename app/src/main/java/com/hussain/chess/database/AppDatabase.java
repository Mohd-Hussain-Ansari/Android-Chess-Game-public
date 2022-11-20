package com.hussain.chess.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SavedGame.class, TempSavedOfflineGame.class, TempSavedComputerGame.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static final String databaseName = "ChessDatabase";
    private static AppDatabase INSTANCE;

    public abstract SavedGameDao savedGameDao();


    public abstract TempSavedOfflineGameDao tempSavedGameDao();

    public abstract TempSavedComputerGameDao tempSavedComputerGameDao();

    public static AppDatabase getDbInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, databaseName)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

}




