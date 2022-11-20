package com.hussain.chess.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


@Dao
public interface TempSavedOfflineGameDao {
    /*  @Query("SELECT * FROM TempSavedOfflineGame")
    List<TempSavedOfflineGame> getAll();

  @Query("SELECT * FROM EntityGame WHERE id IN (:userIds)")
    List<EntityGame> loadAllByIds(int[] userIds);*/

    @Query("SELECT * FROM TempSavedOfflineGame WHERE id = :id ")
    TempSavedOfflineGame getById(int id);

    @Insert
    void insertAll(TempSavedOfflineGame... games);

    /*@Query("Update TempSavedOfflineGame set gameObject=:gameObject where id=:id" )
    void updateGameObject(int id,String gameObject);*/

    @Query("DELETE  FROM TempSavedOfflineGame WHERE id = :id ")
    void deleteById(int id);

    @Delete
    void delete(TempSavedOfflineGame game);
}
