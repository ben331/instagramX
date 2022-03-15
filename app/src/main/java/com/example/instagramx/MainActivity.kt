package com.example.instagramx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private var isFirstLogin = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(500) //To Show Splash or simulate back operations
        setTheme(R.style.Theme_InstagramX) //Reset Theme to clean SplashScreen
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Go to Login or Home depending if is first login or not
        if(isFirstLogin){
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }else{
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}