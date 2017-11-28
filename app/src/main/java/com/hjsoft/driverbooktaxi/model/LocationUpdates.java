package com.hjsoft.driverbooktaxi.model;

/**
 * Created by hjsoft on 2/2/17.
 */
public class LocationUpdates {

    String requestId,startingTime,stoppingTime,distance,guestName,guestMobile,idleTime;
    double latitude,longitude,pLat,pLng,dLat,dLng;

    public LocationUpdates(String requestId,String startingTime,String stoppingTime,String distance,double latitude,double longitude,double pLat,double pLng,double dLat,double dLng,String guestName,String guestMobile,String idleTime)
    {
       this.requestId=requestId;
        this.startingTime=startingTime;
        this.stoppingTime=stoppingTime;
        this.distance=distance;
        this.latitude=latitude;
        this.longitude=longitude;
        this.pLat=pLat;
        this.pLng=pLng;
        this.dLat=dLat;
        this.dLng=dLng;
        this.guestName=guestName;
        this.guestMobile=guestMobile;
        this.idleTime=idleTime;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getStoppingTime() {
        return stoppingTime;
    }

    public void setStoppingTime(String stoppingTime) {
        this.stoppingTime = stoppingTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getpLng() {
        return pLng;
    }

    public void setpLng(double pLng) {
        this.pLng = pLng;
    }

    public double getpLat() {
        return pLat;
    }

    public void setpLat(double pLat) {
        this.pLat = pLat;
    }

    public double getdLat() {
        return dLat;
    }

    public void setdLat(double dLat) {
        this.dLat = dLat;
    }

    public double getdLng() {
        return dLng;
    }

    public void setdLng(double dLng) {
        this.dLng = dLng;
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

    public String getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(String idleTime) {
        this.idleTime = idleTime;
    }
}
