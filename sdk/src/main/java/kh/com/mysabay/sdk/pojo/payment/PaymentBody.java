package kh.com.mysabay.sdk.pojo.payment;

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
public class PaymentBody implements Parcelable {

    @SerializedName("uuid")
    @Expose
    public String uuid;
    @SerializedName("amount")
    @Expose
    public String amount;
    @SerializedName("psp_code")
    @Expose
    public String pspCode;
    @SerializedName("psp_asset_code")
    @Expose
    public String pspAssetCode;
    @SerializedName("package_code")
    @Expose
    public String packageCode;
    public final static Creator<PaymentBody> CREATOR = new Creator<PaymentBody>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PaymentBody createFromParcel(Parcel in) {
            return new PaymentBody(in);
        }

        public PaymentBody[] newArray(int size) {
            return (new PaymentBody[size]);
        }

    };

    protected PaymentBody(Parcel in) {
        this.uuid = ((String) in.readValue((String.class.getClassLoader())));
        this.amount = ((String) in.readValue((String.class.getClassLoader())));
        this.pspCode = ((String) in.readValue((String.class.getClassLoader())));
        this.pspAssetCode = ((String) in.readValue((String.class.getClassLoader())));
        this.packageCode = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     *
     * @param uuid
     * @param amount
     * @param pspCode
     * @param pspAssetCode
     * @param packageCode
     */
    public PaymentBody(String uuid, String amount, String pspCode, String pspAssetCode, String packageCode) {
        super();
        this.uuid = uuid;
        this.amount = amount;
        this.pspCode = pspCode;
        this.pspAssetCode = pspAssetCode;
        this.packageCode = packageCode;
    }

    public PaymentBody() {}

    public PaymentBody withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public PaymentBody withAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public PaymentBody withPspCode(String pspCode) {
        this.pspCode = pspCode;
        return this;
    }

    public PaymentBody withPspAssetCode(String pspAssetCode) {
        this.pspAssetCode = pspAssetCode;
        return this;
    }

    public PaymentBody withPackageCode(String pspAssetCode) {
        this.packageCode = pspAssetCode;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("uuid", uuid).append("amount", amount).append("pspCode", pspCode).append("pspAssetCode", pspAssetCode).append("packageCode", packageCode).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(amount).append(pspCode).append(pspAssetCode).append(packageCode).append(uuid).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PaymentBody)) {
            return false;
        }
        PaymentBody rhs = ((PaymentBody) other);
        return new EqualsBuilder().append(amount, rhs.amount).append(pspCode, rhs.pspCode).append(pspAssetCode, rhs.pspAssetCode).append(packageCode, rhs.packageCode).append(uuid, rhs.uuid).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(uuid);
        dest.writeValue(amount);
        dest.writeValue(pspCode);
        dest.writeValue(pspAssetCode);
        dest.writeValue(packageCode);
    }

    public int describeContents() {
        return 0;
    }

}