package com.demirgroup.skttakip.model;

public class CustomNotificationViewInfo {
    public String productImage;
    public String productName;
    public String sktDate;
    public String productNote;
    public String barcodeNumber;
    public CustomNotificationViewInfo(String productImage, String productName, String sktDate, String barcodeNumber,String productNote) {
        this.productImage = productImage;
        this.productName = productName;
        this.sktDate = sktDate;
        this.barcodeNumber = barcodeNumber;
        this.productNote=productNote;
    }
}
