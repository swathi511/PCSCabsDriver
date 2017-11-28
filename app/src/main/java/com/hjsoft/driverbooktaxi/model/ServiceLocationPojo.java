package com.hjsoft.driverbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 27/4/17.
 */
public class ServiceLocationPojo {

    @SerializedName("locationid")
    @Expose
    private String locationid;
    @SerializedName("location")
    @Expose
    private String location;

    public String getLocationid() {
        return locationid;
    }

    public void setLocationid(String locationid) {
        this.locationid = locationid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
