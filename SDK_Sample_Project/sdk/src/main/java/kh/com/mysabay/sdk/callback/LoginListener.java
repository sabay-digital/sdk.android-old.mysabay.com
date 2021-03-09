package kh.com.mysabay.sdk.callback;

/**
 * Created by Tan Phirum on 3/27/20
 * Gmail phirumtan@gmail.com
 */
public interface LoginListener {
    void loginSuccess(String accessToken);

    void loginFailed(Object error);
}
