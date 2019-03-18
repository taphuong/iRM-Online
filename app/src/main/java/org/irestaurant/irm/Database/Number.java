package org.irestaurant.irm.Database;

public class Number {
    private int id;
    private String number, status;

//    public Number(String number, String status) {
//        this.number = number;
//        this.status = status;
//    }
//
//    public Number() {
//
//    }

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

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
