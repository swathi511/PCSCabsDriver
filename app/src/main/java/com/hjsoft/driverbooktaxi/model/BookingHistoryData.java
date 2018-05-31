package com.hjsoft.driverbooktaxi.model;

/**
 * Created by hjsoft on 17/2/18.
 */
public class BookingHistoryData {

    String date,completedRides,cancelledRides,amount;


    public BookingHistoryData(String date,String completedRides,String cancelledRides,String amount)
    {
        this.date=date;
        this.completedRides=completedRides;
        this.cancelledRides=cancelledRides;
        this.amount=amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCompletedRides() {
        return completedRides;
    }

    public void setCompletedRides(String completedRides) {
        this.completedRides = completedRides;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCancelledRides() {
        return cancelledRides;
    }

    public void setCancelledRides(String cancelledRides) {
        this.cancelledRides = cancelledRides;
    }
}
