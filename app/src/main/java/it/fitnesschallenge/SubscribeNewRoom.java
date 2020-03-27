package it.fitnesschallenge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SubscribeNewRoom extends AppCompatActivity {

    private static final String TAG = "SubscribeNewRoom";
    private static final String X_FAB_CENTER = "xFabCenter";
    private static final String Y_FAB_CENTER = "yFabCenter";
    private static final String FAB_RADIUS = "fabRadius";

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_new_room);

        fab = findViewById(R.id.new_room_fab);

        final ConstraintLayout layout = findViewById(R.id.subscribe_new_room_constraint);
        layout.getViewTreeObserver().addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
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
                            layout, Math.round(xFabCenter),
                            Math.round(yFabCenter),
                            startRadius, endRadius);
                    circularReveal.setDuration(700);
                    circularReveal.start();
                    circularReveal.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            fab.hide();
                            layout.setBackgroundColor(Color.WHITE);
                        }
                    });
                }
            }

            @Override
            public void onWindowDetached() {

            }
        });
    }
}
