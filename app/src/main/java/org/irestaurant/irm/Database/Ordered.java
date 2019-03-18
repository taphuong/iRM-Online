package org.irestaurant.irm.Database;

public class Ordered {
    private int id;
    private String number, foodname, amount, status, date;

    public Ordered(String number, String foodname, String amount, String status, String date) {
        this.number = number;
        this.foodname = foodname;
        this.amount = amount;
        this.status = status;
        this.date = date;
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
}
