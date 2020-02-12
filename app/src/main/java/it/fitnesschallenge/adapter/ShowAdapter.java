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

import java.text.NumberFormat;
import java.util.List;

import it.fitnesschallenge.R;
import it.fitnesschallenge.model.room.PersonalExercise;

//TODO: capire come questire le query dal DB

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ViewHolder> {

    private List<PersonalExercise> mList;
    private Context mContext;
    private OnLongClickListener mOnLongClickListener;
    private OnClickListener mOnClickListener;

    public ShowAdapter(List<PersonalExercise> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    public void setOnClickListener(OnClickListener onClickListener){
        mOnClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener){
        mOnLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_list_layout, parent, false);
        return new ViewHolder(view, mOnClickListener, mOnLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonalExercise exercise = mList.get(position);
        holder.mImageView.setImageResource(exercise.getImageReference());
        holder.mTitleTextView.setText(exercise.getExerciseName());
        StringBuilder builder = new StringBuilder(NumberFormat.getInstance().format(exercise.getRepetition()));
        builder.append("/");
        builder.append(NumberFormat.getInstance().format(exercise.getSteps()));
        holder.mSetNumberTextView.setText(builder.toString());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnClickListener{
        void onClickListener(View view);
    }

    public interface OnLongClickListener{
        boolean onLongClickListener(View view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mLayout;
        private ImageView mImageView;
        private TextView mTitleTextView;
        private TextView mSetNumberTextView;
        private ImageButton mActionButton;
        private ImageButton mDragHandleButton;

        public ViewHolder(@NonNull View itemView, final OnClickListener actionClickListener, final OnLongClickListener handleClickListener) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.exercise_item);
            mImageView = itemView.findViewById(R.id.add_exercise_img);
            mTitleTextView = itemView.findViewById(R.id.add_exercise_title);
            mSetNumberTextView = itemView.findViewById(R.id.exercise_item_number);
            mActionButton = itemView.findViewById(R.id.exercise_item_action);
            mDragHandleButton = itemView.findViewById(R.id.exercise_item_drag_handle);
            mActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionClickListener.onClickListener(v);
                }
            });
            mDragHandleButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    handleClickListener.onLongClickListener(v);
                    return true;
                }
            });
        }
    }
}
