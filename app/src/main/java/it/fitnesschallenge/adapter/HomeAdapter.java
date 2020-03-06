package it.fitnesschallenge.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import it.fitnesschallenge.R;
import it.fitnesschallenge.model.room.entity.Exercise;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private static final String TAG = "HomeAdapter";

    private mOnClickListener mOnClickListener;
    private mOnSelectItemListener mOnSelectedItemListener;
    private List<Exercise> mList;

    public HomeAdapter(List<Exercise> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_exercise_item, parent, false);

        return new ViewHolder(view, mOnClickListener, mOnSelectedItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "Collego il ViewHolder");
        holder.exerciseTitle.setText(mList.get(position).getExerciseName());
        holder.exerciseImage.setImageResource(mList.get(position).getImageReference());
        holder.cardView.setTag(mList.get(position).getExerciseId());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface mOnClickListener {
        void onClickListener(int finalHeight, int startHeight, View itemView, boolean expanded);
    }

    public interface mOnSelectItemListener {
        void onSelectItemListener(View view, int position);
    }
        class ViewHolder extends RecyclerView.ViewHolder {

        private TextView exerciseTitle;
        private CardView cardView;
        private ImageView exerciseImage;

            ViewHolder(@NonNull final View itemView, final mOnClickListener mOnClickListener,
                       final mOnSelectItemListener mOnSelectedItemListener) {
                super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.exercise_list);
            exerciseTitle = itemView.findViewById(R.id.home_exercise_title);
            exerciseImage = itemView.findViewById(R.id.home_exercise_img);
            View divider = itemView.findViewById(R.id.add_card_divider);

        }
    }


}
