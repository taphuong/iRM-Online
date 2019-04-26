package org.irestaurant.irm.Database;

import android.support.annotation.NonNull;

public class NumberId {
    public String numberId;
    public <T extends NumberId> T withId(@NonNull final String id){
        this.numberId = id;
        return (T) this;
    }
}
