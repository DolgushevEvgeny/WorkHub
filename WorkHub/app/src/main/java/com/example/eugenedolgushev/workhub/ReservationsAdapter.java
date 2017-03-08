package com.example.eugenedolgushev.workhub;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView m_TextView1;
        public TextView m_TextView2;
        public Button m_deleteItemBtn;

        public ViewHolder(View v) {
            super(v);
            m_TextView1 = (TextView) v.findViewById(R.id.test1);
            m_TextView2 = (TextView) v.findViewById(R.id.test2);
            m_deleteItemBtn = (Button) v.findViewById(R.id.delete_item);
        }
    }

    MyOnClick m_onClickListener;
    ArrayList<Reservation> m_reservationList;

    public ReservationsAdapter(MyOnClick onClickListener, ArrayList<Reservation> reservations) {
        m_onClickListener = onClickListener;
        m_reservationList = reservations;
    }

    @Override
    public ReservationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ReservationsAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return m_reservationList.size();
    }

    public void setList(ArrayList<Reservation> list) {
        m_reservationList = list;
        notifyDataSetChanged();
    }
}
