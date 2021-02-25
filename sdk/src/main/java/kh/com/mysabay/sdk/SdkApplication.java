package kh.com.mysabay.sdk;

import org.matomo.sdk.TrackerBuilder;
import org.matomo.sdk.extra.MatomoApplication;

import kh.com.mysabay.sdk.utils.LogUtil;

public class SdkApplication extends MatomoApplication {
    public int id;

    public void createConfig() {
        onCreateTrackerConfig();
    }

    @Override
    public TrackerBuilder onCreateTrackerConfig() {
        LogUtil.info("Side Id", String.valueOf(id));
        return TrackerBuilder.createDefault("https://matomo.testing.sabay.com/matomo.php", id);
    }
}
