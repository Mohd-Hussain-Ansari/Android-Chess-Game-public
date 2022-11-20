package com.hussain.chess.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SavedGameDao {
    @Query("SELECT * FROM SavedGame")
    List<SavedGame> getAll();

  /*  @Query("SELECT * FROM EntityGame WHERE id IN (:userIds)")
    List<EntityGame> loadAllByIds(int[] userIds);*/

    @Query("SELECT * FROM SavedGame WHERE id = :id ")
    SavedGame getById(int id);

    @Insert
    void insertAll(SavedGame... games);

    @Query("Update SavedGame set gameObject=:gameObject where id=:id")
    void updateGameObject(int id, String gameObject);

    @Query("DELETE  FROM SavedGame WHERE id = :id ")
    void deleteById(int id);

    @Delete
    void delete(SavedGame game);
}

