package com.example.instagramx

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.instagramx.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.Job
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity(), PostFragment.OnPostListener, ProfileFragment.OnDoneChanges, MenuItem.OnMenuItemClickListener,
    PopupMenu.OnMenuItemClickListener {

    //Constants
    companion object{
        const val CAMERA_INTENT:Int = 1
        const val GALLERY_INTENT:Int = 1
    }

    //Fragments
    private lateinit var postFragment: PostFragment
    private lateinit var homeFragment: HomeFragment
    private lateinit var profileFragment: ProfileFragment

    //Binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    //File
    private var file: File? = null
    private lateinit var uri: Uri

    //Vars
    private var havePermissions = false

    override fun onCreate(savedInstanceState: Bundle?) {
        //Thread.sleep(500) //To Show Splash or simulate back operations
        super.onCreate(savedInstanceState)

        //Bindings
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        //Instance fragments
        postFragment = PostFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()
        homeFragment = HomeFragment.newInstance()
        homeFragment.adapter = PostAdapter()

        //Subscription -> MainActivity Observes postFragment in an Observer Pattern
        postFragment.listener = this
        //Subscription -> MainActivity Observes profileFragment in an Observer Pattern
        profileFragment.listener = this

        //Define function of navigator button
        binding.navigator.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.postItem ->{
                    openCamara()
                }
                R.id.homeItem -> {
                    binding.toolbar.visibility = View.VISIBLE
                    showFragment(homeFragment)
                }
                R.id.profileItem -> {
                    binding.toolbar.visibility = View.GONE
                    showFragment(profileFragment)
                }
            }
            true
        }

        //Read User
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val currentUser = preferences.getString("currentUser","NO_DATA")
        havePermissions = preferences.getBoolean("permissions",false)
        if(currentUser!="NO_DATA"){
            SingleLoggedUser.user = Gson().fromJson(currentUser, User::class.java)
        }

        //Go to Login or keep in home depending if is first login or not
        if (SingleLoggedUser.user == null) login() else loadHome()
    }

    private fun login(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun requestPermissions(constant: Int){
        requestPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            constant
        )
    }

    //Result after ask for permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        havePermissions = true
        for (result in grantResults) {
            havePermissions = havePermissions && (result!=-1)
        }
        if(havePermissions){
            if (requestCode == CAMERA_INTENT) {
                openCamara()
            }else{
                openGallery()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(menu: MenuItem): Boolean {
        when (menu.itemId) {
            R.id.messenger_action -> {
                Toast.makeText(this, "item Add Clicked", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.activity_action -> {
                Toast.makeText(this, "item Add Clicked", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.media_action -> {

                showPopup(binding.toolbar.findViewById(R.id.media_action))
                return true
            }
        }
        return super.onOptionsItemSelected(menu)
    }

    private fun loadHome() {
        showFragment(homeFragment)
    }

    private fun showFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.media_menu)
        popup.show()
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ::onCameraResult
    )

    private fun openCamara() {
        if(havePermissions){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val fileName = "photo${Calendar.getInstance().time}.png"
            file = File("${getExternalFilesDir(null)}/${fileName}")
            uri = FileProvider.getUriForFile(this, packageName, file!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            cameraLauncher.launch(intent)
        }else{
            requestPermissions(CAMERA_INTENT)
        }
    }

    private fun onCameraResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            postFragment.bitmap = BitmapFactory.decodeFile(file?.path)
            postFragment.postUri = uri
            binding.toolbar.visibility = View.GONE
            showFragment(postFragment)
        } else if (result.resultCode == RESULT_CANCELED) {
            showFragment(homeFragment)
        }
    }

    //Overwritten Methods as listener PostFragment.OnPostListener ----------------------------------
    override fun newPost(post: Post?) {
        if (post != null) {
            homeFragment.newPost(post)
        }
        showFragment(homeFragment)
    }

    override fun loadPost(postingCoroutine: Job) {
        val loaderView = LoadingViewModel(postingCoroutine)
        loaderView.dotsLive.observe(this) {

        }
    }

    //ProfileFragment.OnDoneChanges
    override fun doneChanges(done: Boolean) {
        if (done) {
            Toast.makeText(this, "Saved changes", Toast.LENGTH_SHORT).show()
        }
        showFragment(homeFragment)
        binding.toolbar.visibility = View.VISIBLE
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ::onGalleryResult
    )

    private fun openGallery(){
        if(havePermissions){
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
        }else{
            requestPermissions(GALLERY_INTENT)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.gallery_action -> openGallery()
            R.id.camera_action -> openCamara()
        }
        return true
    }

    private fun onGalleryResult(result: ActivityResult) {
        if(result.resultCode==Activity.RESULT_OK){
            val uriImage = result.data?.data
            uriImage.let{
                postFragment.postUri = uriImage
                binding.toolbar.visibility = View.GONE
                showFragment(postFragment)
            }
        }
    }
    // Serialization----------------------------------------------------------------------------------------------

    override fun onPause() {
        super.onPause()
        val posts = Gson().toJson(PostsWrapper(homeFragment.adapter.posts))
        val currentUser = Gson().toJson(SingleLoggedUser.user)
        val preferences = getPreferences(Context.MODE_PRIVATE)
        preferences.edit()
            .putString("posts",posts)
            .putString("currentUser",currentUser)
            .putBoolean("permissions",havePermissions)
            .apply()
    }

    override fun onResume(){
        super.onResume()

        //Read users and posts
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val currentUser = preferences.getString("currentUser","NO_DATA")
        val posts = preferences.getString("posts","NO_DATA")
        havePermissions = preferences.getBoolean("permissions",false)
        if(posts!="NO_DATA"){
            homeFragment.adapter.posts = Gson().fromJson(posts,PostsWrapper::class.java).posts
        }
        if(currentUser!="NO_DATA"){
            SingleLoggedUser.user = Gson().fromJson(currentUser, User::class.java)
        }
    }

    override fun logout() {
        SingleLoggedUser.user = null
        showFragment(homeFragment)
        login()
    }
}