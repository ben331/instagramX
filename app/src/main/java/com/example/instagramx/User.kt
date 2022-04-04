package com.example.instagramx

import android.net.Uri

data class User (var id: String, var password: String){
  var photo:Uri? = null
  var name = ""
}