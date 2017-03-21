package com.example.eugenedolgushev.workhub;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView m_reservationDateView, m_reservationStartTimeView,
                        m_reservationOfficeAddressView, m_reservationDateMoreView,
                        m_reservationStartTimeMoreView, m_reservationDurationMoreView,
                        m_reservationOfficeAddressMoreView, m_reservationStatusMoreView;
        public Button m_moreInfoBtn, m_lessInfoBtn;
        public LinearLayout m_reservationLessInfo, m_reservationMoreInfo;

        public ViewHolder(View v) {
            super(v);
            m_reservationDateView = (TextView) v.findViewById(R.id.reservation_date);
            //m_reservationStartTimeView = (TextView) v.findViewById(R.id.reservation_start_time);
            //m_reservationOfficeAddressView = (TextView) v.findViewById(R.id.reservation_office_address);
            m_moreInfoBtn = (Button) v.findViewById(R.id.reservation_more_info_button);
            m_moreInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_reservationLessInfo.setVisibility(View.GONE);
                    m_reservationMoreInfo.setVisibility(View.VISIBLE);
                }
            });

            m_reservationDateMoreView = (TextView) v.findViewById(R.id.reservation_date_more);
            m_reservationStartTimeMoreView = (TextView) v.findViewById(R.id.reservation_start_time_more);
            m_reservationOfficeAddressMoreView = (TextView) v.findViewById(R.id.reservation_office_address_more);
            m_reservationStatusMoreView = (TextView) v.findViewById(R.id.reservation_status_more);
            m_lessInfoBtn = (Button) v.findViewById(R.id.reservation_less_info_button);
            m_lessInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_reservationMoreInfo.setVisibility(View.GONE);
                    m_reservationLessInfo.setVisibility(View.VISIBLE);
                }
            });

            m_reservationLessInfo = (LinearLayout) v.findViewById(R.id.less_info_item);
            m_reservationMoreInfo = (LinearLayout) v.findViewById(R.id.more_info_item);
        }
    }

    private ArrayList<Reservation> m_reservationList;

    public ReservationsAdapter(ArrayList<Reservation> reservations) {
        m_reservationList = reservations;
    }

    @Override
    public ReservationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReservationsAdapter.ViewHolder holder, int position) {
        holder.m_reservationDateView.setText(createDate(position));

        holder.m_reservationDateMoreView.setText(createDate(position));
        holder.m_reservationStartTimeMoreView.setText("Время посещения с " +
                m_reservationList.get(position).getStartTime().toString() + " до " +
                (m_reservationList.get(position).getStartTime() + m_reservationList.get(position).getDuration()));
        holder.m_reservationOfficeAddressMoreView.setText("Офис " + m_reservationList.get(position).getOfficeName() + ",  " +
                m_reservationList.get(position).getOfficeAddress());
        holder.m_reservationStatusMoreView.setText(m_reservationList.get(position).getReservationStatus());
    }

    @Override
    public int getItemCount() {
        return m_reservationList.size();
    }

    public void setList(ArrayList<Reservation> list) {
        m_reservationList = list;
        notifyDataSetChanged();
    }

    private String createDate(final int position) {
        return dayOfWeek(m_reservationList.get(position).getReservationDay(),
                m_reservationList.get(position).getReservationMonth(), m_reservationList.get(position).getReservationYear()) +
                ", " + m_reservationList.get(position).getReservationDay() + " " +
                getMonthFromDate(m_reservationList.get(position).getReservationMonth()) + " " +
                m_reservationList.get(position).getReservationYear();
    }

    private static String getMonthFromDate(int month) {
        String months[] = {"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа",
                            "Сентября", "Октября", "Ноября", "Декабря"};
        return months[month];
    }

    public static String dayOfWeek(int day, int month, int year){
        String days[] = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
        int a = (14 - month) / 12, y = year - a, m = month + 12 * a - 2;
        return days[((7000 + (day + y + y / 4 - y / 100 + y / 400 + (31 * m) / 12)) % 7) - 1];
    }
}
