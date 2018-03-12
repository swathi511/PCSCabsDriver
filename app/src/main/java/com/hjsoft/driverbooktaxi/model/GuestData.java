package com.hjsoft.driverbooktaxi.model;

import java.io.Serializable;

/**
 * Created by hjsoft on 18/11/16.
 */
public class GuestData implements Serializable{

    String gName,gMobile,gPickup,gDrop,pLat,pLng,dLat,dLng,gRequestId,gProfileId,travelType,travelPackage,scheduledDate,scheduledTime,otpRequired,bookingType,paymentMode,otherCharges,appPickupLat,appPickupLong,rideStartTime;

    public GuestData(String gRequestId,String gProfileId,String gName,String gMobile,String pLat,
                     String pLng,String dLat,String dLng,String gPickup,String gDrop,String travelType,String travelPackage,String scheduledDate,String scheduledTime,
                     String otpRequired,String bookingType,String paymentMode,String otherCharges,String appPickupLat,String appPickupLong,String rideStartTime)
    {
       this.gRequestId=gRequestId;
        this.gProfileId=gProfileId;
        this.gName=gName;
        this.gMobile=gMobile;
        this.pLat=pLat;
        this.pLng=pLng;
        this.dLat=dLat;
        this.dLng=dLng;
        this.gPickup=gPickup;
        this.gDrop=gDrop;
        this.travelType=travelType;
        this.travelPackage=travelPackage;
        this.scheduledDate=scheduledDate;
        this.scheduledTime=scheduledTime;
        this.otpRequired=otpRequired;
        this.bookingType=bookingType;
        this.paymentMode=paymentMode;
        this.otherCharges=otherCharges;
        this.appPickupLat=appPickupLat;
        this.appPickupLong=appPickupLong;
        this.rideStartTime=rideStartTime;
    }


    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getgMobile() {
        return gMobile;
    }

    public void setgMobile(String gMobile) {
        this.gMobile = gMobile;
    }

    public String getgPickup() {
        return gPickup;
    }

    public void setgPickup(String gPickup) {
        this.gPickup = gPickup;
    }

    public String getgDrop() {
        return gDrop;
    }

    public void setgDrop(String gDrop) {
        this.gDrop = gDrop;
    }

    public String getpLat() {
        return pLat;
    }

    public void setpLat(String pLat) {
        this.pLat = pLat;
    }

    public String getpLng() {
        return pLng;
    }

    public void setpLng(String pLng) {
        this.pLng = pLng;
    }

    public String getdLat() {
        return dLat;
    }

    public void setdLat(String dLat) {
        this.dLat = dLat;
    }

    public String getdLng() {
        return dLng;
    }

    public void setdLng(String dLng) {
        this.dLng = dLng;
    }

    public String getgRequestId() {
        return gRequestId;
    }

    public void setgRequestId(String gRequestId) {
        this.gRequestId = gRequestId;
    }

    public String getgProfileId() {
        return gProfileId;
    }

    public void setgProfileId(String gProfileId) {
        this.gProfileId = gProfileId;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public String getTravelPackage() {
        return travelPackage;
    }

    public void setTravelPackage(String travelPackage) {
        this.travelPackage = travelPackage;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getOtpRequired() {
        return otpRequired;
    }

    public void setOtpRequired(String otpRequired) {
        this.otpRequired = otpRequired;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(String otherCharges) {
        this.otherCharges = otherCharges;
    }

    public String getAppPickupLat() {
        return appPickupLat;
    }

    public void setAppPickupLat(String appPickupLat) {
        this.appPickupLat = appPickupLat;
    }

    public String getAppPickupLong() {
        return appPickupLong;
    }

    public void setAppPickupLong(String appPickupLong) {
        this.appPickupLong = appPickupLong;
    }

    public String getRideStartTime() {
        return rideStartTime;
    }

    public void setRideStartTime(String rideStartTime) {
        this.rideStartTime = rideStartTime;
    }
}
