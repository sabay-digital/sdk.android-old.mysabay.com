package kh.com.mysabay.sdk.pojo.onetime;

import android.webkit.JavascriptInterface;
import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;
import kh.com.mysabay.sdk.Globals;
import kh.com.mysabay.sdk.pojo.payment.SubscribePayment;


public class OneTime {

    @JavascriptInterface
    public void postMessage(String msg) {
        Gson gson = new Gson();
        Data data = gson.fromJson(msg, Data.class);
        EventBus.getDefault().post(new SubscribePayment(Globals.ONE_TIME, data, null));
    }
}