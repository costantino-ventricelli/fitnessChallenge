package it.fitnesschallenge.adapter;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import it.fitnesschallenge.R;
import it.fitnesschallenge.model.room.entity.Exercise;
import it.fitnesschallenge.model.room.FitnessChallengeRepository;
import it.fitnesschallenge.model.room.entity.PersonalExercise;

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ViewHolder>
        implements ItemTouchHelperCallBack.ItemTouchHelperContract {

    private static final String TAG = "ShowAdapter";

    private List<PersonalExercise> mList;
    private OnClickListener mOnClickListener;
    private FitnessChallengeRepository mRepository;
    private LifecycleOwner mCallingFragment;

    public ShowAdapter(List<PersonalExercise> mList, Application callingApplication, LifecycleOwner fragment) {
        this.mList = mList;
        this.mRepository = new FitnessChallengeRepository(callingApplication);
        this.mCallingFragment = fragment;
    }

    /**
     * Questo metodo sovrasctitto da ItemTouchHelperCallBack implementa lo spostamento effettivo tra
     * le card nella RecyclerView.
     *
     * @param fromPosition indica la posizione iniziale della card nella RecyclerView
     * @param toPosition   inidica la posizione finale della card nella RecyclerView
     */
    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        Log.d(TAG, "FromPosition: " + fromPosition);
        Log.d(TAG, "ToPosition: " + toPosition);
        if (fromPosition < toPosition)
            for (int i = fromPosition; i < toPosition; i++) {
                /*
                 * Collection.swap è un metodo che scambia automanticamente la posizione tra gli
                 * elementi di una Collection in questo caso la List<>
                 */
                Collections.swap(mList, i, i + 1);
                Log.d(TAG, "(DOWN SWAPPING) swapping: " + i);
            }
        else
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mList, i, i - 1);
                Log.d(TAG, "(UP SWAPPING) Swapping: " + i);
            }
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * onRowSelected permette di dare un feedback quando la selezione della card avviene.
     *
     * @param viewHolder viewHolder è l'holder dell'item selezionato.
     */
    @Override
    public void onRowSelected(ViewHolder viewHolder) {
        viewHolder.mCardView.setBackgroundColor(Color.LTGRAY);
    }

    /**
     * onRowClear permette di dare un feedback di avvenuto rilascio della card.
     *
     * @param viewHolder viewHolder è l'holder dell'item selezionato.
     */
    @Override
    public void onRowClear(ViewHolder viewHolder) {
        viewHolder.mCardView.setBackgroundColor(Color.WHITE);
    }

    /**
     * Interfacce per la gestione dei click
     */
    public interface OnClickListener {
        void onClickListener(View view, int position);
    }

    /**
     * Metodi di set da richiamare nell'oggetto chiamate per gestire il click
     *
     * @param onClickListener serve per richiamare l'interfaccia e sovrascirvere il metodo di gestione
     *                        del click
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_list_layout, parent, false);
        Log.d(TAG, "Creo il ViewHolder");
        return new ViewHolder(view, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, "Collego il ViewHolder");
        final PersonalExercise personalExercise = mList.get(position);
        mRepository.getExercise(personalExercise.getExerciseId()).observe(mCallingFragment, new Observer<Exercise>() {
            @Override
            public void onChanged(Exercise exercise) {
                Log.d(TAG, "Exercise: " + exercise.getExerciseId());
                holder.mImageView.setImageResource(exercise.getImageReference());
                holder.mTitleTextView.setText(exercise.getExerciseName());
                StringBuilder builder = new StringBuilder(NumberFormat.getInstance().format(personalExercise.getRepetition()));
                builder.append("/");
                builder.append(NumberFormat.getInstance().format(personalExercise.getSteps()));
                holder.mSetNumberTextView.setText(builder.toString());
                holder.mCoolDown.setText(NumberFormat.getInstance(Locale.getDefault()).format(personalExercise.getCoolDown()));
                holder.mCardView.setTag(exercise.getExerciseName());
                if (personalExercise.isDeleted())
                    holder.mActionButton.setImageResource(R.drawable.ic_undo);
                else
                    holder.mActionButton.setImageResource(R.drawable.ic_remove_circle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView mTitleTextView;
        private TextView mSetNumberTextView;
        private View mCardView;
        private ImageButton mActionButton;
        private TextView mCoolDown;

        ViewHolder(@NonNull View itemView, final OnClickListener actionClickListener) {
            super(itemView);
            Log.d(TAG, "Creo la view per l'item");
            mCardView = itemView;
            mImageView = itemView.findViewById(R.id.add_exercise_img);
            mTitleTextView = itemView.findViewById(R.id.add_exercise_title);
            mSetNumberTextView = itemView.findViewById(R.id.exercise_item_number);
            mActionButton = itemView.findViewById(R.id.exercise_item_action);
            mCoolDown = itemView.findViewById(R.id.exercise_item_timer);
            //Qui vengono collegati i metodi di call back con gli oggetti a cui appartengono
            mActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionClickListener.onClickListener(v, getAdapterPosition());
                }
            });
        }
    }
}
