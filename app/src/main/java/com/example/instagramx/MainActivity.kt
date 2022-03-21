package com.example.instagramx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var loggedUsername:String? = null

    //Fragments
    private lateinit var postFragment:PostFragment
    private lateinit var homeFragment: HomeFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(500) //To Show Splash or simulate back operations
        setTheme(R.style.Theme_InstagramX) //Reset Theme to clean SplashScreen
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        postFragment = PostFragment.newInstance()
        homeFragment = HomeFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()

        navigator.setOnItemSelectedListener { menuItem->
            if(menuItem.itemId==R.id.postItem){
                showFragment(postFragment)
            }else if(menuItem.itemId==R.id.homeItem){
                showFragment(homeFragment)
            }else{
                showFragment(profileFragment)
            }
            true
        }


        //Go to Login or keep in home depending if is first login or not
        if(loggedUsername==null){
            var intent = Intent(this, LoginActivity::class.java)
            val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),::onResultLogin)
            launcher.launch(intent)
        }else{
            loadHome()
        }
    }

    private fun onResultLogin(result:ActivityResult) {
        loggedUsername = result.data?.extras?.getString("username")
        loadHome()
    }

    private fun showFragment(fragment:Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    private fun loadHome() {
        showFragment(homeFragment)
    }
}