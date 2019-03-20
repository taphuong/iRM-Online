package org.irestaurant.irm.Database;

public class Revenue {
    private int id;
    private String date, rdate, time, number, total, discount, totalat;

    public Revenue(String date, String rdate, String time, String number, String total, String discount, String totalat) {
        this.date = date;
        this.rdate = rdate;
        this.time = time;
        this.number = number;
        this.total = total;
        this.discount = discount;
        this.totalat = totalat;
    }

    public Revenue() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRdate() {
        return rdate;
    }

    public void setRdate(String rdate) {
        this.rdate = rdate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTotalat() {
        return totalat;
    }

    public void setTotalat(String totalat) {
        this.totalat = totalat;
    }
}
