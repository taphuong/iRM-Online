package org.irestaurant.irm.Database;

public class Revenue extends RevenueId {
    private String date, total;

    public Revenue(String date, String total) {
        this.date = date;
        this.total = total;
    }

    public Revenue() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
