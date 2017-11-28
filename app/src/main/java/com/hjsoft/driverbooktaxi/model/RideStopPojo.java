package com.hjsoft.driverbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 27/1/17.
 */
public class RideStopPojo
{
    @SerializedName("fromlocation")
    @Expose
    private String fromlocation;
    @SerializedName("tolocation")
    @Expose
    private String tolocation;
    @SerializedName("totalfare")
    @Expose
    private String totalfare;
    @SerializedName("distancetravelled")
    @Expose
    private String distancetravelled;
    @SerializedName("ridestarttime")
    @Expose
    private String ridestarttime;
    @SerializedName("ridestoptime")
    @Expose
    private String ridestoptime;
    @SerializedName("vehicle_category")
    @Expose
    private String vehicleCategory;
    @SerializedName("vehicle_type")
    @Expose
    private String vehicleType;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("payment_cash")
    @Expose
    private String paymentCash;
    @SerializedName("payment_wallet")
    @Expose
    private String paymentWallet;
    @SerializedName("wallet_balance")
    @Expose
    private String walletBalance;
    @SerializedName("travelType")
    @Expose
    private String travelType;
    @SerializedName("DriverBattaAmt")
    @Expose
    private String driverBattaAmt;



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

    public String getTotalfare() {
        return totalfare;
    }

    public void setTotalfare(String totalfare) {
        this.totalfare = totalfare;
    }

    public String getDistancetravelled() {
        return distancetravelled;
    }

    public void setDistancetravelled(String distancetravelled) {
        this.distancetravelled = distancetravelled;
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

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentCash() {
        return paymentCash;
    }

    public void setPaymentCash(String paymentCash) {
        this.paymentCash = paymentCash;
    }

    public String getPaymentWallet() {
        return paymentWallet;
    }

    public void setPaymentWallet(String paymentWallet) {
        this.paymentWallet = paymentWallet;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public String getDriverBattaAmt() {
        return driverBattaAmt;
    }

    public void setDriverBattaAmt(String driverBattaAmt) {
        this.driverBattaAmt = driverBattaAmt;
    }
}
