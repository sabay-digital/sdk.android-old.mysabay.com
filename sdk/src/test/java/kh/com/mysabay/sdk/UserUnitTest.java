package kh.com.mysabay.sdk;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.mysabay.sdk.LoginWithMySabayMutation;
import com.mysabay.sdk.LoginWithPhoneMutation;
import com.mysabay.sdk.VerifyOtpCodMutation;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Predicate;
import kh.com.mysabay.sdk.utils.Utils;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertNotNull;

public class UserUnitTest {

    ApolloClient apolloClient;
    @Rule
    public final MockWebServer server = new MockWebServer();
    private static final String STORE_NAME = "stores.json";

    @Before
    public void setup() {
        apolloClient =ApolloClient.builder()
                .serverUrl("https://gateway.master.sabay.com/graphql/")
                .okHttpClient(getClientConfig("mysabay"))
                .build();
    }

    @After
    public void tearDown() {
        try {
            server.shutdown();
        } catch (IOException ignored) {
        }
    }

    public OkHttpClient getClientConfig(String serviceCode) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                HttpLoggingInterceptor.Level.NONE);

        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request newRequest = null;
                        newRequest  = chain.request().newBuilder()
                                .addHeader("service-code", serviceCode)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .dispatcher(new Dispatcher(Utils.immediateExecutorService()))
                .build();
    }

    @Test
    public void testLogin() throws Exception {
        LoginWithPhoneMutation loginWithPhoneMutation = new LoginWithPhoneMutation("85512808080");
        Rx2Apollo.from(apolloClient.mutate(loginWithPhoneMutation))
                .test()
                .await()
                .assertNoErrors()
                .assertValue(new Predicate<Response<LoginWithPhoneMutation.Data>>() {
                    @Override
                    public boolean test(Response<LoginWithPhoneMutation.Data> dataResponse) throws Exception {
                        assertNotNull(dataResponse);
                        return true;
                    }
                });

    }

    @Test
    public void testLoginWithMySabay() throws Exception {
        LoginWithMySabayMutation loginWithPhoneMutation = new LoginWithMySabayMutation("test_pro05", "11111111");
        Rx2Apollo.from(apolloClient.mutate(loginWithPhoneMutation))
                .test()
                .await()
                .assertNoErrors()
                .assertValue(new Predicate<Response<LoginWithMySabayMutation.Data>>() {
                    @Override
                    public boolean test(Response<LoginWithMySabayMutation.Data> dataResponse) throws Exception {
                        assertNotNull(dataResponse.getData());
                        return true;
                    }
                });

    }

    @Test
    public void testVerfifyLogin() throws Exception {
        VerifyOtpCodMutation verifyOtpCodMutation = new VerifyOtpCodMutation("85512808080", "111111");
        Rx2Apollo.from(apolloClient.mutate(verifyOtpCodMutation))
                .test()
                .await()
                .assertNoErrors()
                .assertValue(new Predicate<Response<VerifyOtpCodMutation.Data>>() {
                    @Override
                    public boolean test(Response<VerifyOtpCodMutation.Data> dataResponse) throws Exception {
                        assertNotNull(dataResponse);
                        return true;
                    }
                });

    }
}
