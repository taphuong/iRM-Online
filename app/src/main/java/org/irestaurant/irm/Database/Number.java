package org.irestaurant.irm.Database;

import java.util.List;

public class Number extends NumberId {
    private String number, status, total;

    public Number(String number, String status, String total) {
        this.number = number;
        this.status = status;
        this.total = total;
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

    public String getTotal() { return total; }

    public void setTotal(String total) { this.total = total; }
}
