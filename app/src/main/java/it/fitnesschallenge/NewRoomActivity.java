package it.fitnesschallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

import it.fitnesschallenge.model.room.entity.Room;

public class NewRoomActivity extends AppCompatActivity {

    private static final String TAG = "NewRoomActivity";

    private FloatingActionButton mFab;
    private TextInputLayout mRoomNameInput;
    private TextView mGeneratedCode;
    private ProgressBar mProgressBar;
    private TextView mGeneratedCodeLabel;

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
                activeNetwork.isConnectedOrConnecting())
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveNewRoom();
                }
            });
        else {
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
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            final String generatedCode = createNewId();
            Room room = new Room(generatedCode, roomName, auth.getCurrentUser().getEmail());
            database.collection("room/").document(generatedCode).set(room)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mProgressBar.setVisibility(View.GONE);
                            mGeneratedCode.setText(generatedCode);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1500);
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

    private String createNewId() {
        return Long.toString(System.currentTimeMillis() % 1000000);
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
    }
}
