package kh.com.mysabay.sdk.callback;

public interface LoginListener {
    void loginSuccess(String accessToken);

    void loginFailed(Object error);
}
