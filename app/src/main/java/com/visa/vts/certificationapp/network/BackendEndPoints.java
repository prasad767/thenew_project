package com.visa.vts.certificationapp.network;


import com.visa.cbp.external.aam.ReplenishAckRequest;
import com.visa.cbp.external.aam.ReplenishRequest;
import com.visa.cbp.external.aam.ReplenishResponse;
import com.visa.cbp.external.common.EnrollPanResponse;
import com.visa.cbp.external.enp.EnrollDeviceResponse;
import com.visa.cbp.external.enp.EnrollPanRequest;
import com.visa.cbp.external.enp.ProvisionAckRequest;
import com.visa.cbp.external.enp.ProvisionRequestWithEnrollId;
import com.visa.cbp.external.enp.ProvisionResponse;
import com.visa.cbp.external.lcm.LcmTokenRequest;
import com.visa.cbp.external.lcm.LcmTokenResponse;
//import com.visa.cbp.sdk.facade.callbacks.ResponseCallback;
import com.visa.vts.certificationapp.model.request.EnrollDeviceParams;
import com.visa.vts.certificationapp.utils.Constants;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by prrathin on 11/17/15.
 */
public interface BackendEndPoints {

    @POST("VTS/dmpd/enrolldevice?envName=sandbox&action=enrolldevice")
    Call<EnrollDeviceResponse> enrollDevice(@Header("x-pay-token")String x_pay_token,@Body EnrollDeviceParams enrollDeviceParams,@Query("deviceid") String deviceid);


    @POST("VTS/dmpd/enrollpan?envName=sandbox&action=enrollPan")
    Call<EnrollPanResponse> enrollPan(@Body EnrollPanRequest enrollPanRequest);


    @POST("VTS/dmpd/provisionpan?envName=sandbox&action=provisiontoken")
    Call<ProvisionResponse> provision(@Body ProvisionRequestWithEnrollId provisionPan,@Query("vpanenrollmentid") String vpanenrollmentid);

    @POST("VTS/dmpd/confirmprovision?envName=sandbox&action=confirmprovision")
    //@POST("vts/provisionedTokens/08b9fb56f05208d93f401af0d73ece01/confirmProvisioning?apikey=NOLVJ3EZ02A5ZNGOJIHC13y9daLe_QaWrdboJHOv5LonM2QKI")
    Call<ProvisionResponse> confirmProvision(@Body ProvisionAckRequest provisionAckRequest,@Query("vProvisionedTokenID") String vProvisionedTokenID);
    //Call<ProvisionResponse> confirmProvision(@Body ProvisionAckRequest provisionAckRequest/*,@Query("vProvisionedTokenID") String vProvisionedTokenID*/);

    @POST("VTS/dmpd/replenish?envName=sandbox&action=replenish")
    //@POST("VTS/dmpd/replenish?envName=sandbox&action=replenish&vProvisionedTokenID="+Constants.vProvisionedTokenID)
    Call<ReplenishResponse> Replenish(@Body ReplenishRequest replenishRequest,@Query("vProvisionedTokenID") String vProvisionedTokenID);

   @POST("VTS/dmpd/confirmreplenish?envName=sandbox&action=confirmreplenish")
    //@POST("VTS/dmpd/confirmreplenish?envName=sandbox&action=confirmreplenish&vProvisionedTokenID="+Constants.vProvisionedTokenID)
    Call<ReplenishResponse> confirmReplenish(@Body ReplenishAckRequest replenishAckRequest,@Query("vProvisionedTokenID") String vProvisionedTokenID);

    @POST("VTS/dmpd/tokenLCM?envName=sandbox&action=suspend")
   // @POST("VTS/dmpd/tokenLCM?envName=sandbox&action=suspend&vProvisionedTokenID="+Constants.vProvisionedTokenID)
    Call<LcmTokenResponse> lcmSuspend (@Body LcmTokenRequest lcmTokenRequest,@Query("vProvisionedTokenID") String vProvisionedTokenID);

    @POST("VTS/dmpd/tokenLCM?envName=sandbox&action=resume")
   // @POST("VTS/dmpd/tokenLCM?envName=sandbox&action=resume&vProvisionedTokenID="+Constants.vProvisionedTokenID)
    Call<LcmTokenResponse> lcmResume (@Body LcmTokenRequest lcmTokenRequest,@Query("vProvisionedTokenID") String vProvisionedTokenID);

    @POST("VTS/dmpd/tokenLCM?envName=sandbox&action=delete")
   // @POST("VTS/dmpd/tokenLCM?envName=sandbox&action=delete&vProvisionedTokenID="+Constants.vProvisionedTokenID)
    Call<LcmTokenResponse> lcmDelete (@Body LcmTokenRequest lcmTokenRequest ,@Query("vProvisionedTokenID") String vProvisionedTokenID);

   @GET("VTS/dmpd/cps/getContent")
  // @GET("VTS/dmpd/cps/getContent?envName=sandbox&action=getContent")
  // @GET("VTS/dmpd/enrollpan?envName=sandbox&action=getContent")
   // Call<EnrollPanResponse> getContent(@Body ProvisionRequestWithEnrollId provisionPan,@Query("vpanenrollmentid") String vpanenrollmentid);
   //Call<String> getContent(@Query("guid")String guid);
   // Call<String> getContent(@Query("guid")String guid ,@Query("vPanEnrollmentID")String vPanEnrollmentID );
   Call<String> getContent(@Query("guid")String guid ,@Query("apikey")String apikey );
}
