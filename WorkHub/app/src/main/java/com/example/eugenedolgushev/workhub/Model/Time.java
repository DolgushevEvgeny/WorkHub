package com.example.eugenedolgushev.workhub.Model;

public class Time {
    private Boolean m_isChoose = false;
    private Integer m_timeOfDay = 0;

    public void setTime(Integer timeOfDay) {
        m_timeOfDay = timeOfDay;
    }

    public Integer getTime() {
        return m_timeOfDay;
    }

    public void chooseTime(Boolean choose) {
        m_isChoose = choose;
    }

    public Boolean isTimeChoose() {
        return m_isChoose;
    }
}
