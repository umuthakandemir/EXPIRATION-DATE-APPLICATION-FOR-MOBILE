package com.demirgroup.skttakip.model;

public class ProductInfoClass {
    public String productImage;
    public String productname;
    public String productNote;
    public String sktDate,addDate;
    public long remainingdate;
    public String barcodeNumber;
    public String productId;

    public ProductInfoClass(String barcodeNumber,String productImage, String productname, String productNote, String sktDate, String addDate,long remainingdate,String productId) {
        this.productImage = productImage;
        this.productname = productname;
        this.productId = productId;
        this.productNote = productNote;
        this.sktDate = sktDate;
        this.addDate = addDate;
        this.remainingdate = remainingdate;
        this.barcodeNumber = barcodeNumber;
    }
}
