package com.example.eugenedolgushev.workhub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.eugenedolgushev.workhub.DefaultValues.COLUMN_NAME_CITY;
import static com.example.eugenedolgushev.workhub.DefaultValues.COLUMN_NAME_DATE;
import static com.example.eugenedolgushev.workhub.DefaultValues.COLUMN_NAME_DURATION;
import static com.example.eugenedolgushev.workhub.DefaultValues.COLUMN_NAME_ID;
import static com.example.eugenedolgushev.workhub.DefaultValues.COLUMN_NAME_OFFICE;
import static com.example.eugenedolgushev.workhub.DefaultValues.COLUMN_NAME_OFFICE_ADDRESS;
import static com.example.eugenedolgushev.workhub.DefaultValues.COLUMN_NAME_PLAN;
import static com.example.eugenedolgushev.workhub.DefaultValues.COLUMN_NAME_PLAN_PRICE;
import static com.example.eugenedolgushev.workhub.DefaultValues.COLUMN_NAME_START_TIME;
import static com.example.eugenedolgushev.workhub.DefaultValues.COLUMN_NAME_STATUS;
import static com.example.eugenedolgushev.workhub.DefaultValues.COLUMN_NAME_USER_ID;
import static com.example.eugenedolgushev.workhub.DefaultValues.DB_NAME;
import static com.example.eugenedolgushev.workhub.DefaultValues.DB_VERSION;
import static com.example.eugenedolgushev.workhub.DefaultValues.TABLE_NAME;
import static com.example.eugenedolgushev.workhub.Utils.compareDates;
import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;


public class DBManager extends SQLiteOpenHelper {

    private Context m_context;

    public DBManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        m_context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        String create = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_NAME_CITY + " TEXT, " + COLUMN_NAME_OFFICE + " TEXT, " + COLUMN_NAME_PLAN + " TEXT, " +
                COLUMN_NAME_DATE + " TEXT, " + COLUMN_NAME_START_TIME + " INTEGER, " +
                COLUMN_NAME_DURATION + " INTEGER, " + COLUMN_NAME_PLAN_PRICE + " INTEGER, " +
                COLUMN_NAME_USER_ID + " TEXT, " + COLUMN_NAME_OFFICE_ADDRESS + " TEXT, " + COLUMN_NAME_STATUS + " TEXT);";

        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void setToDB(SQLiteDatabase database, DBManager dbManager, Reservation reservation, Context context) {
        if (database == null) {
            database = dbManager.getWritableDatabase();
        } else if (!database.isOpen()) {
            database = dbManager.getWritableDatabase();
        }

        String selection = "City = ? " + "and Office = ? " + "and Plan = ? " + "and Date = ? " + "and StartTime = ? "
                + "and Duration = ? " + "and PlanPrice = ? " + "and OfficeAddress = ? " + "and Status = ?";

        String[] selectionArgs = new String[]{ reservation.getOfficeCity(),
                reservation.getOfficeName(), reservation.getReservationPlanName(),
                reservation.getReservationDate(), reservation.getStartTime().toString(),
                reservation.getDuration().toString(),
                String.valueOf(reservation.getReservationSum() / reservation.getDuration()),
                reservation.getOfficeAddress(), reservation.getReservationStatus()
        };
        Cursor cursor = database.query(DefaultValues.TABLE_NAME, DefaultValues.COLUMN_PROJECTION, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.getCount() == 0) {
            ContentValues content = new ContentValues();
            content.put(DefaultValues.COLUMN_NAME_CITY, reservation.getOfficeCity());
            content.put(DefaultValues.COLUMN_NAME_OFFICE, reservation.getOfficeName());
            content.put(DefaultValues.COLUMN_NAME_PLAN, reservation.getReservationPlanName());
            content.put(DefaultValues.COLUMN_NAME_DATE, reservation.getReservationDate());
            content.put(DefaultValues.COLUMN_NAME_START_TIME, reservation.getStartTime());
            content.put(DefaultValues.COLUMN_NAME_DURATION, reservation.getDuration());
            content.put(DefaultValues.COLUMN_NAME_PLAN_PRICE, (reservation.getReservationSum() / reservation.getDuration()));
            content.put(DefaultValues.COLUMN_NAME_USER_ID, getStringFromSharedPreferences("userID", context));
            content.put(DefaultValues.COLUMN_NAME_OFFICE_ADDRESS, reservation.getOfficeAddress());
            content.put(DefaultValues.COLUMN_NAME_STATUS, reservation.getReservationStatus());
            long result = database.insert(DefaultValues.TABLE_NAME, null, content);
        }

        if (database != null) {
            database.close();
        }
    }

    public ArrayList<Reservation> getFromDB(SQLiteDatabase database, DBManager dbManager) {
        if (database == null) {
            database = dbManager.getWritableDatabase();
        } else if (!database.isOpen()) {
            database = dbManager.getWritableDatabase();
        }

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        int currentDay = date.getDate();
        int currentMonth = date.getMonth() + 1;
        int currentYear = date.getYear() % 100 + 2000;

        Cursor cursor = database.query(DefaultValues.TABLE_NAME, DefaultValues.COLUMN_PROJECTION, null, null, null, null, null);
        ArrayList<Reservation> reservations = new ArrayList<>();
        if (null != cursor && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                if (!cursor.isAfterLast()) {
                    do {
                        String resDate = cursor.getString(cursor.getColumnIndex(DefaultValues.COLUMN_NAME_DATE));
                        if (compareDates(currentDay, currentMonth, currentYear, resDate)) {
                            Reservation reservation = new Reservation();
                            reservation.setOfficeCity(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CITY)));
                            reservation.setReservationPlanName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PLAN)));
                            reservation.setOfficeName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_OFFICE)));
                            reservation.setReservationDate(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DATE)));
                            reservation.setStartTime(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_START_TIME)));
                            reservation.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DURATION)));
                            reservation.setReservationSum(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DURATION))
                                * cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PLAN_PRICE)));
                            reservation.setOfficeAddress(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_OFFICE_ADDRESS)));
                            reservation.setReservationStatus(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_STATUS)));

                            reservations.add(reservation);
                        }
                    } while (cursor.moveToNext());
                }
            }
        }
        if (database != null) {
            database.close();
            cursor.close();
        }

        return reservations;
    }
}
