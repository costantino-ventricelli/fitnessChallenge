package it.fitnesschallenge.model.room.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {

    private String idCode;
    private String roomName;
    private String roomCreator;

    public Room() {
        // necessario per deserializzazione FireBase
    }

    public Room(Parcel in) {
        idCode = in.readString();
        roomName = in.readString();
        roomCreator = in.readString();
    }

    public Room(String idCode, String roomName, String roomCreator) {
        this.idCode = idCode;
        this.roomName = roomName;
        this.roomCreator = roomCreator;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomCreator() {
        return roomCreator;
    }

    public void setRoomCreator(String roomCreator) {
        this.roomCreator = roomCreator;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idCode);
        dest.writeString(roomName);
        dest.writeString(roomCreator);
    }

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
