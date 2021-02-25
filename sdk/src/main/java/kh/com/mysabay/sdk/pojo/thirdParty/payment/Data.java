package kh.com.mysabay.sdk.pojo.thirdParty.payment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Tan Phirum on 4/1/20
 * Gmail phirumtan@gmail.com
 */
public class Data implements Parcelable {

    @SerializedName("requestUrl")
    @Expose
    public String requestUrl;
    @SerializedName("publicKey")
    @Expose
    public String publicKey;
    @SerializedName("signature")
    @Expose
    public String signature;
    @SerializedName("hash")
    @Expose
    public String hash;
    @SerializedName("redirect")
    @Expose
    public String redirect;
    @SerializedName("additionalHeader")
    @Expose
    public Object additionalHeader;
    @SerializedName("additionalBody")
    @Expose
    public Object additionalBody;

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

    protected Data(Parcel in) {
        this.requestUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.publicKey = ((String) in.readValue((String.class.getClassLoader())));
        this.signature = ((String) in.readValue((String.class.getClassLoader())));
        this.hash = ((String) in.readValue((String.class.getClassLoader())));
        this.redirect = ((String) in.readValue((String.class.getClassLoader())));
        this.additionalBody= ((Object) in.readValue((WingAuthorization.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Data() {
    }

    /**
     *
     * @param requestUrl
     * @param publicKey
     * @param signature
     * @param hash
     * @param redirect
     */
    public Data(String requestUrl, String publicKey, String signature, String hash, String redirect, Object additionalBodies) {
        super();
        this.requestUrl = requestUrl;
        this.publicKey = publicKey;
        this.signature = signature;
        this.hash = hash;
        this.redirect = redirect;
        this.additionalBody = additionalBodies;
    }

    public Data withRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    public Data withPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public Data withSignature(String signature) {
        this.signature = signature;
        return this;
    }

    public Data withHash(String hash) {
        this.hash = hash;
        return this;
    }

    public Data withRedirect(String redirect) {
        this.redirect = redirect;
        return this;
    }

    public Data withAdditionalBody (Object additionalBody) {
        this.additionalBody = additionalBody;
        return this;

    }

    public Data withAdditionalHeader (Object additionalHeader) {
        this.additionalHeader = additionalHeader;
        return this;

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("requestUrl", requestUrl).append("publicKey", publicKey).append("signature", signature)
                .append("hash", hash).append("redirect", redirect).append("additionalBodies", additionalBody).append("additionalHeader", additionalHeader).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(publicKey).append(signature).append(requestUrl).append(hash).append(redirect).append(additionalBody).append(additionalHeader).toHashCode();
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
        return new EqualsBuilder().append(publicKey, rhs.publicKey).append(signature, rhs.signature).append(requestUrl, rhs.requestUrl)
                .append(hash, rhs.hash).append(redirect, rhs.redirect).append(additionalBody, rhs.additionalBody).append(additionalHeader, rhs.additionalHeader).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(requestUrl);
        dest.writeValue(publicKey);
        dest.writeValue(signature);
        dest.writeValue(hash);
        dest.writeValue(redirect);
        dest.writeValue(additionalBody);
        dest.writeValue(additionalHeader);
    }

    public int describeContents() {
        return 0;
    }

}