package it.fitnesschallenge.formatter;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class DayAxisValueFormatter extends ValueFormatter {

    private static final String[] MONTHS = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };
    private static final int DECIMAL_TO_INT = 100;

    private final BarLineChartBase<?> chart;

    private int mDayOfMonth;

    public DayAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value) {
        int year = determineYear(value);
        int dayOfYear = (int) (value - year) * DECIMAL_TO_INT;
        int month = determineMonth(dayOfYear, year);
        //TODO: creare array con valori dei mesi in giorni.
    }

    private int getDaysForMonth() {

    }

    private int determineMonth(int dayOfYear, int year) {
        int february;
        int month = 0;
        int days = 31;
        if (isBissextile(year))
            february = 29;
        else
            february = 28;
        if (dayOfYear > days)
            month++; //gennaio
        if (dayOfYear > (days += february))
            month++; //febbraio
        if (dayOfYear > (days + 31))
            month++; //marzo
        if (dayOfYear > (days + 30))
            month++; //aprile
        if (dayOfYear > (days + 31))
            month++; //maggio
        if (dayOfYear > (days + 30))
            month++; //giugno
        if (dayOfYear > (days + 31))
            month++; //luglio
        if (dayOfYear > (days + 31))
            month++; //agosto
        if (dayOfYear > (days + 30))
            month++; //settembre
        if (dayOfYear > (days + 31))
            month++; //ottobre
        if (dayOfYear > (days + 30))
            month++; //novembre
        if (dayOfYear > (days + 1))
            month++; //dicembre
        return month;
    }

    private int determineDayOfMonth(int days, int month) {

    }

    private boolean isBissextile(int year) {
        return ((year % 4) == 0 || (year % 100) == 0 || (year % 400) == 0);
    }

    private int determineYear(float value) {
        return (int) value;
    }
}
