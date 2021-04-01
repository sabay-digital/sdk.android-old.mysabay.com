package kh.com.mysabay.sdk.pojo.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

public class LoginResponseItem implements Parcelable {

    @SerializedName("access_token")
    @Expose
    public String accessToken;
    @SerializedName("refresh_token")
    @Expose
    public String refreshToken;
    @SerializedName("expire")
    @Expose
    public int expire;

    public final static Creator<LoginResponseItem> CREATOR = new Creator<LoginResponseItem>() {


        @SuppressWarnings({
                "unchecked"
        })
        public LoginResponseItem createFromParcel(Parcel in) {
            return new LoginResponseItem(in);
        }

        public LoginResponseItem[] newArray(int size) {
            return (new LoginResponseItem[size]);
        }

    };

    protected LoginResponseItem(@NotNull Parcel in) {
        this.accessToken = ((String) in.readValue((String.class.getClassLoader())));
        this.refreshToken = ((String) in.readValue((String.class.getClassLoader())));
        this.expire = ((int) in.readValue((int.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public LoginResponseItem() {
    }

    /**
     * @param accessToken
     * @param refreshToken
     * @param expire
     * @param message
     */
    public LoginResponseItem(String accessToken, String refreshToken, int expire, String message) {
        super();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expire = expire;
    }

    public LoginResponseItem withAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public LoginResponseItem withRefreshCode(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public LoginResponseItem withExpire(int expire) {
        this.expire = expire;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("accessToken", accessToken).append("refreshToken", refreshToken).append("expire", expire).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(refreshToken).append(accessToken).append(expire).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LoginResponseItem)) {
            return false;
        }
        LoginResponseItem rhs = ((LoginResponseItem) other);
        return new EqualsBuilder().append(refreshToken, rhs.refreshToken).append(accessToken, rhs.accessToken).append(expire, rhs.expire).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(accessToken);
        dest.writeValue(refreshToken);
        dest.writeValue(expire);
    }

    public int describeContents() {
        return 0;
    }

}
