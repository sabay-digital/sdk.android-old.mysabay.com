package kh.com.mysabay.sdk.pojo.profile;

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
 * Created by Tan Phirum on 3/13/20
 * Gmail phirumtan@gmail.com
 */
public class Balance implements Parcelable {

    @SerializedName("coin")
    @Expose
    public Double coin;
    @SerializedName("gold")
    @Expose
    public Double gold;
    public final static Creator<Balance> CREATOR = new Creator<Balance>() {


        @NotNull
        @Contract("_ -> new")
        public Balance createFromParcel(Parcel in) {
            return new Balance(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public Balance[] newArray(int size) {
            return (new Balance[size]);
        }

    };

    protected Balance(@NotNull Parcel in) {
        this.coin = ((Double) in.readValue((Double.class.getClassLoader())));
        this.gold = ((Double) in.readValue((Double.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Balance() {
    }

    /**
     *
     * @param coin
     * @param gold
     */
    public Balance(Double coin, Double gold) {
        super();
       this.coin = coin;
       this.gold = gold;
    }

    public Balance withCoin(Double coin) {
        this.coin = coin;
        return this;
    }

    public Balance withGold(Double gold) {
        this.gold = gold;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("coin", coin).append("gold", gold).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(coin).append(gold).toHashCode();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Balance)) {
            return false;
        }
        Balance rhs = ((Balance) other);
        return new EqualsBuilder().append(coin, rhs.coin).append(gold, rhs.gold).isEquals();
    }

    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeValue(coin);
        dest.writeValue(gold);
    }

    public int describeContents() {
        return 0;
    }

}
