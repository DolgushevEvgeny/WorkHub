package com.example.eugenedolgushev.workhub;

public class Reservation {
    private String m_city = "";
    private String m_officeName = "";
    private String m_officeAddress = "";
    private Integer m_startTime = 0;
    private Integer m_reservationSum = 0;
    private Integer m_duration = 0;
    private String m_planName = "";
    private Integer m_reservationDay = 0;
    private Integer m_reservationMonth = 0;
    private Integer m_reservationYear = 0;
    private String m_reservationStatus = "Не оплачено";
    private Boolean m_isPaid = false;

    public void setOfficeCity(String city) {
        m_city = city;
    }

    public String getOfficeCity() {
        return m_city;
    }

    public void setOfficeName(String officeName) {
        m_officeName = officeName;
    }

    public String getOfficeName() {
        return m_officeName;
    }

    public void setOfficeAddress(String officeAddress) {
        m_officeAddress = officeAddress;
    }

    public String getOfficeAddress() {
        return m_officeAddress;
    }

    public void setStartTime(Integer startTime) {
        m_startTime = startTime;
    }

    public Integer getStartTime() {
        return m_startTime;
    }

    public void setReservationSum(Integer sum) {
        m_reservationSum = sum;
    }

    public Integer getReservationSum() {
        return m_reservationSum;
    }

    public void setDuration(Integer duration) {
        m_duration = duration;
    }

    public Integer getDuration() {
        return m_duration;
    }

    public void setReservationPlanName(String planName) {
        m_planName = planName;
    }

    public String getReservationPlanName() {
        return m_planName;
    }

    public void setReservationDay(Integer day) {
        m_reservationDay = day;
    }

    public Integer getReservationDay() {
        return m_reservationDay;
    }

    public void setReservationMonth(Integer month) {
        m_reservationMonth = month;
    }

    public Integer getReservationMonth() {
        return m_reservationMonth;
    }

    public void setReservationYear(Integer year) {
        m_reservationYear = year;
    }

    public Integer getReservationYear() {
        return m_reservationYear;
    }

    public void setReservationDate(String date) {
        String[] values = date.split("\\.");
        setReservationDay(Integer.parseInt(values[0]));
        setReservationMonth(Integer.parseInt(values[1]));
        setReservationYear(Integer.parseInt(values[2]));
    }

    public String getReservationDate() {
        return "" + m_reservationDay + "." + m_reservationMonth + "." + m_reservationYear;
    }

    public void setReservationStatus(String status) {
        m_reservationStatus = status;
    }

    public String getReservationStatus() {
        return m_reservationStatus;
    }

    public void setReservationPaid(Boolean paid) {
        m_isPaid = paid;
    }

    public Boolean isReservationPaid() {
        return m_isPaid;
    }
}
