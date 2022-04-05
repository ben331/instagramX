package com.example.instagramx

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), PostFragment.OnPostListener, ProfileFragment.OnDoneChanges, MenuItem.OnMenuItemClickListener,
    PopupMenu.OnMenuItemClickListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        //Thread.sleep(500) //To Show Splash or simulate back operations
        super.onCreate(savedInstanceState)

        //Bindings
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                R.id.postItem ->
                    openCamara()
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

        //Ask permissions
        requestPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), 1
        )

        //Go to Login or keep in home depending if is first login or not
        if (SingleLoggedUser.user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            val launcher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                ::onResultLogin
            )
            launcher.launch(intent)
        } else {
            loadHome()
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

    private fun onResultLogin(result: ActivityResult) {
        val username: String? = result.data?.extras?.getString("username")
        val password: String? = result.data?.extras?.getString("pass")
        SingleLoggedUser.user = User(username!!, password!!)
        loadHome()
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

    private fun openCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val fileName = "photo${Calendar.getInstance().time}.png"
        file = File("${getExternalFilesDir(null)}/${fileName}")
        uri = FileProvider.getUriForFile(this, packageName, file!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        val launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ::onCameraResult
        )
        launcher.launch(intent)
    }

    private fun onCameraResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            postFragment.bitmap = result.data?.extras?.get("data") as Bitmap
            postFragment.postUri = uri
            binding.toolbar.visibility = View.GONE
            showFragment(postFragment)
        } else if (result.resultCode == RESULT_CANCELED) {
            showFragment(homeFragment)
        }
    }

    //Result after ask for permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (result in grantResults) {
                if (result == -1)
                    this.finish(); exitProcess(0)
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
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.gallery_action -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                val launcher = registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult(),
                    ::onGalleryResult
                )
                launcher.launch(intent)
            }
            R.id.camera_action -> {
                openCamara()
            }
        }
        return true
    }

    private fun onGalleryResult(result: ActivityResult) {
        val uriImage = result.data?.data
        uriImage.let{
            postFragment.postUri = uri
            binding.toolbar.visibility = View.GONE
            showFragment(postFragment)
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
            .apply()
    }

    override fun onResume(){
        super.onResume()
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val currentUser = preferences.getString("currentUser","NO_DATA")
        val posts = preferences.getString("posts","NO_DATA")
        if(posts!="NO_DATA"){
            homeFragment.adapter.posts = Gson().fromJson(posts,PostsWrapper::class.java).posts
        }
        if(currentUser!="NO_DATA"){
            SingleLoggedUser.user = Gson().fromJson(currentUser, User::class.java)
        }
    }
}