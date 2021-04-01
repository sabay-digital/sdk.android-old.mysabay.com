package kh.com.mysabay.sdk.pojo.mysabay;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MySabayAccount implements Parcelable {

    public String username;
    public String password;
    public String phoneNumber;
    public String otpCode;

    public final static Creator<MySabayAccount> CREATOR = new Creator<MySabayAccount>() {


        @NotNull
        @Contract("_ -> new")
        public MySabayAccount createFromParcel(Parcel in) {
            return new MySabayAccount(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public MySabayAccount[] newArray(int size) {
            return (new MySabayAccount[size]);
        }

    };

    protected MySabayAccount(@NotNull Parcel in) {
        this.username = ((String) in.readValue((String.class.getClassLoader())));
        this.password = ((String) in.readValue((String.class.getClassLoader())));;
        this.phoneNumber = ((String) in.readValue((String.class.getClassLoader())));
        this.otpCode = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public MySabayAccount() {
    }

    /**
     * @param username
     * @param password
     * @param phoneNumber
     */
    public MySabayAccount(String username, String password, String phoneNumber) {
        super();
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("username", username).append("password", password)
                .append("phoneNumber", phoneNumber).append("otpCode", otpCode).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(username).append(phoneNumber).append(phoneNumber).append(otpCode).toHashCode();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MySabayAccount) == false) {
            return false;
        }
        MySabayAccount rhs = ((MySabayAccount) other);
        return new EqualsBuilder().append(username, rhs.username).append(password, rhs.password).append(otpCode, rhs.otpCode).isEquals();
    }

    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(phoneNumber);
        dest.writeString(otpCode);
    }

    public int describeContents() {
        return 0;
    }

}