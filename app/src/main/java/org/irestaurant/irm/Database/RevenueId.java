package org.irestaurant.irm.Database;

import android.support.annotation.NonNull;

public class RevenueId {
    public String revenueId;
    public <T extends RevenueId> T withId(@NonNull final String id){
        this.revenueId = id;
        return (T) this;
    }
}
