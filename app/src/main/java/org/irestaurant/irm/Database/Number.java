package org.irestaurant.irm.Database;

import java.util.List;

public class Number extends NumberId {
    private String number, status;

    public Number(String number, String status) {
        this.number = number;
        this.status = status;
    }

    public Number() {

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
