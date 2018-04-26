package com.olayinkapeter.myreddit;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //No content view defined. Only Splash Theme applied.

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}