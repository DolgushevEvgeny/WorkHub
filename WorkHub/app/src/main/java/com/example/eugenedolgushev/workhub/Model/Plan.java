package com.example.eugenedolgushev.workhub.Model;

public class Plan {
    private String m_planName = "";
    private Integer m_planPrice = 0;

    public void setPlanName(String planName) {
        m_planName = planName;
    }

    public String getPlanName() {
        return m_planName;
    }

    public void setPlanPrice(Integer planPrice) {
        m_planPrice = planPrice;
    }

    public Integer getPlanPrice() {
        return m_planPrice;
    }
}
