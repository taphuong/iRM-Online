package org.irestaurant.irm.Database;

import android.support.annotation.NonNull;

public class FoodId {
    public String foodId;
    public <T extends FoodId> T withId(@NonNull final String id){
        this.foodId = id;
        return (T) this;
    }
}
