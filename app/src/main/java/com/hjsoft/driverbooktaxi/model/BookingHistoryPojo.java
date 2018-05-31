package com.hjsoft.driverbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 17/2/18.
 */
public class BookingHistoryPojo{


    @SerializedName("bookingdate")
    @Expose
    private String bookingdate;
    @SerializedName("completerides")
    @Expose
    private String completerides;
    @SerializedName("cancelrides")
    @Expose
    private String cancelrides;
    @SerializedName("totalamount")
    @Expose
    private String totalamount;

    public String getBookingdate() {
        return bookingdate;
    }

    public void setBookingdate(String bookingdate) {
        this.bookingdate = bookingdate;
    }

    public String getCompleterides() {
        return completerides;
    }

    public void setCompleterides(String completerides) {
        this.completerides = completerides;
    }

    public String getCancelrides() {
        return cancelrides;
    }

    public void setCancelrides(String cancelrides) {
        this.cancelrides = cancelrides;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

}
