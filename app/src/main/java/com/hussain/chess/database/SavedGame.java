package com.hussain.chess.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
    public class SavedGame {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String gameObject;
}

