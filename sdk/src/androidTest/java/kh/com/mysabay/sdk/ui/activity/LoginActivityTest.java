package kh.com.mysabay.sdk.ui.activity;

import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.SdkConfiguration;
import kh.com.mysabay.sdk.di.component.UserComponent;
import kh.com.mysabay.sdk.ui.fragment.LoginFragment;
import kh.com.mysabay.sdk.ui.fragment.VerifiedFragment;
import kh.com.mysabay.sdk.utils.SdkTheme;

import static android.support.test.espresso.Espresso.onView;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> activityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void init() {

        FragmentManager fm = activityActivityTestRule.getActivity().getSupportFragmentManager();
        fm.beginTransaction().add(new LoginFragment(), "1").add(new VerifiedFragment(), "2").commit();
        fm.executePendingTransactions();
    }

    @Test
    public void testLoginFrament() {
        onView(ViewMatchers.withId(R.id.container)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}