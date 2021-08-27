package com.toolsbox.contractor.common.api;


import com.google.gson.JsonObject;
import com.toolsbox.contractor.common.model.api.AttachData;
import com.toolsbox.contractor.common.model.api.ContractorSearchData;
import com.toolsbox.contractor.common.model.api.GeneralData;
import com.toolsbox.contractor.common.model.api.JobAppliedData;
import com.toolsbox.contractor.common.model.api.JobDetailData;
import com.toolsbox.contractor.common.model.api.JobHistoryData;
import com.toolsbox.contractor.common.model.api.JobProposalData;
import com.toolsbox.contractor.common.model.api.JobSearchData;
import com.toolsbox.contractor.common.model.api.LoginData;
import com.toolsbox.contractor.common.model.api.ReviewData;
import com.toolsbox.contractor.common.model.api.TwilioAccessTokenData;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface ApiInterface {

    String version = "v8";

    @POST("/api/" + version +"/contractor/register")
    Call<LoginData> signup(@Body RequestBody bean);

    @POST("/api/" + version +"/contractor/login")
    Call<LoginData> login(@Body RequestBody bean);

    @GET("/api/" + version +"/contractor/logout")
    Call<GeneralData> logout(@Query("token") String token);

    @Multipart
    @POST("/api/" + version +"/contractor/update")
    Call<LoginData> updateProfile(@Query("token") String token, @Part MultipartBody.Part file, @Part("email") RequestBody email, @Part("phone") RequestBody phone,
                                  @Part("business_name") RequestBody business_name, @Part("business_number") RequestBody business_number, @Part("speciality_title") RequestBody speciality_title, @Part("address") RequestBody address,
                                  @Part("address_lati") RequestBody address_lati, @Part("address_longi") RequestBody address_longi, @Part("business_structure") RequestBody business_structure,
                                  @Part("industry") RequestBody industry);

    @Multipart
    @POST("/api/" + version +"/jobs/post")
    Call<GeneralData> postJob(@Part("name") RequestBody name, @Part("industry") RequestBody industry,
                              @Part("area") RequestBody area, @Part("area_lati") RequestBody area_lati,
                              @Part("area_longi") RequestBody area_longi, @Part("description") RequestBody description,
                              @Part("poster_type") RequestBody poster_type, @Part("poster_id") RequestBody poster_id,
                              @Part("poster_name") RequestBody poster_name, @Part("urgency") RequestBody urgency,
                              @Part("availability_dates") RequestBody availability_dates);

    @Multipart
    @POST("/api/" + version +"/jobs/update")
    Call<GeneralData> editJob(@Part("job_id") RequestBody job_id, @Part("token") RequestBody token,
                              @Part("name") RequestBody name, @Part("industry") RequestBody industry,
                              @Part("area") RequestBody area, @Part("area_lati") RequestBody area_lati,
                              @Part("area_longi") RequestBody area_longi, @Part("description") RequestBody description,
                              @Part("urgency") RequestBody urgency,
                              @Part("availability_dates") RequestBody availability_dates);

    @Multipart
    @POST("/api/" + version +"/jobs/hire")
    Call<GeneralData> hireJob(@Part("name") RequestBody name, @Part("industry") RequestBody industry,
                              @Part("area") RequestBody area, @Part("area_lati") RequestBody area_lati,
                              @Part("area_longi") RequestBody area_longi, @Part("description") RequestBody description,
                              @Part("poster_type") RequestBody poster_type, @Part("poster_id") RequestBody poster_id,
                              @Part("poster_name") RequestBody poster_name, @Part("urgency") RequestBody urgency,
                              @Part("availability_dates") RequestBody availability_dates, @Part("contractor_id") RequestBody contractor_id);

    @GET("/api/" + version +"/contractor/search")
    Call<ContractorSearchData> searchContractor(@Query("page") int page, @Query("per_page") int per_page, @Query("industry") int industry,
                                                @Query("lati") String lati, @Query("longi") String longi, @Query("radius") int radius);

    @GET("/api/" + version +"/jobs/search")
    Call<JobSearchData> searchJob(@Query("page") int page, @Query("per_page") int per_page, @Query("industry") int industry,
                                         @Query("lati") String lati, @Query("longi") String longi, @Query("radius") int radius);


    @GET("/api/" + version +"/jobs/search/newly_posted")
    Call<JobSearchData> newlyPostedJob(@Query("page") int page, @Query("per_page") int per_page, @Query("industry") String industry,
                                  @Query("lati") String lati, @Query("longi") String longi, @Query("radius") int radius);


    @GET("/api/" + version +"/review/history")
    Call<ReviewData> fetchReviews(@Query("page") int page, @Query("per_page") int per_page, @Query("contractor_id") int contractor_id);

    @POST("/api/" + version +"/review/provide")
    Call<GeneralData> addReview(@Body RequestBody bean);

    @POST("/api/" + version +"/jobs/placebid")
    Call<GeneralData> sendQuote(@Query("token") String token,  @Body RequestBody bean);

    @POST("/api/" + version +"/jobs/bid/update")
    Call<GeneralData> updateQuote(@Query("token") String token,  @Body RequestBody bean);

    @GET("/api/" + version +"/jobs/myjob/contractor/post")
    Call<JobHistoryData> fetchJobHistory(@Query("page") int page, @Query("per_page") int per_page, @Query("status") int status, @Query("token") String token);

    @GET("/api/" + version +"/jobs/myjob/contractor/apply")
    Call<JobAppliedData> fetchJobApplied(@Query("page") int page, @Query("per_page") int per_page, @Query("status") int status, @Query("token") String token);

    @GET("/api/" + version +"/jobs/myjob/contractor/apply/all")
    Call<JobAppliedData> fetchJobAppliedAll(@Query("page") int page, @Query("per_page") int per_page,  @Query("token") String token);


    @GET("/api/" + version +"/jobs/bidders")
    Call<JobProposalData> fetchProposal(@Query("job_id") int job_id, @Query("type") int type, @Query("contractor_id") int contractor_id);

    @POST("/api/" + version +"/jobs/remove/contractor")
    Call<GeneralData> removeJob(@Body RequestBody bean, @Query("token") String token);

    @POST("/api/" + version +"/jobs/complete")
    Call<GeneralData> completeJob(@Body RequestBody bean);

    @POST("/api/" + version +"/jobs/request/enroute")
    Call<GeneralData> requestEnRouteJob(@Query("token") String token, @Body RequestBody bean);

    @POST("/api/" + version +"/jobs/request/start")
    Call<GeneralData> requestStartJob(@Query("token") String token, @Body RequestBody bean);

    @POST("/api/" + version +"/jobs/request/complete")
    Call<GeneralData> makeJobComplete(@Query("token") String token, @Body RequestBody bean);

    @POST("/api/" + version +"/jobs/bidders/award")
    Call<GeneralData> acceptBidder(@Body RequestBody bean);

    @POST("/api/" + version +"/jobs/bidders/reject")
    Call<GeneralData> declineBidder(@Body RequestBody bean);

    @GET("/api/" + version +"/contractor/payment/getcards")
    Call<JsonObject> fetchCard(@Query("token") String token);

    @POST("/api/" + version +"/contractor/payment/add")
    Call<GeneralData> addCreditCard(@Body RequestBody bean, @Query("token") String token);

    @POST("/api/" + version +"/contractor/payment/defaultcard")
    Call<GeneralData> updateDefaultCreditCard(@Body RequestBody bean, @Query("token") String token);

    @GET("/api/" + version +"/contractor/payout/getbanks")
    Call<JsonObject> fetchBankCards(@Query("token") String token);

    @POST("/api/" + version +"/contractor/payout/default_bank")
    Call<GeneralData> updateDefaultBank(@Body RequestBody bean, @Query("token") String token);

    @POST("/api/" + version +"/contractor/payout/add_bank")
    Call<GeneralData> addBankAccount(@Body RequestBody bean, @Query("token") String token);

    @POST("/api/" + version +"/jobs/contractor-details")
    Call<JobDetailData> jobContractorDetails(@Body RequestBody bean);

    @POST("/api/" + version +"/contractor/chat/access_token")
    Call<TwilioAccessTokenData> fetchTwilioAccessToken(@Body RequestBody bean);

    @POST("/api/" + version +"/contractor/forgot/email_verify/request")
    Call<GeneralData> forgotEmailVerify(@Body RequestBody bean);

    @POST("/api/" + version +"/contractor/forgot/email_verify/confirm")
    Call<LoginData> forgotEmailVerifyConfirm(@Body RequestBody bean);

    @POST("/api/" + version +"/contractor/reset_password_confirm")
    Call<GeneralData> updatePassword(@Body RequestBody bean, @Query("token") String token);

    @Multipart
    @POST("/api/" + version + "/jobs/bid/attach")
    Call<AttachData> uploadAttachment(@Part MultipartBody.Part file);

}