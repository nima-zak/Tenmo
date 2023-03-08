package com.techelevator.tenmo.model;

public class TransferStatus {

    private int statusId;
    private String statusDesc;

    public TransferStatus(int statusId, String statusDesc) {
        this.statusId = statusId;
        this.statusDesc = statusDesc;
    }

    public TransferStatus(){}

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }
}
