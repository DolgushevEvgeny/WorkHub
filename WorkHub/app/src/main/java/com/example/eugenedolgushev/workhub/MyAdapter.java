package com.example.eugenedolgushev.workhub;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public Button deleteItemBtn;
        public ViewHolder(View v) {
            super(v);
            mTextView1 = (TextView) v.findViewById(R.id.test1);
            mTextView2 = (TextView) v.findViewById(R.id.test2);
            mTextView3 = (TextView) v.findViewById(R.id.test3);
            deleteItemBtn = (Button) v.findViewById(R.id.delete_item);
        }
    }

    MyOnClick m_onClickListener;
    ArrayList<Reservation> m_reservationList;

    public MyAdapter(MyOnClick onClickListener, ArrayList<Reservation> reservations) {
        m_onClickListener = onClickListener;
        m_reservationList = reservations;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item_2, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        final int myPosition = position;
        holder.mTextView1.setText("Дата - " + m_reservationList.get(position).getReservationDate());
        holder.mTextView2.setText("Время посещения с " + m_reservationList.get(position).getStartTime().toString()
                + " до " + String.valueOf(m_reservationList.get(position).getStartTime() +
                m_reservationList.get(position).getDuration()));
        holder.mTextView3.setText("Стоимость - " + m_reservationList.get(position).getReservationSum());
        holder.deleteItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_onClickListener.onClick(m_reservationList.get(myPosition));
            }
        });
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
