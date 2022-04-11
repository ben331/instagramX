package com.example.instagramx

import android.graphics.Bitmap
import android.net.Uri
import com.google.gson.Gson

data class User (var id: String, var password: String){
  var photo:String? = null
  var name = ""

  override fun toString(): String {
    return Gson().toJson(this)
  }
}