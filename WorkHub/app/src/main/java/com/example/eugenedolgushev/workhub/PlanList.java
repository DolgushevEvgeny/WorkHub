package com.example.eugenedolgushev.workhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.eugenedolgushev.workhub.Model.Plan;

import java.util.ArrayList;

public class PlanList extends BaseAdapter {

    Context m_context;
    LayoutInflater m_lInflater;
    ArrayList<Plan> m_planList;

    public PlanList(Context context) {
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
            view = m_lInflater.inflate(R.layout.plan_item, parent, false);
        }

        Plan plan = getPlan(position);

        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.plan_name)).setText(plan.getPlanName());
        ((TextView) view.findViewById(R.id.plan_price)).setText(plan.getPlanPrice() + " р/час");

        return view;
    }

    Plan getPlan(int position) {
        return ((Plan) getItem(position));
    }

    public void setList(ArrayList<Plan> list) {
        this.m_planList = list;
    }
}
