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
public class Data implements Parcelable {

    @SerializedName("access_token")
    @Expose
    public String accessToken;
    @SerializedName("verify_code")
    @Expose
    public int verifyCode;
    @SerializedName("expire")
    @Expose
    public int expire;
    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("phone")
    @Expose
    public String phone;

    @SerializedName("app_secret")
    @Expose
    public String appSecret;


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

    protected Data(@NotNull Parcel in) {
        this.accessToken = ((String) in.readValue((String.class.getClassLoader())));
        this.verifyCode = ((int) in.readValue((int.class.getClassLoader())));
        this.expire = ((int) in.readValue((int.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Data() {
    }

    /**
     * @param verifyCode
     * @param expire
     * @param accessToken
     * @param message
     */
    public Data(String accessToken, int verifyCode, int expire, String message) {
        super();
        this.accessToken = accessToken;
        this.verifyCode = verifyCode;
        this.expire = expire;
        this.message = message;
    }

    public Data withAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public Data withVerifyCode(int verifyCode) {
        this.verifyCode = verifyCode;
        return this;
    }

    public Data withExpire(int expire) {
        this.expire = expire;
        return this;
    }

    public Data withMessage(String message) {
        this.message = message;
        return this;
    }

    public Data withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Data withAppSecret(String appSecret) {
        this.appSecret = appSecret;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("accessToken", accessToken).append("verifyCode", verifyCode).append("expire", expire).append("message", message).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(verifyCode).append(accessToken).append(message).append(expire).toHashCode();
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
        return new EqualsBuilder().append(verifyCode, rhs.verifyCode).append(accessToken, rhs.accessToken).append(message, rhs.message).append(expire, rhs.expire).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(accessToken);
        dest.writeValue(verifyCode);
        dest.writeValue(expire);
        dest.writeValue(message);
    }

    public int describeContents() {
        return 0;
    }

    public static class LoginResponseItem {
    }
}