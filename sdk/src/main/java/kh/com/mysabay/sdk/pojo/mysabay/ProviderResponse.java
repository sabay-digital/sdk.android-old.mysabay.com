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
 * Created by Tan Phirum on 01/05/21
 * Gmail chhoeunsongheng@gmail.com
 */
public class ProviderResponse implements Parcelable {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("ssnAccountPk")
    @Expose
    public String ssnAccountPk;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("info")
    @Expose
    public Info info;
    @SerializedName("label")
    @Expose
    public String label;
    @SerializedName("value")
    @Expose
    public Double value;
    @SerializedName("issueCurrencies")
    @Expose
    public List<String> issueCurrencies;
    @SerializedName("additionalHeader")
    @Expose
    public Object additionalHeader;
    @SerializedName("additionalBody")
    @Expose
    public Object additionalBody;
    @SerializedName("packageId")
    @Expose
    public String packageId;

    public final static Creator<ProviderResponse> CREATOR = new Creator<ProviderResponse>() {

        @NotNull
        @Contract("_ -> new")
        public ProviderResponse createFromParcel(Parcel in) {
            return new ProviderResponse(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public ProviderResponse[] newArray(int size) {
            return (new ProviderResponse[size]);
        }

    };

    protected ProviderResponse(@NotNull Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.code = ((String) in.readValue((String.class.getClassLoader())));
        this.ssnAccountPk = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.info = ((Info) in.readValue((Info.class.getClassLoader())));
        this.label = ((String) in.readValue((String.class.getClassLoader())));
        this.value = ((Double) in.readValue((Double.class.getClassLoader())));
        this.packageId = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.issueCurrencies, (String.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public ProviderResponse() {
    }

    /**
     * @param id
     * @param name
     * @param code
     * @param ssnAccountPk
     * @param type
     * @param label
     * @param value
     * @param issueCurrencies
     * @param info
     */
    public ProviderResponse(String id, String name, String code, String ssnAccountPk, String type, String label, Double value, List<String> issueCurrencies, Info info) {
        super();
        this.id = id;
        this.name = name;
        this.code = code;
        this.ssnAccountPk = ssnAccountPk;
        this.type = type;
        this.label = label;
        this.value = value;
        this.issueCurrencies = issueCurrencies;
        this.info = info;
    }

    public ProviderResponse withId(String id) {
        this.id = id;
        return this;
    }

    public ProviderResponse withName(String name) {
        this.name = name;
        return this;
    }

    public ProviderResponse withCode(String code) {
        this.code = code;
        return this;
    }

    public ProviderResponse withSsnAccountPK(String ssnAccountPk) {
        this.ssnAccountPk = ssnAccountPk;
        return this;
    }

    public ProviderResponse withType(String type) {
        this.type = type;
        return this;
    }

    public ProviderResponse withLabel(String label) {
        this.label = label;
        return this;
    }

    public ProviderResponse withValue(Double value) {
        this.value = value;
        return this;
    }

    public ProviderResponse withInfo(List<String> issueCurrencies) {
        this.issueCurrencies = issueCurrencies;
        return this;
    }

    public ProviderResponse withInfo(Info info) {
        this.info = info;
        return this;
    }

    public ProviderResponse withPackageId(String packageId){
        this.packageId = packageId;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("name", name).append("code", code)
                .append("ssnAccountPk", ssnAccountPk).append("type", type).append("info", info)
                .append("packageId", packageId).append("label", label).append("value", value)
                .append("issueCurrencies", issueCurrencies).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(packageId).append(name).append(code).append(ssnAccountPk).append(type).append(info)
                .append(label).append(value).append(issueCurrencies).toHashCode();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ProviderResponse)) {
            return false;
        }
        ProviderResponse rhs = ((ProviderResponse) other);
        return new EqualsBuilder().append(id, rhs.id).append(packageId, rhs.packageId).append(name, rhs.name).append(code, rhs.code)
                .append(ssnAccountPk, rhs.ssnAccountPk).append(type, rhs.type).append(info, rhs.info)
                .append(label, rhs.label).append(value, rhs.value).append(issueCurrencies, rhs.issueCurrencies).isEquals();
    }

    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(packageId);
        dest.writeValue(name);
        dest.writeValue(code);
        dest.writeValue(ssnAccountPk);
        dest.writeValue(type);
        dest.writeValue(info);
        dest.writeValue(label);
        dest.writeValue(value);
        dest.writeValue(issueCurrencies);
    }

    public int describeContents() {
        return 0;
    }

}