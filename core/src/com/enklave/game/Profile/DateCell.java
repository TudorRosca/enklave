package com.enklave.game.Profile;

public class DateCell {
    private int cellNumber, cellRateTime,cellRateScrap,cellRateEnergy, cellValue;
    private int cellUsageTime,cellUsageEnergy;
    public DateCell() {
        cellNumber = 0;
        cellRateEnergy = 0;
        cellRateScrap = 0;
        cellRateTime = 0;
        cellValue = 0;
        cellUsageTime = 0;
        cellUsageEnergy = 0;
    }

    public int getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(int cellNumber) {
        this.cellNumber = cellNumber;
    }

    public int getCellRateTime() {
        return cellRateTime;
    }

    public void setCellRateTime(int cellRateTime) {
        this.cellRateTime = cellRateTime;
    }

    public int getCellRateScrap() {
        return cellRateScrap;
    }

    public void setCellRateScrap(int cellRateScrap) {
        this.cellRateScrap = cellRateScrap;
    }

    public int getCellRateEnergy() {
        return cellRateEnergy;
    }

    public void setCellRateEnergy(int cellRateEnergy) {
        this.cellRateEnergy = cellRateEnergy;
    }

    public int getCellValue() {
        return cellValue;
    }

    public void setCellValue(int cellValue) {
        this.cellValue = cellValue;
    }

    public int getCellUsageTime() {
        return cellUsageTime;
    }

    public void setCellUsageTime(int cellUsageTime) {
        this.cellUsageTime = cellUsageTime;
    }

    public int getCellUsageEnergy() {
        return cellUsageEnergy;
    }

    public void setCellUsageEnergy(int cellUsageEnergy) {
        this.cellUsageEnergy = cellUsageEnergy;
    }
}
