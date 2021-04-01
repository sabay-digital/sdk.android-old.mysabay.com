package kh.com.mysabay.sdk.pojo.shop;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Tan Phirum on 3/13/20
 * Gmail phirumtan@gmail.com
 */
public class Data implements Parcelable {

    @SerializedName("package_code")
    @Expose
    public String packageCode;
    @SerializedName("package_id")
    @Expose
    public String packageId;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("price_in_usd")
    @Expose
    public Float priceInUsd;
    @SerializedName("price_in_sabay_coin")
    @Expose
    public Float priceInSc;
    @SerializedName("price_in_sabay_gold")
    @Expose
    public Float priceInSG;
    @SerializedName("label")
    @Expose
    public String label;

    public final static Creator<Data> CREATOR = new Creator<Data>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        public Data[] newArray(int size) {
            return (new Data[size]);
        }

    };

    protected Data(Parcel in) {
        this.packageCode = ((String) in.readValue((String.class.getClassLoader())));
        this.packageId = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.priceInUsd = ((Float) in.readValue((Float.class.getClassLoader())));
        this.priceInSc = ((Float) in.readValue((Float.class.getClassLoader())));
        this.priceInSG = ((Float) in.readValue((Float.class.getClassLoader())));
        this.label = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Data() {
    }

    /**
     *
     * @param packageCode
     * @param packageId
     * @param name
     * @param priceInUsd
     * @param priceInSc
     * @param priceInSG
     * @param label
     */
    public Data(String packageCode, String packageId, String name, Float priceInUsd, Float priceInSc, Float priceInSG, String label) {
        super();
        this.packageCode = packageCode;
        this.packageId = packageId;
        this.name = name;
        this.priceInUsd = priceInUsd;
        this.priceInSc = priceInSc;
        this.priceInSG = priceInSG;
        this.label = label;
    }

    public Data withPackageCode(String packageCode) {
        this.packageCode = packageCode;
        return this;
    }

    public Data withPackageId(String packageId) {
        this.packageId = packageId;
        return this;
    }

    public Data withName(String name) {
        this.name = name;
        return this;
    }

    public Data withPriceInUsd(Float priceInUsd) {
        this.priceInUsd = priceInUsd;
        return this;
    }

    public Data withPriceInSc(Float priceInSc) {
        this.priceInSc = priceInSc;
        return this;
    }

    public Data withPriceInSG(Float priceInSG) {
        this.priceInSG = priceInSG;
        return this;
    }

    public Data withPackageLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("packageCode", packageCode).append("packageId", packageId).append("name", name).append("priceInUsd", priceInUsd).append("priceInSc", priceInSc).append("priceInSG", priceInSG).append("label", label).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(packageId).append(packageCode).append(priceInUsd).append(priceInSc).append(priceInSG).append(name).append(label).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Data)) {
            return false;
        }
        Data rhs = ((Data) other);
        return new EqualsBuilder().append(packageId, rhs.packageId).append(packageCode, rhs.packageCode).append(priceInUsd, rhs.priceInUsd).append(priceInSc, rhs.priceInSc).append(priceInSG, rhs.priceInSG).append(name, rhs.name).append(label, rhs.label).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(packageCode);
        dest.writeValue(packageId);
        dest.writeValue(name);
        dest.writeValue(priceInUsd);
        dest.writeValue(priceInSc);
        dest.writeValue(priceInSG);
        dest.writeValue(label);
    }

    public int describeContents() {
        return 0;
    }

}
