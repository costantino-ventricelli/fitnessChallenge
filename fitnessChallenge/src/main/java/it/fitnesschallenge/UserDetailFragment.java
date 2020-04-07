package it.fitnesschallenge;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.fitnesschallenge.model.ExecutionList;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.room.entity.reference.WorkoutWithExercise;


/**
 * A fragment representing a single User detail screen.
 * This fragment is either contained in a {@link UserListActivity}
 * in two-pane mode (on tablets) or a {@link UserDetailActivity}
 * on handsets.
 */
public class UserDetailFragment extends Fragment {

    private static final String TAG = "UserDetailFragment";

    private List<Entry> mEntryList = new ArrayList<>();
    private LineChart mLineChart;
    private FirebaseFirestore mDatabase;

    private static final String ARG_ITEM_ID = "itemId";

    private String mItem;

    public UserDetailFragment() {
        //Need empty constructor
    }

    public static UserDetailFragment newInstance(String userItem) {
        UserDetailFragment fragment = new UserDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ITEM_ID, userItem);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = getArguments().getString(ARG_ITEM_ID);
        }
        mDatabase = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);
        mLineChart = rootView.findViewById(R.id.progress_chart);

        /*
         * In questo richiamo firebase ottengo l'utlimo workout, che a sua volta richiama getExecutionList
         */
        mDatabase.collection("user").document(mItem).collection("workout")
                .orderBy("workout").limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Log.d(TAG, "Ottenuto workout con esercizi");
                    getExecutionList(documentSnapshot.toObject(WorkoutWithExercise.class).getWorkout());
                }
            }
        });
        return rootView;
    }

    /**
     * In questo metodo viene richiamato Firebase fino ad ottenere la lista di tutte le esecuzioni
     * per quel workout, ora per ogni esecuzione va a richiamare getExecutionData.
     */
    private void getExecutionList(Workout workout) {
        mDatabase.collection("user").document(mItem).collection("workout")
                .document(new SimpleDateFormat(getString(R.string.date_pattern), Locale.getDefault()).format(workout.getStartDate()))
                .collection("execution").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Log.d(TAG, "Ottenuto lista esecuzione");
                    getExecutionData(documentSnapshot.toObject(ExecutionList.class));
                }
            }
        });
    }

    /**
     * Ogni lista di esecuzione contiene una lista di ExerciseExecution dove bisogna calcolare per
     * ognuno il peso medio usato in quanto la classe contiene un array contentente i pesi usati,
     * per ogni ExecutionList la media deve essere unica.
     *
     * @param executionList
     */
    private void getExecutionData(ExecutionList executionList) {

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
