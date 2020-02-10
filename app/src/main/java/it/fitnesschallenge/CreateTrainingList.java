package it.fitnesschallenge;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.text.NumberFormat;

import it.fitnesschallenge.model.view.CreationViewModel;

public class CreateTrainingList extends Fragment {

    private static final String TAG = "CreateTrainingList";
    private Context mContext;
    private CreationViewModel mViewModel;
    private ProgressBar mProgressBar;
    private TextView mProgressTextView;

    public CreateTrainingList() {
        //empty creation method needed
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_training_list, container, false);
        mProgressBar = view.findViewById(R.id.create_list_progress_bar);
        mProgressTextView = view.findViewById(R.id.percent);
        ImageButton mNext = view.findViewById(R.id.right_key_arrow);
        ImageButton mPrev = view.findViewById(R.id.left_key_arrow);
        FrameLayout frameContainer = view.findViewById(R.id.inner_frame_creation_list);

        //collego il View model al Fragment
        mViewModel = ViewModelProviders.of(getActivity()).get(CreationViewModel.class);
        mViewModel.getLiveDataProgress().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(mProgressBar != null && mProgressTextView != null){
                    mProgressBar.setProgress(integer);
                    mProgressTextView.setText(NumberFormat.getInstance().format(integer));
                } else {
                    Log.d(TAG, "Change detected on progress: " + integer);
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
