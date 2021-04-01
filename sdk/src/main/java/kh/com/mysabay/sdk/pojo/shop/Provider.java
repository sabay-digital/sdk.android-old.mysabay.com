package kh.com.mysabay.sdk.pojo.shop;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Provider implements Parcelable {

    @SerializedName("label")
    @Expose
    public String label;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public Double value;
    @SerializedName("value")

    public final static Creator<Provider> CREATOR = new Creator<Provider>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Provider createFromParcel(Parcel in) {
            return new Provider(in);
        }

        public Provider[] newArray(int size) {
            return (new Provider[size]);
        }

    };

    protected Provider(Parcel in) {
        this.label = ((String) in.readValue((String.class.getClassLoader())));
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.value = ((Double) in.readValue((Double.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Provider() {
    }

    public Provider(String label, String id, Double value) {
        super();
        this.label = label;
        this.id = id;
        this.value = value;
    }

    public Provider withLabel(String label) {
        this.label = label;
        return this;
    }

    public Provider withId(String id) {
        this.id = id;
        return this;
    }

    public Provider withValue(Double value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("label", label).append("id", id).append("value", value).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(label).append(id).append(value).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Provider)) {
            return false;
        }
        Provider rhs = ((Provider) other);
        return new EqualsBuilder().append(label, rhs.label).append(id, rhs.label).append(value, rhs.value).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(label);
        dest.writeValue(id);
        dest.writeValue(value);
    }

    public int describeContents() {
        return 0;
    }

}
