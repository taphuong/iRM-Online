package org.irestaurant.irm.Database;

import android.support.annotation.NonNull;

public class FoodGroupId {
    public String foodGroupId;
    public <T extends FoodGroupId> T withId(@NonNull final String id){
        this.foodGroupId = id;
        return (T) this;
    }
}
