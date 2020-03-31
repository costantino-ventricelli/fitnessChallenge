package it.fitnesschallenge;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SubscribeNewRoom extends AppCompatActivity {

    private static final String TAG = "SubscribeNewRoom";
    private static final String X_FAB_CENTER = "xFabCenter";
    private static final String Y_FAB_CENTER = "yFabCenter";
    private static final String FAB_RADIUS = "fabRadius";

    private FloatingActionButton mFab;
    private ImageView mImage;
    private RecyclerView mRecyclerView;
    private ConstraintLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_new_room);

        Toolbar toolbar = findViewById(R.id.subscribe_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationContentDescription(R.string.close_subscribe);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFab = findViewById(R.id.new_room_fab);
        mImage = findViewById(R.id.subscribe_image);
        mRecyclerView = findViewById(R.id.subscribe_recycler_view);

        mLayout = findViewById(R.id.subscribe_new_room_constraint);
        setCircleOpenAnimation();
    }

    /**
     * Per creare l'animazione di apertura dal FAB della pagina precedente, viene prima passata la view
     * del FAB come shared content, per evitare che esso sparisca e poi riappaia, dando un senso di
     * continuità alla transazione, dalla precedente activity vengono passate informazioni quali la
     * posizione del FAB e il suo diametro, dopo di che vengono calcolati l'altezza e la larghezza dello
     * schermo del dispositivo, a quel punto viene tolta a questi valori la posizione del FAB e calcolato
     * con il Teorema di Pitagora il raggio dell'animazione, il quale viene implementato con un animazione
     * predefinita di Android: ViewAnimationUtils.createCircularReveal() la quale prende in ingresso,
     * rispettivamente, il Layout da animare, il centro di partenza dell'animazione, dato da x e y,
     * il raggio iniziale e il raggio finale dell'animazione, contemporaneamente viene avviata una
     * animazione per far apparire gradualmente l'immagine di fondo.
     */
    private void setCircleOpenAnimation() {
        mLayout.getViewTreeObserver().addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
            @Override
            public void onWindowAttached() {
                Log.d(TAG, "onWindowAttached");
                Intent intent = getIntent();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                if (intent.getExtras() != null) {
                    float xFabCenter = getIntent().getFloatExtra(X_FAB_CENTER, 0.00F);
                    float yFabCenter = getIntent().getFloatExtra(Y_FAB_CENTER, 0.00F);
                    float startRadius = getIntent().getFloatExtra(FAB_RADIUS, 0.00F);
                    float width = displayMetrics.widthPixels - startRadius;
                    float height = displayMetrics.heightPixels - startRadius;
                    float endRadius = (float) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
                    Log.d(TAG, "endRadius: " + endRadius);
                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                            mLayout, Math.round(xFabCenter),
                            Math.round(yFabCenter),
                            startRadius, endRadius);
                    circularReveal.setDuration(700);
                    circularReveal.start();
                    circularReveal.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mFab.hide();
                            mLayout.setBackgroundColor(Color.WHITE);
                            setImageFadeReveal();
                        }
                    });
                }
            }

            @Override
            public void onWindowDetached() {
                Log.d(TAG, "Window detached");
            }
        });
    }

    /**
     * Questa animazione è detta di fade ed è la graduale comparsa di un oggetto nel layout.
     * In onPostAnimation viene resa visibile l'immagine, altrimenti sarebbe scomparsa nuovamente
     * al termine dell'animazone
     */
    private void setImageFadeReveal() {
        Animation fadeAnimation = AnimationUtils.loadAnimation(SubscribeNewRoom.this, R.anim.fade_in);
        mImage.startAnimation(fadeAnimation);
        mImage.postOnAnimation(new Runnable() {
            @Override
            public void run() {
                mImage.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.subscribe_new_room_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        setSearchViewLayout(searchView);
        return true;
    }

    /**
     * Questo metrodo forza il layout della SearchView per mantenere coerenza all'interno dell'app.
     *
     * @param searchView contiene il riferimento alla SearchView che viene visualizzata nella subscribe_toolbar.
     */
    private void setSearchViewLayout(SearchView searchView) {
        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setHint(getString(R.string.search_room_hint));
        editText.setHintTextColor(Color.WHITE);
        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
        editText.setTextColor(Color.WHITE);
        editText.setTextCursorDrawable(R.drawable.override_cursor);
        ImageView searchBack = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchBack.setImageResource(R.drawable.ic_arrow_back_white);
    }


}
