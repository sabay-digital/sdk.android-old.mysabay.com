package kh.com.mysabay.sdk.callback;

public interface DataCallback<T> {

    void onSuccess(T response);
    void onFailed(Object error);
}
