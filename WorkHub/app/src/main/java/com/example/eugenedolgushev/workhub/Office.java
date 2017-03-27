package com.example.eugenedolgushev.workhub;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Office implements Parcelable{
    private String m_cityName = "";
    private String m_officeAddress = "";
    private String m_officeName = "";
    private Double m_latitude = (double) 0, m_longitude = (double) 0;
    private ArrayList<ArrayList<Integer>> m_workTimeList = new ArrayList<>();

    public Office() {}

    private Office(Parcel parcel) {
        m_cityName = parcel.readString();
        m_officeAddress = parcel.readString();
        m_officeName = parcel.readString();
        m_latitude = parcel.readDouble();
        m_longitude = parcel.readDouble();
        m_workTimeList = parcel.readArrayList(ClassLoader.getSystemClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(m_cityName);
        parcel.writeString(m_officeAddress);
        parcel.writeString(m_officeName);
        parcel.writeDouble(m_latitude);
        parcel.writeDouble(m_longitude);
        parcel.writeList(m_workTimeList);
    }

    public static final Creator<Office> CREATOR = new Creator<Office>() {
        @Override
        public Office createFromParcel(Parcel in) {
            return new Office(in);
        }

        @Override
        public Office[] newArray(int size) {
            return new Office[size];
        }
    };

    public void setCityName(String cityName) {
        m_cityName = cityName;
    }

    public String getCityName() {
        return m_cityName;
    }

    public void setOfficeAddress(String officeAddress) {
        m_officeAddress = officeAddress;
    }

    public String getOfficeAddress() {
        return m_officeAddress;
    }

    public void setOfficeName(String officeName) {
        m_officeName = officeName;
    }

    public String getOfficeName() {
        return m_officeName;
    }

    public void setLatitude(Double latitude) {
        m_latitude = latitude;
    }

    public Double getLatitude() {
        return m_latitude;
    }

    public void setLongitude(Double longitude) {
        m_longitude = longitude;
    }

    public Double getLongitude() {
        return m_longitude;
    }

    public void setWorkTimeList(ArrayList<ArrayList<Integer>> workTimeList) {
        m_workTimeList = workTimeList;
    }

    public ArrayList<ArrayList<Integer>> getWorkTimeList() {
        return m_workTimeList;
    }
}
