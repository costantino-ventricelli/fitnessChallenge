package it.fitnesschallenge.model.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "room")
public class Room {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "room_id")
    private long roomId;
    @ColumnInfo(name = "room_name")
    private String roomName;
    @ColumnInfo(name = "room_members")
    private int roomMembers;

    public Room() {
        // necessario per deserializzazione FireBase
    }

    public Room(String roomName, int roomMembers) {
        this.roomName = roomName;
        this.roomMembers = roomMembers;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getRoomMembers() {
        return roomMembers;
    }

    public void setRoomMembers(int roomMembers) {
        this.roomMembers = roomMembers;
    }
}
