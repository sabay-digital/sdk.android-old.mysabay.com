package kh.com.mysabay.sdk.webservice;

import com.apollographql.apollo.ApolloClient;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kh.com.mysabay.sdk.BuildConfig;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.webservice.api.StoreApi;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by phirum on 11/4/15.
 */
@Module
public class   ServiceGenerator {
    private static String TAG = ServiceGenerator.class.getName();

    public static final String CONNECT_TIMEOUT = "CONNECT_TIMEOUT";
    public static final String READ_TIMEOUT = "READ_TIMEOUT";
    public static final String WRITE_TIMEOUT = "WRITE_TIMEOUT";

    @Singleton
    @Provides
    public Retrofit instanceUser() {
        return new Retrofit.Builder()
                .baseUrl(MySabaySDK.getInstance().userApiUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getClientConfig(MySabaySDK.getInstance().serviceCode()))
                .build();
    }

    @Singleton
    @Provides
    public ApolloClient instanceUserWithPolloClient() {
        return ApolloClient.builder()
                .serverUrl(MySabaySDK.getInstance().userApiUrl())
                .okHttpClient(getClientConfig(MySabaySDK.getInstance().serviceCode()))
                .build();
    }

    @Singleton
    @Provides
    public Retrofit instanceStore() {
        return new Retrofit.Builder()
                .baseUrl(MySabaySDK.getInstance().storeApiUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getClientConfig(MySabaySDK.getInstance().serviceCode()))
                .build();
    }

    @Singleton
    @Provides
    @NotNull
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
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = null;
                        newRequest  = chain.request().newBuilder()
                                    .addHeader("service-code", serviceCode)
                                    .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();
    }

    @Singleton
    @Provides
    public StoreApi createStoreApi() {
        return instanceStore().create(StoreApi.class);
    }

   /* Interceptor timeoutInterceptor = chain -> {
        Request request = chain.request();

        int connectTimeout = chain.connectTimeoutMillis();
        int readTimeout = chain.readTimeoutMillis();
        int writeTimeout = chain.writeTimeoutMillis();

        String connectNew = request.header(CONNECT_TIMEOUT);
        String readNew = request.header(READ_TIMEOUT);
        String writeNew = request.header(WRITE_TIMEOUT);

        if (!TextUtils.isEmpty(connectNew)) {
            connectTimeout = Integer.parseInt(connectNew);
        }
        if (!TextUtils.isEmpty(readNew)) {
            readTimeout = Integer.parseInt(readNew);
        }
        if (!TextUtils.isEmpty(writeNew)) {
            writeTimeout = Integer.parseInt(writeNew);
        }

        Request.Builder builder = request.newBuilder();
        builder.removeHeader(CONNECT_TIMEOUT);
        builder.removeHeader(READ_TIMEOUT);
        builder.removeHeader(WRITE_TIMEOUT);

        return chain
                .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .proceed(builder.build());
    };*/


   /* private static Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File("", "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Log.e("ServiceGenerator", "Could not create Cache!");
        }
        return cache;
    }*/

  /*  @NotNull
    @Contract(pure = true)
    public static Interceptor provideOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build();

            request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build();
            return chain.proceed(request);
        };
    }*/

}