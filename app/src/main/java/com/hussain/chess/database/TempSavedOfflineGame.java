package com.hussain.chess.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TempSavedOfflineGame {
    @PrimaryKey
    public int id;
    @NonNull
    public String gameObject;
}
