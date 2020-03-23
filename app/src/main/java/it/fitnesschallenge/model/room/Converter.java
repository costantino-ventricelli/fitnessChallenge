package it.fitnesschallenge.model.room;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Converter {
    @TypeConverter
    public Date stringToDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    @TypeConverter
    public String dateToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    /**
     * Per convertire le liste presenti nelle classi entity abbiamo optato per la creazione di stringhe
     * JSON così al momento dello storing queste verranno memorizzate come stringhe, ma nel momento in
     * cui verranno lette dal DB esse verranno riconvertite il Liste di oggetti
     *
     * @param floats contiene la stringa JSON da convertire.
     * @return ritorna una Lista di oggetti di tipo Float
     */
    @TypeConverter
    public List<Float> floatArrayToList(String floats) {
        if (floats.isEmpty())
            return Collections.emptyList();
        else {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Float>>() {
            }.getType();
            return gson.fromJson(floats, listType);
        }
    }

    /**
     * Questo metodo converte da Lista a JSON string
     *
     * @param floatList Contiene la lista degli elementi da salvare
     * @return ritorna una stringa JSON.
     */
    @TypeConverter
    public String floatListToArray(List<Float> floatList) {
        Gson gson = new Gson();
        return gson.toJson(floatList);
    }

}
