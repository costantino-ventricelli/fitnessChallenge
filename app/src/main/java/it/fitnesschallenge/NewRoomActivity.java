package it.fitnesschallenge;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.fitnesschallenge.model.Participation;
import it.fitnesschallenge.model.Room;

public class NewRoomActivity extends AppCompatActivity {

    private static final String TAG = "NewRoomActivity";

    private FloatingActionButton mFab;
    private TextInputLayout mRoomNameInput;
    private TextView mGeneratedCode;
    private ProgressBar mProgressBar;
    private TextView mGeneratedCodeLabel;
    private FirebaseFirestore mDatabase;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);

        Toolbar toolbar = findViewById(R.id.new_room_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle(getString(R.string.new_room));
        toolbar.setNavigationContentDescription(R.string.close_new_room);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mFab = findViewById(R.id.new_room_save_FAB);
        mRoomNameInput = findViewById(R.id.new_room_room_name);
        mGeneratedCode = findViewById(R.id.new_room_generated_code);
        mProgressBar = findViewById(R.id.new_room_progress_bar);
        mGeneratedCodeLabel = findViewById(R.id.new_room_generated_code_label);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting()) {
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveNewRoom();
                }
            });
            mDatabase = FirebaseFirestore.getInstance();
            mUser = FirebaseAuth.getInstance().getCurrentUser();
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.ops)
                    .setMessage(R.string.connection_error_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    }

    private void saveNewRoom() {
        mProgressBar.setVisibility(View.VISIBLE);
        String roomName = mRoomNameInput.getEditText().getText().toString().trim();
        if (!roomName.isEmpty()) {
            final String generatedCode = createNewId();
            Room room = new Room(generatedCode, roomName, mUser.getEmail());
            mDatabase.collection("room/").document(generatedCode).set(room)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mGeneratedCode.setText(generatedCode);
                            setParticipation(generatedCode);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mGeneratedCode.setTextColor(Color.RED);
                            mGeneratedCode.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_error,
                                    0, 0, 0);
                            mGeneratedCode.setText(R.string.upload_room_faild);
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void setParticipation(final String generatedCode) {
        mDatabase.collection("user/").document(mUser.getEmail())
                .collection("/participation")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "Documenti prelevati da FireBase");
                ArrayList<String> roomsList = new ArrayList<>();
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Participation participation = documentSnapshot.toObject(Participation.class);
                        roomsList = participation.getRoomsList();
                        roomsList.add(generatedCode);
                        participation.setRoomsList(roomsList);
                        updateDocument(participation, documentSnapshot.getId());
                    }
                } else {
                    roomsList.add(generatedCode);
                    createNewDocument(new Participation(roomsList));
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Errore connessione");
                    }
                });
    }

    private void updateDocument(Participation participation, String id) {
        if (participation != null) {
            try {
                mDatabase.collection("user").document(mUser.getEmail())
                        .collection("participation").document(id)
                        .update("roomsList", participation.getRoomsList())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mProgressBar.setVisibility(View.GONE);
                                showSuccessDialog();
                            }
                        });
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "user/" + mUser.getEmail() + "/participation/");
                Toast.makeText(this, "user/" + mUser.getEmail() + "/participation/", Toast.LENGTH_LONG).show();
            }
            Log.d(TAG, "Aggiorno il documento");
        }
    }

    private void createNewDocument(Participation participation) {
        if (participation != null) {
            mDatabase.collection("user").document(mUser.getEmail())
                    .collection("participation").add(participation)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            mProgressBar.setVisibility(View.GONE);
                            showSuccessDialog();
                        }
                    });
            Log.d(TAG, "Aggiorno il documento");
        }
    }

    private void showSuccessDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.success)
                .setMessage(R.string.upload_complete_successful)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }

    private String createNewId() {
        return Long.toString(System.currentTimeMillis());
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        closeActivity();
        super.onBackPressed();
    }

    private void closeActivity() {
        mFab.setVisibility(View.GONE);
        mRoomNameInput.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mGeneratedCode.setVisibility(View.GONE);
        mGeneratedCodeLabel.setVisibility(View.GONE);
    }
}
