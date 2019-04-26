package org.irestaurant.irm.Database;

public class Food extends FoodId {
    private String foodname, foodprice;

    public Food(String foodname, String foodprice) {
        this.foodname = foodname;
        this.foodprice = foodprice;
    }

    public Food() {
    }

    public String getFoodname() { return foodname; }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getFoodprice() {
        return foodprice;
    }

    public void setFoodprice(String foodprice) {
        this.foodprice = foodprice;
    }
}
