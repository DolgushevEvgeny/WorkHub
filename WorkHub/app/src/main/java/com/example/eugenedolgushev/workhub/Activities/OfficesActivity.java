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

import com.example.eugenedolgushev.workhub.AsyncTasks.GetOffices;
import com.example.eugenedolgushev.workhub.Office;
import com.example.eugenedolgushev.workhub.OfficeList;
import com.example.eugenedolgushev.workhub.R;

public class OfficesActivity extends AppCompatActivity {
    private ListView lvOffices;
    private Context context;

    private static final String URL = "http://192.168.0.32:3000/getOffices";
    private String cityName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offices);
        setTitle("Выберите офис");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cityName = getIntent().getExtras().getString("cityName");

        lvOffices = (ListView) findViewById(R.id.lvMain);
        context = this;

        lvOffices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListAdapter officeListViewAdapter = lvOffices.getAdapter();
                Office office = (Office) officeListViewAdapter.getItem(position);

                Intent intent = new Intent(OfficesActivity.this, PlansActivity.class);
                intent.putExtra("cityName", cityName);
                intent.putExtra("officeName", office.getOfficeName());
                startActivity(intent);
            }
        });

        GetOffices getOfficeTask = new GetOffices(new GetOffices.AsyncResponse() {
            @Override
            public void processFinish(OfficeList officeList){
                if (lvOffices != null && officeList != null) {
                    lvOffices.setAdapter(officeList);
                }
            }
        }, context);

        getOfficeTask.execute(cityName, URL);
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
