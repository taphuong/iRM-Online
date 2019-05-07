package org.irestaurant.irm.Database;

public class Food extends FoodId {
    private String foodname, foodprice, group;
    int viewType;

    public Food(String foodname, String foodprice, String group, int viewType) {
        this.foodname = foodname;
        this.foodprice = foodprice;
        this.group = group;
        this.viewType = viewType;
    }

    public Food(String group) {
        this.group = group;
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

    public String getGroup() { return group; }

    public void setGroup(String group) { this.group = group; }

    public int getViewType() { return viewType; }

    public void setViewType(int viewType) { this.viewType = viewType; }
}
