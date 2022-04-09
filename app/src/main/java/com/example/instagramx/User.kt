package com.example.instagramx

import android.graphics.Bitmap
import android.net.Uri

data class User (var id: String, var password: String){
  var photo:Bitmap? = null
  var name = ""
}