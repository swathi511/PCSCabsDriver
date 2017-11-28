package com.hjsoft.driverbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hjsoft on 30/3/17.
 */
public class Leg {

    @SerializedName("distance")
    @Expose
    private Distance distance;

    @SerializedName("end_address")
    @Expose
    private String endAddress;

    @SerializedName("start_address")
    @Expose
    private String startAddress;


    @SerializedName("traffic_speed_entry")
    @Expose
    private List<Object> trafficSpeedEntry = null;
    @SerializedName("via_waypoint")
    @Expose
    private List<Object> viaWaypoint = null;

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }



    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }


    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }



    public List<Object> getTrafficSpeedEntry() {
        return trafficSpeedEntry;
    }

    public void setTrafficSpeedEntry(List<Object> trafficSpeedEntry) {
        this.trafficSpeedEntry = trafficSpeedEntry;
    }

    public List<Object> getViaWaypoint() {
        return viaWaypoint;
    }

    public void setViaWaypoint(List<Object> viaWaypoint) {
        this.viaWaypoint = viaWaypoint;
    }

}
