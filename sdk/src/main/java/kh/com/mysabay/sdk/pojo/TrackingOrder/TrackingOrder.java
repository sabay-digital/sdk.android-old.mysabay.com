package kh.com.mysabay.sdk.pojo.TrackingOrder;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.matomo.sdk.extra.EcommerceItems;

import kh.com.mysabay.sdk.pojo.profile.Wallet;

public class TrackingOrder implements Parcelable {

    public String orderId;
    public EcommerceItems ecommerceItems;
    public Integer grandTotal;
    public Integer subTotal;
    public Integer shipping;
    public Integer tax;
    public Integer discount;
    public final static Creator<TrackingOrder> CREATOR = new Creator<TrackingOrder>() {


        @NotNull
        @Contract("_ -> new")
        public TrackingOrder createFromParcel(Parcel in) {
            return new TrackingOrder(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public TrackingOrder[] newArray(int size) {
            return (new TrackingOrder[size]);
        }

    };

    protected TrackingOrder(@NotNull Parcel in) {
        this.orderId = ((String) in.readValue((Double.class.getClassLoader())));
        this.ecommerceItems = ((EcommerceItems) in.readValue((String.class.getClassLoader())));
        this.grandTotal = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.subTotal = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.shipping = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.tax = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.discount = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public TrackingOrder(String orderId, EcommerceItems ecommerceItems, Integer grandTotal, Integer subTotal, Integer shipping, Integer tax, Integer discount) {
        this.orderId = orderId;
        this.ecommerceItems = ecommerceItems;
        this.grandTotal = grandTotal;
        this.subTotal = subTotal;
        this.shipping = shipping;
        this.tax = tax;
        this.discount = discount;
    }

    public TrackingOrder() {}

    public TrackingOrder withOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public TrackingOrder withEcommerceItems(EcommerceItems ecommerceItems) {
        this.ecommerceItems = ecommerceItems;
        return this;
    }

    public TrackingOrder withGrandTotal(Integer grandTotal) {
        this.grandTotal = grandTotal;
        return this;
    }

    public TrackingOrder withSubTotal(Integer subTotal) {
        this.subTotal = subTotal;
        return this;
    }

    public TrackingOrder withTax(Integer tax) {
        this.tax = tax;
        return this;
    }

    public TrackingOrder withDiscount(Integer discount) {
        this.discount = discount;
        return this;
    }

    public TrackingOrder withShipping(Integer shipping) {
        this.shipping = shipping;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("orderId", orderId).append("ecommerceItems", ecommerceItems)
                .append("grandTotal", grandTotal).append("subTotal", subTotal).append("tax", tax)
                .append("discount", discount)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(orderId).append(ecommerceItems).append(grandTotal).append(subTotal)
                .append(tax).append(shipping).append(shipping).toHashCode();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TrackingOrder)) {
            return false;
        }
        TrackingOrder rhs = ((TrackingOrder) other);
        return new EqualsBuilder().append(orderId, rhs.orderId).append(ecommerceItems, rhs.ecommerceItems).append(grandTotal, rhs.ecommerceItems)
                .append(subTotal, rhs.subTotal).append(tax, rhs.tax).append(discount, rhs.discount).append(shipping, rhs.shipping).isEquals();
    }

    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeValue(orderId);
        dest.writeValue(ecommerceItems);
        dest.writeValue(grandTotal);
        dest.writeValue(subTotal);
        dest.writeValue(tax);
        dest.writeValue(discount);
        dest.writeValue(shipping);
    }

    public int describeContents() {
        return 0;
    }

}
