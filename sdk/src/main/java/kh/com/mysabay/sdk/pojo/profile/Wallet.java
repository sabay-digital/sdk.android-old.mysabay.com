package kh.com.mysabay.sdk.pojo.profile;

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
public class Wallet implements Parcelable {

    @SerializedName("balance")
    @Expose
    public Double balance;
    @SerializedName("assetCode")
    @Expose
    public String assetCode;
    public final static Creator<Wallet> CREATOR = new Creator<Wallet>() {


        @NotNull
        @Contract("_ -> new")
        public Wallet createFromParcel(Parcel in) {
            return new Wallet(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public Wallet[] newArray(int size) {
            return (new Wallet[size]);
        }

    };

    protected Wallet(@NotNull Parcel in) {
        this.balance = ((Double) in.readValue((Double.class.getClassLoader())));
        this.assetCode = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Wallet() {
    }

    /**
     * @param balance
     * @param assetCode
     */
    public Wallet(Double balance, String assetCode) {
        super();
       this.balance = balance;
       this.assetCode = assetCode;
    }

    public Wallet withBalance(Double balance) {
        this.balance = balance;
        return this;
    }

    public Wallet withAssetCode(String assetCode) {
        this.assetCode = assetCode;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("balance", balance).append("assetCode", assetCode).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(balance).append(assetCode).toHashCode();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Wallet)) {
            return false;
        }
        Wallet rhs = ((Wallet) other);
        return new EqualsBuilder().append(balance, rhs.balance).append(assetCode, rhs.assetCode).isEquals();
    }

    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeValue(balance);
        dest.writeValue(assetCode);
    }

    public int describeContents() {
        return 0;
    }

    public String toSabayCoin() {
        return (String.format("%,.2f", balance)) + " SC";
    }

    public String toSabayGold() {
        return (String.format("%,.2f", balance)) + " SG";
    }

}
