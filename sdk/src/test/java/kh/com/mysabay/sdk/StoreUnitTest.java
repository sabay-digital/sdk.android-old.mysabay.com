package kh.com.mysabay.sdk;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.mysabay.sdk.GetProductsByServiceCodeQuery;
import com.mysabay.sdk.type.Store_PagerInput;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Predicate;
import kh.com.mysabay.sdk.utils.Utils;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertEquals;

public class StoreUnitTest {

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
    public void apolloGetStore() throws Exception {
        Store_PagerInput pager = Store_PagerInput.builder().page(1).limit(20).build();
        GetProductsByServiceCodeQuery query = new GetProductsByServiceCodeQuery("aog", new Input<>(pager, true));
        ApolloCall<GetProductsByServiceCodeQuery.Data> apolloCall = apolloClient.query(query);
        server.equals(mockResponse(STORE_NAME).setResponseCode(504).setBody("Hello"));

        Rx2Apollo.from(apolloCall)
                .test()
                .await()
                .assertNoErrors()
                .assertValue(new Predicate<Response<GetProductsByServiceCodeQuery.Data>>() {
                    @Override
                    public boolean test(Response<GetProductsByServiceCodeQuery.Data> dataResponse) throws Exception {
                        assertEquals(dataResponse.getData().store_listProduct().products().get(1).currencyCode(), "USD");
                        return true;
                    }
                });
    }

    @Test
    public void testGettingStores() throws IOException, InterruptedException {
        Store_PagerInput pager = Store_PagerInput.builder().page(1).limit(20).build();
        GetProductsByServiceCodeQuery query = GetProductsByServiceCodeQuery.builder().serviceCode("aog").pager(pager).build();

        Rx2Apollo
                .from(apolloClient.prefetch(query))
                .test()
                .await()
                .assertNoErrors()
                .assertComplete();
    }

    private MockResponse mockResponse(String fileName) throws IOException {
        return new MockResponse().setChunkedBody(Utils.readFileToString(getClass(), "/" + fileName), 32);
    }
}
