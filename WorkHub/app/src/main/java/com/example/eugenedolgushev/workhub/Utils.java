package com.example.eugenedolgushev.workhub;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    public static boolean compareDates(final int curDay, final int curMonth, final int curYear, final String resDate) {
        String[] values = resDate.split("\\.");
        if (Integer.parseInt(values[2]) > curYear) {
            return true;
        } else if (Integer.parseInt(values[1]) > curMonth) {
            return true;
        } else if (Integer.parseInt(values[0]) >= curDay){
            return true;
        }

        return false;
    }

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static Integer dayOfWeek(final int day, final int month, final int year){
        String days[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int a = (14 - month) / 12, y = year - a, m = month + 12 * a - 2;
        return ((7000 + (day + y + y / 4 - y / 100 + y / 400 + (31 * m) / 12)) % 7) - 1;
    }
}
