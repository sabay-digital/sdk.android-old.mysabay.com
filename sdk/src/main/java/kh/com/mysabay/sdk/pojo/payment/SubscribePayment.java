package kh.com.mysabay.sdk.pojo.payment;

/**
 * Created by Tan Phirum on 3/28/20
 * Gmail phirumtan@gmail.com
 */
public class SubscribePayment {

    public final String type;
    public final Object data;
    public final Object error;

    public SubscribePayment(String type, Object data, Object error) {
        this.type = type;
        this.data = data;
        this.error = error;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
