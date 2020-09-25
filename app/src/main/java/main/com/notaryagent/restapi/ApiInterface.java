package main.com.notaryagent.restapi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by technorizen on 14/2/18.
 */

public interface ApiInterface {
    @GET("login?")
    Call<ResponseBody> loginCall(@Query("mobile") String email, @Query("password") String password, @Query("register_id") String register_id, @Query("lat") String lat, @Query("lon") String lon, @Query("type") String type, @Query("continue") String login_sts) ;
    @GET("signup?")
    Call<ResponseBody> SignupCall(@Query("first_name") String first_name, @Query("last_name") String last_name, @Query("mobile") String phone, @Query("email") String email, @Query("password") String password, @Query("register_id") String register_id, @Query("lat") String lat, @Query("lon") String lon, @Query("type") String type) ;
    @GET("forgot_password?")
    Call<ResponseBody> ForgotCall(@Query("email") String email, @Query("type") String type) ;
    @GET("get_user_history?")
    Call<ResponseBody> GetRideHistory(@Query("user_id") String user_id, @Query("type") String type) ;
    @GET("get_my_withdraw_request?")
    Call<ResponseBody> getWithdrawDetail(@Query("user_id") String user_id) ;
    @GET("strips_payment?")
    Call<ResponseBody> makePayment(@Query("request_id") String request_id, @Query("user_id") String user_id, @Query("token") String token, @Query("customer") String customer, @Query("driver_id") String driver_id, @Query("amount") String amount, @Query("transaction_type") String transaction_type, @Query("payment_type") String payment_type, @Query("time_zone") String time_zone, @Query("currency") String currency) ;


}
//http://technorizen.com/notary/webservice/get_my_withdraw_request?user_id=1