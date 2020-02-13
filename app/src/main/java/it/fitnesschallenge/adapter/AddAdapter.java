/**
 * Questa classe crea l'adapter per la recycler view che permette l'aggiunta degli esercizi
 * particolare attenzione va data alle interfaccie e alle loro implementazioni
 */
package it.fitnesschallenge.adapter;

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
import it.fitnesschallenge.model.room.Exercise;

public class AddAdapter extends RecyclerView.Adapter<AddAdapter.ViewHolder> {


    private OnClickListener mOnClickListener;
    private OnSelectItemListener mOnSelectedItemListener;
    private List<Exercise> mList;

    public AddAdapter(List<Exercise> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_exercise_item, parent, false);
        //qui avviene l'assegnazione dei listener al ViewHolder
        return new ViewHolder(view, mOnClickListener, mOnSelectedItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.exerciseTitle.setText(mList.get(position).getExerciseName());
        holder.exerciseDescription.setText(mList.get(position).getExerciseDescription());
        holder.exerciseImage.setImageResource(mList.get(position).getImageReference());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * Questa interfaccia permette la gestione del click sul tasto di espansione della card
     */
    public interface OnClickListener {
        /**
         * Il metodo onClickListener è il metodo che verrà richiamato per gestire il click dal ViewHolder
         * @param finalHeight serve a individuare l'altezza finale che la card avrà dopo l'espasione
         * @param startHeight serve a individuare l'altezza iniziale della card
         * @param itemView permette di passare la view della card in questione
         * @param expanded permette di discernere se la card è già espansa
         */
        void onClickListener(int finalHeight, int startHeight, View itemView, boolean expanded);
    }

    /**
     * Questa interfaccia gestisce il click sulla selezione dell'esercizio
     */
    public interface OnSelectItemListener {
        /**
         * Questo metodo verrà richiamato alla per gestire il click dal ViewHolder
         * @param view permette di passare la view della card in questione
         * @param position position permette di selezionare direttamente l'esercizio selezionato
         *                 dalla lista degli esercizi
         */
        void onSelectItemListener(View view, int position);
    }

    /**
     * Questi due metodi di set permettono di settare il listener dall'activity o fragment chiamante
     * @param onClickListener permette di ottenere un riferimento a questa classe tramite il chiamante
     *                        poichè necessita della sovrasctittura del metodo onClickListener o onSelectedItemListener
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setOnSelectedItemListener(OnSelectItemListener onSelectedItemListener) {
        this.mOnSelectedItemListener = onSelectedItemListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView exerciseTitle;
        private CardView cardView;
        private TextView exerciseDescription;
        private boolean modified;
        private boolean expanded;
        private ImageView exerciseImage;
        private int finalHeight;
        private int startHeight;

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
                        exerciseDescription.setVisibility(View.GONE);
                    }
                }
            });
            expanded = false;
            exerciseTitle = itemView.findViewById(R.id.add_exercise_title);
            exerciseImage = itemView.findViewById(R.id.add_exercise_img);
            exerciseDescription = itemView.findViewById(R.id.add_exercise_description);
            TextInputLayout exerciseSeries = itemView.findViewById(R.id.exercise_series);
            TextInputLayout exerciseRepetition = itemView.findViewById(R.id.exercise_repetition);
            ImageButton expandCollapseButton = itemView.findViewById(R.id.card_expander_collapse_arrow);
            MaterialCheckBox selectedCheckBox = itemView.findViewById(R.id.select_exercise_check);
            View divider = itemView.findViewById(R.id.add_card_divider);
            /*
            * La gestione dei click sui pulsanti avviene qiu infatti vengono richiamti i metodi sopra
            * descritti per gestire il click dall'esterno
             */
            expandCollapseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!expanded) {
                        startHeight = cardView.getHeight();
                        expanded = true;
                    }else
                        expanded = false;
                    if (mOnClickListener != null && getAdapterPosition()
                            != RecyclerView.NO_POSITION)
                        //qui abbiamo la gestione dell'onClickListener
                        mOnClickListener.onClickListener(finalHeight, startHeight, itemView, expanded);
                }
            });
            selectedCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null && getAdapterPosition()
                            != RecyclerView.NO_POSITION)
                        //qui abbiamo la gestione dell'onSelectedItemListener
                        mOnSelectedItemListener.onSelectItemListener(itemView, getAdapterPosition());
                }
            });
        }
    }
}
