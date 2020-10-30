package com.example.barbershopadmin.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barbershopadmin.Dashboard;
import com.example.barbershopadmin.R;
import com.example.barbershopadmin.Registration.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {
    Animation topAnim,bottomAnim;
    TextView slogan;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        slogan=findViewById(R.id.slogan);
        image=findViewById(R.id.logo);

//Set animation to elements
        image.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);
        Thread thread=new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                    if(FirebaseAuth.getInstance().getCurrentUser() !=null) {
                        Intent intent = new Intent(Splash.this, Dashboard.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        thread.start();

    }
}