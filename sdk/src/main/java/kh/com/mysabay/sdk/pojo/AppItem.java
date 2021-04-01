package kh.com.mysabay.sdk.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Tan Phirum on 3/9/20
 * Gmail phirumtan@gmail.com
 */
public class AppItem implements Parcelable {

    @SerializedName("verifyMySabay")
    @Expose
    public Boolean verifyMySabay;

    @SerializedName("mySabayUsername")
    @Expose
    public String mySabayUsername;

    @SerializedName("token")
    @Expose
    public String token;

    @SerializedName("refreshToken")
    @Expose
    public String refreshToken;

    @SerializedName("uuid")
    @Expose
    public String uuid;

    @SerializedName("expire")
    @Expose
    public long expire;

    @SerializedName("mysabay_user_id")
    @Expose
    public Integer mysabayUserId;
    @SerializedName("enable_local_pay")
    @Expose
    public boolean enableLocalPay;

    public AppItem(String mySabayUsername, Boolean verifyMySabay, String token, String refreshToken, String uuid, long expire) {
        this.mySabayUsername = mySabayUsername;
        this.verifyMySabay = verifyMySabay;
        this.token = token;
        this.refreshToken = refreshToken;
        this.uuid = uuid;
        this.expire = expire;
    }

    public AppItem(String mySabayUsername, Boolean verifyMySabay, String token, String refreshToken, long expire) {
        this.mySabayUsername = mySabayUsername;
        this.verifyMySabay = verifyMySabay;
        this.token = token;
        this.refreshToken = refreshToken;
        this.expire = expire;
    }

    public AppItem(String token, String refreshToken, long expire) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.expire = expire;
    }

    public AppItem(String mySabayUsername, Boolean verifyMySabay, String token, String refreshToken) {
        this(mySabayUsername,verifyMySabay, token, refreshToken, null, 0);
    }


    protected AppItem(@NotNull Parcel in) {
        mySabayUsername = in.readString();
        verifyMySabay = in.readBoolean();
        token = in.readString();
        refreshToken = in.readString();
        uuid = in.readString();
        expire = in.readLong();
        mysabayUserId = in.readInt();
        enableLocalPay = in.readBoolean();
    }

    public static final Creator<AppItem> CREATOR = new Creator<AppItem>() {
        @Override
        public AppItem createFromParcel(Parcel in) {
            return new AppItem(in);
        }

        @Override
        public AppItem[] newArray(int size) {
            return new AppItem[size];
        }
    };

    public AppItem withVerifyMySabay(Boolean verifyMySabay) {
        this.verifyMySabay = verifyMySabay;
        return this;
    }

    public AppItem withToken(String token) {
        this.token = token;
        return this;
    }

    public AppItem withRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public AppItem withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public AppItem withExpired(long expire) {
        this.expire = expire;
        return this;
    }

    public AppItem withMySabayUserId(Integer mySabayUserId) {
        this.mysabayUserId = mySabayUserId;
        return this;
    }

    public AppItem withEnableLocaPay(Boolean enableLocalPay) {
        this.enableLocalPay = enableLocalPay;
        return this;
    }

    public AppItem withMySabayUsername(String mySabayUsername) {
        this.mySabayUsername = mySabayUsername;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(verifyMySabay);
        dest.writeString(token);
        dest.writeString(refreshToken);
        dest.writeString(uuid);
        dest.writeLong(expire);
        dest.writeValue(mysabayUserId);
        dest.writeValue(enableLocalPay);
        dest.writeString(mySabayUsername);
    }
}
