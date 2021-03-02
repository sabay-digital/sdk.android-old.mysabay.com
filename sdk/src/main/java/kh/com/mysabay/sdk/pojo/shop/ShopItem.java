package kh.com.mysabay.sdk.pojo.shop;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Tan Phirum on 3/13/20
 * Gmail phirumtan@gmail.com
 */
public class ShopItem implements Parcelable {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("salePrice")
    @Expose
    public Double salePrice;
    @SerializedName("cost")
    @Expose
    public Double cost;
    @SerializedName("currencyCode")
    @Expose
    public String currencyCode;
    @SerializedName("isActive")
    @Expose
    public boolean isActive;
    @SerializedName("serviceCode")
    @Expose
    public String serviceCode;

    @SerializedName("properties")
    @Expose
    public Property properties;

    public final static Creator<ShopItem> CREATOR = new Creator<ShopItem>() {
        @SuppressWarnings({
                "unchecked"
        })
        public ShopItem createFromParcel(Parcel in) {
            return new ShopItem(in);
        }

        public ShopItem[] newArray(int size) {
            return (new ShopItem[size]);
        }

    };

    protected ShopItem(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.isActive = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.cost = ((Double) in.readValue((Double.class.getClassLoader())));
        this.salePrice = ((Double) in.readValue((Double.class.getClassLoader())));
        this.currencyCode = ((String) in.readValue((String.class.getClassLoader())));
        this.serviceCode = ((String) in.readValue((String.class.getClassLoader())));
        this.properties = ((Property) in.readValue((Property.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public ShopItem() {
    }

    /**
     * @param id
     * @param salePrice
     * @param currencyCode
     * @param properties
     */
    public ShopItem(String id, Double salePrice, String currencyCode, Property properties) {
        super();
        this.id = id;
        this.salePrice = salePrice;
        this.currencyCode = currencyCode;
        this.properties = properties;
    }

    public ShopItem(String id, Property properties) {
        super();
        this.id = id;
        this.properties = properties;
    }

    public ShopItem(String id) {
        this.id = id;
    }

    public ShopItem withId(String id) {
        this.id = id;
        return this;
    }

    public ShopItem withSalePrice(Double salePrice) {
        this.salePrice = salePrice;
        return this;
    }


    public  ShopItem withProperties(Property properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("salePrice", salePrice)
                .append("cost", cost).append("salePrice", salePrice).append("currencyCode", currencyCode)
                .append("properties", properties).append("serviceCode", serviceCode).append("isActive", isActive)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(salePrice).append(properties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ShopItem)) {
            return false;
        }
        ShopItem rhs = ((ShopItem) other);
        return new EqualsBuilder().append(id, rhs.id).append(salePrice, rhs.salePrice)
                .append(serviceCode, serviceCode).append(cost, rhs.cost)
                .append(currencyCode, rhs.currencyCode).append(properties, rhs.properties).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(salePrice);
        dest.writeValue(cost);
        dest.writeValue(isActive);
        dest.writeValue(currencyCode);
        dest.writeValue(serviceCode);
        dest.writeValue(salePrice);
        dest.writeValue(properties);
    }

    public int describeContents() {
        return 0;
    }

    public String toUSDPrice() {
        return String.format("%s %s", "$", this.salePrice);
    }
}