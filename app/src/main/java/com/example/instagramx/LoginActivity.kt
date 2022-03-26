package com.example.instagramx

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.instagramx.databinding.ActivityLoginBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding?=null
    private val binding get() = _binding!!

    private val defaultUsers = arrayOf(
        User("alfa@gmail.com","aplicacionesmoviles"),
        User("beta@gamil.com", "aplicacionesmoviles"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lateinit var user:User

        binding.loginBtn.setOnClickListener {
            Log.e("Info", "bTN")
            val username = binding.loginUsername.text.toString()
            val password = binding.loginPassword.text.toString()

            var correctPassword:String? = null
            for(element in defaultUsers) {
                if (element.id == username)
                    correctPassword = element.password
            }

            when(correctPassword) {

                null -> {
                    binding.loginUsername.setText("")
                    binding.loginPassword.setText("")
                    Toast.makeText(this, "$username is not registered", Toast.LENGTH_SHORT).show()
                }
                password -> {
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("username", "$username" )
                        putExtra("pass", "$password")
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                else -> {
                    binding.loginPassword.setText("")
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /*
        fun getPasswordByUser(user:String):String?{
            for(element in defaultUsers){
                if(element.id == username)
                    return element.password
            }
            return null
        }
         */
    }
}