package com.example.instagramx

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import com.example.instagramx.databinding.ActivityLoginBinding
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {

    //Binding
    private var _binding: ActivityLoginBinding?=null
    private val binding get() = _binding!!

    //Default registered users
    private var defaultUsers = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readUsers()

        if(intent.extras!=null){ //This happens cause an intent to logout from MainActivity
            SingleLoggedUser.user = null
            getPreferences(Context.MODE_PRIVATE).edit()
                .remove("currentUser")
                .apply()
        }else{
            //Read User
            val preferences = getPreferences(Context.MODE_PRIVATE)
            val currentUser = preferences.getString("currentUser","NO_DATA")
            if(currentUser!="NO_DATA") SingleLoggedUser.user = Gson().fromJson(currentUser, User::class.java)
        }

        //Go to MainActivity or keep in login depending if user is logged or not
        if (SingleLoggedUser.user != null) getIntoMain()
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
                    if (element.id == username){
                        correctPassword = element.password
                        SingleLoggedUser.index=defaultUsers.indexOf(element)
                    }
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

    private fun readUsers(){
        //Read users
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val users = preferences.getString("users","NO_DATA")
        if(users!="NO_DATA"){
            defaultUsers =  Gson().fromJson(users,UsersWrapper::class.java).users
        }else{
            defaultUsers.add(User("alfa@gmail.com","aplicacionesmoviles"))
            defaultUsers.add(User("beta@gmail.com","aplicacionesmoviles"))
        }
    }

    private fun getIntoMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun updateUser(){
        readUsers()
        defaultUsers[SingleLoggedUser.index] = SingleLoggedUser.user!!
        getPreferences(Context.MODE_PRIVATE).edit()
            .putString("users", Gson().toJson(UsersWrapper(defaultUsers)))
            .apply()
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

    //-----------------------------------------------   SERIALIZATION   ---------------------------------------------
    override fun onPause() {
        super.onPause()
        val users = Gson().toJson(UsersWrapper(defaultUsers))
        val preferences = getPreferences(Context.MODE_PRIVATE)
        preferences.edit()
            .putString("users",users)
            .apply()
    }
    override fun onResume(){
        super.onResume()
        readUsers()
    }
}