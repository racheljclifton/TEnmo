package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class NewTransfer {

    private long otherUserId;
    private BigDecimal transferAmount;

    public NewTransfer(long otherUserId, BigDecimal transferAmount) {
        this.otherUserId = otherUserId;
        this.transferAmount = transferAmount;
    }

    public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }
}
