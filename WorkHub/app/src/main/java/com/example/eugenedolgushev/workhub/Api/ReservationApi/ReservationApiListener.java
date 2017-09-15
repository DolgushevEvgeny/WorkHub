package com.example.eugenedolgushev.workhub.Api.ReservationApi;

import com.example.eugenedolgushev.workhub.Reservation;

import java.util.ArrayList;

public interface ReservationApiListener {

    void onReservationsLoad(final ArrayList<Reservation> reservations);
}
