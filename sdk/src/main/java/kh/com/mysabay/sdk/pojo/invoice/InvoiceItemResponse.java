package kh.com.mysabay.sdk.pojo.invoice;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import kh.com.mysabay.sdk.pojo.shop.Property;

/**
 * Created by Tan Phirum on 3/13/20
 * Gmail phirumtan@gmail.com
 */
public class InvoiceItemResponse implements Parcelable {

    @SerializedName("ssnTxHash")
    @Expose
    public String ssnTxHash;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("currency")
    @Expose
    public String currency;

    public final static Creator<InvoiceItemResponse> CREATOR = new Creator<InvoiceItemResponse>() {
        @SuppressWarnings({
                "unchecked"
        })
        public InvoiceItemResponse createFromParcel(Parcel in) {
            return new InvoiceItemResponse(in);
        }

        public InvoiceItemResponse[] newArray(int size) {
            return (new InvoiceItemResponse[size]);
        }
    };

    protected InvoiceItemResponse(Parcel in) {
        this.ssnTxHash = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.currency = ((String) in.readValue((String.class.getClassLoader())));
    }


    public InvoiceItemResponse withSsnTxHash(String status) {
        this.status = status;
        return this;
    }

    public InvoiceItemResponse withCurrency(Double salePrice) {
        this.currency = currency;
        return this;
    }


    public InvoiceItemResponse withStatus(String status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("ssnTxHash", ssnTxHash).append("status", status)
                .append("currency", currency).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(ssnTxHash).append(status).append(currency).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof InvoiceItemResponse)) {
            return false;
        }
        InvoiceItemResponse rhs = ((InvoiceItemResponse) other);
        return new EqualsBuilder().append(ssnTxHash, rhs.ssnTxHash).append(status, rhs.status)
                .append(currency, rhs.currency).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(ssnTxHash);
        dest.writeValue(status);
        dest.writeValue(currency);
    }

    public int describeContents() {
        return 0;
    }
}