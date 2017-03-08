package com.example.eugenedolgushev.workhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReservationList extends BaseAdapter {

    Context m_context;
    LayoutInflater m_lInflater;
    ArrayList<Reservation> m_reservationList;

    public ReservationList(Context context) {
        this.m_context = context;
        m_lInflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return m_reservationList.size();
    }

    @Override
    public Object getItem(int position) {
        return m_reservationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = m_lInflater.inflate(R.layout.reservation_item, parent, false);
        }

        Reservation reservation = (Reservation) getItem(position);

        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.reservation_date)).setText(reservation.getReservationDate());
        ((TextView) view.findViewById(R.id.reservation_start_time)).setText(String.valueOf(reservation.getStartTime()));
        ((TextView) view.findViewById(R.id.reservation_office_address)).setText(reservation.getOfficeName() +
            ", " + reservation.getOfficeAddress());

        return view;
    }

    public void setList(ArrayList<Reservation> list) {
        this.m_reservationList = list;
    }
}
