package com.techelevator.tenmo.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class NewTransferDTO {


    @NotNull(message = "User Id for other user must not be Null.")
    private long otherUserId;
    @Positive (message = "Transfer amount cannot be negative.")
    @NotNull (message = "Transfer amount cannot be Null.")
    private BigDecimal transferAmount;

    public NewTransferDTO(long OtherUserId, BigDecimal transferAmount) {
        this.otherUserId = OtherUserId;
        this.transferAmount = transferAmount;
    }

    public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long OtherUserId) {
        this.otherUserId = OtherUserId;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }
}
