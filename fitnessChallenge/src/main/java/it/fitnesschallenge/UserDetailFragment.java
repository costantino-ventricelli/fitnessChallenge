package it.fitnesschallenge;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.Entry;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.fitnesschallenge.model.room.entity.ExerciseExecution;
import it.fitnesschallenge.model.room.entity.Workout;


/**
 * A fragment representing a single User detail screen.
 * This fragment is either contained in a {@link UserListActivity}
 * in two-pane mode (on tablets) or a {@link UserDetailActivity}
 * on handsets.
 */
public class UserDetailFragment extends Fragment {

    private static final String TAG = "UserDetailFragment";

    private List<Entry> mEntryList = new ArrayList<>();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private String mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = getArguments().getString(ARG_ITEM_ID);

            Activity activity = this.getActivity();

        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").document(mItem).collection("workout").orderBy("workout", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    e.printStackTrace();
                    return;
                }
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Workout workout = documentSnapshot.toObject(Workout.class);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);

        // Show the dummy content as text in a TextView.






       return rootView;
    }

    private void ottieniEsecuzioneWorkout(Workout workout) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").document(mItem).collection("workout").document(new SimpleDateFormat(getString(R.string.date_pattern), Locale.getDefault()).format(workout.getStartDate())).collection("execution").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    e.printStackTrace();
                    return;
                }
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    ExerciseExecution execution = documentSnapshot.toObject(ExerciseExecution.class);
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


        });
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
