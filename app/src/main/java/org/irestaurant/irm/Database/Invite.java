package org.irestaurant.irm.Database;

public class Invite extends InviteId {
    private String resemail, resname, date;

    public Invite() {
    }

    public String getResemail() {
        return resemail;
    }

    public void setResemail(String resemail) {
        this.resemail = resemail;
    }

    public String getResname() {
        return resname;
    }

    public void setResname(String resname) {
        this.resname = resname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
