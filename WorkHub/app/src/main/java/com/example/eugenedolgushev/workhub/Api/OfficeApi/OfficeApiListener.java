package com.example.eugenedolgushev.workhub.Api.OfficeApi;

import com.example.eugenedolgushev.workhub.Model.Office;

import java.util.ArrayList;

public interface OfficeApiListener {

    void onOfficesLoad(final ArrayList<Office> offices);
}
