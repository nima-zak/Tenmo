package com.techelevator.tenmo.model;

public class TransferType {

    private int typeId;
    private String typeDesc; //description

    public TransferType(int typeId, String typeDesc) {
        this.typeId = typeId;
        this.typeDesc = typeDesc;
    }

    public TransferType() {

    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTransferTypeDesc() {
        return typeDesc;
    }

    public void setTransferTypeDesc(String transferTypeDesc) {
        this.typeDesc = transferTypeDesc;
    }
}
