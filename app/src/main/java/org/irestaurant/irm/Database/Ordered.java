package org.irestaurant.irm.Database;

public class Ordered {
    private int id;
    private String number, foodname, amount, status, date, time, price, total;

    public Ordered(String number, String foodname, String amount, String status, String date, String price, String total) {
        this.number = number;
        this.foodname = foodname;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.price = price;
        this.total = total;
    }

    public Ordered() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getPrice() { return price; }

    public void setPrice(String price) { this.price = price; }

    public String getTotal() { return total; }

    public void setTotal(String total) { this.total = total; }
}
