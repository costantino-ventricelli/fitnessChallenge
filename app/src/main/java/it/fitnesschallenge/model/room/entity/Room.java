package it.fitnesschallenge.model.room.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "room")
public class Room implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "room_id")
    private long roomId;
    @ColumnInfo(name = "room_name")
    private String roomName;
    @ColumnInfo(name = "room_members")
    private int roomMembers;

    @Ignore
    public Room() {
        // necessario per deserializzazione FireBase
    }

    @Ignore
    public Room(Parcel in) {
        roomId = in.readLong();
        roomName = in.readString();
        roomMembers = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(roomId);
        dest.writeString(roomName);
        dest.writeInt(roomMembers);
    }

    @Ignore
    public static final Parcelable.Creator<Room> CREATOR
            = new Parcelable.Creator<Room>() {
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        public Room[] newArray(int size) {
            return new Room[size];
        }
    };
}
