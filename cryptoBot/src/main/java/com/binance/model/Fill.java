package com.binance.model;

/**
 * Fill
 */
public class Fill {

    private Double price;
    private Double qty;
    private Double commission;
    private String commissionAsset;
    private int tradeId;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public String getCommissionAsset() {
        return commissionAsset;
    }

    public void setCommissionAsset(String commissionAsset) {
        this.commissionAsset = commissionAsset;
    }

    public int getTradeId() {
        return tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    @Override
    public String toString() {
        return "Fill [commission=" + commission + ", commissionAsset=" + commissionAsset + ", price=" + price + ", qty="
                + qty + ", tradeId=" + tradeId + "]";
    }
}