package main.com.notaryagent.constant;

/**
 * Created by technorizen on 4/3/19.
 */

public class CancelReasBean {
    String id;
    String reason;
    boolean checked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
