package com.digidactylus.recorder.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.digidactylus.recorder.R;
import com.google.android.material.button.MaterialButton;

public class CustomDialog {
    Context context;
    Dialog dialog;

    public CustomDialog(Context context){
        this.context = context;
    }

    public void show(String text){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.upload_response);
        TextView message = dialog.findViewById(R.id.uploadStatus);
        message.setText(text);
        MaterialButton close = dialog.findViewById(R.id.closeBtn);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

}
