package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Transfer {
    private long transferId;
    private long transferTypeId;
    private long transferStatusId;
    private long accountFrom;
    private long accountTo;
    private BigDecimal amount;
    private long userToId;
    private long userFromId;
    private String userToName;
    private String userFromName;


    public Transfer(long transferId, long transferTypeId, long transferStatusId, long accountFrom, long accountTo, BigDecimal amount) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;

    }

    public Transfer() {}

    public String getTransferTypeDescription(){
        String description = "";
        if(transferTypeId == 1){
            description = "Request";
        } else if (transferTypeId == 2){
            description = "Send";
        }
        return description;
    }

    public String getTransferStatusDescription(){
        String description = "";
        if(transferStatusId == 1){
            description = "Pending";
        } else if (transferStatusId == 2){
            description = "Approved";
        } else if (transferStatusId == 3){
            description = "Rejected";
        }
        return description;
    }


    public long getUserToId() {
        return userToId;
    }

    public void setUserToId(long userToId) {
        this.userToId = userToId;
    }

    public long getUserFromId() {
        return userFromId;
    }

    public void setUserFromId(long userFromId) {
        this.userFromId = userFromId;
    }

    public String getUserToName() {
        return userToName;
    }

    public void setUserToName(String userToName) {
        this.userToName = userToName;
    }

    public String getUserFromName() {
        return userFromName;
    }

    public void setUserFromName(String userFromName) {
        this.userFromName = userFromName;
    }

    public long getTransferId() {
        return transferId;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public long getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(long transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public long getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(long transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public long getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(long accountFrom) {
        this.accountFrom = accountFrom;
    }

    public long getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(long accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "--------------------------------------------\n" +
                "Transfer Details\n" +
                "--------------------------------------------\n" +
                "Id: " + transferId +
                "\nFrom: " + userFromName +
                "\nTo: " + userToName +
                "\nType: " + getTransferTypeDescription() +
                "\nStatus: " + getTransferStatusDescription() +
                "\nAmount: $" + amount +"\n";
    }
}
