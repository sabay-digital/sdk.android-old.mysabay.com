package kh.com.mysabay.sdk.pojo.mysabay;

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
 * Created by Tan Phirum on 01/05/21
 * Gmail chhoeunsongheng@gmail.com
 */
public class Info implements Parcelable {

    @SerializedName("logo")
    @Expose
    public String logo;
    public final static Creator<Info> CREATOR = new Creator<Info>() {


        @NotNull
        @Contract("_ -> new")
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public Info[] newArray(int size) {
            return (new Info[size]);
        }

    };

    protected Info(@NotNull Parcel in) {
        this.logo = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Info() {
    }

    /**
     * @param logo
     */
    public Info(String logo) {
        super();
        this.logo = logo;
    }

    public Info withLogo(String logo) {
        this.logo = logo;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("logo", logo).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(logo).toHashCode();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Info) == false) {
            return false;
        }
        Info rhs = ((Info) other);
        return new EqualsBuilder().append(logo, rhs.logo).isEquals();
    }

    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeValue(logo);
    }

    public int describeContents() {
        return 0;
    }

}