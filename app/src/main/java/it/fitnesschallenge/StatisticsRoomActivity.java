package it.fitnesschallenge;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import it.fitnesschallenge.adapter.SectionsPagerAdapter;
import it.fitnesschallenge.model.User;

public class StatisticsRoomActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsRoomActivity";
    private static final String USER = "user";
    private static final String X_FAB_CENTER = "xFabCenter";
    private static final String Y_FAB_CENTER = "yFabCenter";
    private static final String FAB_RADIUS = "fabRadius";


    private User mUser;
    private ViewPager mViewPager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_room);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), (User) getIntent().getParcelableExtra(USER));
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);

        ImageButton backButton = findViewById(R.id.statistics_activity_back_button);
        fab = findViewById(R.id.statistics_FAB);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Non ci sono implementazioni per questo call back
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    fab.setImageDrawable(getDrawable(R.drawable.ic_share));
                    fab.show();
                } else if (position == 1) {
                    fab.setImageDrawable(getDrawable(R.drawable.ic_add));
                    fab.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING)
                    fab.hide();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mViewPager.getCurrentItem();
                if (position == 0) {
                    //Ancora nulla
                } else if (position == 1) {
                    Intent intent = new Intent(StatisticsRoomActivity.this, SubscribeNewRoom.class);
                    float[] fabAttribute = getFabAttribute();
                    intent.putExtra(X_FAB_CENTER, fabAttribute[0]);
                    intent.putExtra(Y_FAB_CENTER, fabAttribute[1]);
                    intent.putExtra(FAB_RADIUS, fabAttribute[2]);
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(StatisticsRoomActivity.this,
                                    fab, "fab");
                    startActivity(intent, options.toBundle());
                }
            }
        });
    }

    private float[] getFabAttribute() {
        float[] fabAttributes = new float[3];
        int[] fabPosition = new int[2];
        fab.getLocationOnScreen(fabPosition);
        fabAttributes[0] = fabPosition[0] - (fab.getWidth() / 2.00F);
        fabAttributes[1] = fabPosition[1] - (fab.getHeight() / 2.00F);
        Log.d(TAG, "XPosition: " + fabAttributes[0]);
        Log.d(TAG, "YPosition: " + fabAttributes[1]);
        fabAttributes[2] = fab.getWidth() / 2.00F;
        Log.d(TAG, "Radius: " + fabAttributes[2]);
        return fabAttributes;
    }
}