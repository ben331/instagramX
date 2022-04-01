package com.example.instagramx

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.instagramx.databinding.ActivityMainBinding
import kotlinx.coroutines.Job

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

        //Ask permissions
        requestPermissions(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ),1)

        //Bindings
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postFragment = PostFragment.newInstance()
        homeFragment = HomeFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()

        postFragment.listener = this

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

        //Go to Login or keep in home depending if is first login or not
        if(SingleLoggedUser.user == null){
            val intent = Intent(this, LoginActivity::class.java)
            val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),::onResultLogin)
            launcher.launch(intent)
        }else{
            loadHome()
        }
    }

    private fun onResultLogin(result:ActivityResult) {
        val username:String? = result.data?.extras?.getString("username")
        val password:String? = result.data?.extras?.getString("pass")
        SingleLoggedUser.user = User(username!!,password!!)
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

    override fun newPost(post: Post?) {
        if(post!=null){
            homeFragment.newPost(post)
        }
    }

    override fun loadPost(postingCoroutine: Job) {
        val loaderView = LoadingViewModel(postingCoroutine)
        loaderView.dotsLive.observe(this, Observer{

        })
    }

    override fun doneChanges(done: Boolean) {
        if(done){
            Toast.makeText(this, "Saved changes",3)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1){
            for(result in grantResults){
                if (result==-1)
                    this.finish(); System.exit(0);
            }
        }
    }
}