package com.example.eugenedolgushev.workhub;

public class DefaultValues {

    public static final String SHARED_PREFERENCES_NAME = "MyStorage";
    public static final String MAIN_URL = "http://192.168.0.32:3000/";
    public static final String LOGIN_URL = "login";
    public static final String REMOVE_RESERVATION_URL = "removeReservation";
    public static final String CAN_TAKE_PLACE_URL = "canTakePlace";
    public static final String MY_RESERVATIONS_URL = "MyReservations";
    public static final String CHANGE_PASSWORD_URL = "changePassword";
    public static final String GET_CITIES_URL = "getCities";
    public static final Integer NOTIFICATION_DELAY = 1;

    public static final String DB_NAME = "reservations.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "Reservations";
    public static final String COLUMN_NAME_ID = "ID";
    public static final String COLUMN_NAME_CITY = "City";
    public static final String COLUMN_NAME_OFFICE = "Office";
    public static final String COLUMN_NAME_PLAN = "Plan";
    public static final String COLUMN_NAME_DATE = "Date";
    public static final String COLUMN_NAME_START_TIME = "StartTime";
    public static final String COLUMN_NAME_DURATION = "Duration";
    public static final String COLUMN_NAME_PLAN_PRICE = "PlanPrice";
    public static final String COLUMN_NAME_USER_ID = "UserID";
    public static final String COLUMN_NAME_OFFICE_ADDRESS = "OfficeAddress";
    public static final String COLUMN_NAME_STATUS = "Status";

    public static String[] COLUMN_PROJECTION = {
            COLUMN_NAME_ID, COLUMN_NAME_CITY, COLUMN_NAME_OFFICE, COLUMN_NAME_PLAN, COLUMN_NAME_DATE,
            COLUMN_NAME_START_TIME, COLUMN_NAME_DURATION, COLUMN_NAME_PLAN_PRICE, COLUMN_NAME_USER_ID,
            COLUMN_NAME_OFFICE_ADDRESS, COLUMN_NAME_STATUS
    };

    public static String[] DAYS_OF_WEEK = {"Понедельник", "Вторник", "Среда", "Четверг",
            "Пятница", "Суббота", "Воскресенье"};
}
