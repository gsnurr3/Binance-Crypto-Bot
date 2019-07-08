package com.binance.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Order
 */
public class Order {

    private String symbol;
    private int orderId;
    private String clientOrderId;
    private Timestamp transactTime;
    private Double price;
    private Double origQty;
    private Double executedQty;
    private Double cummulativeQuoteQty;
    private String status;
    private String timeInForce;
    private String type;
    private String side; 

    private List<Fill> fills = new ArrayList<>();

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public Timestamp getTransactTime() {
        return transactTime;
    }

    public void setTransactTime(Timestamp transactTime) {
        this.transactTime = transactTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOrigQty() {
        return origQty;
    }

    public void setOrigQty(Double origQty) {
        this.origQty = origQty;
    }

    public Double getExecutedQty() {
        return executedQty;
    }

    public void setExecutedQty(Double executedQty) {
        this.executedQty = executedQty;
    }

    public Double getCummulativeQuoteQty() {
        return cummulativeQuoteQty;
    }

    public void setCummulativeQuoteQty(Double cummulativeQuoteQty) {
        this.cummulativeQuoteQty = cummulativeQuoteQty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public List<Fill> getFills() {
        return fills;
    }

    public void setFills(List<Fill> fills) {
        this.fills = fills;
    }

    @Override
    public String toString() {
        return "Order [clientOrderId=" + clientOrderId + ", cummulativeQuoteQty=" + cummulativeQuoteQty
                + ", executedQty=" + executedQty + ", fills=" + fills + ", orderId=" + orderId + ", origQty=" + origQty
                + ", price=" + price + ", side=" + side + ", status=" + status + ", symbol=" + symbol + ", timeInForce="
                + timeInForce + ", transactTime=" + transactTime + ", type=" + type + "]";
    }
}