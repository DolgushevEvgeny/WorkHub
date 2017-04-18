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

import static com.example.eugenedolgushev.workhub.DefaultValues.GET_OFFICES;
import static com.example.eugenedolgushev.workhub.DefaultValues.MAIN_URL;
import static com.example.eugenedolgushev.workhub.Utils.hasConnection;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

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
                if (hasConnection(context)) {
                    ListAdapter officeListViewAdapter = lvOffices.getAdapter();
                    Office office = (Office) officeListViewAdapter.getItem(position);

                    Intent intent = new Intent(OfficesActivity.this, OfficeDetailInfoActivity.class);
                    intent.putExtra("cityName", cityName);
                    intent.putExtra("officeName", office.getOfficeName());
                    intent.putExtra("officeAddress", office.getOfficeAddress());
                    intent.putExtra("latitude", office.getLatitude());
                    intent.putExtra("longitude", office.getLongitude());
                    intent.putExtra("office", office);
                    startActivity(intent);
                } else {
                    showAlertDialog("Нет подключения к интернету", context);
                }
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

        getOfficeTask.execute(cityName, MAIN_URL + GET_OFFICES);
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
