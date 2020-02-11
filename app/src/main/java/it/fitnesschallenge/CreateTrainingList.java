package it.fitnesschallenge;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.text.NumberFormat;
import java.util.ArrayList;

import it.fitnesschallenge.model.view.CreationViewModel;

import static it.fitnesschallenge.model.SharedConstance.FIRST_STEP_CREATION;
import static it.fitnesschallenge.model.SharedConstance.SECOND_STEP_CREATION;

public class CreateTrainingList extends Fragment {

    private static final String TAG = "CreateTrainingList";
    private Context mContext;
    private CreationViewModel mViewModel;
    private ProgressBar mProgressBar;
    private TextView mProgressTextView;
    private TextView mPrevText;
    private ImageButton mNext;
    private ImageButton mPrev;

    public CreateTrainingList() {
        //empty creation method needed
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_training_list, container, false);
        mProgressBar = view.findViewById(R.id.create_list_progress_bar);
        mProgressTextView = view.findViewById(R.id.create_list_percent);
        mPrevText = view.findViewById(R.id.previous_text);
        mNext = view.findViewById(R.id.right_key_arrow);
        mPrev = view.findViewById(R.id.left_key_arrow);

        //collego il View model al Fragment
        mViewModel = ViewModelProviders.of(getActivity()).get(CreationViewModel.class);
        mViewModel.getLiveDataProgress().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(mProgressBar != null && mProgressTextView != null){
                    ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", integer)
                            .setDuration(300);
                    objectAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mProgressTextView.setText(NumberFormat
                                    .getNumberInstance()
                                    .format(mProgressBar.getProgress()));
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressTextView.setText(NumberFormat
                                    .getNumberInstance()
                                    .format(mProgressBar.getProgress()));
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.shit_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            mProgressTextView.setText(NumberFormat
                                    .getNumberInstance()
                                    .format(mProgressBar.getProgress()));
                        }
                    });
                    objectAnimator.start();
                } else {
                    Log.d(TAG, "Change detected on progress: " + integer);
                }
            }
        });

        mViewModel.getLiveDataSteps().observe(getViewLifecycleOwner(), new Observer<ArrayList<Integer>>() {
            @Override
            public void onChanged(ArrayList<Integer> integers) {
                setCurrentStep(integers);
                Log.d(TAG, "Step changed: " + integers.size());
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.nextStep();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void setCurrentStep(ArrayList<Integer> integers){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
                R.anim.enter_from_rigth, R.anim.exit_from_left);
        Log.d(TAG, "Current step: " + integers.size());
        switch (integers.size()){
            case 0:
            case 1:
                FirstCreationStep firstCreationStep = new FirstCreationStep();
                transaction.replace(R.id.inner_frame_creation_list, firstCreationStep, FIRST_STEP_CREATION)
                        .commit();
                mPrev.setVisibility(View.GONE);
                mPrevText.setVisibility(View.GONE);
                break;
            case 2:
                mPrev.setVisibility(View.VISIBLE);
                mPrevText.setVisibility(View.VISIBLE);
                ExerciseList exerciseList = new ExerciseList();
                transaction.replace(R.id.inner_frame_creation_list, exerciseList, SECOND_STEP_CREATION)
                        .commit();
                mViewModel.setLiveDataProgress(33);
                break;
        }
    }
}
