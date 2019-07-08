package com.binance.model;

/**
 * Balance
 */
public class Balance {

    private String asset;
    private Double free;
    private Double locked;

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public Double getFree() {
        return free;
    }

    public void setFree(Double free) {
        this.free = free;
    }

    public Double getLocked() {
        return locked;
    }

    public void setLocked(Double locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return "Balance [asset=" + asset + ", free=" + free + ", locked=" + locked + "]";
    }
}