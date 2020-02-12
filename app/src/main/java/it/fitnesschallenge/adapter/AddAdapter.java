package it.fitnesschallenge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import it.fitnesschallenge.R;
import it.fitnesschallenge.model.room.Exercise;

public class AddAdapter extends RecyclerView.Adapter<AddAdapter.ViewHolder> {

    private OnClickListener mOnClickListener;
    private OnSelectItemListener mOnSelectedItemListener;
    private Context mContext;
    private List<Exercise> mList;

    public AddAdapter(Context mContext, List<Exercise> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_exercise_item, parent, false);
        return new ViewHolder(view, mOnClickListener, mOnSelectedItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.exerciseTitle.setText(mList.get(position).getExerciseName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnClickListener{
        void onClickListener(View view);
    }

    public interface OnSelectItemListener{
        void onSelectItemListener(View view);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView exerciseTitle;
        private TextView exerciseDescrption;
        private TextInputLayout exerciseSeries;
        private TextInputLayout exerciseRepetition;
        private ImageButton expandCollapseButton;
        private MaterialCheckBox selectedCheckBox;
        private View divider;

        ViewHolder(@NonNull View itemView, final OnClickListener mOnClickListener, OnSelectItemListener mOnSelectedItemListener) {
            super(itemView);
            exerciseTitle = itemView.findViewById(R.id.add_exercise_title);
            exerciseDescrption = itemView.findViewById(R.id.add_exercise_description);
            exerciseSeries = itemView.findViewById(R.id.exercise_series);
            exerciseRepetition = itemView.findViewById(R.id.exercise_repetition);
            expandCollapseButton = itemView.findViewById(R.id.card_expander_collapse_arrow);
            selectedCheckBox = itemView.findViewById(R.id.select_exercise_check);
            divider = itemView.findViewById(R.id.add_card_divider);
            expandCollapseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onClickListener(v);
                }
            });

        }
    }
}