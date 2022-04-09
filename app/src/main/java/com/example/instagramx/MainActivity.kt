package com.example.instagramx

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
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
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity(), PostFragment.OnPostListener, ProfileFragment.OnDoneChanges, MenuItem.OnMenuItemClickListener,
    PopupMenu.OnMenuItemClickListener {

    //Constants
    companion object{
        const val CAMERA_INTENT:Int = 1
        const val GALLERY_INTENT:Int = 2
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

    //Launchers
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ::onCameraResult
    )
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ::onGalleryResult
    )

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

        //-----------------------------------------------   NAVIGATION BAR   ---------------------------------------------
        binding.navigator.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.postItem -> openCamara()
                R.id.homeItem -> showFragment(homeFragment)
                R.id.profileItem -> showFragment(profileFragment)
            }
            true
        }
    }

    override fun logout() {
        SingleLoggedUser.user = null
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("logout", true)
        }
        startActivity(intent)
    }

    //-----------------------------------------------   FRAGMENTS   ---------------------------------------------
    private fun showFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        if(fragment == homeFragment){
            binding.toolbar.visibility = View.VISIBLE
        }else{
            binding.toolbar.visibility = View.GONE
        }
        transaction.commit()
    }

    //-----------------------------------------------   TOOL BAR   ----------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bar, menu)
        return true
    }
    override fun onOptionsItemSelected(menu: MenuItem): Boolean {
        when (menu.itemId) {
            R.id.messenger_action -> Toast.makeText(this, "Messenger Button Pressed", Toast.LENGTH_SHORT).show()
            R.id.activity_action -> Toast.makeText(this, "Activity Button Pressed", Toast.LENGTH_SHORT).show()
            R.id.media_action -> showPopup(binding.toolbar.findViewById(R.id.media_action))
        }
        return super.onOptionsItemSelected(menu)
    }

    //-----------------------------------------------   POPUP MENU  ---------------------------------------------
    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.media_menu)
        popup.show()
    }
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.gallery_action -> openGallery()
            R.id.camera_action -> openCamara()
        }
        return true
    }

    //-----------------------------------------------   GAllERY   ---------------------------------------------
    private fun openGallery(){
        if(havePermissions){
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
        }else{
            requestPermissions(GALLERY_INTENT)
        }
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

    //-----------------------------------------------   CAMERA   ---------------------------------------------
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

    //-----------------------------------------------   PERMISSIONS   ---------------------------------------------
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

    //Overwritten Methods as listener PostFragment.OnPostListener ----------------------------------
    override fun newPost(post: Post?) {
        if (post != null) {
            homeFragment.newPost(post)
        }
        showFragment(homeFragment)
    }

    //ProfileFragment.OnDoneChanges
    override fun doneChanges(done: Boolean) {
        if (done) {
            Toast.makeText(this, "Saved changes", Toast.LENGTH_SHORT).show()
        }
        showFragment(homeFragment)
        binding.toolbar.visibility = View.VISIBLE
    }

    //-----------------------------------------------   SERIALIZATION   ---------------------------------------------
    override fun onPause() {
        super.onPause()
        val posts = Gson().toJson(PostsWrapper(homeFragment.adapter.posts))
        val preferences = getPreferences(Context.MODE_PRIVATE)
        preferences.edit()
            .putString("posts",posts)
            .putBoolean("permissions",havePermissions)
            .apply()
    }
    override fun onResume(){
        super.onResume()

        //Read users and posts
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val posts = preferences.getString("posts","NO_DATA")
        havePermissions = preferences.getBoolean("permissions",false)
        if(posts!="NO_DATA"){
            homeFragment.adapter.posts = Gson().fromJson(posts,PostsWrapper::class.java).posts
        }
    }

    //-----------------------------------------------   CLOSE APP   ---------------------------------------------
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(homeFragment.isAdded){
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.alert_exit)
                builder.apply {
                    setPositiveButton(R.string.yes) { _, _ ->
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.addCategory(Intent.CATEGORY_HOME)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
                }
                builder.create()
                builder.show()
            }else{
                showFragment(homeFragment)
            }
        }
        return true
    }
}