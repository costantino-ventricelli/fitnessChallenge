package it.fitnesschallenge;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.fitnesschallenge.adapter.LineChartMakerView;
import it.fitnesschallenge.model.ExecutionList;
import it.fitnesschallenge.model.User;
import it.fitnesschallenge.model.room.entity.ExerciseExecution;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.room.entity.reference.WorkoutWithExercise;
import it.fitnesschallenge.model.view.StatisticsViewModel;

public class Statistics extends Fragment {

    private static final String TAG = "Statistics";
    private static final String USER = "user";

    private LineChart mLineChart;
    private TextView mWorkoutsChart;
    private ArrayList<Entry> mEntryList;
    private StatisticsViewModel mViewModel;
    private FirebaseFirestore mDatabase;
    private User mUser;

    public Statistics() {
        // Required empty public constructor
    }

    public static Statistics newInstance(User mUser) {
        Statistics fragment = new Statistics();
        Bundle bundle = new Bundle();
        bundle.putParcelable(USER, mUser);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mUser = getArguments().getParcelable(USER);
        mDatabase = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        mViewModel = ViewModelProviders.of(getActivity()).get(StatisticsViewModel.class);
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

        mViewModel.getExerciseExecutionList().observe(getViewLifecycleOwner(), new Observer<List<ExerciseExecution>>() {
            @Override
            public void onChanged(List<ExerciseExecution> exerciseExecutions) {
                if (exerciseExecutions.size() > 0) {
                    Log.d(TAG, "Trovate esecuzioni");
                    setEntryList(exerciseExecutions);
                } else {
                    mViewModel.getWorkoutList().observe(getViewLifecycleOwner(), new Observer<List<Workout>>() {
                        @Override
                        public void onChanged(List<Workout> workoutList) {
                            if (workoutList.size() > 0) {
                                Log.d(TAG, "Trovati workout nel db locale.");
                                setActiveWorkout(workoutList);
                            } else {
                                Log.d(TAG, "Non ci sono workout nel db locale.");
                            }
                        }
                    });
                }
                setBarChart();
            }
        });

        setBarChart();

        return view;
    }

    private void setActiveWorkout(List<Workout> workouts) {
        Calendar calendar = Calendar.getInstance();
        for (Workout workout : workouts) {
            if (workout.getEndDate().before(calendar.getTime())) {
                Log.d(TAG, "Workout id: " + workout.getWorkOutId() + " start date: " + workout.getStartDate());
                workout.setActive(false);
            } else {
                getExecution(workout);
            }
        }
    }

    private void getExecution(Workout workout) {
        Log.d(TAG, "Prelevo dati da FireStore.");
        final ArrayList<ExecutionList> executionLists = new ArrayList<>();
        String documentPath = "user/" + mUser.getUsername() + "/workout/"
                + new SimpleDateFormat(getString(R.string.date_pattern),
                Locale.getDefault()).format(workout.getStartDate()) + "/execution";
        Log.d(TAG, "documentPath: " + documentPath);
        mDatabase.collection(documentPath).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Log.d(TAG, "Prelevate esecuzioni da Firestore");
                            executionLists.add(documentSnapshot.toObject(ExecutionList.class));
                        }
                        mViewModel.writeExecutionsInLocalDB(executionLists);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), getString(R.string.shit_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setEntryList(List<ExerciseExecution> exerciseExecutions) {
        for (ExerciseExecution execution : exerciseExecutions) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTime(execution.getExecutionDate());
            String floatDate = calendar.get(Calendar.DAY_OF_YEAR) + "." + calendar.get(Calendar.YEAR);
            float executionDate = Float.parseFloat(floatDate);
            Log.d(TAG, "Data di esecuzione: " + executionDate);
            float usedKilogramsAvg = getKilogramsAVG(execution.getUsedKilograms());
            Entry entry = new Entry(executionDate, usedKilogramsAvg);
            Log.d(TAG, "\tCreata nuova entry: " + executionDate + ", " + usedKilogramsAvg);
            mEntryList.add(entry);
        }
    }

    private void setBarChart() {
        LineDataSet lineDataSet = new LineDataSet(mEntryList, "Execution");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setCircleColor(getContext().getColor(R.color.colorPrimaryDark));
        lineDataSet.setLineWidth(4f);
        lineDataSet.setCircleRadius(6f);

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
        xAxis.setGranularity(1F);
        xAxis.setXOffset(10F);
        //xAxis.setDrawAxisLine(false);
        //xAxis.setDrawLabels(false);


        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawTopYLabelEntry(false);
        rightAxis.setDrawLabels(false);

        LineChartMakerView makerView = new LineChartMakerView(getContext(), R.layout.custom_point_view);
        makerView.setChartView(mLineChart);
        mLineChart.setMarker(makerView);
    }

    private Float getKilogramsAVG(List<Float> usedKilograms) {
        float sum = 0.0F;
        for (Float kilograms : usedKilograms)
            sum += kilograms;
        return sum / usedKilograms.size();
    }
}
