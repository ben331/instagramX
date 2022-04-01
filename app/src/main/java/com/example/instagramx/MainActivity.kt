package com.example.instagramx

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.instagramx.databinding.ActivityMainBinding
import kotlinx.coroutines.Job
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), PostFragment.OnPostListener, ProfileFragment.OnDoneChanges {

    //Fragments
    private lateinit var postFragment:PostFragment
    private lateinit var homeFragment: HomeFragment
    private lateinit var profileFragment: ProfileFragment

    //Binding
    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        //Thread.sleep(500) //To Show Splash or simulate back operations
        super.onCreate(savedInstanceState)

        //Bindings
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Instance fragments
        postFragment = PostFragment.newInstance()
        homeFragment = HomeFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()

        //Subscription -> MainActivity Observes postFragment in an Observer Pattern
        postFragment.listener = this
        //Subscription -> MainActivity Observes profileFragment in an Observer Pattern
        profileFragment.listener = this

        //Define function of navigator button
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

        //Ask permissions
        requestPermissions(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ),1)

        //Go to Login or keep in home depending if is first login or not
        if(SingleLoggedUser.user == null){
            val intent = Intent(this, LoginActivity::class.java)
            val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),::onResultLogin)
            launcher.launch(intent)
        }else {
            loadHome()
        }
    }

    private fun onResultLogin(result:ActivityResult) {
        val username:String? = result.data?.extras?.getString("username")
        val password:String? = result.data?.extras?.getString("pass")
        SingleLoggedUser.user = User(username!!,password!!)
        loadHome()
    }

    private fun loadHome() {
        showFragment(homeFragment)
    }

    private fun showFragment(fragment:Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    //Result after ask for permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1){
            for(result in grantResults){
                if (result==-1)
                    this.finish(); exitProcess(0)
            }
        }
    }

    //Overwritten Methods as listener PostFragment.OnPostListener ----------------------------------
    override fun newPost(post: Post?) {
        if(post!=null){
            homeFragment.newPost(post)
        }
    }

    override fun loadPost(postingCoroutine: Job) {
        val loaderView = LoadingViewModel(postingCoroutine)
        loaderView.dotsLive.observe(this) {

        }
    }

    //ProfileFragment.OnDoneChanges
    override fun doneChanges(done: Boolean) {
        if(done){
            Toast.makeText(this, "Saved changes",3)
        }
    }
    //----------------------------------------------------------------------------------------------
}