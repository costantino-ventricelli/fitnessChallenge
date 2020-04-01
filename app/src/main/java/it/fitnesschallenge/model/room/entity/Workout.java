/**
 * Questa classe definisce l'entità workout, in uno schema E-R sarebbe la relazione alla quale si
 * fa riferimento per accedere ad una serie di dati ad essa collegata
 */
package it.fitnesschallenge.model.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "workout")
public class Workout {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "workout_id")
    private int workOutId;
    @ColumnInfo(name = "is_active")
    private boolean isActive;
    @ColumnInfo(name = "start_date")
    private Date startDate;
    @ColumnInfo(name = "end_date")
    private Date endDate;
    @Ignore
    private long endDateInMillis;

    @Ignore
    public Workout() {
        //Required empty constructor
    }

    public Workout(boolean isActive, Date startDate, Date endDate) {
        this.isActive = isActive;
        this.startDate = startDate;
        this.endDate = endDate;
        this.endDateInMillis = endDate.getTime();
    }

    public int getWorkOutId() {
        return workOutId;
    }

    public void setWorkOutId(int id) {
        this.workOutId = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDateInMillis(long endDateInMillis) {
        this.endDateInMillis = endDateInMillis;
    }

    public long getEndDateInMillis() {
        return endDateInMillis;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
