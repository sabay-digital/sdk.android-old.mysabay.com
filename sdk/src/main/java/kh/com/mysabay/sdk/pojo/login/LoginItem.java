package kh.com.mysabay.sdk.pojo.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Tan Phirum on 3/10/20
 * Gmail phirumtan@gmail.com
 */
public class LoginItem implements Parcelable {

    @SerializedName("access_token")
    @Expose
    public String accessToken;
    @SerializedName("verify_code")
    @Expose
    public int verifyCode;
    @SerializedName("expire")
    @Expose
    public String  expire;
    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("phone")
    @Expose
    public String phone;

    @SerializedName("verifyMySabay")
    @Expose
    public Boolean verifyMySabay;

    @SerializedName("mySabayUsername")
    @Expose
    public String mySabayUsername;

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

    protected LoginItem(@NotNull Parcel in) {
        this.accessToken = ((String) in.readValue((String.class.getClassLoader())));
        this.verifyCode = ((int) in.readValue((int.class.getClassLoader())));
        this.expire = ((String) in.readValue((String.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.mySabayUsername = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public LoginItem() {
    }

    /**
     * @param verifyCode
     * @param expire
     * @param accessToken
     * @param message
     */
    public LoginItem(String accessToken, int verifyCode, String expire, String message) {
        super();
        this.accessToken = accessToken;
        this.verifyCode = verifyCode;
        this.expire = expire;
        this.message = message;
    }

    public LoginItem withAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public LoginItem withVerifyCode(int verifyCode) {
        this.verifyCode = verifyCode;
        return this;
    }

    public LoginItem withExpire(String expire) {
        this.expire = expire;
        return this;
    }

    public LoginItem withMessage(String message) {
        this.message = message;
        return this;
    }

    public LoginItem withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public LoginItem withMySabayUserName(String mySabayUserName) {
        this.mySabayUsername = mySabayUserName;
        return this;
    }

    public LoginItem withVerifyMySabay(Boolean verifyMySabay) {
        this.verifyMySabay = verifyMySabay;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("accessToken", accessToken).append("mySabayUsername", mySabayUsername).append("verifyCode", verifyCode).append("expire", expire).append("message", message).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(verifyCode).append(accessToken).append(message).append(expire).append(verifyMySabay).append(mySabayUsername).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LoginItem)) {
            return false;
        }
        LoginItem rhs = ((LoginItem) other);
        return new EqualsBuilder().append(verifyCode, rhs.verifyCode).append(accessToken, rhs.accessToken).append(message, rhs.message)
                .append(expire, rhs.expire).append(expire, rhs.expire).append(verifyMySabay, rhs.verifyMySabay).append(mySabayUsername, rhs.mySabayUsername).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(accessToken);
        dest.writeValue(verifyCode);
        dest.writeValue(expire);
        dest.writeValue(message);
        dest.writeValue(verifyMySabay);
        dest.writeValue(mySabayUsername);
    }

    public int describeContents() {
        return 0;
    }

    public static class LoginResponseItem {
    }

}