package kh.com.mysabay.sdk.pojo.shop;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Property implements Parcelable {

    @SerializedName("packageCode")
    @Expose
    public String packageCode;
    @SerializedName("displayName")
    @Expose
    public String displayName;
    @SerializedName("priceInSabayCoin")
    @Expose
    public Double priceInSC;
    @SerializedName("priceInSabayGold")
    @Expose
    public Double priceInSG;
    @SerializedName("paymentServiceProvider")
    @Expose
    public List<PaymentServiceProvider> paymentServiceProvider;

    public final static Creator<Property> CREATOR = new Creator<Property>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Property createFromParcel(Parcel in) {
            return new Property(in);
        }

        public Property[] newArray(int size) {
            return (new Property[size]);
        }

    };

    protected Property(Parcel in) {
        this.packageCode = ((String) in.readValue((String.class.getClassLoader())));
        this.displayName = ((String) in.readValue((String.class.getClassLoader())));
        this.priceInSC = ((Double) in.readValue((Double.class.getClassLoader())));
        this.priceInSG = ((Double) in.readValue((Double.class.getClassLoader())));
        in.readList(this.paymentServiceProvider, (PaymentServiceProvider.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public Property() {
    }

    /**
     * @param packageCode
     * @param displayName
     * @param priceInSC
     * @param priceInSG
     */
    public Property(String packageCode, String displayName, Double priceInSC, Double priceInSG) {
        super();
        this.packageCode = packageCode;
        this.displayName = displayName;
        this.priceInSC = priceInSC;
        this.priceInSG = priceInSG;
    }

    public Property withPackageCode(String packageCode) {
        this.packageCode = packageCode;
        return this;
    }


    public Property withName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Property withPriceInSc(Double priceInSC) {
        this.priceInSC = priceInSC;
        return this;
    }

    public Property withPriceInSG(Double priceInSG) {
        this.priceInSG = priceInSG;
        return this;
    }

    public  Property withPaymentServiceProvider(List<PaymentServiceProvider> paymentServiceProvider) {
        this.paymentServiceProvider = paymentServiceProvider;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("packageCode", packageCode).append("displayName", displayName)
                .append("priceInSC", priceInSC).append("priceInSG", priceInSG)
                .append("paymentServiceProvider", paymentServiceProvider)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(priceInSC).append(priceInSG).append(displayName).append(paymentServiceProvider).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Property)) {
            return false;
        }
        Property rhs = ((Property) other);
        return new EqualsBuilder().append(packageCode, rhs.packageCode).append(priceInSC, rhs.priceInSC)
                .append(priceInSG, rhs.priceInSG).append(displayName, rhs.displayName).append(paymentServiceProvider, rhs.paymentServiceProvider).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(packageCode);
        dest.writeValue(displayName);
        dest.writeValue(priceInSC);
        dest.writeValue(priceInSG);
        dest.writeValue(paymentServiceProvider);
    }

    public int describeContents() {
        return 0;
    }

    public String toSabayCoin() {
        return  this.priceInSC + " SC";
    }

    public String toRoundSabayCoin() {
        return  Math.round(this.priceInSC) + " SC";
    }

    public String toRoundSabayGold() {
        return  Math.round(this.priceInSG) + " SG";
    }
}
