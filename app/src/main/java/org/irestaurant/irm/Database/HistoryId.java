package org.irestaurant.irm.Database;

import android.support.annotation.NonNull;

public class HistoryId {
    public String historyId;
    public <T extends HistoryId> T withId(@NonNull final String id){
        this.historyId = id;
        return (T) this;
    }
}
