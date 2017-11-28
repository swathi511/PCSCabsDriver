package com.hjsoft.driverbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 30/11/16.
 */
public class CabRequestsPojo {

    @SerializedName("RequestId")
    @Expose
    private String requestId;
    @SerializedName("GuestProfileid")
    @Expose
    private String guestProfileid;
    @SerializedName("GuestName")
    @Expose
    private String guestName;
    @SerializedName("GuestMobile")
    @Expose
    private String guestMobile;
    @SerializedName("PickupLat")
    @Expose
    private String pickupLat;
    @SerializedName("PickupLong")
    @Expose
    private String pickupLong;
    @SerializedName("DropLat")
    @Expose
    private String dropLat;
    @SerializedName("DropLong")
    @Expose
    private String dropLong;
    @SerializedName("PickupLoc")
    @Expose
    private String pickupLoc;
    @SerializedName("DropLoc")
    @Expose
    private String dropLoc;
    @SerializedName("travelType")
    @Expose
    private String travelType;
    @SerializedName("travelPackage")
    @Expose
    private String travelPackage;
    @SerializedName("scheduledDate")
    @Expose
    private String scheduledDate;
    @SerializedName("scheduledTime")
    @Expose
    private String scheduledTime;
    @SerializedName("OTPrequired")
    @Expose
    private String oTPrequired;
    @SerializedName("bookingtype")
    @Expose
    private String bookingtype;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;


    /**
     *
     * @return
     * The requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     *
     * @param requestId
     * The RequestId
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     *
     * @return
     * The guestProfileid
     */
    public String getGuestProfileid() {
        return guestProfileid;
    }

    /**
     *
     * @param guestProfileid
     * The GuestProfileid
     */
    public void setGuestProfileid(String guestProfileid) {
        this.guestProfileid = guestProfileid;
    }

    /**
     *
     * @return
     * The guestName
     */
    public String getGuestName() {
        return guestName;
    }

    /**
     *
     * @param guestName
     * The GuestName
     */
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    /**
     *
     * @return
     * The guestMobile
     */
    public String getGuestMobile() {
        return guestMobile;
    }

    /**
     *
     * @param guestMobile
     * The GuestMobile
     */
    public void setGuestMobile(String guestMobile) {
        this.guestMobile = guestMobile;
    }

    /**
     *
     * @return
     * The pickupLat
     */
    public String getPickupLat() {
        return pickupLat;
    }

    /**
     *
     * @param pickupLat
     * The PickupLat
     */
    public void setPickupLat(String pickupLat) {
        this.pickupLat = pickupLat;
    }

    /**
     *
     * @return
     * The pickupLong
     */
    public String getPickupLong() {
        return pickupLong;
    }

    /**
     *
     * @param pickupLong
     * The PickupLong
     */
    public void setPickupLong(String pickupLong) {
        this.pickupLong = pickupLong;
    }

    /**
     *
     * @return
     * The dropLat
     */
    public String getDropLat() {
        return dropLat;
    }

    /**
     *
     * @param dropLat
     * The DropLat
     */
    public void setDropLat(String dropLat) {
        this.dropLat = dropLat;
    }

    /**
     *
     * @return
     * The dropLong
     */
    public String getDropLong() {
        return dropLong;
    }

    /**
     *
     * @param dropLong
     * The DropLong
     */
    public void setDropLong(String dropLong) {
        this.dropLong = dropLong;
    }

    /**
     *
     * @return
     * The pickupLoc
     */
    public String getPickupLoc() {
        return pickupLoc;
    }

    /**
     *
     * @param pickupLoc
     * The PickupLoc
     */
    public void setPickupLoc(String pickupLoc) {
        this.pickupLoc = pickupLoc;
    }

    /**
     *
     * @return
     * The dropLoc
     */
    public String getDropLoc() {
        return dropLoc;
    }

    /**
     *
     * @param dropLoc
     * The DropLoc
     */
    public void setDropLoc(String dropLoc) {
        this.dropLoc = dropLoc;
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

    public String getOTPrequired() {
        return oTPrequired;
    }

    public void setOTPrequired(String oTPrequired) {
        this.oTPrequired = oTPrequired;
    }

    public String getBookingtype() {
        return bookingtype;
    }

    public void setBookingtype(String bookingtype) {
        this.bookingtype = bookingtype;
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
}
