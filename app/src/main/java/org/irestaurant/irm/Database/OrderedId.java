package org.irestaurant.irm.Database;

import android.support.annotation.NonNull;

public class OrderedId {
    public String orderedId;
    public <T extends OrderedId> T withId(@NonNull final String id){
        this.orderedId = id;
        return (T) this;
    }
}
