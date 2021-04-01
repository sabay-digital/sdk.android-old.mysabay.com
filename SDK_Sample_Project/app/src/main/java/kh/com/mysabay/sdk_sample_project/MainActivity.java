package kh.com.mysabay.sdk_sample_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import kh.com.mysabay.sdk.MySabaySDK;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn_show_sdk);

        btn.setOnClickListener(v -> {
            MySabaySDK.getInstance().showLoginView();
        });
    }
}
