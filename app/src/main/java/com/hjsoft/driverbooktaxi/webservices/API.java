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

    //Driver Login
    @POST("Login/CheckLogin")
    Call<Pojo> validateLogin(@Body JsonObject v);

    //To push Location Coordinates
    @POST("VehicleStatus/AddVehicleStatus")
    Call<Pojo> sendStatus(@Body JsonObject v);

    //To get Booking Request
    @GET("UserDetailsToCab/GetUserDetails")
    Call<List<CabRequestsPojo>> getCabRequests(@Query("profileid") String profileId,
                                               @Query("companyid") String companyId);

    //To update booking status- accept/decline
    @POST("UpdateStatus/UpdateUserStatus")
    Call<Pojo> sendCabAcceptanceStatus(@Body JsonObject v);

    //To send OTP to guest
    @POST("CabArrive/UpdateArrival")
    Call<Pojo> sendOtpNotify(@Body JsonObject v);

    //To Validate OTP
    @POST("OTPStatus/CheckOTP")
    Call<Pojo> checkOTP(@Body JsonObject v);

    //To send journey details
    @POST("TravelDetails/AddTravelDetails")
    Call<Pojo> sendRideDetails(@Body JsonObject v);

    //To get Ride history
    @GET("GuestDetails/GetGuestDetails")
    Call<ArrayList<AllRidesPojo>> getUserRides(@Query("profileid") String profileId,
                                               @Query("user") String user,
                                               @Query("companyid") String companyId);

    //To generate bill and process payment- cash/wallet
    @GET("GuestDetailsByRequest/GetGuestDetails")
    Call<List<RideStopPojo>> getRideStopData(@Query("reqid") String requestId,
                                             @Query("companyid") String companyId,
                                             @Query("user") String user);

    @GET
    Call<DurationPojo> getDistanceDetails(@Url String urlString);

    //To calculate ride distance
    @GET
    Call<DistancePojo> getOSDistanceDetails(@Url String urlString);

    //To send Offline status
    @POST("UpdateDriverStatus/DriverStatus")
    Call<Pojo> toOffline(@Body JsonObject v);

    @GET("ServiceLocations/GetServicelocations")
    Call<List<ServiceLocationPojo>> getServiceLocations(@Query("companyid") String companyId);

    //To get Ongoing booking details
    @GET("CurrentBookingDetails/GetCurrentBookingDetails")
    Call<ArrayList<AllRidesPojo>> getCurrentBooking(@Query("driverprofileid") String profileId,
                                                    @Query("companyid") String companyId);

    //To get the ride history based on dates
    @GET("BookingHistory/GetDetails")
    Call<ArrayList<AllRidesPojo>> getRideHistory(@Query("profileid") String profileId,
                                                 @Query("user") String user,
                                                 @Query("companyid") String companyId,
                                                 @Query("fromdate") String fromdate,
                                                 @Query("todate") String todate);

    //To get Cancellation reasons
    @GET("CancellationReasons/GetReasons")
    Call<ArrayList<CancelData>> getCancelList(@Query("companyid") String companyId,
                                              @Query("user") String user);

    //To send booking cancel status
    @POST("DriverCancellation/CancelRide")
    Call<Pojo> sendCancelStatus(@Body JsonObject v);

    //To get the number of booking either Completed or Cancelled.
    @GET("BookingsCount/GetDetails")
    Call<ArrayList<BookingHistoryPojo>> getBookingCountDetails(@Query("profileid") String profileId,
                                                               @Query("user") String user,
                                                               @Query("companyid") String companyId,
                                                               @Query("fromdate") String fromdate,
                                                               @Query("todate") String todate);
    //To send Driver mobile IMIE number
    @POST("DriverIMEI/UpdateDriverIMEI")
    Call<Pojo> sendIMIEnumber(@Body JsonObject v);

}
