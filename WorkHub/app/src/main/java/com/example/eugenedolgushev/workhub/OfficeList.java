package com.example.eugenedolgushev.workhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class OfficeList extends BaseAdapter {

    Context m_context;
    LayoutInflater m_lInflater;
    ArrayList<Office> m_officeList;

    public OfficeList(Context context) {
        this.m_context = context;
        m_lInflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return m_officeList.size();
    }

    @Override
    public Object getItem(int position) {
        return m_officeList.get(position);
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
            view = m_lInflater.inflate(R.layout.office_item, parent, false);
        }

        Office office = getOffice(position);

        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.city)).setText(office.getCityName());
        ((TextView) view.findViewById(R.id.office_name)).setText("\"" + office.getOfficeName() + "\"");
        ((TextView) view.findViewById(R.id.office_address)).setText(office.getOfficeAddress());

        return view;
    }

    Office getOffice(int position) {
        return ((Office) getItem(position));
    }

    public void setList(ArrayList<Office> list) {
        this.m_officeList = list;
    }
}
