package com.nationalhandloomcorp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.nationalhandloomcorp.R;
import com.nationalhandloomcorp.util.Global;
import io.fabric.sdk.android.Fabric;



public class SplashActivity extends AppCompatActivity {

    static int SPLASH_TIME_OUT = 4000;
    Global global;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_splash);
        global = new Global(this);
        //  setAnimation();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

//******change activity here*******
        bindLogo();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run()
            {
                if(global.getPrefBoolean("Verify", false))
                {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    // Redirect to Login Activity
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

    private void bindLogo(){
        // Start animating the image
        final ImageView splash = (ImageView) findViewById(R.id.imagelogo2);
        final AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(700);
        final AlphaAnimation animation2 = new AlphaAnimation(1.0f, 0.2f);
        animation2.setDuration(700);
        //animation1 AnimationListener
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start animation2 when animation1 ends (continue)
                splash.startAnimation(animation2);
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {}
            @Override
            public void onAnimationStart(Animation arg0) {}
        });

        //animation2 AnimationListener
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start animation1 when animation2 ends (repeat)
                splash.startAnimation(animation1);
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {}
            @Override
            public void onAnimationStart(Animation arg0) {}
        });

        splash.startAnimation(animation1);
    }

}
