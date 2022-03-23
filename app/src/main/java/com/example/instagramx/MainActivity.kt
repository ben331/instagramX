package com.example.instagramx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.instagramx.databinding.ActivityMainBinding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {

    //Fragments
    private lateinit var postFragment:PostFragment
    private lateinit var homeFragment: HomeFragment
    private lateinit var profileFragment: ProfileFragment

    //Binding
    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(500) //To Show Splash or simulate back operations
        setTheme(R.style.Theme_InstagramX) //Reset Theme to clean SplashScreen
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postFragment = PostFragment.newInstance()
        homeFragment = HomeFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()

        binding.navigator.setOnItemSelectedListener { menuItem->
            when(menuItem.itemId) {
                R.id.postItem ->
                    showFragment(postFragment)
                R.id.homeItem ->
                    showFragment(homeFragment)
                R.id.profileItem->
                    showFragment(profileFragment)
            }
            true
        }
    }

    init{
        //Go to Login or keep in home depending if is first login or not
        if(SingleLoggedUser.user == null){ //Now is always false, but with serialization it could be true
            val intent = Intent(this, LoginActivity::class.java)
            val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),::onResultLogin)
            launcher.launch(intent)
        }else{
            loadHome()
        }
    }

    private fun onResultLogin(result:ActivityResult) {
        val json:String? = result.data?.extras?.getString("username")
        val user = json?.let<String, User?> { Json.decodeFromString(it) }
        SingleLoggedUser.user = user!!
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