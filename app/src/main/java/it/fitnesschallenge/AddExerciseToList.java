package it.fitnesschallenge;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import it.fitnesschallenge.adapter.AddAdapter;
import it.fitnesschallenge.model.room.Exercise;
import it.fitnesschallenge.model.room.PersonalExercise;
import it.fitnesschallenge.model.view.AddExerciseToListModel;


public class AddExerciseToList extends Fragment {

    private static final String ARG_PARAM1 = "personalExerciseList";
    private static final String TAG = "AddExerciseToList";

    private List<PersonalExercise> mPersonalExerciseList;
    private List<Exercise> mExerciseList;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private AddExerciseToListModel mViewModel;
    private AddAdapter mAddAdapter;

    public AddExerciseToList() {
        // Required empty public constructor
    }

    public static AddExerciseToList newInstance(ArrayList<PersonalExercise> personalExerciseList) {
        AddExerciseToList fragment = new AddExerciseToList();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, personalExerciseList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPersonalExerciseList = getArguments().getParcelableArrayList(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_exercise_to_list, container, false);
        mRecyclerView = view.findViewById(R.id.adding_exercise_list);
        mViewModel = ViewModelProviders.of(getActivity()).get(AddExerciseToListModel.class);
        mViewModel.getExerciseList().observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                Log.d(TAG, "Exercise getted: " + exercises.size());
                mExerciseList = exercises;
                mAddAdapter = new AddAdapter(mContext, mExerciseList);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                if (mAddAdapter != null) {
                    mAddAdapter.setOnClickListener(new AddAdapter.OnClickListener() {
                        @Override
                        public void onClickListener(int finalHeight, int startHeight, View itemView, boolean expanded) {
                            expandCardLayout(itemView, finalHeight, startHeight, expanded);
                        }
                    });
                    mAddAdapter.setOnSelectedItemListener(new AddAdapter.OnSelectItemListener() {
                        @Override
                        public void onSelectItemListener(View view, final int position) {
                            TextInputLayout repetitionText = view.findViewById(R.id.exercise_repetition);
                            TextInputLayout seriesText = view.findViewById(R.id.exercise_series);
                            MaterialCheckBox checkBox = view.findViewById(R.id.select_exercise_check);
                            if (checkBox.isChecked())
                                addPersonalExercise(position, repetitionText, seriesText);
                            else{
                                mViewModel.removePersonalExercise(new PersonalExercise(mExerciseList.get(position)));
                            }
                        }
                    });
                }
                mRecyclerView.setAdapter(mAddAdapter);
            }
        });

        mViewModel.getPersonalExerciseLiveData().observe(getViewLifecycleOwner(), new Observer<List<PersonalExercise>>() {
            @Override
            public void onChanged(List<PersonalExercise> personalExerciseList) {
                Log.d(TAG, "Aggiunto o rimosso esercizio");
                //TODO: implementare FAB con controllo sulla compilazione dei campi
            }
        });
        return view;
    }

    private void addPersonalExercise(int position, TextInputLayout repetitionText, TextInputLayout seriesText) {
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
        PersonalExercise personalExercise = new PersonalExercise(mExerciseList.get(position).getImageReference(),
                mExerciseList.get(position).getExerciseName(), mExerciseList.get(position).getExerciseDescription(), series, repetition);
        mViewModel.addPersonalExercise(personalExercise);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    private void expandCardLayout(final View view, final int finalHeight, final int startHeight, final boolean expanded) {
        final View layoutView = view.findViewById(R.id.exercise_item);
        int duration = 300;
        Log.d(TAG, "final: " + finalHeight);
        Log.d(TAG, "start: " + startHeight);
        final TextView description = view.findViewById(R.id.add_exercise_description);
        final ImageButton expandButton = view.findViewById(R.id.card_expander_collapse_arrow);
        ValueAnimator animator;
        if (expanded) {
            Log.d(TAG, "expanded");
            animator = ValueAnimator.ofInt(startHeight, finalHeight);
            expandButton.setImageResource(R.drawable.ic_keyboard_arrow_up);
            expandButton.setContentDescription("EXPANDED");
        } else {
            Log.d(TAG, "not expanded");
            animator = ValueAnimator.ofInt(finalHeight, startHeight);
            description.setVisibility(View.GONE);
            expandButton.setContentDescription("COLLAPSED");
            expandButton.setImageResource(R.drawable.ic_keyboard_arrow_down);
        }
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animationValue = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layoutView.getLayoutParams();
                layoutParams.height = animationValue;
                layoutView.setLayoutParams(layoutParams);
            }
        });
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
}
