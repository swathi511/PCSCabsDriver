package com.hjsoft.driverbooktaxi.webservices;

import com.google.gson.JsonObject;
import com.hjsoft.driverbooktaxi.model.AllRidesPojo;
import com.hjsoft.driverbooktaxi.model.BookingHistoryPojo;
import com.hjsoft.driverbooktaxi.model.CabRequestsPojo;
import com.hjsoft.driverbooktaxi.model.CancelData;
import com.hjsoft.driverbooktaxi.model.DistancePojo;
import com.hjsoft.driverbooktaxi.model.DurationPojo;
import com.hjsoft.driverbooktaxi.model.Pojo;
import com.hjsoft.driverbooktaxi.model.RideStopPojo;
import com.hjsoft.driverbooktaxi.model.ServiceLocationPojo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by hjsoft on 23/11/16.
 */
public interface API {

    @POST("Login/CheckLogin")
    Call<Pojo> validateLogin(@Body JsonObject v);

    @POST("VehicleStatus/AddVehicleStatus")
    Call<Pojo> sendStatus(@Body JsonObject v);

    @GET("UserDetailsToCab/GetUserDetails")
    Call<List<CabRequestsPojo>> getCabRequests(@Query("profileid") String profileId,
                                               @Query("companyid") String companyId);

    @POST("UpdateStatus/UpdateUserStatus")
    Call<Pojo> sendCabAcceptanceStatus(@Body JsonObject v);

    @POST("CabArrive/UpdateArrival")
    Call<Pojo> sendOtpNotify(@Body JsonObject v);

    @POST("OTPStatus/CheckOTP")
    Call<Pojo> checkOTP(@Body JsonObject v);

    @POST("TravelDetails/AddTravelDetails")
    Call<Pojo> sendRideDetails(@Body JsonObject v);

    @GET("GuestDetails/GetGuestDetails")
    Call<ArrayList<AllRidesPojo>> getUserRides(@Query("profileid") String profileId,
                                               @Query("user") String user,
                                               @Query("companyid") String companyId);

    @GET("GuestDetailsByRequest/GetGuestDetails")
    Call<List<RideStopPojo>> getRideStopData(@Query("reqid") String requestId,
                                             @Query("companyid") String companyId,
                                             @Query("user") String user);

    @GET
    Call<DurationPojo> getDistanceDetails(@Url String urlString);

    @GET
    Call<DistancePojo> getOSDistanceDetails(@Url String urlString);

    @POST("UpdateDriverStatus/DriverStatus")
    Call<Pojo> toOffline(@Body JsonObject v);

    @GET("ServiceLocations/GetServicelocations")
    Call<List<ServiceLocationPojo>> getServiceLocations(@Query("companyid") String companyId);

    @GET("CurrentBookingDetails/GetCurrentBookingDetails")
    Call<ArrayList<AllRidesPojo>> getCurrentBooking(@Query("driverprofileid") String profileId,
                                               @Query("companyid") String companyId);

    @GET("BookingHistory/GetDetails")
    Call<ArrayList<AllRidesPojo>> getRideHistory(@Query("profileid") String profileId,
                                               @Query("user") String user,
                                               @Query("companyid") String companyId,
                                                 @Query("fromdate") String fromdate,
                                                 @Query("todate") String todate);

    @GET("CancellationReasons/GetReasons")
    Call<ArrayList<CancelData>> getCancelList(@Query("companyid") String companyId,
                                              @Query("user") String user);

    @POST("DriverCancellation/CancelRide")
    Call<Pojo> sendCancelStatus(@Body JsonObject v);

    @GET("BookingsCount/GetDetails")
    Call<ArrayList<BookingHistoryPojo>> getBookingCountDetails(@Query("profileid") String profileId,
                                                               @Query("user") String user,
                                                               @Query("companyid") String companyId,
                                                               @Query("fromdate") String fromdate,
                                                               @Query("todate") String todate);

}
