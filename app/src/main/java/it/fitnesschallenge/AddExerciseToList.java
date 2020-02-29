package it.fitnesschallenge;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import it.fitnesschallenge.adapter.AddAdapter;
import it.fitnesschallenge.model.room.entity.Exercise;
import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.view.AddExerciseToListModel;
import it.fitnesschallenge.model.view.CreationViewModel;

import static android.app.Activity.RESULT_OK;
import static it.fitnesschallenge.model.SharedConstance.DATE_PICKER;
import static it.fitnesschallenge.model.SharedConstance.POSITION_IN_ADAPTER;
import static it.fitnesschallenge.model.SharedConstance.SELECTED_TIMER;


public class AddExerciseToList extends Fragment {

    private static final String TAG = "AddExerciseToList";
    private static final int TIMER_PICKER_RESULT = 1;

    private List<Exercise> mExerciseList;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private AddExerciseToListModel mViewModel;
    private AddAdapter mAddAdapter;
    private List<PersonalExercise> mPersonalExerciseList;

    public AddExerciseToList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_exercise_to_list, container, false);
        Log.d(TAG, "Prima di tutto:\n" +
                "\t mRecyclerView: " + (mRecyclerView == null ? "null" : mRecyclerView.toString()) + "\n" +
                "\t mAddAdapter: " + (mAddAdapter == null ? "null" : mAddAdapter.toString()) + "\n" +
                "\t mExerciseList: " + (mExerciseList == null ? "null" : mExerciseList.toString()) + "\n");
        FloatingActionButton saveButton = view.findViewById(R.id.add_exercise_FAB);
        mRecyclerView = view.findViewById(R.id.adding_exercise_list);
        mViewModel = ViewModelProviders.of(getActivity()).get(AddExerciseToListModel.class);
        /*
         * Questo observer rimane in ascolto degli esercizi presenti nel DB, quando questi vengono resi
         * disponibili avvia l'inizializzazione della View con i relativi parametri e implementa il
         * click sul bottone di espansione e selezione
         */
        mViewModel.getExerciseList().observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                Log.d(TAG, "Exercise ottenuti: " + exercises.size());
                mExerciseList = exercises;
                mAddAdapter = new AddAdapter(mExerciseList);
                Log.d(TAG, "Creo l'adapter: " + mAddAdapter.toString());
                Log.d(TAG, "Adapter creato, grandezza dati: " + NumberFormat
                        .getInstance().format(mAddAdapter
                                .getItemCount()));
                mRecyclerView.setAdapter(mAddAdapter);
                Log.d(TAG, "RecyclerView impostata");
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                Log.d(TAG, "Layout settato per la recyclerView: " + mRecyclerView.getLayoutManager());
                mAddAdapter.notifyDataSetChanged();
                Log.d(TAG, "Cerco di forzare il binding");
                if (mAddAdapter != null) {
                    mAddAdapter.setOnClickListener(new AddAdapter.OnClickListener() {
                        @Override
                        public void onClickListener(int finalHeight, int startHeight, View itemView, boolean expanded) {
                            expandCardLayout(itemView, finalHeight, startHeight, expanded);
                        }
                    });
                    mAddAdapter.setOnSelectedItemListener(new AddAdapter.OnSelectItemListener() {
                        /*
                         * Questo metodo verifica se era già stato effettuato l'inserimento di quell'
                         * esercizio se non era stato già inserito viene fatto, altrimenti viene
                         * eliminato dalla lista
                         */
                        @Override
                        public void onSelectItemListener(View view, final int position) {
                            TextInputLayout repetitionText = view.findViewById(R.id.exercise_repetition);
                            TextInputLayout seriesText = view.findViewById(R.id.exercise_series);
                            MaterialCheckBox checkBox = view.findViewById(R.id.select_exercise_check);
                            MaterialButton pickTime = view.findViewById(R.id.add_exercise_pick_time_button);
                            long coolDown = 0L;
                            try {
                                coolDown = Long.parseLong(pickTime.getText().toString().replace("''", ""));
                            } catch (NumberFormatException ex) {
                                ex.printStackTrace();
                            }
                            if (checkBox.isChecked())
                                addPersonalExercise(position, repetitionText, seriesText, coolDown);
                            else {
                                mViewModel.removePersonalExercise(mPersonalExerciseList.get(position));
                            }
                        }
                    });
                    /*
                     * Questo listener apre il dialog per settare il timer, il quale viene restutuito
                     * in secondi in onActivityResult
                     */
                    mAddAdapter.setOnClickTimerListener(new AddAdapter.OnClickTimerListener() {
                        @Override
                        public void onTimerListener(int position) {
                            AppCompatDialogFragment appCompatDialogFragment = new TimerPickerFragment(position);
                            appCompatDialogFragment.setTargetFragment(AddExerciseToList.this, TIMER_PICKER_RESULT);
                            appCompatDialogFragment.show(getActivity().getSupportFragmentManager(), DATE_PICKER);
                        }
                    });
                }
                /*
                 * Quando c'è uno scroll sulla RecyclerView potrebbe essere necessario ricostruire la
                 * selezione precedente, quindi viene richiamato RebuildRecyclerView.
                 */
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (mPersonalExerciseList != null) {
                            Log.d(TAG, "Scrolling richiamo Rebuild");
                            new RebuildRecyclerView(mRecyclerView, mPersonalExerciseList).execute();
                        } else
                            Log.d(TAG, "Fuck, onScrolled catturato, ma non c'è un cazzo in lista");
                    }
                });
            }
        });

        mViewModel.getPersonalExerciseLiveData().observe(getViewLifecycleOwner(), new Observer<List<PersonalExercise>>() {
            @Override
            public void onChanged(List<PersonalExercise> personalExerciseList) {
                if (personalExerciseList.size() > 0) {
                    new RebuildRecyclerView(mRecyclerView, personalExerciseList).execute();
                    mPersonalExerciseList = personalExerciseList;
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Quando viene richiesto il salvataggio inizia il processo di controllo sui dati che
                 * permette di verificare se tutto è stato compilato in maniera corretta
                 */
                if (mPersonalExerciseList != null) {
                    for (PersonalExercise personalExercise : mPersonalExerciseList)
                        checkPersonalExercise(personalExercise);
                }
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        return view;
    }

    /**
     * Questo metodo verifica che siano stati compilati i campi dell'esercizio prima di salvarlo
     * nel ViewModel di comunicazione, CreationViewModel
     *
     * @param personalExercise è l'esercizio prelevato da controllare
     */
    private void checkPersonalExercise(PersonalExercise personalExercise) {
        //Error tiene conto di eventuali campi non compilati
        boolean error = false;
        if (personalExercise.getRepetition() == 0 || personalExercise.getSteps() == 0) {
            mViewModel.removePersonalExercise(personalExercise);
            error = true;
            int position = mExerciseList.indexOf(personalExercise);
            Log.d(TAG, "Posizione: " + position);
            View itemView = mRecyclerView.getChildAt(position);
            if (itemView != null) {
                TextInputLayout repetition = itemView.findViewById(R.id.exercise_repetition);
                TextInputLayout series = itemView.findViewById(R.id.exercise_series);
                MaterialCheckBox checkBox = itemView.findViewById(R.id.select_exercise_check);
                checkBox.setChecked(false);
                repetition.setError(mContext.getResources().getString(R.string.complete_correctly_field));
                series.setError(mContext.getResources().getString(R.string.complete_correctly_field));
            }
        }
        //Se non ci sono stati errore viene salvato nel ViewModel la lista degli esercizi
        if (!error) {
            CreationViewModel creationViewModel = ViewModelProviders.of(getActivity()).get(CreationViewModel.class);
            creationViewModel.setPersonalExerciseList(mViewModel.getPersonalExerciseLiveData().getValue());
        }
    }

    /**
     * Questo metodo aggiunge gli esercizi selezionati nella lista sincronizzandola con il ViewModel
     *
     * @param position       è la posizione dell'esercizio nella lista
     * @param repetitionText EditText contentente il numero di ripetizioni da eseguire
     * @param seriesText     EditText contentente il numero di serie da eseguire
     */
    private void addPersonalExercise(int position, TextInputLayout repetitionText, TextInputLayout seriesText, long coolDown) {
        int repetition = 0;
        int series = 0;
        try {
            String stringRepetition = repetitionText.getEditText().getText().toString().trim();
            String stringSeries = seriesText.getEditText().getText().toString().trim();
            if (!stringRepetition.isEmpty())
                repetition = Integer.parseInt(stringRepetition);
            if (!stringSeries.isEmpty())
                series = Integer.parseInt(stringSeries);
        } catch (NumberFormatException ex) {
            repetitionText.setError(mContext.getResources().getString(R.string.complete_correctly_field));
            seriesText.setError(mContext.getResources().getString(R.string.complete_correctly_field));
        }
        PersonalExercise personalExercise = new PersonalExercise(mExerciseList.get(position).getExerciseId(), series, repetition, coolDown);
        mViewModel.addPersonalExercise(personalExercise);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        //Distruggo tutte le variabili del fragment
        super.onDetach();
        mContext = null;
    }

    /**
     * La sovrasctittura di onActivityResult permette di gestire il valore di ritorno da TimePickerFragmet
     *
     * @param requestCode contiene il codice di richiesta passato all'nuovo Intent: TIMER_PICKER_RESULT
     * @param resultCode  contiene il risultato dell'elaborzione nell'Intent
     * @param data        contiene i dati elaborati dall'intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == TIMER_PICKER_RESULT && resultCode == RESULT_OK) {
                mAddAdapter.setCoolDownTime(
                        mRecyclerView.getChildViewHolder(
                                mRecyclerView.getChildAt(data.getIntExtra(POSITION_IN_ADAPTER, -1))
                        ), data.getLongExtra(SELECTED_TIMER, 0));
            }
        } catch (NullPointerException ex) {
            Toast.makeText(mContext, mContext.getString(R.string.shit_error), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    /**
     * Questo metodo gestisce l'animazione di espansione restringimento della card contente l'esercizo
     *
     * @param view        contiente la view da espandere/collassare
     * @param finalHeight contiene l'altezza finale della card dopo l'espansione
     * @param startHeight contiene l'altezza iniziale della card prima dell'espansione
     * @param expanded    permette di verificare se la card è espansa o meno
     */
    private void expandCardLayout(final View view, final int finalHeight, final int startHeight, final boolean expanded) {
        final View layoutView = view.findViewById(R.id.exercise_item);
        int duration = 300;
        Log.d(TAG, "final: " + finalHeight);
        Log.d(TAG, "start: " + startHeight);
        final TextView description = view.findViewById(R.id.add_exercise_description);
        final ImageButton expandButton = view.findViewById(R.id.card_expander_collapse_arrow);
        ValueAnimator animator;
        //Value animator prende due int uno iniziale e uno finale, poi li avvicina incrementalmente
        if (expanded) {
            Log.d(TAG, "Expanded");
            //Questa animazione va dall'altezza iniziale a quella finale per la view
            animator = ValueAnimator.ofInt(startHeight, finalHeight);
            expandButton.setImageResource(R.drawable.ic_keyboard_arrow_up);
            expandButton.setContentDescription("EXPANDED");
        } else {
            Log.d(TAG, "Not expanded");
            //Questa al contrario va dall'altezza finale a quella inizale della view
            animator = ValueAnimator.ofInt(finalHeight, startHeight);
            description.setVisibility(View.GONE);
            expandButton.setContentDescription("COLLAPSED");
            expandButton.setImageResource(R.drawable.ic_keyboard_arrow_down);
        }
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        //Questo Listener permette di aggiornare la view
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //Qui viene preso il valore dell'animazione
                int animationValue = (Integer) animation.getAnimatedValue();
                //Qui viene settato il nuovo valore sulla View
                ViewGroup.LayoutParams layoutParams = layoutView.getLayoutParams();
                layoutParams.height = animationValue;
                layoutView.setLayoutParams(layoutParams);
            }
        });
        /*
         * Alla fine dell'animazione viene richiamato questo metodo di call back che imposta la
         * visibilità del box di descrizione
         */
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (expanded)
                    description.setVisibility(View.VISIBLE);
            }
        });
        animator.start();
    }

    /**
     * L'esecuzione di questo thread asincrono è indispensabile, in quanto l'aggiornamento delle view
     * avviene con i live data quindi su thread a livello differenti, per questo, quando il fragment
     * viene richiamato parte subito l'inizializzazione sulla RecyclerView, mentre non ha ancora
     * ultimato il binding tra layout e item della lista, quindi questa classe permette di attendere
     * finchè non viene inizalizzata la View, dopo di che imposta gli esercizi selezionati
     * precedentemente.
     */
    static class RebuildRecyclerView extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        private RecyclerView mRecyclerView;
        private List<PersonalExercise> mPersonalExerciseList;

        RebuildRecyclerView(RecyclerView recyclerView, List<PersonalExercise> personalExerciseList) {
            mRecyclerView = recyclerView;
            mPersonalExerciseList = personalExerciseList;
        }

        /*
         * In background viene eseguto un comando che non dovrebbe essere eseguito fuori dal UI Thread
         * ma è stato necessario per attende il Binding dell'adapter alla RecyclerView
         */
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            View item;
            do {
                item = mRecyclerView.getChildAt(0);
                Log.d(TAG, "Attendo che vengano ripristinati gli adapter");
            } while (item == null);
            return null;
        }

        /*
         * In onPostExecute viene effettivamente ripristinata la View questo non è un errore in quanto
         * onPostExecute viene eseguito nel UI thread.
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*
             * Scorrendo la lista degli esercizi ogni volta non vogiamo che il metodo cerchi di
             * recuperare informazioni da elementi non visibili al momento, perché nella RecyclerView
             * se un item non è visibile non esiste nell'Holder.
             */
            for (PersonalExercise personalExercise : mPersonalExerciseList) {
                try {
                    checkRecycler(personalExercise);
                } catch (NullPointerException ex) {
                    Log.d(TAG, "La view selezionata non è visibile");
                }
            }
        }

        private void checkRecycler(PersonalExercise personalExercise) {
            View itemView = mRecyclerView.findViewWithTag(personalExercise.getExerciseId());
            if (itemView != null) {
                Log.d(TAG, "Item view: " + itemView.getTag().toString());
                MaterialCheckBox materialCheckBox = itemView.findViewById(R.id.select_exercise_check);
                TextInputLayout series = itemView.findViewById(R.id.exercise_series);
                TextInputLayout repetitions = itemView.findViewById(R.id.exercise_repetition);
                MaterialButton setTimer = itemView.findViewById(R.id.add_exercise_pick_time_button);
                materialCheckBox.setChecked(true);
                /*
                 * In questi controlli si verifica che sia stato inserito un numero di ripetizione
                 * o serie, oppure sia stato impostato il timer, così da non cancellare i valori di
                 * default in caso non ne siano stai settati di nuovi
                 */
                Log.d(TAG, "Cool down: " + personalExercise.getCoolDown());
                if (personalExercise.getRepetition() != 0)
                    repetitions.getEditText().setText(NumberFormat.getInstance().format(
                            personalExercise.getRepetition()
                    ));
                if (personalExercise.getSteps() != 0)
                    series.getEditText().setText(NumberFormat.getInstance().format(
                            personalExercise.getSteps()
                    ));
                if (personalExercise.getCoolDown() != 0L)
                    setTimer.setText(NumberFormat.getInstance(Locale.getDefault()).format(
                            personalExercise.getCoolDown()
                    ));
                //FIXME: se si effettuano modifiche all'esercizio non bisogna riselezionarlo, UX fa schifo
            }
        }
    }
}
