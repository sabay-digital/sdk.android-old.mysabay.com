package kh.com.mysabay.sdk.pojo.profile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Tan Phirum on 01/14/20
 * Gmail phirumtan@gmail.com
 */
public class Persona implements Parcelable {

    @SerializedName("uuid")
    @Expose
    public String uuid;
    @SerializedName("serviceCode")
    @Expose
    public String serviceCode;
    @SerializedName("mysabayUserID")
    @Expose
    public String mysabayUserID;

    protected Persona(@NotNull Parcel in) {
        this.uuid = ((String) in.readValue((String.class.getClassLoader())));
        this.serviceCode = ((String) in.readValue((String.class.getClassLoader())));
        this.mysabayUserID = ((String) in.readValue((String.class.getClassLoader())));
    }

    public static final Creator<Persona> CREATOR = new Creator<Persona>() {
        @Override
        public Persona createFromParcel(Parcel in) {
            return new Persona(in);
        }

        @Override
        public Persona[] newArray(int size) {
            return new Persona[size];
        }
    };

    public Persona withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public Persona withServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
        return this;
    }

    public Persona withMySabayUserId(String mysabayUserID) {
        this.mysabayUserID = mysabayUserID;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("uuid", uuid).
                append("serviceCode", serviceCode).append("mysabayUserID", mysabayUserID).toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeValue(serviceCode);
        dest.writeValue(mysabayUserID);
    }
}