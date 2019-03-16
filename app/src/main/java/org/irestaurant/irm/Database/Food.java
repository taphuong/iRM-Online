package org.irestaurant.irm.Database;

public class Food {
    private int id;
    private String foondname, foodprice;

    public Food() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFoondname() {
        return foondname;
    }

    public void setFoondname(String foondname) {
        this.foondname = foondname;
    }

    public String getFoodprice() {
        return foodprice;
    }

    public void setFoodprice(String foodprice) {
        this.foodprice = foodprice;
    }
}
