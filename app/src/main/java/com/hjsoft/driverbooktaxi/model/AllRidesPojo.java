package com.hjsoft.driverbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by hjsoft on 21/1/17.
 */
public class AllRidesPojo implements Serializable {

    @SerializedName("requestid")
    @Expose
    private String requestid;
    @SerializedName("ridedate")
    @Expose
    private String ridedate;
    @SerializedName("fromlocation")
    @Expose
    private String fromlocation;
    @SerializedName("tolocation")
    @Expose
    private String tolocation;
    @SerializedName("vehicle_category")
    @Expose
    private String vehicleCategory;
    @SerializedName("vehicle_type")
    @Expose
    private String vehicleType;
    @SerializedName("distancetravelled")
    @Expose
    private String distancetravelled;
    @SerializedName("statusofride")
    @Expose
    private String statusofride;
    @SerializedName("ridestarttime")
    @Expose
    private String ridestarttime;
    @SerializedName("ridestoptime")
    @Expose
    private String ridestoptime;
    @SerializedName("totalamount")
    @Expose
    private String totalamount;
    @SerializedName("drivername")
    @Expose
    private String drivername;
    @SerializedName("driverpic")
    @Expose
    private String driverpic;
    @SerializedName("travelType")
    @Expose
    private String travelType;
    @SerializedName("bookingType")
    @Expose
    private String bookingType;
    @SerializedName("travelpackage")
    @Expose
    private String travelpackage;
    @SerializedName("drivermobile")
    @Expose
    private String drivermobile;
    @SerializedName("guestProfileId")
    @Expose
    private String guestProfileId;
    @SerializedName("guestName")
    @Expose
    private String guestName;
    @SerializedName("guestMobile")
    @Expose
    private String guestMobile;
    @SerializedName("pickupLatitude")
    @Expose
    private String pickupLatitude;
    @SerializedName("pickupLongitude")
    @Expose
    private String pickupLongitude;
    @SerializedName("DropLatitude")
    @Expose
    private String dropLatitude;
    @SerializedName("DropLongitude")
    @Expose
    private String dropLongitude;
    @SerializedName("OTPStatus")
    @Expose
    private String oTPStatus;
    @SerializedName("DriverBattaAmt")
    @Expose
    private String driverBattaAmt;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("othercharges")
    @Expose
    private String otherCharges;
    @SerializedName("pickup_lat")
    @Expose
    private String pickupLat;
    @SerializedName("pickup_long")
    @Expose
    private String pickupLong;



    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    public String getRidedate() {
        return ridedate;
    }

    public void setRidedate(String ridedate) {
        this.ridedate = ridedate;
    }

    public String getFromlocation() {
        return fromlocation;
    }

    public void setFromlocation(String fromlocation) {
        this.fromlocation = fromlocation;
    }

    public String getTolocation() {
        return tolocation;
    }

    public void setTolocation(String tolocation) {
        this.tolocation = tolocation;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDistancetravelled() {
        return distancetravelled;
    }

    public void setDistancetravelled(String distancetravelled) {
        this.distancetravelled = distancetravelled;
    }

    public String getStatusofride() {
        return statusofride;
    }

    public void setStatusofride(String statusofride) {
        this.statusofride = statusofride;
    }

    public String getRidestarttime() {
        return ridestarttime;
    }

    public void setRidestarttime(String ridestarttime) {
        this.ridestarttime = ridestarttime;
    }

    public String getRidestoptime() {
        return ridestoptime;
    }

    public void setRidestoptime(String ridestoptime) {
        this.ridestoptime = ridestoptime;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getDriverpic() {
        return driverpic;
    }

    public void setDriverpic(String driverpic) {
        this.driverpic = driverpic;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getTravelpackage() {
        return travelpackage;
    }

    public void setTravelpackage(String travelpackage) {
        this.travelpackage = travelpackage;
    }

    public String getDrivermobile() {
        return drivermobile;
    }

    public void setDrivermobile(String drivermobile) {
        this.drivermobile = drivermobile;
    }

    public String getGuestProfileId() {
        return guestProfileId;
    }

    public void setGuestProfileId(String guestProfileId) {
        this.guestProfileId = guestProfileId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestMobile() {
        return guestMobile;
    }

    public void setGuestMobile(String guestMobile) {
        this.guestMobile = guestMobile;
    }

    public String getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(String pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public String getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(String pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public String getDropLatitude() {
        return dropLatitude;
    }

    public void setDropLatitude(String dropLatitude) {
        this.dropLatitude = dropLatitude;
    }

    public String getDropLongitude() {
        return dropLongitude;
    }

    public void setDropLongitude(String dropLongitude) {
        this.dropLongitude = dropLongitude;
    }

    public String getOTPStatus() {
        return oTPStatus;
    }

    public void setOTPStatus(String oTPStatus) {
        this.oTPStatus = oTPStatus;
    }


    public String getDriverBattaAmt() {
        return driverBattaAmt;
    }

    public void setDriverBattaAmt(String driverBattaAmt) {
        this.driverBattaAmt = driverBattaAmt;
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

    public String getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(String pickupLat) {
        this.pickupLat = pickupLat;
    }

    public String getPickupLong() {
        return pickupLong;
    }

    public void setPickupLong(String pickupLong) {
        this.pickupLong = pickupLong;
    }
}



