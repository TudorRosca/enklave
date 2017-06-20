package com.enklave.game.MapsService;


public class FlagSignal {
    private static FlagSignal ourInstance = new FlagSignal();
    private boolean updatedb = false;
    private boolean downloadimg = false;
    private boolean updateDisplay = false;
    private boolean loadCoordonate = false;

    public boolean isLoadCoordonate() {
        return loadCoordonate;
    }

    public void setLoadCoordonate(boolean loadCoordonate) {
        this.loadCoordonate = loadCoordonate;
    }

    public boolean isUpdateDisplay() {
        return updateDisplay;
    }

    public void setUpdateDisplay(boolean updateDisplay) {
        this.updateDisplay = updateDisplay;
    }

    public static FlagSignal getInstance() {
        return ourInstance;
    }

    private FlagSignal() {
    }

    public boolean isUpdatedb() {
        return updatedb;
    }

    public void setUpdatedb(boolean updatedb) {
        this.updatedb = updatedb;
    }

    public boolean isDownloadimg() {
        return downloadimg;
    }

    public void setDownloadimg(boolean downloadimg) {
        this.downloadimg = downloadimg;
    }
}
