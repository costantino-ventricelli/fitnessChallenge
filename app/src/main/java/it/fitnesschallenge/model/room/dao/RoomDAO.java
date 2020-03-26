package it.fitnesschallenge.model.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import it.fitnesschallenge.model.room.entity.Room;

@Dao
public interface RoomDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Room room);

    @Update
    void update(Room room);

    @Transaction
    @Query("SELECT * FROM room")
    LiveData<List<Room>> selectAllRooms();
}
