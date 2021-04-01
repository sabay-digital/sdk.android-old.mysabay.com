package kh.com.mysabay.sdk.utils;

public class CurrencyUtils {

    public static String toSabayCoin(double sabayCoin) {
        return String.format("%s %s", Math.round(sabayCoin),  "SC");
    }

    public static String toSabayGold(double sabayGold) {
        return String.format("%s %s", Math.round(sabayGold), "SG");
    }
}
