package com.example.cardmates.Activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.example.cardmates.R;

public class LoadingDialog {

    Context context;
    Dialog dialog;

    public LoadingDialog(Context context) {
        this.context = context;
    }

    public void showDialog(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.create();
        dialog.show();
    }

    public void hideDialog(){
        dialog.dismiss();
    }
}
