package it.fitnesschallenge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.fitnesschallenge.R;
import it.fitnesschallenge.model.room.ExerciseTable;
import it.fitnesschallenge.model.room.WorkoutAndExercise;
import it.fitnesschallenge.model.room.WorkoutListExercise;

//TODO: capire come questire le query dal DB

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ViewHolder> {

    private List<WorkoutAndExercise> mList;
    private Context mContext;

    public ShowAdapter(List<WorkoutListExercise> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutAndExercise workoutListExercise = mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mLayout;
        private ImageView mImageView;
        private TextView mTitleTextView;
        private TextView mSetNumberTextView;
        private ImageButton mActionButton;
        private ImageButton mDragHandleButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.exercise_item);
            mImageView = itemView.findViewById(R.id.exercise_item_image);
            mTitleTextView = itemView.findViewById(R.id.exercise_item_title);
            mSetNumberTextView = itemView.findViewById(R.id.exercise_item_number);
            mActionButton = itemView.findViewById(R.id.exercise_item_action);
            mDragHandleButton = itemView.findViewById(R.id.exercise_item_drag_handle);
        }
    }
}
