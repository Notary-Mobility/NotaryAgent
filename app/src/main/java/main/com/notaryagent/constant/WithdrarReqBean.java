package main.com.notaryagent.constant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by technorizen on 15/1/19.
 */

public class WithdrarReqBean {

    @SerializedName("result")
    @Expose
    private List<WithdrarReqBeanList> result = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("invite_earning")
    @Expose
    private String invite_earning;
    @SerializedName("ride_earning")
    @Expose
    private String ride_earning;

    public List<WithdrarReqBeanList> getResult() {
        return result;
    }

    public void setResult(List<WithdrarReqBeanList> result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInvite_earning() {
        return invite_earning;
    }

    public void setInvite_earning(String invite_earning) {
        this.invite_earning = invite_earning;
    }

    public String getRide_earning() {
        return ride_earning;
    }

    public void setRide_earning(String ride_earning) {
        this.ride_earning = ride_earning;
    }
}
