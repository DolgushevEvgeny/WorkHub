package com.example.eugenedolgushev.workhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TimeList extends BaseAdapter {
    Context m_context;
    LayoutInflater m_lInflater;
    ArrayList<Time> m_planList;

    public TimeList(Context context) {
        this.m_context = context;
        m_lInflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return m_planList.size();
    }

    @Override
    public Object getItem(int position) {
        return m_planList.get(position);
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
            view = m_lInflater.inflate(R.layout.time_item, parent, false);
        }

        Time time = getTime(position);

        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.time)).setText(time.getTime().toString());

        return view;
    }

    Time getTime(int position) {
        return ((Time) getItem(position));
    }

    public void setList(ArrayList<Time> list) {
        this.m_planList = list;
    }

    public ArrayList<Time> getList() {
        return this.m_planList;
    }
}
