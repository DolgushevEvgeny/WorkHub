package com.example.eugenedolgushev.workhub;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import static android.content.Context.MODE_PRIVATE;
import static com.example.eugenedolgushev.workhub.DefaultValues.SHARED_PREFERENCES_NAME;

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

    public static void setStringToSharedPreferences(final String key, final String value, final Context context) {
        SharedPreferences sPref = context
                .getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringFromSharedPreferences(final String key, final Context context) {
        SharedPreferences sPref = context
                .getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        return sPref.getString(key, "");
    }

    public static void removeSharedPreferences(final String key, final Context context) {
        SharedPreferences sPref = context
                .getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        SharedPreferences.Editor editor = sPref.edit();
        editor.remove(key);
        editor.commit();
    }
}
