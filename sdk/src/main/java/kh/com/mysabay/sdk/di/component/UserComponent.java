package kh.com.mysabay.sdk.di.component;

import dagger.Subcomponent;
import kh.com.mysabay.sdk.di.scopes.ActivityScope;
import kh.com.mysabay.sdk.ui.activity.LoginActivity;
import kh.com.mysabay.sdk.ui.fragment.LoginFragment;
import kh.com.mysabay.sdk.ui.fragment.MySabayCreateFragment;
import kh.com.mysabay.sdk.ui.fragment.MySabayLoginFm;
import kh.com.mysabay.sdk.ui.fragment.MySabayLoginFragment;
import kh.com.mysabay.sdk.ui.fragment.VerifiedFragment;

/**
 * Created by Tan Phirum on 2019-12-27
 * Gmail phirumtan@gmail.com
 */
// Classes annotated with @ActivityScope are scoped to the graph and the same
// instance of that type is provided every time the type is requested.
@ActivityScope
@Subcomponent
public interface UserComponent {

    // Factory that is used to create instances of this subcomponent
    @Subcomponent.Factory
    interface Factory {
        UserComponent create();
    }

    void inject(LoginActivity loginActivity);

    void inject(LoginFragment loginFragment);

    void inject(MySabayLoginFm mySabayLoginFm);

    void inject(VerifiedFragment verifiedFragment);

    void inject(MySabayLoginFragment mySabayLoginFragment);

    void inject(MySabayCreateFragment mySabayCreateFragment);
}
