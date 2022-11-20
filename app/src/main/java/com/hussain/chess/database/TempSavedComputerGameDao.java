package com.hussain.chess.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


@Dao
public interface TempSavedComputerGameDao {
      /*
    @Query("SELECT * FROM TempSavedComputerGame")
    List<TempSavedComputerGame> getAll();

 @Query("SELECT * FROM EntityGame WHERE id IN (:userIds)")
    List<EntityGame> loadAllByIds(int[] userIds);*/

    @Query("SELECT * FROM TempSavedComputerGame WHERE id = :id ")
    TempSavedComputerGame getById(int id);

    @Insert
    void insertAll(TempSavedComputerGame... games);

    @Query("Update TempSavedComputerGame set gameObject=:gameObject where id=:id")
    void updateGameObject(int id, String gameObject);

    @Query("DELETE  FROM TempSavedComputerGame WHERE id = :id ")
    void deleteById(int id);

    @Delete
    void delete(TempSavedComputerGame game);
}
