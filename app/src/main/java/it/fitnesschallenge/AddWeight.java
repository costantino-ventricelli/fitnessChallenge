package it.fitnesschallenge;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;

import it.fitnesschallenge.adapter.WeightInputAdapter;

public class AddWeight extends Fragment {

    private static final String TAG = "AddWeight";

    private RecyclerView mWeightRecyclerView;
    private RecyclerView mRepetitionRecyclerView;
    private TextView mLastTime;
    private MaterialButton mSaveButton;

    public AddWeight() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_weight, container, false);
        mWeightRecyclerView = view.findViewById(R.id.add_weight_weight_recycler_view);
        mRepetitionRecyclerView = view.findViewById(R.id.add_weight_repetition_recycler_view);
        mLastTime = view.findViewById(R.id.add_weight_last_time);
        mSaveButton = view.findViewById(R.id.add_weight_save_button);

        ArrayList<Double> arrayList = new ArrayList<>();
        arrayList.add(0.00);
        arrayList.add(0.00);
        arrayList.add(0.00);
        WeightInputAdapter adapter = new WeightInputAdapter(arrayList);
        mWeightRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mWeightRecyclerView.setAdapter(adapter);

        return view;
    }
}
