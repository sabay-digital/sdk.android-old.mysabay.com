package kh.com.mysabay.sdk;

import com.mysabay.sdk.GetMatomoTrackingIdQuery;

import org.apache.commons.lang3.StringUtils;
import org.matomo.sdk.TrackerBuilder;
import org.matomo.sdk.extra.MatomoApplication;

import kh.com.mysabay.sdk.callback.DataCallback;
import kh.com.mysabay.sdk.utils.LogUtil;

public class SdkApplication extends MatomoApplication {
    public int id;

    @Override
    public TrackerBuilder onCreateTrackerConfig() {

        MySabaySDK.getInstance().getMatomoTrackingId(MySabaySDK.getInstance().serviceCode(), new DataCallback<GetMatomoTrackingIdQuery.Sso_service>() {
            @Override
            public void onSuccess(GetMatomoTrackingIdQuery.Sso_service response) {
                if (response != null) {
                    if (!StringUtils.isEmpty(response.matomoTrackingID()))
                        id = Integer.parseInt(response.matomoTrackingID());
                }
            }

            @Override
            public void onFailed(Object error) {
                LogUtil.info("Error", error.toString());
            }
        });

        return TrackerBuilder.createDefault(
                MySabaySDK.getInstance().getSdkConfiguration().isSandBox ? "https://ma.testing.sabay.com/matomo.php": "https://ma.sabay.com/matomo.php",
                id);
    }
}