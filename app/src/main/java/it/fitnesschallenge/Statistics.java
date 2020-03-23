package it.fitnesschallenge;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.fitnesschallenge.model.room.entity.ExerciseExecution;
import it.fitnesschallenge.model.view.StatisticsViewModel;

public class Statistics extends Fragment {

    private static final String TAG = "Statistics";

    private LineChart mLineChart;
    private TextView mWorkoutsChart;
    private ArrayList<Entry> mEntryList;

    public Statistics() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        StatisticsViewModel mViewModel = ViewModelProviders.of(getActivity()).get(StatisticsViewModel.class);
        mLineChart = view.findViewById(R.id.execution_chart);
        mWorkoutsChart = view.findViewById(R.id.workout_statistics_times);
        mEntryList = new ArrayList<>();

        mViewModel.getNumberOfExecution().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mWorkoutsChart.setText(NumberFormat.getInstance(Locale.getDefault())
                        .format(integer));
            }
        });

        /*mViewModel.getExerciseExecutionList().observe(getViewLifecycleOwner(), new Observer<List<ExerciseExecution>>() {
            @Override
            public void onChanged(List<ExerciseExecution> exerciseExecutions) {
                for (ExerciseExecution execution : exerciseExecutions) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTime(execution.getExecutionDate());
                    String floatDate = calendar.get(Calendar.YEAR) + "." + calendar.get(Calendar.DAY_OF_YEAR);
                    float executionDate = Float.parseFloat(floatDate);
                    Log.d(TAG, "Data di esecuzione: " + executionDate);
                    float usedKilogramsAvg = getKilogramsAVG(execution.getUsedKilograms());
                    Entry entry = new Entry(executionDate, usedKilogramsAvg);
                    Log.d(TAG, "\tCreata nuova entry: " + executionDate + ", " + usedKilogramsAvg);
                    mEntryList.add(entry);
                }
                setBarChart();
            }
        });*/

        for (int i = 0; i < 50; i++) {
            Entry entry = new Entry(i, (float) Math.random());
            mEntryList.add(entry);
        }

        setBarChart();

        return view;
    }

    private void setBarChart() {
        LineDataSet lineDataSet = new LineDataSet(mEntryList, "Execution");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setCircleColor(getContext().getColor(R.color.colorPrimaryDark));
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleRadius(4f);

        LineData data = new LineData(lineDataSet);
        data.setValueTextSize(10f);

        mLineChart.setData(data);

        initLineChartChart();
    }

    private void initLineChartChart() {
        Legend legend = mLineChart.getLegend();
        legend.setEnabled(false);
        mLineChart.setBackgroundColor(Color.WHITE);
        mLineChart.getDescription().setEnabled(false);
        mLineChart.setTouchEnabled(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setPinchZoom(true);
        mLineChart.animateX(1500);


        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(false);


        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawTopYLabelEntry(false);
        rightAxis.setDrawLabels(false);

    }

    private Float getKilogramsAVG(List<Float> usedKilograms) {
        float sum = 0.0F;
        for (Float kilograms : usedKilograms)
            sum += kilograms;
        return sum / usedKilograms.size();
    }
}
