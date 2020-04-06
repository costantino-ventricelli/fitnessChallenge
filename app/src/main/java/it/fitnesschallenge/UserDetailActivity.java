package it.fitnesschallenge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.fitnesschallenge.adapter.LineChartMakerView;
import it.fitnesschallenge.model.room.entity.ExerciseExecution;
import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.view.GetWorkoutFromDBModel;

/**
 * An activity representing a single User detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link UserListActivity}.
 */
public class UserDetailActivity extends AppCompatActivity {
    private static final String TAG = "UserDetailActivity";

    /*
     * Questa lista contiene degli oggetti denominati Entry, che fondamentalmente, racchiude due valori
     * di tipo float: x, y, che sono rispettivamente i valori che verranno utilizzati per la rappresentazione
     * dei punti sul grafico.
     */
    private ArrayList<Entry> mEntryList;
    private LineChart mLineChart;
    private static String item_id; //conserva l'id utente ricevuto dall'activity user list
    private static GetWorkoutFromDBModel getWorkoutFromDBModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.user_detail_activity_FAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        /**
         * mostra in alto l'id dell'utente ricevuto da UserListActivity
         */
        TextView userDetail = (TextView) findViewById(R.id.user_detail);
        item_id =  getIntent().getExtras().getString(UserDetailFragment.ARG_ITEM_ID);
        userDetail.setText(item_id);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(UserDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(UserDetailFragment.ARG_ITEM_ID));
            UserDetailFragment fragment = new UserDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.user_detail_container, fragment)
                    .commit();
        }

        //otteniamo il riferimento al grafico
        LineChart lineChart = findViewById(R.id.progress_chart);


        getWorkoutFromDBModel = ViewModelProviders.of(this).get(GetWorkoutFromDBModel.class);
        /**
         * otteniamo il workout dal ViewModel
         */
        getWorkoutFromFirebase();
      getWorkoutFromDBModel.getWorkoutMutableLiveData().observe(this, new Observer<Workout>() {
          @Override
          public void onChanged(Workout workout) {
              MutableLiveData<Workout> workoutMutableLiveData = getWorkoutFromDBModel.getWorkoutMutableLiveData();

          }
      });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, UserListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * questo metodo permette attraverso l'id del workout di ottenere le info di esecuzione del workout su cui poi di faranno le statistiche
     * @param workouts
     */
    private static List<PersonalExercise> getInfoAboutWorkout(List<Workout> workouts) {
        final List<PersonalExercise> personalExercises = new ArrayList<>(1);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Workout execution = workouts.get(0);
        Date execution_id = execution.getStartDate();
        db.collection("user").document(item_id).collection("workout").document(String.valueOf(execution_id)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()) {
                        personalExercises.add(documentSnapshot.toObject(PersonalExercise.class));
                    } else {
                        Log.d(TAG, "No such element");
                    }
                }
            }
        });
    return personalExercises;
    }




    /**
     * recupeara i workout da firebase. di questi salva l'utlima esecuzione nel ViewModel
     * * @return la list con il primo elemento che è l'ultima esecuzione
     */
    private void getWorkoutFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").document(item_id).collection("workout").orderBy("workout", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    e.printStackTrace();
                    return;
                }
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    getWorkoutFromDBModel.setWorkoutMutableLiveData(documentSnapshot.toObject(Workout.class));
                }
            }
        });
    }

    /**
     * Questo metodo crea la lista dei nodi necessari per disegnare il grafico in questione.
     * @param exerciseExecutions contiene una lista delle esecuzioni.
     */
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

    /**
     * Questo metodo contiene i comandi per settare i nodi che il grafico dovrà rappresentare.
     * Tramite la classe LineDataSet viene utilizzata la lista dei nodi da passare al grafico, impostando
     * le dipendenze, e gli apetti delle linee di collegamento e i punti da mostrare.
     * LineData racchiude queste informazioni e le invia al grafico per permetterne la reppresentazione.
     */
    private void setBarChart() {
        LineDataSet lineDataSet = new LineDataSet(mEntryList, "Execution");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setCircleColor(getColor(R.color.colorPrimaryDark));
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
        mLineChart.animateX(0);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setGranularity(1F);
        xAxis.setXOffset(10F);


        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawTopYLabelEntry(false);
        rightAxis.setDrawLabels(false);

        LineChartMakerView makerView = new LineChartMakerView(getApplicationContext() ,R.layout.custom_point_view);
        makerView.setChartView(mLineChart);
        mLineChart.setMarker(makerView);

    }




    /**
     * Questo metodo calcola la media di tutti i pesi utilizzati durante il workout
     * @param usedKilograms lista dei pesi utilizzati
     * @return la media di tutti i pesi.
     */
    private Float getKilogramsAVG(List<Float> usedKilograms) {
        float sum = 0.0F;
        for (Float kilograms : usedKilograms)
            sum += kilograms;
        return sum / usedKilograms.size();
    }

}
