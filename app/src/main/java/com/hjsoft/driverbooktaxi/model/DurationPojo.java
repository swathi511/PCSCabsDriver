package com.hjsoft.driverbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjsoft on 23/11/16.
 */
public class DurationPojo {

    @SerializedName("destination_addresses")
    @Expose
    private List<String> destinationAddresses = new ArrayList<String>();
    @SerializedName("origin_addresses")
    @Expose
    private List<String> originAddresses = new ArrayList<String>();
    @SerializedName("rows")
    @Expose
    private List<Row> rows = new ArrayList<Row>();
    @SerializedName("status")
    @Expose
    private String status;

    /**
     *
     * @return
     * The destinationAddresses
     */
    public List<String> getDestinationAddresses() {
        return destinationAddresses;
    }

    /**
     *
     * @param destinationAddresses
     * The destination_addresses
     */
    public void setDestinationAddresses(List<String> destinationAddresses) {
        this.destinationAddresses = destinationAddresses;
    }

    /**
     *
     * @return
     * The originAddresses
     */
    public List<String> getOriginAddresses() {
        return originAddresses;
    }

    /**
     *
     * @param originAddresses
     * The origin_addresses
     */
    public void setOriginAddresses(List<String> originAddresses) {
        this.originAddresses = originAddresses;
    }

    /**
     *
     * @return
     * The rows
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     *
     * @param rows
     * The rows
     */
    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }


}
