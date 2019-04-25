package org.irestaurant.irm.Database;

public class Food {
    private String foondname, foodprice;

    public Food(String foondname, String foodprice) {
        this.foondname = foondname;
        this.foodprice = foodprice;
    }

    public Food() {
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
