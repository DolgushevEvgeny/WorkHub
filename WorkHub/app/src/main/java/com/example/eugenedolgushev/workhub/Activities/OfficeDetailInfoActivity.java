package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eugenedolgushev.workhub.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class OfficeDetailInfoActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context m_context;
    private TextView officeAddressView;
    private Button nextBtn;

    private String officeName = "", officeAddress = "", cityName = "";
    private Double officeLatitude = 0.0, officeLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_detail_info);
        setTitle("Информация");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cityName = getIntent().getExtras().getString("cityName");
        officeName = getIntent().getExtras().getString("officeName");
        officeAddress = getIntent().getExtras().getString("officeAddress");
        officeLatitude = getIntent().getExtras().getDouble("latitude");
        officeLongitude = getIntent().getExtras().getDouble("longitude");

        officeAddressView = (TextView) findViewById(R.id.office_info_address);
        officeAddressView.setText("Офис " + officeName + ", " + officeAddress);

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
}
