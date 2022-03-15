package com.example.instagramx

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val defaultUsers = arrayOf(
        User("alfa@gmail.com","aplicacionesmoviles"),
        User("beta@gamil.com", "aplicacionesmoviles"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        lateinit var username:String
        lateinit var password:String

        loginBtn.setOnClickListener(){
            username = loginUsername.text.toString()
            password = loginPassword.text.toString()

            var correctPassword:String? = null
            for(user in defaultUsers) {
                if (user.id == username)
                    correctPassword = user.password
            }

            if(correctPassword==null){
                loginUsername.setText("")
                loginPassword.setText("")
                Toast.makeText(this, "$username is not registered",Toast.LENGTH_SHORT).show()
            }else if(correctPassword == password){
                var intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("username", username)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else {
                loginPassword.setText("")
                Toast.makeText(this, "Incorrect password",Toast.LENGTH_SHORT).show()
            }
        }

        fun getPasswordByUser(user:String):String?{
            for(user in defaultUsers){
                if(user.id.equals(username))
                    return user.password
            }
            return null
        }
    }
}