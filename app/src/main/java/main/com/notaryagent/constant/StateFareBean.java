package main.com.notaryagent.constant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by technorizen on 28/12/18.
 */

public class StateFareBean {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("state_name")
    @Expose
    private String stateName;
    @SerializedName("state_shotcode")
    @Expose
    private String stateShotcode;
    @SerializedName("notary_fees")
    @Expose
    private String notaryFees;
    @SerializedName("regular")
    @Expose
    private String regular;
    @SerializedName("today")
    @Expose
    private String today;
    @SerializedName("rush")
    @Expose
    private String rush;
    @SerializedName("real_estate_and_additional_charge")
    @Expose
    private String realEstateAndAdditionalCharge;
    @SerializedName("charge_type")
    @Expose
    private String chargeType;
    @SerializedName("status")
    @Expose
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateShotcode() {
        return stateShotcode;
    }

    public void setStateShotcode(String stateShotcode) {
        this.stateShotcode = stateShotcode;
    }

    public String getNotaryFees() {
        return notaryFees;
    }

    public void setNotaryFees(String notaryFees) {
        this.notaryFees = notaryFees;
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getRush() {
        return rush;
    }

    public void setRush(String rush) {
        this.rush = rush;
    }

    public String getRealEstateAndAdditionalCharge() {
        return realEstateAndAdditionalCharge;
    }

    public void setRealEstateAndAdditionalCharge(String realEstateAndAdditionalCharge) {
        this.realEstateAndAdditionalCharge = realEstateAndAdditionalCharge;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
