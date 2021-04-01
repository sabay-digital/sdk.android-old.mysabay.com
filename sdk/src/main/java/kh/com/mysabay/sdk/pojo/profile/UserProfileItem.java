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

import java.util.List;

import kh.com.mysabay.sdk.pojo.shop.PaymentServiceProvider;

/**
 * Created by Tan Phirum on 3/10/20
 * Gmail phirumtan@gmail.com
 */
public class UserProfileItem implements Parcelable {

    @SerializedName("userID")
    @Expose
    public Integer userID;
    @SerializedName("givenName")
    @Expose
    public String givenName;
    @SerializedName("wallet")
    @Expose
    public List<Wallet> wallet;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("localPayEnabled")
    @Expose
    public Boolean localPayEnabled;

    @SerializedName("persona")
    @Expose
    public Persona persona;

    public final static Creator<UserProfileItem> CREATOR = new Creator<UserProfileItem>() {


        @NotNull
        @Contract("_ -> new")
        @SuppressWarnings({
                "unchecked"
        })
        public UserProfileItem createFromParcel(Parcel in) {
            return new UserProfileItem(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public UserProfileItem[] newArray(int size) {
            return (new UserProfileItem[size]);
        }

    };

    protected UserProfileItem(@NotNull Parcel in) {
        this.userID = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.givenName = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.localPayEnabled = ((Boolean) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.wallet, (Wallet.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public UserProfileItem() {
    }

    /**
     * @param userID
     * @param wallet
     * @param localPayEnabled
     * @param status
     */
    public UserProfileItem(Integer userID, String givenName, List<Wallet> wallet, Boolean localPayEnabled, Integer status) {
        super();
        this.userID = userID;
        this.givenName = givenName;
        this.wallet = wallet;
        this.status = status;
        this.localPayEnabled = localPayEnabled;
    }

    public UserProfileItem withUserId(Integer userID) {
        this.userID = userID;
        return this;
    }

    public UserProfileItem withWallet(List<Wallet> wallet) {
        this.wallet = wallet;
        return this;
    }

    public  UserProfileItem withGivenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    public UserProfileItem withStatus(Integer status) {
        this.status = status;
        return this;
    }

    public UserProfileItem withLocalPayEnabled(Boolean localPayEnabled ) {
        this.localPayEnabled = localPayEnabled;
        return this;
    }

    public UserProfileItem withPersona(Persona persona  ) {
        this.persona = persona;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("userID", userID)
                .append("givenName", givenName).append("wallet", wallet)
                .append("status", status).append("localPayEnabled", localPayEnabled)
                .append("persona", persona)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(userID).append(givenName).append(wallet).append(status).append(localPayEnabled).append(persona).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UserProfileItem)) {
            return false;
        }
        UserProfileItem rhs = ((UserProfileItem) other);
        return new EqualsBuilder().append(userID, rhs.userID).append(wallet, rhs.wallet).append(givenName, rhs.givenName)
                .append(status, rhs.status).append(localPayEnabled, rhs.localPayEnabled).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(userID);
        dest.writeValue(givenName);
        dest.writeValue(wallet);
        dest.writeValue(status);
        dest.writeValue(localPayEnabled);
        dest.writeValue(persona);
    }

    public int describeContents() {
        return 0;
    }

}