package com.hjsoft.driverbooktaxi.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hjsoft on 21/1/17.
 */
public class FormattedAllRidesData implements Serializable {

        String requestId,fromLocation,toLocation,vehicleCategory,vehicleType,distanceTravelled;
        String rideStatus,rideStartTime,rideStopTime,totalAmount,driverName,driverPic,travelType,bookingType,travelPackage,osBatta;
        Date rideDate;
        String driverMobile,guestProfileId,guestName,guestMobile,pickupLat,pickupLong,dropLat,dropLong,otpStatus,paymentMode;

        public FormattedAllRidesData(Date rideDate, String requestId,String fromLocation,String toLocation,String vehicleCategory,String vehicleType,String distanceTravelled,
                                     String rideStatus,String rideStartTime,String rideStopTime,String totalAmount,String driverName,String driverPic,String travelType,String bookingType,String travelPackage,
                                     String driverMobile,String guestProfileId,String guestName,String guestMobile,String pickupLat,String pickupLong,String dropLat,String dropLong,
                                     String otpStatus,String osBatta,String paymentMode)
        {
                this.rideDate=rideDate;
                this.requestId=requestId;
                this.fromLocation=fromLocation;
                this.toLocation=toLocation;
                this.vehicleCategory=vehicleCategory;
                this.vehicleType=vehicleType;
                this.distanceTravelled=distanceTravelled;
                this.rideStatus=rideStatus;
                this.rideStartTime=rideStartTime;
                this.rideStopTime=rideStopTime;
                this.totalAmount=totalAmount;
                this.driverName=driverName;
                this.driverPic=driverPic;
                this.travelType=travelType;
                this.bookingType=bookingType;
                this.travelPackage=travelPackage;
                this.driverMobile=driverMobile;
                this.guestProfileId=guestProfileId;
                this.guestName=guestName;
                this.guestMobile=guestMobile;
                this.pickupLat=pickupLat;
                this.pickupLong=pickupLong;
                this.dropLat=dropLat;
                this.dropLong=dropLong;
                this.otpStatus=otpStatus;
                this.osBatta=osBatta;
                this.paymentMode=paymentMode;
        }

        public String getRequestId() {
                return requestId;
        }

        public void setRequestId(String requestId) {
                this.requestId = requestId;
        }

        public String getFromLocation() {
                return fromLocation;
        }

        public void setFromLocation(String fromLocation) {
                this.fromLocation = fromLocation;
        }

        public String getToLocation() {
                return toLocation;
        }

        public void setToLocation(String toLocation) {
                this.toLocation = toLocation;
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

        public String getDistanceTravelled() {
                return distanceTravelled;
        }

        public void setDistanceTravelled(String distanceTravelled) {
                this.distanceTravelled = distanceTravelled;
        }

        public String getRideStatus() {
                return rideStatus;
        }

        public void setRideStatus(String rideStatus) {
                this.rideStatus = rideStatus;
        }

        public String getRideStartTime() {
                return rideStartTime;
        }

        public void setRideStartTime(String rideStartTime) {
                this.rideStartTime = rideStartTime;
        }

        public String getRideStopTime() {
                return rideStopTime;
        }

        public void setRideStopTime(String rideStopTime) {
                this.rideStopTime = rideStopTime;
        }

        public Date getRideDate() {
                return rideDate;
        }

        public void setRideDate(Date rideDate) {
                this.rideDate = rideDate;
        }

        public String getTotalAmount() {
                return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
                this.totalAmount = totalAmount;
        }

        public String getDriverName() {
                return driverName;
        }

        public void setDriverName(String driverName) {
                this.driverName = driverName;
        }

        public String getDriverPic() {
                return driverPic;
        }

        public void setDriverPic(String driverPic) {
                this.driverPic = driverPic;
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

        public String getTravelPackage() {
                return travelPackage;
        }

        public void setTravelPackage(String travelPackage) {
                this.travelPackage = travelPackage;
        }

        public String getDriverMobile() {
                return driverMobile;
        }

        public void setDriverMobile(String driverMobile) {
                this.driverMobile = driverMobile;
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

        public String getPickupLong() {
                return pickupLong;
        }

        public void setPickupLong(String pickupLong) {
                this.pickupLong = pickupLong;
        }

        public String getGuestMobile() {
                return guestMobile;
        }

        public void setGuestMobile(String guestMobile) {
                this.guestMobile = guestMobile;
        }

        public String getPickupLat() {
                return pickupLat;
        }

        public void setPickupLat(String pickupLat) {
                this.pickupLat = pickupLat;
        }

        public String getDropLat() {
                return dropLat;
        }

        public void setDropLat(String dropLat) {
                this.dropLat = dropLat;
        }

        public String getDropLong() {
                return dropLong;
        }

        public void setDropLong(String dropLong) {
                this.dropLong = dropLong;
        }

        public String getOtpStatus() {
                return otpStatus;
        }

        public void setOtpStatus(String otpStatus) {
                this.otpStatus = otpStatus;
        }

        public String getOsBatta() {
                return osBatta;
        }

        public void setOsBatta(String osBatta) {
                this.osBatta = osBatta;
        }

        public String getPaymentMode() {
                return paymentMode;
        }

        public void setPaymentMode(String paymentMode) {
                this.paymentMode = paymentMode;
        }
}


