package com.enklave.game.LoadResursed;

import com.enklave.game.Interfaces.InterfaceAssetsManager;

/**
 * Created by adrian on 27.04.2016.
 */
public class AssertEnklaveScreen implements InterfaceAssetsManager {
    private boolean isupdate = false;

    public AssertEnklaveScreen() {
    }

    @Override
    public boolean update() {
        return isupdate;
    }

    public void setIsupdate(boolean isupdate) {
        this.isupdate = isupdate;
    }
}
