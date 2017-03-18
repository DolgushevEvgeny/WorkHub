package com.example.eugenedolgushev.workhub;

public class Office {
    private String m_cityName = "";
    private String m_officeAddress = "";
    private String m_OfficeName = "";
    private Double m_latitude = (double) 0, m_longitude = (double) 0;

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
        m_OfficeName = officeName;
    }

    public String getOfficeName() {
        return m_OfficeName;
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
}
