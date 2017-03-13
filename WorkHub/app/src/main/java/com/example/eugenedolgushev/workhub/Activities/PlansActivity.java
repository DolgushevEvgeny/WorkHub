package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.eugenedolgushev.workhub.AsyncTasks.GetPlans;
import com.example.eugenedolgushev.workhub.Plan;
import com.example.eugenedolgushev.workhub.PlanList;
import com.example.eugenedolgushev.workhub.R;

public class PlansActivity extends AppCompatActivity {

    private static final String URL = "http://192.168.0.32:3000/getPlans";
    private String officeName = "", cityName = "", officeAddress = "";

    private Context m_context = null;
    private ListView planListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);
        setTitle("Выберите план");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        officeName = getIntent().getExtras().getString("officeName");
        cityName = getIntent().getExtras().getString("cityName");
        officeAddress = getIntent().getExtras().getString("officeAddress");

        m_context = this;

        planListView = (ListView) findViewById(R.id.planActivityListView);
        planListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListAdapter planListViewAdapter = planListView.getAdapter();
                Plan plan = (Plan) planListViewAdapter.getItem(position);
                Intent intent = new Intent(PlansActivity.this, ChooseDaysActivity.class);
                intent.putExtra("cityName", cityName);
                intent.putExtra("officeName", officeName);
                intent.putExtra("officeAddress", officeAddress);
                intent.putExtra("planPrice", plan.getPlanPrice());
                intent.putExtra("planName", plan.getPlanName());
                startActivity(intent);
            }
        });

        GetPlans getPlansTask = new GetPlans(new GetPlans.AsyncResponse() {
            @Override
            public void processFinish(PlanList planList) {
                if (planListView != null && planList != null) {
                    planListView.setAdapter(planList);
                }
            }
        }, m_context);

        getPlansTask.execute(cityName, officeName, URL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
