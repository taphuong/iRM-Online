package org.irestaurant.irm.Database;

public class FoodGroup extends FoodGroupId {
    String foodGroup;
    int viewType;

    public FoodGroup(String foodGroup, int viewType) {
        this.foodGroup = foodGroup;
        this.viewType = viewType;
    }

    public FoodGroup() {

    }

    public String getFoodGroup() {
        return foodGroup;
    }

    public void setFoodGroup(String foodGroup) {
        this.foodGroup = foodGroup;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
