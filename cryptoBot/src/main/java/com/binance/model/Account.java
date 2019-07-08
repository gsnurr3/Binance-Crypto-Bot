package com.binance.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Account
 */
public class Account {

    private Double makerCommission;
    private Double takerCommission;
    private Double buyerCommission;
    private Double sellerCommission;
    private Boolean canTrade;
    private Boolean canWithdraw;
    private Boolean canDeposit;
    private Timestamp updateTime;
    private String accountType;
    List<Balance> balances = new ArrayList<>();

    public Double getMakerCommission() {
        return makerCommission;
    }

    public void setMakerCommission(Double makerCommission) {
        this.makerCommission = makerCommission;
    }

    public Double getTakerCommission() {
        return takerCommission;
    }

    public void setTakerCommission(Double takerCommission) {
        this.takerCommission = takerCommission;
    }

    public Double getBuyerCommission() {
        return buyerCommission;
    }

    public void setBuyerCommission(Double buyerCommission) {
        this.buyerCommission = buyerCommission;
    }

    public Double getSellerCommission() {
        return sellerCommission;
    }

    public void setSellerCommission(Double sellerCommission) {
        this.sellerCommission = sellerCommission;
    }

    public Boolean getCanTrade() {
        return canTrade;
    }

    public void setCanTrade(Boolean canTrade) {
        this.canTrade = canTrade;
    }

    public Boolean getCanWithdraw() {
        return canWithdraw;
    }

    public void setCanWithdraw(Boolean canWithdraw) {
        this.canWithdraw = canWithdraw;
    }

    public Boolean getCanDeposit() {
        return canDeposit;
    }

    public void setCanDeposit(Boolean canDeposit) {
        this.canDeposit = canDeposit;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    @Override
    public String toString() {
        return "Account [accountType=" + accountType + ", balances=" + balances + ", buyerCommission=" + buyerCommission
                + ", canDeposit=" + canDeposit + ", canTrade=" + canTrade + ", canWithdraw=" + canWithdraw
                + ", makerCommission=" + makerCommission + ", sellerCommission=" + sellerCommission
                + ", takerCommission=" + takerCommission + ", updateTime=" + updateTime + "]";
    }
}