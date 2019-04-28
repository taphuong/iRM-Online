package org.irestaurant.irm.Database;

import android.support.annotation.NonNull;

public class PeopleId {
    public String peopleId;
    public <T extends PeopleId> T withId(@NonNull final String id){
        this.peopleId = id;
        return (T) this;
    }
}
