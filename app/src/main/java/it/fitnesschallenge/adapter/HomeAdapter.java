package it.fitnesschallenge.adapter;

import android.app.Application;
import android.graphics.Color;
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

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import it.fitnesschallenge.R;
import it.fitnesschallenge.model.room.FitnessChallengeRepository;
import it.fitnesschallenge.model.room.entity.Exercise;
import it.fitnesschallenge.model.room.entity.PersonalExercise;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>
        implements HomeAdapterDrag.ItemTouchHelperContract2 {

    private List<PersonalExercise> mList;
    private FitnessChallengeRepository mRepository;
    private LifecycleOwner mCallingFragment;
    private OnClickListener mOnClickListener;

    public HomeAdapter(List<PersonalExercise> mList, Application callingApplication, LifecycleOwner fragment) {
        this.mList = mList;
        this.mRepository = new FitnessChallengeRepository(callingApplication);
        this.mCallingFragment = fragment;
    }


    public interface OnClickListener {
        void onClickListener(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mTitleTextView;
        private TextView mSetNumberTextView;
        private View mCardView;
        private ImageButton mActionButton;
        private TextView mCoolDown;

        ViewHolder(@NonNull View itemView, final OnClickListener actionClickListener) {
            super(itemView);

            mCardView = itemView;
            mImageView = itemView.findViewById(R.id.add_exercise_img);
            mTitleTextView = itemView.findViewById(R.id.add_exercise_title);
            mSetNumberTextView = itemView.findViewById(R.id.exercise_item_number);
            mActionButton = itemView.findViewById(R.id.exercise_item_action);
            mCoolDown = itemView.findViewById(R.id.exercise_item_timer);

        }

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_list_layout, parent, false);
        return new ViewHolder(view, mOnClickListener);
    }


    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final PersonalExercise personalExercise = mList.get(position);
        mRepository.getExercise(personalExercise.getExerciseId()).observe(mCallingFragment, new Observer<Exercise>() {
            @Override
            public void onChanged(Exercise exercise) {
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

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition)
            for (int i = fromPosition; i < toPosition; i++) {
                /*
                 * Collection.swap è un metodo che scambia automanticamente la posizione tra gli
                 * elementi di una Collection in questo caso la List<>
                 */
                Collections.swap(mList, i, i + 1);
            }
        else
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mList, i, i - 1);
            }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(ViewHolder viewHolder) {
        viewHolder.mCardView.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onRowClear(ViewHolder viewHolder) {
        viewHolder.mCardView.setBackgroundColor(Color.WHITE);
    }

}