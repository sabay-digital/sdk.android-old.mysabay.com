package kh.com.mysabay.sdk;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.akexorcist.localizationactivity.core.LanguageSetting;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class Globals {

    public static final String EXT_KEY_LANG = "key_lang";
    public static final String LANGUAGE_KHMER = "km";
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String PREF_NAME = "pref_sabaysdk";
    public static final String EXT_KEY_NAME = "ext_name";
    public static final String EXT_KEY_ID = "ext_id";
    private static final String EXT_KEY_TOKEN = "EXT_KEY_TOKEN";
    private static final String EXT_KEY_DISCOUNT = "EXT_KEY_DISCOUNT";
    public static final String EXT_KEY_DATA = "ext_key_data";
    public static final String EXT_KEY_APP_ITEM = "EXT_KEY_APP_ITEM";
    public static final String EXT_KEY_PAYMENT_METHOD = "EXT_KEY_PAYMENT_METHOD";
    public static final String APP_IN_PURCHASE = "APP_IN_PURCHASE";
    public static final String MY_SABAY = "MY_SABAY";
    public static final String ONE_TIME = "ONE_TIME";
    public static final String MY_SABAY_PROVIDER = "pre-auth";
    public static final String ONE_TIME_PROVIDER = "onetime";
    public static final String IAP_PROVIDER = "iap";

    /**
     * Method replace fragment with set title to activity and add backpress
     */

    public static void initAddFragment(FragmentManager manager,
                                       Fragment fragment, String tag) {
        initAddFragment(manager, fragment, tag, false);
    }

    public static void initAddFragment(FragmentManager manager,
                                       Fragment fragment, String tag, boolean isBack) {
        initAddFragment(manager, fragment, 0, tag, isBack);
    }

    public static void initAddFragment(FragmentManager manager, Fragment fragment,
                                       int mainView, String tag, boolean isBack) {
        initAddFragment(manager, fragment, mainView, tag, isBack, null);
    }

    public static void initAddFragment(FragmentManager manager, Fragment fragment,
                                       int mainView, String tag, boolean isBack, Handler handler) {
        if (mainView == 0)
            mainView = R.id.container;
        if (handler != null) {
            int localMainView = mainView;
            handler.postDelayed(() -> {
                if (isBack) {
                    manager.beginTransaction().replace(localMainView, fragment, tag)
                            .addToBackStack(tag)
                            .commitAllowingStateLoss();
                } else {
                    manager.beginTransaction().replace(localMainView, fragment, tag)
                            .commitAllowingStateLoss();
                }
            }, 600);
        } else {
            if (isBack) {
                manager.beginTransaction().replace(mainView, fragment, tag)
                        .addToBackStack(tag)
                        .commitAllowingStateLoss();
            } else {
                manager.beginTransaction().replace(mainView, fragment, tag)
                        .commitAllowingStateLoss();
            }
        }

    }

   /* public static void showTermAndCondDialog(Activity activity, String object, String tag) {
        showTermAndCondDialog(activity, object)
                .onNegative((dialog, which) -> dialog.dismiss())
                .onPositive((dialog, which) ->
                {
                    dialog.dismiss();
                    if (StringUtils.equalsIgnoreCase(tag, RegisterFragment.TAG)) {
                        MessageUtil.displayToast(activity, R.string.create_new_account_success);
                        if (activity instanceof LoginActivity)
                            activity.onBackPressed();
                    }

                }).show();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static MaterialDialog.Builder showTermAndCondDialog(Activity activity, String object) {
        WebView webView = new WebView(activity);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(object, "text/html", "utf-8");

        return new MaterialDialog.Builder(activity)
                .typeface(FontUtils.getTypefaceKhmer(activity), FontUtils.getTypefaceKhmer(activity))
                .title(R.string.title_term_and_condition)
                .customView(webView, true)
                .autoDismiss(false)
                .cancelable(false)
                .positiveText(activity.getString(R.string.title_ok))
                .negativeText(R.string.cancel);
    }*/

    public static boolean isLocalizationKhmer(Context context) {
        return StringUtils.equalsIgnoreCase(getLanguage(context).getLanguage(), LANGUAGE_KHMER);
    }

    // Get current language
    public static Locale getLanguage(Context context) {
        return LanguageSetting.getLanguage(context);
    }
/*
    public static void saveUsername(Activity context, String username) {
        SharedPreferences.Editor editor = Apps.getInstance().getPreferences(context).edit();
        editor.putString(Globals.EXT_KEY_NAME, username);
        editor.apply();
    }

    public static String getUsername(Activity context) {
        return Apps.getInstance().getPreferences(context).getString(EXT_KEY_NAME, null);
    }

    public static void saveUserId(Activity context, String username) {
        SharedPreferences.Editor editor = Apps.getInstance().getPreferences(context).edit();
        editor.putString(Globals.EXT_KEY_ID, username);
        editor.apply();
    }

    public static String getUserId(Activity context) {
        return Apps.getInstance().getPreferences(context).getString(EXT_KEY_ID, null);
    }

    public static void saveToken(Activity context, String token) {
        SharedPreferences.Editor editor = Apps.getInstance().getPreferences(context).edit();
        editor.putString(Globals.EXT_KEY_TOKEN, token);
        editor.apply();
    }

    public static String getToken(Activity context) {
        return Apps.getInstance().getPreferences(context).getString(EXT_KEY_TOKEN, null);
    }

    public static void saveIsDiscount(Activity context, int isDiscount) {
        SharedPreferences.Editor editor = Apps.getInstance().getPreferences(context).edit();
        editor.putBoolean(Globals.EXT_KEY_DISCOUNT, BooleanUtils.toBoolean(isDiscount));
        editor.apply();
    }

    public static boolean getIsDiscount(Activity context) {
        return Apps.getInstance().getPreferences(context).getBoolean(EXT_KEY_DISCOUNT, false);
    }*/

}
