package it.fitnesschallenge.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import it.fitnesschallenge.R;
import it.fitnesschallenge.model.room.Exercise;

public class AddAdapter extends RecyclerView.Adapter<AddAdapter.ViewHolder> {

    private static final String TAG = "AddAdapter";

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
        holder.exerciseDescrption.setText(mList.get(position).getExerciseDescription());
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnClickListener {
        void onClickListener(int finalHeight, int startHeight, View itemView, boolean expanded);
    }

    public interface OnSelectItemListener {
        void onSelectItemListener(View view);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setOnSelectedItemListener(OnSelectItemListener onSelectedItemListener) {
        this.mOnSelectedItemListener = onSelectedItemListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView exerciseTitle;
        private CardView cardView;
        private TextView exerciseDescrption;
        private boolean modified;
        private boolean expanded;
        private TextInputLayout exerciseSeries;
        private TextInputLayout exerciseRepetition;
        private ImageButton expandCollapseButton;
        private MaterialCheckBox selectedCheckBox;
        private View divider;
        private int finalHeight;
        private int startHeigth;

        ViewHolder(@NonNull final View itemView, final OnClickListener mOnClickListener,
                   final OnSelectItemListener mOnSelectedItemListener) {
            super(itemView);
            modified = false;
            cardView = itemView.findViewById(R.id.item_card_view);
            /*
             * Questo call back rileva che la view sta per essere disegnata, da qui prendo l'altezza finale
             * che dovrebbe avere e la assegno a finalHeight per usarla dopo nell'animazione.
             */
            cardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!modified) {
                        modified = true;
                        finalHeight = cardView.getHeight() + 20;
                        exerciseDescrption.setVisibility(View.GONE);
                    }
                }
            });
            expanded = false;
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
                    if(!expanded) {
                        startHeigth = cardView.getHeight();
                        expanded = true;
                    }else
                        expanded = false;
                    if (mOnClickListener != null && getAdapterPosition()
                            != RecyclerView.NO_POSITION)
                        mOnClickListener.onClickListener(finalHeight, startHeigth, itemView, expanded);
                }
            });
            selectedCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null && getAdapterPosition()
                            != RecyclerView.NO_POSITION)
                        mOnSelectedItemListener.onSelectItemListener(v);
                }
            });
        }
    }
}
