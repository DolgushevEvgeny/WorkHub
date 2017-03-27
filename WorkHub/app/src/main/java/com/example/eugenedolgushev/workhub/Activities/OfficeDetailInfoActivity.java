package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eugenedolgushev.workhub.Office;
import com.example.eugenedolgushev.workhub.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.example.eugenedolgushev.workhub.DefaultValues.DAYS_OF_WEEK;

public class OfficeDetailInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context m_context;
    private TextView officeAddressView;
    private Button nextBtn, showWorkListBtn, closeTimeTableBtn;

    private String officeName = "", officeAddress = "", cityName = "";
    private Double officeLatitude = 0.0, officeLongitude = 0.0;
    private Office m_office;
    private LinearLayout m_timeTable, m_timeTableWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_detail_info);
        setTitle("Информация");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        m_context = this;

        cityName = getIntent().getExtras().getString("cityName");
        officeName = getIntent().getExtras().getString("officeName");
        officeAddress = getIntent().getExtras().getString("officeAddress");
        officeLatitude = getIntent().getExtras().getDouble("latitude");
        officeLongitude = getIntent().getExtras().getDouble("longitude");
        m_office = getIntent().getExtras().getParcelable("office");

        m_timeTable = (LinearLayout) findViewById(R.id.work_time_list);
        m_timeTableWrapper = (LinearLayout) findViewById(R.id.timetable_wrapper);

        officeAddressView = (TextView) findViewById(R.id.office_info_address);
        officeAddressView.setText("Офис " + officeName + ", " + officeAddress);

        closeTimeTableBtn = (Button) findViewById(R.id.close_timetable_button);
        closeTimeTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_timeTable.setVisibility(View.GONE);
            }
        });

        showWorkListBtn = (Button) findViewById(R.id.work_time_list_button);
        showWorkListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_timeTable.setVisibility(View.VISIBLE);
            }
        });

        nextBtn = (Button) findViewById(R.id.office_info_next_button);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfficeDetailInfoActivity.this, PlansActivity.class);
                intent.putExtra("cityName", cityName);
                intent.putExtra("officeName", officeName);
                intent.putExtra("officeAddress", officeAddress);
                startActivity(intent);
            }
        });

        setWorkTimeList();
    }

    private void setWorkTimeList() {
        ArrayList<ArrayList<Integer>> workTimeList = m_office.getWorkTimeList();
        for (int i = 0; i < workTimeList.size(); ++i) {
            ArrayList<Integer> workTimeListItem = workTimeList.get(i);
            TextView newTextView = null;
            if (workTimeListItem.size() > 0) {
                newTextView = new AppCompatTextView(m_context);
                newTextView.setVisibility(View.VISIBLE);
                String text = DAYS_OF_WEEK[i] + " : ";
                for (int j = 0; j < workTimeListItem.size(); ++j) {
                    text += workTimeListItem.get(j);
                    text += (j < workTimeListItem.size() - 1) ? " - " : "";
                }
                newTextView.setText(text);
                newTextView.setTextSize(16);
            }
            if (newTextView != null) {
                m_timeTableWrapper.addView(newTextView);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        setMarker(mMap);
    }

    private void setMarker(GoogleMap googleMap) {
        LatLng office = new LatLng(officeLatitude, officeLongitude);
        googleMap.addMarker(new MarkerOptions().position(office).title(officeName));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(office));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(office, 17));
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
