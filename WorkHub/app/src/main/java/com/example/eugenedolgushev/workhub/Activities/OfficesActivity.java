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

import com.example.eugenedolgushev.workhub.Api.OfficeApi.OfficeApi;
import com.example.eugenedolgushev.workhub.Api.OfficeApi.OfficeApiListener;
import com.example.eugenedolgushev.workhub.Model.Office;
import com.example.eugenedolgushev.workhub.OfficeList;
import com.example.eugenedolgushev.workhub.R;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.hasConnection;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class OfficesActivity extends AppCompatActivity {
    private ListView lvOffices;
    private Context context;

    private String cityName = "";
    private OfficeApi officeApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offices);
        setTitle("Выберите офис");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        officeApi = OfficeApi.getInstance();

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

        getOffices();
    }

    private void getOffices() {
        RequestParams params = new RequestParams();
        params.put("userID", getStringFromSharedPreferences("userID", context));
        params.put("city", cityName);
        officeApi.getOffices(params, new OfficeApiListener() {
            @Override
            public void onOfficesLoad(ArrayList<Office> offices) {
                OfficeList officeList = new OfficeList(context);
                officeList.setList(offices);
                lvOffices.setAdapter(officeList);
            }
        }, context);
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
