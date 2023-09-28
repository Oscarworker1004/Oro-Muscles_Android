package com.digidactylus.recorder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.R;

public class WorkoutSelection extends AppCompatActivity {
    CardView quickStart, workoutSelection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_selection);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        workoutSelection = findViewById(R.id.workoutSelection);
        workoutSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkoutSelection.this, MainActivity.class);
                intent.putExtra("currentPage", 0);
                intent.putExtra("selectTraining", true);
                startActivity(intent);
            }
        });

        quickStart = findViewById(R.id.quickStart);
        quickStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WorkoutSelection.this, MainActivity.class));
            }
        });
    }
}