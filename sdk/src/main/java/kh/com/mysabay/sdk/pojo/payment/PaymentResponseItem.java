package kh.com.mysabay.sdk.pojo.payment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Tan Phirum on 3/13/20
 * Gmail phirumtan@gmail.com
 */
public class PaymentResponseItem implements Parcelable {

    @SerializedName("code")
    @Expose
    public Integer code;
    @SerializedName("tx_hash")
    @Expose
    public String txHash;
    @SerializedName("payment_address")
    @Expose
    public String paymentAddress;
    public final static Parcelable.Creator<PaymentResponseItem> CREATOR = new Creator<PaymentResponseItem>() {


        @NotNull
        @Contract("_ -> new")
        public PaymentResponseItem createFromParcel(Parcel in) {
            return new PaymentResponseItem(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public PaymentResponseItem[] newArray(int size) {
            return (new PaymentResponseItem[size]);
        }

    };

    protected PaymentResponseItem(@NotNull Parcel in) {
        this.code = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.txHash = ((String) in.readValue((String.class.getClassLoader())));
        this.paymentAddress = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public PaymentResponseItem() {
    }

    /**
     * @param status
     * @param txHash
     * @param paymentAddress
     */
    public PaymentResponseItem(Integer status, String txHash, String paymentAddress) {
        super();
        this.code = status;
        this.txHash = txHash;
        this.paymentAddress = paymentAddress;
    }

    public PaymentResponseItem withCode(Integer code) {
        this.code = code;
        return this;
    }

    public PaymentResponseItem withTxHash(String txHash) {
        this.txHash = txHash;
        return this;
    }

    public PaymentResponseItem withPaymentAddress(String paymentAddress) {
        this.paymentAddress = paymentAddress;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("code", code).append("txHash", txHash).append("paymentAddress", paymentAddress).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(code).append(txHash).append(paymentAddress).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PaymentResponseItem) == false) {
            return false;
        }
        PaymentResponseItem rhs = ((PaymentResponseItem) other);
        return new EqualsBuilder().append(code, rhs.code).append(txHash, rhs.txHash).append(paymentAddress, rhs.paymentAddress).isEquals();
    }

    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeValue(code);
        dest.writeValue(txHash);
        dest.writeValue(paymentAddress);
    }

    public int describeContents() {
        return 0;
    }

}