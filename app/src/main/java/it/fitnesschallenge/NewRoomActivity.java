package it.fitnesschallenge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
