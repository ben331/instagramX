package com.example.instagramx

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import com.example.instagramx.databinding.ActivityLoginBinding
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {

    //Binding
    private var _binding: ActivityLoginBinding?=null
    private val binding get() = _binding!!

    //Default registered users
    private val defaultUsers = arrayOf(
        User("alfa@gmail.com","aplicacionesmoviles"),
        User("beta@gamil.com", "aplicacionesmoviles"))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.extras==null){
            //Read User
            val preferences = getPreferences(Context.MODE_PRIVATE)
            val currentUser = preferences.getString("currentUser","NO_DATA")
            if(currentUser!="NO_DATA") SingleLoggedUser.user = Gson().fromJson(currentUser, User::class.java)
        }else{
            getPreferences(Context.MODE_PRIVATE).edit()
                .remove("currentUser")
                .apply()
        }

        //Go to MainActivity or keep in login depending if user is logged or not
        if (SingleLoggedUser.user != null) {getIntoMain(); Log.e("no null","no null")}
        else{

            //Binding
            _binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            //Login function
            binding.loginBtn.setOnClickListener {

                //Read fields
                val username = binding.loginUsername.text.toString()
                val password = binding.loginPassword.text.toString()

                //Validate user-password
                var correctPassword:String? = null
                for(element in defaultUsers) {
                    if (element.id == username)
                        correctPassword = element.password
                }

                when(correctPassword) {

                    //User no registered
                    null -> {
                        //Clean fields
                        binding.loginUsername.setText("")
                        binding.loginPassword.setText("")
                        Toast.makeText(this, "$username is not registered", Toast.LENGTH_SHORT).show()
                    }
                    //Correct password
                    password -> {

                        //Save and serialize user
                        SingleLoggedUser.user = User(username, password)
                        val cUser = Gson().toJson(SingleLoggedUser.user)
                        getPreferences(Context.MODE_PRIVATE).edit()
                            .putString("currentUser", cUser)
                            .apply()

                        getIntoMain()
                    }
                    //Incorrect password
                    else -> {
                        binding.loginPassword.setText("")
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getIntoMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode==KeyEvent.KEYCODE_BACK){
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
        }
        return true
    }
}