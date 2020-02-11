package it.fitnesschallenge.model.room;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converter {
    @TypeConverter
    public Date timestampToDate(Long date){
        return date == null ? null : new Date(date);
    }

    @TypeConverter
    public long dateToTimestamp(Date date){
        return date == null ? null : date.getTime();
    }
}
