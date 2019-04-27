package org.irestaurant.irm.Database;

public class Ordered extends OrderedId{
    private String foodname, amount, date, time, price, total;

    public Ordered(String foodname, String amount, String date,String time, String price, String total) {
        this.foodname = foodname;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.price = price;
        this.total = total;
    }

    public Ordered() {

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
