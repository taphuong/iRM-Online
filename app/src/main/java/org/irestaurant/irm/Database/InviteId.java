package org.irestaurant.irm.Database;

import android.support.annotation.NonNull;

public class InviteId {
    public String inviteId;
    public <T extends InviteId> T withId(@NonNull final String id){
        this.inviteId = id;
        return (T) this;
    }
}
