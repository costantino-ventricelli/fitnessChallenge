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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import it.fitnesschallenge.adapter.RegistrationCompleteBottomSheet;
import it.fitnesschallenge.adapter.RoomsAdapter;
import it.fitnesschallenge.model.AlgoliaApiKeys;
import it.fitnesschallenge.model.Participation;
import it.fitnesschallenge.model.Room;

public class SubscribeNewRoom extends AppCompatActivity {

    private static final String TAG = "SubscribeNewRoom";
    private static final String X_FAB_CENTER = "xFabCenter";
    private static final String Y_FAB_CENTER = "yFabCenter";
    private static final String FAB_RADIUS = "fabRadius";

    private FloatingActionButton mFab;
    private ImageView mImage;
    private RecyclerView mRecyclerView;
    private ConstraintLayout mLayout;
    private FirebaseFirestore mDatabase;
    private FirebaseUser mUser;
    private ArrayList<Room> mRoomList;
    private RoomsAdapter mRoomAdapter;
    private AlgoliaApiKeys mApiKeys;
    private Client mClient;
    private Index mIndex;

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

        mDatabase = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mFab = findViewById(R.id.new_room_fab);
        mImage = findViewById(R.id.subscribe_image);
        mRecyclerView = findViewById(R.id.subscribe_recycler_view);

        mRoomList = new ArrayList<>();
        mRoomAdapter = new RoomsAdapter(mRoomList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRoomAdapter);

        mLayout = findViewById(R.id.subscribe_new_room_constraint);

        mRoomAdapter.setOnClickListener(new RoomsAdapter.OnClickListener() {
            @Override
            public void onClick(final int position, RoomsAdapter.ViewHolder view) {
                mDatabase.collection("user").document(mUser.getEmail())
                        .collection("participation").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    addRoomToList(position, snapshot.toObject(Participation.class), snapshot.getId());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Firebase exception", e);
                            }
                        });
            }
        });

        mDatabase.collection("AlgoliaApi").document("apiCode").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mApiKeys = documentSnapshot.toObject(AlgoliaApiKeys.class);
                        mClient = new Client(mApiKeys.getClientCode(), mApiKeys.getApiKey());
                        mIndex = mClient.getIndex("rooms_name");
                        Log.d(TAG, "ClientCode: " + mApiKeys.getClientCode());
                        Log.d(TAG, "ApiKey: " + mApiKeys.getApiKey());
                    }
                });
        setCircleOpenAnimation();
    }

    private void addRoomToList(int position, Participation participation, String documentId) {
        final RegistrationCompleteBottomSheet bottomSheet = new RegistrationCompleteBottomSheet();
        bottomSheet.show(getSupportFragmentManager(), "REGISTERING_BOTTOM");
        String clickedRoom = mRoomAdapter.getItemAtPosition(position).getIdCode();
        ArrayList<String> participationList = participation.getRoomsList();
        if (participationList.contains(clickedRoom)) {
            Log.d(TAG, "Già registrato");
            bottomSheet.setSuccess(false);
        } else {
            Log.d(TAG, "Registro");
            participationList.add(clickedRoom);
            participation.setRoomsList(participationList);
            mDatabase.collection("user").document(mUser.getEmail())
                    .collection("participation").document(documentId)
                    .update("roomsList", participation.getRoomsList())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            bottomSheet.setSuccess(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Firebase exception", e);
                        }
                    });
        }
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mRoomList.clear();
                mRoomAdapter.notifyDataSetChanged();
                algoliaSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mRoomList.clear();
                mRoomAdapter.notifyDataSetChanged();
                algoliaSearch(newText);
                return true;
            }
        });
        return true;
    }

    private void algoliaSearch(String query) {
        Query algoliaQuery = new Query(query)
                .setAttributesToRetrieve("roomName")
                .setHitsPerPage(50);
        mIndex.searchAsync(algoliaQuery, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try {
                    JSONArray jsonArray = content.getJSONArray("hits");
                    ArrayList<String> resultSet = new ArrayList<>();
                    if (jsonArray.length() <= 0) {
                        mRecyclerView.setVisibility(View.GONE);
                        setImageFadeReveal();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Log.d(TAG, "Json ottenuto: " + object.getString("roomName"));
                        resultSet.add(object.getString("roomName"));
                    }
                    findRoom(resultSet);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void findRoom(ArrayList<String> searchObject) {
        Log.d(TAG, "Search object: " + searchObject.toString());
        mRecyclerView.setVisibility(View.VISIBLE);
        mImage.setVisibility(View.GONE);
        for (String query : searchObject) {
            Log.d(TAG, "Query: " + query);
            mDatabase.collection("room")
                    .whereEqualTo("roomName", query).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d(TAG, "Ottenuto da fire base");
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mRoomList.add(snapshot.toObject(Room.class));
                            Log.d(TAG, "Room list: " + mRoomList.toString());
                            mRoomAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "FirebaseException", e);
                        }
                    });
        }
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
