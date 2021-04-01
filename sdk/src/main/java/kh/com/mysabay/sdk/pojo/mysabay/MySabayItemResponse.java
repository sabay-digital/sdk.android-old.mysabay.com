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

import java.util.List;

/**
 * Created by Chhoeun Songheng on 01/05/21
 * Gmail chhoeunsongheng@gmail.com
 */
public class MySabayItemResponse implements Parcelable {

    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("providers")
    @Expose
    public List<ProviderResponse> providers;

    public final static Creator<MySabayItemResponse> CREATOR = new Creator<MySabayItemResponse>() {


        @NotNull
        @Contract("_ -> new")
        public MySabayItemResponse createFromParcel(Parcel in) {
            return new MySabayItemResponse(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public MySabayItemResponse[] newArray(int size) {
            return (new MySabayItemResponse[size]);
        }

    };

    protected MySabayItemResponse(@NotNull Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.providers, (ProviderResponse.class.getClassLoader()));
    }

    /**
     * @param providers
     */
    public MySabayItemResponse(String type, List<ProviderResponse> providers) {
        super();
        this.type = type;
        this.providers = providers;
    }

    public MySabayItemResponse withType(String type) {
        this.type = type;
        return this;
    }

    public MySabayItemResponse withProvider(List<ProviderResponse> providers) {
        this.providers = providers;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("type", type).append("providers", providers).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(providers).toHashCode();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MySabayItemResponse) == false) {
            return false;
        }
        MySabayItemResponse rhs = ((MySabayItemResponse) other);
        return new EqualsBuilder().append(type, rhs.type).append(providers, rhs.providers).isEquals();
    }

    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeList(providers);
    }

    public int describeContents() {
        return 0;
    }
}
