package it.fitnesschallenge;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.fitnesschallenge.model.view.CreationViewModel;

import static android.app.Activity.RESULT_OK;
import static it.fitnesschallenge.model.SharedConstance.DATE_PICKER;
import static it.fitnesschallenge.model.SharedConstance.SELECTED_DATE;

public class FirstCreationStep extends Fragment {

    private static String[] goals;
    private static final String TAG = "FirstCreationStep";
    private static final int DATE_PICKER_RESULT = 1;
    private CreationViewModel creationViewModel;
    private MaterialButton dateButton;

    public FirstCreationStep() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_creation_step, container, false);

        final TextInputLayout email = view.findViewById(R.id.first_step_creation_text_input);
        final TextInputLayout goal = view.findViewById(R.id.first_step_creation_training_goal);
        dateButton = view.findViewById(R.id.first_step_creation_pick_date);
        AutoCompleteTextView dropDownGoals = view.findViewById(R.id.dropdown_goal);
        goals = new String[]  {getContext().getString(R.string.gain_muscle),
                getContext().getString(R.string.get_fit)};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.drop_down_single_layout,
                goals);
        dropDownGoals.setAdapter(adapter);

        creationViewModel = ViewModelProviders.of(getActivity()).get(CreationViewModel.class);
        creationViewModel.getEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null)
                    email.getEditText().setText(s);
            }
        });
        creationViewModel.getGoal().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    goal.getEditText().setText(s);
                }
            }
        });
        creationViewModel.getStartDate().observe(getViewLifecycleOwner(), new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                if (date != null)
                    dateButton.setText(date.toString());
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout view = (TextInputLayout) v;
                creationViewModel.setEmail(view.getEditText().getText().toString().trim());
            }
        });

        goal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout view = (TextInputLayout) v;
                creationViewModel.setGoal(view.getEditText().getText().toString().trim());
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatDialogFragment appCompatDialogFragment = new DatePickerFragment();
                appCompatDialogFragment.setTargetFragment(FirstCreationStep.this, DATE_PICKER_RESULT);
                appCompatDialogFragment.show(getActivity().getSupportFragmentManager(), DATE_PICKER);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DATE_PICKER_RESULT && resultCode == RESULT_OK){
            Date date = (Date) data.getSerializableExtra(SELECTED_DATE);
            creationViewModel.setStartDate(date);
            String dateFormat = new SimpleDateFormat("YYYY-MM-dd", Locale.getDefault()).format(date);
            dateButton.setText(dateFormat);
        }
    }
}
