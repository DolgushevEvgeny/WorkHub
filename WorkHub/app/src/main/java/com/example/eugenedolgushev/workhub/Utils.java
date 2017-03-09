package com.example.eugenedolgushev.workhub;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class Utils {

    public static void showAlertDialog(final String message, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Ошибка")
                .setMessage(message)
                .setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showAlertDialog(final String message, final Context context, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Ошибка")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Вернуться назад", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
