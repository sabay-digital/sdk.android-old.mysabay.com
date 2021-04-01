package kh.com.mysabay.sdk.pojo.shop;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class PaymentServiceProvider implements Parcelable {

    @SerializedName("groupId")
    @Expose
    public String groupId;
    @SerializedName("providers")
    @Expose
    public List<Provider> providers;

    public final static Parcelable.Creator<PaymentServiceProvider> CREATOR = new Parcelable.Creator<PaymentServiceProvider>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PaymentServiceProvider createFromParcel(Parcel in) {
            return new PaymentServiceProvider(in);
        }

        public PaymentServiceProvider[] newArray(int size) {
            return (new PaymentServiceProvider[size]);
        }

    };

    protected PaymentServiceProvider(Parcel in) {
        this.groupId = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.providers, (Provider.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public PaymentServiceProvider() {
    }

    /**
     * @param groupId
     * @param providers
     */
    public PaymentServiceProvider(String groupId, List<Provider> providers) {
        super();
        this.groupId = groupId;
        this.providers = providers;
    }

    public PaymentServiceProvider withGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public PaymentServiceProvider withProviders(List<Provider> providers) {
        this.providers = providers;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("groupId", groupId).append("providers", providers).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(groupId).append(providers).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PaymentServiceProvider)) {
            return false;
        }
        PaymentServiceProvider rhs = ((PaymentServiceProvider) other);
        return new EqualsBuilder().append(groupId, rhs.groupId).append(providers, rhs.providers).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(groupId);
        dest.writeValue(providers);
    }

    public int describeContents() {
        return 0;
    }
}
