package kh.com.mysabay.sdk.pojo.payment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PaymentBodyPreAuth implements Parcelable {

    @SerializedName("hash")
    @Expose
    public String hash;
    @SerializedName("signature")
    @Expose
    public String signature;
    @SerializedName("public_key")
    @Expose
    public String publicKey;
    @SerializedName("payment_address")
    @Expose
    public String paymentAddress;
    @SerializedName("time")
    @Expose
    public String time;
    public final static Creator<PaymentBodyPreAuth> CREATOR = new Creator<PaymentBodyPreAuth>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PaymentBodyPreAuth createFromParcel(Parcel in) {
            return new PaymentBodyPreAuth(in);
        }

        public PaymentBodyPreAuth[] newArray(int size) {
            return (new PaymentBodyPreAuth[size]);
        }

    };

    protected PaymentBodyPreAuth(Parcel in) {
        this.hash = ((String) in.readValue((String.class.getClassLoader())));
        this.signature = ((String) in.readValue((String.class.getClassLoader())));
        this.publicKey = ((String) in.readValue((String.class.getClassLoader())));
        this.paymentAddress = ((String) in.readValue((String.class.getClassLoader())));
        this.time = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * @param hash
     * @param signature
     * @param publicKey
     * @param paymentAddress
     */
    public PaymentBodyPreAuth(String hash, String signature, String publicKey, String paymentAddress) {
        super();
        this.hash = hash;
        this.signature = signature;
        this.publicKey = publicKey;
        this.paymentAddress = paymentAddress;
    }

    public PaymentBodyPreAuth withHash(String hash) {
        this.hash = hash;
        return this;
    }

    public PaymentBodyPreAuth withSignature(String signature) {
        this.signature = signature;
        return this;
    }

    public PaymentBodyPreAuth withPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public PaymentBodyPreAuth withPaymentAddress(String paymentAddress) {
        this.paymentAddress = paymentAddress;
        return this;
    }

    public PaymentBodyPreAuth withTime(String time) {
        this.time = time;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("hash", hash).append("signature", signature)
                .append("publicKey", publicKey).append("paymentAddress", paymentAddress).append("time", time).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(hash).append(signature).append(publicKey).append(paymentAddress).append(time).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PaymentBodyPreAuth)) {
            return false;
        }
        PaymentBodyPreAuth rhs = ((PaymentBodyPreAuth) other);
        return new EqualsBuilder().append(hash, rhs.hash).append(signature, rhs.signature).append(publicKey, rhs.publicKey).append(paymentAddress, rhs.paymentAddress).append(time, rhs.time).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(hash);
        dest.writeValue(signature);
        dest.writeValue(publicKey);
        dest.writeValue(paymentAddress);
        dest.writeValue(time);
    }

    public int describeContents() {
        return 0;
    }

}