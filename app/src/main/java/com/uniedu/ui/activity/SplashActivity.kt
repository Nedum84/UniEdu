package com.ng.rapetracker.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.navigation.ActivityNavigator
import com.ng.rapetracker.R
import com.uniedu.utils.ClassSharedPreferences


class SplashActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)


        Handler().postDelayed({
            if (ClassSharedPreferences(this).isLoggedIn()){
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this,   ActivityLogin::class.java))
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 1000)
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

}
