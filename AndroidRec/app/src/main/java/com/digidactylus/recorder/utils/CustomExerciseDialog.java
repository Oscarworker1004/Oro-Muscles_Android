package com.digidactylus.recorder.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.digidactylus.recorder.R;
import com.digidactylus.recorder.models.TrainingModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Objects;

public class CustomExerciseDialog extends Dialog {
    final Context context;

    private static final String TAG = "CustomExerciseDialog";

    boolean isSecondPage = false;
    public CustomExerciseDialog(@NonNull Context context, boolean isSecondPage) {
        super(context);
        this.context = context;
        this.isSecondPage = isSecondPage;
    }
    EditText editText;
    MaterialButton savebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_exercise_dialog_layout);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);
        editText = findViewById(R.id.editText);
        savebtn = findViewById(R.id.savebtn);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = editText.getText().toString();
                if(!txt.isEmpty()) {
                    boolean exists = Tools.checkCustomExercies(context, txt);
                    if(!exists) {
                        Tools.addToCustomExercise(context, editText.getText().toString(), isSecondPage);
                        Toast.makeText(context, "Add exercise " + editText.getText().toString() + " to the custom list", Toast.LENGTH_SHORT).show();
                        editText.setText("");
                        dismiss();
                    }
                    else {
                        Toast.makeText(context, "Custom Exercise already existed", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "Exercise name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}
