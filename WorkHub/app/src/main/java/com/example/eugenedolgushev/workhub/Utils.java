package com.example.eugenedolgushev.workhub;

import android.content.Context;
import android.support.v7.app.AlertDialog;

public class Utils {

    public static void showAlertDialog(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Ошибка")
                .setMessage(message)
                .setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
