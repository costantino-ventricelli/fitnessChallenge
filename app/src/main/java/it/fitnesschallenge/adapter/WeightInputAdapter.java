package it.fitnesschallenge.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import it.fitnesschallenge.R;

public class WeightInputAdapter extends RecyclerView.Adapter<WeightInputAdapter.ViewHoled> {

    private static final String TAG = "WeightInputAdapter";

    private List<Double> mUsedKilograms;

    public WeightInputAdapter(List<Double> usedKilograms) {
        this.mUsedKilograms = usedKilograms;
        Log.d(TAG, "Dimensione lista: " + mUsedKilograms.size());
    }

    @NonNull
    @Override
    public ViewHoled onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.input_weight_recycler_item, parent, false);
        Log.d(TAG, "Setto il layout della RecyclerView");
        return new ViewHoled(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoled holder, int position) {
        holder.mTextInputLayout.setVisibility(View.VISIBLE);
        Log.d(TAG, "Faccio il bind tra RecyclerView e ViewHolder");
    }

    @Override
    public int getItemCount() {
        return mUsedKilograms.size();
    }

    static class ViewHoled extends RecyclerView.ViewHolder {

        private TextInputLayout mTextInputLayout;

        ViewHoled(@NonNull View itemView) {
            super(itemView);
            mTextInputLayout = itemView.findViewById(R.id.input_weight_item);
            Log.d(TAG, "Creo il ViewHolder");
        }
    }
}
