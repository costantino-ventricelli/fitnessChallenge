package it.fitnesschallenge;

import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import it.fitnesschallenge.model.Room;

public class RoomActivity extends AppCompatActivity {

    private static final String TAG = "RoomActivity";
    private static final String ROOM = "room";

    private TextView mRoomName;
    private TextView mRoomCode;
    private TextView mRoomRanking;
    private TextView mRoomCreator;
    private RecyclerView mRankingRecycler;
    private boolean isClosed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Toolbar toolbar = findViewById(R.id.room_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationContentDescription(getString(R.string.close_details));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Room room = getIntent().getParcelableExtra(ROOM);
        mRoomName = findViewById(R.id.room_name);
        mRoomCode = findViewById(R.id.room_code);
        mRoomRanking = findViewById(R.id.room_ranking_label);
        mRankingRecycler = findViewById(R.id.room_recyclerview);
        mRoomCreator = findViewById(R.id.room_creator);
        mRoomName.setText(room.getRoomName());
        mRoomCreator.setText(room.getRoomCreator());
        mRoomCode.setText(room.getIdCode());

        setTransitionListener();


    }

    private void setTransitionListener() {
        Transition transition = getWindow().getSharedElementEnterTransition();
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Log.d(TAG, "Transizione avviata");
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Log.d(TAG, "Transizione conclusa");
                if (!isClosed) {
                    mRoomCode.setVisibility(View.VISIBLE);
                    mRoomRanking.setVisibility(View.VISIBLE);
                    mRankingRecycler.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                Log.d(TAG, "Transizione cancellata");
            }

            @Override
            public void onTransitionPause(Transition transition) {
                Log.d(TAG, "Transizione in pausa");
            }

            @Override
            public void onTransitionResume(Transition transition) {
                Log.d(TAG, "Transizione ripristinata");
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        closeActivity();
        super.onBackPressed();
    }

    private void closeActivity() {
        mRoomCode.setVisibility(View.GONE);
        mRoomRanking.setVisibility(View.GONE);
        mRankingRecycler.setVisibility(View.GONE);
        mRoomCreator.setVisibility(View.GONE);
        isClosed = true;
    }
}
